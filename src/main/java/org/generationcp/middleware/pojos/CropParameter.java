package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "crop_parameter")
public class CropParameter {

	@Id
	@Basic(optional = false)
	@Column(name = "`key`")
	private String key;

	@Basic(optional = false)
	@Column(name = "value")
	private String value;

	@Basic(optional = false)
	@Column(name = "description")
	private String description;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "is_encrypted", columnDefinition = "TINYINT")
	private Boolean isEncrypted;

	@Transient
	private String encryptedValue;

	public String getKey() {
		return this.key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getEncryptedValue() {
		return this.encryptedValue;
	}

	public void setEncryptedValue(final String encryptedValue) {
		this.encryptedValue = encryptedValue;
	}

	public Boolean isEncrypted() {
		return this.isEncrypted;
	}

	public void setIsEncrypted(final Boolean encrypted) {
		this.isEncrypted = encrypted;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
