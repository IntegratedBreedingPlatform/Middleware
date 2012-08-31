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
@Table(name = "workbench_ibdb_user_map")
public class IbdbUserMap implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "ibdb_user_map_id")
    private Long ibdbUserMapId;
    
    @Basic(optional = false)
    @Column(name = "workbench_user_id")
    private Integer workbenchUserId;
    
    @Basic(optional = false)
    @Column(name = "project_id")
    private Long projectId;
    
    @Basic(optional = false)
    @Column(name = "ibdb_user_id")
    private Integer ibdbUserId;


    public Long getIbdbUserMapId() {
        return ibdbUserMapId;
    }

    public void setIbdbUserMapId(Long ibdbUserMapId) {
        this.ibdbUserMapId = ibdbUserMapId;
    }

    public Integer getWorkbenchUserId() {
        return workbenchUserId;
    }

    public void setWorkbenchUserId(Integer workbenchUserId) {
        this.workbenchUserId = workbenchUserId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getIbdbUserId() {
        return ibdbUserId;
    }

    public void setIbdbUserId(Integer ibdbUserId) {
        this.ibdbUserId = ibdbUserId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(ibdbUserMapId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!IbdbUserMap.class.isInstance(obj)) {
            return false;
        }

        IbdbUserMap otherObj = (IbdbUserMap) obj;

        return new EqualsBuilder().append(ibdbUserMapId, otherObj.ibdbUserMapId).isEquals();
    }
}
