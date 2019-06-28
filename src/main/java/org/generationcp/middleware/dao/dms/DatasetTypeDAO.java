package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

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
}
