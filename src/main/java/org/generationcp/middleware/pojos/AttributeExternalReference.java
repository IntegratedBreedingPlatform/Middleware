package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

@Entity
@Table(name = "external_reference_atributs")
public class AttributeExternalReference extends AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aid")
	private Attribute attribute;

	@Basic(optional = false)
	@Column(name = "reference_id")
	private String referenceId;

	@Basic(optional = false)
	@Column(name = "reference_source")
	private String source;

	public AttributeExternalReference() {
	}

	public AttributeExternalReference(final Attribute attribute, final String referenceId, final String source) {
		this.attribute = attribute;
		this.referenceId = referenceId;
		this.source = source;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudyExternalReference)) {
			return false;
		}
		final AttributeExternalReference castOther = (AttributeExternalReference) other;
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
