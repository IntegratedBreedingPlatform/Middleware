
package org.generationcp.middleware.ruleengine.settings;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CrossNameSetting implements Serializable {

	public static final String DEFAULT_SEPARATOR = "/";

	private static final long serialVersionUID = 2944997653987903733L;

	private String prefix;
	private String suffix;
	private boolean addSpaceBetweenPrefixAndCode;
	private boolean addSpaceBetweenSuffixAndCode;
	private Integer numOfDigits;
	private String separator;
	private boolean saveParentageDesignationAsAString;

	private Integer startNumber; // "transient" attribute, not saved in DB

	public CrossNameSetting() {

	}

	public CrossNameSetting(String prefix, String suffix, boolean addSpaceBetweenPrefixAndCode, boolean addSpaceBetweenSuffixAndCode,
			Integer numOfDigits, String separator) {
		super();
		this.prefix = prefix;
		this.suffix = suffix;
		this.addSpaceBetweenPrefixAndCode = addSpaceBetweenPrefixAndCode;
		this.addSpaceBetweenSuffixAndCode = addSpaceBetweenSuffixAndCode;
		this.numOfDigits = numOfDigits;
		this.separator = separator;
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

	@XmlAttribute
	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@XmlAttribute
	public boolean isSaveParentageDesignationAsAString() {
		return this.saveParentageDesignationAsAString;
	}

	public void setSaveParentageDesignationAsAString(boolean saveParentageDesignationAsAString) {
		this.saveParentageDesignationAsAString = saveParentageDesignationAsAString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.addSpaceBetweenPrefixAndCode ? 1231 : 1237);
		result = prime * result + (this.addSpaceBetweenSuffixAndCode ? 1231 : 1237);
		result = prime * result + (this.numOfDigits == null ? 0 : this.numOfDigits.hashCode());
		result = prime * result + (this.prefix == null ? 0 : this.prefix.hashCode());
		result = prime * result + (this.separator == null ? 0 : this.separator.hashCode());
		result = prime * result + (this.startNumber == null ? 0 : this.startNumber.hashCode());
		result = prime * result + (this.suffix == null ? 0 : this.suffix.hashCode());
		result = prime * result + (this.saveParentageDesignationAsAString ? 1231 : 1237);
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
		if (!(obj instanceof CrossNameSetting)) {
			return false;
		}

		CrossNameSetting rhs = (CrossNameSetting) obj;
		return new EqualsBuilder().append(this.prefix, rhs.prefix).append(this.suffix, rhs.suffix)
				.append(this.addSpaceBetweenPrefixAndCode, rhs.addSpaceBetweenPrefixAndCode)
				.append(this.addSpaceBetweenSuffixAndCode, rhs.addSpaceBetweenSuffixAndCode).append(this.numOfDigits, rhs.numOfDigits)
				.append(this.saveParentageDesignationAsAString, rhs.saveParentageDesignationAsAString)
				.append(this.separator, rhs.separator).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CrossNameSetting [prefix=");
		builder.append(this.prefix);
		builder.append(", suffix=");
		builder.append(this.suffix);
		builder.append(", addSpaceBetweenPrefixAndCode=");
		builder.append(this.addSpaceBetweenPrefixAndCode);
		builder.append(", addSpaceBetweenSuffixAndCode=");
		builder.append(this.addSpaceBetweenSuffixAndCode);
		builder.append(", saveParentageDesignationAsAString=");
		builder.append(this.saveParentageDesignationAsAString);
		builder.append(", numOfDigits=");
		builder.append(this.numOfDigits);
		builder.append(", separator=");
		builder.append(this.separator);
		builder.append("]");
		return builder.toString();
	}

}
