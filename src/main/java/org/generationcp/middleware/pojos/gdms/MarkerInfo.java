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
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for marker_retrieval_info table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "marker_retrieval_info")
public class MarkerInfo implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /* Used by MarkerInfoDAO.getByMarkerName() */
    public static final String GET_BY_MARKER_NAME = 
            "SELECT marker_id " +
                    ", CONCAT(marker_type, '') " + 
                    ", CONCAT(marker_name, '')  " +
                    ", CONCAT(species, '')  " +
                    ", accession_id " +
                    ", reference " +
                    ", CONCAT(genotype, '') " + 
                    ", ploidy " +
                    ", CONCAT(principal_investigator, '') " + 
                    ", contact " +
                    ", institute " +
                    ", genotypes_count " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(marker_name) LIKE LOWER(:markerName)";


    /* Used by MarkerInfoDAO.countByMarkerName() */
    public static final String COUNT_BY_MARKER_NAME = 
            "SELECT COUNT(*) " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(marker_name) LIKE LOWER(:markerName)";

    /* Used by MarkerInfoDAO.getByGenotype() */
    public static final String GET_BY_GENOTYPE = 
            "SELECT marker_id " +
                    ", CONCAT(marker_type, '') " + 
                    ", CONCAT(marker_name, '')  " +
                    ", CONCAT(species, '')  " +
                    ", accession_id " +
                    ", reference " +
                    ", CONCAT(genotype, '') " + 
                    ", ploidy " +
                    ", CONCAT(principal_investigator, '') " + 
                    ", contact " +
                    ", institute " +
                    ", genotypes_count " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(genotype) LIKE LOWER(:genotype)";
    
    /* Used by MarkerInfoDAO.countByGenotype() */
    public static final String COUNT_BY_GENOTYPE = 
            "SELECT COUNT(*) " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(genotype) LIKE LOWER(:genotype)";

    /* Used by MarkerInfoDAO.getByDbAccessionId() */
    public static final String GET_BY_DB_ACCESSION_ID = 
            "SELECT marker_id " +
                    ", CONCAT(marker_type, '') " + 
                    ", CONCAT(marker_name, '')  " +
                    ", CONCAT(species, '')  " +
                    ", accession_id " +
                    ", reference " +
                    ", CONCAT(genotype, '') " + 
                    ", ploidy " +
                    ", CONCAT(principal_investigator, '') " + 
                    ", contact " +
                    ", institute " +
                    ", genotypes_count " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(accession_id) LIKE LOWER(:dbAccessionId)";
    
    /* Used by MarkerInfoDAO.countByDbAccessionId() */
    public static final String COUNT_BY_DB_ACCESSION_ID = 
            "SELECT COUNT(*) " +
            "FROM marker_retrieval_info " +
            "WHERE LOWER(accession_id) LIKE LOWER(:dbAccessionId)";

    
    @Id
    @Column(name = "marker_id")
    private Integer markerId;

    @Column(name = "marker_type")
    private String markerType;
    
    @Column(name = "marker_name")
    private String markerName; 
    
    @Column(name = "species")
    private String species;

    @Column(name = "accession_id")
    private String accessionId;
    
    @Column(name = "reference")
    private String reference;

    @Column(name = "genotype")
    private String genotype;

    @Column(name = "ploidy")
    private String ploidy;

    @Column(name = "principal_investigator")
    private String principalInvestigator;

    @Column(name = "contact")
    private String contact;

    @Column(name = "institute")
    private String institute;

    @Column(name = "genotypes_count")
    private BigInteger genotypesCount;


    
    public MarkerInfo() {
        super();
    }

    public MarkerInfo(Integer markerId, String markerType, String markerName, String species, String accessionId, String reference,
            String genotype, String ploidy, String principalInvestigator, String contact, String institute, BigInteger genotypesCount) {
        super();
        this.markerId = markerId;
        this.markerType = markerType;
        this.markerName = markerName;
        this.species = species;
        this.accessionId = accessionId;
        this.reference = reference;
        this.genotype = genotype;
        this.ploidy = ploidy;
        this.principalInvestigator = principalInvestigator;
        this.contact = contact;
        this.institute = institute;
        this.genotypesCount = genotypesCount;
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

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
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
    
    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }
    
    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getInstitute() {
        return institute;
    }
    
    public void setInstitute(String institute) {
        this.institute = institute;
    }
    
    public BigInteger getGenotypesCount() {
        return genotypesCount;
    }
    
    public void setGenotypesCount(BigInteger genotypesCount) {
        this.genotypesCount = genotypesCount;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 127).append(markerId).toHashCode();
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
        if (!(obj instanceof MarkerInfo)) {
            return false;
        }

        MarkerInfo rhs = (MarkerInfo) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(markerId, rhs.markerId).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MarkerInfo " +
                    "[markerId=" + markerId + 
                    ", markerType=" + markerType + 
                    ", markerName=" + markerName + 
                    ", species=" + species + 
                    ", accessionId=" + accessionId + 
                    ", reference=" + reference + 
                    ", genotype=" + genotype + 
                    ", ploidy=" + ploidy + 
                    ", principalInvestigator=" + principalInvestigator + 
                    ", contact=" + contact + 
                    ", institute=" + institute + 
                    ", genotypesCount=" + genotypesCount + "]";
    }
    
}
