package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.HibernateException;
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

	public Criteria getObservationLevelsCriteria() {

		Criteria criteria = this.getSession().createCriteria(DatasetType.class, "datasetType");
		criteria.setProjection(Projections.property("datasetType.name"));

		Criterion isSubObs = Restrictions.eq("datasetType.isSubObservationType", true);
		Criterion isObsType = Restrictions.eq("datasetType.isObservationType", true);

		LogicalExpression orExp = Restrictions.or(isSubObs, isObsType);
		criteria.add(orExp);

		return criteria;
	}

	public List<String> getObservationLevels(final Integer pageSize, final Integer pageNumber) {
		final Criteria criteria = this.getObservationLevelsCriteria();
		criteria.setFirstResult(pageSize * (pageNumber - 1));
		criteria.setMaxResults(pageSize);
		return criteria.list();
	}

	public long countObservationLevels() {
		try{
			final Criteria criteria = this.getObservationLevelsCriteria();
			criteria.setProjection(Projections.rowCount());
			return	((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in countObservationLevels(): " + e.getMessage(), e);
		}
	}
}
