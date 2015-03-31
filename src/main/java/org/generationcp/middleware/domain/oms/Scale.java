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
package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.util.Debug;

import java.util.HashMap;
import java.util.Map;

public class Scale {

    private Term term;
    private DataType dataType;
    private final Map<String, String> categories = new HashMap<>();

    private String minValue;
    private String maxValue;


    private String displayName;
    
    public Scale() {
        this.term = new Term();
        this.term.setVocabularyId(CvId.SCALES.getId());
    }

    public Scale(Term term) {
        this.term = term;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public String getDisplayName() {
        if (displayName == null) {
            return term.getName();
        } else {
            return displayName;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return term.getId();
    }

    public void setId(int id) {
        term.setId(id);
    }

    public String getName() {
        return term.getName();
    }

    public void setName(String name) {
        term.setName(name);
    }

    public String getDefinition() {
        return term.getDefinition();
    }

    public void setDefinition(String definition) {
        term.setDefinition(definition);
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

    public void addCategory(String name, String description){
        this.categories.put(name, description);
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

    @Override
    public String toString() {

        if (term == null){
            return "";
        }

        return "Scale [id=" + term.getId() + ", name=" + term.getName() + ", definition=" + term.getDefinition() + "]";
    }

    public void print(int indent) {
        Debug.println(indent, "Scale: ");
        if (term != null){
            term.print(indent + 3);
        } else {
            Debug.println(indent + 3, "null");
        }
    }

}