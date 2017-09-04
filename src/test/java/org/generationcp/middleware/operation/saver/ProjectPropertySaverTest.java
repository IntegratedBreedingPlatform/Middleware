
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

@Ignore("Disabling temporarily. Please enable once failing tests are fixed.")
public class ProjectPropertySaverTest {

	private static final List<Integer> DATASET_STUDY_TRIAL_IDS = Arrays.asList(8150, 8155, 8160, 8190, 8180,
			TermId.TRIAL_INSTANCE_FACTOR.getId());
	private static final List<Integer> GERMPLASM_PLOT_VARIATE_IDS = Arrays.asList(8230, 8250, 8377, 8240, 8200, 20345, 20325, 20338, 20310,
			20314, 20327, 20307, 8390, 8263, 8255, 8400, 8410);
	private static final List<Integer> VARS_TO_DELETE = Arrays.asList(8377, 8263, 20310);

	private ProjectPropertySaver projectPropSaver;
	private ProjectPropertyDao projectPropDao;

	private int datasetId;
	private List<ProjectProperty> dummyProjectPropIds;

	private static final String propertyName = "Property Name";

	@Before
	public void setup() {
		this.projectPropDao = Mockito.mock(ProjectPropertyDao.class);
		final Saver mockSaver = Mockito.mock(Saver.class);
		Mockito.when(mockSaver.getProjectPropertyDao()).thenReturn(this.projectPropDao);
		this.projectPropSaver = new ProjectPropertySaver(mockSaver);

		this.datasetId = -11;
		this.dummyProjectPropIds = ProjectPropertySaverTest.getDummyProjectPropIds();
	}

