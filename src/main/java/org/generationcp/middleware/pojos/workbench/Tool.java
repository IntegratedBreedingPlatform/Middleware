package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "tool")
public class Tool implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "tool_id")
    private Long toolId;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String toolName;
    
    @Basic(optional = false)
    @Column(name = "tool_type")
    private ToolType toolType;
    
    @Basic(optional = false)
    @Column(name = "path")
    private String path;

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(toolId).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!Tool.class.isInstance(obj)) return false;
        
        Tool otherObj = (Tool) obj;
        
        return new EqualsBuilder().append(toolId, otherObj.toolId).isEquals();
    }
}
