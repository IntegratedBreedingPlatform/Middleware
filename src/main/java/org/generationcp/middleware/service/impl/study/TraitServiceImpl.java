
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TraitServiceImpl implements TraitService {

	private final Session session;

	final static String STUDY_TRAITS_QUERY = "SELECT cvterm_id, name \n" +
			"FROM cvterm cvt \n " + 
			"INNER JOIN projectprop ppTermId on (ppTermId.value = cvt.cvterm_id and ppTermId.type_id = " + TermId.STANDARD_VARIABLE.getId() + ") \n" +
			"INNER JOIN projectprop ppVarType on (ppVarType.project_id = ppTermId.project_id and ppVarType.rank = ppTermId.rank) \n" +
			"WHERE ppVarType.type_id = " + VariableType.TRAIT.getId() +  
			" AND ppTermId.project_id = (SELECT \n" + "            p.project_id\n" + "        FROM\n"
			+ "            project_relationship pr\n" + "                INNER JOIN\n"
			+ "            project p ON p.project_id = pr.subject_project_id \n" + "        WHERE \n"
			+ "            pr.object_project_id = ?\n" + "                AND name LIKE '%PLOTDATA')";
	

	final static String STUDY_SELECTION_METHODS_QUERY = "SELECT cvterm_id, name \n" +
			"FROM cvterm cvt \n " + 
			"INNER JOIN projectprop ppTermId on (ppTermId.value = cvt.cvterm_id and ppTermId.type_id = " + TermId.STANDARD_VARIABLE.getId() + ") \n" +
			"INNER JOIN projectprop ppVarType on (ppVarType.project_id = ppTermId.project_id and ppVarType.rank = ppTermId.rank) \n" +
			"WHERE ppVarType.type_id = " + VariableType.SELECTION_METHOD.getId() +  
			" AND ppTermId.project_id = (SELECT \n" + "            p.project_id\n" + "        FROM\n"
			+ "            project_relationship pr\n" + "                INNER JOIN\n"
			+ "            project p ON p.project_id = pr.subject_project_id \n" + "        WHERE \n"
			+ "            pr.object_project_id = ?\n" + "                AND name LIKE '%PLOTDATA')";
	

	public TraitServiceImpl(final Session session) {
		this.session = session;
	}

	@Override
	public List<TraitDto> getTraits(final int studyIdentifier) {
		final List<TraitDto> list = this.getTraitListForStudy(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<TraitDto>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<TraitDto> getTraitListForStudy(final int studyIdentifier) {
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(TraitServiceImpl.STUDY_TRAITS_QUERY);
		traitSqlQuery.addScalar("cvterm_id");
		traitSqlQuery.addScalar("name");
		traitSqlQuery.setParameter(0, studyIdentifier);
		final List<Object[]> list = traitSqlQuery.list();
		final List<TraitDto> traitList = new ArrayList<TraitDto>();
		for (final Object[] rows : list) {
			traitList.add(new TraitDto((Integer) rows[0], (String) rows[1]));
		}
		return traitList;
	}



	@Override
	public List<TraitDto> getSelectionMethods(final int studyIdentifier) {
		final List<TraitDto> list = getSelectionMethodListForStudy(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<TraitDto>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<TraitDto> getSelectionMethodListForStudy(final int studyIdentifier) {
		final SQLQuery traitSqlQuery = this.session.createSQLQuery(TraitServiceImpl.STUDY_SELECTION_METHODS_QUERY);
		traitSqlQuery.addScalar("cvterm_id");
		traitSqlQuery.addScalar("name");
		traitSqlQuery.setParameter(0, studyIdentifier);
		final List<Object[]> list = traitSqlQuery.list();
		final List<TraitDto> traitList = new ArrayList<TraitDto>();
		for (final Object[] rows : list) {
			traitList.add(new TraitDto((Integer) rows[0], (String) rows[1]));
		}
		return traitList;
	}
}
