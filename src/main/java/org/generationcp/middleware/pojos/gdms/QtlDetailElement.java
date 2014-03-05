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

/**
 * Placeholder POJO for QtlDetail Element.
 * 
 * @author Joyce Avestro
 * 
 */
public class QtlDetailElement implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String qtlName;
    private String mapName;
    private QtlDetails qtlDetails;
    private String tRName;
    private String ontology;
    
    public QtlDetailElement() {
    }


    /**
     * @param qtlName
     * @param mapName
     * @param qtlDetails
     * @param tRName
     * @param ontology
     */
    public QtlDetailElement(String qtlName, String mapName, QtlDetails qtlDetails, String tRName, String ontology) {
        this.qtlName = qtlName;
        this.mapName = mapName;
        this.qtlDetails = qtlDetails;
        this.tRName = tRName;
        this.ontology = ontology;
    }
    
    public String getQtlName() {
        return qtlName;
    }
    
    public void setQtlName(String qtlName) {
        this.qtlName = qtlName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public QtlDetails getQtlDetails() {
        return qtlDetails;
    }
    
    public void setQtlDetails(QtlDetails qtlDetails) {
        this.qtlDetails = qtlDetails;
    }

    public String getChromosome() {
        return qtlDetails.getLinkageGroup();
    }
    
    public void setChromosome(String chromosome) {
        qtlDetails.setLinkageGroup(chromosome);
    }
    
    public Float getMinPosition() {
        return qtlDetails.getMinPosition();
    }
    
    public void setMinPosition(Float minPosition) {
        qtlDetails.setMinPosition(minPosition);
    }
    
    public Float getMaxPosition() {
        return qtlDetails.getMaxPosition();
    }

    public void setMaxPosition(Float maxPosition) {
        qtlDetails.setMaxPosition(maxPosition);
    }
    
    public Integer getTraitId() {
        return qtlDetails.getTraitId();
    }

    public void setTraitId(Integer traitId) {
        qtlDetails.setTraitId(traitId);
    }
    
    public String getExperiment() {
        return qtlDetails.getExperiment();
    }

    public void setExperiment(String experiment) {
        qtlDetails.setExperiment(experiment);
    }

    public String getLeftFlankingMarker() {
        return qtlDetails.getLeftFlankingMarker();
    }
    
    public void setLeftFlankingMarker(String leftFlankingMarker) {
        qtlDetails.setLeftFlankingMarker(leftFlankingMarker);
    }
    
    public String getRightFlankingMarker() {
        return qtlDetails.getRightFlankingMarker();
    }
    
    public void setRightFlankingMarker(String rightFlankingMarker) {
        qtlDetails.setRightFlankingMarker(rightFlankingMarker);
    }
    
    public Integer getEffect() {
        return qtlDetails.getEffect();
    }
    
    public void setEffect(Integer effect) {
        qtlDetails.setEffect(effect);
    }
    
    public Float getScoreValue() {
        return qtlDetails.getScoreValue();
    }
    
    public void setScoreValue(Float scoreValue) {
        qtlDetails.setScoreValue(scoreValue);
    }
    
    public Float getrSquare() {
        return qtlDetails.getrSquare();
    }
    
    public void setrSquare(Float rSquare) {
        qtlDetails.setrSquare(rSquare);
    }
    
    public String getInteractions() {
        return qtlDetails.getInteractions();
    }
    
    public void setInteractions(String interactions) {
        qtlDetails.setInteractions(interactions);
    }
    
    public Float getPosition() {
        return qtlDetails.getPosition();
    }
    
    public void setPosition(Float position) {
        qtlDetails.setPosition(position);
    }
    
    public Float getClen() {
        return qtlDetails.getClen();
    }
    
    public void setClen(Float clen) {
        qtlDetails.setClen(clen);
    }
    
    public String getSeAdditive() {
        return qtlDetails.getSeAdditive();
    }
    
    public void setSeAdditive(String seAdditive) {
        qtlDetails.setSeAdditive(seAdditive);
    }
    
    public String getHvParent() {
        return qtlDetails.getHvParent();
    }

    public void setHvParent(String hvParent) {
        qtlDetails.setHvParent(hvParent);
    }
    
    public String getHvAllele() {
        return qtlDetails.getHvAllele();
    }
    
    public void setHvAllele(String hvAllele) {
        qtlDetails.setHvAllele(hvAllele);
    }
    
    public String getLvParent() {
        return qtlDetails.getLvParent();
    }
    
    public void setLvParent(String lvParent) {
        qtlDetails.setLvParent(lvParent);
    }

    public String getLvAllele() {
        return qtlDetails.getLvAllele();
    }
    
    public void setLvAllele(String lvAllele) {
        qtlDetails.setLvAllele(lvAllele);
    }
    
    public String gettRName() {
        return tRName;
    }
    
    public void settRName(String tRName) {
        this.tRName = tRName;
    }
    
    public String getOntology() {
        return ontology;
    }
    
    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result + ((ontology == null) ? 0 : ontology.hashCode());
        result = prime * result + ((qtlDetails == null) ? 0 : qtlDetails.hashCode());
        result = prime * result + ((qtlName == null) ? 0 : qtlName.hashCode());
        result = prime * result + ((tRName == null) ? 0 : tRName.hashCode());
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
        QtlDetailElement other = (QtlDetailElement) obj;
        if (mapName == null) {
            if (other.mapName != null)
                return false;
        } else if (!mapName.equals(other.mapName))
            return false;
        if (ontology == null) {
            if (other.ontology != null)
                return false;
        } else if (!ontology.equals(other.ontology))
            return false;
        if (qtlDetails == null) {
            if (other.qtlDetails != null)
                return false;
        } else if (!qtlDetails.equals(other.qtlDetails))
            return false;
        if (qtlName == null) {
            if (other.qtlName != null)
                return false;
        } else if (!qtlName.equals(other.qtlName))
            return false;
        if (tRName == null) {
            if (other.tRName != null)
                return false;
        } else if (!tRName.equals(other.tRName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QtlDetailElement [qtlName=");
        builder.append(qtlName);
        builder.append(", mapName=");
        builder.append(mapName);
        builder.append(", chromosome=");
        builder.append(getChromosome());
        builder.append(", minPosition=");
        builder.append(getMinPosition());
        builder.append(", maxPosition=");
        builder.append(getMaxPosition());
        builder.append(", traitId=");
        builder.append(getTraitId());
        builder.append(", experiment=");
        builder.append(getExperiment());
        builder.append(", leftFlankingMarker=");
        builder.append(getLeftFlankingMarker());
        builder.append(", rightFlankingMarker=");
        builder.append(getRightFlankingMarker());
        builder.append(", effect=");
        builder.append(getEffect());
        builder.append(", scoreValue=");
        builder.append(getScoreValue());
        builder.append(", rSquare=");
        builder.append(getrSquare());
        builder.append(", interactions=");
        builder.append(getInteractions());
        builder.append(", position=");
        builder.append(getPosition());
        builder.append(", clen=");
        builder.append(getClen());
        builder.append(", seAdditive=");
        builder.append(getSeAdditive());
        builder.append(", hvParent=");
        builder.append(getHvParent());
        builder.append(", hvAllele=");
        builder.append(getHvAllele());
        builder.append(", lvParent=");
        builder.append(getLvParent());
        builder.append(", lvAllele=");
        builder.append(getLvAllele());
        builder.append(", tRName=");
        builder.append(tRName);
        builder.append(", ontology=");
        builder.append(ontology);
        builder.append("]");
        return builder.toString();
    }    
    
}