	@Test
	public void testUpdateVariablesRanking_AllGermplasmPlotAndVariates() throws MiddlewareQueryException {
		final List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, this.dummyProjectPropIds);
	}

	@Test
	public void testUpdateVariablesRanking_IncludeTrialInstanceVar() throws MiddlewareQueryException {
		final List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		variableIds.add(TermId.TRIAL_INSTANCE_FACTOR.getId());
		Collections.shuffle(variableIds);

		this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, this.dummyProjectPropIds);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateVariablesRanking_NewVariableInDBAndNotInVarListParameter() throws MiddlewareQueryException {
		final List<Integer> variableIds = new ArrayList<>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		// New variables exist in DB but not included in passed in list of variables
		final List<Integer> newVariableIds = Arrays.asList(123, 456, 789);
		final List<ProjectProperty> projectProperties= new ArrayList<>(this.dummyProjectPropIds);
		for (int i = 0; i < newVariableIds.size(); i++) {
			final ProjectProperty projectProperty = new ProjectProperty();
			projectProperty.setVariableId(newVariableIds.get(i));
			projectProperties.add(projectProperty);
		}
		Mockito.doReturn(newVariableIds).when(this.projectPropDao)
				.getDatasetVariableIdsForGivenStoredInIds(Matchers.anyInt(), Matchers.anyList(), Matchers.anyList());

		this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, projectProperties);
		final List<Integer> idsToUpdate = new ArrayList<>(variableIds);
		idsToUpdate.addAll(newVariableIds);
	}

	@Test
	public void testUpdateVariablesRanking_DeletedVariableStillInVarListParameter() throws MiddlewareQueryException {
		final List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		// Variable ID was included in list of variables but actually already deleted from DB
		final List<ProjectProperty> projectProperties= new ArrayList<>(this.dummyProjectPropIds);
		final List<Integer> idsToUpdate = new ArrayList<Integer>(variableIds);
		for (final Integer deletedId : ProjectPropertySaverTest.VARS_TO_DELETE) {
			projectProperties.remove(deletedId);
			idsToUpdate.remove(deletedId);
		}

		this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, projectProperties);
	}

	@Test
	public void testSaveVariableTypeShouldCheckSuppliedVariableTypeFirstThenRole() {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID("UUID");

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
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType, null /* TODO */);
		dmsVariableType.setVariableType(null);

		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 3, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Role", VariableType.STUDY_DETAIL.getId(), dmsProject
				.getProperties().get(0).getTypeId());

		// Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		// role and variable type
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(VariableType.ANALYSIS);
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType, null /* TODO */);
		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 3, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Variable Type", VariableType.ANALYSIS.getId(),
				dmsProject.getProperties().get(0).getTypeId());

		// Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		// null role and variable type
		dmsVariableType.setRole(null);
		dmsVariableType.setVariableType(VariableType.TRAIT);
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType, null /* TODO */);

		Assert.assertEquals("SaveVariableType should add properties to dmsProject as expected", 3, dmsProject.getProperties().size());
		Assert.assertEquals("SaveVariableType Properties are not matching for supplied Variable Type", VariableType.TRAIT.getId(),
				dmsProject.getProperties().get(0).getTypeId());
	}

	@Test
	public void testCreateVariableTypeShouldMapProperties() {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setVariableType(VariableType.ANALYSIS);
		measurementVariable.setRole(PhenotypicType.STUDY);

		final int rank = 0;
		final DMSVariableType dmsVariableType = this.projectPropSaver.createVariableType(measurementVariable, rank);

		final String message = "Create Variable Type for %s not mapped properly with Properties.";

		Assert.assertNotNull(String.format(message, "DMSVariable Type"), dmsVariableType);
		final StandardVariable standardVariable = dmsVariableType.getStandardVariable();
		Assert.assertEquals(String.format(message, "Phenotypic Type"), measurementVariable.getRole(), standardVariable.getPhenotypicType());
		Assert.assertEquals(String.format(message, "Role"), measurementVariable.getRole(), dmsVariableType.getRole());
		Assert.assertEquals(String.format(message, "Type"), measurementVariable.getVariableType(), dmsVariableType.getVariableType());
		Assert.assertEquals(String.format(message, "Rank"), rank, dmsVariableType.getRank());
	}

	/**
	 * Create list of valid project properties from the given dmsproject and dmsvariabletype data.
	 */
	@Test
	public void testCreateProjectProperty(){
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID("UUID");

		VariableTypeList variableTypeList = new VariableTypeList();

		PhenotypicType role = PhenotypicType.STUDY;
		VariableType variableType = VariableType.STUDY_DETAIL;

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(new Random().nextInt(10000));
		standardVariable.setProperty(new Term(new Random().nextInt(1000), ProjectPropertySaverTest.propertyName, "Property Description"));
		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(
				new ArrayList<>(Collections.singletonList(OntologyDataHelper.mapFromPhenotype(role, ProjectPropertySaverTest.propertyName)))));

		DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("Local Name");
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setTreatmentLabel("STUDY");
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);

		dmsVariableType.setStandardVariable(standardVariable);
		variableTypeList.add(dmsVariableType);

		List<ProjectProperty> properties = this.projectPropSaver.create(dmsProject, variableTypeList, null);

		Integer typeId = 0;
		DmsProject project = null;
		for(ProjectProperty projectProperty : properties){
			typeId = projectProperty.getTypeId();
			project = projectProperty.getProject();

			Assert.assertEquals(dmsProject.getProjectId(), project.getProjectId());
			Assert.assertEquals(dmsProject.getName(), project.getName());
			Assert.assertEquals(dmsProject.getDescription(), project.getDescription());
			Assert.assertEquals(dmsProject.getProgramUUID(), project.getProgramUUID());

			if(Objects.equals(typeId, VariableType.STUDY_DETAIL.getId())){
				Assert.assertEquals(dmsVariableType.getLocalName(), projectProperty.getValue());
			} else if(Objects.equals(typeId, TermId.MULTIFACTORIAL_INFO.getId())){
				Assert.assertEquals(dmsVariableType.getTreatmentLabel(), projectProperty.getValue());
			}
		}
	}

	/**
	 * Create list of valid project properties from the given dmsproject and dmsvariabletype data with TRAIT variable type.
	 */
	@Test
	public void testCreateProjectPropertyWithTraitVariableType(){
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID("UUID");

		VariableTypeList variableTypeList = new VariableTypeList();

		PhenotypicType role = PhenotypicType.VARIATE;
		VariableType variableType = VariableType.TRAIT;

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(new Random().nextInt(10000));
		standardVariable.setProperty(new Term(new Random().nextInt(1000), ProjectPropertySaverTest.propertyName, "Property Description"));
		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(new ArrayList<>(
				Collections.singletonList(OntologyDataHelper.mapFromPhenotype(role, ProjectPropertySaverTest.propertyName)))));

		DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("Local Name");
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setTreatmentLabel("STUDY");
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);

		dmsVariableType.setStandardVariable(standardVariable);
		variableTypeList.add(dmsVariableType);

		List<ProjectProperty> properties = this.projectPropSaver.create(dmsProject, variableTypeList, null);

		Integer typeId = 0;
		DmsProject project = null;
		for(ProjectProperty projectProperty : properties){
			typeId = projectProperty.getTypeId();
			project = projectProperty.getProject();

			Assert.assertEquals(dmsProject.getProjectId(), project.getProjectId());
			Assert.assertEquals(dmsProject.getName(), project.getName());
			Assert.assertEquals(dmsProject.getDescription(), project.getDescription());
			Assert.assertEquals(dmsProject.getProgramUUID(), project.getProgramUUID());

			if(Objects.equals(typeId, VariableType.TRAIT.getId())){
				Assert.assertEquals(dmsVariableType.getLocalName(), projectProperty.getValue());
			} else if(Objects.equals(typeId, TermId.MULTIFACTORIAL_INFO.getId())){
				Assert.assertEquals(dmsVariableType.getTreatmentLabel(), projectProperty.getValue());
			}
		}
	}

	/**
	 * This test will expect RuntimeException as not valid role is provided, for that no valid variable type is exist.
	 */
	@Test(expected = RuntimeException.class)
	public void testCreateProjectPropertyThrowRuntimeException(){
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID("UUID");

		VariableTypeList variableTypeList = new VariableTypeList();

		PhenotypicType role = PhenotypicType.UNASSIGNED;

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(new Random().nextInt(10000));
		standardVariable.setProperty(new Term(new Random().nextInt(1000), ProjectPropertySaverTest.propertyName, "Property Description"));
		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(
				new ArrayList<>(Collections.singletonList(OntologyDataHelper.mapFromPhenotype(role, ProjectPropertySaverTest.propertyName)))));

		DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("Local Name");
		dmsVariableType.setLocalDescription("Local Description");
		dmsVariableType.setRank(1);
		dmsVariableType.setTreatmentLabel("STUDY");
		dmsVariableType.setRole(role);

		dmsVariableType.setStandardVariable(standardVariable);
		variableTypeList.add(dmsVariableType);

		this.projectPropSaver.create(dmsProject, variableTypeList, null);
	}

	private static List<ProjectProperty> getDummyProjectPropIds() {
		final List<Integer> allVariableIds = new ArrayList<>();
		allVariableIds.addAll(ProjectPropertySaverTest.DATASET_STUDY_TRIAL_IDS);
		allVariableIds.addAll(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);

		final List<ProjectProperty> projectProperties = new ArrayList<>();

		for (final Integer i: allVariableIds) {
			ProjectProperty projectProperty = new ProjectProperty();
			projectProperty.setVariableId(i);
			projectProperties.add(projectProperty);
		}

		return projectProperties;
	}

	private int callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(final List<Integer> variableIds,
			final List<ProjectProperty> projectProperties) throws MiddlewareQueryException {
		final int startRank = projectProperties.size() + 1;
		Mockito.doReturn(startRank).when(this.projectPropDao).getNextRank(this.datasetId);
		Mockito.doReturn(projectProperties).when(this.projectPropDao).getByProjectId(this.datasetId);

		this.projectPropSaver.updateVariablesRanking(this.datasetId, variableIds);

		return startRank;
	}

	@Test
	public void testCreateOfProjectProperties() {
		final List<PhenotypicType> testVarRoles =
				Arrays.asList(PhenotypicType.STUDY, PhenotypicType.DATASET, PhenotypicType.TRIAL_ENVIRONMENT, PhenotypicType.GERMPLASM,
						PhenotypicType.TRIAL_DESIGN, PhenotypicType.TRIAL_DESIGN, PhenotypicType.VARIATE, null, PhenotypicType.VARIATE);
		final List<VariableType> testVarVariableTypes =
				Arrays.asList(null, VariableType.STUDY_DETAIL, null, VariableType.GERMPLASM_DESCRIPTOR, VariableType.EXPERIMENTAL_DESIGN,
						VariableType.TREATMENT_FACTOR, VariableType.NURSERY_CONDITION, VariableType.TRIAL_CONDITION, null);
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		final VariableTypeList variableTypeList = this.createVariableTypeListTestData(testVarRoles, testVarVariableTypes);
		final List<ProjectProperty> projectProperties = this.projectPropSaver.create(dmsProject, variableTypeList, null);
		Assert.assertNotNull(projectProperties);
		// a project property record is created for each variable for its name, description, ontology variable and treatment label if
		// available
		final int expectedNumberOfProjectProperties = variableTypeList.size() * 3 + 1;
		Assert.assertEquals("The number of project properties should be " + expectedNumberOfProjectProperties,
				expectedNumberOfProjectProperties, projectProperties.size());
		int i = 0;
		final Iterator<ProjectProperty> projectPropIterator = projectProperties.iterator();
		while (projectPropIterator.hasNext()) {
			final DMSVariableType dmsVariableType = variableTypeList.getVariableTypes().get(i);
			// verify name projectprop record
			final ProjectProperty projectPropertyName = projectPropIterator.next();
			Assert.assertEquals("The name should be " + dmsVariableType.getLocalName(), dmsVariableType.getLocalName(),
					projectPropertyName.getValue());
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

			// verify description projectprop record
			final ProjectProperty projectPropertyDesc = projectPropIterator.next();
			Assert.assertEquals("The description should be " + dmsVariableType.getLocalDescription(),
					dmsVariableType.getLocalDescription(), projectPropertyDesc.getValue());
			Assert.assertEquals("The project id should " + dmsProject.getProjectId(), dmsProject.getProjectId(), projectPropertyDesc
					.getProject().getProjectId());
			Assert.assertEquals("The rank should " + dmsVariableType.getRank(), dmsVariableType.getRank(), projectPropertyDesc.getRank()
					.intValue());

			// verify ontology variable projectprop record
			final ProjectProperty projectPropertyOntologyVar = projectPropIterator.next();
			Assert.assertEquals("The ontology variable should be " + String.valueOf(dmsVariableType.getId()),
					String.valueOf(dmsVariableType.getId()), projectPropertyOntologyVar.getValue());
			Assert.assertEquals("The project id should " + dmsProject.getProjectId(), dmsProject.getProjectId(), projectPropertyOntologyVar
					.getProject().getProjectId());
				Assert.assertEquals("The rank should " + dmsVariableType.getRank(), dmsVariableType.getRank(), projectPropertyOntologyVar
					.getRank().intValue());

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

	private VariableTypeList createVariableTypeListTestData(final List<PhenotypicType> testVarRoles,
			final List<VariableType> testVarVariableTypes) {
		final VariableTypeList variableTypeList = new VariableTypeList();
		for (int i = 0; i < testVarRoles.size(); i++) {
			final int rank = i + 1;
			variableTypeList.add(this.createDMSVariableType("VAR-NAME" + rank, "VAR-DESC-" + rank, rank, testVarRoles.get(i),
					testVarVariableTypes.get(i)));
		}
		return variableTypeList;
	}

	private DMSVariableType createDMSVariableType(final String localName, final String localDescription, final int rank,
			final PhenotypicType role, final VariableType variableType) {
		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName(localName);
		dmsVariableType.setLocalDescription(localDescription);
		dmsVariableType.setRank(rank);
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);
		if (variableType != null && variableType.getId() == VariableType.TREATMENT_FACTOR.getId()) {
			dmsVariableType.setTreatmentLabel("TEST TREATMENT LABEL");
		}
		dmsVariableType.setStandardVariable(this.createStandardVariable(rank));
		return dmsVariableType;
	}

	private StandardVariable createStandardVariable(final int id) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(id);
		standardVariable.setProperty(new Term(new Random().nextInt(1000), ProjectPropertySaverTest.propertyName, "Property Description"));
		return standardVariable;
	}
}
