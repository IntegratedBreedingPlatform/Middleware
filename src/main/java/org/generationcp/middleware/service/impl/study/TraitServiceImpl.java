
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TraitServiceImpl implements TraitService {

	private final Session session;

	final TraitNamesQuery traitQuery = new TraitNamesQuery();

	public TraitServiceImpl(final Session session) {
		this.session = session;
	}

	@Override
	public List<TraitDto> getTraits(final int studyBusinessIdentifier) {
		final List<TraitDto> list = this.getTraitListForTrail(studyBusinessIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<TraitDto>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<TraitDto> getTraitListForTrail(final int projectBusinessIdentifier) {
		final String traitsInProjectQuery = this.traitQuery.getTraitQuery();
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(traitsInProjectQuery);
		traitSqlQuery.addScalar("cvterm_id");
		traitSqlQuery.addScalar("name");
		traitSqlQuery.setParameter(0, projectBusinessIdentifier);
		List<Object[]> list = traitSqlQuery.list();
		final List<TraitDto> traitList = new ArrayList<TraitDto>();
		for (Object[] rows : list) {
			traitList.add(new TraitDto((Integer) rows[0], (String) rows[1]));
		}
		return traitList;
	}
}
