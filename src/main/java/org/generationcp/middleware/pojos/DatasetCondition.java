/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.middleware.pojos;

import java.io.Serializable;

/**
 * Represents a condition of a dataset.
 *
 * The value can either be a String or a Double, depending on the type, if it is a "C" (String) or a "N" (Double).
 *
 * @author Kevin L. Manansala
 *
 */
public class DatasetCondition implements Serializable {

	private static final long serialVersionUID = 1883187407218392570L;

	private Integer factorId;
	private String name;
	private Object value;
	private Integer traitId;
	private Integer scaleId;
	private Integer methodId;
	private String type;

	public DatasetCondition(Integer factorId, String name, Object value, Integer traitId, Integer scaleId, Integer methodId, String type) {
		super();
		this.factorId = factorId;
		this.name = name;
		this.value = value;
		this.traitId = traitId;
		this.scaleId = scaleId;
		this.methodId = methodId;
		this.type = type;
	}

	public Integer getFactorId() {
		return this.factorId;
	}

	public void setFactorId(Integer factorId) {
		this.factorId = factorId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getTraitId() {
		return this.traitId;
	}

	public void setTraitId(Integer traitId) {
		this.traitId = traitId;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getMethodId() {
		return this.methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatasetCondition [factorId=");
		builder.append(this.factorId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", traitId=");
		builder.append(this.traitId);
		builder.append(", scaleId=");
		builder.append(this.scaleId);
		builder.append(", methodId=");
		builder.append(this.methodId);
		builder.append(", type=");
		builder.append(this.type);
		builder.append("]");
		return builder.toString();
	}

}
