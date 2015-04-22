package org.generationcp.middleware.service.impl.study;

import java.util.Collections;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TrialTraits {
	
	private Session session;

	public TrialTraits(final Session session) {
		this.session = session;
		
	}
	
	List<String> getTraits(final int projectBusinessIdentifier) {
		final List<String> list = getTraitListForTrail(projectBusinessIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return  Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> getTraitListForTrail(final int projectBusinessIdentifier) {
		final TraitNamesQuery traitQuery = new TraitNamesQuery();
		final String traitsInProjectQuery = traitQuery.generateQuery();
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(traitsInProjectQuery);
		traitSqlQuery.addScalar("value");
		traitSqlQuery.setParameter(0, projectBusinessIdentifier);
		return traitSqlQuery.list();
	}
}
