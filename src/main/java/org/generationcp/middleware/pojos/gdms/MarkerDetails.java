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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * POJO for gdms_marker_details table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_details")
public class MarkerDetails implements Serializable{

    /** The Constant serialVersionUID. */
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
        return markerId;
    }

    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    
    public Integer getNoOfRepeats() {
        return noOfRepeats;
    }

    
    public void setNoOfRepeats(Integer noOfRepeats) {
        this.noOfRepeats = noOfRepeats;
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

    
    public Integer getSsrNr() {
        return ssrNr;
    }

    
    public void setSsrNr(Integer ssrNr) {
        this.ssrNr = ssrNr;
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

    
    public Integer getPositionOnReferenceSequence() {
        return positionOnReferenceSequence;
    }

    
    public void setPositionOnReferenceSequence(Integer positionOnReferenceSequence) {
        this.positionOnReferenceSequence = positionOnReferenceSequence;
    }

    
    public String getRestrictionEnzymeForAssay() {
        return restrictionEnzymeForAssay;
    }

    
    public void setRestrictionEnzymeForAssay(String restrictionEnzymeForAssay) {
        this.restrictionEnzymeForAssay = restrictionEnzymeForAssay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elongationTemp == null) ? 0 : elongationTemp.hashCode());
        result = prime * result + ((expectedProductSize == null) ? 0 : expectedProductSize.hashCode());
        result = prime * result + ((forwardPrimerTemp == null) ? 0 : forwardPrimerTemp.hashCode());
        result = prime * result + ((fragmentSizeExpected == null) ? 0 : fragmentSizeExpected.hashCode());
        result = prime * result + ((fragmentSizeObserved == null) ? 0 : fragmentSizeObserved.hashCode());
        result = prime * result + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result + ((maxAllele == null) ? 0 : maxAllele.hashCode());
        result = prime * result + ((minAllele == null) ? 0 : minAllele.hashCode());
        result = prime * result + ((motifType == null) ? 0 : motifType.hashCode());
        result = prime * result + ((noOfRepeats == null) ? 0 : noOfRepeats.hashCode());
        result = prime * result + ((positionOnReferenceSequence == null) ? 0 : positionOnReferenceSequence.hashCode());
        result = prime * result + ((restrictionEnzymeForAssay == null) ? 0 : restrictionEnzymeForAssay.hashCode());
        result = prime * result + ((reversePrimerTemp == null) ? 0 : reversePrimerTemp.hashCode());
        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
        result = prime * result + ((sequenceLength == null) ? 0 : sequenceLength.hashCode());
        result = prime * result + ((ssrNr == null) ? 0 : ssrNr.hashCode());
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
        MarkerDetails other = (MarkerDetails) obj;
        if (elongationTemp == null) {
            if (other.elongationTemp != null)
                return false;
        } else if (!elongationTemp.equals(other.elongationTemp))
            return false;
        if (expectedProductSize == null) {
            if (other.expectedProductSize != null)
                return false;
        } else if (!expectedProductSize.equals(other.expectedProductSize))
            return false;
        if (forwardPrimerTemp == null) {
            if (other.forwardPrimerTemp != null)
                return false;
        } else if (!forwardPrimerTemp.equals(other.forwardPrimerTemp))
            return false;
        if (fragmentSizeExpected == null) {
            if (other.fragmentSizeExpected != null)
                return false;
        } else if (!fragmentSizeExpected.equals(other.fragmentSizeExpected))
            return false;
        if (fragmentSizeObserved == null) {
            if (other.fragmentSizeObserved != null)
                return false;
        } else if (!fragmentSizeObserved.equals(other.fragmentSizeObserved))
            return false;
        if (markerId == null) {
            if (other.markerId != null)
                return false;
        } else if (!markerId.equals(other.markerId))
            return false;
        if (maxAllele == null) {
            if (other.maxAllele != null)
                return false;
        } else if (!maxAllele.equals(other.maxAllele))
            return false;
        if (minAllele == null) {
            if (other.minAllele != null)
                return false;
        } else if (!minAllele.equals(other.minAllele))
            return false;
        if (motifType == null) {
            if (other.motifType != null)
                return false;
        } else if (!motifType.equals(other.motifType))
            return false;
        if (noOfRepeats == null) {
            if (other.noOfRepeats != null)
                return false;
        } else if (!noOfRepeats.equals(other.noOfRepeats))
            return false;
        if (positionOnReferenceSequence == null) {
            if (other.positionOnReferenceSequence != null)
                return false;
        } else if (!positionOnReferenceSequence.equals(other.positionOnReferenceSequence))
            return false;
        if (restrictionEnzymeForAssay == null) {
            if (other.restrictionEnzymeForAssay != null)
                return false;
        } else if (!restrictionEnzymeForAssay.equals(other.restrictionEnzymeForAssay))
            return false;
        if (reversePrimerTemp == null) {
            if (other.reversePrimerTemp != null)
                return false;
        } else if (!reversePrimerTemp.equals(other.reversePrimerTemp))
            return false;
        if (sequence == null) {
            if (other.sequence != null)
                return false;
        } else if (!sequence.equals(other.sequence))
            return false;
        if (sequenceLength == null) {
            if (other.sequenceLength != null)
                return false;
        } else if (!sequenceLength.equals(other.sequenceLength))
            return false;
        if (ssrNr == null) {
            if (other.ssrNr != null)
                return false;
        } else if (!ssrNr.equals(other.ssrNr))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerDetails [markerId=");
        builder.append(markerId);
        builder.append(", noOfRepeats=");
        builder.append(noOfRepeats);
        builder.append(", motifType=");
        builder.append(motifType);
        builder.append(", sequence=");
        builder.append(sequence);
        builder.append(", sequenceLength=");
        builder.append(sequenceLength);
        builder.append(", minAllele=");
        builder.append(minAllele);
        builder.append(", maxAllele=");
        builder.append(maxAllele);
        builder.append(", ssrNr=");
        builder.append(ssrNr);
        builder.append(", forwardPrimerTemp=");
        builder.append(forwardPrimerTemp);
        builder.append(", reversePrimerTemp=");
        builder.append(reversePrimerTemp);
        builder.append(", elongationTemp=");
        builder.append(elongationTemp);
        builder.append(", fragmentSizeExpected=");
        builder.append(fragmentSizeExpected);
        builder.append(", fragmentSizeObserved=");
        builder.append(fragmentSizeObserved);
        builder.append(", expectedProductSize=");
        builder.append(expectedProductSize);
        builder.append(", positionOnReferenceSequence=");
        builder.append(positionOnReferenceSequence);
        builder.append(", restrictionEnzymeForAssay=");
        builder.append(restrictionEnzymeForAssay);
        builder.append("]");
        return builder.toString();
    }
    
}
