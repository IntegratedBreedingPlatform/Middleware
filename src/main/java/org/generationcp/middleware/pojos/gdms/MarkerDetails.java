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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for gdms_marker_details table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_details")
public class MarkerDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "no_of_repeats")
	private Integer noOfRepeats;

	@Column(name = "motif_type")
	private String motifType;

	@Column(name = "sequence")
	private String sequence;

	@Column(name = "sequence_length")
	private Integer sequenceLength;

	@Column(name = "min_allele")
	private Integer minAllele;

	@Column(name = "max_allele")
	private Integer maxAllele;

	@Column(name = "ssr_nr")
	private Integer ssrNr;

	@Column(name = "forward_primer_temp")
	private Float forwardPrimerTemp;

	@Column(name = "reverse_primer_temp")
	private Float reversePrimerTemp;

	@Column(name = "elongation_temp")
	private Float elongationTemp;

	@Column(name = "fragment_size_expected")
	private Integer fragmentSizeExpected;

	@Column(name = "fragment_size_observed")
	private Integer fragmentSizeObserved;

	@Column(name = "expected_product_size")
	private Integer expectedProductSize;

	@Column(name = "position_on_reference_sequence")
	private Integer positionOnReferenceSequence;

	@Column(name = "restriction_enzyme_for_assay")
	private String restrictionEnzymeForAssay;

	public MarkerDetails() {
		super();
	}

	public MarkerDetails(Integer markerId, Integer noOfRepeats, String motifType, String sequence, Integer sequenceLength,
			Integer minAllele, Integer maxAllele, Integer ssrNr, Float forwardPrimerTemp, Float reversePrimerTemp, Float elongationTemp,
			Integer fragmentSizeExpected, Integer fragmentSizeObserved, Integer expectedProductSize, Integer positionOnReferenceSequence,
			String restrictionEnzymeForAssay) {
		super();
		this.markerId = markerId;
		this.noOfRepeats = noOfRepeats;
		this.motifType = motifType;
		this.sequence = sequence;
		this.sequenceLength = sequenceLength;
		this.minAllele = minAllele;
		this.maxAllele = maxAllele;
		this.ssrNr = ssrNr;
		this.forwardPrimerTemp = forwardPrimerTemp;
		this.reversePrimerTemp = reversePrimerTemp;
		this.elongationTemp = elongationTemp;
		this.fragmentSizeExpected = fragmentSizeExpected;
		this.fragmentSizeObserved = fragmentSizeObserved;
		this.expectedProductSize = expectedProductSize;
		this.positionOnReferenceSequence = positionOnReferenceSequence;
		this.restrictionEnzymeForAssay = restrictionEnzymeForAssay;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getNoOfRepeats() {
		return this.noOfRepeats;
	}

	public void setNoOfRepeats(Integer noOfRepeats) {
		this.noOfRepeats = noOfRepeats;
	}

	public String getMotifType() {
		return this.motifType;
	}

	public void setMotifType(String motifType) {
		this.motifType = motifType;
	}

	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Integer getSequenceLength() {
		return this.sequenceLength;
	}

	public void setSequenceLength(Integer sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	public Integer getMinAllele() {
		return this.minAllele;
	}

	public void setMinAllele(Integer minAllele) {
		this.minAllele = minAllele;
	}

	public Integer getMaxAllele() {
		return this.maxAllele;
	}

	public void setMaxAllele(Integer maxAllele) {
		this.maxAllele = maxAllele;
	}

	public Integer getSsrNr() {
		return this.ssrNr;
	}

	public void setSsrNr(Integer ssrNr) {
		this.ssrNr = ssrNr;
	}

	public Float getForwardPrimerTemp() {
		return this.forwardPrimerTemp;
	}

	public void setForwardPrimerTemp(Float forwardPrimerTemp) {
		this.forwardPrimerTemp = forwardPrimerTemp;
	}

	public Float getReversePrimerTemp() {
		return this.reversePrimerTemp;
	}

	public void setReversePrimerTemp(Float reversePrimerTemp) {
		this.reversePrimerTemp = reversePrimerTemp;
	}

	public Float getElongationTemp() {
		return this.elongationTemp;
	}

	public void setElongationTemp(Float elongationTemp) {
		this.elongationTemp = elongationTemp;
	}

	public Integer getFragmentSizeExpected() {
		return this.fragmentSizeExpected;
	}

	public void setFragmentSizeExpected(Integer fragmentSizeExpected) {
		this.fragmentSizeExpected = fragmentSizeExpected;
	}

	public Integer getFragmentSizeObserved() {
		return this.fragmentSizeObserved;
	}

	public void setFragmentSizeObserved(Integer fragmentSizeObserved) {
		this.fragmentSizeObserved = fragmentSizeObserved;
	}

	public Integer getExpectedProductSize() {
		return this.expectedProductSize;
	}

	public void setExpectedProductSize(Integer expectedProductSize) {
		this.expectedProductSize = expectedProductSize;
	}

	public Integer getPositionOnReferenceSequence() {
		return this.positionOnReferenceSequence;
	}

	public void setPositionOnReferenceSequence(Integer positionOnReferenceSequence) {
		this.positionOnReferenceSequence = positionOnReferenceSequence;
	}

	public String getRestrictionEnzymeForAssay() {
		return this.restrictionEnzymeForAssay;
	}

	public void setRestrictionEnzymeForAssay(String restrictionEnzymeForAssay) {
		this.restrictionEnzymeForAssay = restrictionEnzymeForAssay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.elongationTemp == null ? 0 : this.elongationTemp.hashCode());
		result = prime * result + (this.expectedProductSize == null ? 0 : this.expectedProductSize.hashCode());
		result = prime * result + (this.forwardPrimerTemp == null ? 0 : this.forwardPrimerTemp.hashCode());
		result = prime * result + (this.fragmentSizeExpected == null ? 0 : this.fragmentSizeExpected.hashCode());
		result = prime * result + (this.fragmentSizeObserved == null ? 0 : this.fragmentSizeObserved.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.maxAllele == null ? 0 : this.maxAllele.hashCode());
		result = prime * result + (this.minAllele == null ? 0 : this.minAllele.hashCode());
		result = prime * result + (this.motifType == null ? 0 : this.motifType.hashCode());
		result = prime * result + (this.noOfRepeats == null ? 0 : this.noOfRepeats.hashCode());
		result = prime * result + (this.positionOnReferenceSequence == null ? 0 : this.positionOnReferenceSequence.hashCode());
		result = prime * result + (this.restrictionEnzymeForAssay == null ? 0 : this.restrictionEnzymeForAssay.hashCode());
		result = prime * result + (this.reversePrimerTemp == null ? 0 : this.reversePrimerTemp.hashCode());
		result = prime * result + (this.sequence == null ? 0 : this.sequence.hashCode());
		result = prime * result + (this.sequenceLength == null ? 0 : this.sequenceLength.hashCode());
		result = prime * result + (this.ssrNr == null ? 0 : this.ssrNr.hashCode());
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
		MarkerDetails other = (MarkerDetails) obj;
		if (this.elongationTemp == null) {
			if (other.elongationTemp != null) {
				return false;
			}
		} else if (!this.elongationTemp.equals(other.elongationTemp)) {
			return false;
		}
		if (this.expectedProductSize == null) {
			if (other.expectedProductSize != null) {
				return false;
			}
		} else if (!this.expectedProductSize.equals(other.expectedProductSize)) {
			return false;
		}
		if (this.forwardPrimerTemp == null) {
			if (other.forwardPrimerTemp != null) {
				return false;
			}
		} else if (!this.forwardPrimerTemp.equals(other.forwardPrimerTemp)) {
			return false;
		}
		if (this.fragmentSizeExpected == null) {
			if (other.fragmentSizeExpected != null) {
				return false;
			}
		} else if (!this.fragmentSizeExpected.equals(other.fragmentSizeExpected)) {
			return false;
		}
		if (this.fragmentSizeObserved == null) {
			if (other.fragmentSizeObserved != null) {
				return false;
			}
		} else if (!this.fragmentSizeObserved.equals(other.fragmentSizeObserved)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.maxAllele == null) {
			if (other.maxAllele != null) {
				return false;
			}
		} else if (!this.maxAllele.equals(other.maxAllele)) {
			return false;
		}
		if (this.minAllele == null) {
			if (other.minAllele != null) {
				return false;
			}
		} else if (!this.minAllele.equals(other.minAllele)) {
			return false;
		}
		if (this.motifType == null) {
			if (other.motifType != null) {
				return false;
			}
		} else if (!this.motifType.equals(other.motifType)) {
			return false;
		}
		if (this.noOfRepeats == null) {
			if (other.noOfRepeats != null) {
				return false;
			}
		} else if (!this.noOfRepeats.equals(other.noOfRepeats)) {
			return false;
		}
		if (this.positionOnReferenceSequence == null) {
			if (other.positionOnReferenceSequence != null) {
				return false;
			}
		} else if (!this.positionOnReferenceSequence.equals(other.positionOnReferenceSequence)) {
			return false;
		}
		if (this.restrictionEnzymeForAssay == null) {
			if (other.restrictionEnzymeForAssay != null) {
				return false;
			}
		} else if (!this.restrictionEnzymeForAssay.equals(other.restrictionEnzymeForAssay)) {
			return false;
		}
		if (this.reversePrimerTemp == null) {
			if (other.reversePrimerTemp != null) {
				return false;
			}
		} else if (!this.reversePrimerTemp.equals(other.reversePrimerTemp)) {
			return false;
		}
		if (this.sequence == null) {
			if (other.sequence != null) {
				return false;
			}
		} else if (!this.sequence.equals(other.sequence)) {
			return false;
		}
		if (this.sequenceLength == null) {
			if (other.sequenceLength != null) {
				return false;
			}
		} else if (!this.sequenceLength.equals(other.sequenceLength)) {
			return false;
		}
		if (this.ssrNr == null) {
			if (other.ssrNr != null) {
				return false;
			}
		} else if (!this.ssrNr.equals(other.ssrNr)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerDetails [markerId=");
		builder.append(this.markerId);
		builder.append(", noOfRepeats=");
		builder.append(this.noOfRepeats);
		builder.append(", motifType=");
		builder.append(this.motifType);
		builder.append(", sequence=");
		builder.append(this.sequence);
		builder.append(", sequenceLength=");
		builder.append(this.sequenceLength);
		builder.append(", minAllele=");
		builder.append(this.minAllele);
		builder.append(", maxAllele=");
		builder.append(this.maxAllele);
		builder.append(", ssrNr=");
		builder.append(this.ssrNr);
		builder.append(", forwardPrimerTemp=");
		builder.append(this.forwardPrimerTemp);
		builder.append(", reversePrimerTemp=");
		builder.append(this.reversePrimerTemp);
		builder.append(", elongationTemp=");
		builder.append(this.elongationTemp);
		builder.append(", fragmentSizeExpected=");
		builder.append(this.fragmentSizeExpected);
		builder.append(", fragmentSizeObserved=");
		builder.append(this.fragmentSizeObserved);
		builder.append(", expectedProductSize=");
		builder.append(this.expectedProductSize);
		builder.append(", positionOnReferenceSequence=");
		builder.append(this.positionOnReferenceSequence);
		builder.append(", restrictionEnzymeForAssay=");
		builder.append(this.restrictionEnzymeForAssay);
		builder.append("]");
		return builder.toString();
	}

}
