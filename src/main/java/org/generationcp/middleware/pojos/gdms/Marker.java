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
    
    public static final String GET_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION = 
        "SELECT marker_id "
        + "FROM gdms_markers_onmap "
        + "WHERE map_id = :map_id "
        + "AND linkage_group = :linkage_group "
        + "AND start_position "
        + "BETWEEN :start_position "
        + "AND :end_position " 
        + "ORDER BY marker_id";
    
    public static final String COUNT_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION = 
        "SELECT COUNT(marker_id) "
        + "FROM gdms_markers_onmap "
        + "WHERE map_id = :map_id "
        + "AND linkage_group = :linkage_group "
        + "AND start_position "
        + "BETWEEN :start_position "
        + "AND :end_position";
            
    public static final String GET_MARKER_TYPE_BY_MARKER_IDS = 
            "SELECT DISTINCT CONCAT(marker_type, '') " +
            "FROM gdms_marker " +
            "WHERE marker_id IN (:markerIdList)";

    public static final String GET_IDS_BY_NAMES = 
            "SELECT marker_id " +
            "FROM gdms_marker " +
            "WHERE marker_name IN (:markerNameList)";

    public static final String GET_ID_AND_NAME_BY_NAMES = 
            "SELECT marker_id, CONCAT(marker_name,'') " +
            "FROM gdms_marker " +
            "WHERE marker_name IN (:markerNameList) " +
            "ORDER BY marker_id ";
    
    public static final String GET_NAMES_BY_IDS = 
            "SELECT marker_id, CONCAT(marker_name, '') AS marker_name " +
            "FROM gdms_marker " +
            "WHERE marker_id IN (:markerIdList) " +
            "ORDER BY marker_id asc";
    
    public static final String GET_ALL_MARKER_TYPES = 
            "SELECT DISTINCT CONCAT(marker_type, '') " +
            "FROM gdms_marker " +
            "WHERE UPPER(marker_type) != 'UA'";
    
    public static final String GET_NAMES_BY_TYPE = 
            "SELECT DISTINCT CONCAT(marker_name, '') " +
            "FROM gdms_marker " +
            "WHERE UPPER(marker_type) = UPPER(:markerType)";
    
    public static final String COUNT_ALL_MARKER_TYPES = 
            "SELECT COUNT(DISTINCT marker_type) " +
            "FROM gdms_marker " +
            "WHERE UPPER(marker_type) != 'UA'";
    
    public static final String COUNT_MARKER_NAMES_BY_MARKER_TYPE = 
            "SELECT COUNT(DISTINCT marker_name) " +
            "FROM gdms_marker " +
            "WHERE UPPER(marker_type) = UPPER(:markerType)";
    
    // For getMarkerNamesByGIds()
    public static final String GET_ALLELE_MARKER_NAMES_BY_GID = 
            "SELECT DISTINCT gdms_allele_values.gid, CONCAT(gdms_marker.marker_name,'') " +
            "FROM gdms_allele_values JOIN gdms_marker ON gdms_allele_values.marker_id = gdms_marker.marker_id " +
            "WHERE gdms_allele_values.gid IN (:gIdList) " +
            "ORDER BY gid, marker_name";

    public static final String GET_CHAR_MARKER_NAMES_BY_GID =         
            "SELECT DISTINCT gdms_char_values.gid, CONCAT(gdms_marker.marker_name,'') " +
            "FROM gdms_char_values JOIN gdms_marker ON gdms_char_values.marker_id = gdms_marker.marker_id " +
            "WHERE gdms_char_values.gid IN (:gIdList) " +
            "ORDER BY gid, marker_name";

    public static final String GET_MAPPING_MARKER_NAMES_BY_GID = 
            "SELECT DISTINCT gdms_mapping_pop_values.gid, CONCAT(gdms_marker.marker_name,'') " + 
            "FROM gdms_mapping_pop_values JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id " +
            "WHERE gdms_mapping_pop_values.gid IN (:gIdList) " +
            "ORDER BY gid, marker_name";

    public static final String GET_MARKER_IDS_BY_HAPLOTYPE = "SELECT track.marker_id  "
                    + "FROM gdms_track_markers track "
                    + "INNER JOIN gdms_track_data tdata ON (tdata.track_id = track.track_id) "
                    + "WHERE track_name = (:trackName)";

    public static final String GET_SNP_MARKERS_BY_HAPLOTYPE = "SELECT gdms.marker_id  "
                        + ", CONCAT(marker_type, '') "
                        + ", CONCAT(marker_name, '') "
                        + ", CONCAT(species, '') "
                        + ", db_accession_id "
                        + ", reference "
                        + ", CONCAT(genotype, '') "
                        + ", ploidy  "
                        + ", primer_id  "
                        + ", remarks  "
                        + ", assay_type "
                        + ", motif  "
                        + ", forward_primer  "
                        + ", reverse_primer  "
                        + ", product_size  "
                        + ", annealing_temp "
                        + ", amplification "
                + "FROM (gdms_marker gdms INNER JOIN gdms_track_markers track ON(track.marker_id = gdms.marker_id)) "
                + "INNER JOIN gdms_track_data tdata ON (tdata.track_id = track.track_id) "
                + "WHERE track_name = (:trackName) and gdms.marker_type = 'SNP'";
    
    public static final String GET_ALL_DB_ACCESSION_IDS = 
            "SELECT DISTINCT (db_accession_id) " +
            "FROM gdms_marker " +
            "WHERE db_accession_id is not null " +
            "OR db_accession_id != ''";
    
    public static final String COUNT_ALL_DB_ACCESSION_IDS = 
            "SELECT COUNT(DISTINCT db_accession_id) " +
            "FROM gdms_marker " +
            "WHERE db_accession_id is not null " +
            "OR db_accession_id != ''";

    public static final String GET_MARKERS_BY_IDS = 
            "SELECT marker_id  "
                    + ", CONCAT(marker_type, '') "
                    + ", CONCAT(marker_name, '') "
                    + ", CONCAT(species, '') "
                    + ", db_accession_id "
                    + ", reference "
                    + ", CONCAT(genotype, '') "
                    + ", ploidy  "
                    + ", primer_id  "
                    + ", remarks  "
                    + ", assay_type " 
                    + ", motif  "
                    + ", forward_primer  "
                    + ", reverse_primer  "
                    + ", product_size  "
                    + ", annealing_temp " 
                    + ", amplification " 
            + "FROM gdms_marker "
            + "WHERE marker_id IN (:markerIdList) ";

    public static final String COUNT_MARKERS_BY_IDS = 
            "SELECT COUNT(marker_id)  "
            + "FROM gdms_marker "
            + "WHERE marker_id IN (:markerIdList) " 
            ;
    
    public static final String GET_ID_BY_NAME =
    		"SELECT marker_id "
    		+ "FROM gdms_marker "
    		+ "WHERE marker_name = :markerName "
    		+ "LIMIT 0,1";
    
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
