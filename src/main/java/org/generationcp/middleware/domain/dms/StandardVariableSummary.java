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

	public StandardVariableSummary(Integer id, String name, String description) {
		this.term = new TermSummary(id, name, description);
	}
	
	public Integer getId() {
    	return term.getId();
    }

	public String getName() {
		return term.getName();
	}
	
	public String getDescription() {
		return term.getDefinition();
	}
	
	public TermSummary getTerm() {
		return term;
	}

	public TermSummary getProperty() {
		return property;
	}

	public void setProperty(TermSummary property) {
		this.property = property;
	}

	public TermSummary getMethod() {
		return method;
	}

	public void setMethod(TermSummary method) {
		this.method = method;
	}

	public TermSummary getScale() {
		return scale;
	}

	public void setScale(TermSummary scale) {
		this.scale = scale;
	}

	public TermSummary getIsA() {
		return isA;
	}

	public void setIsA(TermSummary isA) {
		this.isA = isA;
	}

	public TermSummary getDataType() {
		return dataType;
	}

	public void setDataType(TermSummary dataType) {
		this.dataType = dataType;
	}

	public TermSummary getStoredIn() {
		return storedIn;
	}

	public void setStoredIn(TermSummary storedIn) {
		this.storedIn = storedIn;
	}

	public PhenotypicType getPhenotypicType() {
		return phenotypicType;
	}

	public void setPhenotypicType(PhenotypicType phenotypicType) {
		this.phenotypicType = phenotypicType;
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof StandardVariableSummary)) return false;
		StandardVariableSummary other = (StandardVariableSummary) obj;
		return getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "StandardVariableSummary [term=" + term + ", property="
				+ property + ", method=" + method + ", scale=" + scale
				+ ", isA=" + isA + ", dataType=" + dataType + ", storedIn="
				+ storedIn + ", phenotypicType=" + phenotypicType + "]";
	}
}
