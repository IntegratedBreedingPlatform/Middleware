package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.Trait;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class TraitDAO extends GenericDAO<Trait, Integer> {
    public Trait getByTraitId(Integer id) {
	Criteria crit = getSession().createCriteria(Trait.class);
	crit.add(Restrictions.eq("traitId", id));
	crit.add(Restrictions.eq("nameStatus", new Integer(1)));
	List results = crit.list();

	if (results.isEmpty())
	    return null;
	else
	    return (Trait) results.get(0);
    }
}
