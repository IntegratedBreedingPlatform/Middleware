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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * POJO for workbench_crop table.
 *  
 */
@Entity
@Table(name = "workbench_crop")
public class CropType implements Serializable{

    private static final long serialVersionUID = 1L;

    // Initial pre-defined crops. Used mainly in testing
    public final static String CHICKPEA = "Chickpea";
    public final static String COWPEA = "Cowpea";
    public final static String MAIZE = "Maize";
    public final static String RICE = "Rice";
    public final static String WHEAT = "Wheat";
    public final static String CASSAVA = "Cassava";
    public final static String GROUNDNUT = "Groundnut";
    public final static String SORGHUM = "Sorghum";
    public final static String PHASEOLUS = "Phaseolus";
    
    public enum CropEnum {
    	CASSAVA,
    	CHICKPEA,
    	COWPEA,
    	GROUNDNUT,
    	MAIZE,
    	PHASEOLUS,
    	RICE,
    	SORGHUM,
    	WHEAT,
    	LENTIL,
    	SOYBEAN,
    	BEAN,
    	PEARLMILLET;
    	
    	@Override
		public String toString(){
    		return super.toString().toLowerCase();
    	}
    	
    }

    @Id
    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "central_db_name")
    private String centralDbName;

    @Column(name = "schema_version")
    private String version;

    public CropType() {
    }

    public CropType(String cropName) {
        this.cropName = cropName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCentralDbName() {
    	// HACK: hard coded to return the same single DB per crop.
        return String.format("ibdbv2_%s_merged", cropName.trim().toLowerCase().replaceAll("\\s+", "_"));
    }

    public void setCentralDbName(String centralDbName) {
        this.centralDbName = centralDbName;
    }
    
    public String getLocalDatabaseNameWithProject(Project project) {
        return getLocalDatabaseNameWithProjectId(project.getProjectId());
    }
    
    //TODO rename/replace once the centralDbName column is renamed. ProjectId is no longer a factor in deriving crop db name.
    public String getLocalDatabaseNameWithProjectId(Long projectId) {
    	// HACK: hard coded to return the same single DB per crop.
    	return String.format("ibdbv2_%s_merged", cropName.trim().toLowerCase().replaceAll("\\s+", "_"));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cropName == null) ? 0 : cropName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CropType other = (CropType) obj;
        if (cropName == null) {
            if (other.cropName != null) {
                return false;
            }
        } else if (!cropName.equals(other.cropName)) {
            return false;
        }
        return true;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CropType [cropName=");
        builder.append(cropName);
        builder.append(", centralDbName=");
        builder.append(centralDbName);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
