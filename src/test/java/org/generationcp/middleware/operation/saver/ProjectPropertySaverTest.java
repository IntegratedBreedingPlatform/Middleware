
package org.generationcp.middleware.operation.saver;

import com.google.common.base.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class ProjectPropertySaverTest extends IntegrationTestBase {

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private StudyDataManagerImpl studyDataManager;

	private ProjectPropertySaver projectPropertySaver;

	private ProjectPropertyDao projectPropertyDao;

	private DmsProjectDao dmsProjectDao;

	private CVTermDao cvTermDao;

	private StandardVariableSaver standardVariableSaver;

	private static final String propertyName = "Property Name";

	private Project commonTestProject;
	private StudyReference studyReference;

	private StudyTestDataInitializer studyTDI;

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setup() throws Exception {
		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.standardVariableSaver = new StandardVariableSaver(this.sessionProvder);
		this.projectPropertySaver = new ProjectPropertySaver(this.sessionProvder);
		this.projectPropertyDao = new ProjectPropertyDao(this.sessionProvder.getSession());
		this.dmsProjectDao = new DmsProjectDao(this.sessionProvder.getSession());
		this.cvTermDao = new CVTermDao(this.sessionProvder.getSession());

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (workbenchDaoFactory == null) {
			workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
		}

		this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);

		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		this.studyTDI =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject,
				this.sessionProvder);
		this.studyReference = this.studyTDI.addTestStudy();
	}

	@Test
	public void testUpdateVariablesRanking() {

		final DmsProject dmsProject = this.createDMSProject();

		final VariableTypeList variableTypeList = this.createVariableTypeListVariatesTestData();

		this.projectPropertySaver.saveProjectProperties(dmsProject, variableTypeList, null);

		final List<Integer> variableIds = new LinkedList<>();
		for (final DMSVariableType dmsVariableType : variableTypeList.getVariableTypes()) {
			variableIds.add(dmsVariableType.getId());
		}

		Collections.shuffle(variableIds);

		this.projectPropertySaver.updateVariablesRanking(dmsProject.getProjectId(), variableIds);

		final List<ProjectProperty> projectProperties = projectPropertyDao.getByProjectId(dmsProject.getProjectId());

		final List<Integer> rankedVariableIds = new LinkedList<>();
		for (final ProjectProperty projectProperty : projectProperties) {
			rankedVariableIds.add(projectProperty.getVariableId());
		}

		// Check if the shuffled variableIds and ranked variable ids contain the same elements in the same order.
		Assert.assertTrue(variableIds.equals(rankedVariableIds));

	}

	@Test
	public void testUpdateVariablesRankingAddGermplasmAndExperimentalDesign() {
		final DmsProject dmsProject = this.createDMSProject();

		final VariableTypeList variableTypeList = this.createVariableTypeListVariatesTestData();

		this.projectPropertySaver.saveProjectProperties(dmsProject, variableTypeList, null);

		final List<Integer> variableIds = new LinkedList<>();
		for (final DMSVariableType dmsVariableType : variableTypeList.getVariableTypes()) {
			variableIds.add(dmsVariableType.getId());
		}

		this.projectPropertySaver.updateVariablesRanking(dmsProject.getProjectId(), variableIds);

		final List<ProjectProperty> projectProperties = projectPropertyDao.getByProjectId(dmsProject.getProjectId());

		final List<Integer> rankedVariableIds = new LinkedList<>();
		for (final ProjectProperty projectProperty : projectProperties) {
			rankedVariableIds.add(projectProperty.getVariableId());
		}

		// Check if the shuffled variableIds and ranked variable ids contain the same elements in the same order.
		Assert.assertTrue(variableIds.equals(rankedVariableIds));

		final VariableTypeList germplasmAndExperimentalDesignVariableTypeList = new VariableTypeList();
		germplasmAndExperimentalDesignVariableTypeList
			.add(this.createDMSVariableType("VAR-NAME99", "VAR-DESC99", 99, PhenotypicType.GERMPLASM, VariableType.GERMPLASM_DESCRIPTOR));
		germplasmAndExperimentalDesignVariableTypeList
			.add(this.createDMSVariableType("VAR-NAME100", "VAR-DESC100", 100, PhenotypicType.TRIAL_DESIGN,
				VariableType.EXPERIMENTAL_DESIGN));

		// Add Germplasm And ExperimentalDesign variable
		this.projectPropertySaver.saveProjectProperties(dmsProject, germplasmAndExperimentalDesignVariableTypeList, null);

		final List<Integer> germplasmAndExperimentalDesignVariableIds = new LinkedList<>();
		for (final DMSVariableType dmsVariableType : germplasmAndExperimentalDesignVariableTypeList.getVariableTypes()) {
			germplasmAndExperimentalDesignVariableIds.add(dmsVariableType.getId());
		}

		this.projectPropertySaver.updateVariablesRanking(dmsProject.getProjectId(), germplasmAndExperimentalDesignVariableIds);

		final List<ProjectProperty> result = projectPropertyDao.getByProjectId(dmsProject.getProjectId());

		final Optional<ProjectProperty> addedGermplasmProjectProperty =
			getProjectPropertyByVariableId(germplasmAndExperimentalDesignVariableIds.get(0), result);
		final Optional<ProjectProperty> addedExperimentalDesignProjectProperty =
			getProjectPropertyByVariableId(germplasmAndExperimentalDesignVariableIds.get(1), result);

		// if any factors were added but not included in list of variables, update their ranks also so they come last
		Assert.assertEquals(101, addedGermplasmProjectProperty.get().getRank().intValue());
		Assert.assertEquals(102, addedExperimentalDesignProjectProperty.get().getRank().intValue());

	}

	@Test
	public void testCreateVariableProperties() {
		final DmsProject dmsProject = this.createDMSProject();

		final DMSVariableType variableType = DMSVariableTypeTestDataInitializer.createDmsVariableType("NFERT_NO", "localDescription", 1);
		variableType.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		variableType.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariable(8241, "NFERT_NO"));
		final VariableList variableList = new VariableList();

		final List<ProjectProperty> projectProperties =
			this.projectPropertySaver.createVariableProperties(dmsProject, variableType, variableList);

		Assert.assertEquals(1, projectProperties.size());
		final ProjectProperty projectProperty = projectProperties.get(0);
		Assert.assertEquals(dmsProject.getName(), projectProperty.getProject().getName());
		Assert.assertEquals(variableType.getVariableType().getId(), projectProperty.getTypeId());
		Assert.assertEquals("1", projectProperty.getRank().toString());
		Assert.assertEquals(String.valueOf(variableType.getStandardVariable().getId()), projectProperty.getVariableId().toString());
		Assert.assertEquals(variableType.getLocalName(), projectProperty.getAlias());
	}

	@Test
	public void testCreateVariablePropertiesForTreatMentFactor() {
		final DmsProject dmsProject = this.createDMSProject();
		final DMSVariableType variableType = DMSVariableTypeTestDataInitializer.createDmsVariableType("NFERT_NO", "localDescription", 1);
		variableType.setVariableType(VariableType.TREATMENT_FACTOR);
		variableType.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariable(8241, variableType.getLocalName()));
		variableType.setTreatmentLabel("NFert_kg_ha");

		final VariableList variableList = new VariableList();
		final Variable variable = new Variable();
		variable.setValue("1");
		variable.setVariableType(variableType);
		variableList.add(variable);

		final List<ProjectProperty> projectProperties =
			this.projectPropertySaver.createVariableProperties(dmsProject, variableType, variableList);

		Assert.assertEquals(2, projectProperties.size());
		final ProjectProperty projectProperty = projectProperties.get(0);
		Assert.assertEquals(dmsProject.getName(), projectProperty.getProject().getName());
		Assert.assertEquals(variableType.getVariableType().getId(), projectProperty.getTypeId());
		Assert.assertEquals("1", projectProperty.getRank().toString());
		Assert.assertEquals(String.valueOf(variableType.getStandardVariable().getId()), projectProperty.getVariableId().toString());
		Assert.assertEquals(variableType.getLocalName(), projectProperty.getAlias());
		Assert.assertEquals("1", projectProperty.getValue());

		final ProjectProperty multiFactorialProperty = projectProperties.get(1);
		Assert.assertEquals(dmsProject.getName(), multiFactorialProperty.getProject().getName());
		Assert.assertEquals(String.valueOf(TermId.MULTIFACTORIAL_INFO.getId()), multiFactorialProperty.getTypeId().toString());
		Assert.assertEquals("1", multiFactorialProperty.getRank().toString());
		Assert.assertEquals(String.valueOf(variableType.getStandardVariable().getId()), multiFactorialProperty.getVariableId().toString());
		Assert.assertEquals(variableType.getLocalName(), multiFactorialProperty.getAlias());
		Assert.assertEquals(variableType.getTreatmentLabel(), multiFactorialProperty.getValue());
	}

	@Test
	public void testSaveVariableTypeShouldCheckSuppliedVariableTypeFirstThenRole() {
		final DmsProject dmsProject = this.createDMSProject();

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("DMSName");
		dmsVariableType.setLocalDescription("DMSDescription");
		dmsVariableType.setRank(1);

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(1);
		standardVariable.setName("Name");
		standardVariable.setDescription("Description");
		standardVariable.setPhenotypicType(PhenotypicType.STUDY);
		standardVariable.setProperty(new Term(1, "Property Name", "Property Description"));

		dmsVariableType.setStandardVariable(standardVariable);

		//role and null variable type
		dmsVariableType.setRole(PhenotypicType.STUDY);
		this.projectPropertySaver.saveVariableType(dmsProject, dmsVariableType, null);
		dmsVariableType.setVariableType(null);

		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 1, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Role", VariableType.STUDY_DETAIL.getId(), dmsProject
			.getProperties().get(0).getTypeId());

		// Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		// role and variable type
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(VariableType.ANALYSIS);
		this.projectPropertySaver.saveVariableType(dmsProject, dmsVariableType, null);
		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 1, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Variable Type", VariableType.ANALYSIS.getId(),
			dmsProject.getProperties().get(0).getTypeId());

		// Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		// null role and variable type
		dmsVariableType.setRole(null);
		dmsVariableType.setVariableType(VariableType.TRAIT);
		this.projectPropertySaver.saveVariableType(dmsProject, dmsVariableType, null);

		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 1, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Variable Type", VariableType.TRAIT.getId(),
			dmsProject.getProperties().get(0).getTypeId());
	}

	@Test
	public void testDeleteVariable() {
		DmsProject project = this.dmsProjectDao.getById(studyReference.getId());
		final MeasurementVariable mvar =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name(), "1");
		this.projectPropertySaver.insertVariable(project, mvar, 1);
		project = this.dmsProjectDao.getById(studyReference.getId());
		Assert.assertEquals(1, project.getProperties().size());
		this.projectPropertySaver.deleteVariable(project, mvar.getTermId());
		Assert.assertEquals(0, project.getProperties().size());
	}

	@Test
	public void testDeleteVariableWhereMultipleVariablesAreNeededTobeDeleted() {
		DmsProject project = this.dmsProjectDao.getById(studyReference.getId());
		MeasurementVariable mvar =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name(), "1");
		this.projectPropertySaver.insertVariable(project, mvar, 1);
		mvar = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name(), "2");
		this.projectPropertySaver.insertVariable(project, mvar, 2);
		project = this.dmsProjectDao.getById(studyReference.getId());
		Assert.assertEquals(2, project.getProperties().size());
		this.projectPropertySaver.deleteVariable(project, mvar.getTermId());
		Assert.assertEquals(0, project.getProperties().size());
	}

	@Test
	public void testCreateVariableTypeShouldMapProperties() {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setVariableType(VariableType.ANALYSIS);
		measurementVariable.setRole(PhenotypicType.STUDY);

		final int rank = 0;
		final DMSVariableType dmsVariableType = this.projectPropertySaver.createVariableType(measurementVariable, rank);

		final String message = "Create Variable Type for %s not mapped properly with Properties.";

		Assert.assertNotNull(String.format(message, "DMSVariable Type"), dmsVariableType);
		final StandardVariable standardVariable = dmsVariableType.getStandardVariable();
		Assert.assertEquals(String.format(message, "Phenotypic Type"), measurementVariable.getRole(), standardVariable.getPhenotypicType());
		Assert.assertEquals(String.format(message, "Role"), measurementVariable.getRole(), dmsVariableType.getRole());
		Assert.assertEquals(String.format(message, "Type"), measurementVariable.getVariableType(), dmsVariableType.getVariableType());
		Assert.assertEquals(String.format(message, "Rank"), rank, dmsVariableType.getRank());
	}

	@Test
	public void testCreateProjectProperty() {

		final DmsProject dmsProject = this.createDMSProject();

		final String variableName = "Local Name";
		final StandardVariable standardVariable = this.createStandardVariable(variableName);
		standardVariable.setPhenotypicType(PhenotypicType.STUDY);
		standardVariable.setVariableTypes(new HashSet<>(
			new ArrayList<>(Collections
				.singletonList(OntologyDataHelper.mapFromPhenotype(PhenotypicType.STUDY, ProjectPropertySaverTest.propertyName)))));
		this.standardVariableSaver.save(standardVariable);

		final DMSVariableType dmsVariableType = new DMSVariableType();

		dmsVariableType.setLocalName(variableName);
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(VariableType.STUDY_DETAIL);
		dmsVariableType.setStandardVariable(standardVariable);

		final VariableTypeList variableTypeList = new VariableTypeList();
		variableTypeList.add(dmsVariableType);
		final List<ProjectProperty> properties = this.projectPropertySaver.create(dmsProject, variableTypeList, null);

		Assert.assertEquals(1, properties.size());
		final ProjectProperty createdProjectProperty = properties.get(0);
		final DmsProject project = createdProjectProperty.getProject();
		Assert.assertEquals(dmsProject.getProjectId(), project.getProjectId());
		Assert.assertEquals(dmsProject.getName(), project.getName());
		Assert.assertEquals(dmsProject.getDescription(), project.getDescription());
		Assert.assertEquals(dmsProject.getProgramUUID(), project.getProgramUUID());
		Assert.assertEquals(dmsVariableType.getLocalName(), createdProjectProperty.getAlias());
		Assert.assertNull(createdProjectProperty.getValue());
		Assert.assertEquals(1, createdProjectProperty.getRank().intValue());

	}

	@Test
	public void testCreateProjectPropertyVariableTypeHasTreatmentLabel() {

		final DmsProject dmsProject = this.createDMSProject();

		final String variableName = "Local Name";
		final StandardVariable standardVariable = this.createStandardVariable(variableName);
		standardVariable.setPhenotypicType(PhenotypicType.STUDY);
		standardVariable.setVariableTypes(new HashSet<>(
			new ArrayList<>(Collections
				.singletonList(OntologyDataHelper.mapFromPhenotype(PhenotypicType.STUDY, ProjectPropertySaverTest.propertyName)))));

		final DMSVariableType dmsVariableType = new DMSVariableType();

		dmsVariableType.setLocalName(variableName);
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(VariableType.STUDY_DETAIL);
		dmsVariableType.setStandardVariable(standardVariable);
		dmsVariableType.setTreatmentLabel("STUDY");

		final VariableTypeList variableTypeList = new VariableTypeList();
		variableTypeList.add(dmsVariableType);
		final List<ProjectProperty> properties = this.projectPropertySaver.create(dmsProject, variableTypeList, null);

		Assert.assertEquals(2, properties.size());

		final ProjectProperty firstItemProjectProperty = properties.get(0);
		Assert.assertNull(firstItemProjectProperty.getValue());
		Assert.assertNotNull(firstItemProjectProperty.getProject());
		Assert.assertEquals(1, firstItemProjectProperty.getRank().intValue());
		Assert.assertEquals(VariableType.STUDY_DETAIL.getId(), firstItemProjectProperty.getTypeId());

		final ProjectProperty secondItemProjectProperty = properties.get(1);
		Assert.assertNotNull(secondItemProjectProperty.getProject());
		Assert.assertEquals("STUDY", secondItemProjectProperty.getValue());
		Assert.assertEquals(1, secondItemProjectProperty.getRank().intValue());
		Assert.assertEquals(TermId.MULTIFACTORIAL_INFO.getId(), secondItemProjectProperty.getTypeId().intValue());

	}

	/**
	 * Create list of valid project properties from the given dmsproject and dmsvariabletype data with TRAIT variable type.
	 */
	@Test
	public void testCreateProjectPropertyWithTraitVariableType() {
		final DmsProject dmsProject = this.createDMSProject();

		final String variableName = "Local Name";
		final StandardVariable standardVariable = this.createStandardVariable(variableName);
		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);
		standardVariable.setVariableTypes(new HashSet<>(new ArrayList<>(
			Collections
				.singletonList(OntologyDataHelper.mapFromPhenotype(PhenotypicType.VARIATE, ProjectPropertySaverTest.propertyName)))));

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName(variableName);
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setTreatmentLabel("STUDY");
		dmsVariableType.setRole(PhenotypicType.VARIATE);
		dmsVariableType.setVariableType(VariableType.TRAIT);
		dmsVariableType.setStandardVariable(standardVariable);

		final VariableTypeList variableTypeList = new VariableTypeList();
		variableTypeList.add(dmsVariableType);

		final List<ProjectProperty> properties = this.projectPropertySaver.create(dmsProject, variableTypeList, null);

		final ProjectProperty firstItemProjectProperty = properties.get(0);
		Assert.assertNull(firstItemProjectProperty.getValue());
		Assert.assertNotNull(firstItemProjectProperty.getProject());
		Assert.assertEquals(1, firstItemProjectProperty.getRank().intValue());
		Assert.assertEquals(VariableType.TRAIT.getId(), firstItemProjectProperty.getTypeId());

		final ProjectProperty secondItemProjectProperty = properties.get(1);
		Assert.assertNotNull(secondItemProjectProperty.getProject());
		Assert.assertEquals("STUDY", secondItemProjectProperty.getValue());
		Assert.assertEquals(1, secondItemProjectProperty.getRank().intValue());
		Assert.assertEquals(TermId.MULTIFACTORIAL_INFO.getId(), secondItemProjectProperty.getTypeId().intValue());
	}

	/**
	 * This test will expect RuntimeException as not valid role is provided, for that no valid variable type is exist.
	 */
	@Test(expected = RuntimeException.class)
	public void testCreateProjectPropertyThrowRuntimeException() {
		final DmsProject dmsProject = this.createDMSProject();

		final VariableTypeList variableTypeList = new VariableTypeList();

		final PhenotypicType role = PhenotypicType.UNASSIGNED;

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(new Random().nextInt(10000));
		standardVariable.setProperty(new Term(new Random().nextInt(1000), ProjectPropertySaverTest.propertyName, "Property Description"));
		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(
			new ArrayList<>(Collections.singletonList(OntologyDataHelper.mapFromPhenotype(role, ProjectPropertySaverTest.propertyName)))));

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("Local Name");
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setTreatmentLabel("STUDY");
		dmsVariableType.setRole(role);

		dmsVariableType.setStandardVariable(standardVariable);
		variableTypeList.add(dmsVariableType);

		this.projectPropertySaver.create(dmsProject, variableTypeList, null);
	}

	@Test
	public void testCreateOfProjectProperties() {

		final List<PhenotypicType> testVarRoles =
			Arrays.asList(PhenotypicType.STUDY, PhenotypicType.DATASET, PhenotypicType.TRIAL_ENVIRONMENT, PhenotypicType.GERMPLASM,
				PhenotypicType.TRIAL_DESIGN, PhenotypicType.TRIAL_DESIGN, PhenotypicType.VARIATE, null, PhenotypicType.VARIATE);
		final List<VariableType> testVarVariableTypes =
			Arrays.asList(null, VariableType.STUDY_DETAIL, null, VariableType.GERMPLASM_DESCRIPTOR, VariableType.EXPERIMENTAL_DESIGN,
				VariableType.TREATMENT_FACTOR, VariableType.ENVIRONMENT_CONDITION, VariableType.ENVIRONMENT_CONDITION, null);

		final DmsProject dmsProject = this.createDMSProject();

		final VariableTypeList variableTypeList = this.createVariableTypeListTestData(testVarRoles, testVarVariableTypes);

		final List<ProjectProperty> projectProperties = this.projectPropertySaver.create(dmsProject, variableTypeList, null);
		Assert.assertNotNull(projectProperties);

		final int expectedNumberOfProjectProperties = variableTypeList.size() + 1;
		Assert.assertEquals("The number of project properties should be " + expectedNumberOfProjectProperties,
			expectedNumberOfProjectProperties, projectProperties.size());
		int i = 0;
		final Iterator<ProjectProperty> projectPropIterator = projectProperties.iterator();
		while (projectPropIterator.hasNext()) {
			final DMSVariableType dmsVariableType = variableTypeList.getVariableTypes().get(i);
			// verify name projectprop record
			final ProjectProperty projectPropertyName = projectPropIterator.next();
			Assert.assertEquals("The name should be " + dmsVariableType.getLocalName(), dmsVariableType.getLocalName(),
				projectPropertyName.getAlias());
			Assert.assertEquals("The project id should be " + dmsProject.getProjectId(), dmsProject.getProjectId(), projectPropertyName
				.getProject().getProjectId());
			final VariableType variableType = testVarVariableTypes.get(i);
			final PhenotypicType role = testVarRoles.get(i);
			if (variableType != null) {
				Assert.assertEquals("The variable type id must be " + variableType.getId(), variableType.getId(),
					projectPropertyName.getTypeId());
			} else {
				final VariableType defaultVariableType =
					new StandardVariableBuilder(null).mapPhenotypicTypeToDefaultVariableType(role, false);
				Assert.assertEquals("The variable type id must be " + defaultVariableType.getId(), defaultVariableType.getId(),
					projectPropertyName.getTypeId());
			}
			Assert.assertEquals("The rank should " + dmsVariableType.getRank(), dmsVariableType.getRank(), projectPropertyName.getRank()
				.intValue());

			if (dmsVariableType.getTreatmentLabel() != null && !"".equals(dmsVariableType.getTreatmentLabel())) {
				// verify treatment label projectprop record
				final ProjectProperty projectPropertyTreatmentLabel = projectPropIterator.next();
				Assert.assertEquals("The treatment label should be " + dmsVariableType.getTreatmentLabel(),
					dmsVariableType.getTreatmentLabel(), projectPropertyTreatmentLabel.getValue());
				Assert.assertEquals("The project id should " + dmsProject.getProjectId(), dmsProject.getProjectId(),
					projectPropertyTreatmentLabel.getProject().getProjectId());
				Assert.assertEquals("The type id must be " + TermId.MULTIFACTORIAL_INFO.getId(), TermId.MULTIFACTORIAL_INFO.getId(),
					projectPropertyTreatmentLabel.getTypeId().intValue());
				Assert.assertEquals("The rank should " + dmsVariableType.getRank(), dmsVariableType.getRank(),
					projectPropertyTreatmentLabel.getRank().intValue());
			}
			i++;
		}
	}

	private DmsProject createDMSProject() {

		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID(UUID.randomUUID().toString());

		return dmsProject;
	}

	private VariableTypeList createVariableTypeListVariatesTestData() {
		final VariableTypeList variableTypeList = new VariableTypeList();

		variableTypeList.add(this.createDMSVariableType("VAR-NAME3", "VAR-DESC-3", 3, PhenotypicType.VARIATE, VariableType.TRAIT));
		variableTypeList.add(this.createDMSVariableType("VAR-NAME4", "VAR-DESC-4", 4, PhenotypicType.VARIATE, VariableType.TRAIT));
		variableTypeList.add(this.createDMSVariableType("VAR-NAME5", "VAR-DESC-5", 5, PhenotypicType.VARIATE, VariableType.TRAIT));
		variableTypeList.add(this.createDMSVariableType("VAR-NAME6", "VAR-DESC-6", 6, PhenotypicType.VARIATE, VariableType.TRAIT));
		variableTypeList.add(this.createDMSVariableType("VAR-NAME7", "VAR-DESC-7", 7, PhenotypicType.VARIATE, VariableType.TRAIT));
		variableTypeList.add(this.createDMSVariableType("VAR-NAME8", "VAR-DESC-8", 8, PhenotypicType.VARIATE, VariableType.TRAIT));

		return variableTypeList;
	}

	private VariableTypeList createVariableTypeListTestData(
		final List<PhenotypicType> testVarRoles,
		final List<VariableType> testVarVariableTypes) {
		final VariableTypeList variableTypeList = new VariableTypeList();
		for (int i = 0; i < testVarRoles.size(); i++) {
			final int rank = i + 1;
			variableTypeList.add(this.createDMSVariableType("VAR-NAME" + rank, "VAR-DESC-" + rank, rank, testVarRoles.get(i),
				testVarVariableTypes.get(i)));
		}
		return variableTypeList;
	}

	private DMSVariableType createDMSVariableType(
		final String localName, final String localDescription, final int rank,
		final PhenotypicType role, final VariableType variableType) {
		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName(localName);
		dmsVariableType.setLocalDescription(localDescription);
		dmsVariableType.setRank(rank);
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);
		if (variableType != null && variableType.getId().intValue() == VariableType.TREATMENT_FACTOR.getId()) {
			dmsVariableType.setTreatmentLabel("TEST TREATMENT LABEL");
		}
		dmsVariableType.setStandardVariable(this.createStandardVariable(localName));
		return dmsVariableType;
	}

	private StandardVariable createStandardVariable(final String name) {

		final CVTerm property = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		final CVTerm method = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setProperty(new Term(property.getCvTermId(), property.getName(), property.getDefinition()));
		standardVariable.setScale(new Term(scale.getCvTermId(), scale.getName(), scale.getDefinition()));
		standardVariable.setMethod(new Term(method.getCvTermId(), method.getName(), method.getDefinition()));
		standardVariable.setDataType(new Term(DataType.CHARACTER_VARIABLE.getId(), "Character variable", "variable with char values"));
		standardVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));

		this.standardVariableSaver.save(standardVariable);

		return standardVariable;
	}

	private Optional<ProjectProperty> getProjectPropertyByVariableId(
		final int standardVariableId, final List<ProjectProperty> projectProperties) {
		for (final ProjectProperty projectProperty : projectProperties) {
			if (projectProperty.getVariableId() == standardVariableId) {
				return Optional.of(projectProperty);
			}
		}

		return Optional.absent();
	}
}
