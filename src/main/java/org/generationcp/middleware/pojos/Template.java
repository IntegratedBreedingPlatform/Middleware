package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "template")
public class Template extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "program_uuid")
    private String programUUID;

    @Column(name = "template_type")
    private String templateType;

    @Column(name = "template_name")
    private String templateName;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<TemplateDetails> templateDetails = new ArrayList<>();

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

    public String getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(final String templateType) {
        this.templateType = templateType;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public List<TemplateDetails> getTemplateDetails() {
        return this.templateDetails;
    }

    public void setTemplateDetails(final List<TemplateDetails> templateDetails) {
        this.templateDetails.clear();
        this.templateDetails.addAll(templateDetails);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Template)) {
            return false;
        }
        final Template castOther = (Template) other;
        return new EqualsBuilder().append(this.templateId, castOther.templateId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.templateId).hashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }


}
