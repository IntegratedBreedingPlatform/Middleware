package org.generationcp.middleware.service.impl.study;

import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TraitServiceImpl implements TraitService {
	
	private Session session;

	final TraitNamesQuery traitQuery = new TraitNamesQuery();

	public TraitServiceImpl(final Session session) {
		this.session = session;
	}
	
	@Override
	public List<String> getTraits(final int studyBusinessIdentifier) {
		final List<String> list = getTraitListForTrail(studyBusinessIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return  Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> getTraitListForTrail(final int projectBusinessIdentifier) {
		final String traitsInProjectQuery = traitQuery.getTraitQuery();
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(traitsInProjectQuery);
		traitSqlQuery.addScalar("value");
		traitSqlQuery.setParameter(0, projectBusinessIdentifier);
		return traitSqlQuery.list();
	}
}
