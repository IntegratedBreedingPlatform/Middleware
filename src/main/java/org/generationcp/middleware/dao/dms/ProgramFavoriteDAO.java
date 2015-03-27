/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import java.util.List;

/**
 * DAO class for {@link ProgramFavoriteDAO}.
 * 
 */
@SuppressWarnings("unchecked")
public class ProgramFavoriteDAO extends GenericDAO<ProgramFavorite, Integer> {
    
    public List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID) throws MiddlewareQueryException{
    	
    	try{
    		
    		Criteria criteria = getSession().createCriteria(getPersistentClass());
    		criteria.add(Restrictions.eq("entityType", type.getName()));
    		criteria.add(Restrictions.eq("uniqueID", programUUID));

			return (List<ProgramFavorite>) criteria.list();
    		
    	} catch (HibernateException e) {
            logAndThrowException(
                    "Error in getProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}
    	
    	return null;
    	
    }

	public ProgramFavorite getProgramFavorite(String programUUID, ProgramFavorite.FavoriteType type, Integer entityId) throws MiddlewareQueryException{

		try{

			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("uniqueID", programUUID));
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.add(Restrictions.eq("entityId", entityId));

			List<ProgramFavorite> result = (List<ProgramFavorite>) criteria.list();
			return result.size() > 0 ? result.get(0) : null;

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}
	}
    
    public int countProgramFavorites(ProgramFavorite.FavoriteType type) throws MiddlewareQueryException{
    	
    	try{
    		
    		Criteria criteria = getSession().createCriteria(getPersistentClass());
    		criteria.add(Restrictions.eq("entityType", type.getName()));
    		criteria.setProjection(Projections.rowCount());

			return (Integer) criteria.uniqueResult();
    		
    	} catch (HibernateException e) {
            logAndThrowException(
                    "Error in countProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}
    	
    	return 0;
    	
    }

	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, int max, String programUUID) throws MiddlewareQueryException{
		try{
    		
    		Criteria criteria = getSession().createCriteria(getPersistentClass());
    		criteria.add(Restrictions.eq("entityType", type.getName()));
    		criteria.add(Restrictions.eq("uniqueID", programUUID));
    		criteria.setMaxResults(max);

			return (List<ProgramFavorite>) criteria.list();
    		
    	} catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.getName() + "," + max + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}
	}
     
	
}
