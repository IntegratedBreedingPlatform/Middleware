/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
public class Mta implements Serializable {

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

	public Mta(Integer mtaId, Integer markerId, Integer datasetId, Integer mapId, Float position, Integer tId, Float effect,
			Float scoreValue, Float rSquare, String gene, String chromosome, String alleleA, String alleleB, String alleleAPhenotype,
			String alleleBPhenotype, Float freqAlleleA, Float freqAlleleB, Float pValueUncorrected, Float pValueCorrected,
			String correctionMethod, Float traitAvgAlleleA, Float traitAvgAlleleB, String dominance, String evidence, String reference,
			String notes) {
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
		return this.mtaId;
	}

	public void setMtaId(Integer mtaId) {
		this.mtaId = mtaId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getMapId() {
		return this.mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public Float getPosition() {
		return this.position;
	}

	public void setPosition(Float position) {
		this.position = position;
	}

	public Integer gettId() {
		return this.tId;
	}

	public void settId(Integer tId) {
		this.tId = tId;
	}

	public Float getEffect() {
		return this.effect;
	}

	public void setEffect(Float effect) {
		this.effect = effect;
	}

	public Float getScoreValue() {
		return this.scoreValue;
	}

	public void setScoreValue(Float scoreValue) {
		this.scoreValue = scoreValue;
	}

	public Float getrSquare() {
		return this.rSquare;
	}

	public void setrSquare(Float rSquare) {
		this.rSquare = rSquare;
	}

	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getChromosome() {
		return this.chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getAlleleA() {
		return this.alleleA;
	}

	public void setAlleleA(String alleleA) {
		this.alleleA = alleleA;
	}

	public String getAlleleB() {
		return this.alleleB;
	}

	public void setAlleleB(String alleleB) {
		this.alleleB = alleleB;
	}

	public String getAlleleAPhenotype() {
		return this.alleleAPhenotype;
	}

	public void setAlleleAPhenotype(String alleleAPhenotype) {
		this.alleleAPhenotype = alleleAPhenotype;
	}

	public String getAlleleBPhenotype() {
		return this.alleleBPhenotype;
	}

	public void setAlleleBPhenotype(String alleleBPhenotype) {
		this.alleleBPhenotype = alleleBPhenotype;
	}

	public Float getFreqAlleleA() {
		return this.freqAlleleA;
	}

	public void setFreqAlleleA(Float freqAlleleA) {
		this.freqAlleleA = freqAlleleA;
	}

	public Float getFreqAlleleB() {
		return this.freqAlleleB;
	}

	public void setFreqAlleleB(Float freqAlleleB) {
		this.freqAlleleB = freqAlleleB;
	}

	public Float getpValueUncorrected() {
		return this.pValueUncorrected;
	}

	public void setpValueUncorrected(Float pValueUncorrected) {
		this.pValueUncorrected = pValueUncorrected;
	}

	public Float getpValueCorrected() {
		return this.pValueCorrected;
	}

	public void setpValueCorrected(Float pValueCorrected) {
		this.pValueCorrected = pValueCorrected;
	}

	public String getCorrectionMethod() {
		return this.correctionMethod;
	}

	public void setCorrectionMethod(String correctionMethod) {
		this.correctionMethod = correctionMethod;
	}

	public Float getTraitAvgAlleleA() {
		return this.traitAvgAlleleA;
	}

	public void setTraitAvgAlleleA(Float traitAvgAlleleA) {
		this.traitAvgAlleleA = traitAvgAlleleA;
	}

	public Float getTraitAvgAlleleB() {
		return this.traitAvgAlleleB;
	}

	public void setTraitAvgAlleleB(Float traitAvgAlleleB) {
		this.traitAvgAlleleB = traitAvgAlleleB;
	}

	public String getDominance() {
		return this.dominance;
	}

	public void setDominance(String dominance) {
		this.dominance = dominance;
	}

	public String getEvidence() {
		return this.evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public static long getSerialversionuid() {
		return Mta.serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.alleleA == null ? 0 : this.alleleA.hashCode());
		result = prime * result + (this.alleleAPhenotype == null ? 0 : this.alleleAPhenotype.hashCode());
		result = prime * result + (this.alleleB == null ? 0 : this.alleleB.hashCode());
		result = prime * result + (this.alleleBPhenotype == null ? 0 : this.alleleBPhenotype.hashCode());
		result = prime * result + (this.chromosome == null ? 0 : this.chromosome.hashCode());
		result = prime * result + (this.correctionMethod == null ? 0 : this.correctionMethod.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.dominance == null ? 0 : this.dominance.hashCode());
		result = prime * result + (this.effect == null ? 0 : this.effect.hashCode());
		result = prime * result + (this.evidence == null ? 0 : this.evidence.hashCode());
		result = prime * result + (this.freqAlleleA == null ? 0 : this.freqAlleleA.hashCode());
		result = prime * result + (this.freqAlleleB == null ? 0 : this.freqAlleleB.hashCode());
		result = prime * result + (this.gene == null ? 0 : this.gene.hashCode());
		result = prime * result + (this.mapId == null ? 0 : this.mapId.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.mtaId == null ? 0 : this.mtaId.hashCode());
		result = prime * result + (this.notes == null ? 0 : this.notes.hashCode());
		result = prime * result + (this.pValueCorrected == null ? 0 : this.pValueCorrected.hashCode());
		result = prime * result + (this.pValueUncorrected == null ? 0 : this.pValueUncorrected.hashCode());
		result = prime * result + (this.position == null ? 0 : this.position.hashCode());
		result = prime * result + (this.rSquare == null ? 0 : this.rSquare.hashCode());
		result = prime * result + (this.reference == null ? 0 : this.reference.hashCode());
		result = prime * result + (this.scoreValue == null ? 0 : this.scoreValue.hashCode());
		result = prime * result + (this.tId == null ? 0 : this.tId.hashCode());
		result = prime * result + (this.traitAvgAlleleA == null ? 0 : this.traitAvgAlleleA.hashCode());
		result = prime * result + (this.traitAvgAlleleB == null ? 0 : this.traitAvgAlleleB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Mta other = (Mta) obj;
		if (this.alleleA == null) {
			if (other.alleleA != null) {
				return false;
			}
		} else if (!this.alleleA.equals(other.alleleA)) {
			return false;
		}
		if (this.alleleAPhenotype == null) {
			if (other.alleleAPhenotype != null) {
				return false;
			}
		} else if (!this.alleleAPhenotype.equals(other.alleleAPhenotype)) {
			return false;
		}
		if (this.alleleB == null) {
			if (other.alleleB != null) {
				return false;
			}
		} else if (!this.alleleB.equals(other.alleleB)) {
			return false;
		}
		if (this.alleleBPhenotype == null) {
			if (other.alleleBPhenotype != null) {
				return false;
			}
		} else if (!this.alleleBPhenotype.equals(other.alleleBPhenotype)) {
			return false;
		}
		if (this.chromosome == null) {
			if (other.chromosome != null) {
				return false;
			}
		} else if (!this.chromosome.equals(other.chromosome)) {
			return false;
		}
		if (this.correctionMethod == null) {
			if (other.correctionMethod != null) {
				return false;
			}
		} else if (!this.correctionMethod.equals(other.correctionMethod)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.dominance == null) {
			if (other.dominance != null) {
				return false;
			}
		} else if (!this.dominance.equals(other.dominance)) {
			return false;
		}
		if (this.effect == null) {
			if (other.effect != null) {
				return false;
			}
		} else if (!this.effect.equals(other.effect)) {
			return false;
		}
		if (this.evidence == null) {
			if (other.evidence != null) {
				return false;
			}
		} else if (!this.evidence.equals(other.evidence)) {
			return false;
		}
		if (this.freqAlleleA == null) {
			if (other.freqAlleleA != null) {
				return false;
			}
		} else if (!this.freqAlleleA.equals(other.freqAlleleA)) {
			return false;
		}
		if (this.freqAlleleB == null) {
			if (other.freqAlleleB != null) {
				return false;
			}
		} else if (!this.freqAlleleB.equals(other.freqAlleleB)) {
			return false;
		}
		if (this.gene == null) {
			if (other.gene != null) {
				return false;
			}
		} else if (!this.gene.equals(other.gene)) {
			return false;
		}
		if (this.mapId == null) {
			if (other.mapId != null) {
				return false;
			}
		} else if (!this.mapId.equals(other.mapId)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.mtaId == null) {
			if (other.mtaId != null) {
				return false;
			}
		} else if (!this.mtaId.equals(other.mtaId)) {
			return false;
		}
		if (this.notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!this.notes.equals(other.notes)) {
			return false;
		}
		if (this.pValueCorrected == null) {
			if (other.pValueCorrected != null) {
				return false;
			}
		} else if (!this.pValueCorrected.equals(other.pValueCorrected)) {
			return false;
		}
		if (this.pValueUncorrected == null) {
			if (other.pValueUncorrected != null) {
				return false;
			}
		} else if (!this.pValueUncorrected.equals(other.pValueUncorrected)) {
			return false;
		}
		if (this.position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!this.position.equals(other.position)) {
			return false;
		}
		if (this.rSquare == null) {
			if (other.rSquare != null) {
				return false;
			}
		} else if (!this.rSquare.equals(other.rSquare)) {
			return false;
		}
		if (this.reference == null) {
			if (other.reference != null) {
				return false;
			}
		} else if (!this.reference.equals(other.reference)) {
			return false;
		}
		if (this.scoreValue == null) {
			if (other.scoreValue != null) {
				return false;
			}
		} else if (!this.scoreValue.equals(other.scoreValue)) {
			return false;
		}
		if (this.tId == null) {
			if (other.tId != null) {
				return false;
			}
		} else if (!this.tId.equals(other.tId)) {
			return false;
		}
		if (this.traitAvgAlleleA == null) {
			if (other.traitAvgAlleleA != null) {
				return false;
			}
		} else if (!this.traitAvgAlleleA.equals(other.traitAvgAlleleA)) {
			return false;
		}
		if (this.traitAvgAlleleB == null) {
			if (other.traitAvgAlleleB != null) {
				return false;
			}
		} else if (!this.traitAvgAlleleB.equals(other.traitAvgAlleleB)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mta [mtaId=");
		builder.append(this.mtaId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", mapId=");
		builder.append(this.mapId);
		builder.append(", position=");
		builder.append(this.position);
		builder.append(", tId=");
		builder.append(this.tId);
		builder.append(", effect=");
		builder.append(this.effect);
		builder.append(", scoreValue=");
		builder.append(this.scoreValue);
		builder.append(", rSquare=");
		builder.append(this.rSquare);
		builder.append(", gene=");
		builder.append(this.gene);
		builder.append(", chromosome=");
		builder.append(this.chromosome);
		builder.append(", alleleA=");
		builder.append(this.alleleA);
		builder.append(", alleleB=");
		builder.append(this.alleleB);
		builder.append(", alleleAPhenotype=");
		builder.append(this.alleleAPhenotype);
		builder.append(", alleleBPhenotype=");
		builder.append(this.alleleBPhenotype);
		builder.append(", freqAlleleA=");
		builder.append(this.freqAlleleA);
		builder.append(", freqAlleleB=");
		builder.append(this.freqAlleleB);
		builder.append(", pValueUncorrected=");
		builder.append(this.pValueUncorrected);
		builder.append(", pValueCorrected=");
		builder.append(this.pValueCorrected);
		builder.append(", correctionMethod=");
		builder.append(this.correctionMethod);
		builder.append(", traitAvgAlleleA=");
		builder.append(this.traitAvgAlleleA);
		builder.append(", traitAvgAlleleB=");
		builder.append(this.traitAvgAlleleB);
		builder.append(", dominance=");
		builder.append(this.dominance);
		builder.append(", evidence=");
		builder.append(this.evidence);
		builder.append(", reference=");
		builder.append(this.reference);
		builder.append(", notes=");
		builder.append(this.notes);
		builder.append("]");
		return builder.toString();
	}

}
