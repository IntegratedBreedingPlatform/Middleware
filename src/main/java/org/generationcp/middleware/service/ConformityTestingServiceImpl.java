
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.exceptions.ConformityException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.service.api.ConformityTestingService;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public class ConformityTestingServiceImpl implements ConformityTestingService {

	private final ThreadLocal<Map<String, List<String>>> genotypeInfo;
	private final ThreadLocal<UploadInput> inputThreadLocal;

	private final GenotypicDataManager genotypicDataManager;
	private final PedigreeDataManager pedigreeDataManager;

	public static final String CROSS_SEPARATOR = "/";
	public static final String EMPTY_VALUE_CHARACTER = "-";

	public ConformityTestingServiceImpl(GenotypicDataManager genotypicDataManager, PedigreeDataManager pedigreeDataManager) {

		this.genotypicDataManager = genotypicDataManager;
		this.pedigreeDataManager = pedigreeDataManager;
		this.genotypeInfo = new ThreadLocal<Map<String, List<String>>>();
		this.inputThreadLocal = new ThreadLocal<UploadInput>();
	}

	@Override
	public Map<Integer, Map<String, String>> testConformity(UploadInput input) throws MiddlewareQueryException, ConformityException {
		this.genotypeInfo.set(new HashMap<String, List<String>>());
		this.inputThreadLocal.set(input);
		try {
			this.prepareInput(input);

			Map<String, List<String>> processedInput = this.genotypeInfo.get();

			Map<Integer, Map<String, String>> errorItems = new HashMap<Integer, Map<String, String>>();

			for (ConformityGermplasmInput conformityGermplasmInput : input.getEntries().values()) {
				Map<String, String> errorMarkers = new HashMap<String, String>();

				for (Map.Entry<String, String> entry : conformityGermplasmInput.getMarkerValues().entrySet()) {
					// empty marker values don't need to be checked
					if (StringUtils.isEmpty(entry.getValue())
							|| entry.getValue().equals(ConformityTestingServiceImpl.EMPTY_VALUE_CHARACTER)) {
						continue;
					}

					if (input.isParentInputAvailable()) {

						// don't process the parental information provided in the inputThreadLocal
						if (conformityGermplasmInput.getGid().equals(input.getParentAGID())
								|| conformityGermplasmInput.getGid().equals(input.getParentBGID())) {
							continue;
						}

						boolean passed = true;
						if (entry.getValue().contains(ConformityTestingServiceImpl.CROSS_SEPARATOR)) {
							passed &= processedInput.get(entry.getKey()).contains(this.normalizeHeterozygousValue(entry.getValue()));
						} else {
							passed &= processedInput.get(entry.getKey()).contains(entry.getValue());
						}

						if (!passed) {
							errorMarkers.put(entry.getKey(), entry.getValue());
						}

					} else {
						boolean passed = true;
						if (entry.getValue().contains(ConformityTestingServiceImpl.CROSS_SEPARATOR)) {
							passed &= this.processHeterozygousMarker(entry.getKey(), entry.getValue());
						} else {
							passed &= this.processHomozygousMarker(entry.getKey(), entry.getValue());
						}

						if (!passed) {
							errorMarkers.put(entry.getKey(), entry.getValue());
						}
					}

					if (errorMarkers.size() > 0) {
						errorItems.put(conformityGermplasmInput.getGid(), errorMarkers);
					}

				}
			}

			return errorItems;
		} catch (MiddlewareQueryException e) {
			throw e;
		} finally {
			// ensure that whatever happens, the contents of the threadlocal are removed so that it doesn't cause memory starvation
			this.genotypeInfo.remove();
			this.inputThreadLocal.remove();
		}
	}

	// represents the preparatory step. here we either compute the possible cross results for parent marker data, or gather marker
	// information on ancestors
	protected void prepareInput(UploadInput input) throws MiddlewareQueryException, ConformityException {
		if (input.isParentInputAvailable()) {
			this.computeCrosses(input.getParentAInput(), input.getParentBInput());
		} else {

			// check if the genotypic data is available for the parents first before using "ancestor mode"
			ConformityGermplasmInput parentA = this.retrieveParentInput(input.getParentAGID());
			ConformityGermplasmInput parentB = this.retrieveParentInput(input.getParentBGID());

			if (!(parentA == null || parentB == null)) {
				this.computeCrosses(parentA, parentB);
				input.addEntry(parentA);
				input.addEntry(parentB);
			} else {
				if (parentA == null) {
					this.processParentInformation(input.getParentAGID());
				} else {
					this.processParentInformation(parentA);
				}

				if (parentB == null) {
					this.processParentInformation(input.getParentBGID());
				} else {
					this.processParentInformation(parentB);
				}

				if (this.genotypeInfo.get().isEmpty()) {
					throw new ConformityException("Parent and ancestor data not found. No basis for conformity checking");
				}
			}

		}
	}

	protected ConformityGermplasmInput retrieveParentInput(Integer gid) throws MiddlewareQueryException {
		List<AllelicValueElement> parentAlleleValues = this.genotypicDataManager.getAllelicValuesByGid(gid);
		if (parentAlleleValues == null || parentAlleleValues.isEmpty()) {
			return null;
		} else {

			// TODO retrieve other details from the database aside from just the marker values
			ConformityGermplasmInput input = new ConformityGermplasmInput("", "", gid);

			for (AllelicValueElement parentAlleleValue : parentAlleleValues) {
				input.getMarkerValues().put(parentAlleleValue.getMarkerName(), parentAlleleValue.getData());
			}

			return input;
		}
	}

	protected void computeCrosses(ConformityGermplasmInput inputA, ConformityGermplasmInput inputB) {
		Map<String, List<String>> info = this.genotypeInfo.get();

		Map<String, String> parentAMarkers = inputA.getMarkerValues();
		Map<String, String> parentBMarkers = inputB.getMarkerValues();

		for (String markerName : parentAMarkers.keySet()) {
			String[] parentAValues = parentAMarkers.get(markerName).split(ConformityTestingServiceImpl.CROSS_SEPARATOR);
			String[] parentBValues = parentBMarkers.get(markerName).split(ConformityTestingServiceImpl.CROSS_SEPARATOR);

			List<String> markerCrosses = info.get(markerName);
			if (markerCrosses == null) {
				markerCrosses = new ArrayList<String>();
				info.put(markerName, markerCrosses);
			}

			for (String aValue : parentAValues) {
				for (String bValue : parentBValues) {
					String cross = this.performCross(aValue, bValue);
					if (!markerCrosses.contains(cross)) {
						markerCrosses.add(cross);
					}
				}
			}
		}
	}

	protected String performCross(String... crossParticipants) {

		// the cross participants are put temporarily into a list so that they can be sorted in alphabetical order first
		List<String> temp = new ArrayList<String>();

		for (String crossParticipant : crossParticipants) {
			if (!(StringUtils.isEmpty(crossParticipant) || crossParticipant.equals(ConformityTestingServiceImpl.EMPTY_VALUE_CHARACTER))) {
				if (!temp.contains(crossParticipant)) {
					temp.add(crossParticipant);
				}
			}
		}

		Collections.sort(temp);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < temp.size(); i++) {
			if (i != 0) {
				builder.append(ConformityTestingServiceImpl.CROSS_SEPARATOR);
			}

			builder.append(temp.get(i));
		}

		return builder.toString();
	}

	protected void processParentInformation(Integer parentGID) throws MiddlewareQueryException {
		// retrieve parent's pedigree, and store all of its ancestors' values
		GermplasmPedigreeTree pedigreeTree = this.pedigreeDataManager.generatePedigreeTree(parentGID, 4);
		if (pedigreeTree != null) {
			this.processPedigreeNode(pedigreeTree.getRoot());
		}

	}

	protected void processParentInformation(ConformityGermplasmInput parent) {
		for (Map.Entry<String, String> entry : parent.getMarkerValues().entrySet()) {
			this.processGenotypeInfo(entry.getKey(), entry.getValue());
		}
	}

	protected void processPedigreeNode(GermplasmPedigreeTreeNode node) throws MiddlewareQueryException {
		/* boolean markersStored = */
		this.storeParentMarkers(node.getGermplasm().getGid());

		if (/* !markersStored && */node.getLinkedNodes() != null) {
			for (GermplasmPedigreeTreeNode germplasmPedigreeTreeNode : node.getLinkedNodes()) {
				this.processPedigreeNode(germplasmPedigreeTreeNode);
			}
		}
	}

	protected boolean storeParentMarkers(Integer parentGID) throws MiddlewareQueryException {
		boolean markersStored = false;
		UploadInput input = this.inputThreadLocal.get();
		if (input.getEntries().containsKey(parentGID)) {
			for (Map.Entry<String, String> entry : input.getEntries().get(parentGID).getMarkerValues().entrySet()) {
				this.processGenotypeInfo(entry.getKey(), entry.getValue());
				markersStored = true;
			}

			// remove parent / ancestral information from input list so that rest of app can continue on items that need to be validated
			input.getEntries().remove(parentGID);
		} else {
			List<AllelicValueElement> parentAlleleValues = this.genotypicDataManager.getAllelicValuesByGid(parentGID);
			for (AllelicValueElement parentAlleleValue : parentAlleleValues) {
				this.processGenotypeInfo(parentAlleleValue.getMarkerName(), parentAlleleValue.getData());
				markersStored = true;
			}
		}

		return markersStored;
	}

	protected boolean processHomozygousMarker(String markerName, String markerValue) {
		// blank marker value
		if (!(StringUtils.isEmpty(markerValue) || markerValue.equals(ConformityTestingServiceImpl.EMPTY_VALUE_CHARACTER))) {
			return this.markerValueExists(markerName, markerValue);
		}

		return true;
	}

	protected boolean processHeterozygousMarker(String markerName, String markerValue) {
		String[] values = markerValue.split(ConformityTestingServiceImpl.CROSS_SEPARATOR);

		boolean passed = true;
		for (String value : values) {
			if (!(StringUtils.isEmpty(value) || value.equals(ConformityTestingServiceImpl.EMPTY_VALUE_CHARACTER))) {
				passed &= this.markerValueExists(markerName, value);
			}
		}

		return passed;
	}

	protected boolean markerValueExists(String marker, String value) {
		List<String> valueList = this.genotypeInfo.get().get(marker);

		if (valueList != null) {
			return valueList.contains(value);
		} else {
			return false;
		}
	}

	protected void processGenotypeInfo(String marker, String alleleValue) {

		int index = alleleValue.indexOf(ConformityTestingServiceImpl.CROSS_SEPARATOR);
		if (index != -1) {

			// heterozyous 2nd allelle
			String value = alleleValue.substring(index + 1);
			this.addGenotypeInfo(marker, value);

			// heterozygous first allelle
			value = alleleValue.substring(0, index);
			this.addGenotypeInfo(marker, value);
		} else {
			this.addGenotypeInfo(marker, alleleValue);
		}

	}

	protected void addGenotypeInfo(String marker, String value) {

		List<String> characterList = this.genotypeInfo.get().get(marker);

		if (characterList == null) {
			characterList = new ArrayList<String>();
			this.genotypeInfo.get().put(marker, characterList);
		}

		if (!characterList.contains(value)) {
			characterList.add(value);
		}

	}

	protected String normalizeHeterozygousValue(String heterozygousValue) {
		assert heterozygousValue.contains(ConformityTestingServiceImpl.CROSS_SEPARATOR);

		String[] values = heterozygousValue.split(ConformityTestingServiceImpl.CROSS_SEPARATOR);
		Arrays.sort(values);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				builder.append(ConformityTestingServiceImpl.CROSS_SEPARATOR);
			}

			builder.append(values[i]);
		}

		return builder.toString();
	}
}
