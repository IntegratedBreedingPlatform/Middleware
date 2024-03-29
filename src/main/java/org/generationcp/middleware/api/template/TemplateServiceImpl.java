package org.generationcp.middleware.api.template;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Template;
import org.generationcp.middleware.pojos.TemplateDetails;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final DaoFactory daoFactory;

    public TemplateServiceImpl(final HibernateSessionProvider sessionProvider) {
        this.daoFactory = new DaoFactory(sessionProvider);
    }

    @Override
    public TemplateDTO getTemplateByIdAndProgramUUID(final Integer templateId, final String programUUID) {
        final TemplateDTO templateDTO = this.daoFactory.getTemplateDAO().getTemplateByIdAndProgramUUID(templateId, programUUID);
        this.populateTemplateDetails(templateDTO);
        return templateDTO;
    }

    @Override
    public TemplateDTO getTemplateByNameAndProgramUUID(final String name, final String programUUID) {
        final TemplateDTO templateDTO = this.daoFactory.getTemplateDAO().getTemplateByNameAndProgramUUID(name, programUUID);
        this.populateTemplateDetails(templateDTO);
        return templateDTO;
    }

    @Override
    public TemplateDTO saveTemplate(final TemplateDTO templateDTO) {
        final Template template = new Template();
        template.setTemplateName(templateDTO.getTemplateName());
        template.setTemplateType(templateDTO.getTemplateType());
        template.setProgramUUID(templateDTO.getProgramUUID());
        template.setTemplateDetails(new ArrayList<>());
        for(final TemplateDetailsDTO templateDetailsDTO: templateDTO.getTemplateDetails()) {
            final TemplateDetails templateDetails = this.mapTemplateDetails(template, templateDetailsDTO);
            template.getTemplateDetails().add(templateDetails);
        }
        this.daoFactory.getTemplateDAO().save(template);
        templateDTO.setTemplateId(template.getTemplateId());
        return templateDTO;
    }

    @Override
    public TemplateDTO updateTemplate(final TemplateDTO templateDTO) {
        final Template template = this.daoFactory.getTemplateDAO().getById(templateDTO.getTemplateId());
        template.setTemplateName(templateDTO.getTemplateName());
        template.setTemplateType(templateDTO.getTemplateType());
        template.setProgramUUID(templateDTO.getProgramUUID());
        this.daoFactory.getTemplateDetailsDAO().deleteByTemplateId(templateDTO.getTemplateId());
        final List<TemplateDetails> templateDetailsList = new ArrayList<>();
        for(final TemplateDetailsDTO templateDetailsDTO: templateDTO.getTemplateDetails()) {
            final TemplateDetails templateDetails = this.mapTemplateDetails(template, templateDetailsDTO);
            templateDetailsList.add(templateDetails);
        }
        template.setTemplateDetails(templateDetailsList);
        this.daoFactory.getTemplateDAO().saveOrUpdate(template);
        return templateDTO;
    }

    private TemplateDetails mapTemplateDetails(final Template template, final TemplateDetailsDTO templateDetailsDTO) {
        final TemplateDetails templateDetails = new TemplateDetails();
        templateDetails.setTemplate(template);
        final CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(templateDetailsDTO.getVariableId());
        templateDetails.setVariable(cvTerm);
        templateDetails.setName(templateDetailsDTO.getName());
        templateDetails.setType(templateDetailsDTO.getType());
        return templateDetails;
    }

    @Override
    public void deleteTemplate(final Integer templateId) {
        Preconditions.checkNotNull(templateId);
        final Template template = this.daoFactory.getTemplateDAO().getById(templateId);

        Preconditions.checkArgument(template != null, "Template does not exist");
        try {
            this.daoFactory.getTemplateDetailsDAO().deleteByTemplateId(templateId);
            this.daoFactory.getTemplateDAO().makeTransient(template);
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException("Error in deleteTemplate in TemplateServiceImpl: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TemplateDTO> getTemplateDTOsByType(final String programUUID, final String type) {
        final List<TemplateDTO> templateDTOS = this.daoFactory.getTemplateDAO().getTemplateDTOsByType(programUUID, type);
        this.populateTemplateDetails(templateDTOS);
        return templateDTOS;
    }

    public void populateTemplateDetails(final TemplateDTO templateDTO) {
        if (templateDTO != null) {
            this.populateTemplateDetails(Collections.singletonList(templateDTO));
        }
    }

    private void populateTemplateDetails(final List<TemplateDTO> templateDTOS) {
        if (CollectionUtils.isNotEmpty(templateDTOS)) {
            final Map<Integer, List<TemplateDetailsDTO>> templateDetailsMapByTemplateIds = this.daoFactory.getTemplateDetailsDAO()
                    .getTemplateDetailsMapByTemplateIds(templateDTOS.stream().map(TemplateDTO::getTemplateId).collect(Collectors.toList()));
            for (final TemplateDTO dto : templateDTOS) {
                if (CollectionUtils.isNotEmpty(templateDetailsMapByTemplateIds.get(dto.getTemplateId()))) {
                    dto.setTemplateDetails(templateDetailsMapByTemplateIds.get(dto.getTemplateId()));
                }
            }
        }
    }
}
