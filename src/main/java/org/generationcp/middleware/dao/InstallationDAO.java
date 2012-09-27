package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Installation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;


public class InstallationDAO extends GenericDAO<Installation, Long>{
    
    @SuppressWarnings("unchecked")
    public List<Installation> getByAdminId(Long id){
        List<Installation> toreturn = new ArrayList<Installation>();
        
        Criteria criteria = getSession().createCriteria(Installation.class);
        criteria.add(Restrictions.eq("adminId", id));
        
        toreturn.addAll(criteria.list());
        
        return toreturn;
    }
    
    public Installation getLatest(Database instance){
        Long id = null;
        Criteria criteria = getSession().createCriteria(Installation.class);
       
        if(instance == Database.LOCAL) {
            criteria.setProjection(Projections.min("id"));
            id = (Long) criteria.uniqueResult();
        } else if(instance == Database.CENTRAL) {
            criteria.setProjection(Projections.max("id"));
            id = (Long) criteria.uniqueResult();
        }
        
        if(id != null) {
            return findById(id, false);
        } else {
            return null;
        }
    }

}
