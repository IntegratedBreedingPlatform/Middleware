
package org.generationcp.middleware.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "key_sequence_register")
public class KeySequenceRegister {

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "key_prefix", nullable = false)
	private String keyPrefix;
	
	@Column(name = "suffix", nullable = true)
	private String suffix;

	@Column(name = "last_used_sequence", nullable = false)
	private int lastUsedSequence;

	@Version
	@Column(name = "optimistic_lock_number", nullable = false)
	private int optimisticLockNumber;

	public KeySequenceRegister() {
	}

	public KeySequenceRegister(final Integer id) {
		this.id = id;
	}
	
	public KeySequenceRegister(final String keyPrefix, final String suffix, final int lastUsedSequence) {
		this.keyPrefix = keyPrefix;
		this.suffix = suffix;
		this.lastUsedSequence = lastUsedSequence;
	}
	
	public KeySequenceRegister(final Integer id, final String keyPrefix, final String suffix, final int lastUsedSequence, final int optimisticLockNumber) {
		this.id = id;
		this.keyPrefix = keyPrefix;
		this.suffix = suffix;
		this.lastUsedSequence = lastUsedSequence;
		this.optimisticLockNumber = optimisticLockNumber;
	}

	public String getKeyPrefix() {
		return this.keyPrefix;
	}

	public void setKeyPrefix(final String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public int getLastUsedSequence() {
		return this.lastUsedSequence;
	}

	public void setLastUsedSequence(final int lastUsedSequence) {
		this.lastUsedSequence = lastUsedSequence;
	}

	public int getOptimisticLockNumber() {
		return this.optimisticLockNumber;
	}

	public void setOptimisticLockNumber(final int optimisticLockNumber) {
		this.optimisticLockNumber = optimisticLockNumber;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.id).append(this.keyPrefix).append(this.suffix).append(this.lastUsedSequence)
				.toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof KeySequenceRegister)) {
			return false;
		}
		final KeySequenceRegister castOther = (KeySequenceRegister) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	
	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getSuffix() {
		return suffix;
	}

	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
