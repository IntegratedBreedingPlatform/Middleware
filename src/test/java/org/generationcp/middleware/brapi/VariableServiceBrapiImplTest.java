package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.StudyServiceBrapi;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.VariableServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeSearchRequestDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.CvTermExternalReference;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableServiceBrapiImplTest extends IntegrationTestBase {

	@Autowired
	private VariableServiceBrapi variableServiceBrapi;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private TrialServiceBrapi trialServiceBrapi;

	@Resource
	private StudyServiceBrapi studyServiceBrapi;

	private IntegrationTestDataInitializer testDataInitializer;

	private DaoFactory daoFactory;
	private WorkbenchUser testUser;
	private Project commonTestProject;
	private CropType cropType;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.testUser = this.testDataInitializer.createUserForTesting();
		this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());
	}

	@Test
	public void testCountObservationVariables() {
		final List<String> possibleValues = new ArrayList<>();
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		final CVTerm categoricalVariable = this.testDataInitializer.createCategoricalVariable(VariableType.TRAIT, possibleValues);

		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(categoricalVariable.getCvTermId().toString()));

		final long count = this.variableServiceBrapi.countObservationVariables(searchRequestDTO);
		Assert.assertEquals((long) 1, count);
	}

	@Test
	public void testGetObservationVariables() {
		final List<String> possibleValues = new ArrayList<>();
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		final CVTerm categoricalVariable = this.testDataInitializer.createCategoricalVariable(VariableType.TRAIT, possibleValues);
		final VariableOverrides vo = new VariableOverrides();
		vo.setAlias(RandomStringUtils.randomAlphabetic(10));
		vo.setVariableId(categoricalVariable.getCvTermId());
		this.daoFactory.getVariableProgramOverridesDao().save(vo);

		final CvTermExternalReference varExRef = new CvTermExternalReference();
		varExRef.setCvTerm(categoricalVariable);
		varExRef.setSource(RandomStringUtils.randomAlphabetic(5));
		varExRef.setReferenceId(RandomStringUtils.randomAlphabetic(5));
		this.daoFactory.getCvTermExternalReferenceDAO().save(varExRef);

		final CVTermSynonym synonym = new CVTermSynonym();
		synonym.setSynonym(RandomStringUtils.randomAlphabetic(10));
		synonym.setCvTermId(categoricalVariable.getCvTermId());
		synonym.setTypeId(1230);
		synonym.setCvTermSynonymId(categoricalVariable.getCvTermId());
		this.daoFactory.getCvTermSynonymDao().save(synonym);

		this.sessionProvder.getSession().flush();

		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(categoricalVariable.getCvTermId().toString()));
		final String cropName = "MAIZE";
		final List<VariableDTO> variableDTOS = this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		Assert.assertEquals(1, variableDTOS.size());
		final VariableDTO dto = variableDTOS.get(0);
		Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getObservationVariableDbId());
		Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getOntologyReference().getOntologyDbId());
		Assert.assertEquals(vo.getAlias(), dto.getObservationVariableName());
		Assert.assertEquals(categoricalVariable.getName(), dto.getOntologyReference().getOntologyName());
		Assert.assertNotNull(dto.getMethod());
		Assert.assertNotNull(dto.getTrait());
		Assert.assertNotNull(dto.getScale());
		Assert.assertEquals(3, dto.getScale().getValidValues().getCategories().size());
		Assert.assertEquals(1, dto.getSynonyms().size());
		Assert.assertEquals(synonym.getSynonym(), dto.getSynonyms().get(0));
		Assert.assertEquals(vo.getAlias(), dto.getName());
		Assert.assertEquals(1, dto.getExternalReferences().size());
		Assert.assertEquals(varExRef.getSource(), dto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals(varExRef.getReferenceId(), dto.getExternalReferences().get(0).getReferenceID());
	}

	@Test
	public void testGetGermplasmAttributes() {
		final List<String> possibleValues = new ArrayList<>();
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		final CVTerm categoricalVariable = this.testDataInitializer.createCategoricalVariable(VariableType.GERMPLASM_PASSPORT, possibleValues);
		final VariableOverrides vo = new VariableOverrides();
		vo.setAlias(RandomStringUtils.randomAlphabetic(10));
		vo.setVariableId(categoricalVariable.getCvTermId());
		this.daoFactory.getVariableProgramOverridesDao().save(vo);

		final CvTermExternalReference varExRef = new CvTermExternalReference();
		varExRef.setCvTerm(categoricalVariable);
		varExRef.setSource(RandomStringUtils.randomAlphabetic(5));
		varExRef.setReferenceId(RandomStringUtils.randomAlphabetic(5));
		this.daoFactory.getCvTermExternalReferenceDAO().save(varExRef);

		final CVTermSynonym synonym = new CVTermSynonym();
		synonym.setSynonym(RandomStringUtils.randomAlphabetic(10));
		synonym.setCvTermId(categoricalVariable.getCvTermId());
		synonym.setTypeId(1230);
		synonym.setCvTermSynonymId(categoricalVariable.getCvTermId());
		this.daoFactory.getCvTermSynonymDao().save(synonym);

		this.sessionProvder.getSession().flush();

		final AttributeSearchRequestDTO searchRequestDTO = new AttributeSearchRequestDTO();
		searchRequestDTO.setAttributeDbIds(Collections.singletonList(categoricalVariable.getCvTermId().toString()));
		final String cropName = "MAIZE";
		final List<AttributeDTO> attributeDTOS = this.variableServiceBrapi.getGermplasmAttributes(searchRequestDTO, null);
		Assert.assertEquals(1, attributeDTOS.size());
		final AttributeDTO dto = attributeDTOS.get(0);
		Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getAttributeDbId());
		Assert.assertEquals(categoricalVariable.getDefinition(), dto.getAttributeDescription());
		Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getOntologyReference().getOntologyDbId());
		Assert.assertEquals(vo.getAlias(), dto.getAttributeName());
		Assert.assertEquals(categoricalVariable.getName(), dto.getOntologyReference().getOntologyName());
		Assert.assertNotNull(dto.getMethod());
		Assert.assertNotNull(dto.getTrait());
		Assert.assertNotNull(dto.getScale());
		Assert.assertEquals(3, dto.getScale().getValidValues().getCategories().size());
		Assert.assertEquals(1, dto.getSynonyms().size());
		Assert.assertEquals(synonym.getSynonym(), dto.getSynonyms().get(0));
		Assert.assertEquals(1, dto.getExternalReferences().size());
		Assert.assertEquals(varExRef.getSource(), dto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals(varExRef.getReferenceId(), dto.getExternalReferences().get(0).getReferenceID());
	}

	@Test
	public void testCountGermplasmAttributes() {
		final List<String> possibleValues = new ArrayList<>();
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		possibleValues.add(RandomStringUtils.randomAlphabetic(5));
		final CVTerm categoricalVariable = this.testDataInitializer.createCategoricalVariable(VariableType.GERMPLASM_ATTRIBUTE, possibleValues);

		final AttributeSearchRequestDTO searchRequestDTO = new AttributeSearchRequestDTO();
		searchRequestDTO.setAttributeDbIds(Collections.singletonList(categoricalVariable.getCvTermId().toString()));

		final long count = this.variableServiceBrapi.countGermplasmAttributes(searchRequestDTO);
		Assert.assertEquals((long) 1, count);
	}

	@Test
	public void testUpdateObservationVariable_AddVariableToStudy() {
		final TrialImportRequestDTO trialImportRequestDTO = new TrialImportRequestDTO();
		trialImportRequestDTO.setStartDate("2019-01-01");
		trialImportRequestDTO.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		trialImportRequestDTO.setTrialName(RandomStringUtils.randomAlphabetic(20));
		trialImportRequestDTO.setProgramDbId(this.commonTestProject.getUniqueID());
		final StudySummary studySummary = this.trialServiceBrapi
			.saveStudies(this.cropType.getCropName(), Collections.singletonList(trialImportRequestDTO), this.testUser.getUserid()).get(0);
		final StudyImportRequestDTO instance = new StudyImportRequestDTO();
		instance.setTrialDbId(studySummary.getTrialDbId().toString());
		instance.setLocationDbId("0");
		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(instance), this.testUser.getUserid()).get(0);

		final CVTerm variable = this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);
		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(variable.getCvTermId().toString()));
		final List<VariableDTO> variableDTOS =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		final VariableDTO variableDTO = variableDTOS.get(0);
		variableDTO.setStudyDbIds(Collections.singletonList(savedInstance.getStudyDbId()));

		this.variableServiceBrapi.updateObservationVariable(variableDTO);
		this.sessionProvder.getSession().flush();

		searchRequestDTO.setStudyDbId(Collections.singletonList(savedInstance.getStudyDbId()));
		final List<VariableDTO> retrievedVariableByStudyId =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		Assert.assertEquals(1, retrievedVariableByStudyId.size());
		Assert.assertEquals(variableDTO.getObservationVariableDbId(), retrievedVariableByStudyId.get(0).getObservationVariableDbId());
	}

	@Test
	public void testUpdateObservationVariable_UpdateVariable() {

		final CVTerm variable = this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);
		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(variable.getCvTermId().toString()));
		final List<VariableDTO> variableDTOS =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		final VariableDTO variableDTO = variableDTOS.get(0);

		variableDTO.setObservationVariableName(RandomStringUtils.randomAlphabetic(10));
		this.variableServiceBrapi.updateObservationVariable(variableDTO);
		this.sessionProvder.getSession().flush();

		final List<VariableDTO> retrievedVariables =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		Assert.assertEquals(1, retrievedVariables.size());
		final VariableDTO retrievedVariable = retrievedVariables.get(0);
		Assert.assertEquals(variableDTO.getObservationVariableDbId(), retrievedVariable.getObservationVariableDbId());
		Assert.assertEquals(variableDTO.getObservationVariableName(), retrievedVariable.getObservationVariableName());
	}

	@Test
	public void testUpdateObservationVariable_UpdateVariable_VariableAlreadyAssignedToStudy() {

		final TrialImportRequestDTO trialImportRequestDTO = new TrialImportRequestDTO();
		trialImportRequestDTO.setStartDate("2019-01-01");
		trialImportRequestDTO.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		trialImportRequestDTO.setTrialName(RandomStringUtils.randomAlphabetic(20));
		trialImportRequestDTO.setProgramDbId(this.commonTestProject.getUniqueID());
		final StudySummary studySummary = this.trialServiceBrapi
			.saveStudies(this.cropType.getCropName(), Collections.singletonList(trialImportRequestDTO), this.testUser.getUserid()).get(0);
		final StudyImportRequestDTO instance = new StudyImportRequestDTO();
		instance.setTrialDbId(studySummary.getTrialDbId().toString());
		instance.setLocationDbId("0");
		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(instance), this.testUser.getUserid()).get(0);

		final CVTerm variable = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(20));
		final CVTerm scale = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_SCALE.getId(), variable.getCvTermId(), scale.getCvTermId()));

		final CVTerm property = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.PROPERTIES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_PROPERTY.getId(), variable.getCvTermId(), property.getCvTermId()));

		final CVTerm method = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.METHODS.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_METHOD.getId(), variable.getCvTermId(), method.getCvTermId()));

		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), scale.getCvTermId(), DataType.NUMERIC_VARIABLE.getId()));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(TermId.VARIABLE_TYPE.getId(), VariableType.TRAIT.getName(), 1, variable.getCvTermId()));

		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(variable.getCvTermId().toString()));
		final List<VariableDTO> variableDTOS =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		final VariableDTO variableDTO = variableDTOS.get(0);
		variableDTO.setStudyDbIds(Collections.singletonList(savedInstance.getStudyDbId()));

		// Assign variable to study first
		this.variableServiceBrapi.updateObservationVariable(variableDTO);
		this.sessionProvder.getSession().flush();

		// Try to update the fields
		variableDTO.setObservationVariableName(RandomStringUtils.randomAlphabetic(10));
		this.variableServiceBrapi.updateObservationVariable(variableDTO);
		this.sessionProvder.getSession().flush();

		final List<VariableDTO> retrievedVariables =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		Assert.assertEquals(1, retrievedVariables.size());
		final VariableDTO retrievedVariable = retrievedVariables.get(0);
		Assert.assertEquals(variable.getCvTermId().toString(), retrievedVariable.getObservationVariableDbId());
		Assert.assertEquals(variable.getName(), retrievedVariable.getObservationVariableName());
		Assert.assertEquals(property.getName(), retrievedVariable.getTrait().getTraitName());
	}

	@Test
	public void testUpdateObservationVariable_UpdateTraitScaleAndMethod() {

		final CVTerm variable = this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);
		final CVTerm newScale = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
				.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), newScale.getCvTermId(), DataType.NUMERIC_VARIABLE
						.getId()));
		final CVTerm newProperty = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.PROPERTIES.getId());
		final CVTerm newMethod = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.METHODS.getId());

		final VariableSearchRequestDTO searchRequestDTO = new VariableSearchRequestDTO();
		searchRequestDTO.setObservationVariableDbIds(Collections.singletonList(variable.getCvTermId().toString()));
		final List<VariableDTO> variableDTOS =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		final VariableDTO variableDTO = variableDTOS.get(0);

		variableDTO.setObservationVariableName(RandomStringUtils.randomAlphabetic(10));
		variableDTO.getTrait().setTraitDbId(newProperty.getCvTermId().toString());
		variableDTO.getMethod().setMethodDbId(newMethod.getCvTermId().toString());
		variableDTO.getScale().setScaleDbId(newScale.getCvTermId().toString());
		this.variableServiceBrapi.updateObservationVariable(variableDTO);
		this.sessionProvder.getSession().flush();

		final List<VariableDTO> retrievedVariables =
			this.variableServiceBrapi.getObservationVariables(searchRequestDTO, null);
		Assert.assertEquals(1, retrievedVariables.size());
		final VariableDTO retrievedVariable = retrievedVariables.get(0);
		Assert.assertEquals(variableDTO.getObservationVariableDbId(), retrievedVariable.getObservationVariableDbId());
		Assert.assertEquals(variableDTO.getObservationVariableName(), retrievedVariable.getObservationVariableName());
		Assert.assertEquals(newProperty.getName(), retrievedVariable.getTrait().getTraitName());
		Assert.assertEquals(newProperty.getDefinition(), retrievedVariable.getTrait().getTraitDescription());
		Assert.assertEquals(newMethod.getName(), retrievedVariable.getMethod().getMethodName());
		Assert.assertEquals(newMethod.getDefinition(), retrievedVariable.getMethod().getDescription());
		Assert.assertEquals(newScale.getName(), retrievedVariable.getScale().getScaleName());

	}

}
