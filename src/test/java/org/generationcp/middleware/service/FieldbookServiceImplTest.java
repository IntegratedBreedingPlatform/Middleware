/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.StudyTestDataUtil;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FieldbookServiceImplTest extends DataManagerIntegrationTest {

	private static FieldbookService fieldbookService;
	private static DataImportService dataImportService;
	private static Project commonTestProject;
	private static WorkbenchTestDataUtil workbenchTestDataUtil;
	private static int LEVEL = 3;
	private static CrossExpansionProperties crossExpansionProperties;

	@BeforeClass
	public static void setUp() throws Exception {
		FieldbookServiceImplTest.fieldbookService = DataManagerIntegrationTest.managerFactory.getFieldbookMiddlewareService();
		FieldbookServiceImplTest.dataImportService = DataManagerIntegrationTest.managerFactory.getDataImportService();
		FieldbookServiceImplTest.workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
		FieldbookServiceImplTest.commonTestProject = FieldbookServiceImplTest.workbenchTestDataUtil.getCommonTestProject();
		FieldbookServiceImplTest.crossExpansionProperties = new CrossExpansionProperties();
		FieldbookServiceImplTest.crossExpansionProperties.setDefaultLevel(1);
	}

	@Test
	public void testGetAllLocalNurseryDetails() throws MiddlewareQueryException {

		List<StudyDetails> nurseryStudyDetails =
				FieldbookServiceImplTest.fieldbookService.getAllLocalNurseryDetails(FieldbookServiceImplTest.commonTestProject
						.getUniqueID());
		for (StudyDetails study : nurseryStudyDetails) {
			study.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + nurseryStudyDetails.size());

	}

	@Test
	public void testGetAllLocalTrialStudyDetails() throws MiddlewareQueryException {
		List<StudyDetails> studyDetails =
				FieldbookServiceImplTest.fieldbookService.getAllLocalTrialStudyDetails(FieldbookServiceImplTest.commonTestProject
						.getUniqueID());
		for (StudyDetails study : studyDetails) {
			study.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + studyDetails.size());

	}

	@Test
	public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException {
		List<Integer> trialIds = new ArrayList<Integer>();
		trialIds.add(Integer.valueOf(1));
		List<FieldMapInfo> fieldMapCount =
				FieldbookServiceImplTest.fieldbookService.getFieldMapInfoOfTrial(trialIds,
						FieldbookServiceImplTest.crossExpansionProperties);
		for (FieldMapInfo fieldMapInfo : fieldMapCount) {
			fieldMapInfo.print(MiddlewareIntegrationTest.INDENT);
		}
		// assertTrue(fieldMapCount.getEntryCount() > 0);
	}

	@Test
	public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException {
		List<Integer> nurseryIds = new ArrayList<Integer>();
		nurseryIds.add(Integer.valueOf(5734));
		List<FieldMapInfo> fieldMapCount =
				FieldbookServiceImplTest.fieldbookService.getFieldMapInfoOfNursery(nurseryIds,
						FieldbookServiceImplTest.crossExpansionProperties);
		for (FieldMapInfo fieldMapInfo : fieldMapCount) {
			fieldMapInfo.print(MiddlewareIntegrationTest.INDENT);
		}
		// assertTrue(fieldMapCount.getEntryCount() > 0);
	}

	@Test
	public void testGetAllFieldMapsInBlockByTrialInstanceId() throws MiddlewareQueryException {
		List<FieldMapInfo> fieldMapCount =
				FieldbookServiceImplTest.fieldbookService.getAllFieldMapsInBlockByTrialInstanceId(-2, -1,
						FieldbookServiceImplTest.crossExpansionProperties);
		for (FieldMapInfo fieldMapInfo : fieldMapCount) {
			fieldMapInfo.print(MiddlewareIntegrationTest.INDENT);
		}
		// assertTrue(fieldMapCount.getEntryCount() > 0);
	}

	@Test
	public void testGetAllLocations() throws MiddlewareQueryException {
		List<Location> locations = FieldbookServiceImplTest.fieldbookService.getAllLocations();
		Debug.printObjects(locations);
	}

	@Test
	public void testGetAllBreedingLocations() throws MiddlewareQueryException {
		List<Location> locations = FieldbookServiceImplTest.fieldbookService.getAllBreedingLocations();
		Debug.printObjects(locations);
	}

	@Test
	public void testGetAllSeedLocations() throws MiddlewareQueryException {
		List<Location> locations = FieldbookServiceImplTest.fieldbookService.getAllSeedLocations();
		Debug.printObjects(locations);
	}

	@Test
	public void testGetNurseryDataSet() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		workbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);
		workbook.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testSaveTrialMeasurementRows() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
		workbook.print(MiddlewareIntegrationTest.INDENT);

		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		Workbook createdWorkbook = FieldbookServiceImplTest.fieldbookService.getTrialDataSet(id);

		createdWorkbook = WorkbookTest.addEnvironmentAndConstantVariables(createdWorkbook);

		List<MeasurementRow> observations = createdWorkbook.getObservations();
		for (MeasurementRow observation : observations) {
			List<MeasurementData> fields = observation.getDataList();
			for (MeasurementData field : fields) {
				try {
					if (field.getValue() != null) {
						field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
						field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
					}
				} catch (NumberFormatException e) {
					// Ignore. Update only numeric values
				}
			}
		}

		FieldbookServiceImplTest.fieldbookService.saveMeasurementRows(createdWorkbook);
		workbook = FieldbookServiceImplTest.fieldbookService.getTrialDataSet(id);
		assertFalse(workbook.equals(createdWorkbook));

		assertEquals("Expected " + createdWorkbook.getTrialConditions().size() + " of records for trial conditions but got "
				+ workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size());
		assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConditions(), workbook.getTrialConditions()));
		assertEquals("Expected " + createdWorkbook.getTrialConstants().size() + " of records for trial constants but got "
				+ workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size());
		assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConstants(), workbook.getTrialConstants()));

	}

	@Test
	public void testTrialSaveMeasurementRows_WithAcceptedAndMissingValues() throws MiddlewareQueryException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		Workbook createdWorkbook = FieldbookServiceImplTest.fieldbookService.getTrialDataSet(id);

		WorkbookTest.addVariatesAndObservations(createdWorkbook);
		FieldbookServiceImplTest.fieldbookService.saveMeasurementRows(createdWorkbook);
		Workbook updatedWorkbook = FieldbookServiceImplTest.fieldbookService.getTrialDataSet(id);

		List<MeasurementRow> previousObservations = createdWorkbook.getObservations();
		List<MeasurementRow> observations = updatedWorkbook.getObservations();
		int observationIndex = 0;
		for (MeasurementRow observation : observations) {
			List<MeasurementData> previousFields = previousObservations.get(observationIndex).getDataList();
			List<MeasurementData> fields = observation.getDataList();
			int dataIndex = 0;
			for (MeasurementData field : fields) {
				if (field.getMeasurementVariable().getTermId() == WorkbookTest.CRUST_ID) {
					String previousCValueId = previousFields.get(dataIndex).getcValueId();
					assertEquals("Cvalue id must be the same", previousCValueId, field.getcValueId());
					if (previousCValueId == null) {
						String previousValue = previousFields.get(dataIndex).getValue();
						if (null == previousValue || "".equals(previousValue)) {
							assertEquals("Value must be empty", "", field.getValue());
						} else {
							assertEquals("Value must be the same", previousValue, field.getValue());
						}
					}
				}
				dataIndex++;
			}
			observationIndex++;
		}
		WorkbookTest.setTestWorkbook(null);
	}

	@Test
	public void testNurserySaveMeasurementRows_WithAcceptedAndMissingValues() throws MiddlewareQueryException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		Workbook createdWorkbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);

		WorkbookTest.addVariatesAndObservations(createdWorkbook);
		FieldbookServiceImplTest.fieldbookService.saveMeasurementRows(createdWorkbook);
		Workbook updatedWorkbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);

		List<MeasurementRow> previousObservations = createdWorkbook.getObservations();
		List<MeasurementRow> observations = updatedWorkbook.getObservations();
		int observationIndex = 0;
		for (MeasurementRow observation : observations) {
			List<MeasurementData> previousFields = previousObservations.get(observationIndex).getDataList();
			List<MeasurementData> fields = observation.getDataList();
			int dataIndex = 0;
			for (MeasurementData field : fields) {
				if (field.getMeasurementVariable().getTermId() == WorkbookTest.CRUST_ID) {
					String previousCValueId = previousFields.get(dataIndex).getcValueId();
					assertEquals("Cvalue id must be the same", previousCValueId, field.getcValueId());
					if (previousCValueId == null) {
						String previousValue = previousFields.get(dataIndex).getValue();
						if (null == previousValue || "".equals(previousValue)) {
							assertEquals("Value must be empty", "", field.getValue());
						} else {
							assertEquals("Value must be the same", previousValue, field.getValue());
						}
					}
				}
				dataIndex++;
			}
			observationIndex++;
		}
		WorkbookTest.setTestWorkbook(null);
	}

	@Test
	public void testSaveNurseryMeasurementRows() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
		workbook.print(MiddlewareIntegrationTest.INDENT);

		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		Workbook createdWorkbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);

		createdWorkbook = WorkbookTest.addEnvironmentAndConstantVariables(createdWorkbook);

		List<MeasurementRow> observations = createdWorkbook.getObservations();
		for (MeasurementRow observation : observations) {
			List<MeasurementData> fields = observation.getDataList();
			for (MeasurementData field : fields) {
				try {
					if (field.getValue() != null) {
						field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
						field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
					}
				} catch (NumberFormatException e) {
					// Ignore. Update only numeric values
				}
			}
		}

		FieldbookServiceImplTest.fieldbookService.saveMeasurementRows(createdWorkbook);
		workbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);
		assertFalse(workbook.equals(createdWorkbook));

		assertEquals("Expected " + createdWorkbook.getTrialConditions().size() + " of records for trial conditions but got "
				+ workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size());
		assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConditions(), workbook.getTrialConditions()));
		assertEquals("Expected " + createdWorkbook.getTrialConstants().size() + " of records for trial constants but got "
				+ workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size());
		assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConstants(), workbook.getTrialConstants()));
	}

	@Test
	public void testGetStandardVariableIdByPropertyScaleMethodRole() throws MiddlewareQueryException {
		String property = "Germplasm entry";
		String scale = "Number";
		String method = "Enumerated";
		Integer termId =
				FieldbookServiceImplTest.fieldbookService.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method,
						PhenotypicType.GERMPLASM);
		Debug.println(MiddlewareIntegrationTest.INDENT, termId.toString());
		assertEquals((Integer) 8230, termId);
	}

	@Test
	public void testGetAllBreedingMethods() throws MiddlewareQueryException {
		List<Method> methods = FieldbookServiceImplTest.fieldbookService.getAllBreedingMethods(false);
		assertFalse(methods.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, methods);
	}

	@Test
	public void testSaveNurseryAdvanceGermplasmListCimmytWheat() throws MiddlewareQueryException {
		Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
		Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
		GermplasmList germplasmList = this.createGermplasmsCimmytWheat(germplasms, listData);

		Integer listId = FieldbookServiceImplTest.fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

		assertTrue(listId != null && listId < 0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasm List Added: ");
		Debug.println(MiddlewareIntegrationTest.INDENT * 2, germplasmList.toString());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasms Added: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, new ArrayList<Germplasm>(germplasms.keySet()));
	}

	@Test
	public void testSaveNurseryAdvanceGermplasmListOtherCrop() throws MiddlewareQueryException {
		Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
		Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
		GermplasmList germplasmList = this.createGermplasmsOtherCrop(germplasms, listData);

		Integer listId = FieldbookServiceImplTest.fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

		assertTrue(listId != null && listId < 0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasm List Added: ");
		Debug.println(MiddlewareIntegrationTest.INDENT * 2, germplasmList.toString());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasms Added: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, new ArrayList<Germplasm>(germplasms.keySet()));
	}

	@Test
	public void testSaveCrossesGermplasmListCimmytWheat() throws MiddlewareQueryException {
		Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
		Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
		GermplasmList germplasmList = this.createGermplasmsCimmytWheat(germplasms, listData);

		Integer listId = FieldbookServiceImplTest.fieldbookService.saveGermplasmList(listData, germplasmList);

		assertTrue(listId != null && listId < 0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasm List Added: ");
		Debug.println(MiddlewareIntegrationTest.INDENT * 2, germplasmList.toString());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Germplasms Added: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, new ArrayList<Germplasm>(germplasms.keySet()));
	}

	@Test
	public void testGetDistinctStandardVariableValues() throws Exception {
		int stdVarId = 8250;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8135;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8170;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8191;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8192;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8193;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8194;
		this.getDistinctStandardVariableValues(stdVarId);
		stdVarId = 8007;
		this.getDistinctStandardVariableValues(stdVarId);
	}

	private void getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException {
		List<ValueReference> list = FieldbookServiceImplTest.fieldbookService.getDistinctStandardVariableValues(stdVarId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "StandardVariable ID: " + stdVarId);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT + 2, list);
		Debug.println("");
	}

	private GermplasmList createGermplasmsCimmytWheat(Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listData) {

		int numberOfEntries = 3;

		GermplasmList germList = this.createGermplasmList();

		for (int i = 0; i < numberOfEntries; i++) {
			Germplasm g = this.createGermplasm();

			List<Name> names = new ArrayList<Name>();
			Name n = this.createGermplasmName(i);
			n.setTypeId(GermplasmNameType.UNRESOLVED_NAME.getUserDefinedFieldID());
			n.setNstat(Integer.valueOf(0));
			names.add(n);

			n = this.createGermplasmName(i);
			n.setTypeId(GermplasmNameType.CIMMYT_SELECTION_HISTORY.getUserDefinedFieldID());
			n.setNstat(Integer.valueOf(1));
			names.add(n);

			n = this.createGermplasmName(i);
			n.setTypeId(GermplasmNameType.CIMMYT_WHEAT_PEDIGREE.getUserDefinedFieldID());
			n.setNstat(Integer.valueOf(0));
			names.add(n);

			germplasms.put(g, names);

			GermplasmListData germplasmListData = this.createGermplasmListData();
			listData.put(g, germplasmListData);

		}

		return germList;
	}

	private GermplasmList createGermplasmsOtherCrop(Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listData) {

		int numberOfEntries = 3;

		GermplasmList germList = this.createGermplasmList();

		for (int i = 0; i < numberOfEntries; i++) {
			Germplasm g = this.createGermplasm();

			List<Name> names = new ArrayList<Name>();
			names.add(this.createGermplasmName(1));
			germplasms.put(g, names);

			GermplasmListData germplasmListData = this.createGermplasmListData();
			listData.put(g, germplasmListData);
		}

		return germList;
	}

	private GermplasmList createGermplasmList() {
		String name = "Test List #1_" + "_" + (int) (Math.random() * 100);
		GermplasmList germList =
				new GermplasmList(null, name, Long.valueOf(20140206), "LST", Integer.valueOf(1), name + " Description", null, 1);
		return germList;
	}

	private Germplasm createGermplasm() {
		Germplasm g = new Germplasm();
		g.setGdate(Util.getCurrentDateAsIntegerValue());
		g.setGnpgs(Integer.valueOf(0));
		g.setGpid1(Integer.valueOf(0));
		g.setGpid2(Integer.valueOf(0));
		g.setGrplce(Integer.valueOf(0));
		g.setLocationId(Integer.valueOf(9000));
		g.setMethodId(Integer.valueOf(1));
		g.setMgid(Integer.valueOf(1));
		g.setUserId(Integer.valueOf(1));
		g.setReferenceId(Integer.valueOf(1));
		return g;
	}

	private Name createGermplasmName(int i) {
		Name n = new Name();
		n.setLocationId(Integer.valueOf(9000));
		n.setNdate(Util.getCurrentDateAsIntegerValue());
		n.setNval("Germplasm_" + i + "_" + (int) (Math.random() * 100));
		n.setReferenceId(Integer.valueOf(1));
		n.setTypeId(Integer.valueOf(1));
		n.setNstat(Integer.valueOf(0));
		n.setUserId(Integer.valueOf(1));
		return n;
	}

	private GermplasmListData createGermplasmListData() {
		return new GermplasmListData(null, null, Integer.valueOf(2), 1, "EntryCode", "SeedSource", "Germplasm Name 3", "GroupName", 0,
				99992);
	}

	@Test
	public void testGetAllNurseryTypes() throws MiddlewareQueryException {
		List<ValueReference> nurseryTypes = FieldbookServiceImplTest.fieldbookService.getAllNurseryTypes();
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, nurseryTypes);
	}

	@Test
	public void testGetAllPersons() throws MiddlewareQueryException {
		List<Person> persons = FieldbookServiceImplTest.fieldbookService.getAllPersons();
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, persons);
		// Debug.println(INDENT, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(-146));
	}

	@Test
	public void testCountPlotsWithPlantsSelectedofNursery() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		// Debug.println(INDENT, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(id));
	}

	@Test
	public void testGetNurseryVariableSettings() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		workbook = FieldbookServiceImplTest.fieldbookService.getStudyVariableSettings(id, true);
		workbook.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetBlockId() throws MiddlewareQueryException {
		int datasetId = -167;
		String trialInstance = "1";
		System.out.println(FieldbookServiceImplTest.fieldbookService.getBlockId(datasetId, trialInstance));
	}

	@Test
	public void testAddFieldLocation() throws MiddlewareQueryException {
		String fieldName = "Test Field JUnit";
		Integer parentLocationId = 17649;
		int result = FieldbookServiceImplTest.fieldbookService.addFieldLocation(fieldName, parentLocationId, -1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Added: Location with id = " + result);
	}

	@Test
	public void testAddBlockLocation() throws MiddlewareQueryException {
		String blockName = "Test Block JUnit";
		Integer parentFieldId = -11;
		int result = FieldbookServiceImplTest.fieldbookService.addBlockLocation(blockName, parentFieldId, -1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Added: Location with id = " + result);
	}

	@Test
	public void testGetStudyType() throws MiddlewareQueryException {
		int studyId = -74;
		TermId studyType = FieldbookServiceImplTest.fieldbookService.getStudyType(studyId);
		System.out.println("STUDY TYPE IS " + studyType.name());
	}

	@Test
	public void testGetFolderNameById() throws MiddlewareQueryException {
		String folderName = FieldbookServiceImplTest.fieldbookService.getFolderNameById(1);
		System.out.println("Folder Name is: " + folderName);
	}

	@Test
	public void testCheckIfStudyHasFieldmap() throws MiddlewareQueryException {
		int studyId = -12;
		System.out.println("RESULT1 = " + FieldbookServiceImplTest.fieldbookService.checkIfStudyHasFieldmap(studyId));
		studyId = -18;
		System.out.println("RESULT2 = " + FieldbookServiceImplTest.fieldbookService.checkIfStudyHasFieldmap(studyId));
	}

	@Test
	public void testCheckIfStudyHasMeasurementData() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		workbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);
		List<Integer> variateIds = new ArrayList<Integer>();
		variateIds.add(new Integer(21980));
		variateIds.add(new Integer(21981));

		boolean hasMeasurementData =
				FieldbookServiceImplTest.fieldbookService.checkIfStudyHasMeasurementData(workbook.getMeasurementDatesetId(), variateIds);
		System.out.println(hasMeasurementData);
	}

	@Test
	public void testDeleteObservationsOfStudy() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = FieldbookServiceImplTest.dataImportService.saveDataset(workbook, FieldbookServiceImplTest.commonTestProject.getUniqueID());
		workbook = FieldbookServiceImplTest.fieldbookService.getNurseryDataSet(id);

		FieldbookServiceImplTest.fieldbookService.deleteObservationsOfStudy(workbook.getMeasurementDatesetId());
		workbook.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetProjectIdByName() throws Exception {
		String name = "ROOT STUDY";
		System.out.println("ID IS "
				+ FieldbookServiceImplTest.fieldbookService.getProjectIdByNameAndProgramUUID(name,
						FieldbookServiceImplTest.commonTestProject.getUniqueID()));
	}

	@Test
	public void testGetGidsByName() throws Exception {
		String name = "CG7";
		System.out.println("GIDS = " + FieldbookServiceImplTest.fieldbookService.getGermplasmIdsByName(name));
	}

	@Test
	public void testGetAllTreatmentFactors() throws Exception {
		List<Integer> hiddenFields = Arrays.asList(8200, 8380, 8210, 8220, 8400, 8410, 8581, 8582);
		List<StandardVariableReference> variables = FieldbookServiceImplTest.fieldbookService.getAllTreatmentLevels(hiddenFields);
		if (variables != null) {
			for (StandardVariableReference variable : variables) {
				System.out.println(variable);
			}
		}
	}

	@Test
	public void testSaveOrUpdateListDataProject() throws Exception {
		List<ListDataProject> list = new ArrayList<ListDataProject>();
		int projectId = -422;
		Integer originalListId = 1;
		list.add(new ListDataProject());
		list.get(0).setCheckType(1);
		list.get(0).setDesignation("DESIG1");
		list.get(0).setEntryId(1);
		list.get(0).setEntryCode("ABC1");
		list.get(0).setGermplasmId(1);
		// list.get(0).setGroupName("CROSS1");
		list.get(0).setSeedSource("SOURCE1");
		list.get(0).setListDataProjectId(null);
		// fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.NURSERY, originalListId, list);
		// fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.TRIAL, originalListId, list);
		FieldbookServiceImplTest.fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.ADVANCED, originalListId, list,
				0);
		// fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.CHECK, null, list);
	}

	@Test
	public void testGetGermplasmListsByProjectId() throws Exception {
		int projectId = -422;
		System.out.println("NURSERY");
		List<GermplasmList> lists =
				FieldbookServiceImplTest.fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.NURSERY);
		for (GermplasmList list : lists) {
			System.out.println(list);
		}
		System.out.println("TRIAL");
		lists = FieldbookServiceImplTest.fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.TRIAL);
		for (GermplasmList list : lists) {
			System.out.println(list);
		}
		System.out.println("ADVANCED");
		lists = FieldbookServiceImplTest.fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.ADVANCED);
		for (GermplasmList list : lists) {
			System.out.println(list);
		}
		System.out.println("CHECK");
		lists = FieldbookServiceImplTest.fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.CHECK);
		for (GermplasmList list : lists) {
			System.out.println(list);
		}
	}

	@Test
	public void testGetListDataProject() throws Exception {
		int listId = -31;
		System.out.println(FieldbookServiceImplTest.fieldbookService.getListDataProject(listId));
	}

	@Test
	public void testDeleteListDataProjects() throws Exception {
		int projectId = -422;

		FieldbookServiceImplTest.fieldbookService.deleteListDataProjects(projectId, GermplasmListType.ADVANCED);
	}

	@Test
	public void testDeleteStudy() throws Exception {
		StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
		String uniqueId = FieldbookServiceImplTest.commonTestProject.getUniqueID();
		DmsProject testFolder = studyTestDataUtil.createFolderTestData(uniqueId);
		DmsProject testStudy1 = studyTestDataUtil.createStudyTestData(uniqueId);
		DmsProject testStudy2 = studyTestDataUtil.createStudyTestDataWithActiveStatus(uniqueId);
		FieldbookServiceImplTest.fieldbookService.deleteStudy(testFolder.getProjectId());
		FieldbookServiceImplTest.fieldbookService.deleteStudy(testStudy1.getProjectId());
		FieldbookServiceImplTest.fieldbookService.deleteStudy(testStudy2.getProjectId());

		boolean folderExists = false;
		boolean study1Exists = false;
		boolean study2Exists = false;
		List<FolderReference> rootFolders = studyTestDataUtil.getLocalRootFolders(uniqueId);
		for (FolderReference folderReference : rootFolders) {
			if (folderReference.getId().equals(testFolder.getProjectId())) {
				folderExists = true;
			}
			if (folderReference.getId().equals(testStudy1.getProjectId())) {
				study1Exists = true;
			}
			if (folderReference.getId().equals(testStudy2.getProjectId())) {
				study2Exists = true;
			}
		}
		assertFalse("Folder should no longer be found", folderExists);
		assertFalse("Study should no longer be found", study1Exists);
		assertFalse("Study should no longer be found", study2Exists);
	}

	@Test
	public void testGetFavoriteLocationByProjectId() throws MiddlewareQueryException {
		List<Long> locationIds = new ArrayList<Long>();
		locationIds.add(1L);
		locationIds.add(2L);

		List<Location> locations = FieldbookServiceImplTest.fieldbookService.getFavoriteLocationByProjectId(locationIds);

		assertEquals("Expecting to return the same number of Location objects from the input of List of Ids", locationIds.size(),
				locations.size());
	}

	@Test
	public void testAddListDataProjectList() throws MiddlewareQueryException {
		List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		listDataProjectList.add(this.createListDataProjectTest());
		FieldbookServiceImplTest.fieldbookService.addListDataProjectList(listDataProjectList);
		assertNotNull("List 1 should have list data projects", FieldbookServiceImplTest.fieldbookService.getListDataProject(1));
	}

	@Test
	public void testFilterStandardVariableReferenceByIsAIds() {
		List<Integer> isAIds = new ArrayList<Integer>();
		isAIds.add(new Integer(1610));
		isAIds.add(new Integer(1620));
		List<StandardVariableReference> standardReferences = new ArrayList<StandardVariableReference>();
		standardReferences.add(new StandardVariableReference(18140, "GRAIN_NAME"));
		standardReferences = FieldbookServiceImplTest.fieldbookService.filterStandardVariablesByIsAIds(standardReferences, isAIds);
		assertEquals("Should have no items remaining in the list since it was filtered", 0, standardReferences.size());
	}

	private ListDataProject createListDataProjectTest() {
		ListDataProject listDataProject = new ListDataProject();
		listDataProject.setList(new GermplasmList(1));
		listDataProject.setGermplasmId(1);
		listDataProject.setCheckType(10170);
		listDataProject.setEntryId(1);
		listDataProject.setEntryCode("1");
		listDataProject.setSeedSource("Germplasm List Import_basic_2nd pedigree.xls:1");
		listDataProject.setDesignation("IR 68815-25-PMI 3-UBN 6-B-B");
		listDataProject.setGroupName("-");
		return listDataProject;
	}
	
	@Test
	public void testGetMethodByCode() throws MiddlewareQueryException {
		String code = "AGB1";
		String programUUID = null;
		Method method = fieldbookService.getMethodByCode(code, programUUID);
		assertNotNull(method);
		if(method.getUniqueID()!=null) {
			assertEquals(programUUID,method.getUniqueID());
		}
		assertEquals(code,method.getMcode());
	}

}
