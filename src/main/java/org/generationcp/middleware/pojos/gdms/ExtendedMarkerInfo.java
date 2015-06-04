
package org.generationcp.middleware.pojos.gdms;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 9/4/2014 Time: 10:58 AM
 */

@Entity(name = "ExtendedMarkerInfo")
public class ExtendedMarkerInfo extends MarkerInfo {

	private static final long serialVersionUID = 1L;

	@Column(name = "no_of_repeats")
	private Integer numberOfRepeats;

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
	private Integer ssrNumber;

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
	private Integer positionOnSequence;

	@Column(name = "restriction_enzyme_for_assay")
	private String restrictionEnzyme;

	public ExtendedMarkerInfo() {
		super();
	}

	public ExtendedMarkerInfo(Integer markerId, String markerType, String markerName, String species, String accessionId, String reference,
			String genotype, String ploidy, String motif, String forwardPrimer, String reversePrimer, String productSize,
			Float annealingTemp, String amplification, String principalInvestigator, String contact, String institute,
			BigInteger genotypesCount) {
		super(markerId, markerType, markerName, species, accessionId, reference, genotype, ploidy, motif, forwardPrimer, reversePrimer,
				productSize, annealingTemp, amplification, principalInvestigator, contact, institute, genotypesCount);
	}

	public ExtendedMarkerInfo(Integer markerId, String markerType, String markerName, String species, String accessionId, String reference,
			String genotype, String ploidy, String motif, String forwardPrimer, String reversePrimer, String productSize,
			Float annealingTemp, String amplification, String principalInvestigator, String contact, String institute,
			BigInteger genotypesCount, Integer numberOfRepeats, String motifType, String sequence, Integer sequenceLength,
			Integer minAllele, Integer maxAllele, Integer ssrNumber, Float forwardPrimerTemp, Float reversePrimerTemp,
			Float elongationTemp, Integer fragmentSizeExpected, Integer fragmentSizeObserved, Integer expectedProductSize,
			Integer positionOnSequence, String restrictionEnzyme) {
		super(markerId, markerType, markerName, species, accessionId, reference, genotype, ploidy, motif, forwardPrimer, reversePrimer,
				productSize, annealingTemp, amplification, principalInvestigator, contact, institute, genotypesCount);
		this.numberOfRepeats = numberOfRepeats;
		this.motifType = motifType;
		this.sequence = sequence;
		this.sequenceLength = sequenceLength;
		this.minAllele = minAllele;
		this.maxAllele = maxAllele;
		this.ssrNumber = ssrNumber;
		this.forwardPrimerTemp = forwardPrimerTemp;
		this.reversePrimerTemp = reversePrimerTemp;
		this.elongationTemp = elongationTemp;
		this.fragmentSizeExpected = fragmentSizeExpected;
		this.fragmentSizeObserved = fragmentSizeObserved;
		this.expectedProductSize = expectedProductSize;
		this.positionOnSequence = positionOnSequence;
		this.restrictionEnzyme = restrictionEnzyme;
	}

	public static long getSerialVersionUID() {
		return ExtendedMarkerInfo.serialVersionUID;
	}

	public Integer getNumberOfRepeats() {
		return this.numberOfRepeats;
	}

