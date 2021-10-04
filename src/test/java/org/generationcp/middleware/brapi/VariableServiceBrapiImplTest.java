package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.VariableServiceBrapi;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.VariableExternalReference;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableServiceBrapiImplTest  extends IntegrationTestBase {

    @Autowired
    private VariableServiceBrapi variableServiceBrapi;

    private IntegrationTestDataInitializer testDataInitializer;

    private DaoFactory daoFactory;

    @Before
    public void setUp() {
        this.daoFactory = new DaoFactory(this.sessionProvder);
        this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
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

        final VariableExternalReference varExRef = new VariableExternalReference();
        varExRef.setCvTerm(categoricalVariable);
        varExRef.setSource(RandomStringUtils.randomAlphabetic(5));
        varExRef.setReferenceId(RandomStringUtils.randomAlphabetic(5));
        this.daoFactory.getVariableExternalReferenceDAO().save(varExRef);

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
}
