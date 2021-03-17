package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

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
import java.io.Serializable;

@AuditOverrides({
	@AuditOverride(forClass = AbstractEntity.class)
})
@Audited
@Entity
@Table(name = "external_reference")
public class ExternalReference extends AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gid")
	private Germplasm germplasm;

	@Basic(optional = false)
	@Column(name = "reference_id")
	private String referenceId;

	@Basic(optional = false)
	@Column(name = "reference_source")
	private String source;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Germplasm getGermplasm() {
		return germplasm;
	}

	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public ExternalReference() {
	}

	public ExternalReference(final Germplasm germplasm, final String referenceId, final String source) {
		this.germplasm = germplasm;
		this.referenceId = referenceId;
		this.source = source;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ExternalReference)) {
			return false;
		}
		final ExternalReference castOther = (ExternalReference) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
