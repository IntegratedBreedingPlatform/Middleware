
package org.generationcp.middleware.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "configuration")
public class Configuration {

	@Id
	@Column(name = "key", nullable = false)
	private String key;

	@Column(name = "value")
	private String value;

	public Configuration() {

	}

	public Configuration(String key, String value) {
		this.key = key;
		this.value = value;
	}

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

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.key).append(this.value).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Configuration)) {
			return false;
		}
		Configuration castOther = (Configuration) other;
		return new EqualsBuilder().append(this.key, castOther.key).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.key).toHashCode();
	}
}
