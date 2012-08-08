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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NamedQueries({
        // SELECT * FROM represtn WHERE effectid = 1176
        @NamedQuery(name = "getRepresentationByEffectId", query = "SELECT r FROM Representation r WHERE r.effectId = :effectId"),
        @NamedQuery(name = "getRepresentationByStudyId",
                query = "SELECT r FROM Representation r, StudyEffect se WHERE r.effectId = se.effectId AND se.studyId = :studyId") })
@Entity
@Table(name = "represtn")
public class Representation implements Serializable{

    private static final long serialVersionUID = 3521757463492775303L;

    public static final String GET_REPRESENTATION_BY_EFFECT_ID = "getRepresentationByEffectId";
    public static final String GET_REPRESENTATION_BY_STUDY_ID = "getRepresentationByStudyId";

    @Id
    @Basic(optional = false)
    @Column(name = "represno")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "effectid")
    private Integer effectId;

    @Basic(optional = false)
    @Column(name = "represname")
    private String name;

    public Representation() {
    }

    public Representation(Integer id, Integer effectId, String name) {
        super();
        this.id = id;
        this.effectId = effectId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEffectId() {
        return effectId;
    }

    public void setEffectId(Integer effectId) {
        this.effectId = effectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Representation [id=" + id + ", effectId=" + effectId + ", name=" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Representation)) {
            return false;
        }

        Representation rhs = (Representation) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(35, 21).append(id).toHashCode();
    }
}
