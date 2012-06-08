package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ScaleDiscretePK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "scaleid")
    private Integer scaleId;

    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public ScaleDiscretePK() {
    }

    public ScaleDiscretePK(Integer scaleId, String value) {
	super();
	this.scaleId = scaleId;
	this.value = value;
    }

    public Integer getScaleId() {
	return scaleId;
    }

    public void setScaleId(Integer scaleId) {
	this.scaleId = scaleId;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((scaleId == null) ? 0 : scaleId.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ScaleDiscretePK other = (ScaleDiscretePK) obj;
	if (scaleId == null) {
	    if (other.scaleId != null)
		return false;
	} else if (!scaleId.equals(other.scaleId))
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "ScaleDiscretePK [scaleId=" + scaleId + ", value=" + value + "]";
    }

}
