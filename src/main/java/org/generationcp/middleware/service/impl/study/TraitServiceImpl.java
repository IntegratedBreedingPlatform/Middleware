
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TraitServiceImpl implements TraitService {

	private final Session session;

	final static String STUDY_TRAITS_QUERY = "SELECT \n" + "    cvterm_id, name\n" + "FROM\n" + "    projectprop pp\n"
			+ "        INNER JOIN\n" + "    cvterm cvt ON cvt.name = pp.value " + "WHERE\n" + "    type_id = " + VariableType.TRAIT.getId()
			+ "\n" + "        AND project_id = (SELECT \n" + "            p.project_id\n" + "        FROM\n"
			+ "            project_relationship pr\n" + "                INNER JOIN\n"
			+ "            project p ON p.project_id = pr.subject_project_id \n" + "        WHERE \n"
			+ "            pr.object_project_id = ?\n" + "                AND name LIKE '%PLOTDATA')";

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
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(TraitServiceImpl.STUDY_TRAITS_QUERY);
		traitSqlQuery.addScalar("cvterm_id");
		traitSqlQuery.addScalar("name");
		traitSqlQuery.setParameter(0, projectBusinessIdentifier);
		final List<Object[]> list = traitSqlQuery.list();
		final List<TraitDto> traitList = new ArrayList<TraitDto>();
		for (final Object[] rows : list) {
			traitList.add(new TraitDto((Integer) rows[0], (String) rows[1]));
		}
		return traitList;
	}
}
