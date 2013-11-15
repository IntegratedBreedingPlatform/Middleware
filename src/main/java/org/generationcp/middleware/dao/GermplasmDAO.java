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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Germplasm}.
 * 
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer>{

	private static final String STATUS_DELETED = "9";

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByPrefName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_PREF_NAME);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByPrefName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public long countByPrefName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
            query.setParameter("name", name);
            return ((BigInteger) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByPrefName(prefName=" + name + ") query from Germplasm: " + e.getMessage(),e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByName(String name, Operation operation, Integer status, GermplasmNameType type, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");

            if (operation == null || operation == Operation.EQUAL) {
                queryString.append("n.nval = :name ");
            } else if (operation == Operation.LIKE) {
                queryString.append("n.nval LIKE :name ");
            }

            if (status != null && status != 0) {
                queryString.append("AND n.nstat = :nstat ");
            }

            if (type != null) {
                queryString.append("AND n.ntype = :ntype ");
            }

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("name", name);
            query.addEntity("g", Germplasm.class);

            if (status != null && status != 0) {
                query.setParameter("nstat", status);
            }

            if (type != null) {
                query.setParameter("ntype", Integer.valueOf(type.getUserDefinedFieldID()));
            }

            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    @SuppressWarnings("unchecked")    
    public List<Germplasm> getByName(List<String> names, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
        try {            
        	if (names != null && names.size() > 0){
	            String originalName = names.get(0);
	            String standardizedName = names.get(1);
	            String noSpaceName = names.get(2);
	            
	            // Search using = by default
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES_USING_EQUAL);
	            if (operation == Operation.LIKE) {
	                query = getSession().createSQLQuery(Germplasm.GET_BY_NAME_USING_LIKE);
	            }  
	            
	            // Set the parameters
	            query.setParameter("name", originalName);
	            if (operation == Operation.EQUAL) {
	                query.setParameter("noSpaceName", noSpaceName);
	                query.setParameter("standardizedName", standardizedName);
	            }
	            
	            query.addEntity("g", Germplasm.class);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	
	            return query.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(names=" + names + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }


    public long countByName(String name, Operation operation, Integer status, GermplasmNameType type) throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");

            if (operation == null || operation == Operation.EQUAL) {
                queryString.append("n.nval = :name ");
            } else if (operation == Operation.LIKE) {
                queryString.append("n.nval LIKE :name ");
            }

            if (status != null && status != 0) {
                queryString.append("AND n.nstat = :nstat ");
            }

            if (type != null) {
                queryString.append("AND n.ntype = :ntype ");
            }

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("name", name);

            if (status != null && status != 0) {
                query.setParameter("nstat", status);
            }

            if (type != null) {
                query.setParameter("ntype", Integer.valueOf(type.getUserDefinedFieldID()));
            }

            return ((BigInteger) query.uniqueResult()).longValue();
            
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public long countByName(List<String> names, Operation operation) throws MiddlewareQueryException {
        try {
            if (names != null){
	            String originalName = names.get(0);
	            String standardizedName = names.get(1);
	            String noSpaceName = names.get(2);
	
	            // Count using = by default
	            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES_USING_EQUAL);            
	            if (operation == Operation.LIKE) {
	                query = getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_USING_LIKE);
	            } 
	
	            // Set the parameters
	            query.setParameter("name", originalName);
	            if (operation == Operation.EQUAL){
	                query.setParameter("noSpaceName", noSpaceName);
	                query.setParameter("standardizedName", standardizedName);
	            }
	
	            return ((BigInteger) query.uniqueResult()).longValue();
            }
            
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(names=" + names + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByMethodNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMethodNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public long countByMethodNameUsingEqual(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByMethodNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByMethodNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMethodNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public long countByMethodNameUsingLike(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByMethodNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByLocationNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByLocationNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public long countByLocationNameUsingEqual(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByLocationNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByLocationNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByLocationNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public long countByLocationNameUsingLike(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByLocationNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public Germplasm getByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_NAME);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("n", Name.class);
	            query.setParameter("gid", gid);
	            List results = query.list();
	            
	            if(results.size() > 0){
	                Object[] result = (Object[]) results.get(0);
	                if (result != null) {
	                    Germplasm germplasm = (Germplasm) result[0];
	                    Name prefName = (Name) result[1];
	                    germplasm.setPreferredName(prefName);
	                    return germplasm;
	                } 
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByGIDWithPrefName(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Germplasm getByGIDWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_ABBREV);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("n", Name.class);
	            query.addEntity("abbrev", Name.class);
	            query.setParameter("gid", gid);
	            List results = query.list();
	            
	            if(results.size() > 0){
	                Object[] result = (Object[]) results.get(0);
	                if (result != null) {
	                    Germplasm germplasm = (Germplasm) result[0];
	                    Name prefName = (Name) result[1];
	                    Name prefAbbrev = (Name) result[2];
	                    germplasm.setPreferredName(prefName);
	                    if (prefAbbrev != null) {
	                        germplasm.setPreferredAbbreviation(prefAbbrev.getNval());
	                    }
	                    return germplasm;
	                }
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException(
                    "Error with getByGIDWithPrefAbbrev(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            List<Germplasm> progenitors = new ArrayList<Germplasm>();
	
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_PROGENITORS_BY_GID_WITH_PREF_NAME);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("n", Name.class);
	            query.setParameter("gid", gid);
	            List<Object[]> results = query.list();
	            for (Object[] result : results) {
	                Germplasm germplasm = (Germplasm) result[0];
	                Name prefName = (Name) result[1];
	                germplasm.setPreferredName(prefName);
	                progenitors.add(germplasm);
	            }
	
	            return progenitors;
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getProgenitorsByGIDWithPrefName(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
   }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getGermplasmDescendantByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            Query query = getSession().getNamedQuery(Germplasm.GET_DESCENDANTS);
	            query.setParameter("gid", gid);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return query.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }

    public Germplasm getProgenitorByGID(Integer gid, Integer pro_no) throws MiddlewareQueryException {
        try {
        	if (gid != null & pro_no != null){
	            String progenitorQuery = "";
	            if (pro_no == 1) {
	                progenitorQuery = Germplasm.GET_PROGENITOR1;
	            } else if (pro_no == 2) {
	                progenitorQuery = Germplasm.GET_PROGENITOR2;
	            } else if (pro_no > 2) {
	                progenitorQuery = Germplasm.GET_PROGENITOR;
	            }
	
	            Query query = getSession().getNamedQuery(progenitorQuery);
	            query.setParameter("gid", gid);
	
	            if (pro_no > 2) {
	                query.setParameter("pno", pro_no);
	            }
	
	            return (Germplasm) query.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getProgenitorByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return null;
    }

    public long countGermplasmDescendantByGID(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
            Query query = getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
            query.setParameter("gid", gid);
            return ((BigInteger) query.uniqueResult()).longValue();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_MANAGEMENT_NEIGHBORS);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("n", Name.class);
	            query.setParameter("gid", gid);
	
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	
	            for (Object resultObject : query.list()) {
	                Object[] result = (Object[]) resultObject;
	                Germplasm germplasm = (Germplasm) result[0];
	                Name prefName = (Name) result[1];
	                germplasm.setPreferredName(prefName);
	                toreturn.add(germplasm);
	            }
        	}

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error with getManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return toreturn;
    }
    
    public long countManagementNeighbors(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_MANAGEMENT_NEIGHBORS);
	            query.setParameter("gid", gid);
	            BigInteger count = (BigInteger) query.uniqueResult();
	            return count.longValue();
        	}
        }
        catch (HibernateException e) {
            logAndThrowException("Error with countManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countGroupRelatives(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
	            query.setParameter("gid", gid);
	            BigInteger count = (BigInteger) query.uniqueResult();
	            return count.longValue();
        	}
        }
        catch (HibernateException e) {
            logAndThrowException("Error with countGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_GROUP_RELATIVES);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("n", Name.class);
	            query.setParameter("gid", gid);
	            
	            query.setFirstResult(start);
	            query.setMaxResults(numRows);
	
	            for (Object resultObject : query.list()) {
	                Object[] result = (Object[]) resultObject;
	                Germplasm germplasm = (Germplasm) result[0];
	                Name prefName = (Name) result[1];
	                germplasm.setPreferredName(prefName);
	                toreturn.add(germplasm);
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
        return toreturn;
    }

/*    public List<Germplasm> getDerivativeChildren(Integer gid) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();
        try {
            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_DERIVATIVE_CHILDREN);
            query.addEntity("g", Germplasm.class);
            query.addEntity("n", Name.class);
            query.setParameter("gid", gid);

            for (Object resultObject : query.list()) {
                Object[] result = (Object[]) resultObject;
                Germplasm germplasm = (Germplasm) result[0];
                Name prefName = (Name) result[1];
                germplasm.setPreferredName(prefName);
                toreturn.add(germplasm);
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getDerivativeChildren(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
                    e);
        }
        return toreturn;
    }
 */   
    public List<Germplasm> getChildren(Integer gid, char methodType) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();
        try {
        	String queryString = methodType == 'D' ? Germplasm.GET_DERIVATIVE_CHILDREN : Germplasm.GET_MAINTENANCE_CHILDREN;
            SQLQuery query = getSession().createSQLQuery(queryString);
            query.addEntity("g", Germplasm.class);
            query.addEntity("n", Name.class);
            query.setParameter("gid", gid);

            for (Object resultObject : query.list()) {
                Object[] result = (Object[]) resultObject;
                Germplasm germplasm = (Germplasm) result[0];
                Name prefName = (Name) result[1];
                germplasm.setPreferredName(prefName);
                toreturn.add(germplasm);
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getDerivativeChildren(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
                    e);
        }
        return toreturn;
    	
    }

    public void validateId(Germplasm germplasm) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
    	if (germplasm != null){
	        Integer id = germplasm.getGid();
	        if (id != null && id.intValue() > 0) {
	            logAndThrowException("Error with validateId(germplasm=" + germplasm
	                    + "): Cannot update a Central Database record. "
	                    + "Attribute object to update must be a Local Record (ID must be negative)", new Throwable());
	        }
    	} else {
	            logAndThrowException("Error with validateId(germplasm=" + germplasm
	                    + "): germplasm is null. ", new Throwable());
    	}
    }
    
    public String getNextSequenceNumberForCrossName(String prefix) throws MiddlewareQueryException{
    	String nextInSequence = "1";
    	
    	try {
		      /*
		       * This query will generate next number will be queried on first "word" 
		       * of cross name.
		       * 
		       * eg.
		       *   input prefix: "IR"
		       *   output: next number in "IRNNNNN -----" sequence 
		       */
    		
        	SQLQuery query = getSession().createSQLQuery(Germplasm.GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX3);
        	query.setParameter("prefix", prefix);
        	query.setParameter("prefixLike", prefix + "%");
    		
              /* 
               * If the next number will be queried on second "word" of cross name.
               * IMPORTANT: assumes that passed in prefix value has a whitespace at the end
               * 
               * eg.
               *   input prefix: "IR "
               *   output: next number in "IR NNNNN..." sequence
               */
    			
//    		SQLQuery query = getSession().createSQLQuery(Germplasm.GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_WITH_SPACE);
//    		query.setParameter("prefix", prefix);
//    		query.setParameter("prefixLike", prefix + "%");
//                
		
    		BigInteger nextNumberInSequence = (BigInteger) query.uniqueResult();
    		
    		if (nextNumberInSequence != null){
    			nextInSequence = String.valueOf(nextNumberInSequence);
    		}
                
    	} catch (HibernateException e) {
			logAndThrowException("Error with getNextSequenceNumberForCrossName(prefix=" + prefix + ") " +
					"query : " + e.getMessage(), e);
		}
    	
    	return nextInSequence;
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByLocationId(String name, int locationID) throws MiddlewareQueryException {
    	try{
    		StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");
        	queryString.append("n.nval = :name ");
            queryString.append("AND g.glocn = :locationID ");

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("name", name);
            query.setParameter("locationID", locationID);
            query.addEntity("g", Germplasm.class);

            return query.list();
            
    	} catch (HibernateException e) {
            logAndThrowException("Error with getByLocationId(name=" + name + ", locationID=" + locationID + ") query from Germplasm: " + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }
    
    @SuppressWarnings("rawtypes")
    public Germplasm getByGIDWithMethodType(Integer gid) throws MiddlewareQueryException {
        try {
        	if (gid != null){
	            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_METHOD_TYPE);
	            query.addEntity("g", Germplasm.class);
	            query.addEntity("m", Method.class);
	            query.setParameter("gid", gid);
	            List results = query.list();
	            
	            if(results.size() > 0){
	                Object[] result = (Object[]) results.get(0);
	                if (result != null) {
	                    Germplasm germplasm = (Germplasm) result[0];
	                    Method method = (Method) result[1];
	                    germplasm.setMethod(method);
	                    return germplasm;
	                } 
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByGIDWithMethodType(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Germplasm> getByGIDRange(int startGID, int endGID) throws MiddlewareQueryException {
    	try{
    		StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
        	queryString.append("g.gid >= :startGID ");
            queryString.append("AND g.gid <= :endGID ");

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("startGID", startGID);
            query.setParameter("endGID", endGID);
            query.addEntity("g", Germplasm.class);

            return query.list();
            
    	} catch (HibernateException e) {
            logAndThrowException("Error with getByGIDRange(startGID=" + startGID + ", endGID=" + endGID + ") query from Germplasm: " + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Germplasm> getByGIDList(List<Integer> gids) throws MiddlewareQueryException {
    	
    	try{
    		StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
        	queryString.append("g.gid IN( :gids )");

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameterList("gids", gids);
            query.addEntity("g", Germplasm.class);

            return query.list();
            
    	} catch (HibernateException e) {
            logAndThrowException("Error with getByGIDList(gids=" + gids.toString() + ") query from Germplasm: " + e.getMessage(), e);
        }
        return new ArrayList<Germplasm>();
    }
    
    /**
     * @SuppressWarnings("unchecked") public List<Germplasm>
     *                                getByExample(Germplasm sample, int start,
     *                                int numOfRows) { Example sampleGermplasm =
     *                                Example.create(sample) .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                Criteria mainCriteria =
     *                                getSession().createCriteria
     *                                (Germplasm.class);
     *                                mainCriteria.add(sampleGermplasm);
     * 
     *                                if(sample.getMethod() != null) { Example
     *                                sampleMethod =
     *                                Example.create(sample.getMethod())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("method").add(
     *                                sampleMethod); }
     * 
     *                                if(sample.getLocation() != null) { Example
     *                                sampleLocation =
     *                                Example.create(sample.getLocation())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("location").
     *                                add(sampleLocation); }
     * 
     *                                if(sample.getUser() != null) { Example
     *                                sampleUser =
     *                                Example.create(sample.getUser())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("user").add(
     *                                sampleUser); }
     * 
     *                                if(sample.getReference() != null) {
     *                                Example sampleRef =
     *                                Example.create(sample.getReference())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("reference").
     *                                add(sampleRef); }
     * 
     *                                if(sample.getAttributes() != null &&
     *                                !sample.getAttributes().isEmpty()) {
     *                                Set<Attribute> attrs =
     *                                sample.getAttributes(); Criteria
     *                                attributesCriteria =
     *                                mainCriteria.createCriteria("attributes");
     *                                for(Attribute attr : attrs) { Example
     *                                sampleAttribute = Example.create(attr)
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                attributesCriteria.add(sampleAttribute); }
     *                                }
     * 
     *                                mainCriteria.setFirstResult(start);
     *                                mainCriteria.setMaxResults(numOfRows);
     *                                return mainCriteria.list(); }
     * 
     *                                public long countByExample(Germplasm
     *                                sample) { Example sampleGermplasm =
     *                                Example.create(sample) .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                Criteria mainCriteria =
     *                                getSession().createCriteria
     *                                (Germplasm.class);
     *                                mainCriteria.add(sampleGermplasm);
     * 
     *                                if(sample.getMethod() != null) { Example
     *                                sampleMethod =
     *                                Example.create(sample.getMethod())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("method").add(
     *                                sampleMethod); }
     * 
     *                                if(sample.getLocation() != null) { Example
     *                                sampleLocation =
     *                                Example.create(sample.getLocation())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("location").
     *                                add(sampleLocation); }
     * 
     *                                if(sample.getUser() != null) { Example
     *                                sampleUser =
     *                                Example.create(sample.getUser())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("user").add(
     *                                sampleUser); }
     * 
     *                                if(sample.getReference() != null) {
     *                                Example sampleRef =
     *                                Example.create(sample.getReference())
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                mainCriteria.createCriteria("reference").
     *                                add(sampleRef); }
     * 
     *                                if(sample.getAttributes() != null &&
     *                                !sample.getAttributes().isEmpty()) {
     *                                Set<Attribute> attrs =
     *                                sample.getAttributes(); Criteria
     *                                attributesCriteria =
     *                                mainCriteria.createCriteria("attributes");
     *                                for(Attribute attr : attrs) { Example
     *                                sampleAttribute = Example.create(attr)
     *                                .ignoreCase()
     *                                .enableLike(MatchMode.ANYWHERE);
     * 
     *                                attributesCriteria.add(sampleAttribute); }
     *                                }
     * 
     *                                mainCriteria.setProjection
     *                                (Projections.rowCount()); Long count =
     *                                return ((Long) mainCriteria.uniqueResult()).longValue();
     *                                }
     **/

    
    
    /**
     * Get Germplasms with names like Q or germplasms part of list with names like Q
     * @param q
     * @return List of Germplasms
     * @throws MiddlewareQueryException 
     */
    public List<Germplasm> searchForGermplasms(String q) throws MiddlewareQueryException{
        try {

        	List<Germplasm> result = new ArrayList<Germplasm>();
        	List<Germplasm> resultParents = new ArrayList<Germplasm>();
        	
        	//First priority, germplasms with GID=q
            SQLQuery p1_query = getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GID);
            p1_query.setParameter("gid", q);
            p1_query.addEntity("germplsm", Germplasm.class);
            //p1_query.setParameter("deletedStatus", STATUS_DELETED);
			result.addAll(p1_query.list());
            
            //Second priority, get germplasms with nVal like q
            SQLQuery p2_query1 = getSession().createSQLQuery(Germplasm.SEARCH_GID_BY_GERMPLASM_NAME);
            p2_query1.setParameter("q", q+"%");
            p2_query1.setParameter("deletedStatus", STATUS_DELETED);
            List p2_result1 = p2_query1.list();
            
            if(p2_result1.size()>0 && p2_result1.get(0)!=null){
	            SQLQuery p2_query2 = getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GIDS);
	            p2_query2.setParameterList("gids", p2_result1);
	            p2_query2.addEntity("germplsm", Germplasm.class);
	            result.addAll(p2_query2.list());
            }
            
            //Third priority, get germplasms in list with listname like (full text search) q
            SQLQuery p3_query1 = getSession().createSQLQuery(Germplasm.SEARCH_LIST_ID_BY_LIST_NAME);
            p3_query1.setParameter("q", q);
            p3_query1.setParameter("deletedStatus", STATUS_DELETED);
            
            List p3_result1 = p2_query1.list();
            
            if(p3_result1.size()>0 && p3_result1.get(0)!=null){            
	            SQLQuery p3_query2 = getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_LIST_ID);
	            p3_query2.setParameterList("listids", p3_result1);
	            p3_query2.addEntity("germplsm", Germplasm.class);
	            result.addAll(p3_query2.list());
            }
            
            for(Germplasm g: result){
            	List<Integer> parentGids = new ArrayList<Integer>();
            	parentGids.add(g.getGpid1());
            	parentGids.add(g.getGpid2());
            	
            	SQLQuery p_query = getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GIDS);
	            p_query.setParameterList("gids", parentGids);
	            p_query.addEntity("germplsm", Germplasm.class);
	            
	            resultParents.addAll(p_query.list());
            }
            result.addAll(resultParents);
            
            return result;

        } catch (Exception e) {
                logAndThrowException("Error with searchGermplasms(" + q + ") " + e.getMessage(), e);
                }
        return new ArrayList<Germplasm>();
    }
    
    
}
