package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Scale;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ScaleDAO extends GenericDAO<Scale, Integer>
{
	@SuppressWarnings("unchecked")
	public List<Scale> getByTraitId(Integer traitId)
	{
		Criteria crit = getSession().createCriteria(Scale.class);
		crit.add(Restrictions.eq("traitId", traitId));
		return crit.list();
	}
}
