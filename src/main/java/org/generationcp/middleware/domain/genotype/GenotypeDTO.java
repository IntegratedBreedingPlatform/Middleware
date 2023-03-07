package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GenotypeDTO {

    private Integer genotypeId;

    private Integer gid;

    private String designation;

    private Integer plotNumber;

    private Integer sampleNo;

    private String sampleName;

    private String variableName;

    private String value;

    public Integer getGenotypeId() {
        return genotypeId;
    }

    public void setGenotypeId(Integer genotypeId) {
        this.genotypeId = genotypeId;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(Integer plotNumber) {
        this.plotNumber = plotNumber;
    }

    public Integer getSampleNo() {
        return sampleNo;
    }

    public void setSampleNo(Integer sampleNo) {
        this.sampleNo = sampleNo;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
