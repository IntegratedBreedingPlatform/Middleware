package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "project_workflow_step")
public class ProjectWorkflowStep implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_workflow_step_id")
    private Long projectWorkflowStepId;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "step_id")
    private WorkflowStep step;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "contact_id")
    private Contact owner;
    
    @Basic(optional = false)
    @Column(name = "due_date")
    private Date dueDate;
    
    @Basic(optional = false)
    @Column(name = "status")
    private String status;

    public Long getProjectWorkflowStepId() {
        return projectWorkflowStepId;
    }

    public void setProjectWorkflowStepId(Long projectWorkflowStepId) {
        this.projectWorkflowStepId = projectWorkflowStepId;
    }

    public WorkflowStep getStep() {
        return step;
    }

    public void setStep(WorkflowStep step) {
        this.step = step;
    }

    public Contact getOwner() {
        return owner;
    }

    public void setOwner(Contact owner) {
        this.owner = owner;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectWorkflowStepId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!ProjectWorkflowStep.class.isInstance(obj)) return false;

        ProjectWorkflowStep otherObj = (ProjectWorkflowStep) obj;

        return new EqualsBuilder().append(projectWorkflowStepId, otherObj.projectWorkflowStepId).isEquals();
    }
}
