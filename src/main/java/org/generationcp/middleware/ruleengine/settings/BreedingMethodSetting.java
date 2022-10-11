
package org.generationcp.middleware.ruleengine.settings;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class BreedingMethodSetting implements Serializable {

	private static final long serialVersionUID = -7580936794539379309L;

	private Integer methodId;
	private boolean isBasedOnStatusOfParentalLines;
	private boolean basedOnImportFile;

	public BreedingMethodSetting() {

	}

	public BreedingMethodSetting(Integer methodId, boolean isBasedOnStatusOfParentalLines, boolean basedOnImportFile) {
		super();
		this.methodId = methodId;
		this.isBasedOnStatusOfParentalLines = isBasedOnStatusOfParentalLines;
		this.basedOnImportFile = basedOnImportFile;
	}

	@XmlAttribute
	public Integer getMethodId() {
		return this.methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	@XmlAttribute
	public boolean isBasedOnStatusOfParentalLines() {
		return this.isBasedOnStatusOfParentalLines;
	}

	public void setBasedOnStatusOfParentalLines(boolean isBasedOnStatusOfParentalLines) {
		this.isBasedOnStatusOfParentalLines = isBasedOnStatusOfParentalLines;
	}

	@XmlAttribute
	public boolean isBasedOnImportFile() {
	  return basedOnImportFile;
	}

	public void setBasedOnImportFile(final boolean basedOnImportFile) {
	  this.basedOnImportFile = basedOnImportFile;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BreedingMethodSetting)) {
			return false;
		}

		BreedingMethodSetting rhs = (BreedingMethodSetting) obj;
		return new EqualsBuilder().append(this.methodId, rhs.methodId)
				.append(this.isBasedOnStatusOfParentalLines, rhs.isBasedOnStatusOfParentalLines)
				.append(this.basedOnImportFile, rhs.isBasedOnImportFile()).isEquals();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.isBasedOnStatusOfParentalLines ? 1231 : 1237);
		result = prime * result + (this.basedOnImportFile ? 1231 : 1237);
		result = prime * result + (this.methodId == null ? 0 : this.methodId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "BreedingMethodSetting [methodId=" + this.methodId + ", isBasedOnStatusOfParentalLines="
						+ this.isBasedOnStatusOfParentalLines + ", basedOnImportFile=" + this.basedOnImportFile + "]";
	}

}
