package org.generationcp.middleware.domain.oms;

public class Property {
	Term term;
	Term IsA;
	
	
	public Property() {
	}

	public Term getIsA() {
		return IsA;
	}

	public void setIsA(Term isA) {
		IsA = isA;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Property [term=");
		builder.append(term);
		builder.append(", IsA=");
		builder.append(IsA);
		builder.append("]");
		return builder.toString();
	}
	
}
