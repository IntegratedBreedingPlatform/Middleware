package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Plant;
import org.hibernate.SQLQuery;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantDao extends GenericDAO<Plant, Integer> {

	private static final String MAX_PLANT_NUMBER_QUERY =
		"select nde.nd_experiment_id  as nd_experiment_id,  max(p.plant_no) as max_plant_no  from nd_experiment nde"
			+ " inner join plant p on p.nd_experiment_id = nde.nd_experiment_id"
			+ " where nde.nd_experiment_id in (:experimentIds)  group by nde.nd_experiment_id";

	/*This query returns the maximun sequence number included in sample name. It's the number after the : in the sample name. If : does
	not exist, returns 1.*/
	private static final String MAX_SEQUENCE_NUMBER_QUERY = "SELECT st.dbxref_id as gid," + " max(IF(           convert("
		+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1),               SIGNED) = 0,           0,"
		+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1))*1) AS max_sequence_no"
		+ " FROM project p" + " INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id"
		+ " INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id"
		+ " INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id"
		+ " INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id"
		+ " INNER JOIN stock st ON st.stock_id = es.stock_id INNER JOIN plant pl ON pl.nd_experiment_id = nde.nd_experiment_id"
		+ " INNER JOIN sample s ON s.plant_id = pl.plant_id WHERE st.dbxref_id IN (:gids)"
		+ " GROUP BY st.dbxref_id;";

	public Map<Integer, Integer> getMaxPlantNumber(final Collection<Integer> experimentIds) {
		final SQLQuery createSQLQuery = this.getSession().createSQLQuery(PlantDao.MAX_PLANT_NUMBER_QUERY);
		createSQLQuery.addScalar("nd_experiment_id", new IntegerType());
		createSQLQuery.addScalar("max_plant_no", new IntegerType());

		createSQLQuery.setParameterList("experimentIds", experimentIds);
		return this.mapResults(createSQLQuery.list());

	}

	public Map<Integer, Integer> getMaxSequenceNumber(final Collection<Integer> gids) {
		final SQLQuery createSQLQuery = this.getSession().createSQLQuery(PlantDao.MAX_SEQUENCE_NUMBER_QUERY);
		createSQLQuery.addScalar("gid", new StringType());
		createSQLQuery.addScalar("max_sequence_no", new IntegerType());

		createSQLQuery.setParameterList("gids", gids);
		return this.mapResultsToSampleSequence(createSQLQuery.list());
	}

	private Map<Integer, Integer> mapResults(final List<Object[]> results) {

		final Map<Integer, Integer> map = new HashMap<>();
		if (results != null && !results.isEmpty()) {

			for (final Object[] row : results) {
				map.put((Integer) row[0], (Integer) row[1]);
			}
		}
		return map;
	}


	private Map<Integer, Integer> mapResultsToSampleSequence(final List<Object[]> results) {

		final Map<Integer, Integer> map = new HashMap<>();
		if (results != null && !results.isEmpty()) {

			for (final Object[] row : results) {
				map.put(Integer.valueOf((String) row[0]), (Integer) row[1]);
			}
		}
		return map;
	}
}
