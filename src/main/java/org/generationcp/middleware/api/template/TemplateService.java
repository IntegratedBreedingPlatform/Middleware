package org.generationcp.middleware.api.template;

import org.generationcp.middleware.pojos.Template;

import java.util.List;

public interface TemplateService {

    TemplateDTO saveTemplate(TemplateDTO templateDTO);

    TemplateDTO updateTemplate(TemplateDTO templateDTO);

    void deleteTemplate(Integer templateId);

    List<TemplateDTO> getTemplateDTOsByType(String programUUID, String type);

    TemplateDTO getTemplateByNameAndProgramUUID(String name, String programUUID);

    TemplateDTO getTemplateByIdAndProgramUUID(Integer id, String programUUID);
}
