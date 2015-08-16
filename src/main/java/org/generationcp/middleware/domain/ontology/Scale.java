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

package org.generationcp.middleware.domain.ontology;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.util.Debug;

/**
 * Extends {@link Term}
 */

public class Scale extends Term {

	/**
	 *
	 */
	private static final long serialVersionUID = -1600344025199591903L;
	private DataType dataType;

	private final List<TermSummary> categories = new ArrayList<>();

	private String minValue;
	private String maxValue;

	public Scale() {
		this.setVocabularyId(CvId.SCALES.getId());
	}

	public Scale(org.generationcp.middleware.domain.oms.Term term) {
		super(term);
		this.setVocabularyId(CvId.SCALES.getId());
	}

	public DataType getDataType() {
		return this.dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public List<TermSummary> getCategories() {
		return categories;
	}

	public void addCategory(TermSummary category){
		categories.add(category);
	}

	public String getMinValue() {
		return this.minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public String toString() {
		return "Scale{" + "dataType=" + this.dataType + ", categories=" + this.categories + ", minValue='" + this.minValue + '\''
				+ ", maxValue='" + this.maxValue + '\'' + "} " + super.toString();
	}

	@Override
	public void print(int indent) {
		Debug.println(indent, "Scale: ");
		super.print(indent + 3);
		if (this.dataType != null) {
			Debug.print(indent + 6, "DataType: " + this.getDataType());
		}

		if (this.categories != null) {
			Debug.println(indent + 3, "Categories: " + this.getCategories());
		}

		if (this.minValue != null) {
			Debug.print(indent + 3, "minValue: " + this.getMinValue());
		}

		if (this.maxValue != null) {
			Debug.print(indent + 3, "maxValue: " + this.getMaxValue());
		}
	}
}
