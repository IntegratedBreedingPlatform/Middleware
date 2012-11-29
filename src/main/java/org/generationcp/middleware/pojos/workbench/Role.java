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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "workbench_role")
public class Role implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public final static int MANAGER_ROLE_ID = 1;
    
    public final static String MANAGER_ROLE_NAME = "Manager";
    public final static String MARS_ROLE_NAME = "MARS Breeder";
    public final static String MAS_ROLE_NAME = "MAS Breeder";
    public final static String MABC_ROLE_NAME = "MABC Breeder";
    public final static String CB_ROLE_NAME = "CB Breeder";

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

    @Basic(optional = false)
    @Column(name = "role_label")
    private String label;
    
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

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [roleId=");
        builder.append(roleId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", workflowTemplate=");
        builder.append(workflowTemplate);
        builder.append("]");
        return builder.toString();
    }

}
