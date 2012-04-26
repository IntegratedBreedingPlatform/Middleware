package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.ScaleDiscretePK;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class ScaleDiscreteDAO extends GenericDAO<ScaleDiscrete, ScaleDiscretePK>
{
	@SuppressWarnings("unchecked")
	public List<ScaleDiscrete> getByScaleId(Integer id)
	{
		Criteria crit = getSession().createCriteria(ScaleDiscrete.class);
		crit.add(Restrictions.eq("id.scaleId", id));
		return crit.list();
	}
}
