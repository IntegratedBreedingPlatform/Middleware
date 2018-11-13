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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
	private static DmsProjectDao projectDao;
	private static ProjectPropertyDao projectPropDao;
	private static CVTermDao cvTermDao;

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

	@Before
	public void setUp() throws Exception {
		daoFactory = new DaoFactory(this.sessionProvder);
		cvTermDao = daoFactory.getCvTermDao();

		projectPropDao = new ProjectPropertyDao();
		projectPropDao.setSession(this.sessionProvder.getSession());
		
		projectDao = new DmsProjectDao();
		projectDao.setSession(this.sessionProvder.getSession());
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertyNames() throws Exception {

		final List<String> propertyNames = new ArrayList<String>();
		propertyNames.add(TRIAL_INSTANCE);

		final Map<String, Map<Integer, VariableType>> results = projectPropDao.getStandardVariableIdsWithTypeByAlias(propertyNames);

		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsValue(VariableType.ENVIRONMENT_DETAIL));
		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
	}

	@Test
	public void testConvertToVariablestandardVariableIdsWithTypeMap() {

		final List<Object[]> objectToConvert = this.createObjectToConvert();

		final Map<String, Map<Integer, VariableType>> results = projectPropDao.convertToVariablestandardVariableIdsWithTypeMap(objectToConvert);

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
	public void testGetStandardVariableIdsWithTypeByAliasWhenVariableIsObsolete() throws Exception {

		final List<String> aliases = Arrays.asList(TRIAL_INSTANCE);

		final CVTerm trialInstanceTerm = cvTermDao.getByName(TRIAL_INSTANCE);
		trialInstanceTerm.setIsObsolete(true);
		cvTermDao.saveOrUpdate(trialInstanceTerm);
		this.sessionProvder.getSession().flush();

		final Map<String, Map<Integer, VariableType>> results = projectPropDao.getStandardVariableIdsWithTypeByAlias(aliases);

		// The TRIAL_INSTANCE variable is obsolete so the result should be empty
		Assert.assertTrue(results.isEmpty());
	}
	
	@Test
	public void testGetStandardVariableIdsWithTypeByAliasExcludeStudyDetail() throws Exception {
		// Seed two nurseries: 1)with LOCATION_NAME used as "Study Detail" and 2) with LOCATION_NAME used as "Environment Detail"
		this.createNurseryTestData(true);
		this.createNurseryTestData(false);
		
		final List<String> aliases = Arrays.asList(DataSetupTest.LOCATION_NAME);
		final Map<String, Map<Integer, VariableType>> results = projectPropDao.getStandardVariableIdsWithTypeByAlias(aliases);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		for (final String alias : aliases) {
			final Map<Integer, VariableType> variableMap = results.get(alias);
			for (final VariableType variableType : variableMap.values()) {
				Assert.assertFalse(VariableType.STUDY_DETAIL.equals(variableType));
				if (DataSetupTest.LOCATION_NAME.equals(alias)) {
					Assert.assertTrue(VariableType.ENVIRONMENT_DETAIL.equals(variableType));
				}
			}
		}
	}
	
	@Test
	public void testDeleteProjectVariables() {
		final DmsProject project = new DmsProject();
		project.setName(RandomStringUtils.randomAlphabetic(20));
		project.setDescription(RandomStringUtils.randomAlphabetic(20));
		projectDao.save(project);

		final CVTerm trait1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		final CVTerm trait2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		cvTermDao.save(trait1);
		cvTermDao.save(trait2);
		
		final ProjectProperty property1 = new ProjectProperty();
		property1.setAlias(RandomStringUtils.randomAlphabetic(20));
		property1.setRank(1);
		property1.setTypeId(VariableType.GERMPLASM_DESCRIPTOR.getId());
		property1.setProject(project);
		property1.setVariableId(trait1.getCvTermId());
		projectPropDao.save(property1);
		
		final ProjectProperty property2 = new ProjectProperty();
		property2.setAlias(RandomStringUtils.randomAlphabetic(20));
		property2.setRank(2);
		property2.setTypeId(VariableType.TRAIT.getId());
		property2.setProject(project);
		property2.setVariableId(trait2.getCvTermId());
		projectPropDao.save(property2);
		
		final Integer projectId = project.getProjectId();
		Assert.assertEquals(2, projectPropDao.getByProjectId(projectId).size());
		projectPropDao.deleteProjectVariables(projectId, Arrays.asList(trait1.getCvTermId(), trait2.getCvTermId()));
		Assert.assertTrue(projectPropDao.getByProjectId(projectId).isEmpty());
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
	
	private int createNurseryTestData(final boolean locationIsStudyDetail) {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		final String cropPrefix = "BJ06";
		Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
				.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, DataSetupTest.GERMPLSM_PREFIX,
						parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(programUUID, gids, cropPrefix, locationIsStudyDetail);

		return nurseryId;
	}

}
