/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectPropertyDaoTest extends IntegrationTestBase {

	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String CROSS = "CROSS";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";
	public static final String SITE_SOIL_PH = "SITE_SOIL_PH";
	public static final String SITE_SOIL_PH_TERMID = "9999";
	private DmsProjectDao projectDao;
	private ProjectPropertyDao projectPropDao;
	private CVTermDao cvTermDao;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;
	private DmsProject study;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.cvTermDao = this.daoFactory.getCvTermDao();

		this.projectPropDao = new ProjectPropertyDao();
		this.projectPropDao.setSession(this.sessionProvder.getSession());

		this.projectDao = new DmsProjectDao();
		this.projectDao.setSession(this.sessionProvder.getSession());
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName(RandomStringUtils.randomAlphabetic(20));
			this.study.setDescription(RandomStringUtils.randomAlphabetic(20));
			this.projectDao.save(this.study);
		}

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertyNames() {

		final List<String> propertyNames = new ArrayList<>();
		propertyNames.add(DataSetupTest.LOCATION_NAME);

		final String programUUID = UUID.randomUUID().toString();
		this.createNurseryTestData(programUUID);

		final Map<String, Map<Integer, VariableType>> results =
			this.projectPropDao.getStandardVariableIdsWithTypeByAlias(propertyNames, programUUID);

		Assert.assertTrue(results.get(DataSetupTest.LOCATION_NAME).containsValue(VariableType.ENVIRONMENT_DETAIL));
		Assert.assertTrue(results.get(DataSetupTest.LOCATION_NAME).containsKey(TermId.TRIAL_LOCATION.getId()));
	}

	@Test
	public void testConvertToVariablestandardVariableIdsWithTypeMap() {

		final List<Object[]> objectToConvert = this.createObjectToConvert();

		final Map<String, Map<Integer, VariableType>> results =
			this.projectPropDao.convertToVariablestandardVariableIdsWithTypeMap(objectToConvert);

		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsValue(VariableType.ENVIRONMENT_DETAIL));
		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
		Assert.assertTrue(results.get(ENTRY_NO).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(ENTRY_NO).containsKey(TermId.ENTRY_NO.getId()));
		Assert.assertTrue(results.get(DESIGNATION).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(DESIGNATION).containsKey(TermId.DESIG.getId()));
		Assert.assertTrue(results.get(GID).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(GID).containsKey(TermId.GID.getId()));
		Assert.assertTrue(results.get(CROSS).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(CROSS).containsKey(TermId.CROSS.getId()));
		Assert.assertTrue(results.get(PLOT_NO).containsValue(VariableType.EXPERIMENTAL_DESIGN));
		Assert.assertTrue(results.get(PLOT_NO).containsKey(TermId.PLOT_NO.getId()));
		Assert.assertTrue(results.get(REP_NO).containsValue(VariableType.EXPERIMENTAL_DESIGN));
		Assert.assertTrue(results.get(REP_NO).containsKey(TermId.REP_NO.getId()));
		Assert.assertTrue(results.get(SITE_SOIL_PH).containsValue(VariableType.TRAIT));
		Assert.assertTrue(results.get(SITE_SOIL_PH).containsKey(Integer.valueOf(SITE_SOIL_PH_TERMID)));

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByAliasWhenVariableIsObsolete() {

		final List<String> aliases = Collections.singletonList(DataSetupTest.LOCATION_NAME);

		final String programUUID = UUID.randomUUID().toString();
		this.createNurseryTestData(programUUID);

		// Check first if the location name variable exists in the program
		final Map<String, Map<Integer, VariableType>> results = this.projectPropDao
			.getStandardVariableIdsWithTypeByAlias(aliases, programUUID);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());

		// Then mark the location name variable as obsolete to test if we can still retrieve it
		final CVTerm locationName = this.cvTermDao.getByName(DataSetupTest.LOCATION_NAME);
		locationName.setIsObsolete(true);
		this.cvTermDao.merge(locationName);
		this.sessionProvder.getSession().flush();

		final Map<String, Map<Integer, VariableType>> results2 = this.projectPropDao
			.getStandardVariableIdsWithTypeByAlias(aliases, programUUID);

		// The LOCATION_NAME variable is obsolete so the result should be empty
		Assert.assertTrue(results2.isEmpty());
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByAliasExcludeStudyDetail() {

		final String programUUID = UUID.randomUUID().toString();
		this.createNurseryTestData(programUUID);

		final List<String> aliases = Arrays.asList(DataSetupTest.LOCATION_NAME, DataSetupTest.STUDY_INSTITUTE);

		final Map<String, Map<Integer, VariableType>> results = this.projectPropDao
			.getStandardVariableIdsWithTypeByAlias(aliases, programUUID);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Assert.assertFalse(results.containsKey(DataSetupTest.STUDY_INSTITUTE));
		Assert.assertTrue(results.containsKey(DataSetupTest.LOCATION_NAME));
		Assert.assertEquals(
			VariableType.ENVIRONMENT_DETAIL,
			results.get(DataSetupTest.LOCATION_NAME).entrySet().iterator().next().getValue());

	}

	@Test
	public void testDeleteProjectVariables() {
		final CVTerm trait1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm trait2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait1);
		this.cvTermDao.save(trait2);

		this.saveProjectVariable(this.study, trait1, 1, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(this.study, trait2, 2, VariableType.TRAIT);

		final Integer projectId = this.study.getProjectId();
		Assert.assertEquals(2, this.projectPropDao.getByProjectId(projectId).size());
		this.projectPropDao.deleteProjectVariables(projectId, Arrays.asList(trait1.getCvTermId(), trait2.getCvTermId()));
		Assert.assertTrue(this.projectPropDao.getByProjectId(projectId).isEmpty());
	}

	@Test
	public void testGetGermplasmDescriptors() {
		final DmsProject plotDataset = this.saveDataset(DatasetTypeEnum.PLOT_DATA);
		final CVTerm variable1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable3 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable4 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(variable1);
		this.cvTermDao.save(variable2);
		this.cvTermDao.save(variable3);
		this.cvTermDao.save(variable4);

		this.saveProjectVariable(plotDataset, variable1, 1, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(plotDataset, variable2, 2, VariableType.TRAIT);
		this.saveProjectVariable(plotDataset, variable3, 3, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(plotDataset, variable4, 4, VariableType.EXPERIMENTAL_DESIGN);

		final List<String> germplasmDescriptors = this.projectPropDao.getGermplasmDescriptors(this.study.getProjectId());
		Assert.assertNotNull(germplasmDescriptors);
		Assert.assertTrue(germplasmDescriptors.size() == 2);
		Assert.assertTrue(germplasmDescriptors.contains(variable1.getName()));
		Assert.assertTrue(germplasmDescriptors.contains(variable3.getName()));
	}

	@Test
	public void testGetDesignFactors() {
		final DmsProject plotDataset = this.saveDataset(DatasetTypeEnum.PLOT_DATA);
		final CVTerm variable1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable3 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable4 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable5 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(variable1);
		this.cvTermDao.save(variable2);
		this.cvTermDao.save(variable3);
		this.cvTermDao.save(variable4);
		this.cvTermDao.save(variable5);

		this.saveProjectVariable(plotDataset, variable1, 1, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(plotDataset, variable2, 2, VariableType.TRAIT);
		this.saveProjectVariable(plotDataset, variable3, 3, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(plotDataset, variable4, 4, VariableType.EXPERIMENTAL_DESIGN);
		this.saveProjectVariable(plotDataset, variable5, 5, VariableType.TREATMENT_FACTOR);

		final List<String> designFactors = this.projectPropDao.getDesignFactors(this.study.getProjectId());
		Assert.assertNotNull(designFactors);
		Assert.assertTrue(designFactors.size() == 2);
		Assert.assertTrue(designFactors.contains(variable4.getName()));
		Assert.assertTrue(designFactors.contains(variable5.getName()));
	}

	@Test
	public void testGetVariablesOfSiblingDatasets() {
		final DmsProject plotDataset = this.saveDataset(DatasetTypeEnum.PLOT_DATA);
		final DmsProject trialDataset = this.saveDataset(DatasetTypeEnum.SUMMARY_DATA);
		final CVTerm variable1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable3 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm variable4 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(variable1);
		this.cvTermDao.save(variable2);
		this.cvTermDao.save(variable3);
		this.cvTermDao.save(variable4);

		this.saveProjectVariable(plotDataset, variable1, 1, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(plotDataset, variable2, 2, VariableType.TRAIT);
		this.saveProjectVariable(trialDataset, variable3, 1, VariableType.GERMPLASM_DESCRIPTOR);
		this.saveProjectVariable(trialDataset, variable4, 2, VariableType.EXPERIMENTAL_DESIGN);

		List<Integer> variableIds = this.projectPropDao.getVariablesOfSiblingDatasets(plotDataset.getProjectId());
		Assert.assertNotNull(variableIds);
		Assert.assertFalse(variableIds.isEmpty());
		Assert.assertEquals(Arrays.asList(variable3.getCvTermId(), variable4.getCvTermId()), variableIds);

		variableIds = this.projectPropDao.getVariablesOfSiblingDatasets(trialDataset.getProjectId());
		Assert.assertNotNull(variableIds);
		Assert.assertFalse(variableIds.isEmpty());
		Assert.assertEquals(Arrays.asList(variable1.getCvTermId(), variable2.getCvTermId()), variableIds);
	}

	private DmsProject saveDataset(final DatasetTypeEnum datasetType) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(RandomStringUtils.randomAlphabetic(20));
		dataset.setDescription(RandomStringUtils.randomAlphabetic(20));
		dataset.setParent(this.study);
		dataset.setStudy(this.study);
		dataset.setDatasetType(new DatasetType(datasetType.getId()));
		this.projectDao.save(dataset);

		return dataset;
	}

	private void saveProjectVariable(final DmsProject project, final CVTerm variable, final int rank, final VariableType variableType) {
		final ProjectProperty property1 = new ProjectProperty();
		property1.setAlias(RandomStringUtils.randomAlphabetic(20));
		property1.setRank(rank);
		property1.setTypeId(variableType.getId());
		property1.setProject(project);
		property1.setVariableId(variable.getCvTermId());
		this.projectPropDao.save(property1);
	}

	private List<Object[]> createObjectToConvert() {

		final List<Object[]> objectToConvert = new ArrayList<>();

		objectToConvert.add(new Object[] {TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_FACTOR.getId(), VariableType.ENVIRONMENT_DETAIL.getId()});
		objectToConvert.add(new Object[] {ENTRY_NO, TermId.ENTRY_NO.getId(), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {DESIGNATION, TermId.DESIG.getId(), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {GID, TermId.GID.getId(), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {CROSS, TermId.CROSS.getId(), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {PLOT_NO, TermId.PLOT_NO.getId(), VariableType.EXPERIMENTAL_DESIGN.getId()});
		objectToConvert.add(new Object[] {REP_NO, TermId.REP_NO.getId(), VariableType.EXPERIMENTAL_DESIGN.getId()});
		objectToConvert.add(new Object[] {SITE_SOIL_PH, Integer.valueOf(SITE_SOIL_PH_TERMID), VariableType.TRAIT.getId()});

		return objectToConvert;
	}

	private int createNurseryTestData(final String programUUID) {
		final String cropPrefix = "BJ06";
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, DataSetupTest.GERMPLSM_PREFIX,
				parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(programUUID, gids, cropPrefix);

		return nurseryId;
	}

}
