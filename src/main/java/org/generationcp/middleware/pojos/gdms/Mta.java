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
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for the gdms_mta table.
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_mta")
public class Mta implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String GET_MTAS_BY_TRAIT = 
            "SELECT mta_id "
            + "     ,marker_id "
            + "     ,dataset_id "
            + "     ,map_id "
            + "     ,linkage_group "
            + "     ,position "
            + "     ,tid "
            + "     ,effect "
            + "     ,CONCAT(hv_allele, '') "
            + "     ,CONCAT(experiment, '') "
            + "     ,score_value "
            + "     ,r_square "
            + "FROM gdms_mta "
            + "WHERE tid = :traitId ";

    @Id
    @Basic(optional = false)
    @Column(name = "mta_id")
    private Integer mtaId;
    
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;
    
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;
    
    @Basic(optional = false)
    @Column(name = "map_id")
    private Integer mapId;
    
    @Column(name = "linkage_group")
    private String linkageGroup;
    
    @Column(name = "position")
    private Float position;
    
    @Column(name = "tid")
    private Integer tId;

    @Column(name = "effect")
    private Integer effect;
    
    @Column(name = "hv_allele", columnDefinition = "char(20)")
    private String hvAllele; 

    @Column(name = "experiment", columnDefinition = "char(100)")
    private String experiment; 

    @Column(name = "score_value")
    private Float scoreValue;

    @Column(name = "r_square")
    private Float rSquare;

    public Mta() {
    }

    public Mta(Integer mtaId, Integer markerId, Integer datasetId,
            Integer mapId, String linkageGroup, Float position, Integer tId,
            Integer effect, String hvAllele, String experiment,
            Float scoreValue, Float rSquare) {
        super();
        this.mtaId = mtaId;
        this.markerId = markerId;
        this.datasetId = datasetId;
        this.mapId = mapId;
        this.linkageGroup = linkageGroup;
        this.position = position;
        this.tId = tId;
        this.effect = effect;
        this.hvAllele = hvAllele;
        this.experiment = experiment;
        this.scoreValue = scoreValue;
        this.rSquare = rSquare;
    }
    
    public Integer getMtaId() {
        return mtaId;
    }
    
    public void setMtaId(Integer mtaId) {
        this.mtaId = mtaId;
    }
    
    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public Integer getMapId() {
        return mapId;
    }
    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }
    
    public String getLinkageGroup() {
        return linkageGroup;
    }
    
    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }
    
    public Float getPosition() {
        return position;
    }

    public void setPosition(Float position) {
        this.position = position;
    }
    
    public Integer gettId() {
        return tId;
    }
    
    public void settId(Integer tId) {
        this.tId = tId;
    }

    public Integer getEffect() {
        return effect;
    }
    
    public void setEffect(Integer effect) {
        this.effect = effect;
    }

    public String getHvAllele() {
        return hvAllele;
    }

    public void setHvAllele(String hvAllele) {
        this.hvAllele = hvAllele;
    }

    public String getExperiment() {
        return experiment;
    }
    
    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public Float getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Float scoreValue) {
        this.scoreValue = scoreValue;
    }
    
    public Float getrSquare() {
        return rSquare;
    }
    
    public void setrSquare(Float rSquare) {
        this.rSquare = rSquare;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((datasetId == null) ? 0 : datasetId.hashCode());
        result = prime * result + ((effect == null) ? 0 : effect.hashCode());
        result = prime * result
                + ((experiment == null) ? 0 : experiment.hashCode());
        result = prime * result
                + ((hvAllele == null) ? 0 : hvAllele.hashCode());
        result = prime * result
                + ((linkageGroup == null) ? 0 : linkageGroup.hashCode());
        result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
        result = prime * result
                + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result + ((mtaId == null) ? 0 : mtaId.hashCode());
        result = prime * result
                + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((rSquare == null) ? 0 : rSquare.hashCode());
        result = prime * result
                + ((scoreValue == null) ? 0 : scoreValue.hashCode());
        result = prime * result + ((tId == null) ? 0 : tId.hashCode());
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
        Mta other = (Mta) obj;
        if (datasetId == null) {
            if (other.datasetId != null)
                return false;
        } else if (!datasetId.equals(other.datasetId))
            return false;
        if (effect == null) {
            if (other.effect != null)
                return false;
        } else if (!effect.equals(other.effect))
            return false;
        if (experiment == null) {
            if (other.experiment != null)
                return false;
        } else if (!experiment.equals(other.experiment))
            return false;
        if (hvAllele == null) {
            if (other.hvAllele != null)
                return false;
        } else if (!hvAllele.equals(other.hvAllele))
            return false;
        if (linkageGroup == null) {
            if (other.linkageGroup != null)
                return false;
        } else if (!linkageGroup.equals(other.linkageGroup))
            return false;
        if (mapId == null) {
            if (other.mapId != null)
                return false;
        } else if (!mapId.equals(other.mapId))
            return false;
        if (markerId == null) {
            if (other.markerId != null)
                return false;
        } else if (!markerId.equals(other.markerId))
            return false;
        if (mtaId == null) {
            if (other.mtaId != null)
                return false;
        } else if (!mtaId.equals(other.mtaId))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (rSquare == null) {
            if (other.rSquare != null)
                return false;
        } else if (!rSquare.equals(other.rSquare))
            return false;
        if (scoreValue == null) {
            if (other.scoreValue != null)
                return false;
        } else if (!scoreValue.equals(other.scoreValue))
            return false;
        if (tId == null) {
            if (other.tId != null)
                return false;
        } else if (!tId.equals(other.tId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Mta [mtaId=");
        builder.append(mtaId);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append(", datasetId=");
        builder.append(datasetId);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", position=");
        builder.append(position);
        builder.append(", tId=");
        builder.append(tId);
        builder.append(", effect=");
        builder.append(effect);
        builder.append(", hvAllele=");
        builder.append(hvAllele);
        builder.append(", experiment=");
        builder.append(experiment);
        builder.append(", scoreValue=");
        builder.append(scoreValue);
        builder.append(", rSquare=");
        builder.append(rSquare);
        builder.append("]");
        return builder.toString();
    }
    
}
