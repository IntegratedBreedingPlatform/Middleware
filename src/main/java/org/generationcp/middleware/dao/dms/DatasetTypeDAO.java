package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.DatasetTypeDTO;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.hibernate.SQLQuery;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.HibernateException;
import java.util.ArrayList;
import java.util.List;

public class DatasetTypeDAO extends GenericDAO<DatasetType, Integer> {

	public List<Integer> getObservationDatasetTypeIds() {
		return this.getSession().createCriteria(DatasetType.class, "datasetType")
			.setProjection(Projections.property("datasetType.datasetTypeId"))
			.add(Restrictions.eq("datasetType.isObservationType", true)).list();
	}

	public List<Integer> getSubObservationDatasetTypeIds() {
		return this.getSession().createCriteria(DatasetType.class, "datasetType")
			.setProjection(Projections.property("datasetType.datasetTypeId"))
			.add(Restrictions.eq("datasetType.isSubObservationType", true)).list();
	}

	public List<String> getObservationLevels(final Integer pageSize, final Integer pageNumber) {
		final StringBuilder sqlString = new StringBuilder();
		try {
			sqlString
				.append(" SELECT name")
				.append(" FROM dataset_type ").append(" WHERE is_subobs_type = true  ").append(" OR is_obs_type = true  ")
				.append(" ORDER BY dataset_type_id ");
			;

			final SQLQuery query =
				this.getSession().createSQLQuery(sqlString.toString()).addScalar("name");

			if (pageNumber != null && pageSize != null) {
				query.setFirstResult(pageSize * (pageNumber - 1));
				query.setMaxResults(pageSize);
			}

			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getObservationLevels(): " + e.getMessage(), e);
		}
	}


	public long countSubObservationLevels() {
		try {
			final Criteria criteria = this.getSession().createCriteria(DatasetType.class, "datasetType");
			criteria.setProjection(Projections.rowCount())
				.add(Restrictions.eq("datasetType.isSubObservationType", true));

			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in countSubObservationLevels(): " + e.getMessage(), e);
		}
	}
}
