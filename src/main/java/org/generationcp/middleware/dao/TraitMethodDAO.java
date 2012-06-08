package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.TraitMethod;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class TraitMethodDAO extends GenericDAO<TraitMethod, Integer> {
    @SuppressWarnings("unchecked")
    public List<TraitMethod> getByTraitId(Integer traitId) {
	Criteria crit = getSession().createCriteria(TraitMethod.class);
	crit.add(Restrictions.eq("traitId", traitId));
	return crit.list();
    }
}
