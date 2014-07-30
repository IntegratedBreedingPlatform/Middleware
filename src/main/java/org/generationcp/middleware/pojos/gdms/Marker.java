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
import java.util.Comparator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_marker table.
 * 
 * @author Mark Agarrado 
 * <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@Entity
@Table(name = "gdms_marker")
public class Marker implements Serializable{

    private static final long serialVersionUID = 1L;
      
    @Id
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    @Basic(optional = false)
    @Column(name = "marker_type")
    private String markerType;

    @Basic(optional = false)
    @Column(name = "marker_name")
    private String markerName;

    @Basic(optional = false)
    @Column(name = "species")
    private String species;

    @Column(name = "db_accession_id")
    private String dbAccessionId;

    @Column(name = "reference")
    private String reference;

    @Column(name = "genotype")
    private String genotype;

    @Column(name = "ploidy")
    private String ploidy;

    @Column(name = "primer_id")
    private String primerId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "assay_type")
    private String assayType;

    @Column(name = "motif")
    private String motif;

    @Column(name = "forward_primer")
    private String forwardPrimer;

    @Column(name = "reverse_primer")
    private String reversePrimer;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "annealing_temp")
    private Float annealingTemp;

    @Column(name = "amplification")
    private String amplification;

    public Marker() {
    }

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
    
    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDbAccessionId() {
        return dbAccessionId;
    }

    public void setDbAccessionId(String dbAccessionId) {
        this.dbAccessionId = dbAccessionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getGenotype() {
        return genotype;
    }

    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    public String getPloidy() {
        return ploidy;
    }

    public void setPloidy(String ploidy) {
        this.ploidy = ploidy;
    }

    public String getPrimerId() {
        return primerId;
    }

    public void setPrimerId(String primerId) {
        this.primerId = primerId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAssayType() {
        return assayType;
    }

    public void setAssayType(String assayType) {
        this.assayType = assayType;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getForwardPrimer() {
        return forwardPrimer;
    }

    public void setForwardPrimer(String forwardPrimer) {
        this.forwardPrimer = forwardPrimer;
    }

    public String getReversePrimer() {
        return reversePrimer;
    }

    public void setReversePrimer(String reversePrimer) {
        this.reversePrimer = reversePrimer;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public Float getAnnealingTemp() {
        return annealingTemp;
    }

    public void setAnnealingTemp(Float annealingTemp) {
        this.annealingTemp = annealingTemp;
    }

    public String getAmplification() {
        return amplification;
    }

    public void setAmplification(String amplification) {
        this.amplification = amplification;
    }

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(61, 131).append(markerId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Marker [markerId=");
        builder.append(markerId);
        builder.append(", markerType=");
        builder.append(markerType);
        builder.append(", markerName=");
        builder.append(markerName);
        builder.append(", species=");
        builder.append(species);
        builder.append(", dbAccessionId=");
        builder.append(dbAccessionId);
        builder.append(", reference=");
        builder.append(reference);
        builder.append(", genotype=");
        builder.append(genotype);
        builder.append(", ploidy=");
        builder.append(ploidy);
        builder.append(", primerId=");
        builder.append(primerId);
        builder.append(", remarks=");
        builder.append(remarks);
        builder.append(", assayType=");
        builder.append(assayType);
        builder.append(", motif=");
        builder.append(motif);
        builder.append(", forwardPrimer=");
        builder.append(forwardPrimer);
        builder.append(", reversePrimer=");
        builder.append(reversePrimer);
        builder.append(", productSize=");
        builder.append(productSize);
        builder.append(", annealingTemp=");
        builder.append(annealingTemp);
        builder.append(", amplification=");
        builder.append(amplification);
        builder.append("]");
        return builder.toString();
    }

    public static Comparator<Marker> markerComparator = new Comparator<Marker>() { 

        @Override
        public int compare(Marker element1, Marker element2) {
            String markerName1 = element1.getMarkerName();
            String markerName2 = element2.getMarkerName();
            return markerName1.compareToIgnoreCase(markerName2);
        }

};

}
