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
public class StudyEffectPK implements Serializable{

    private static final long serialVersionUID = -2102480538369140848L;

    @Basic(optional = false)
    @Column(name = "effectid")
    private Integer effectId;

    @Basic(optional = false)
    @Column(name = "studyid")
    private Integer studyId;

    public StudyEffectPK() {
    }

    public StudyEffectPK(Integer effectId, Integer studyId) {
        super();
        this.effectId = effectId;
        this.studyId = studyId;
    }

    public Integer getEffectId() {
        return effectId;
    }

    public void setEffectId(Integer effectId) {
        this.effectId = effectId;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public String toString() {
        return "StudyEffectPK [effectId=" + effectId + ", studyId=" + studyId + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StudyEffectPK)) {
            return false;
        }

        StudyEffectPK rhs = (StudyEffectPK) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(effectId, rhs.effectId).append(studyId, rhs.studyId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(effectId).append(studyId).toHashCode();
    }
}
