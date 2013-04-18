package org.generationcp.middleware.v2.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.v2.pojos.CVTerm;

public class CVTermHelper {

	private Map<Integer, String> termsMap = new HashMap<Integer, String>();
	
	
	public CVTermHelper(Collection<CVTerm> terms) {
		if (terms != null && terms.size() > 0) {
			for (CVTerm term : terms) {
				termsMap.put(term.getCvTermId(), term.getName());
			}
		}
	}
	
	public String getName(Integer cvtermId) {
		return termsMap.get(cvtermId);
	}
}
