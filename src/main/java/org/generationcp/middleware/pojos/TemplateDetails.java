package org.generationcp.middleware.pojos;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.oms.CVTerm;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "template_details")
public class TemplateDetails extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "template_details_id")
    private Integer templateDetailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false, updatable = false)
    private Template template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variable_id")
    private CVTerm variable;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    public Integer getTemplateDetailsId() {
        return templateDetailsId;
    }

    public void setTemplateDetailsId(final Integer templateDetailsId) {
        this.templateDetailsId = templateDetailsId;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(final Template template) {
        this.template = template;
    }

    public CVTerm getVariable() {
        return this.variable;
    }

    public void setVariable(final CVTerm variable) {
        this.variable = variable;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof TemplateDetails)) {
            return false;
        }
        final TemplateDetails castOther = (TemplateDetails) other;
        return new EqualsBuilder().append(this.templateDetailsId, castOther.templateDetailsId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.templateDetailsId).hashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
