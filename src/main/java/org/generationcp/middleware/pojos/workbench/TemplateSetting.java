/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * POJO for the template_setting table in the workbench database.
 * 
 * @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "template_setting")
public class TemplateSetting implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "template_setting_id")
    private Integer templateSettingId;

    @Basic(optional = false)
    @Column(name = "project_id")
    private Integer projectId;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;
    
    @Basic(optional = false)
    @Column(name = "configuration")
    private String configuration;

    @Basic(optional = true)
    @Column(name = "is_default")
    private Integer isDefault;    
    
    public TemplateSetting() {
        super();
    }

    public TemplateSetting(Integer templateSettingId, Integer projectId, String name, Tool tool, 
            String configuration, Boolean isDefault) {
        this.templateSettingId = templateSettingId;
        this.projectId = projectId;
        this.name = name;
        this.tool = tool;
        this.configuration = configuration;
        this.setIsDefault(isDefault);
    }

    public Integer getTemplateSettingId() {
        return templateSettingId;
    }
    
    public void setTemplateSettingId(Integer templateSettingId) {
        this.templateSettingId = templateSettingId;
    }
    
    public Integer getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Tool getTool() {
        return tool;
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
        
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    public Integer getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        if (isDefault == null){
            this.isDefault = null;
        } else if (isDefault){
            this.isDefault = 1;
        } else {
            this.isDefault = 0;
        }
    }

    public Boolean isDefault() {
       return isDefault > 0 ? true : false;
   }
    
    //mainly use to set the default to null so we can search all
    public void setIsDefaultToNull(){
    	isDefault = null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        result = prime * result + ((isDefault == null) ? 0 : isDefault.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + ((templateSettingId == null) ? 0 : templateSettingId.hashCode());
        result = prime * result + ((tool == null) ? 0 : tool.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TemplateSetting other = (TemplateSetting) obj;
        if (configuration == null) {
            if (other.configuration != null)
                return false;
        } else if (!configuration.equals(other.configuration))
            return false;
        if (isDefault == null) {
            if (other.isDefault != null)
                return false;
        } else if (!isDefault.equals(other.isDefault))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (templateSettingId == null) {
            if (other.templateSettingId != null)
                return false;
        } else if (!templateSettingId.equals(other.templateSettingId))
            return false;
        if (tool == null) {
            if (other.tool != null)
                return false;
        } else if (!tool.equals(other.tool))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TemplateSetting [templateSettingId=");
        builder.append(templateSettingId);
        builder.append(", projectId=");
        builder.append(projectId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", tool=");
        builder.append(tool);
        builder.append(", configuration=");
        builder.append(configuration);
        builder.append(", isDefault=");
        builder.append(isDefault);
        builder.append("]");
        return builder.toString();
    }

    
}
