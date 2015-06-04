
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermSummary;

public class StandardVariableSummary {

	private final TermSummary term;

	private TermSummary property;

	private TermSummary method;

	private TermSummary scale;

	private TermSummary isA;

	private TermSummary dataType;

	private TermSummary storedIn;

	private PhenotypicType phenotypicType;

	private boolean hasPair;

	public StandardVariableSummary(Integer id, String name, String description) {
		this.term = new TermSummary(id, name, description);
	}

	public Integer getId() {
		return this.term.getId();
	}

	public String getName() {
		return this.term.getName();
	}

	public String getDescription() {
		return this.term.getDefinition();
	}

	public TermSummary getTerm() {
		return this.term;
	}

	public TermSummary getProperty() {
		return this.property;
	}

	public void setProperty(TermSummary property) {
		this.property = property;
	}

	public TermSummary getMethod() {
		return this.method;
	}

	public void setMethod(TermSummary method) {
		this.method = method;
	}

	public TermSummary getScale() {
		return this.scale;
	}

	public void setScale(TermSummary scale) {
		this.scale = scale;
	}

	public TermSummary getIsA() {
		return this.isA;
	}

	public void setIsA(TermSummary isA) {
		this.isA = isA;
	}

	public TermSummary getDataType() {
		return this.dataType;
	}

	public void setDataType(TermSummary dataType) {
		this.dataType = dataType;
	}

	public TermSummary getStoredIn() {
		return this.storedIn;
	}

	public void setStoredIn(TermSummary storedIn) {
		this.storedIn = storedIn;
	}

	public PhenotypicType getPhenotypicType() {
		return this.phenotypicType;
	}

	public void setPhenotypicType(PhenotypicType phenotypicType) {
		this.phenotypicType = phenotypicType;
	}

	/**
	 * @return the hasPair
	 */
	public boolean isHasPair() {
		return this.hasPair;
	}

	/**
	 * @param hasPair the hasPair to set
	 */
	public void setHasPair(boolean hasPair) {
		this.hasPair = hasPair;
	}

	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StandardVariableSummary)) {
			return false;
		}
		StandardVariableSummary other = (StandardVariableSummary) obj;
		return this.getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "StandardVariableSummary [term=" + this.term + ", property=" + this.property + ", method=" + this.method + ", scale="
				+ this.scale + ", isA=" + this.isA + ", dataType=" + this.dataType + ", storedIn=" + this.storedIn + ", phenotypicType="
				+ this.phenotypicType + "]";
	}
}
