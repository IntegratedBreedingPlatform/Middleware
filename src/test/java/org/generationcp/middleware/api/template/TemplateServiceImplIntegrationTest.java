package org.generationcp.middleware.api.template;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TemplateServiceImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    TemplateService templateService;

    private IntegrationTestDataInitializer testDataInitializer;
    private DaoFactory daoFactory;

    private static final String PROGRAM_UUID = UUID.randomUUID().toString();

    @Before
    public void setUp() {
        this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
        this.daoFactory = new DaoFactory(this.sessionProvder);
    }

    @Test
    public void testSaveTemplate() {
        final TemplateDTO templateToSave = this.createTemplateDTO();
        this.templateService.saveTemplate(templateToSave);
        final TemplateDTO savedTemplate = this.templateService.getTemplateByIdAndProgramUUID(templateToSave.getTemplateId(), PROGRAM_UUID);
        Assert.assertNotNull(savedTemplate);
        Assert.assertEquals(templateToSave.getTemplateName(), savedTemplate.getTemplateName());
        Assert.assertEquals(templateToSave.getTemplateType(), savedTemplate.getTemplateType());
        Assert.assertEquals(templateToSave.getTemplateDetails().size(), savedTemplate.getTemplateDetails().size());
    }

    @Test
    public void testUpdateTemplate() {
        final TemplateDTO templateToSave = this.createTemplateDTO();
        this.templateService.saveTemplate(templateToSave);
        final TemplateDTO savedTemplate = this.templateService.getTemplateByIdAndProgramUUID(templateToSave.getTemplateId(), PROGRAM_UUID);
        savedTemplate.setTemplateName(RandomStringUtils.randomAlphabetic(15));
        final TemplateDTO updatedTemplate = this.templateService.updateTemplate(savedTemplate);
        Assert.assertNotNull(updatedTemplate);
        Assert.assertEquals(savedTemplate.getTemplateName(), updatedTemplate.getTemplateName());
        Assert.assertEquals(savedTemplate.getTemplateType(), updatedTemplate.getTemplateType());
        Assert.assertEquals(savedTemplate.getTemplateDetails().size(), updatedTemplate.getTemplateDetails().size());
    }

    @Test
    public void testGetTemplateByIdAndProgramUUID() {
        final TemplateDTO templateToSave = this.createTemplateDTO();
        this.templateService.saveTemplate(templateToSave);
        final TemplateDTO returnedTemplate = this.templateService.getTemplateByIdAndProgramUUID(templateToSave.getTemplateId(), PROGRAM_UUID);
        Assert.assertNotNull(returnedTemplate);
        Assert.assertEquals(templateToSave.getTemplateName(), returnedTemplate.getTemplateName());
        Assert.assertEquals(templateToSave.getTemplateType(), returnedTemplate.getTemplateType());
        Assert.assertEquals(templateToSave.getTemplateDetails().size(), returnedTemplate.getTemplateDetails().size());
    }

    @Test
    public void testGetTemplateByNameAndProgramUUID() {
        final TemplateDTO templateToSave = this.createTemplateDTO();
        this.templateService.saveTemplate(templateToSave);
        final TemplateDTO returnedTemplate = this.templateService.getTemplateByNameAndProgramUUID(templateToSave.getTemplateName(), PROGRAM_UUID);
        Assert.assertNotNull(returnedTemplate);
        Assert.assertEquals(templateToSave.getTemplateName(), returnedTemplate.getTemplateName());
        Assert.assertEquals(templateToSave.getTemplateType(), returnedTemplate.getTemplateType());
        Assert.assertEquals(templateToSave.getTemplateDetails().size(), returnedTemplate.getTemplateDetails().size());
    }

    @Test
    public void testDeleteTemplate() {
        final TemplateDTO templateToSave = this.createTemplateDTO();
        this.templateService.saveTemplate(templateToSave);
        final TemplateDTO returnedTemplate = this.templateService.getTemplateByNameAndProgramUUID(templateToSave.getTemplateName(), PROGRAM_UUID);
        Assert.assertNotNull(returnedTemplate);
        this.templateService.deleteTemplate(returnedTemplate.getTemplateId());
        Assert.assertNull(this.daoFactory.getTemplateDAO().getById(returnedTemplate.getTemplateId()));
    }

    @Test
    public void testGetTemplateDTOsByType() {
        final List<TemplateDTO> templateDTOS = this.templateService.getTemplateDTOsByType(PROGRAM_UUID, TemplateType.DESCRIPTORS.getName());
        this.templateService.saveTemplate(this.createTemplateDTO());
        final List<TemplateDTO> updatedTemplateDTOList = this.templateService.getTemplateDTOsByType(PROGRAM_UUID, TemplateType.DESCRIPTORS.getName());
        Assert.assertEquals(templateDTOS.size() + 1, updatedTemplateDTOList.size());
    }

    public TemplateDTO createTemplateDTO() {
        final TemplateDTO templateDTO = new TemplateDTO();
        templateDTO.setTemplateName(RandomStringUtils.randomAlphabetic(10));
        templateDTO.setTemplateType(TemplateType.DESCRIPTORS.getName());
        templateDTO.setProgramUUID(PROGRAM_UUID);
        final List<String> possibleValues = new ArrayList<>();
        possibleValues.add(RandomStringUtils.randomAlphabetic(5));
        final CVTerm categoricalVariable = this.testDataInitializer
                .createCategoricalVariable(VariableType.GERMPLASM_ATTRIBUTE, possibleValues);
        final TemplateDetailsDTO detail = new TemplateDetailsDTO();
        detail.setName(categoricalVariable.getName());
        detail.setVariableId(categoricalVariable.getCvTermId());
        detail.setType(VariableType.GERMPLASM_ATTRIBUTE.name());
        templateDTO.setTemplateDetails(Collections.singletonList(detail));
        return templateDTO;
    }
}
