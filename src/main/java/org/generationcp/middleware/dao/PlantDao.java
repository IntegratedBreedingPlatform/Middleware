
package org.generationcp.middleware.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.Plant;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

public class PlantDao extends GenericDAO<Plant, Integer> {

	private static final String MAX_PLANT_NUMBER_QUERY =
			"select nde.nd_experiment_id  as nd_experiment_id,\n" + " max(p.plant_no) as max_plant_no \n" + "from nd_experiment nde\n"
					+ " inner join plant p on p.nd_experiment_id = nde.nd_experiment_id\n"
					+ "where nde.nd_experiment_id in (:experimentIds)\n" + " group by nde.nd_experiment_id";

	public Plant getByPlantId(final String plantId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Plant.class);
		criteria.add(Restrictions.eq("plantId", plantId));
		return (Plant) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public Map<Integer, Integer> getMaxPlantNumber(final Collection<Integer> experimentIds) {
		final SQLQuery createSQLQuery = this.getSession().createSQLQuery(PlantDao.MAX_PLANT_NUMBER_QUERY);
		createSQLQuery.addScalar("nd_experiment_id", new IntegerType());
		createSQLQuery.addScalar("max_plant_no", new IntegerType());

		createSQLQuery.setParameterList("experimentIds", experimentIds);
		return this.mapResults(createSQLQuery.list());

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
}
