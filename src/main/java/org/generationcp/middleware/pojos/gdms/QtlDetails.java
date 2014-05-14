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
 * POJO for gdms_qtl_details table
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_qtl_details")
public class QtlDetails implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "qtl_id")
    private Integer qtlId;

    @Basic(optional = false)
    @Column(name = "map_id")
    private Integer mapId;

    @Column(name = "min_position")
    private Float minPosition;
    
    @Column(name = "max_position")
    private Float maxPosition;
    
    @Column(name = "tid")
    private Integer traitId;
    
    @Column(name = "experiment")
    private String experiment;

    @Column(name = "effect")
    private Float effect;

    @Column(name = "score_value")
    private Float scoreValue;

    @Column(name = "r_square")
    private Float rSquare;

    @Column(name = "linkage_group")
    private String linkageGroup;

    @Column(name = "interactions")
    private String interactions;

    @Column(name = "left_flanking_marker")
    private String leftFlankingMarker;

    @Column(name = "right_flanking_marker")
    private String rightFlankingMarker;

    @Column(name = "position")
    private Float position;

    @Column(name = "clen")
    private Float clen;

    @Column(name = "se_additive")
    private String seAdditive;

    @Column(name = "hv_parent")
    private String hvParent;

    @Column(name = "hv_allele")
    private String hvAllele;

    @Column(name = "lv_parent")
    private String lvParent;

    @Column(name = "lv_allele")
    private String lvAllele;
    

    public QtlDetails(){
        
    }

    public QtlDetails(Integer qtlId, Integer mapId, Float minPosition, Float maxPosition, Integer traitId, String experiment, Float effect,
            Float scoreValue, Float rSquare, String linkageGroup, String interactions, String leftFlankingMarker,
            String rightFlankingMarker, Float position, Float clen, String seAdditive, String hvParent, String hvAllele, String lvParent,
            String lvAllele) {
        super();
        this.qtlId = qtlId;
        this.mapId = mapId;
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
        this.traitId = traitId;
        this.experiment = experiment;
        this.effect = effect;
        this.scoreValue = scoreValue;
        this.rSquare = rSquare;
        this.linkageGroup = linkageGroup;
        this.interactions = interactions;
        this.leftFlankingMarker = leftFlankingMarker;
        this.rightFlankingMarker = rightFlankingMarker;
        this.position = position;
        this.clen = clen;
        this.seAdditive = seAdditive;
        this.hvParent = hvParent;
        this.hvAllele = hvAllele;
        this.lvParent = lvParent;
        this.lvAllele = lvAllele;
    }

    public Integer getQtlId() {
        return this.qtlId;
    }

    public void setQtlId(Integer qtlId) {
        this.qtlId = qtlId;
    }

    public Integer getMapId() {
        return this.mapId;
    }
    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }
        
    public Float getMinPosition() {
        return minPosition;
    }
    
    public void setMinPosition(Float minPosition) {
        this.minPosition = minPosition;
    }

    public Float getMaxPosition() {
        return maxPosition;
    }
    
    public void setMaxPosition(Float maxPosition) {
        this.maxPosition = maxPosition;
    }
    
    public Integer getTraitId() {
        return traitId;
    }
    
    public void setTraitId(Integer traitId) {
        this.traitId = traitId;
    }
    
    public String getExperiment() {
        return experiment;
    }
    
    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }
    
    public Float getEffect() {
        return effect;
    }
    
    public void setEffect(Float effect) {
        this.effect = effect;
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
    
    public String getLinkageGroup() {
        return linkageGroup;
    }
    
    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }
    
    public String getInteractions() {
        return interactions;
    }
    
    public void setInteractions(String interactions) {
        this.interactions = interactions;
    }
    
    public String getLeftFlankingMarker() {
        return leftFlankingMarker;
    }
    
    public void setLeftFlankingMarker(String leftFlankingMarker) {
        this.leftFlankingMarker = leftFlankingMarker;
    }
    
    public String getRightFlankingMarker() {
        return rightFlankingMarker;
    }
    
    public void setRightFlankingMarker(String rightFlankingMarker) {
        this.rightFlankingMarker = rightFlankingMarker;
    }
    
    public Float getPosition() {
        return position;
    }
    
    public void setPosition(Float position) {
        this.position = position;
    }
    
    public Float getClen() {
        return clen;
    }
    
    public void setClen(Float clen) {
        this.clen = clen;
    }
    
    public String getSeAdditive() {
        return seAdditive;
    }

    public void setSeAdditive(String seAdditive) {
        this.seAdditive = seAdditive;
    }

    public String getHvParent() {
        return hvParent;
    }
    
    public void setHvParent(String hvParent) {
        this.hvParent = hvParent;
    }

    public String getHvAllele() {
        return hvAllele;
    }
    
    public void setHvAllele(String hvAllele) {
        this.hvAllele = hvAllele;
    }
    
    public String getLvParent() {
        return lvParent;
    }
    
    public void setLvParent(String lvParent) {
        this.lvParent = lvParent;
    }
    
    public String getLvAllele() {
        return lvAllele;
    }
    
    public void setLvAllele(String lvAllele) {
        this.lvAllele = lvAllele;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clen == null) ? 0 : clen.hashCode());
        result = prime * result + ((effect == null) ? 0 : effect.hashCode());
        result = prime * result + ((experiment == null) ? 0 : experiment.hashCode());
        result = prime * result + ((hvAllele == null) ? 0 : hvAllele.hashCode());
        result = prime * result + ((hvParent == null) ? 0 : hvParent.hashCode());
        result = prime * result + ((qtlId == null) ? 0 : qtlId.hashCode());
        result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
        result = prime * result + ((interactions == null) ? 0 : interactions.hashCode());
        result = prime * result + ((leftFlankingMarker == null) ? 0 : leftFlankingMarker.hashCode());
        result = prime * result + ((linkageGroup == null) ? 0 : linkageGroup.hashCode());
        result = prime * result + ((lvAllele == null) ? 0 : lvAllele.hashCode());
        result = prime * result + ((lvParent == null) ? 0 : lvParent.hashCode());
        result = prime * result + ((maxPosition == null) ? 0 : maxPosition.hashCode());
        result = prime * result + ((minPosition == null) ? 0 : minPosition.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((rSquare == null) ? 0 : rSquare.hashCode());
        result = prime * result + ((rightFlankingMarker == null) ? 0 : rightFlankingMarker.hashCode());
        result = prime * result + ((scoreValue == null) ? 0 : scoreValue.hashCode());
        result = prime * result + ((seAdditive == null) ? 0 : seAdditive.hashCode());
        result = prime * result + ((traitId == null) ? 0 : traitId.hashCode());
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
        QtlDetails other = (QtlDetails) obj;
        if (clen == null) {
            if (other.clen != null)
                return false;
        } else if (!clen.equals(other.clen))
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
        if (hvParent == null) {
            if (other.hvParent != null)
                return false;
        } else if (!hvParent.equals(other.hvParent))
            return false;
        if (qtlId == null) {
            if (other.qtlId != null)
                return false;
        } else if (!qtlId.equals(other.qtlId))
            return false;
        if (mapId == null) {
            if (other.mapId != null)
                return false;
        } else if (!mapId.equals(other.mapId))
            return false;
        if (interactions == null) {
            if (other.interactions != null)
                return false;
        } else if (!interactions.equals(other.interactions))
            return false;
        if (leftFlankingMarker == null) {
            if (other.leftFlankingMarker != null)
                return false;
        } else if (!leftFlankingMarker.equals(other.leftFlankingMarker))
            return false;
        if (linkageGroup == null) {
            if (other.linkageGroup != null)
                return false;
        } else if (!linkageGroup.equals(other.linkageGroup))
            return false;
        if (lvAllele == null) {
            if (other.lvAllele != null)
                return false;
        } else if (!lvAllele.equals(other.lvAllele))
            return false;
        if (lvParent == null) {
            if (other.lvParent != null)
                return false;
        } else if (!lvParent.equals(other.lvParent))
            return false;
        if (maxPosition == null) {
            if (other.maxPosition != null)
                return false;
        } else if (!maxPosition.equals(other.maxPosition))
            return false;
        if (minPosition == null) {
            if (other.minPosition != null)
                return false;
        } else if (!minPosition.equals(other.minPosition))
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
        if (rightFlankingMarker == null) {
            if (other.rightFlankingMarker != null)
                return false;
        } else if (!rightFlankingMarker.equals(other.rightFlankingMarker))
            return false;
        if (scoreValue == null) {
            if (other.scoreValue != null)
                return false;
        } else if (!scoreValue.equals(other.scoreValue))
            return false;
        if (seAdditive == null) {
            if (other.seAdditive != null)
                return false;
        } else if (!seAdditive.equals(other.seAdditive))
            return false;
        if (traitId == null) {
            if (other.traitId != null)
                return false;
        } else if (!traitId.equals(other.traitId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QtlDetails [qtlId=");
        builder.append(qtlId);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append(", minPosition=");
        builder.append(minPosition);
        builder.append(", maxPosition=");
        builder.append(maxPosition);
        builder.append(", traitId=");
        builder.append(traitId);
        builder.append(", experiment=");
        builder.append(experiment);
        builder.append(", effect=");
        builder.append(effect);
        builder.append(", scoreValue=");
        builder.append(scoreValue);
        builder.append(", rSquare=");
        builder.append(rSquare);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", interactions=");
        builder.append(interactions);
        builder.append(", leftFlankingMarker=");
        builder.append(leftFlankingMarker);
        builder.append(", rightFlankingMarker=");
        builder.append(rightFlankingMarker);
        builder.append(", position=");
        builder.append(position);
        builder.append(", clen=");
        builder.append(clen);
        builder.append(", seAdditive=");
        builder.append(seAdditive);
        builder.append(", hvParent=");
        builder.append(hvParent);
        builder.append(", hvAllele=");
        builder.append(hvAllele);
        builder.append(", lvParent=");
        builder.append(lvParent);
        builder.append(", lvAllele=");
        builder.append(lvAllele);
        builder.append("]");
        return builder.toString();
    }
    

}
