package org.generationcp.middleware.v2.factory;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class CvTermFactory {

	public CvTerm create(CVTerm term) {
		CvTerm cvTerm = new CvTerm();
		cvTerm.setId(term.getCvTermId());
		cvTerm.setName(term.getName());
		cvTerm.setDescription(term.getDefinition());
		return cvTerm;
	}
	
	public List<CvTerm> create(List<CVTerm> terms) {
		List<CvTerm> cvTerms = new ArrayList<CvTerm>();
		if (terms != null) {
			for (CVTerm term : terms) {
				cvTerms.add(create(term));
			}
		}
		return cvTerms;
	}
}
