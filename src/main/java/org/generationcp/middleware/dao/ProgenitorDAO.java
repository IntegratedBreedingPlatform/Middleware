package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class ProgenitorDAO extends GenericDAO<Progenitor, ProgenitorPK> {
    public Progenitor getByGIDAndPID(Integer gid, Integer pid) {
	List<Criterion> criterions = new ArrayList<Criterion>();
	criterions.add(Restrictions.eq("pid", pid));
	criterions.add(Restrictions.eq("progntrsPK.gid", gid));
	List<Progenitor> progenitors = findByCriteria(criterions);
	if (!progenitors.isEmpty())
	    return progenitors.get(0);
	else
	    return null;
    }
}
