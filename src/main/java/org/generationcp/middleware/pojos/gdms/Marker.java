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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Marker POJO
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@Entity
@Table(name = "marker")
public class Marker implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    public static final String GET_MARKER_TYPE_BY_MARKER_IDS = 
                "select distinct concat(marker_type, '') from marker where marker_id in (:markerIdList)";

    public static final String GET_IDS_BY_NAMES = "select marker_id from marker where marker_name in (:markerNameList)";

    /** The marker id. */
    @Id
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    /** The marker type. */
    @Basic(optional = false)
    @Column(name = "marker_type")
    private String markerType;

    /** The marker name. */
    @Basic(optional = false)
    @Column(name = "marker_name")
    private String markerName;

    /** The species. */
    @Basic(optional = false)
    @Column(name = "species")
    private String species;

    /** The db accession id. */
    @Column(name = "db_accession_id")
    private String dbAccessionId;

    /** The reference. */
    @Column(name = "reference")
    private String reference;

    /** The genotype. */
    @Column(name = "genotype")
    private String genotype;

    /** The ploidy. */
    @Column(name = "ploidy")
    private String ploidy;

    /** The primer id. */
    @Column(name = "primer_id")
    private String primerId;

    /** The remarks. */
    @Column(name = "remarks")
    private String remarks;

    /** The assay type. */
    @Column(name = "assay_type")
    private String assayType;

    /** The motif. */
    @Column(name = "motif")
    private String motif;

    /** The forward primer. */
    @Column(name = "forward_primer")
    private String forwardPrimer;

    /** The reverse primer. */
    @Column(name = "reverse_primer")
    private String reversePrimer;

    /** The product size. */
    @Column(name = "product_size")
    private String productSize;

    /** The annealing temp. */
    @Column(name = "annealing_temp")
    private Float annealingTemp;

    /** The amplification. */
    @Column(name = "amplification")
    private String amplification;

    
    /**
     * Instantiates a new marker.
     */
    public Marker() {
    }

    /**
     * Instantiates a new marker.
     *
     * @param markerId the marker id
     * @param markerType the marker type
     * @param markerName the marker name
     * @param species the species
     * @param dbAccessionId the db accession id
     * @param reference the reference
     * @param genotype the genotype
     * @param ploidy the ploidy
     * @param primerId the primer id
     * @param remarks the remarks
     * @param assayType the assay type
     * @param motif the motif
     * @param forwardPrimer the forward primer
     * @param reversePrimer the reverse primer
     * @param productSize the product size
     * @param annealingTemp the annealing temp
     * @param amplification the amplification
     */
    public Marker(Integer markerId,
                    String markerType,
                    String markerName,
                    String species,
                    String dbAccessionId,
                    String reference,
                    String genotype,
                    String ploidy,
                    String primerId,
                    String remarks,
                    String assayType,
                    String motif,
                    String forwardPrimer,
                    String reversePrimer,
                    String productSize,
                    Float annealingTemp,
                    String amplification) {
        
        this.markerId = markerId;
        this.markerType = markerType;
        this.markerName = markerName;
        this.species = species;
        this.dbAccessionId = dbAccessionId;
        this.reference = reference;
        this.genotype = genotype;
        this.ploidy = ploidy;
        this.primerId = primerId;
        this.remarks = remarks;
        this.assayType = assayType;
        this.motif = motif;
        this.forwardPrimer = forwardPrimer;
        this.reversePrimer = reversePrimer;
        this.productSize = productSize;
        this.annealingTemp = annealingTemp;
        this.amplification = amplification;
    }
    
    /**
     * Gets the marker id.
     *
     * @return the marker id
     */
    public Integer getMarkerId() {
        return markerId;
    }
    
    /**
     * Sets the marker id.
     *
     * @param markerId the new marker id
     */
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    /**
     * Gets the marker type.
     *
     * @return the marker type
     */
    public String getMarkerType() {
        return markerType;
    }

    /**
     * Sets the marker type.
     *
     * @param markerType the new marker type
     */
    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    /**
     * Gets the marker name.
     *
     * @return the marker name
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * Sets the marker name.
     *
     * @param markerName the new marker name
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    /**
     * Gets the species.
     *
     * @return the species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Sets the species.
     *
     * @param species the new species
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     * Gets the db accession id.
     *
     * @return the db accession id
     */
    public String getDbAccessionId() {
        return dbAccessionId;
    }

    /**
     * Sets the db accession id.
     *
     * @param dbAccessionId the new db accession id
     */
    public void setDbAccessionId(String dbAccessionId) {
        this.dbAccessionId = dbAccessionId;
    }

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     *
     * @param reference the new reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the genotype.
     *
     * @return the genotype
     */
    public String getGenotype() {
        return genotype;
    }

    /**
     * Sets the genotype.
     *
     * @param genotype the new genotype
     */
    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    /**
     * Gets the ploidy.
     *
     * @return the ploidy
     */
    public String getPloidy() {
        return ploidy;
    }

    /**
     * Sets the ploidy.
     *
     * @param ploidy the new ploidy
     */
    public void setPloidy(String ploidy) {
        this.ploidy = ploidy;
    }

    /**
     * Gets the primer id.
     *
     * @return the primer id
     */
    public String getPrimerId() {
        return primerId;
    }

    /**
     * Sets the primer id.
     *
     * @param primerId the new primer id
     */
    public void setPrimerId(String primerId) {
        this.primerId = primerId;
    }

    /**
     * Gets the remarks.
     *
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     *
     * @param remarks the new remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the assay type.
     *
     * @return the assay type
     */
    public String getAssayType() {
        return assayType;
    }

    /**
     * Sets the assay type.
     *
     * @param assayType the new assay type
     */
    public void setAssayType(String assayType) {
        this.assayType = assayType;
    }

    /**
     * Gets the motif.
     *
     * @return the motif
     */
    public String getMotif() {
        return motif;
    }

    /**
     * Sets the motif.
     *
     * @param motif the new motif
     */
    public void setMotif(String motif) {
        this.motif = motif;
    }

    /**
     * Gets the forward primer.
     *
     * @return the forward primer
     */
    public String getForwardPrimer() {
        return forwardPrimer;
    }

    /**
     * Sets the forward primer.
     *
     * @param forwardPrimer the new forward primer
     */
    public void setForwardPrimer(String forwardPrimer) {
        this.forwardPrimer = forwardPrimer;
    }

    /**
     * Gets the reverse primer.
     *
     * @return the reverse primer
     */
    public String getReversePrimer() {
        return reversePrimer;
    }

    /**
     * Sets the reverse primer.
     *
     * @param reversePrimer the new reverse primer
     */
    public void setReversePrimer(String reversePrimer) {
        this.reversePrimer = reversePrimer;
    }

    /**
     * Gets the product size.
     *
     * @return the product size
     */
    public String getProductSize() {
        return productSize;
    }

    /**
     * Sets the product size.
     *
     * @param productSize the new product size
     */
    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    /**
     * Gets the annealing temp.
     *
     * @return the annealing temp
     */
    public Float getAnnealingTemp() {
        return annealingTemp;
    }

    /**
     * Sets the annealing temp.
     *
     * @param annealingTemp the new annealing temp
     */
    public void setAnnealingTemp(Float annealingTemp) {
        this.annealingTemp = annealingTemp;
    }

    /**
     * Gets the amplification.
     *
     * @return the amplification
     */
    public String getAmplification() {
        return amplification;
    }

    /**
     * Sets the amplification.
     *
     * @param amplification the new amplification
     */
    public void setAmplification(String amplification) {
        this.amplification = amplification;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Marker)) {
            return false;
        }

        Marker rhs = (Marker) obj;
        return new EqualsBuilder().append(markerId, rhs.markerId).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(61, 131).append(markerId).toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Marker [markerId=" + markerId +
                        ", markerType=" + markerType +
                        ", markerName=" + markerName +
                        ", species=" + species +
                        ", dbAccessionId=" + dbAccessionId +
                        ", reference=" + reference +
                        ", genotype=" + genotype +
                        ", ploidy=" + ploidy +
                        ", primerId=" + primerId +
                        ", remarks=" + remarks +
                        ", assayType=" + assayType +
                        ", motif=" + motif +
                        ", forwardPrimer=" + forwardPrimer +
                        ", reversePrimer=" + reversePrimer +
                        ", productSize=" + productSize +
                        ", annealingTemp=" + annealingTemp +
                        ", amplification=" + amplification +"]";
    }

}
