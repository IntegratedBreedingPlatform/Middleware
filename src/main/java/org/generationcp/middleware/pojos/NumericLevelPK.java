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
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class NumericLevelPK implements Serializable{

    private static final long serialVersionUID = -8070038562792694453L;

    @Basic(optional = false)
    @Column(name = "labelid")
    private Integer labelId;

    @Basic(optional = false)
    @Column(name = "factorid")
    private Integer factorId;

    @Basic(optional = false)
    @Column(name = "levelno")
    private Integer levelNumber;

    public NumericLevelPK() {
    }

    public NumericLevelPK(Integer labelId, Integer factorId, Integer levelNumber) {
        super();
        this.labelId = labelId;
        this.factorId = factorId;
        this.levelNumber = levelNumber;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    public Integer getFactorId() {
        return factorId;
    }

    public void setFactorId(Integer factorId) {
        this.factorId = factorId;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumericLevelPK)) {
            return false;
        }

        NumericLevelPK rhs = (NumericLevelPK) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(labelId, rhs.labelId).append(factorId, rhs.factorId)
                .append(levelNumber, rhs.levelNumber).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 69).append(labelId).append(factorId).append(levelNumber).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NumericLevelPK [labelId=");
        builder.append(labelId);
        builder.append(", factorId=");
        builder.append(factorId);
        builder.append(", levelNumber=");
        builder.append(levelNumber);
        builder.append("]");
        return builder.toString();
    }

}
