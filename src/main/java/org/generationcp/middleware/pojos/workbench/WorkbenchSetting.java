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
@Table(name = "workbench_setting")
public class WorkbenchSetting implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "setting_id")
    private Integer settingId;
    
    @Basic(optional = false)
    @Column(name = "installation_directory")
    private String installationDirectory;

    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public String getInstallationDirectory() {
        return installationDirectory;
    }

    public void setInstallationDirectory(String installationDirectory) {
        this.installationDirectory = installationDirectory;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(settingId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!WorkbenchSetting.class.isInstance(obj)) {
            return false;
        }
        
        WorkbenchSetting otherObj = (WorkbenchSetting) obj;
        
        return new EqualsBuilder().append(settingId, otherObj.settingId).isEquals();
    }
}
