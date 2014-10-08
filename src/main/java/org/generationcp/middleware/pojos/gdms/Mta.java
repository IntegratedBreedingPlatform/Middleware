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

import javax.persistence.*;
import java.io.Serializable;

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
    
    @Column(name = "position")
    private Float position;
    
    @Column(name = "tid")
    private Integer tId;

    @Column(name = "effect")
    private Float effect;
    
    @Column(name = "score_value")
    private Float scoreValue;

    @Column(name = "r_square")
    private Float rSquare;
    
    @Column(name = "gene")
    private String gene;
    
    @Column(name = "chromosome")
    private String chromosome; // linkage_group
    
    @Column(name = "allele_a")
    private String alleleA;
    
    @Column(name = "allele_b") 
    private String alleleB;

    @Column(name = "allele_a_phenotype") 
    private String alleleAPhenotype;

    @Column(name = "allele_b_phenotype") 
    private String alleleBPhenotype;

    @Column(name = "freq_allele_a")
    private Float freqAlleleA;

    @Column(name = "freq_allele_b") 
    private Float freqAlleleB;
    
    @Column(name = "p_value_uncorrected")
    private Float pValueUncorrected;
    
    @Column(name = "p_value_corrected") 
    private Float pValueCorrected;
    
    @Column(name = "correction_method")
    private String correctionMethod;
    
    @Column(name = "trait_avg_allele_a") 
    private Float traitAvgAlleleA;
    
    @Column(name = "trait_avg_allele_b")
    private Float traitAvgAlleleB;
    
    @Column(name = "dominance")
    private String dominance;
    
    @Column(name = "evidence")
    private String evidence;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "notes")
    private String notes;
    

    public Mta() {
    }

    public Mta(Integer mtaId, Integer markerId, Integer datasetId,
			Integer mapId, Float position, Integer tId,
			Float effect, Float scoreValue,
			Float rSquare, String gene, String chromosome, String alleleA,
			String alleleB, String alleleAPhenotype, String alleleBPhenotype,
			Float freqAlleleA, Float freqAlleleB, Float pValueUncorrected,
			Float pValueCorrected, String correctionMethod,
			Float traitAvgAlleleA, Float traitAvgAlleleB, String dominance,
			String evidence, String reference, String notes) {
		this.mtaId = mtaId;
		this.markerId = markerId;
		this.datasetId = datasetId;
		this.mapId = mapId;
		this.position = position;
		this.tId = tId;
		this.effect = effect;
		this.scoreValue = scoreValue;
		this.rSquare = rSquare;
		this.gene = gene;
		this.chromosome = chromosome;
		this.alleleA = alleleA;
		this.alleleB = alleleB;
		this.alleleAPhenotype = alleleAPhenotype;
		this.alleleBPhenotype = alleleBPhenotype;
		this.freqAlleleA = freqAlleleA;
		this.freqAlleleB = freqAlleleB;
		this.pValueUncorrected = pValueUncorrected;
		this.pValueCorrected = pValueCorrected;
		this.correctionMethod = correctionMethod;
		this.traitAvgAlleleA = traitAvgAlleleA;
		this.traitAvgAlleleB = traitAvgAlleleB;
		this.dominance = dominance;
		this.evidence = evidence;
		this.reference = reference;
		this.notes = notes;
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

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getAlleleA() {
		return alleleA;
	}

	public void setAlleleA(String alleleA) {
		this.alleleA = alleleA;
	}

	public String getAlleleB() {
		return alleleB;
	}

	public void setAlleleB(String alleleB) {
		this.alleleB = alleleB;
	}

	public String getAlleleAPhenotype() {
		return alleleAPhenotype;
	}

	public void setAlleleAPhenotype(String alleleAPhenotype) {
		this.alleleAPhenotype = alleleAPhenotype;
	}

	public String getAlleleBPhenotype() {
		return alleleBPhenotype;
	}

	public void setAlleleBPhenotype(String alleleBPhenotype) {
		this.alleleBPhenotype = alleleBPhenotype;
	}

	public Float getFreqAlleleA() {
		return freqAlleleA;
	}

	public void setFreqAlleleA(Float freqAlleleA) {
		this.freqAlleleA = freqAlleleA;
	}

	public Float getFreqAlleleB() {
		return freqAlleleB;
	}

	public void setFreqAlleleB(Float freqAlleleB) {
		this.freqAlleleB = freqAlleleB;
	}

	public Float getpValueUncorrected() {
		return pValueUncorrected;
	}

	public void setpValueUncorrected(Float pValueUncorrected) {
		this.pValueUncorrected = pValueUncorrected;
	}

	public Float getpValueCorrected() {
		return pValueCorrected;
	}

	public void setpValueCorrected(Float pValueCorrected) {
		this.pValueCorrected = pValueCorrected;
	}

	public String getCorrectionMethod() {
		return correctionMethod;
	}

	public void setCorrectionMethod(String correctionMethod) {
		this.correctionMethod = correctionMethod;
	}

	public Float getTraitAvgAlleleA() {
		return traitAvgAlleleA;
	}

	public void setTraitAvgAlleleA(Float traitAvgAlleleA) {
		this.traitAvgAlleleA = traitAvgAlleleA;
	}

	public Float getTraitAvgAlleleB() {
		return traitAvgAlleleB;
	}

	public void setTraitAvgAlleleB(Float traitAvgAlleleB) {
		this.traitAvgAlleleB = traitAvgAlleleB;
	}

	public String getDominance() {
		return dominance;
	}

	public void setDominance(String dominance) {
		this.dominance = dominance;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleA == null) ? 0 : alleleA.hashCode());
		result = prime
				* result
				+ ((alleleAPhenotype == null) ? 0 : alleleAPhenotype.hashCode());
		result = prime * result + ((alleleB == null) ? 0 : alleleB.hashCode());
		result = prime
				* result
				+ ((alleleBPhenotype == null) ? 0 : alleleBPhenotype.hashCode());
		result = prime * result
				+ ((chromosome == null) ? 0 : chromosome.hashCode());
		result = prime
				* result
				+ ((correctionMethod == null) ? 0 : correctionMethod.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((dominance == null) ? 0 : dominance.hashCode());
		result = prime * result + ((effect == null) ? 0 : effect.hashCode());
		result = prime * result
				+ ((evidence == null) ? 0 : evidence.hashCode());
		result = prime * result
				+ ((freqAlleleA == null) ? 0 : freqAlleleA.hashCode());
		result = prime * result
				+ ((freqAlleleB == null) ? 0 : freqAlleleB.hashCode());
		result = prime * result + ((gene == null) ? 0 : gene.hashCode());
		result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result + ((mtaId == null) ? 0 : mtaId.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result
				+ ((pValueCorrected == null) ? 0 : pValueCorrected.hashCode());
		result = prime
				* result
				+ ((pValueUncorrected == null) ? 0 : pValueUncorrected
						.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((rSquare == null) ? 0 : rSquare.hashCode());
		result = prime * result
				+ ((reference == null) ? 0 : reference.hashCode());
		result = prime * result
				+ ((scoreValue == null) ? 0 : scoreValue.hashCode());
		result = prime * result + ((tId == null) ? 0 : tId.hashCode());
		result = prime * result
				+ ((traitAvgAlleleA == null) ? 0 : traitAvgAlleleA.hashCode());
		result = prime * result
				+ ((traitAvgAlleleB == null) ? 0 : traitAvgAlleleB.hashCode());
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
		if (alleleA == null) {
			if (other.alleleA != null)
				return false;
		} else if (!alleleA.equals(other.alleleA))
			return false;
		if (alleleAPhenotype == null) {
			if (other.alleleAPhenotype != null)
				return false;
		} else if (!alleleAPhenotype.equals(other.alleleAPhenotype))
			return false;
		if (alleleB == null) {
			if (other.alleleB != null)
				return false;
		} else if (!alleleB.equals(other.alleleB))
			return false;
		if (alleleBPhenotype == null) {
			if (other.alleleBPhenotype != null)
				return false;
		} else if (!alleleBPhenotype.equals(other.alleleBPhenotype))
			return false;
		if (chromosome == null) {
			if (other.chromosome != null)
				return false;
		} else if (!chromosome.equals(other.chromosome))
			return false;
		if (correctionMethod == null) {
			if (other.correctionMethod != null)
				return false;
		} else if (!correctionMethod.equals(other.correctionMethod))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (dominance == null) {
			if (other.dominance != null)
				return false;
		} else if (!dominance.equals(other.dominance))
			return false;
		if (effect == null) {
			if (other.effect != null)
				return false;
		} else if (!effect.equals(other.effect))
			return false;
		if (evidence == null) {
			if (other.evidence != null)
				return false;
		} else if (!evidence.equals(other.evidence))
			return false;
		if (freqAlleleA == null) {
			if (other.freqAlleleA != null)
				return false;
		} else if (!freqAlleleA.equals(other.freqAlleleA))
			return false;
		if (freqAlleleB == null) {
			if (other.freqAlleleB != null)
				return false;
		} else if (!freqAlleleB.equals(other.freqAlleleB))
			return false;
		if (gene == null) {
			if (other.gene != null)
				return false;
		} else if (!gene.equals(other.gene))
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
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (pValueCorrected == null) {
			if (other.pValueCorrected != null)
				return false;
		} else if (!pValueCorrected.equals(other.pValueCorrected))
			return false;
		if (pValueUncorrected == null) {
			if (other.pValueUncorrected != null)
				return false;
		} else if (!pValueUncorrected.equals(other.pValueUncorrected))
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
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
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
		if (traitAvgAlleleA == null) {
			if (other.traitAvgAlleleA != null)
				return false;
		} else if (!traitAvgAlleleA.equals(other.traitAvgAlleleA))
			return false;
		if (traitAvgAlleleB == null) {
			if (other.traitAvgAlleleB != null)
				return false;
		} else if (!traitAvgAlleleB.equals(other.traitAvgAlleleB))
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
		builder.append(", position=");
		builder.append(position);
		builder.append(", tId=");
		builder.append(tId);
		builder.append(", effect=");
		builder.append(effect);
		builder.append(", scoreValue=");
		builder.append(scoreValue);
		builder.append(", rSquare=");
		builder.append(rSquare);
		builder.append(", gene=");
		builder.append(gene);
		builder.append(", chromosome=");
		builder.append(chromosome);
		builder.append(", alleleA=");
		builder.append(alleleA);
		builder.append(", alleleB=");
		builder.append(alleleB);
		builder.append(", alleleAPhenotype=");
		builder.append(alleleAPhenotype);
		builder.append(", alleleBPhenotype=");
		builder.append(alleleBPhenotype);
		builder.append(", freqAlleleA=");
		builder.append(freqAlleleA);
		builder.append(", freqAlleleB=");
		builder.append(freqAlleleB);
		builder.append(", pValueUncorrected=");
		builder.append(pValueUncorrected);
		builder.append(", pValueCorrected=");
		builder.append(pValueCorrected);
		builder.append(", correctionMethod=");
		builder.append(correctionMethod);
		builder.append(", traitAvgAlleleA=");
		builder.append(traitAvgAlleleA);
		builder.append(", traitAvgAlleleB=");
		builder.append(traitAvgAlleleB);
		builder.append(", dominance=");
		builder.append(dominance);
		builder.append(", evidence=");
		builder.append(evidence);
		builder.append(", reference=");
		builder.append(reference);
		builder.append(", notes=");
		builder.append(notes);
		builder.append("]");
		return builder.toString();
	}
    
}
