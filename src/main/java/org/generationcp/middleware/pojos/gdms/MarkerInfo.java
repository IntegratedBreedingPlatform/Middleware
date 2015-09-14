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
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_marker_retrieval_info table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_retrieval_info")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class MarkerInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "marker_type")
	private String markerType;

	@Column(name = "marker_name")
	private String markerName;

	@Column(name = "species")
	private String species;

	@Column(name = "db_accession_id")
	private String accessionId;

	@Column(name = "reference")
	private String reference;

	@Column(name = "genotype")
	private String genotype;

	@Column(name = "ploidy")
	private String ploidy;

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
			String genotype, String ploidy, String motif, String forwardPrimer, String reversePrimer, String productSize,
			Float annealingTemp, String amplification, String principalInvestigator, String contact, String institute,
			BigInteger genotypesCount) {
		super();
		this.markerId = markerId;
		this.markerType = markerType;
		this.markerName = markerName;
		this.species = species;
		this.accessionId = accessionId;
		this.reference = reference;
		this.genotype = genotype;
		this.ploidy = ploidy;
		this.motif = motif;
		this.forwardPrimer = forwardPrimer;
		this.reversePrimer = reversePrimer;
		this.productSize = productSize;
		this.annealingTemp = annealingTemp;
		this.amplification = amplification;
		this.principalInvestigator = principalInvestigator;
		this.contact = contact;
		this.institute = institute;
		this.genotypesCount = genotypesCount;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getMarkerType() {
		return this.markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public String getMarkerName() {
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getAccessionId() {
		return this.accessionId;
	}

	public void setAccessionId(String accessionId) {
		this.accessionId = accessionId;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getGenotype() {
		return this.genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public String getPloidy() {
		return this.ploidy;
	}

	public void setPloidy(String ploidy) {
		this.ploidy = ploidy;
	}

	public String getMotif() {
		return this.motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getForwardPrimer() {
		return this.forwardPrimer;
	}

	public void setForwardPrimer(String forwardPrimer) {
		this.forwardPrimer = forwardPrimer;
	}

	public String getReversePrimer() {
		return this.reversePrimer;
	}

	public void setReversePrimer(String reversePrimer) {
		this.reversePrimer = reversePrimer;
	}

	public String getProductSize() {
		return this.productSize;
	}

	public void setProductSize(String productSize) {
		this.productSize = productSize;
	}

	public Float getAnnealingTemp() {
		return this.annealingTemp;
	}

	public void setAnnealingTemp(Float annealingTemp) {
		this.annealingTemp = annealingTemp;
	}

	public String getAmplification() {
		return this.amplification;
	}

	public void setAmplification(String amplification) {
		this.amplification = amplification;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getInstitute() {
		return this.institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public BigInteger getGenotypesCount() {
		return this.genotypesCount;
	}

	public void setGenotypesCount(BigInteger genotypesCount) {
		this.genotypesCount = genotypesCount;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 127).append(this.markerId).toHashCode();
	}

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
		return new EqualsBuilder().append(this.markerId, rhs.markerId).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerInfo [markerId=");
		builder.append(this.markerId);
		builder.append(", markerType=");
		builder.append(this.markerType);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append(", species=");
		builder.append(this.species);
		builder.append(", accessionId=");
		builder.append(this.accessionId);
		builder.append(", reference=");
		builder.append(this.reference);
		builder.append(", genotype=");
		builder.append(this.genotype);
		builder.append(", ploidy=");
		builder.append(this.ploidy);
		builder.append(", motif=");
		builder.append(this.motif);
		builder.append(", forwardPrimer=");
		builder.append(this.forwardPrimer);
		builder.append(", reversePrimer=");
		builder.append(this.reversePrimer);
		builder.append(", productSize=");
		builder.append(this.productSize);
		builder.append(", annealing_temp=");
		builder.append(this.annealingTemp);
		builder.append(", amplification=");
		builder.append(this.amplification);
		builder.append(", principalInvestigator=");
		builder.append(this.principalInvestigator);
		builder.append(", contact=");
		builder.append(this.contact);
		builder.append(", institute=");
		builder.append(this.institute);
		builder.append(", genotypesCount=");
		builder.append(this.genotypesCount);
		builder.append("]");
		return builder.toString();
	}

}