	public void setNumberOfRepeats(Integer numberOfRepeats) {
		this.numberOfRepeats = numberOfRepeats;
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

	public Integer getSsrNumber() {
		return this.ssrNumber;
	}

	public void setSsrNumber(Integer ssrNumber) {
		this.ssrNumber = ssrNumber;
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

	public Integer getPositionOnSequence() {
		return this.positionOnSequence;
	}

	public void setPositionOnSequence(Integer positionOnSequence) {
		this.positionOnSequence = positionOnSequence;
	}

	public String getRestrictionEnzyme() {
		return this.restrictionEnzyme;
	}

	public void setRestrictionEnzyme(String restrictionEnzyme) {
		this.restrictionEnzyme = restrictionEnzyme;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		ExtendedMarkerInfo that = (ExtendedMarkerInfo) o;

		if (this.elongationTemp != null ? !this.elongationTemp.equals(that.elongationTemp) : that.elongationTemp != null) {
			return false;
		}
		if (this.expectedProductSize != null ? !this.expectedProductSize.equals(that.expectedProductSize)
				: that.expectedProductSize != null) {
			return false;
		}
		if (this.forwardPrimerTemp != null ? !this.forwardPrimerTemp.equals(that.forwardPrimerTemp) : that.forwardPrimerTemp != null) {
			return false;
		}
		if (this.fragmentSizeExpected != null ? !this.fragmentSizeExpected.equals(that.fragmentSizeExpected)
				: that.fragmentSizeExpected != null) {
			return false;
		}
		if (this.fragmentSizeObserved != null ? !this.fragmentSizeObserved.equals(that.fragmentSizeObserved)
				: that.fragmentSizeObserved != null) {
			return false;
		}
		if (this.maxAllele != null ? !this.maxAllele.equals(that.maxAllele) : that.maxAllele != null) {
			return false;
		}
		if (this.minAllele != null ? !this.minAllele.equals(that.minAllele) : that.minAllele != null) {
			return false;
		}
		if (this.motifType != null ? !this.motifType.equals(that.motifType) : that.motifType != null) {
			return false;
		}
		if (this.numberOfRepeats != null ? !this.numberOfRepeats.equals(that.numberOfRepeats) : that.numberOfRepeats != null) {
			return false;
		}
		if (this.positionOnSequence != null ? !this.positionOnSequence.equals(that.positionOnSequence) : that.positionOnSequence != null) {
			return false;
		}
		if (this.restrictionEnzyme != null ? !this.restrictionEnzyme.equals(that.restrictionEnzyme) : that.restrictionEnzyme != null) {
			return false;
		}
		if (this.reversePrimerTemp != null ? !this.reversePrimerTemp.equals(that.reversePrimerTemp) : that.reversePrimerTemp != null) {
			return false;
		}
		if (this.sequence != null ? !this.sequence.equals(that.sequence) : that.sequence != null) {
			return false;
		}
		if (this.sequenceLength != null ? !this.sequenceLength.equals(that.sequenceLength) : that.sequenceLength != null) {
			return false;
		}
		if (this.ssrNumber != null ? !this.ssrNumber.equals(that.ssrNumber) : that.ssrNumber != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (this.numberOfRepeats != null ? this.numberOfRepeats.hashCode() : 0);
		result = 31 * result + (this.motifType != null ? this.motifType.hashCode() : 0);
		result = 31 * result + (this.sequence != null ? this.sequence.hashCode() : 0);
		result = 31 * result + (this.sequenceLength != null ? this.sequenceLength.hashCode() : 0);
		result = 31 * result + (this.minAllele != null ? this.minAllele.hashCode() : 0);
		result = 31 * result + (this.maxAllele != null ? this.maxAllele.hashCode() : 0);
		result = 31 * result + (this.ssrNumber != null ? this.ssrNumber.hashCode() : 0);
		result = 31 * result + (this.forwardPrimerTemp != null ? this.forwardPrimerTemp.hashCode() : 0);
		result = 31 * result + (this.reversePrimerTemp != null ? this.reversePrimerTemp.hashCode() : 0);
		result = 31 * result + (this.elongationTemp != null ? this.elongationTemp.hashCode() : 0);
		result = 31 * result + (this.fragmentSizeExpected != null ? this.fragmentSizeExpected.hashCode() : 0);
		result = 31 * result + (this.fragmentSizeObserved != null ? this.fragmentSizeObserved.hashCode() : 0);
		result = 31 * result + (this.expectedProductSize != null ? this.expectedProductSize.hashCode() : 0);
		result = 31 * result + (this.positionOnSequence != null ? this.positionOnSequence.hashCode() : 0);
		result = 31 * result + (this.restrictionEnzyme != null ? this.restrictionEnzyme.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ExtendedMarkerInfo{" + super.toString() + "numberOfRepeats=" + this.numberOfRepeats + ", motifType='" + this.motifType
				+ '\'' + ", sequence='" + this.sequence + '\'' + ", sequenceLength=" + this.sequenceLength + ", minAllele="
				+ this.minAllele + ", maxAllele=" + this.maxAllele + ", ssrNumber=" + this.ssrNumber + ", forwardPrimerTemp="
				+ this.forwardPrimerTemp + ", reversePrimerTemp=" + this.reversePrimerTemp + ", elongationTemp=" + this.elongationTemp
				+ ", fragmentSizeExpected=" + this.fragmentSizeExpected + ", fragmentSizeObserved=" + this.fragmentSizeObserved
				+ ", expectedProductSize=" + this.expectedProductSize + ", positionOnSequence=" + this.positionOnSequence
				+ ", restrictionEnzyme='" + this.restrictionEnzyme + '\'' + '}';
	}
}
