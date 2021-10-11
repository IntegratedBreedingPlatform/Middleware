package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.StudyServiceBrapi;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.VariableServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.CvTermExternalReference;
import org.generationcp.middleware.pojos.oms.CVTerm;
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

public class VariableServiceBrapiImplTest  extends IntegrationTestBase {

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
        Assert.assertEquals((long)1, count);
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
        final List<VariableDTO> variableDTOS = this.variableServiceBrapi.getObservationVariables(cropName, searchRequestDTO, null);
        Assert.assertEquals(1, variableDTOS.size());
        final VariableDTO dto = variableDTOS.get(0);
        Assert.assertEquals(cropName, dto.getCrop());
        Assert.assertEquals(cropName, dto.getCommonCropName());
        Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getObservationVariableDbId());
        Assert.assertEquals(categoricalVariable.getCvTermId().toString(), dto.getOntologyDbId());
        Assert.assertEquals(vo.getAlias(), dto.getObservationVariableName());
        Assert.assertEquals(categoricalVariable.getName(), dto.getOntologyName());
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
        final List<VariableDTO> variableDTOS = this.variableServiceBrapi.getObservationVariables(this.cropType.getCropName(), searchRequestDTO, null);
        final VariableDTO variableDTO = variableDTOS.get(0);
        variableDTO.setStudyDbIds(Collections.singletonList(savedInstance.getStudyDbId()));

        this.variableServiceBrapi.updateObservationVariable(variableDTO);


        this.sessionProvder.getSession().flush();

        searchRequestDTO.setStudyDbId(Collections.singletonList(savedInstance.getStudyDbId()));
        final List<VariableDTO> retrievedVariableByStudyId = this.variableServiceBrapi.getObservationVariables(this.cropType.getCropName(), searchRequestDTO, null);
        Assert.assertEquals(1, retrievedVariableByStudyId.size());
        Assert.assertEquals(variableDTO.getObservationVariableDbId(), retrievedVariableByStudyId.get(0).getObservationVariableDbId());
    }
}
