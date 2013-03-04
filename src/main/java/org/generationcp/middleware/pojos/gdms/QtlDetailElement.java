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
 * 
 * @author Joyce Avestro
 * 
 */
public class QtlDetailElement implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String qtlName;
    private String mapName;
    private String chromosome;
    private Float minPosition;
    private Float maxPosition;
    private String trait;
    private String experiment;
    private String leftFlankingMarker;
    private String rightFlankingMarker;
    private Integer effect;
    private Float scoreValue;
    private Float rSquare;
    private String interactions;
    private String tRName;
    private String ontology;
    
    public QtlDetailElement() {
    }


    public QtlDetailElement(String qtlName, String mapName, String chromosome, float minPosition, Float maxPosition, String trait,
            String experiment, String leftFlankingMarker, String rightFlankingMarker, Integer effect, Float scoreValue, Float rSquare,
            String interactions, String tRName, String ontology) {
        super();
        this.qtlName = qtlName;
        this.mapName = mapName;
        this.chromosome = chromosome;
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
        this.trait = trait;
        this.experiment = experiment;
        this.leftFlankingMarker = leftFlankingMarker;
        this.rightFlankingMarker = rightFlankingMarker;
        this.effect = effect;
        this.scoreValue = scoreValue;
        this.rSquare = rSquare;
        this.interactions = interactions;
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
    
    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
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
    
    public String getTrait() {
        return trait;
    }
    
    public void setTrait(String trait) {
        this.trait = trait;
    }
    
    public String getExperiment() {
        return experiment;
    }
    
    public void setExperiment(String experiment) {
        this.experiment = experiment;
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
    
    public Integer getEffect() {
        return effect;
    }
    
    public void setEffect(Integer effect) {
        this.effect = effect;
    }
    
    public Float getScoreValue() {
        return scoreValue;
    }
    
    public void setScoreValue(Float scoreValue) {
        this.scoreValue = scoreValue;
    }
    
    public Float getRSquare() {
        return rSquare;
    }
    
    public void setRSquare(Float rSquare) {
        this.rSquare = rSquare;
    }

    public String getInteractions() {
        return interactions;
    }
    
    public void setInteractions(String interactions) {
        this.interactions = interactions;
    }

    public String getTRName() {
        return tRName;
    }
    
    public void setTRName(String tRName) {
        this.tRName = tRName;
    }

    public String getOntology() {
        return ontology;
    }
    
    public void setOntology(String ontology) {
        this.ontology = ontology;
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
        if (chromosome == null) {
            if (other.chromosome != null)
                return false;
        } else if (!chromosome.equals(other.chromosome))
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
        if (mapName == null) {
            if (other.mapName != null)
                return false;
        } else if (!mapName.equals(other.mapName))
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
        if (ontology == null) {
            if (other.ontology != null)
                return false;
        } else if (!ontology.equals(other.ontology))
            return false;
        if (qtlName == null) {
            if (other.qtlName != null)
                return false;
        } else if (!qtlName.equals(other.qtlName))
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
        if (tRName == null) {
            if (other.tRName != null)
                return false;
        } else if (!tRName.equals(other.tRName))
            return false;
        if (trait == null) {
            if (other.trait != null)
                return false;
        } else if (!trait.equals(other.trait))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chromosome == null) ? 0 : chromosome.hashCode());
        result = prime * result + ((effect == null) ? 0 : effect.hashCode());
        result = prime * result + ((experiment == null) ? 0 : experiment.hashCode());
        result = prime * result + ((interactions == null) ? 0 : interactions.hashCode());
        result = prime * result + ((leftFlankingMarker == null) ? 0 : leftFlankingMarker.hashCode());
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result + ((maxPosition == null) ? 0 : maxPosition.hashCode());
        result = prime * result + ((minPosition == null) ? 0 : minPosition.hashCode());
        result = prime * result + ((ontology == null) ? 0 : ontology.hashCode());
        result = prime * result + ((qtlName == null) ? 0 : qtlName.hashCode());
        result = prime * result + ((rSquare == null) ? 0 : rSquare.hashCode());
        result = prime * result + ((rightFlankingMarker == null) ? 0 : rightFlankingMarker.hashCode());
        result = prime * result + ((scoreValue == null) ? 0 : scoreValue.hashCode());
        result = prime * result + ((tRName == null) ? 0 : tRName.hashCode());
        result = prime * result + ((trait == null) ? 0 : trait.hashCode());
        return result;
    }

    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QtlDetail [qtlName=");
        builder.append(qtlName);
        builder.append(", mapName=");
        builder.append(mapName);
        builder.append(", chromosome=");
        builder.append(chromosome);
        builder.append(", minPosition=");
        builder.append(minPosition);
        builder.append(", maxPosition=");
        builder.append(maxPosition);
        builder.append(", trait=");
        builder.append(trait);
        builder.append(", experiment=");
        builder.append(experiment);
        builder.append(", leftFlankingMarker=");
        builder.append(leftFlankingMarker);
        builder.append(", rightFlankingMarker=");
        builder.append(rightFlankingMarker);
        builder.append(", effect=");
        builder.append(effect);
        builder.append(", scoreValue=");
        builder.append(scoreValue);
        builder.append(", rSquare=");
        builder.append(rSquare);
        builder.append(", interactions=");
        builder.append(interactions);
        builder.append(", tRName=");
        builder.append(tRName);
        builder.append(", ontology=");
        builder.append(ontology);
        builder.append("]");
        return builder.toString();
    }
}
