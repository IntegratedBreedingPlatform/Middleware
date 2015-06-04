/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

/**
 * POJO corresponding to Mapping Allelic SNP Genotyping data row used in setMappingAllelicSNP.
 *
 */
public class MappingAllelicSNPRow {

	private AccMetadataSet accMetadataSet;

	private MappingPopValues mappingPopValues;

	private CharValues charValues;

	public MappingAllelicSNPRow() {
	}

	public MappingAllelicSNPRow(AccMetadataSet accMetadataSet, MappingPopValues mappingPopValues, CharValues charValues) {
		this.accMetadataSet = accMetadataSet;
		this.mappingPopValues = mappingPopValues;
		this.charValues = charValues;
	}

	public MappingAllelicSNPRow(Marker marker, AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet,
			MappingPopValues mappingPopValues, CharValues charValues) {
		this.accMetadataSet = accMetadataSet;
		this.mappingPopValues = mappingPopValues;
		this.charValues = charValues;
	}

	public AccMetadataSet getAccMetadataSet() {
		return this.accMetadataSet;
	}

	public void setAccMetadataSet(AccMetadataSet accMetadataSet) {
		this.accMetadataSet = accMetadataSet;
	}

	public MappingPopValues getMappingPopValues() {
		return this.mappingPopValues;
	}

	public void setMappingPopValues(MappingPopValues mappingPopValues) {
		this.mappingPopValues = mappingPopValues;
	}

	public CharValues getCharValues() {
		return this.charValues;
	}

	public void setCharValues(CharValues charValues) {
		this.charValues = charValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.accMetadataSet == null ? 0 : this.accMetadataSet.hashCode());
		result = prime * result + (this.mappingPopValues == null ? 0 : this.mappingPopValues.hashCode());
		result = prime * result + (this.charValues == null ? 0 : this.charValues.hashCode());
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
		MappingAllelicSNPRow other = (MappingAllelicSNPRow) obj;
		if (this.accMetadataSet == null) {
			if (other.accMetadataSet != null) {
				return false;
			}
		} else if (!this.accMetadataSet.equals(other.accMetadataSet)) {
			return false;
		}
		if (this.mappingPopValues == null) {
			if (other.mappingPopValues != null) {
				return false;
			}
		} else if (!this.mappingPopValues.equals(other.mappingPopValues)) {
			return false;
		}
		if (this.charValues == null) {
			if (other.charValues != null) {
				return false;
			}
		} else if (!this.charValues.equals(other.charValues)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingAllelicSNPRow [accMetadataSet=");
		builder.append(this.accMetadataSet);
		builder.append(", mappingPopValues=");
		builder.append(this.mappingPopValues);
		builder.append(", charValues=");
		builder.append(this.charValues);
		builder.append("]");
		return builder.toString();
	}

}
