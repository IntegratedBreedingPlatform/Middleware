package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Plant;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class PlantDao extends GenericDAO<Plant, Integer> {

	public Plant getByPlantId(String plantId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Plant.class);
		criteria.add(Restrictions.eq("plantId", plantId));
		return (Plant) criteria.getExecutableCriteria(getSession()).uniqueResult();
	}
}
