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
@Table(name = "workbench_workflow_step")
public class WorkflowStep implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "step_id")
    private Long stepId;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "workflow_step_tool", joinColumns = { @JoinColumn(name = "step_id") }, inverseJoinColumns = { @JoinColumn(name = "tool_id") })
    @OrderColumn(name = "tool_number")
    private List<Tool> tools;

    public Long getStepId() {
	return stepId;
    }

    public void setStepId(Long stepId) {
	this.stepId = stepId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<Tool> getTools() {
	return tools;
    }

    public void setTools(List<Tool> tools) {
	this.tools = tools;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(stepId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!WorkflowStep.class.isInstance(obj))
	    return false;

	WorkflowStep otherObj = (WorkflowStep) obj;

	return new EqualsBuilder().append(stepId, otherObj.stepId).isEquals();
    }
}
