package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GenotypeData {

    private Integer genotypeId;

    private String variableName;

    private Integer variableId;

    private String value;

    private Integer datasetId;

    public GenotypeData() {

    }

    public GenotypeData(final Integer variableId, final String variableName, final String value) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.value = value;
    }
    public Integer getGenotypeId() {
        return genotypeId;
    }

    public void setGenotypeId(Integer genotypeId) {
        this.genotypeId = genotypeId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Integer getVariableId() {
        return variableId;
    }

    public void setVariableId(Integer variableId) {
        this.variableId = variableId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }
}
