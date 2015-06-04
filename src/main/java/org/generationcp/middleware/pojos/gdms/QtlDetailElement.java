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

/**
 * Placeholder POJO for QtlDetail Element.
 *
 * @author Joyce Avestro
 *
 */
public class QtlDetailElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String qtlName;
	private String mapName;
	private QtlDetails qtlDetails;
	private String tRName;
	private String ontology;

	public QtlDetailElement() {
	}

	public QtlDetailElement(String qtlName, String mapName, QtlDetails qtlDetails) {
		this.qtlName = qtlName;
		this.mapName = mapName;
		this.qtlDetails = qtlDetails;
	}

	public QtlDetailElement(String qtlName, String mapName, QtlDetails qtlDetails, String tRName, String ontology) {
		this.qtlName = qtlName;
		this.mapName = mapName;
		this.qtlDetails = qtlDetails;
		this.tRName = tRName;
		this.ontology = ontology;
	}

	public String getQtlName() {
		return this.qtlName;
	}

	public void setQtlName(String qtlName) {
		this.qtlName = qtlName;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public QtlDetails getQtlDetails() {
		return this.qtlDetails;
	}

	public void setQtlDetails(QtlDetails qtlDetails) {
		this.qtlDetails = qtlDetails;
	}

	public String getChromosome() {
		return this.qtlDetails.getLinkageGroup();
	}

	public void setChromosome(String chromosome) {
		this.qtlDetails.setLinkageGroup(chromosome);
	}

	public Float getMinPosition() {
		return this.qtlDetails.getMinPosition();
	}

	public void setMinPosition(Float minPosition) {
		this.qtlDetails.setMinPosition(minPosition);
	}

	public Float getMaxPosition() {
		return this.qtlDetails.getMaxPosition();
	}

	public void setMaxPosition(Float maxPosition) {
		this.qtlDetails.setMaxPosition(maxPosition);
	}

	public Integer getTraitId() {
		return this.qtlDetails.getTraitId();
	}

	public void setTraitId(Integer traitId) {
		this.qtlDetails.setTraitId(traitId);
	}

	public String getExperiment() {
		return this.qtlDetails.getExperiment();
	}

	public void setExperiment(String experiment) {
		this.qtlDetails.setExperiment(experiment);
	}

	public String getLeftFlankingMarker() {
		return this.qtlDetails.getLeftFlankingMarker();
	}

	public void setLeftFlankingMarker(String leftFlankingMarker) {
		this.qtlDetails.setLeftFlankingMarker(leftFlankingMarker);
	}

	public String getRightFlankingMarker() {
		return this.qtlDetails.getRightFlankingMarker();
	}

	public void setRightFlankingMarker(String rightFlankingMarker) {
		this.qtlDetails.setRightFlankingMarker(rightFlankingMarker);
	}

	public Float getEffect() {
		return this.qtlDetails.getEffect();
	}

	public void setEffect(Float effect) {
		this.qtlDetails.setEffect(effect);
	}

	public Float getScoreValue() {
		return this.qtlDetails.getScoreValue();
	}

	public void setScoreValue(Float scoreValue) {
		this.qtlDetails.setScoreValue(scoreValue);
	}

	public Float getrSquare() {
		return this.qtlDetails.getrSquare();
	}

	public void setrSquare(Float rSquare) {
		this.qtlDetails.setrSquare(rSquare);
	}

	public String getInteractions() {
		return this.qtlDetails.getInteractions();
	}

	public void setInteractions(String interactions) {
		this.qtlDetails.setInteractions(interactions);
	}

	public Float getPosition() {
		return this.qtlDetails.getPosition();
	}

	public void setPosition(Float position) {
		this.qtlDetails.setPosition(position);
	}

	public Float getClen() {
		return this.qtlDetails.getClen();
	}

	public void setClen(Float clen) {
		this.qtlDetails.setClen(clen);
	}

	public String getSeAdditive() {
		return this.qtlDetails.getSeAdditive();
	}

	public void setSeAdditive(String seAdditive) {
		this.qtlDetails.setSeAdditive(seAdditive);
	}

	public String getHvParent() {
		return this.qtlDetails.getHvParent();
	}

	public void setHvParent(String hvParent) {
		this.qtlDetails.setHvParent(hvParent);
	}

	public String getHvAllele() {
		return this.qtlDetails.getHvAllele();
	}

	public void setHvAllele(String hvAllele) {
		this.qtlDetails.setHvAllele(hvAllele);
	}

	public String getLvParent() {
		return this.qtlDetails.getLvParent();
	}

	public void setLvParent(String lvParent) {
		this.qtlDetails.setLvParent(lvParent);
	}

	public String getLvAllele() {
		return this.qtlDetails.getLvAllele();
	}

	public void setLvAllele(String lvAllele) {
		this.qtlDetails.setLvAllele(lvAllele);
	}

	public String gettRName() {
		return this.tRName;
	}

	public void settRName(String tRName) {
		this.tRName = tRName;
	}

	public String getOntology() {
		return this.ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.mapName == null ? 0 : this.mapName.hashCode());
		result = prime * result + (this.ontology == null ? 0 : this.ontology.hashCode());
		result = prime * result + (this.qtlDetails == null ? 0 : this.qtlDetails.hashCode());
		result = prime * result + (this.qtlName == null ? 0 : this.qtlName.hashCode());
		result = prime * result + (this.tRName == null ? 0 : this.tRName.hashCode());
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
		QtlDetailElement other = (QtlDetailElement) obj;
		if (this.mapName == null) {
			if (other.mapName != null) {
				return false;
			}
		} else if (!this.mapName.equals(other.mapName)) {
			return false;
		}
		if (this.ontology == null) {
			if (other.ontology != null) {
				return false;
			}
		} else if (!this.ontology.equals(other.ontology)) {
			return false;
		}
		if (this.qtlDetails == null) {
			if (other.qtlDetails != null) {
				return false;
			}
		} else if (!this.qtlDetails.equals(other.qtlDetails)) {
			return false;
		}
		if (this.qtlName == null) {
			if (other.qtlName != null) {
				return false;
			}
		} else if (!this.qtlName.equals(other.qtlName)) {
			return false;
		}
		if (this.tRName == null) {
			if (other.tRName != null) {
				return false;
			}
		} else if (!this.tRName.equals(other.tRName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QtlDetailElement [qtlName=");
		builder.append(this.qtlName);
		builder.append(", mapName=");
		builder.append(this.mapName);
		builder.append(", chromosome=");
		builder.append(this.getChromosome());
		builder.append(", minPosition=");
		builder.append(this.getMinPosition());
		builder.append(", maxPosition=");
		builder.append(this.getMaxPosition());
		builder.append(", traitId=");
		builder.append(this.getTraitId());
		builder.append(", experiment=");
		builder.append(this.getExperiment());
		builder.append(", leftFlankingMarker=");
		builder.append(this.getLeftFlankingMarker());
		builder.append(", rightFlankingMarker=");
		builder.append(this.getRightFlankingMarker());
		builder.append(", effect=");
		builder.append(this.getEffect());
		builder.append(", scoreValue=");
		builder.append(this.getScoreValue());
		builder.append(", rSquare=");
		builder.append(this.getrSquare());
		builder.append(", interactions=");
		builder.append(this.getInteractions());
		builder.append(", position=");
		builder.append(this.getPosition());
		builder.append(", clen=");
		builder.append(this.getClen());
		builder.append(", seAdditive=");
		builder.append(this.getSeAdditive());
		builder.append(", hvParent=");
		builder.append(this.getHvParent());
		builder.append(", hvAllele=");
		builder.append(this.getHvAllele());
		builder.append(", lvParent=");
		builder.append(this.getLvParent());
		builder.append(", lvAllele=");
		builder.append(this.getLvAllele());
		builder.append(", tRName=");
		builder.append(this.tRName);
		builder.append(", ontology=");
		builder.append(this.ontology);
		builder.append("]");
		return builder.toString();
	}

}
