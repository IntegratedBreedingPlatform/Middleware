/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.domain.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.DataType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.util.Debug;
import java.util.HashMap;
import java.util.Map;
/**
 * Extends {@link OntologyTerm}
 */

public class OntologyScale extends OntologyTerm {

    private DataType dataType;
    private final Map<String, String> categories = new HashMap<>();

    private String minValue;
    private String maxValue;

    public OntologyScale() {
        this.setVocabularyId(CvId.SCALES.getId());
    }

    public OntologyScale(Term term) {
        super(term);
        this.setVocabularyId(CvId.SCALES.getId());
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void addCategory(String name, String description){
        this.categories.put(name, description);
    }

    @Override
    public String toString() {
        return "OntologyScale{" +
                "dataType=" + dataType +
                ", categories=" + categories +
                ", minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                "} " + super.toString();
    }

    @Override
    public void print(int indent) {
        Debug.println(indent, "Scale: ");
        super.print(indent + 3);
        if(dataType != null)
        {
            Debug.print(indent + 6, "DataType: " + this.getDataType());
        }

        if(categories != null){
            Debug.println(indent + 3, "Classes: " + this.getCategories());
        }

        if(minValue != null){
            Debug.print(indent + 3, "minValue: " + this.getMinValue());
        }

        if(maxValue != null){
            Debug.print(indent + 3, "maxValue: " + this.getMaxValue());
        }
    }
}
