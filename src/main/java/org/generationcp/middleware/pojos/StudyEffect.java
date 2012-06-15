/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NamedQueries({ @NamedQuery(name = "getStudyEffectsByStudyID", query = "FROM StudyEffect se WHERE se.studyId = :studyId") })
@Entity
@Table(name = "steffect")
public class StudyEffect implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String GET_STUDY_EFFECTS_BY_STUDYID = "getStudyEffectsByStudyID";

    @EmbeddedId
    private StudyEffectPK id;

    @Basic(optional = false)
    @Column(name = "effectname")
    private String name;

    private Integer studyId;

    public StudyEffect() {
    }

    public StudyEffect(StudyEffectPK id, String name) {
	super();
	this.id = id;
	this.name = name;
	this.studyId = (id != null ? id.getStudyId() : null);
    }

    public StudyEffectPK getId() {
	return id;
    }

    public void setId(StudyEffectPK id) {
	this.id = id;
	this.studyId = (id != null ? id.getStudyId() : null);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getStudyId() {
	if (id != null) {
	    return id.getStudyId();
	}
	return null;
    }

    public void setStudyId(Integer studyId) {
	if (id != null) {
	    id.setStudyId(studyId);
	    this.studyId = studyId;
	}
    }

    @Override
    public String toString() {
	return "StudyEffect [id=" + id + ", name=" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!(obj instanceof StudyEffect))
	    return false;

	StudyEffect rhs = (StudyEffect) obj;
	return new EqualsBuilder().appendSuper(super.equals(obj))
		.append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(13, 79).append(id).toHashCode();
    }

}
