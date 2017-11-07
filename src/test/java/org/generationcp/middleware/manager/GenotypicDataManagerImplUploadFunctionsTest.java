/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.MappingABHRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSNPRow;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.SNPDataRow;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GenotypicDataManagerImplUploadFunctionsTest extends IntegrationTestBase {

	@Autowired
	private GenotypicDataManager manager;

	private static final String DATASET = "Dataset";
	private static final String DATASET_USERS = "DatasetUsers";
	private static final String ACC_METADATA_SET = "AccMetadataSet";
	private static final String MARKER_METADATA_SET = "MarkerMetadataSet";
	private static final String ALLELE_VALUES = "AlleleValues";
	private static final String CHAR_VALUES = "CharValues";
	private static final String MAPPING_POP = "MappingPop";
	private static final String MAPPING_POP_VALUES = "MappingPopValues";
	private static final String DART_VALUES = "DartValues";
	private static final String MARKER = "Marker";

	private static final int NUMBER_OF_ROWS = 10; // 210 * 260;

	private Dataset createDataset() throws Exception {
		Integer datasetId = null; // Crop tested: Groundnut
		String datasetName = " QTL_ ICGS 44 X ICGS 78  " + (int) (Math.random() * 1000);
		String datasetDesc = "ICGS 44 X ICGS 78";
		String datasetType = "QTL";
		String genus = "Groundnut";
		String species = "";
		Date uploadTemplateDate = new Date(System.currentTimeMillis());
		String remarks = "";
		String dataType = "int";
		String missingData = null;
		String method = null;
		String score = null;
		String institute = null;
		String principalInvestigator = null;
		String email = null;
		String purposeOfStudy = null;

		return new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks, dataType,
				missingData, method, score, institute, principalInvestigator, email, purposeOfStudy, null, null, null);
	}

	private List<Object> createMarkerRecords() {

		Integer markerId = null; // Will be set/overridden by the function
		String markerType = null; // Will be set/overridden by the function
		String markerName = "SeqTEST " + (int) (Math.random() * 1000);
		String species = "Groundnut";
		String dbAccessionId = null;
		String reference = null;
		String genotype = null;
		String ploidy = null;
		String primerId = null;
		String remarks = null;
		String assayType = null;
		String motif = null;
		String forwardPrimer = null;
		String reversePrimer = null;
		String productSize = null;
		Float annealingTemp = Float.valueOf(0);
		String amplification = null;

		String alias = "testalias";
		Integer noOfRepeats = 0;
		String motifType = "";
		String sequence = "";
		Integer sequenceLength = 0;
		Integer minAllele = 0;
		Integer maxAllele = 0;
		Integer ssrNr = 0;
		Float forwardPrimerTemp = 0f;
		Float reversePrimerTemp = 0f;
		Float elongationTemp = 0f;
		Integer fragmentSizeExpected = 0;
		Integer fragmentSizeObserved = 0;
		Integer expectedProductSize = 0;
		Integer positionOnReferenceSequence = 0;
		String restrictionEnzymeForAssay = null;
		String principalInvestigator = "Juan Dela Cruz";
		String contact = "juan@irri.com.ph";
		String institute = "IRRI";

		Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
				assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);
		MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
		MarkerDetails markerDetails =
				new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);
		MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

		List<Object> markerRecords = new ArrayList<Object>();
		markerRecords.add(marker);
		markerRecords.add(markerAlias);
		markerRecords.add(markerDetails);
		markerRecords.add(markerUserInfo);
		return markerRecords;

	}

	// Create test data for setSSR, setSNP, setDArT, setMappingData
	private java.util.Map<String, Object> createMappingRecords() throws Exception {
		java.util.Map<String, Object> records = new HashMap<String, Object>();

		// DatasetUser Fields
		Integer datasetId = null; // Will be set/overridden by the function
		Integer userId = 123;

		// AccMetadataSet Additional Fields
		//		Integer gId = 1;
		Integer sampleId = 1;

		// MarkerMetadataSet Additional Field
		Integer markerId = 1;
		Integer markerSampleId = 1;
		Integer accSampleId = 1;

		// AlleleValues Additional Fields
		Integer anId = null; // Will be set/overridden by the function
		String alleleBinValue = "238:238";
		String alleleRawValue = "0.0:0.0";
		Integer peakHeight = 10;

		// DartValues Additional Fields
		Integer adId = null; // Will be set/overridden by the function
		Integer cloneId = 1;
		Float qValue = 0f;
		Float reproducibility = 0f;
		Float callRate = 0f;
		Float picValue = 0f;
		Float discordance = 0f;

		// charValues Additional Fields
		Integer acId = null;
		String charValue = "CV";

		// MappingPop Additional Fields
		String mappingType = "abh";
		Integer parentAGId = 1035;
		Integer parentBGId = 1036;
		Integer populationSize = 999;
		String populationType = "";
		String mapDataDescription = "Flood resistant";
		String scoringScheme = "";
		Integer mapId = 1;

		// MappingPopValues Additional Fields
		Integer mpId = null; // Will be set/overridden by the function
		String mapCharValue = "-";

		Dataset dataset = this.createDataset();

		AccMetadataSet accMetadataSet = new AccMetadataSet(null, dataset, sampleId, accSampleId);
		MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(null, dataset, markerId, markerSampleId);
		DatasetUsers datasetUser = new DatasetUsers(dataset, userId);
		AlleleValues alleleValues = new AlleleValues(anId, datasetId, sampleId, markerId, alleleBinValue, alleleRawValue, peakHeight);
		CharValues charValues = new CharValues(acId, dataset, markerId, sampleId, charValue, markerSampleId, accSampleId);
		DartValues dartValues =
				new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);
		MappingPop mappingPop =
				new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription,
						scoringScheme, mapId);
		MappingPopValues mappingPopValues = new MappingPopValues(mpId, mapCharValue, datasetId, sampleId, markerId, markerSampleId, accSampleId);
		Marker marker = (Marker) this.createMarkerRecords().get(0);

		records.put(GenotypicDataManagerImplUploadFunctionsTest.DATASET, dataset);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET, accMetadataSet);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.MARKER_METADATA_SET, markerMetadataSet);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.DATASET_USERS, datasetUser);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.ALLELE_VALUES, alleleValues);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.CHAR_VALUES, charValues);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.DART_VALUES, dartValues);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP, mappingPop);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP_VALUES, mappingPopValues);
		records.put(GenotypicDataManagerImplUploadFunctionsTest.MARKER, marker);

		return records;
	}

	@Test
	public void testSetSNP() throws Exception {

		java.util.Map<String, Object> mappingRecords = this.createMappingRecords();

		Dataset dataset = (Dataset) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.DATASET);
		dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
		if (dataset.getDatasetName().length() > 30) {
			dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
		}
		DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.DATASET_USERS);

		List<Marker> markers = new ArrayList<Marker>();
		List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
		List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
		List<CharValues> charValueList = new ArrayList<CharValues>();

		int id = 1;
		for (int i = 0; i < GenotypicDataManagerImplUploadFunctionsTest.NUMBER_OF_ROWS; i++) {

			mappingRecords = this.createMappingRecords();

			Marker marker = (Marker) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MARKER);
			marker.setMarkerId(id);
			marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
			marker.setMarkerName(marker.getMarkerName() + id);
			markers.add(marker);

			AccMetadataSet accMetadataSet =
					(AccMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET);
			accMetadataSet.setSampleId(id);
			accMetadataSets.add(accMetadataSet);

			MarkerMetadataSet markerMetadataSet =
					(MarkerMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MARKER_METADATA_SET);
			markerMetadataSet.setMarkerId(marker.getMarkerId());
			markerMetadataSets.add(markerMetadataSet);

			CharValues charValues = (CharValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.CHAR_VALUES);
			charValues.setMarkerId(marker.getMarkerId());
			charValues.setSampleId(accMetadataSet.getSampleId());
			charValueList.add(charValues);

			id++;
		}

		Boolean addStatus = this.manager.setSNP(dataset, datasetUser, markers, markerMetadataSets, accMetadataSets, charValueList);
		Assert.assertTrue(addStatus);

		Debug.println(IntegrationTestBase.INDENT, "testSetSNP() Added: ");
		this.printUploadedData(dataset, datasetUser, markers, markerMetadataSets, null);
		if (accMetadataSets.size() < 20) {
			Debug.printObjects(IntegrationTestBase.INDENT * 2, accMetadataSets);
			Debug.printObjects(IntegrationTestBase.INDENT * 2, charValueList);
		} else {
			Debug.println(IntegrationTestBase.INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
		}
		/*
		 * TO VERIFY RESULTS IN DB: select * from gdms_dataset where dataset_id = <datasetId>; select * from gdms_dataset_users order by
		 * dataset_id limit 1; select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>; select * from gdms_marker_metadataset
		 * where dataset_id = <datasetId>; select * from gdms_acc_metadataset where dataset_id = <datasetId>; select * from gdms_char_values
		 * where dataset_id = <datasetId>;
		 */
	}

	private void printUploadedData(Dataset dataset, DatasetUsers datasetUser, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, MappingPop mappingPop) {

		Debug.println(IntegrationTestBase.INDENT * 2, dataset.toString());
		Debug.println(IntegrationTestBase.INDENT * 2, datasetUser.toString());
		if (mappingPop != null) {
			Debug.println(IntegrationTestBase.INDENT * 2, mappingPop.toString());
		}
		if (markers != null && markers.size() < 20) {
			Debug.printObjects(IntegrationTestBase.INDENT * 2, markers);
		} else {
			Debug.println(IntegrationTestBase.INDENT * 2, "#Markers Added: " + markers.size());
		}
		if (markerMetadataSets != null && markerMetadataSets.size() < 20) {
			Debug.printObjects(IntegrationTestBase.INDENT * 2, markerMetadataSets);
		} else {
			Debug.println(IntegrationTestBase.INDENT * 2, "#MarkerMetadataSets Added: " + markerMetadataSets.size());
		}

	}

	// ============================================ UPDATE FUNCTIONS =====================================================

	private Dataset getTestDatasetByType(GdmsType type, GdmsType mappingType) throws MiddlewareQueryException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		if (type == GdmsType.TYPE_MAPPING) {
			datasets = this.manager.getDatasetsByMappingTypeFromLocal(mappingType);
		} else {
			datasets = this.manager.getDatasetsByType(type);
		}
		if (datasets.size() > 0) {
			for (Dataset dataset : datasets) {
				if (dataset.getDatasetId() < 0) {
					return dataset;
				}
			}
		}
		Debug.println("No dataset of type " + type + " found in the local database for testing");
		return null;
	}

	private void updateDataset(Dataset dataset, String updateId) {
		Debug.printObject(0, "DATASET BEFORE: " + dataset);
		String datasetDescription = dataset.getDatasetDesc() + updateId;
		if (datasetDescription.length() > 255) {
			datasetDescription = datasetDescription.substring(0, 255);
		}
		dataset.setDatasetDesc(datasetDescription);
	}

	private MappingPop updateMappingPop(MappingPop mappingPop, String updateId) {
		Debug.printObject(0, "MAPPINGPOP BEFORE: " + mappingPop);
		String mapDataDescription =
				mappingPop.getMapDataDescription() == null ? "" + updateId : mappingPop.getMapDataDescription() + updateId;
		if (mapDataDescription.length() > 150) {
			mapDataDescription = mapDataDescription.substring(0, 150);
		}
		mappingPop.setMapDataDescription(mapDataDescription);
		return mappingPop;
	}

	private MappingPopValues updateMappingPopValues(MappingPopValues mappingPopValues, String updateId) {
		if (mappingPopValues == null) {
			return null;
		}
		String mapCharValue = mappingPopValues.getMapCharValue() + updateId;
		if (mapCharValue.length() > 20) {
			mapCharValue = mapCharValue.substring(0, 20);
		}
		mappingPopValues.setMapCharValue(mapCharValue);
		return mappingPopValues;
	}

	private CharValues updateCharValues(CharValues charValues, String updateId) {
		if (charValues == null) {
			return null;
		}
		String charValue = charValues.getCharValue() + updateId;
		if (charValue.length() > 4) {
			charValue = charValue.substring(0, 4);
		}
		charValues.setCharValue(charValue);
		return charValues;
	}

	@Test
	public void testUpdateSNP() throws Exception {
		GdmsType gdmsType = GdmsType.TYPE_SNP;
		Integer updateId = (int) (Math.random() * 1000); // to append to description/remarks field for the update operation

		Dataset dataset = this.getTestDatasetByType(gdmsType, null);
		if (dataset == null) {
			Debug.println(IntegrationTestBase.INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
			return;
		}

		Integer datasetId = dataset.getDatasetId();

		this.updateDataset(dataset, updateId.toString());

		List<SNPDataRow> rows = this.manager.getSNPDataRows(datasetId);
		Debug.println("ROWS BEFORE: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rows);

		// Update markers
		List<Marker> markers = this.manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
		List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

		// Update existing rows
		List<SNPDataRow> updatedRows = new ArrayList<SNPDataRow>();

		for (SNPDataRow row : rows) {

			// No change in accMetadataSet
			AccMetadataSet accMetadataSet = row.getAccMetadataSet();

			// Update dartValues
			CharValues charValues = this.updateCharValues(row.getCharValues(), updateId.toString());

			updatedRows.add(new SNPDataRow(accMetadataSet, charValues));
		}

		// Add (or update) a row - existing dataset, new charValues, new accMetadataSet
		java.util.Map<String, Object> mappingRecords = this.createMappingRecords();
		AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET);
		CharValues charValues = (CharValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.CHAR_VALUES);
		SNPDataRow newRow = new SNPDataRow(accMetadataSet, charValues);
		updatedRows.add(newRow);
		Debug.println("ADD OR UPDATE ROW: " + newRow);

		// UPDATE
		this.manager.updateSNP(dataset, markers, markerMetadataSets, updatedRows);

		Dataset datasetAfter = this.manager.getDatasetById(datasetId);
		Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
		Assert.assertEquals(dataset, datasetAfter); // Dataset updated

		List<SNPDataRow> rowsAfter = this.manager.getSNPDataRows(datasetId);
		Debug.println("ROWS AFTER: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rowsAfter);
	}

	@Test
	public void testUpdateMappingABH() throws Exception {
		GdmsType gdmsType = GdmsType.TYPE_MAPPING;
		String updateId = " UPDATED " + String.valueOf((int) (Math.random() * 1000)); // to append to description/remarks field for the
		// update operation

		Dataset dataset = this.getTestDatasetByType(gdmsType, null);
		if (dataset == null) {
			Debug.println(IntegrationTestBase.INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
			return;
		}

		Integer datasetId = dataset.getDatasetId();
		this.updateDataset(dataset, updateId);

		// Update markers
		List<Marker> markers = this.manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
		List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

		// Update mappingPop
		MappingPop mappingPop = this.manager.getMappingPopByDatasetId(datasetId);
		this.updateMappingPop(mappingPop, updateId);

		List<MappingABHRow> rows = this.manager.getMappingABHRows(datasetId);
		Debug.println("ROWS BEFORE: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rows);

		// Update existing rows
		List<MappingABHRow> updatedRows = new ArrayList<MappingABHRow>();
		for (MappingABHRow row : rows) {

			// Update mappingPopValues
			MappingPopValues mappingPopValues = this.updateMappingPopValues(row.getMappingPopValues(), updateId);

			// No change in accMetadataSet
			AccMetadataSet accMetadataSet = row.getAccMetadataSet();

			updatedRows.add(new MappingABHRow(accMetadataSet, mappingPopValues));
		}

		// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
		java.util.Map<String, Object> mappingRecords = this.createMappingRecords();
		AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET);
		MappingPopValues mappingPopValues =
				(MappingPopValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP_VALUES);
		MappingABHRow newRow = new MappingABHRow(accMetadataSet, mappingPopValues);
		updatedRows.add(newRow);
		Debug.println("ADD OR UPDATE ROW: " + newRow);

		// UPDATE
		this.manager.updateMappingABH(dataset, mappingPop, markers, markerMetadataSets, updatedRows);

		Dataset datasetAfter = this.manager.getDatasetById(datasetId);
		Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
		Assert.assertEquals(dataset, datasetAfter); // Dataset updated

		MappingPop mappingPopAfter = this.manager.getMappingPopByDatasetId(datasetId);
		Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
		Assert.assertEquals(mappingPop, mappingPopAfter); // MappingPop updated

		List<MappingABHRow> rowsAfter = this.manager.getMappingABHRows(datasetId);
		Debug.println("ROWS AFTER: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rowsAfter);
	}


	@Test
	public void testSetMappingAllelicSNP() throws Exception {

		java.util.Map<String, Object> mappingRecords = this.createMappingRecords();

		Dataset dataset = (Dataset) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.DATASET);
		dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
		if (dataset.getDatasetName().length() > 30) {
			dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
		}
		DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.DATASET_USERS);
		MappingPop mappingPop = (MappingPop) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP);

		List<Marker> markers = new ArrayList<Marker>();
		List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
		List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
		List<MappingPopValues> mappingPopValueList = new ArrayList<MappingPopValues>();
		List<CharValues> charValueList = new ArrayList<CharValues>();

		int id = 1;
		for (int i = 0; i < GenotypicDataManagerImplUploadFunctionsTest.NUMBER_OF_ROWS; i++) {

			mappingRecords = this.createMappingRecords();

			Marker marker = (Marker) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MARKER);
			marker.setMarkerId(id);
			marker.setMarkerType(GdmsType.TYPE_MAPPING.getValue());
			marker.setMarkerName(marker.getMarkerName() + id);
			markers.add(marker);

			AccMetadataSet accMetadataSet =
					(AccMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET);
			accMetadataSet.setSampleId(id);
			accMetadataSets.add(accMetadataSet);

			MarkerMetadataSet markerMetadataSet =
					(MarkerMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MARKER_METADATA_SET);
			markerMetadataSet.setMarkerId(marker.getMarkerId());
			markerMetadataSets.add(markerMetadataSet);

			MappingPopValues mappingPopValues =
					(MappingPopValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP_VALUES);
			mappingPopValues.setMarkerId(marker.getMarkerId());
			mappingPopValues.setSampleId(accMetadataSet.getSampleId());
			mappingPopValueList.add(mappingPopValues);

			CharValues charValues = (CharValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.CHAR_VALUES);
			charValues.setMarkerId(marker.getMarkerId());
			charValues.setSampleId(accMetadataSet.getSampleId());
			charValueList.add(charValues);

			id++;
		}

		Boolean addStatus =
				this.manager.setMappingAllelicSNP(dataset, datasetUser, mappingPop, markers, markerMetadataSets, accMetadataSets,
						mappingPopValueList, charValueList);

		Assert.assertTrue(addStatus);

		Debug.println(IntegrationTestBase.INDENT, "testSetMappingAllelicSNP() Added: ");
		this.printUploadedData(dataset, datasetUser, markers, markerMetadataSets, mappingPop);
		if (accMetadataSets.size() < 20) {
			Debug.printObjects(IntegrationTestBase.INDENT * 2, accMetadataSets);
			Debug.printObjects(IntegrationTestBase.INDENT * 2, mappingPopValueList);
			Debug.printObjects(IntegrationTestBase.INDENT * 2, charValueList);
		} else {
			Debug.println(IntegrationTestBase.INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
		}
		/*
		 * TO VERIFY RESULTS IN DB: select * from gdms_dataset where dataset_id = <datasetId>; select * from gdms_dataset_users order by
		 * dataset_id limit 1; select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>; select * from gdms_marker_metadataset
		 * where dataset_id = <datasetId>; select * from gdms_acc_metadataset where dataset_id = <datasetId>; select * from
		 * gdms_mapping_pop_values where dataset_id = <datasetId>; select * from gdms_char_values where dataset_id = <datasetId>;
		 */
	}

	@Test
	public void testUpdateMappingAllelicSNP() throws Exception {
		GdmsType gdmsType = GdmsType.TYPE_MAPPING;
		String updateId = " UPDATED " + String.valueOf((int) (Math.random() * 1000)); // to append to description/remarks field for the
		// update operation

		Dataset dataset = this.getTestDatasetByType(gdmsType, GdmsType.TYPE_SNP);
		if (dataset == null) {
			Debug.println(IntegrationTestBase.INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
			return;
		}

		Integer datasetId = dataset.getDatasetId();
		this.updateDataset(dataset, updateId);

		// Update markers
		List<Marker> markers = this.manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
		List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

		// Update mappingPop
		MappingPop mappingPop = this.manager.getMappingPopByDatasetId(datasetId);
		this.updateMappingPop(mappingPop, updateId);

		List<MappingAllelicSNPRow> rows = this.manager.getMappingAllelicSNPRows(datasetId);
		Debug.println("ROWS BEFORE: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rows);

		// Update existing rows
		List<MappingAllelicSNPRow> updatedRows = new ArrayList<MappingAllelicSNPRow>();
		for (MappingAllelicSNPRow row : rows) {

			// Update mappingPopValues
			MappingPopValues mappingPopValues = this.updateMappingPopValues(row.getMappingPopValues(), updateId);

			// No change in accMetadataSet
			AccMetadataSet accMetadataSet = row.getAccMetadataSet();

			// Update charValues
			CharValues charValues = this.updateCharValues(row.getCharValues(), updateId);

			updatedRows.add(new MappingAllelicSNPRow(accMetadataSet, mappingPopValues, charValues));
		}

		// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
		java.util.Map<String, Object> mappingRecords = this.createMappingRecords();
		AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.ACC_METADATA_SET);
		MappingPopValues mappingPopValues =
				(MappingPopValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.MAPPING_POP_VALUES);
		CharValues charValues = (CharValues) mappingRecords.get(GenotypicDataManagerImplUploadFunctionsTest.CHAR_VALUES);
		MappingAllelicSNPRow newRow = new MappingAllelicSNPRow(accMetadataSet, mappingPopValues, charValues);
		updatedRows.add(newRow);
		Debug.println("ADD OR UPDATE ROW: " + newRow);

		// UPDATE
		this.manager.updateMappingAllelicSNP(dataset, mappingPop, markers, markerMetadataSets, updatedRows);

		Dataset datasetAfter = this.manager.getDatasetById(datasetId);
		Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
		Assert.assertEquals(dataset, datasetAfter); // Dataset updated

		MappingPop mappingPopAfter = this.manager.getMappingPopByDatasetId(datasetId);
		Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
		Assert.assertEquals(mappingPop, mappingPopAfter); // MappingPop updated

		List<MappingAllelicSNPRow> rowsAfter = this.manager.getMappingAllelicSNPRows(datasetId);
		Debug.println("ROWS AFTER: ");
		Debug.printObjects(IntegrationTestBase.INDENT, rowsAfter);
	}

}
