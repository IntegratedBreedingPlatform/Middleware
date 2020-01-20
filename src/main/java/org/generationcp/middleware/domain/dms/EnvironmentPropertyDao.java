package org.generationcp.middleware.domain.dms;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentPropertyDao extends GenericDAO<ExperimentProperty, Integer> {

	public Map<Integer, String> getEnvironmentVariablesMap(final Integer datasetId, final Integer instanceDbId) {
		Preconditions.checkNotNull(datasetId);
		final String sql = "SELECT "
			+ "    xp.type_id as variableId, "
			+ "	   xp.value as value "
			+ "FROM "
			+ "    nd_experiment e "
			+ "        INNER JOIN "
			+ "    nd_experimentprop xp ON xp.nd_experiment_id = e.nd_experiment_id "
			+ "WHERE "
			+ "		e.project_id = :datasetId and e.type_id = 1020 "
			+ "		and e.nd_experiment_id = :instanceDbId";

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.addScalar("variableId").addScalar("value").setParameter("datasetId", datasetId).setParameter("instanceDbId", instanceDbId);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		final List<Map<String, Object>> results = query.list();
		final Map<Integer, String> geoProperties = new HashMap<>();
		for (final Map<String, Object> result : results) {
			final Integer variableId = (Integer) result.get("variableId");
			final String value = (String) result.get("value");
			geoProperties.put(variableId, value);
		}
		return geoProperties;
	}

}
