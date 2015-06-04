
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

public class NameSynonymBuilder extends Builder {

	public NameSynonymBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<NameSynonym> create(List<CVTermSynonym> cvTermSynonyms) {
		List<NameSynonym> synonyms = new ArrayList<NameSynonym>();

		if (cvTermSynonyms != null && !cvTermSynonyms.isEmpty()) {
			for (CVTermSynonym cvTermSynonym : cvTermSynonyms) {
				synonyms.add(new NameSynonym(cvTermSynonym.getSynonym(), NameType.find(cvTermSynonym.getTypeId())));
			}
		}

		return synonyms;
	}

	public List<CVTermSynonym> findSynonyms(int cvTermId) throws MiddlewareQueryException {
		List<CVTermSynonym> synonyms = new ArrayList<CVTermSynonym>();
		synonyms.addAll(this.getCvTermSynonymDao().getByCvTermId(cvTermId));
		return synonyms;
	}

}
