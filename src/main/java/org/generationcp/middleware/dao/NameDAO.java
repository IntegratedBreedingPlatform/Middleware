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
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Name}.
 * 
 */
public class NameDAO extends GenericDAO<Name, Integer>{

    @SuppressWarnings("unchecked")
    public List<Name> getByGIDWithFilters(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            StringBuilder queryString = new StringBuilder();
	            queryString.append("SELECT {n.*} from NAMES n WHERE n.gid = :gid ");
	
	            if (status != null && status != 0) {
	                queryString.append("AND n.nstat = :nstat ");
	            }
	
	            if (type != null) {
	                queryString.append("AND n.ntype = :ntype ");
	            }
	
	            SQLQuery query = getSession().createSQLQuery(queryString.toString());
	            query.addEntity("n", Name.class);
	            query.setParameter("gid", gid);
	
	            if (status != null && status != 0) {
	                query.setParameter("nstat", status);
	            }
	
	            if (type != null) {
	                query.setParameter("ntype", Integer.valueOf(type.getUserDefinedFieldID()));
	            }
	
	            return query.list();
        	}

            /**
             * List<Criterion> criterions = new ArrayList<Criterion>();
             * Criterion gidCriterion = Restrictions.eq("germplasmId", gid);
             * criterions.add(gidCriterion);
             * 
             * if(status != null && status != 0) { Criterion statusCriterion =
             * Restrictions.eq("nstat", status);
             * criterions.add(statusCriterion); }
             * 
             * if(type != null) { Integer typeid = type.getUserDefinedFieldID();
             * Criterion typeCriterion = Restrictions.eq("type.fldno", typeid);
             * criterions.add(typeCriterion); }
             * 
             * List<Name> results = getByCriteria(criterions); return results;
             **/
        } catch (HibernateException e) {
            logAndThrowException("Error with getByGIDWithFilters(gid=" + gid + ", status=" + status + ", type=" + type
                    + ") query from Name " + e.getMessage(), e);
        }
        return new ArrayList<Name>();
    }

    @SuppressWarnings("unchecked")
    public Name getByGIDAndNval(Integer gid, String nval) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            Criteria crit = getSession().createCriteria(Name.class);
	            crit.add(Restrictions.eq("germplasmId", gid));
	            crit.add(Restrictions.eq("nval", nval));
	            List<Name> names = crit.list();
	            if (names.isEmpty()) {
	                // return null if no Name objects match
	                return null;
	            } else {
	                // return first result in the case of multiple matches
	                return names.get(0);
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByGIDAndNval(gid=" + gid + ", nval=" + nval + ") query from Name "
                    + e.getMessage(), e);
        }
        return null;
    }

    public void validateId(Name name) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
    	if (name != null){
	        Integer id = name.getNid();
	        if (id != null && id.intValue() > 0) {
	            logAndThrowException("Error with validateId(name=" + name + "): Cannot update a Central Database record. "
	                    + "Name object to update must be a Local Record (ID must be negative)", new Throwable());
	        }
    	} else {
            logAndThrowException("Error with validateId(name=" + name + "): name is null. ", new Throwable());
    	}
    }

    @SuppressWarnings("unchecked")
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        try {
            if (nIds != null && !nIds.isEmpty()) {
	            Criteria crit = getSession().createCriteria(Name.class);
	            crit.add(Restrictions.in("nid", nIds));
	            return crit.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getNamesByNameIds(nIds=" + nIds + ") query from Name " + e.getMessage(), e);
        }
        return new ArrayList<Name>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Name> getPreferredIdsByListId(Integer listId) throws MiddlewareQueryException {
       try {
    	   if (listId != null){
	           SQLQuery query = getSession().createSQLQuery(Name.GET_PREFERRED_IDS_BY_LIST_ID);
	           query.setParameter("listId", listId);
	           query.addEntity("n", Name.class);
	           return query.list();
    	   }
       } catch (HibernateException e) {
           logAndThrowException("Error with getPreferredIdsByListId(listId=" + listId + ") query from Name " + e.getMessage(), e);
       }
       return new ArrayList<Name>();
    }

    public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
        try {
        	if (nId != null){
	            Criteria crit = getSession().createCriteria(Name.class);
	            crit.add(Restrictions.eq("nid", nId));
	            return (Name) crit.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getNameByNameId(nId=" + nId + ") query from Name " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves the gId and nId pairs for the given germplasm names
     * 
     * @param germplasmNames the list of germplasm names
     * @return the list of GidNidElement (gId and nId pairs)
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<GermplasmNameDetails> getGermplasmNameDetailsByNames(List<String> germplasmNames) throws MiddlewareQueryException {
        List<GermplasmNameDetails> toReturn = new ArrayList<GermplasmNameDetails>();

        try {
            if (germplasmNames != null && !germplasmNames.isEmpty()) {
	            SQLQuery query = getSession().createSQLQuery(Name.GET_NAME_DETAILS_BY_NAME);
	            query.setParameterList("germplasmNameList", germplasmNames);
	            List results = query.list();
	
	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                    Integer gId = (Integer) result[0];
                        Integer nId = (Integer) result[1];
                        String nVal = (String) result[2];
	                    GermplasmNameDetails element = new GermplasmNameDetails(gId, nId, nVal);
	                    toReturn.add(element);
	                }
	            }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getGidAndNidByGermplasmNames(germplasmNames=" + germplasmNames
                    + ") query from Name " + e.getMessage(), e);
        }
        return toReturn;
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();
        for(Integer gid : gids){
            toreturn.put(gid, null);
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(Name.GET_PREFFERED_IDS_BY_GIDS);
            query.setParameterList("gids", gids);
            
            List<Object> results = query.list();
            for(Object result : results){
                Object resultArray[] = (Object[]) result;
                Integer gid = (Integer) resultArray[0];
                String preferredId = (String) resultArray[1];
                toreturn.put(gid, preferredId);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getPrefferedIdsByGIDs(gids=" + gids + ") query from Name " + e.getMessage(), e);
        }
        
        return toreturn;
    }
    
    @SuppressWarnings("unchecked")
    public Map<Integer, String> getPrefferedNamesByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();
        for(Integer gid : gids){
            toreturn.put(gid, null);
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(Name.GET_PREFFERED_NAMES_BY_GIDS);
            query.setParameterList("gids", gids);
            
            List<Object> results = query.list();
            for(Object result : results){
                Object resultArray[] = (Object[]) result;
                Integer gid = (Integer) resultArray[0];
                String preferredId = (String) resultArray[1];
                toreturn.put(gid, preferredId);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getPrefferedNamesByGIDs(gids=" + gids + ") query from Name " + e.getMessage(), e);
        }
        
        return toreturn;
    }
    
    @SuppressWarnings("unchecked")
	public List<Name> getNamesByGids(List<Integer> gids) throws MiddlewareQueryException {
    	List<Name> toReturn = new ArrayList<Name>();
    	
    	if (gids == null || gids.isEmpty()){
    		return toReturn;
    	}
    	
        try{
            Criteria criteria = getSession().createCriteria(Name.class);
			criteria.add(Restrictions.in("germplasmId", gids));

			toReturn = (List<Name>)  criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getNamesByGids(gids=" + gids + ") query from Name " + e.getMessage(), e);
        }
        
        return toReturn;
    }
    
    
    
    
}