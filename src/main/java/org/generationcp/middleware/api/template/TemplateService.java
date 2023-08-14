package org.generationcp.middleware.api.template;

import java.util.List;

public interface TemplateService {

    TemplateDTO saveTemplate(TemplateDTO templateDTO);

    TemplateDTO updateTemplate(TemplateDTO templateDTO);

    void deleteTemplate(Integer templateId);

    List<TemplateDTO> getTemplateDTOsByType(String programUUID, String type);
}
