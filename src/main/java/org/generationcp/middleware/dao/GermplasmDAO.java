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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class GermplasmDAO extends GenericDAO<Germplasm, Integer>{


    @SuppressWarnings("unchecked")
    public List<Germplasm> getByPrefName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_PREF_NAME);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByPrefName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    public BigInteger countByPrefName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
            query.setParameter("name", name);
            return (BigInteger) query.uniqueResult();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByPrefName(prefName=" + name + ") query from Germplasm: " + e.getMessage(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByName(String name, int start, int numOfRows, Operation operation, Integer status, GermplasmNameType type)
            throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");

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
            throw new MiddlewareQueryException("Error with getByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")    
    public List<Germplasm> getByName(List<String> names, int start, int numOfRows) throws MiddlewareQueryException {
        try {            
            String originalName = names.get(0);
            String standardizedName = names.get(1);
            String noSpaceName = names.get(2);

            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES);
            query.setParameter("name", originalName);
            query.setParameter("noSpaceName", noSpaceName);
            query.setParameter("standardizedName", standardizedName);
            query.addEntity("g", Germplasm.class);

            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByName(names=" + names + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with countByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
        }
    }
    
    public long countByName(List<String> names) throws MiddlewareQueryException {
        try {
            
            String originalName = names.get(0);
            String standardizedName = names.get(1);
            String noSpaceName = names.get(2);

            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES);
            query.setParameter("name", originalName);
            query.setParameter("noSpaceName", noSpaceName);
            query.setParameter("standardizedName", standardizedName);

            return ((BigInteger) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByName(names=" + names + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByMethodNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public long countByMethodNameUsingEqual(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByMethodNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByMethodNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public long countByMethodNameUsingLike(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByMethodNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByLocationNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public long countByLocationNameUsingEqual(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByLocationNameUsingEqual(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getByLocationNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<Germplasm> results = query.list();
            return results;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByLocationNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public long countByLocationNameUsingLike(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByLocationNameUsingLike(name=" + name + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public Germplasm getByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_NAME);
            query.addEntity("g", Germplasm.class);
            query.addEntity("n", Name.class);
            query.setParameter("gid", gid);
            Object[] result = (Object[]) query.uniqueResult();
            if (result != null) {
                Germplasm germplasm = (Germplasm) result[0];
                Name prefName = (Name) result[1];
                germplasm.setPreferredName(prefName);
                return germplasm;
            } else {
                return null;
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByGIDWithPrefName(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
        }
    }

    public Germplasm getByGIDWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_ABBREV);
            query.addEntity("g", Germplasm.class);
            query.addEntity("n", Name.class);
            query.addEntity("abbrev", Name.class);
            query.setParameter("gid", gid);
            Object[] result = (Object[]) query.uniqueResult();
            if (result != null) {
                Germplasm germplasm = (Germplasm) result[0];
                Name prefName = (Name) result[1];
                Name prefAbbrev = (Name) result[2];
                germplasm.setPreferredName(prefName);
                if (prefAbbrev != null) {
                    germplasm.setPreferredAbbreviation(prefAbbrev.getNval());
                }
                return germplasm;
            } else {
                return null;
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException(
                    "Error with getByGIDWithPrefAbbrev(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
        try {
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
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getProgenitorsByGIDWithPrefName(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> getGermplasmDescendantByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.GET_DESCENDANTS);
            query.setParameter("gid", gid);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List<Germplasm> result = query.list();

            return result;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public Germplasm getProgenitorByGID(Integer gid, Integer pro_no) throws MiddlewareQueryException {
        try {

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

            Germplasm result = (Germplasm) query.uniqueResult();

            return result;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getProgenitorByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    public long countGermplasmDescendantByGID(Integer gid) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
            query.setParameter("gid", gid);

            BigInteger count = (BigInteger) query.uniqueResult();

            int result = count.intValue();
            return result;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: "
                    + e.getMessage(), e);
        }
    }

    public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            List<Germplasm> toreturn = new ArrayList<Germplasm>();
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

            return toreturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException(
                    "Error with getManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }
    
    public long countManagementNeighbors(Integer gid) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_MANAGEMENT_NEIGHBORS);
            query.setParameter("gid", gid);
            
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.longValue();
        }
        catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    public long countGroupRelatives(Integer gid) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
            query.setParameter("gid", gid);
            
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.longValue();
        }
        catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) throws MiddlewareQueryException {
        try {
            List<Germplasm> toreturn = new ArrayList<Germplasm>();
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

            return toreturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
        }
    }

    public List<Germplasm> getDerivativeChildren(Integer gid) throws MiddlewareQueryException {
        try {
            List<Germplasm> toreturn = new ArrayList<Germplasm>();
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

            return toreturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getDerivativeChildren(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
                    e);
        }
    }

    public void validateId(Germplasm germplasm) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasm.getGid();
        if (id != null && id.intValue() > 0) {
            throw new MiddlewareQueryException("Error with validateId(germplasm=" + germplasm
                    + "): Cannot update a Central Database record. "
                    + "Attribute object to update must be a Local Record (ID must be negative)");
        }
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
}
