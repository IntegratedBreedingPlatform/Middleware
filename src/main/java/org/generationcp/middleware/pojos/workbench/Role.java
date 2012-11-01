/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.generationcp.middleware.pojos.User;

@Entity
@Table(name = "workbench_role")
public class Role implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public final static int MANAGER_ROLE_ID = 1;

    @Id
    @Basic(optional = false)
    @Column(name = "role_id")
    private Integer roleId;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @OneToOne(optional = false)
    @JoinColumn(name = "workflow_template_id")
    private WorkflowTemplate workflowTemplate;

    public Role() {
    }

    public Role(Integer roleId, String name, WorkflowTemplate workflowTemplate) {
        super();
        this.roleId = roleId;
        this.name = name;
        this.workflowTemplate = workflowTemplate;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkflowTemplate getWorkflowTemplate() {
        return workflowTemplate;
    }

    public void setWorkflowTemplate(WorkflowTemplate workflowTemplate) {
        this.workflowTemplate = workflowTemplate;
    }

    @Override
    public String toString() {
        return "Role [roleId=" + roleId + ", name=" + name + ", workflowTemplate=" + workflowTemplate + "]";
    }

}
