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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "level_n")
public class NumericLevel implements Serializable{

    private static final long serialVersionUID = 2864832968167403818L;

    public static final String GET_BY_OUNIT_ID_LIST = 
            "SELECT oi.ounitid, oi.factorid, f.fname, ln.lvalue " +
            "FROM oindex oi JOIN obsunit ou ON oi.ounitid = ou.ounitid " +
                            "JOIN level_n ln ON ln.factorid = oi.factorid " +
                                            "AND ln.levelno = oi.levelno " + 
                            "JOIN factor f ON f.labelid = ln.labelid " +
            "WHERE oi.ounitid IN (:ounitIdList)";
    
    public static final String GET_CONDITION_AND_VALUE = 
            "SELECT f.fname, ln.lvalue, f.traitid, f.scaleid, f.tmethid, f.ltype " +
            "FROM factor f JOIN level_n ln ON f.labelid = ln.labelid " +
            "WHERE ln.factorid = :factorid AND ln.levelno = :levelno";
    
    public static final String COUNT_STUDIES_BY_GID =
        "SELECT COUNT(DISTINCT s.studyid) "
        + "FROM oindex oi JOIN level_n ln ON ln.factorid = oi.factorid AND ln.levelno = oi.levelno "
                + "JOIN factor f ON f.labelid = ln.labelid "
                + "JOIN study s ON f.studyid = s.studyid "
        + "WHERE f.fname = 'GID' AND ln.lvalue = :gid";
    
    public static final String GET_STUDIES_BY_GID =
        "SELECT s.studyid, s.sname, s.title, s.objectiv, COUNT(DISTINCT oi.ounitid) " 
        + "FROM oindex oi JOIN level_n ln ON ln.factorid = oi.factorid AND ln.levelno = oi.levelno "
                + "JOIN factor f ON f.labelid = ln.labelid "
                + "JOIN study s ON f.studyid = s.studyid "
        + "WHERE f.fname = 'GID' AND ln.lvalue = :gid "
        + "GROUP BY s.studyid";
    
    public static final String GET_BY_FACTOR_AND_REPRESNO =
        "SELECT DISTINCT {ln.*} FROM level_n ln JOIN oindex oi ON ln.factorid = oi.factorid " 
        + "AND ln.levelno = oi.levelno "
        + "WHERE ln.factorid = :factorid AND ln.labelid = :labelid AND oi.represno = :represno";

    @EmbeddedId
    protected NumericLevelPK id;

    @Column(name = "lvalue")
    private Double value;

    public NumericLevel() {
    }

    public NumericLevel(NumericLevelPK id, Double value) {
        super();
        this.id = id;
        this.value = value;
    }

    public NumericLevelPK getId() {
        return id;
    }

    public void setId(NumericLevelPK id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumericLevel)) {
            return false;
        }

        NumericLevel rhs = (NumericLevel) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 67).append(id).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NumericLevel [id=");
        builder.append(id);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
