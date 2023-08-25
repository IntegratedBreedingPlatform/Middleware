package org.generationcp.middleware.api.template;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class TemplateDTO {

    private Integer templateId;

    private String programUUID;

    private String templateName;

    private String templateType;

    private List<TemplateDetailsDTO> templateDetails;

    public TemplateDTO() {
    }

    public TemplateDTO(final Integer templateId, final String templateName, final String programUUID, final String templateType) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.programUUID = programUUID;
        this.templateType = templateType;
    }

    public Integer getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final Integer templateId) {
        this.templateId = templateId;
    }

    public String getProgramUUID() {
        return this.programUUID;
    }

    public void setProgramUUID(final String programUUID) {
        this.programUUID = programUUID;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(final String templateType) {
        this.templateType = templateType;
    }

    public List<TemplateDetailsDTO> getTemplateDetails() {
        return this.templateDetails;
    }

    public void setTemplateDetails(final List<TemplateDetailsDTO> templateDetails) {
        this.templateDetails = templateDetails;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }

}
