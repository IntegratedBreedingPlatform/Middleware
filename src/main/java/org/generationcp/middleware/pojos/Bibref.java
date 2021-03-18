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

package org.generationcp.middleware.pojos;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * POJO for bibrefs table.
 *
 * @author Kevin Manansala, Mark Agarrado
 */
@AuditOverrides({
	@AuditOverride(forClass = AbstractEntity.class)
})
@Audited
@Entity
@Table(name = "bibrefs")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "bibref")
@XmlType(propOrder = {"refid", "typeFname", "pubdate", "authors", "editors", "analyt", "series", "volume", "issue", "publish", "pucity",
		"pucntry"})
@XmlAccessorType(XmlAccessType.NONE)
public class Bibref extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "refid")
	@XmlElement(name = "bibrefId")
	private Integer refid;

	@NotAudited
	@ManyToOne(targetEntity = UserDefinedField.class, optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "pubtype", nullable = true)
	@NotFound(action = NotFoundAction.IGNORE)
	private UserDefinedField type;

	@Column(name = "pubdate")
	@XmlElement(name = "publicationDate")
	private Integer pubdate;

	@Basic(optional = false)
	@Column(name = "authors")
	@XmlElement(name = "authors")
	private String authors;

	@Basic(optional = false)
	@Column(name = "editors")
	@XmlElement(name = "editors")
	private String editors;

	@Basic(optional = false)
	@Column(name = "analyt")
	@XmlElement(name = "title")
	private String analyt;

	@Basic(optional = false)
	@Column(name = "monogr")
	private String monogr;

	@Basic(optional = false)
	@Column(name = "series")
	@XmlElement(name = "series")
	private String series;

	@Basic(optional = false)
	@Column(name = "volume")
	@XmlElement(name = "volume")
	private String volume;

	@Basic(optional = false)
	@Column(name = "issue")
	@XmlElement(name = "issue")
	private String issue;

	@Basic(optional = false)
	@Column(name = "pagecol")
	private String pagecol;

	@Basic(optional = false)
	@Column(name = "publish")
	@XmlElement(name = "publisher")
	private String publish;

	@Basic(optional = false)
	@Column(name = "pucity")
	@XmlElement(name = "publishingCity")
	private String pucity;

	@Basic(optional = false)
	@Column(name = "pucntry")
	@XmlElement(name = "publishingCountry")
	private String pucntry;

	/**
	 * Don't use it. This constructor is required by hibernate.
	 */
	public Bibref() {
	}

	@Deprecated
	public Bibref(final Integer refid) {
		this.refid = refid;
	}

	public Bibref(final Integer refid, final String authors, final String editors, final String analyt,
		final String monogr, final String series,
		final String volume, final String issue, final String pagecol, final String publish, final String pucity, final String pucntry) {
		this.refid = refid;
		this.authors = authors;
		this.editors = editors;
		this.analyt = analyt;
		this.monogr = monogr;
		this.series = series;
		this.volume = volume;
		this.issue = issue;
		this.pagecol = pagecol;
		this.publish = publish;
		this.pucity = pucity;
		this.pucntry = pucntry;
	}

	public Integer getRefid() {
		return this.refid;
	}

	public void setRefid(Integer refid) {
		this.refid = refid;
	}

	public UserDefinedField getType() {
		return this.type;
	}

	@XmlElement(name = "type")
	public String getTypeFname() {
		return this.type.getFname();
	}

	public void setType(UserDefinedField type) {
		this.type = type;
	}

	public Integer getPubdate() {
		return this.pubdate;
	}

	public void setPubdate(Integer pubdate) {
		this.pubdate = pubdate;
	}

	public String getAuthors() {
		return this.authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getEditors() {
		return this.editors;
	}

	public void setEditors(String editors) {
		this.editors = editors;
	}

	public String getAnalyt() {
		return this.analyt;
	}

	public void setAnalyt(String analyt) {
		this.analyt = analyt;
	}

	public String getMonogr() {
		return this.monogr;
	}

	public void setMonogr(String monogr) {
		this.monogr = monogr;
	}

	public String getSeries() {
		return this.series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getVolume() {
		return this.volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getIssue() {
		return this.issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getPagecol() {
		return this.pagecol;
	}

	public void setPagecol(String pagecol) {
		this.pagecol = pagecol;
	}

	public String getPublish() {
		return this.publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getPucity() {
		return this.pucity;
	}

	public void setPucity(String pucity) {
		this.pucity = pucity;
	}

	public String getPucntry() {
		return this.pucntry;
	}

	public void setPucntry(String pucntry) {
		this.pucntry = pucntry;
	}

	@Override
	public int hashCode() {
		return this.getRefid();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Bibref) {
			Bibref param = (Bibref) obj;
			if (this.getRefid().equals(param.getRefid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bibref [refid=");
		builder.append(this.refid);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", pubdate=");
		builder.append(this.pubdate);
		builder.append(", authors=");
		builder.append(this.authors);
		builder.append(", editors=");
		builder.append(this.editors);
		builder.append(", analyt=");
		builder.append(this.analyt);
		builder.append(", monogr=");
		builder.append(this.monogr);
		builder.append(", series=");
		builder.append(this.series);
		builder.append(", volume=");
		builder.append(this.volume);
		builder.append(", issue=");
		builder.append(this.issue);
		builder.append(", pagecol=");
		builder.append(this.pagecol);
		builder.append(", publish=");
		builder.append(this.publish);
		builder.append(", pucity=");
		builder.append(this.pucity);
		builder.append(", pucntry=");
		builder.append(this.pucntry);
		builder.append("]");
		return builder.toString();
	}
}
