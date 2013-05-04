package org.generationcp.middleware.v2.factory;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class TermFactory {

	public Term create(CVTerm cvTerm) {
		Term term = new Term();
		term.setId(cvTerm.getCvTermId());
		term.setName(cvTerm.getName());
		term.setDefinition(cvTerm.getDefinition());
		return term;
	}
	
	public List<Term> create(List<CVTerm> cvTerms) {
		List<Term> terms = new ArrayList<Term>();
		if (terms != null) {
			for (CVTerm cvTerm : cvTerms) {
				terms.add(create(cvTerm));
			}
		}
		return terms;
	}
}
