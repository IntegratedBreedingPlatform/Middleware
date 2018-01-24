package org.generationcp.middleware.pojos.germplasm;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class GermplasmNameSetting implements Serializable {
	
	private static final long serialVersionUID = 3322758636517697964L;
	private String prefix;
	private String suffix;
	private boolean addSpaceBetweenPrefixAndCode;
	private boolean addSpaceBetweenSuffixAndCode;
	private Integer numOfDigits;
	private Integer startNumber;

	public GermplasmNameSetting() {

	}

	public GermplasmNameSetting(String prefix, String suffix, boolean addSpaceBetweenPrefixAndCode, boolean addSpaceBetweenSuffixAndCode,
			Integer numOfDigits) {
		super();
		this.prefix = prefix;
		this.suffix = suffix;
		this.addSpaceBetweenPrefixAndCode = addSpaceBetweenPrefixAndCode;
		this.addSpaceBetweenSuffixAndCode = addSpaceBetweenSuffixAndCode;
		this.numOfDigits = numOfDigits;
	}

	@XmlAttribute
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@XmlAttribute
	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@XmlAttribute
	public boolean isAddSpaceBetweenPrefixAndCode() {
		return this.addSpaceBetweenPrefixAndCode;
	}

	public void setAddSpaceBetweenPrefixAndCode(boolean addSpaceBetweenPrefixAndCode) {
		this.addSpaceBetweenPrefixAndCode = addSpaceBetweenPrefixAndCode;
	}

	@XmlAttribute
	public boolean isAddSpaceBetweenSuffixAndCode() {
		return this.addSpaceBetweenSuffixAndCode;
	}

	public void setAddSpaceBetweenSuffixAndCode(boolean addSpaceBetweenSuffixAndCode) {
		this.addSpaceBetweenSuffixAndCode = addSpaceBetweenSuffixAndCode;
	}

	@XmlAttribute
	public Integer getNumOfDigits() {
		return this.numOfDigits;
	}

	public void setNumOfDigits(Integer numOfDigits) {
		this.numOfDigits = numOfDigits;
	}

	public Integer getStartNumber() {
		return this.startNumber;
	}

	public void setStartNumber(Integer startNumber) {
		this.startNumber = startNumber;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.addSpaceBetweenPrefixAndCode ? 1231 : 1237);
		result = prime * result + (this.addSpaceBetweenSuffixAndCode ? 1231 : 1237);
		result = prime * result + (this.numOfDigits == null ? 0 : this.numOfDigits.hashCode());
		result = prime * result + (this.prefix == null ? 0 : this.prefix.hashCode());
		result = prime * result + (this.startNumber == null ? 0 : this.startNumber.hashCode());
		result = prime * result + (this.suffix == null ? 0 : this.suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GermplasmNameSetting)) {
			return false;
		}

		GermplasmNameSetting rhs = (GermplasmNameSetting) obj;
		return new EqualsBuilder().append(this.prefix, rhs.prefix).append(this.suffix, rhs.suffix)
				.append(this.addSpaceBetweenPrefixAndCode, rhs.addSpaceBetweenPrefixAndCode)
				.append(this.addSpaceBetweenSuffixAndCode, rhs.addSpaceBetweenSuffixAndCode).append(this.numOfDigits, rhs.numOfDigits)
				.isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmNameSetting [prefix=");
		builder.append(this.prefix);
		builder.append(", suffix=");
		builder.append(this.suffix);
		builder.append(", addSpaceBetweenPrefixAndCode=");
		builder.append(this.addSpaceBetweenPrefixAndCode);
		builder.append(", addSpaceBetweenSuffixAndCode=");
		builder.append(this.addSpaceBetweenSuffixAndCode);
		builder.append(", numOfDigits=");
		builder.append(this.numOfDigits);
		builder.append("]");
		return builder.toString();
	}

}
