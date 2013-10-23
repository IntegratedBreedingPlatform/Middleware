package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.util.Debug;

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

    public void print(int indent) {
        Debug.println(indent, "Property: ");
        Debug.println(indent + 3, "term: ");
        term.print(indent+6);
        Debug.println(indent + 3, "IsA: ");
        IsA.print(indent+6);
    }
	
}
