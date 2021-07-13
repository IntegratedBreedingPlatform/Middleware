package org.generationcp.middleware.pojos.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

@AutoProperty
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

	public GermplasmNameSetting(final String prefix, final String suffix, final boolean addSpaceBetweenPrefixAndCode,
		final boolean addSpaceBetweenSuffixAndCode,
		final Integer numOfDigits) {
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

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	@XmlAttribute
	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	@XmlAttribute
	public boolean isAddSpaceBetweenPrefixAndCode() {
		return this.addSpaceBetweenPrefixAndCode;
	}

	public void setAddSpaceBetweenPrefixAndCode(final boolean addSpaceBetweenPrefixAndCode) {
		this.addSpaceBetweenPrefixAndCode = addSpaceBetweenPrefixAndCode;
	}

	@XmlAttribute
	public boolean isAddSpaceBetweenSuffixAndCode() {
		return this.addSpaceBetweenSuffixAndCode;
	}

	public void setAddSpaceBetweenSuffixAndCode(final boolean addSpaceBetweenSuffixAndCode) {
		this.addSpaceBetweenSuffixAndCode = addSpaceBetweenSuffixAndCode;
	}

	@XmlAttribute
	public Integer getNumOfDigits() {
		return this.numOfDigits;
	}

	public void setNumOfDigits(final Integer numOfDigits) {
		this.numOfDigits = numOfDigits;
	}

	public Integer getStartNumber() {
		return this.startNumber;
	}

	public void setStartNumber(final Integer startNumber) {
		this.startNumber = startNumber;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
