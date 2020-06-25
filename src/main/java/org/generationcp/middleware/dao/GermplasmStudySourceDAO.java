package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceRequest;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import java.util.List;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	public List<StudyGermplasmSourceDto> getGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {

		final SQLQuery query = this.getSession().createSQLQuery("SELECT \n"
			+ "gss.gid as `gid`,\n"
			+ "g.mgid as `groupId`,\n"
			+ "n.nval as `designation`,\n"
			+ "m.mcode as `breedingMethodAbbrevation`,\n"
			+ "m.mname as `breedingMethodName`,\n"
			+ "m.mtype as `breedingMethodType`,\n"
			+ "geo.description as `trialInstance`,\n"
			+ "loc.lname as `location`,\n"
			+ "rep_no.value as `replicationNumber`,\n"
			+ "plot_no.value as `plotNumber`,\n"
			+ "g.gdate as `germplasmDate`\n"
			+ "FROM germplasm_study_source gss\n"
			+ "INNER JOIN germplsm g ON g.gid = gss.gid\n"
			+ "INNER JOIN project p ON p.project_id = gss.project_id\n"
			+ "LEFT JOIN nd_experiment e ON e.nd_experiment_id = gss.nd_experiment_id\n"
			+ "LEFT JOIN nd_experimentprop rep_no ON rep_no.nd_experiment_id = e.nd_experiment_id AND rep_no.type_id = 8210\n"
			+ "LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = e.nd_experiment_id AND plot_no.type_id = 8200\n"
			+ "LEFT JOIN nd_geolocation geo ON e.nd_geolocation_id = geo.nd_geolocation_id\n"
			+ "LEFT JOIN nd_geolocationprop locationProp ON geo.nd_geolocation_id = locationProp.nd_geolocation_id AND locationProp.type_id = 8190\n"
			+ "LEFT JOIN location loc ON locationProp.value = loc.locid\n"
			+ "LEFT JOIN methods m ON m.mid = g.methn\n"
			+ "LEFT JOIN names n ON g.gid = n.gid AND n.ntype in (2, 5)\n"
			+ "WHERE gss.project_id = :studyId");
		query.addScalar("gid");
		query.addScalar("groupId");
		query.addScalar("designation");
		query.addScalar("breedingMethodAbbrevation");
		query.addScalar("breedingMethodName");
		query.addScalar("breedingMethodType");
		query.addScalar("trialInstance");
		query.addScalar("location");
		query.addScalar("replicationNumber", new IntegerType());
		query.addScalar("plotNumber", new IntegerType());
		query.addScalar("germplasmDate");
		query.setParameter("studyId", searchParameters.getStudyId());
		// TODO: Implement filter and pagination
		query.setMaxResults(Integer.MAX_VALUE);
		query.setResultTransformer(Transformers.aliasToBean(StudyGermplasmSourceDto.class));
		return query.list();
	}

	public long countGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("study.projectId", searchParameters.getStudyId()));
		// TODO: Implement filter
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

}
