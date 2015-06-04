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
 * POJO for gdms_qtl_details table
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "gdms_qtl_details")
public class QtlDetails implements Serializable {

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

	public QtlDetails() {

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
		return this.minPosition;
	}

	public void setMinPosition(Float minPosition) {
		this.minPosition = minPosition;
	}

	public Float getMaxPosition() {
		return this.maxPosition;
	}

	public void setMaxPosition(Float maxPosition) {
		this.maxPosition = maxPosition;
	}

	public Integer getTraitId() {
		return this.traitId;
	}

	public void setTraitId(Integer traitId) {
		this.traitId = traitId;
	}

	public String getExperiment() {
		return this.experiment;
	}

	public void setExperiment(String experiment) {
		this.experiment = experiment;
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

	public String getLinkageGroup() {
		return this.linkageGroup;
	}

	public void setLinkageGroup(String linkageGroup) {
		this.linkageGroup = linkageGroup;
	}

	public String getInteractions() {
		return this.interactions;
	}

	public void setInteractions(String interactions) {
		this.interactions = interactions;
	}

	public String getLeftFlankingMarker() {
		return this.leftFlankingMarker;
	}

	public void setLeftFlankingMarker(String leftFlankingMarker) {
		this.leftFlankingMarker = leftFlankingMarker;
	}

	public String getRightFlankingMarker() {
		return this.rightFlankingMarker;
	}

	public void setRightFlankingMarker(String rightFlankingMarker) {
		this.rightFlankingMarker = rightFlankingMarker;
	}

	public Float getPosition() {
		return this.position;
	}

	public void setPosition(Float position) {
		this.position = position;
	}

	public Float getClen() {
		return this.clen;
	}

	public void setClen(Float clen) {
		this.clen = clen;
	}

	public String getSeAdditive() {
		return this.seAdditive;
	}

	public void setSeAdditive(String seAdditive) {
		this.seAdditive = seAdditive;
	}

	public String getHvParent() {
		return this.hvParent;
	}

	public void setHvParent(String hvParent) {
		this.hvParent = hvParent;
	}

	public String getHvAllele() {
		return this.hvAllele;
	}

	public void setHvAllele(String hvAllele) {
		this.hvAllele = hvAllele;
	}

	public String getLvParent() {
		return this.lvParent;
	}

	public void setLvParent(String lvParent) {
		this.lvParent = lvParent;
	}

	public String getLvAllele() {
		return this.lvAllele;
	}

	public void setLvAllele(String lvAllele) {
		this.lvAllele = lvAllele;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.clen == null ? 0 : this.clen.hashCode());
		result = prime * result + (this.effect == null ? 0 : this.effect.hashCode());
		result = prime * result + (this.experiment == null ? 0 : this.experiment.hashCode());
		result = prime * result + (this.hvAllele == null ? 0 : this.hvAllele.hashCode());
		result = prime * result + (this.hvParent == null ? 0 : this.hvParent.hashCode());
		result = prime * result + (this.qtlId == null ? 0 : this.qtlId.hashCode());
		result = prime * result + (this.mapId == null ? 0 : this.mapId.hashCode());
		result = prime * result + (this.interactions == null ? 0 : this.interactions.hashCode());
		result = prime * result + (this.leftFlankingMarker == null ? 0 : this.leftFlankingMarker.hashCode());
		result = prime * result + (this.linkageGroup == null ? 0 : this.linkageGroup.hashCode());
		result = prime * result + (this.lvAllele == null ? 0 : this.lvAllele.hashCode());
		result = prime * result + (this.lvParent == null ? 0 : this.lvParent.hashCode());
		result = prime * result + (this.maxPosition == null ? 0 : this.maxPosition.hashCode());
		result = prime * result + (this.minPosition == null ? 0 : this.minPosition.hashCode());
		result = prime * result + (this.position == null ? 0 : this.position.hashCode());
		result = prime * result + (this.rSquare == null ? 0 : this.rSquare.hashCode());
		result = prime * result + (this.rightFlankingMarker == null ? 0 : this.rightFlankingMarker.hashCode());
		result = prime * result + (this.scoreValue == null ? 0 : this.scoreValue.hashCode());
		result = prime * result + (this.seAdditive == null ? 0 : this.seAdditive.hashCode());
		result = prime * result + (this.traitId == null ? 0 : this.traitId.hashCode());
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
		QtlDetails other = (QtlDetails) obj;
		if (this.clen == null) {
			if (other.clen != null) {
				return false;
			}
		} else if (!this.clen.equals(other.clen)) {
			return false;
		}
		if (this.effect == null) {
			if (other.effect != null) {
				return false;
			}
		} else if (!this.effect.equals(other.effect)) {
			return false;
		}
		if (this.experiment == null) {
			if (other.experiment != null) {
				return false;
			}
		} else if (!this.experiment.equals(other.experiment)) {
			return false;
		}
		if (this.hvAllele == null) {
			if (other.hvAllele != null) {
				return false;
			}
		} else if (!this.hvAllele.equals(other.hvAllele)) {
			return false;
		}
		if (this.hvParent == null) {
			if (other.hvParent != null) {
				return false;
			}
		} else if (!this.hvParent.equals(other.hvParent)) {
			return false;
		}
		if (this.qtlId == null) {
			if (other.qtlId != null) {
				return false;
			}
		} else if (!this.qtlId.equals(other.qtlId)) {
			return false;
		}
		if (this.mapId == null) {
			if (other.mapId != null) {
				return false;
			}
		} else if (!this.mapId.equals(other.mapId)) {
			return false;
		}
		if (this.interactions == null) {
			if (other.interactions != null) {
				return false;
			}
		} else if (!this.interactions.equals(other.interactions)) {
			return false;
		}
		if (this.leftFlankingMarker == null) {
			if (other.leftFlankingMarker != null) {
				return false;
			}
		} else if (!this.leftFlankingMarker.equals(other.leftFlankingMarker)) {
			return false;
		}
		if (this.linkageGroup == null) {
			if (other.linkageGroup != null) {
				return false;
			}
		} else if (!this.linkageGroup.equals(other.linkageGroup)) {
			return false;
		}
		if (this.lvAllele == null) {
			if (other.lvAllele != null) {
				return false;
			}
		} else if (!this.lvAllele.equals(other.lvAllele)) {
			return false;
		}
		if (this.lvParent == null) {
			if (other.lvParent != null) {
				return false;
			}
		} else if (!this.lvParent.equals(other.lvParent)) {
			return false;
		}
		if (this.maxPosition == null) {
			if (other.maxPosition != null) {
				return false;
			}
		} else if (!this.maxPosition.equals(other.maxPosition)) {
			return false;
		}
		if (this.minPosition == null) {
			if (other.minPosition != null) {
				return false;
			}
		} else if (!this.minPosition.equals(other.minPosition)) {
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
		if (this.rightFlankingMarker == null) {
			if (other.rightFlankingMarker != null) {
				return false;
			}
		} else if (!this.rightFlankingMarker.equals(other.rightFlankingMarker)) {
			return false;
		}
		if (this.scoreValue == null) {
			if (other.scoreValue != null) {
				return false;
			}
		} else if (!this.scoreValue.equals(other.scoreValue)) {
			return false;
		}
		if (this.seAdditive == null) {
			if (other.seAdditive != null) {
				return false;
			}
		} else if (!this.seAdditive.equals(other.seAdditive)) {
			return false;
		}
		if (this.traitId == null) {
			if (other.traitId != null) {
				return false;
			}
		} else if (!this.traitId.equals(other.traitId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QtlDetails [qtlId=");
		builder.append(this.qtlId);
		builder.append(", mapId=");
		builder.append(this.mapId);
		builder.append(", minPosition=");
		builder.append(this.minPosition);
		builder.append(", maxPosition=");
		builder.append(this.maxPosition);
		builder.append(", traitId=");
		builder.append(this.traitId);
		builder.append(", experiment=");
		builder.append(this.experiment);
		builder.append(", effect=");
		builder.append(this.effect);
		builder.append(", scoreValue=");
		builder.append(this.scoreValue);
		builder.append(", rSquare=");
		builder.append(this.rSquare);
		builder.append(", linkageGroup=");
		builder.append(this.linkageGroup);
		builder.append(", interactions=");
		builder.append(this.interactions);
		builder.append(", leftFlankingMarker=");
		builder.append(this.leftFlankingMarker);
		builder.append(", rightFlankingMarker=");
		builder.append(this.rightFlankingMarker);
		builder.append(", position=");
		builder.append(this.position);
		builder.append(", clen=");
		builder.append(this.clen);
		builder.append(", seAdditive=");
		builder.append(this.seAdditive);
		builder.append(", hvParent=");
		builder.append(this.hvParent);
		builder.append(", hvAllele=");
		builder.append(this.hvAllele);
		builder.append(", lvParent=");
		builder.append(this.lvParent);
		builder.append(", lvAllele=");
		builder.append(this.lvAllele);
		builder.append("]");
		return builder.toString();
	}

}
