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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.StudyTestDataUtil;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class StudyDataManagerImplTest extends DataManagerIntegrationTest {

	private static final Integer STUDY_ID = 10010;
	private static final Integer DATASET_ID = 10045;
	private static final Integer ROOT_STUDY_FOLDER = 1;

	private static StudyDataManager manager;
	private static OntologyDataManager ontologyManager;
	private static Project commonTestProject;
	private static WorkbenchTestDataUtil workbenchTestDataUtil;
	private static CrossExpansionProperties crossExpansionProperties;

	@BeforeClass
	public static void setUp() throws Exception {

		StudyDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getNewStudyDataManager();
		StudyDataManagerImplTest.ontologyManager = DataManagerIntegrationTest.managerFactory.getNewOntologyDataManager();
		StudyDataManagerImplTest.workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
		StudyDataManagerImplTest.workbenchTestDataUtil.setUpWorkbench();
		StudyDataManagerImplTest.commonTestProject = StudyDataManagerImplTest.workbenchTestDataUtil.getCommonTestProject();
		StudyDataManagerImplTest.crossExpansionProperties = new CrossExpansionProperties();
		StudyDataManagerImplTest.crossExpansionProperties.setWheatLevel(0);
		StudyDataManagerImplTest.crossExpansionProperties.setDefaultLevel(1);
	}

	@Test
	public void testGetStudy() throws Exception {
		Study study = StudyDataManagerImplTest.manager.getStudy(StudyDataManagerImplTest.STUDY_ID);
		Assert.assertNotNull(study);
		Debug.println(MiddlewareIntegrationTest.INDENT, "ID: " + study.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + study.getName());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Title: " + study.getTitle());
		Debug.println(MiddlewareIntegrationTest.INDENT, "PI: " + study.getPrimaryInvestigator());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Start Date:" + study.getStartDate());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Creation Date: " + study.getCreationDate());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Study status: " + study.getStatus());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Study type: " + study.getType());
	}

	@Test
	public void testGetStudyConditions() throws Exception {
		Study study = StudyDataManagerImplTest.manager.getStudy(StudyDataManagerImplTest.STUDY_ID);
		Assert.assertNotNull(study);
		VariableList vList = study.getConditions();
		for (Variable v : vList.getVariables()) {
			Debug.print(0, "name[" + v.getVariableType().getStandardVariable().getName() + "]=");
			Debug.println(MiddlewareIntegrationTest.INDENT, v.getDisplayValue());
		}
	}

	@Test
	public void testGetAllStudyFactor() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetFactorDetails");
		int studyId = 10010;
		VariableTypeList factors = StudyDataManagerImplTest.manager.getAllStudyFactors(studyId);
		Assert.assertNotNull(factors);
		Assert.assertTrue(factors.getVariableTypes().size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "FACTORS RETRIEVED " + factors.getVariableTypes().size());
		factors.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetAllStudyVariates() throws Exception {
		int studyId = 10010;
		VariableTypeList variates = StudyDataManagerImplTest.manager.getAllStudyVariates(studyId);
		Assert.assertNotNull(variates);
		Assert.assertTrue(variates.getVariableTypes().size() > 0);
		variates.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		int folderId = 1030;
		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(new ParentFolderStudyQueryFilter(folderId), 5);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetStudiesByFolder(" + folderId + "): " + resultSet.size());
		Assert.assertTrue(resultSet.size() > 0);
		while (resultSet.hasMore()) {
			StudyReference studyRef = resultSet.next();
			Debug.println(MiddlewareIntegrationTest.INDENT, studyRef.toString());
		}
	}

	@Test
	public void testSearchStudiesForName() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testSearchStudiesForName");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

		filter.setName("FooFoo"); // INVALID: Not a study, should not find any
		// studies
		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);
		Assert.assertTrue(resultSet.size() == 0);

		filter.setName("RYT2000WS"); // VALID: is a study

		resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);
		Debug.println(MiddlewareIntegrationTest.INDENT, "INPUT: " + filter);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + resultSet.next());
			System.out.flush();
		}
		/*
		 * to test deleted study, run in mysql: update projectprop set value = 12990 where type_id = 8006 and project_id = (select
		 * project_id from project where name = 'RYT2000WS') then uncomment the test below
		 */
		// Assert.assertTrue(resultSet.size() == 0);
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(20050119);

		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);

		Debug.println(MiddlewareIntegrationTest.INDENT, "INPUT: " + filter);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + resultSet.next());
			System.out.flush();
		}
		/*
		 * to test deleted study, uncomment line above, then run in mysql: update projectprop set value = 12990 where type_id = 8006 and
		 * project_id = 5739 Note: 5739 is one of the project_id returned then uncomment the test below
		 */
		// Assert.assertTrue(resultSet.size() == before-1);
	}

	@Test
	public void testSearchStudiesForSeason() throws Exception {
		Season seasons[] = {Season.GENERAL, Season.DRY, Season.WET};
		for (Season season : seasons) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Season: " + season);
			BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
			filter.setSeason(season);
			StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);
			Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + resultSet.size());
			while (resultSet.hasMore()) {
				Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + resultSet.next());
				System.out.flush();
			}
		}
	}

	@Test
	public void testSearchStudiesForCountry() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

		filter.setCountry("Republic of the Philippines");

		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);
		Debug.println(MiddlewareIntegrationTest.INDENT, "INPUT: " + filter);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + resultSet.next());
			System.out.flush();
		}
	}

	@Test
	public void testSearchStudiesForAll() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(20050119);
		filter.setName("RYT2000WS"); // VALID: is a study
		filter.setCountry("Republic of the Philippines");
		filter.setSeason(Season.DRY);

		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 10);
		Debug.println(MiddlewareIntegrationTest.INDENT, "INPUT: " + filter);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + resultSet.next());
			System.out.flush();
		}
	}

	@Test
	public void testGetRootFolders() throws Exception {
		List<FolderReference> rootFolders =
				StudyDataManagerImplTest.manager.getRootFolders(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Assert.assertNotNull(rootFolders);
		// this should contain the nursery and trial templates
		Assert.assertFalse(rootFolders.isEmpty());

		StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
		String uniqueId = StudyDataManagerImplTest.commonTestProject.getUniqueID();
		studyTestDataUtil.createFolderTestData(uniqueId);
		studyTestDataUtil.createStudyTestData(uniqueId);

		rootFolders = StudyDataManagerImplTest.manager.getRootFolders(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse(rootFolders.isEmpty());

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRootFolders(): " + rootFolders.size());
		for (FolderReference node : rootFolders) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "   " + node);
			Assert.assertEquals(node.getParentFolderId(), StudyDataManagerImplTest.ROOT_STUDY_FOLDER);
		}
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {
		StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
		String uniqueId = StudyDataManagerImplTest.commonTestProject.getUniqueID();
		DmsProject folderWithUUID = studyTestDataUtil.createFolderTestData(uniqueId);
		DmsProject folderWithoutUUID = studyTestDataUtil.createFolderTestData(null);

		List<Integer> folderIds = Arrays.asList(25000, 1);
		for (Integer folderId : folderIds) {
			Debug.println(MiddlewareIntegrationTest.INDENT, " folderId = " + folderId);
			List<Reference> childrenNodes =
					StudyDataManagerImplTest.manager
							.getChildrenOfFolder(folderId, StudyDataManagerImplTest.commonTestProject.getUniqueID());
			Assert.assertNotNull(childrenNodes);
			Assert.assertTrue(childrenNodes.size() > 0);
			Debug.println(MiddlewareIntegrationTest.INDENT, "testGetChildrenOfFolder(folderId=" + folderId + "): " + childrenNodes.size());
			for (Reference node : childrenNodes) {
				Debug.println(MiddlewareIntegrationTest.INDENT, "   " + node);
				if (node.getId().intValue() == folderWithUUID.getProjectId().intValue()) {
					Assert.assertNotNull(node.getProgramUUID());
				} else if (node.getId().intValue() == folderWithoutUUID.getProjectId().intValue()) {
					Assert.assertNull(node.getProgramUUID());
				}
			}
		}
		studyTestDataUtil.deleteTestData(folderWithUUID.getProjectId());
		studyTestDataUtil.deleteTestData(folderWithoutUUID.getProjectId());
	}

	@Test
	public void testGetDatasetNodesByStudyId() throws Exception {
		Integer studyId = 10010;
		List<DatasetReference> datasetReferences = StudyDataManagerImplTest.manager.getDatasetReferences(studyId);
		Assert.assertNotNull(datasetReferences);
		Assert.assertTrue(datasetReferences.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Dataset Nodes By Study Id Count: " + datasetReferences.size());
		for (DatasetReference node : datasetReferences) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "   " + node);
		}
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		Integer gid = 2434138;
		GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
		StudyResultSet resultSet = StudyDataManagerImplTest.manager.searchStudies(filter, 50);
		Assert.assertNotNull(resultSet);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Study Count: " + resultSet.size());
		while (resultSet.hasMore()) {
			StudyReference studyRef = resultSet.next();
			Debug.println(MiddlewareIntegrationTest.INDENT, studyRef.toString());
		}
	}

	@Test
	public void testAddStudy() throws Exception {

		int parentStudyId = 1;

		VariableTypeList typeList = new VariableTypeList();

		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		VariableList locationVariableList = this.createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		studyValues.setLocationId(StudyDataManagerImplTest.manager.addTrialEnvironment(locationVariableList));

		VariableList germplasmVariableList = this.createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(StudyDataManagerImplTest.manager.addStock(germplasmVariableList));

		StudyReference studyRef =
				StudyDataManagerImplTest.manager.addStudy(parentStudyId, typeList, studyValues,
						StudyDataManagerImplTest.commonTestProject.getUniqueID());

		Assert.assertNotNull(studyRef.getId());
		Assert.assertTrue(studyRef.getId() != 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddStudy(): " + studyRef);
	}

	@Test
	public void testAddStudyWithNoLocation() throws Exception {

		int parentStudyId = 1;

		VariableTypeList typeList = new VariableTypeList();

		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		typeList.add(this.createVariableType(TermId.GID.getId(), "GID", "gid", 3));

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		studyValues.setLocationId(null);

		VariableList germplasmVariableList = this.createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(StudyDataManagerImplTest.manager.addStock(germplasmVariableList));

		StudyReference studyRef = StudyDataManagerImplTest.manager.addStudy(parentStudyId, typeList, studyValues, null);

		Assert.assertTrue(studyRef.getId() < 0);
		Study study = StudyDataManagerImplTest.manager.getStudy(studyRef.getId());
		study.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetDataSet() throws Exception {
		for (int i = 10015; i <= 10075; i += 10) {
			DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(i);
			dataSet.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testGetDataSetOfSorghum() throws Exception { // GCP-4986
		int dataSetId = -4; // Local sorghum
		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(dataSetId);
		dataSet.print(MiddlewareIntegrationTest.INDENT);
		List<Experiment> experiments =
				StudyDataManagerImplTest.manager.getExperiments(dataSetId, 0,
						(int) StudyDataManagerImplTest.manager.countExperiments(dataSetId));
		Debug.println(MiddlewareIntegrationTest.INDENT, " Experiments: " + experiments.size());

		Debug.println(MiddlewareIntegrationTest.INDENT, " Variables.getDisplayValue(): " + experiments.size());
		for (Experiment experiment : experiments) {
			List<Variable> variables = new ArrayList<Variable>();

			VariableList factors = experiment.getFactors();
			if (factors != null) {
				variables.addAll(factors.getVariables());
			}

			VariableList variates = experiment.getVariates();
			if (variates != null) {
				variables.addAll(variates.getVariables());
			}

			for (Variable variable : variables) {
				if (!"GID".equals(variable.getVariableType().getLocalName().trim())) {
					String value = variable.getDisplayValue();
					Debug.println(MiddlewareIntegrationTest.INDENT, "Data Type is "
							+ variable.getVariableType().getStandardVariable().getDataType().getName());
					Debug.println(MiddlewareIntegrationTest.INDENT, "\t" + experiment.getId() + "  :  "
							+ variable.getVariableType().getStandardVariable().getName() + "  :  " + value);
				}
			}
		}
	}

	@Test
	public void testCountExperiments() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Dataset Experiment Count: " + StudyDataManagerImplTest.manager.countExperiments(StudyDataManagerImplTest.DATASET_ID));
	}

	@Test
	public void testGetExperiments() throws Exception {
		for (int i = 0; i < 2; i++) {
			List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperiments(StudyDataManagerImplTest.DATASET_ID, 50 * i, 50);
			for (Experiment experiment : experiments) {
				experiment.print(MiddlewareIntegrationTest.INDENT);
			}
		}
	}

	@Test
	public void testGetExperimentsWithAverage() throws Exception {
		List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperiments(5803, 0, 50);
		for (Experiment experiment : experiments) {
			experiment.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testGetExperimentsWithTrialEnvironments() throws Exception {
		List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperimentsWithTrialEnvironment(5803, 5803, 0, 50);
		for (Experiment experiment : experiments) {
			experiment.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testAddDataSet() throws Exception {
		// Parent study, assign a parent study id value, if none exists in db,
		// you may create a dummy one. or you may run testAddStudy first to
		// create
		// the study
		int parentStudyId = -1;

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();
		Variable variable;

		// please make sure that the study name is unique and does not exist in
		// the db.
		variable = this.createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_NAME", "Dataset name (local)");
		variableList.add(variable);

		variable = this.createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
		variableList.add(variable);

		variable = this.createVariable(TermId.DATASET_TYPE.getId(), "10070", 3);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
		variableList.add(variable);

		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setVariables(variableList);

		DatasetReference datasetReference = StudyDataManagerImplTest.manager.addDataSet(parentStudyId, typeList, datasetValues, null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Dataset added : " + datasetReference);

	}

	@Test
	public void testAddDatasetWithNoDataType() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test addDatasetWithNoCoreValues");
		StudyReference studyRef = this.addTestStudy();
		VariableTypeList typeList = new VariableTypeList();

		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName("No Datatype dataset" + new Random().nextInt(10000));
		datasetValues.setDescription("whatever ds");

		VariableType variableType = this.createVariableType(18000, "Grain Yield", "whatever", 4);
		typeList.add(variableType);

		variableType = this.createVariableType(18050, "Disease Pressure", "whatever", 5);
		typeList.add(variableType);

		variableType = this.createVariableType(8200, "Plot No", "whatever", 6);
		typeList.add(variableType);

		DatasetReference dataSetRef = StudyDataManagerImplTest.manager.addDataSet(studyRef.getId(), typeList, datasetValues, null);

		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(dataSetRef.getId());
		dataSet.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testAddDataSetVariableType() throws Exception {
		// Parent study, assign a parent study id value, if none exists in db,
		// you may create a dummy one. or you may run testAddStudy first to
		// create
		// the study
		int parentStudyId = -1;

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();
		Variable variable;

		// please make sure that the study name is unique and does not exist in
		// the db.
		variable = this.createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_NAME", "Dataset name (local)");
		variableList.add(variable);

		variable = this.createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
		variableList.add(variable);

		variable = this.createVariable(TermId.DATASET_TYPE.getId(), "10070", 3);
		typeList.add(variable.getVariableType());
		this.updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
		variableList.add(variable);

		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setVariables(variableList);

		DatasetReference datasetReference = StudyDataManagerImplTest.manager.addDataSet(parentStudyId, typeList, datasetValues, null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Dataset added : " + datasetReference);

		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(datasetReference.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Original Dataset");
		dataSet.print(3);

		VariableType variableType = new VariableType();
		variableType.setLocalName("Dog");
		variableType.setLocalDescription("Man's best friend");
		variableType.setStandardVariable(StudyDataManagerImplTest.ontologyManager.getStandardVariable(8240));
		variableType.setRank(99);
		StudyDataManagerImplTest.manager.addDataSetVariableType(dataSet.getId(), variableType);

		dataSet = StudyDataManagerImplTest.manager.getDataSet(datasetReference.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Modified Dataset");
		dataSet.print(3);

	}

	@Test
	public void testAddExperiment() throws Exception {
		List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperiments(10015, 0, /* 1093 */1);
		int dataSetId = -1;
		ExperimentValues experimentValues = new ExperimentValues();
		List<Variable> varList = new ArrayList<Variable>();
		varList.addAll(experiments.get(0).getFactors().getVariables());
		varList.addAll(experiments.get(0).getVariates().getVariables());
		VariableList list = new VariableList();
		list.setVariables(varList);
		experimentValues.setVariableList(list);
		experimentValues.setGermplasmId(-1);
		experimentValues.setLocationId(-1);
		StudyDataManagerImplTest.manager.addExperiment(dataSetId, ExperimentType.PLOT, experimentValues);
	}

	@Test
	public void testSetExperimentValue() throws Exception {
		StudyReference studyRef = this.addTestStudy();
		DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
		this.addTestExperiments(datasetRef.getId(), 4);
		List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperiments(datasetRef.getId(), 0, 2);

		this.printExperiments("Original", datasetRef.getId());
		for (Experiment experiment : experiments) {
			StudyDataManagerImplTest.manager.setExperimentValue(experiment.getId(), 18000, "666");
			StudyDataManagerImplTest.manager.setExperimentValue(experiment.getId(), 18050, "19010");
			StudyDataManagerImplTest.manager.setExperimentValue(experiment.getId(), 8200, "4");
		}
		this.printExperiments("Modified", datasetRef.getId());
	}

	@Test
	public void testGetTrialEnvironmentsInDataset() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test getTrialEnvironmentsInDataset");
		TrialEnvironments trialEnvironments = StudyDataManagerImplTest.manager.getTrialEnvironmentsInDataset(10085);
		trialEnvironments.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetStocksInDataset() throws Exception {
		Stocks stocks = StudyDataManagerImplTest.manager.getStocksInDataset(10085);
		stocks.print(MiddlewareIntegrationTest.INDENT);
	}

	private void printExperiments(String title, int datasetId) throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, title);
		List<Experiment> experiments = StudyDataManagerImplTest.manager.getExperiments(datasetId, 0, 4);
		for (Experiment experiment : experiments) {
			experiment.print(3);
		}
	}

	@Test
	public void testAddTrialEnvironment() throws Exception {
		VariableList variableList = this.createTrialEnvironment("loc desc", "1.1", "2.2", "datum", "3.3", "prop1", "prop2");
		StudyDataManagerImplTest.manager.addTrialEnvironment(variableList);
	}

	@Test
	public void testAddGermplasm() throws Exception {
		VariableList variableList = this.createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		StudyDataManagerImplTest.manager.addStock(variableList);
	}

	@Test
	public void testGetFactorsByProperty() throws Exception {
		int propertyId = 2205;
		int datasetId = 10015;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetFactorsByProperty (dataset=" + datasetId + ", property=" + propertyId);
		DataSet dataset = StudyDataManagerImplTest.manager.getDataSet(datasetId);
		VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
		if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
			for (VariableType factor : factors.getVariableTypes()) {
				factor.print(MiddlewareIntegrationTest.INDENT);
			}
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH PROPERTY = "
					+ propertyId);
		}
	}

	@Test
	public void testGetFactorsByPhenotypicType() throws Exception {
		PhenotypicType phenotypicType = PhenotypicType.DATASET;
		int datasetId = 10087;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetFactorsByPhenotypicType (dataset=" + datasetId + ", role=" + phenotypicType
				+ ")");
		DataSet dataset = StudyDataManagerImplTest.manager.getDataSet(datasetId);
		if (dataset != null) {
			VariableTypeList factors = dataset.getFactorsByPhenotypicType(phenotypicType);

			if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
				for (VariableType factor : factors.getVariableTypes()) {
					factor.print(MiddlewareIntegrationTest.INDENT);
				}
			} else {
				Debug.println(MiddlewareIntegrationTest.INDENT, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH FACTOR TYPE = "
						+ phenotypicType);
			}
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "DATASET = " + datasetId + " NOT FOUND. ");
		}
	}

	@Test
	public void testGetDataSetsByType() throws Exception {
		int studyId = 10010;
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType
				+ ")");
		List<DataSet> datasets = StudyDataManagerImplTest.manager.getDataSetsByType(studyId, dataSetType);
		for (DataSet dataset : datasets) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}

		studyId = 10080;
		dataSetType = DataSetType.MEANS_DATA;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType
				+ ")");
		datasets = StudyDataManagerImplTest.manager.getDataSetsByType(studyId, dataSetType);
		for (DataSet dataset : datasets) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}

		Debug.println(MiddlewareIntegrationTest.INDENT, "Display data set type in getDataSet");
		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(10087);
		Debug.println(MiddlewareIntegrationTest.INDENT, "DataSet = " + dataSet.getId() + ", name = " + dataSet.getName()
				+ ", description = " + dataSet.getDescription() + ", type = " + dataSet.getDataSetType());
	}

	@Test
	public void testFindOneDataSetByType() throws Exception {
		int studyId = 10010;
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType
				+ ")");
		DataSet dataset = StudyDataManagerImplTest.manager.findOneDataSetByType(studyId, dataSetType);
		if (dataset != null) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}

		studyId = 10080;
		dataSetType = DataSetType.MEANS_DATA;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType
				+ ")");
		dataset = StudyDataManagerImplTest.manager.findOneDataSetByType(studyId, dataSetType);
		if (dataset != null) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}

		dataSetType = DataSetType.SUMMARY_DATA;
		Debug.println(MiddlewareIntegrationTest.INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType
				+ ")");
		dataset = StudyDataManagerImplTest.manager.findOneDataSetByType(studyId, dataSetType);
		Assert.assertNull(dataset);
	}

	@Test
	public void testCountExperimentsByTrialEnvironmentAndVariate() throws Exception {
		long count = StudyDataManagerImplTest.manager.countExperimentsByTrialEnvironmentAndVariate(10070, 20870);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Count of Experiments By TE and Variate: " + count);
	}

	@Test
	public void testCountStocks() throws Exception {
		long count = StudyDataManagerImplTest.manager.countStocks(10087, 10081, 18190);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test CountStocks: " + count);
	}

	@Test
	public void testDeleteDataSet() throws Exception {
		StudyReference studyRef = this.addTestStudy();
		DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
		this.addTestExperiments(datasetRef.getId(), 10);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Test Delete DataSet: " + datasetRef.getId());
		StudyDataManagerImplTest.manager.deleteDataSet(datasetRef.getId());
	}

	@Test
	public void testDeleteExperimentsByLocation() throws Exception {
		StudyReference studyRef = this.addTestStudyWithNoLocation();
		DatasetReference datasetRef = this.addTestDatasetWithLocation(studyRef.getId());
		int locationId = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);
		int locationId2 = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Test Delete ExperimentsByLocation: " + datasetRef.getId() + ", " + locationId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Location id of " + locationId2 + " will NOT be deleted");
		StudyDataManagerImplTest.manager.deleteExperimentsByLocation(datasetRef.getId(), locationId);
	}

	@Test
	public void testGetLocalNameByStandardVariableId() throws Exception {
		Integer projectId = 10085;
		Integer standardVariableId = 8230;
		String localName = StudyDataManagerImplTest.manager.getLocalNameByStandardVariableId(projectId, standardVariableId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocalNameByStandardVariableId(" + projectId + ", " + standardVariableId
				+ "): " + localName);
	}

	@Test
	public void testGetAllStudyDetails() throws Exception {
		List<StudyDetails> nurseryStudyDetails =
				StudyDataManagerImplTest.manager.getAllStudyDetails(StudyType.N, StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetAllStudyDetails(StudyType.N, " + StudyDataManagerImplTest.commonTestProject.getUniqueID() + ")");
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, nurseryStudyDetails);
	}

	@Test
	public void testGetAllNurseryAndTrialStudyNodes() throws Exception {
		List<StudyNode> studyNodes =
				StudyDataManagerImplTest.manager.getAllNurseryAndTrialStudyNodes(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, studyNodes);
	}

	@Test
	public void testCountProjectsByVariable() throws Exception {
		int variableId = 8050;
		long count = StudyDataManagerImplTest.manager.countProjectsByVariable(variableId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countProjectsByVariable on " + variableId + " = " + count);
	}

	@Test
	public void testCountExperimentsByProjectPropVariable() throws Exception {
		int variableId = 8050;
		int storedInId = 1010;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByProjectVariable() throws Exception {
		int variableId = 8005;
		int storedInId = 1011;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByExperimentPropVariable() throws Exception {
		int variableId = 8200;
		int storedInId = 1030;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByGeolocationVariable() throws Exception {
		int variableId = 8170;
		int storedInId = 1021;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByGeolocationPropVariable() throws Exception {
		int variableId = 8370;
		int storedInId = 1020;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByStockVariable() throws Exception {
		int variableId = 8230;
		int storedInId = 1041;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByStockPropVariable() throws Exception {
		int variableId = 8255;
		int storedInId = 1040;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testCountExperimentsByPhenotypeVariable() throws Exception {
		int variableId = 18000;
		int storedInId = 1043;
		long count = StudyDataManagerImplTest.manager.countExperimentsByVariable(variableId, storedInId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
	}

	@Test
	public void testSaveTrialDatasetSummary() throws Exception {

		// get an existing trial environment dataset
		Integer trialDataSetId = 25008;
		DmsProject project = manager.getProject(trialDataSetId);

		if (project != null) {

			// get the geolocation_id of the first trial instance, we will add the summary variables here
			Integer locationId = manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(project.getProjectId(), "1");
			List<Integer> locationIds = new ArrayList<>();
			locationIds.add(locationId);

			// create a list of summary variables to add
			VariableTypeList variableTypeList = this.createVariatypeListForSummary();
			List<ExperimentValues> experimentValues = this.createExperimentValues(variableTypeList, locationId);

			// add variableTypes to project properties if not exists
			VariableTypeList nonExistingVariableTypes = new VariableTypeList();
			for (VariableType variableType : variableTypeList.getVariableTypes()) {
				if (manager.getDataSet(trialDataSetId).findVariableTypeByLocalName(variableType.getLocalName()) == null) {
					nonExistingVariableTypes.add(variableType);
				}
			}

			// save or update the summary variable to the trial dataset
			StudyDataManagerImplTest.manager.saveTrialDatasetSummary(project, nonExistingVariableTypes, experimentValues, locationIds);

			List<Experiment> experiments = manager.getExperiments(trialDataSetId, 0, 1);

			Assert.assertNotNull(experiments);

			for (VariableType variable : variableTypeList.getVariableTypes()) {
				Variable savedVariable = experiments.get(0).getVariates().findByLocalName(variable.getLocalName());
				Assert.assertNotNull(savedVariable);
				Assert.assertEquals("12345", savedVariable.getValue());
			}
		}

	}

	private List<ExperimentValues> createExperimentValues(VariableTypeList variableTypeList, Integer locationId) {
		List<ExperimentValues> experimentValues = new ArrayList<ExperimentValues>();

		ExperimentValues expValue = new ExperimentValues();
		List<Variable> traits = new ArrayList<Variable>();
		VariableList variableList = new VariableList();
		variableList.setVariables(traits);

		expValue.setLocationId(locationId);

		for (VariableType variableType : variableTypeList.getVariableTypes()) {
			variableList.add(new Variable(variableType, "12345"));
		}

		expValue.setVariableList(variableList);
		experimentValues.add(expValue);

		return experimentValues;
	}

	private VariableTypeList createVariatypeListForSummary() throws Exception {

		VariableTypeList variableTypeList = new VariableTypeList();

		VariableType grainYieldSem = this.createVariableType(18140, "GRAIN_YIELD_SUMMARY_STAT", "test", 100);
		variableTypeList.add(grainYieldSem);

		return variableTypeList;
	}

	private Variable createVariable(int termId, String value, int rank) throws Exception {
		StandardVariable stVar = StudyDataManagerImplTest.ontologyManager.getStandardVariable(termId);

		VariableType vtype = new VariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	private VariableType createVariableType(int termId, String name, String description, int rank) throws Exception {
		StandardVariable stdVar = StudyDataManagerImplTest.ontologyManager.getStandardVariable(termId);

		VariableType vtype = new VariableType();
		vtype.setLocalName(name);
		vtype.setLocalDescription(description);
		vtype.setRank(rank);
		vtype.setStandardVariable(stdVar);

		return vtype;
	}

	private void updateVariableType(VariableType type, String name, String description) {
		type.setLocalName(name);
		type.setLocalDescription(description);
	}

	private VariableList createTrialEnvironment(String name, String latitude, String longitude, String data, String altitude,
			String property1, String property2) throws Exception {
		VariableList variableList = new VariableList();
		variableList.add(this.createVariable(8170, name, 0));
		variableList.add(this.createVariable(8191, latitude, 0));
		variableList.add(this.createVariable(8192, longitude, 0));
		variableList.add(this.createVariable(8193, data, 0));
		variableList.add(this.createVariable(8194, altitude, 0));
		variableList.add(this.createVariable(8135, property1, 0));
		variableList.add(this.createVariable(8180, property2, 0));
		variableList.add(this.createVariable(8195, "999", 0));
		return variableList;
	}

	private VariableList createGermplasm(String name, String gid, String designation, String code, String property1, String property2)
			throws Exception {
		VariableList variableList = new VariableList();
		variableList.add(this.createVariable(8230, name, 1));
		variableList.add(this.createVariable(8240, gid, 2));
		variableList.add(this.createVariable(8250, designation, 3));
		variableList.add(this.createVariable(8300, code, 4));
		variableList.add(this.createVariable(8255, property1, 5));
		variableList.add(this.createVariable(8377, property2, 6));
		return variableList;
	}

	private StudyReference addTestStudy() throws Exception {
		int parentStudyId = 1;

		VariableTypeList typeList = new VariableTypeList();

		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		VariableList locationVariableList = this.createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		studyValues.setLocationId(StudyDataManagerImplTest.manager.addTrialEnvironment(locationVariableList));

		VariableList germplasmVariableList = this.createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(StudyDataManagerImplTest.manager.addStock(germplasmVariableList));

		return StudyDataManagerImplTest.manager.addStudy(parentStudyId, typeList, studyValues, null);
	}

	private StudyReference addTestStudyWithNoLocation() throws Exception {
		int parentStudyId = 1;

		VariableTypeList typeList = new VariableTypeList();

		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		VariableList germplasmVariableList = this.createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(StudyDataManagerImplTest.manager.addStock(germplasmVariableList));

		return StudyDataManagerImplTest.manager.addStudy(parentStudyId, typeList, studyValues, null);
	}

	private DatasetReference addTestDataset(int studyId) throws Exception {
		// Parent study, assign a parent study id value, if none exists in db,

		VariableTypeList typeList = new VariableTypeList();

		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName("My Dataset Name " + new Random().nextInt(10000));
		datasetValues.setDescription("My Dataset Description");
		datasetValues.setType(DataSetType.MEANS_DATA);

		VariableType variableType = this.createVariableType(18000, "Grain Yield", "whatever", 4);
		typeList.add(variableType);

		variableType = this.createVariableType(18050, "Disease Pressure", "whatever", 5);
		typeList.add(variableType);

		variableType = this.createVariableType(8200, "Plot No", "whatever", 6);
		typeList.add(variableType);

		return StudyDataManagerImplTest.manager.addDataSet(studyId, typeList, datasetValues, null);
	}

	private DatasetReference addTestDatasetWithLocation(int studyId) throws Exception {
		// Parent study, assign a parent study id value, if none exists in db,

		VariableTypeList typeList = new VariableTypeList();

		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName("My Dataset Name " + new Random().nextInt(10000));
		datasetValues.setDescription("My Dataset Description");
		datasetValues.setType(DataSetType.MEANS_DATA);

		VariableType variableType = this.createVariableType(18000, "Grain Yield", "whatever", 4);
		typeList.add(variableType);

		variableType = this.createVariableType(18050, "Disease Pressure", "whatever", 5);
		typeList.add(variableType);

		variableType = this.createVariableType(8200, "Plot No", "whatever", 6);
		typeList.add(variableType);

		variableType = this.createVariableType(8195, "Site Code", "whatever", 7);
		typeList.add(variableType);

		return StudyDataManagerImplTest.manager.addDataSet(studyId, typeList, datasetValues, null);
	}

	public void addTestExperiments(int datasetId, int numExperiments) throws Exception {
		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(datasetId);
		for (int i = 0; i < numExperiments; i++) {
			ExperimentValues experimentValues = new ExperimentValues();
			VariableList varList = new VariableList();
			varList.add(this.createVariable(dataSet, 18000, "99"));
			varList.add(this.createVariable(dataSet, 18050, "19000"));
			varList.add(this.createVariable(dataSet, 8200, "3"));

			experimentValues.setVariableList(varList);
			experimentValues.setGermplasmId(-1);
			experimentValues.setLocationId(-1);
			StudyDataManagerImplTest.manager.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
		}
	}

	public int addTestExperimentsWithLocation(int datasetId, int numExperiments) throws Exception {
		VariableList locationVariableList = this.createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		int locationId = StudyDataManagerImplTest.manager.addTrialEnvironment(locationVariableList);

		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(datasetId);
		for (int i = 0; i < numExperiments; i++) {
			ExperimentValues experimentValues = new ExperimentValues();
			VariableList varList = new VariableList();
			varList.add(this.createVariable(dataSet, 18000, "99"));
			varList.add(this.createVariable(dataSet, 18050, "19000"));
			varList.add(this.createVariable(dataSet, 8200, "3"));

			experimentValues.setVariableList(varList);
			experimentValues.setGermplasmId(-1);
			experimentValues.setLocationId(locationId);
			StudyDataManagerImplTest.manager.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
		}
		return locationId;
	}

	private Variable createVariable(DataSet dataSet, int stdVarId, String value) {
		Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(dataSet.getVariableTypes().findById(stdVarId));
		return variable;
	}

	public void testCheckIfProjectNameIsExisting() throws Exception {
		Study study = StudyDataManagerImplTest.manager.getStudy(10010);
		String name = study.getName();
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + name);
		boolean isExisting =
				StudyDataManagerImplTest.manager.checkIfProjectNameIsExistingInProgram(name,
						StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Assert.assertTrue(isExisting);

		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + name);
		isExisting =
				StudyDataManagerImplTest.manager.checkIfProjectNameIsExistingInProgram(name,
						StudyDataManagerImplTest.commonTestProject.getUniqueID());
		Assert.assertFalse(isExisting);
	}

	@Test
	public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException {
		List<Integer> trialIdList = new ArrayList<Integer>();
		trialIdList.addAll(Arrays.asList(Integer.valueOf(-4)));
		List<FieldMapInfo> fieldMapInfos =
				StudyDataManagerImplTest.manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T,
						StudyDataManagerImplTest.crossExpansionProperties);
		for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
			Debug.println(MiddlewareIntegrationTest.INDENT, fieldMapInfo.getFieldbookName());
			if (fieldMapInfo.getDatasets() != null) {
				Debug.println(MiddlewareIntegrationTest.INDENT, fieldMapInfo.getDatasets().toString());
			}
		}
		// assertTrue(fieldMapCount.getEntryCount() > 0);
	}

	@Test
	public void testGetParentFolder() throws MiddlewareQueryException {
		DmsProject proj = StudyDataManagerImplTest.manager.getParentFolder(10010);
		if (proj == null) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Parent is null");
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Parent is NOT null");
		}
	}

	@Test
	public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException {
		List<Integer> nurseryIdList = new ArrayList<Integer>();

		// REPLACED BY THIS TO MAKE THE JUNIT WORK - Get the first nursery from
		// the db
		List<StudyDetails> studyDetailsList =
				StudyDataManagerImplTest.manager
						.getAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		if (studyDetailsList != null && studyDetailsList.size() > 0) {
			for (StudyDetails study : studyDetailsList) {
				if (study.getStudyType() == StudyType.N) {
					nurseryIdList.add(study.getId());
					break;
				}
			}
		}

		if (nurseryIdList.size() > 0) {

			List<FieldMapInfo> fieldMapInfos =
					StudyDataManagerImplTest.manager.getFieldMapInfoOfStudy(nurseryIdList, StudyType.N,
							StudyDataManagerImplTest.crossExpansionProperties);
			for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
				Debug.println(MiddlewareIntegrationTest.INDENT, fieldMapInfo.getFieldbookName());
				if (fieldMapInfo.getDatasets() != null) {
					Debug.println(MiddlewareIntegrationTest.INDENT, fieldMapInfo.getDatasets().toString());
				}
			}
			// assertTrue(fieldMapCount.getEntryCount() > 0);
		}
	}

	@Test
	public void testGetGeolocationPropValue() throws MiddlewareQueryException {
		String value = StudyDataManagerImplTest.manager.getGeolocationPropValue(TermId.LOCATION_ID.getId(), -1);
		Debug.println(MiddlewareIntegrationTest.INDENT, value);
	}

	@Test
	public void testSaveFieldMapProperties() throws MiddlewareQueryException {
		List<Integer> trialIdList = new ArrayList<Integer>();

		// REPLACED BY THIS TO MAKE THE JUNIT WORK
		List<StudyDetails> studyDetailsList =
				StudyDataManagerImplTest.manager
						.getAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		if (studyDetailsList != null && studyDetailsList.size() > 0) {
			for (StudyDetails study : studyDetailsList) {
				if (study.getStudyType() == StudyType.T) {
					trialIdList.add(study.getId());
					break;
				}
			}
		}

		List<FieldMapInfo> info =
				StudyDataManagerImplTest.manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T,
						StudyDataManagerImplTest.crossExpansionProperties);

		StudyDataManagerImplTest.manager.saveOrUpdateFieldmapProperties(info, -1, false);
	}

	@Test
	public void testGetAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetStudyDetailsWithPaging");
		Debug.println(MiddlewareIntegrationTest.INDENT, "List ALL Trials and Nurseries");
		List<StudyDetails> list =
				StudyDataManagerImplTest.manager
						.getAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		for (StudyDetails s : list) {
			Debug.println(MiddlewareIntegrationTest.INDENT, s.toString());
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, String.valueOf(StudyDataManagerImplTest.manager
				.countAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID())));
		Debug.println(MiddlewareIntegrationTest.INDENT, "List ALL Trials and Nurseries");
		list = StudyDataManagerImplTest.manager.getAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID());
		for (StudyDetails s : list) {
			Debug.println(MiddlewareIntegrationTest.INDENT, s.toString());
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, String.valueOf(StudyDataManagerImplTest.manager
				.countAllNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID())));

		Debug.println(MiddlewareIntegrationTest.INDENT, "List ALL Trials");
		list = StudyDataManagerImplTest.manager.getAllStudyDetails(StudyType.T, StudyDataManagerImplTest.commonTestProject.getUniqueID());
		for (StudyDetails s : list) {
			Debug.println(MiddlewareIntegrationTest.INDENT, s.toString());
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, String.valueOf(StudyDataManagerImplTest.manager.countAllStudyDetails(StudyType.T,
				StudyDataManagerImplTest.commonTestProject.getUniqueID())));

		Debug.println(MiddlewareIntegrationTest.INDENT, "List ALL Nurseries");
		list = StudyDataManagerImplTest.manager.getAllStudyDetails(StudyType.T, StudyDataManagerImplTest.commonTestProject.getUniqueID());
		for (StudyDetails s : list) {
			Debug.println(MiddlewareIntegrationTest.INDENT, s.toString());
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, String.valueOf(StudyDataManagerImplTest.manager.countAllStudyDetails(StudyType.N,
				StudyDataManagerImplTest.commonTestProject.getUniqueID())));

	}

	@Test
	public void testGetFolderTree() throws MiddlewareQueryException {
		List<FolderReference> tree = StudyDataManagerImplTest.manager.getFolderTree();
		Debug.println(MiddlewareIntegrationTest.INDENT, "GetFolderTree Test");
		this.printFolderTree(tree, 1);
	}

	@Test
	public void testGetPhenotypeIdsByLocationAndPlotNo() throws Exception {

		List<Integer> cvTermIds = new ArrayList<Integer>();

		DataSet dataSet = StudyDataManagerImplTest.manager.getDataSet(-9999);

		if (dataSet == null) {
			return;
		}

		for (VariableType vType : dataSet.getVariableTypes().getVariates().getVariableTypes()) {
			cvTermIds.add(vType.getStandardVariable().getId());
		}

		List<Object[]> value = StudyDataManagerImplTest.manager.getPhenotypeIdsByLocationAndPlotNo(-26, -14, 101, cvTermIds);

		Assert.assertNotNull(value);

		Debug.println(MiddlewareIntegrationTest.INDENT, "getPhenotypeIdsByLocationAndPlotNo Test");
		for (Object[] val : value) {
			Debug.println(val.toString());
		}

	}

	@Test
	public void testSaveOrUpdatePhenotypeOutliers() throws Exception {

		List<PhenotypeOutlier> outliers = new ArrayList<PhenotypeOutlier>();
		PhenotypeOutlier phenotypeOutlier = new PhenotypeOutlier();
		phenotypeOutlier.setPhenotypeId(1);
		phenotypeOutlier.setValue("hello");

		outliers.add(phenotypeOutlier);

		try {
			StudyDataManagerImplTest.manager.saveOrUpdatePhenotypeOutliers(outliers);
		} catch (Exception e) {

		}

		Debug.println(MiddlewareIntegrationTest.INDENT, "testSavePhenotypeOutlier Test");

	}

	private void printFolderTree(List<FolderReference> tree, int tab) {
		if (tree != null && tree.size() > 0) {
			for (FolderReference folder : tree) {
				for (int i = 0; i < tab; i++) {
					Debug.print(0, "\t");
				}
				Debug.println(MiddlewareIntegrationTest.INDENT, folder.getId() + " - " + folder.getName());
				this.printFolderTree(folder.getSubFolders(), tab + 1);
			}
		}
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNotNull() {
		LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		FieldmapBlockInfo fieldMapBlockInfo =
				new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK,
						FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, FieldMapDataUtil.MACHINE_ROW_CAPACITY,
						false, null, FieldMapDataUtil.FIELD_ID);

		StudyDataManagerImpl localManager = (StudyDataManagerImpl) StudyDataManagerImplTest.manager;

		List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);

		localManager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			localManager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);

			FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);

			Assert.assertEquals("Expected " + FieldMapDataUtil.ROWS_IN_BLOCK + " but got " + trialInstance.getRowsInBlock() + " instead.",
					FieldMapDataUtil.ROWS_IN_BLOCK, trialInstance.getRowsInBlock().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.RANGES_IN_BLOCK + " but got " + trialInstance.getRangesInBlock()
					+ " instead.", FieldMapDataUtil.RANGES_IN_BLOCK, trialInstance.getRangesInBlock().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT + " but got " + trialInstance.getRowsPerPlot()
					+ " instead.", FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, trialInstance.getRowsPerPlot().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.PLANTING_ORDER + " but got " + trialInstance.getPlantingOrder()
					+ " instead.", FieldMapDataUtil.PLANTING_ORDER, trialInstance.getPlantingOrder().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.MACHINE_ROW_CAPACITY + " but got " + trialInstance.getMachineRowCapacity()
					+ " instead.", FieldMapDataUtil.MACHINE_ROW_CAPACITY, trialInstance.getMachineRowCapacity().intValue());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNull() {
		LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		FieldmapBlockInfo fieldMapBlockInfo =
				new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK,
						FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, FieldMapDataUtil.MACHINE_ROW_CAPACITY,
						false, null, FieldMapDataUtil.FIELD_ID);

		StudyDataManagerImpl localManager = (StudyDataManagerImpl) StudyDataManagerImplTest.manager;

		List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);
		FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
		trialInstance.setBlockId(null);

		localManager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			localManager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);

			Assert.assertNull("Expected null but got " + trialInstance.getRowsInBlock() + " instead.", trialInstance.getRowsInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRangesInBlock() + " instead.", trialInstance.getRangesInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRowsPerPlot() + " instead.", trialInstance.getRowsPerPlot());
			Assert.assertNull("Expected null but got " + trialInstance.getPlantingOrder() + " instead.", trialInstance.getPlantingOrder());
			Assert.assertNull("Expected null but got " + trialInstance.getMachineRowCapacity() + " instead.",
					trialInstance.getMachineRowCapacity());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	public void testGetStudyType() {
		try {
			Assert.assertEquals("Study type returned did not match.", StudyType.BON,
					StudyDataManagerImplTest.manager.getStudyType(StudyDataManagerImplTest.STUDY_ID));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetStudyTypeNullEdgeCase() {
		try {
			final int PRESUMABLY_NON_EXISTENT_STUDY_ID = -1000000;
			Assert.assertNull("Expected null return value but was non null.",
					StudyDataManagerImplTest.manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_ID));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteProgramStudies() {
		StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
		String uniqueId = StudyDataManagerImplTest.commonTestProject.getUniqueID();
		try {
			studyTestDataUtil.createFolderTestData(uniqueId);
			studyTestDataUtil.createStudyTestData(uniqueId);
			studyTestDataUtil.createStudyTestDataWithActiveStatus(uniqueId);

			List<FolderReference> programStudiesAndFolders =
					studyTestDataUtil.getLocalRootFolders(StudyDataManagerImplTest.commonTestProject.getUniqueID());
			Assert.assertEquals("Current Program with programUUID " + StudyDataManagerImplTest.commonTestProject.getUniqueID()
					+ " should return 3 children", 3, programStudiesAndFolders.size());
			StudyDataManagerImplTest.manager.deleteProgramStudies(StudyDataManagerImplTest.commonTestProject.getUniqueID());
			programStudiesAndFolders = studyTestDataUtil.getLocalRootFolders(StudyDataManagerImplTest.commonTestProject.getUniqueID());
			Assert.assertEquals("Current Program with programUUID " + StudyDataManagerImplTest.commonTestProject.getUniqueID()
					+ " should return no children", 0, programStudiesAndFolders.size());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}

	}

	@Test
	public void testGetStudyDetails() throws MiddlewareQueryException {
		List<StudyDetails> studyDetailsList =
				StudyDataManagerImplTest.manager.getStudyDetails(StudyType.N, StudyDataManagerImplTest.commonTestProject.getUniqueID(), -1,
						-1);
		Assert.assertNotNull(studyDetailsList);
	}

	@Test
	public void testGetNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
		List<StudyDetails> studyDetailsList =
				StudyDataManagerImplTest.manager.getNurseryAndTrialStudyDetails(StudyDataManagerImplTest.commonTestProject.getUniqueID(),
						-1, -1);
		Assert.assertNotNull(studyDetailsList);
	}

	@Test
	public void testGetStudyDetails_ByTypeAndId() throws MiddlewareQueryException {
		DmsProject study =
				StudyTestDataUtil.getInstance().createStudyTestDataWithActiveStatus(
						StudyDataManagerImplTest.commonTestProject.getUniqueID());
		StudyDetails studyDetails = StudyDataManagerImplTest.manager.getStudyDetails(StudyType.T, study.getProjectId());
		Assert.assertNotNull("Study should not be null", studyDetails);
		Assert.assertEquals("Study should have the id " + study.getProjectId(), study.getProjectId(), studyDetails.getId());
		Assert.assertEquals("Study should have the programUUID " + StudyDataManagerImplTest.commonTestProject.getUniqueID(),
				StudyDataManagerImplTest.commonTestProject.getUniqueID(), studyDetails.getProgramUUID());
		Assert.assertEquals("Study should be a trial", StudyType.T, studyDetails.getStudyType());
	}

	@Test
	public void testGetGeolocationIdByProjectIdAndTrialInstanceNumber() {
		try {
			Integer projectId = 25007;
			String trialInstanceNumberExpected = "1";
			Integer geolocationId =
					StudyDataManagerImplTest.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(projectId,
							trialInstanceNumberExpected);
			if (geolocationId != null) {
				String trialInstanceNumberActual = StudyDataManagerImplTest.manager.getTrialInstanceNumberByGeolocationId(geolocationId);
				Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
			}
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetTrialInstanceNumberByGeolocationId() {
		try {
			String trialInstanceNumberExpected = "1";
			String trialInstanceNumberActual = StudyDataManagerImplTest.manager.getTrialInstanceNumberByGeolocationId(1);
			Assert.assertNotNull(trialInstanceNumberActual);
			Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testSaveGeolocationProperty() throws MiddlewareQueryException {
		Integer stdVarId = TermId.EXPERIMENT_DESIGN_FACTOR.getId();
		Integer studyId = 25019;
		String expDesign = StudyDataManagerImplTest.manager.getGeolocationPropValue(stdVarId, studyId);
		String newExpDesign = null;
		if (expDesign != null) {
			if (TermId.RANDOMIZED_COMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId());
			} else if (TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId());
			}
			// update experimental design value
			int ndGeolocationId = StudyDataManagerImplTest.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(studyId, "1");
			StudyDataManagerImplTest.manager.saveGeolocationProperty(ndGeolocationId, stdVarId, newExpDesign);
			String actualExpDesign = StudyDataManagerImplTest.manager.getGeolocationPropValue(stdVarId, studyId);
			Assert.assertEquals(newExpDesign, actualExpDesign);
			Assert.assertNotEquals(expDesign, actualExpDesign);
			// revert to previous value
			StudyDataManagerImplTest.manager.saveGeolocationProperty(ndGeolocationId, stdVarId, expDesign);
			actualExpDesign = StudyDataManagerImplTest.manager.getGeolocationPropValue(stdVarId, studyId);
			Assert.assertEquals(expDesign, actualExpDesign);
			Assert.assertNotEquals(newExpDesign, actualExpDesign);
		}
	}
}
