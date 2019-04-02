/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Type;

import java.io.Serializable;

import javax.persistence.*;

/**
 * POJO for workbench_crop table.
 * 
 */
@Entity
@Table(name = "workbench_crop", schema = "workbench")
public class CropType implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum CropEnum {
		CASSAVA, CHICKPEA, COWPEA, GROUNDNUT, MAIZE, PHASEOLUS, RICE, SORGHUM, WHEAT, LENTIL, SOYBEAN, BEAN, PEARLMILLET;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

	}

	@Id
	@Column(name = "crop_name")
	private String cropName;

	@Column(name = "db_name")
	private String dbName;

	@Column(name = "schema_version")
	private String version;

	@Column(name = "plot_code_prefix")
	private String plotCodePrefix;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "use_uuid", columnDefinition = "TINYINT")
	private boolean useUUID;

	public CropType() {
	}

	public CropType(String cropName) {
		this.cropName = cropName;
	}

	public String getPlotCodePrefix() {
		return plotCodePrefix;
	}

	public void setPlotCodePrefix(final String plotCodePrefix) {
		this.plotCodePrefix = plotCodePrefix;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
	}

	public String getDbName() {
		return String.format("ibdbv2_%s_merged", this.cropName.trim().toLowerCase().replaceAll("\\s+", "_"));
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cropName == null ? 0 : this.cropName.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		CropType other = (CropType) obj;
		if (this.cropName == null) {
			if (other.cropName != null) {
				return false;
			}
		} else if (!this.cropName.equals(other.cropName)) {
			return false;
		}
		return true;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
	public boolean isUseUUID() {
		return useUUID;
	}

	
	public void setUseUUID(boolean useUUID) {
		this.useUUID = useUUID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CropType [cropName=");
		builder.append(this.cropName);
		builder.append(", centralDbName=");
		builder.append(this.dbName);
		builder.append(", version=");
		builder.append(this.version);
		builder.append(", plotCodePrefix=");
		builder.append(this.plotCodePrefix);
		builder.append("]");
		return builder.toString();
	}

}
