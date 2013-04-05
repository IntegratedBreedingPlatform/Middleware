/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scalecon")
public class ScaleContinuous implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "scaleid")
    private Integer scaleId;

    @Basic(optional = false)
    @Column(name = "slevel")
    private Double start;

    @Basic(optional = false)
    @Column(name = "elevel")
    private Double end;

    public ScaleContinuous() {
    }

    public ScaleContinuous(Integer scaleId) {
        super();
        this.scaleId = scaleId;
    }

    public ScaleContinuous(Integer scaleId, Double start, Double end) {
        super();
        this.scaleId = scaleId;
        this.start = start;
        this.end = end;
    }

    public Integer getScaleId() {
        return scaleId;
    }

    public void setScaleId(Integer scaleId) {
        this.scaleId = scaleId;
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getEnd() {
        return end;
    }

    public void setEnd(Double end) {
        this.end = end;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScaleContinuous [scaleId=");
        builder.append(scaleId);
        builder.append(", start=");
        builder.append(start);
        builder.append(", end=");
        builder.append(end);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (scaleId == null ? 0 : scaleId.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        ScaleContinuous other = (ScaleContinuous) obj;
        if (scaleId == null) {
            if (other.scaleId != null) {
                return false;
            }
        } else if (!scaleId.equals(other.scaleId)) {
            return false;
        }
        return true;
    }

}
