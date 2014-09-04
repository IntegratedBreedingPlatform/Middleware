package org.generationcp.middleware.pojos.gdms;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 9/4/2014
 * Time: 10:58 AM
 */

@Entity(name = "ExtendedMarkerInfo")
public class ExtendedMarkerInfo extends MarkerInfo{

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
        return serialVersionUID;
    }

    public Integer getNumberOfRepeats() {
        return numberOfRepeats;
    }

    public void setNumberOfRepeats(Integer numberOfRepeats) {
        this.numberOfRepeats = numberOfRepeats;
    }

    public String getMotifType() {
        return motifType;
    }

    public void setMotifType(String motifType) {
        this.motifType = motifType;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Integer getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(Integer sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public Integer getMinAllele() {
        return minAllele;
    }

    public void setMinAllele(Integer minAllele) {
        this.minAllele = minAllele;
    }

    public Integer getMaxAllele() {
        return maxAllele;
    }

    public void setMaxAllele(Integer maxAllele) {
        this.maxAllele = maxAllele;
    }

    public Integer getSsrNumber() {
        return ssrNumber;
    }

    public void setSsrNumber(Integer ssrNumber) {
        this.ssrNumber = ssrNumber;
    }

    public Float getForwardPrimerTemp() {
        return forwardPrimerTemp;
    }

    public void setForwardPrimerTemp(Float forwardPrimerTemp) {
        this.forwardPrimerTemp = forwardPrimerTemp;
    }

    public Float getReversePrimerTemp() {
        return reversePrimerTemp;
    }

    public void setReversePrimerTemp(Float reversePrimerTemp) {
        this.reversePrimerTemp = reversePrimerTemp;
    }

    public Float getElongationTemp() {
        return elongationTemp;
    }

    public void setElongationTemp(Float elongationTemp) {
        this.elongationTemp = elongationTemp;
    }

    public Integer getFragmentSizeExpected() {
        return fragmentSizeExpected;
    }

    public void setFragmentSizeExpected(Integer fragmentSizeExpected) {
        this.fragmentSizeExpected = fragmentSizeExpected;
    }

    public Integer getFragmentSizeObserved() {
        return fragmentSizeObserved;
    }

    public void setFragmentSizeObserved(Integer fragmentSizeObserved) {
        this.fragmentSizeObserved = fragmentSizeObserved;
    }

    public Integer getExpectedProductSize() {
        return expectedProductSize;
    }

    public void setExpectedProductSize(Integer expectedProductSize) {
        this.expectedProductSize = expectedProductSize;
    }

    public Integer getPositionOnSequence() {
        return positionOnSequence;
    }

    public void setPositionOnSequence(Integer positionOnSequence) {
        this.positionOnSequence = positionOnSequence;
    }

    public String getRestrictionEnzyme() {
        return restrictionEnzyme;
    }

    public void setRestrictionEnzyme(String restrictionEnzyme) {
        this.restrictionEnzyme = restrictionEnzyme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExtendedMarkerInfo that = (ExtendedMarkerInfo) o;

        if (elongationTemp != null ? !elongationTemp.equals(that.elongationTemp) : that.elongationTemp != null) return false;
        if (expectedProductSize != null ? !expectedProductSize.equals(that.expectedProductSize) : that.expectedProductSize != null) return false;
        if (forwardPrimerTemp != null ? !forwardPrimerTemp.equals(that.forwardPrimerTemp) : that.forwardPrimerTemp != null) return false;
        if (fragmentSizeExpected != null ? !fragmentSizeExpected.equals(that.fragmentSizeExpected) : that.fragmentSizeExpected != null) return false;
        if (fragmentSizeObserved != null ? !fragmentSizeObserved.equals(that.fragmentSizeObserved) : that.fragmentSizeObserved != null) return false;
        if (maxAllele != null ? !maxAllele.equals(that.maxAllele) : that.maxAllele != null) return false;
        if (minAllele != null ? !minAllele.equals(that.minAllele) : that.minAllele != null) return false;
        if (motifType != null ? !motifType.equals(that.motifType) : that.motifType != null) return false;
        if (numberOfRepeats != null ? !numberOfRepeats.equals(that.numberOfRepeats) : that.numberOfRepeats != null) return false;
        if (positionOnSequence != null ? !positionOnSequence.equals(that.positionOnSequence) : that.positionOnSequence != null) return false;
        if (restrictionEnzyme != null ? !restrictionEnzyme.equals(that.restrictionEnzyme) : that.restrictionEnzyme != null) return false;
        if (reversePrimerTemp != null ? !reversePrimerTemp.equals(that.reversePrimerTemp) : that.reversePrimerTemp != null) return false;
        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (sequenceLength != null ? !sequenceLength.equals(that.sequenceLength) : that.sequenceLength != null) return false;
        if (ssrNumber != null ? !ssrNumber.equals(that.ssrNumber) : that.ssrNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (numberOfRepeats != null ? numberOfRepeats.hashCode() : 0);
        result = 31 * result + (motifType != null ? motifType.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (sequenceLength != null ? sequenceLength.hashCode() : 0);
        result = 31 * result + (minAllele != null ? minAllele.hashCode() : 0);
        result = 31 * result + (maxAllele != null ? maxAllele.hashCode() : 0);
        result = 31 * result + (ssrNumber != null ? ssrNumber.hashCode() : 0);
        result = 31 * result + (forwardPrimerTemp != null ? forwardPrimerTemp.hashCode() : 0);
        result = 31 * result + (reversePrimerTemp != null ? reversePrimerTemp.hashCode() : 0);
        result = 31 * result + (elongationTemp != null ? elongationTemp.hashCode() : 0);
        result = 31 * result + (fragmentSizeExpected != null ? fragmentSizeExpected.hashCode() : 0);
        result = 31 * result + (fragmentSizeObserved != null ? fragmentSizeObserved.hashCode() : 0);
        result = 31 * result + (expectedProductSize != null ? expectedProductSize.hashCode() : 0);
        result = 31 * result + (positionOnSequence != null ? positionOnSequence.hashCode() : 0);
        result = 31 * result + (restrictionEnzyme != null ? restrictionEnzyme.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExtendedMarkerInfo{" + super.toString() +
                "numberOfRepeats=" + numberOfRepeats +
                ", motifType='" + motifType + '\'' +
                ", sequence='" + sequence + '\'' +
                ", sequenceLength=" + sequenceLength +
                ", minAllele=" + minAllele +
                ", maxAllele=" + maxAllele +
                ", ssrNumber=" + ssrNumber +
                ", forwardPrimerTemp=" + forwardPrimerTemp +
                ", reversePrimerTemp=" + reversePrimerTemp +
                ", elongationTemp=" + elongationTemp +
                ", fragmentSizeExpected=" + fragmentSizeExpected +
                ", fragmentSizeObserved=" + fragmentSizeObserved +
                ", expectedProductSize=" + expectedProductSize +
                ", positionOnSequence=" + positionOnSequence +
                ", restrictionEnzyme='" + restrictionEnzyme + '\'' +
                '}';
    }
}
