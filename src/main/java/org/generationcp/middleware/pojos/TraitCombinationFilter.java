package org.generationcp.middleware.pojos;

import java.io.Serializable;

public class TraitCombinationFilter implements Serializable {
    private Integer traitId;
    private Integer scaleId;
    private Integer methodId;
    private Object value;

    public TraitCombinationFilter(Integer traitId, Integer scaleId,
	    Integer methodId, Object value) {
	super();
	this.traitId = traitId;
	this.scaleId = scaleId;
	this.methodId = methodId;
	this.value = value;
    }

    public Integer getTraitId() {
	return traitId;
    }

    public void setTraitId(Integer traitId) {
	this.traitId = traitId;
    }

    public Integer getScaleId() {
	return scaleId;
    }

    public void setScaleId(Integer scaleId) {
	this.scaleId = scaleId;
    }

    public Integer getMethodId() {
	return methodId;
    }

    public void setMethodId(Integer methodId) {
	this.methodId = methodId;
    }

    public Object getValue() {
	return value;
    }

    public void setValue(Object value) {
	this.value = value;
    }

}
