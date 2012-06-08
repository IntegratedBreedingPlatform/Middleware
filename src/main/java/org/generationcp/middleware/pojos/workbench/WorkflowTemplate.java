package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "workbench_workflow_template")
public class WorkflowTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "template_id")
    private Long templateId;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @Column(name = "user_defined")
    private boolean userDefined;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "workflow_template_step", joinColumns = { @JoinColumn(name = "template_id") }, inverseJoinColumns = { @JoinColumn(name = "step_id") })
    @OrderColumn(name = "step_number")
    private List<WorkflowStep> steps;

    public WorkflowTemplate() {
    }

    public WorkflowTemplate(Long templateId) {
	this.templateId = templateId;
    }

    public WorkflowTemplate(String templateIdStr) {
	templateId = Long.parseLong(templateIdStr);
    }

    public Long getTemplateId() {
	return templateId;
    }

    public void setTemplateId(Long templateId) {
	this.templateId = templateId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean isUserDefined() {
	return userDefined;
    }

    public void setUserDefined(boolean userDefined) {
	this.userDefined = userDefined;
    }

    public List<WorkflowStep> getSteps() {
	return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
	this.steps = steps;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(templateId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!WorkflowTemplate.class.isInstance(obj))
	    return false;

	WorkflowTemplate otherObj = (WorkflowTemplate) obj;

	return new EqualsBuilder().append(templateId, otherObj.templateId)
		.isEquals();
    }

    @Override
    public String toString() {
	return name;
    }
}
