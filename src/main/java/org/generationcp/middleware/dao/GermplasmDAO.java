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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class GermplasmDAO extends GenericDAO<Germplasm, Integer>{

    @SuppressWarnings("unchecked")
    public List<Germplasm> findAll(int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_ALL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find all query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByPrefName(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_BY_PREF_NAME);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by preferred name query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public BigInteger countByPrefName(String name) throws QueryException {
        try {
            Query query = getSession().createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
            query.setParameter("name", name);
            return (BigInteger) query.uniqueResult();
        } catch (HibernateException ex) {
            throw new QueryException("Error with count by preferred name for Germplasm: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByName(String name, int start, int numOfRows, Operation operation, Integer status, GermplasmNameType type) {
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

        /**
         * Criteria criteria = getSession().createCriteria(Germplasm.class);
         * Criteria nameCriteria = criteria.createCriteria("names");
         * 
         * if(operation == null || operation == Operation.EQUAL)
         * nameCriteria.add(Restrictions.eq("nval", name)); else if(operation ==
         * Operation.LIKE) nameCriteria.add(Restrictions.like("nval", name));
         * 
         * if(status != null && status != 0)
         * nameCriteria.add(Restrictions.eq("nstat", status));
         * 
         * if(type != null) { Criteria typeCriteria =
         * nameCriteria.createCriteria("type"); Integer typeid =
         * type.getUserDefinedFieldID();
         * typeCriteria.add(Restrictions.eq("fldno", typeid)); }
         * 
         * criteria.setFirstResult(start); criteria.setMaxResults(numOfRows);
         * return criteria.list();
         **/
    }

    public BigInteger countByName(String name, Operation operation, Integer status, GermplasmNameType type) {
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

        return (BigInteger) query.uniqueResult();

        /**
         * Criteria criteria = getSession().createCriteria(Germplasm.class);
         * criteria.setProjection(Projections.rowCount()); Criteria nameCriteria
         * = criteria.createCriteria("names");
         * 
         * if(operation == null || operation == Operation.EQUAL)
         * nameCriteria.add(Restrictions.eq("nval", name)); else if(operation ==
         * Operation.LIKE) nameCriteria.add(Restrictions.like("nval", name));
         * 
         * if(status != null && status != 0)
         * nameCriteria.add(Restrictions.eq("nstat", status));
         * 
         * if(type != null) { Criteria typeCriteria =
         * nameCriteria.createCriteria("type"); Integer typeid =
         * type.getUserDefinedFieldID();
         * typeCriteria.add(Restrictions.eq("fldno", typeid)); }
         * 
         * Long count = (Long) criteria.uniqueResult(); return count;
         **/
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByMethodNameUsingEqual(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_BY_METHOD_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by method name query using equal for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Long countByMethodNameUsingEqual(String name) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw new QueryException("Error with count by method name using equal for Germplasm: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByMethodNameUsingLike(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_BY_METHOD_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by method name using like query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Long countByMethodNameUsingLike(String name) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
            query.setParameter("name", name);
            return (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw new QueryException("Error with count by method name using like for Germplasm: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByLocationNameUsingEqual(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_BY_LOCATION_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<Germplasm>) query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by location name query using equal for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Long countByLocationNameUsingEqual(String name) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
            query.setParameter("name", name);
            return (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw new QueryException("Error with count by location name using equal for Germplasm: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Germplasm> findByLocationNameUsingLike(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<Germplasm> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by location name using like query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Long countByLocationNameUsingLike(String name) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
            query.setParameter("name", name);
            Long result = (Long) query.uniqueResult();
            return result;
        } catch (HibernateException ex) {
            throw new QueryException("Error with count by location name using like for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Germplasm getByGIDWithPrefName(Integer gid) throws QueryException {
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get by gid with pref name query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public Germplasm getByGIDWithPrefAbbrev(Integer gid) throws QueryException {
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get by gid with pref abbrev query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws QueryException {
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get progenitors by gid with pref name for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public List<Germplasm> getGermplasmDescendantByGID(Integer gid, Integer start, Integer numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Germplasm.FIND_DESCENDANTS);
            query.setParameter("gid", gid);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List<Germplasm> result = query.list();

            return result;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Germplasm Descendant by Gid: " + ex.getMessage(), ex);
        }
    }

    public Germplasm getProgenitorByGID(Integer gid, Integer pro_no) throws QueryException {
        try {

            String progenitorQuery = "";
            if (pro_no == 1) {
                progenitorQuery = Germplasm.FIND_PROGENITOR1;
            } else if (pro_no == 2) {
                progenitorQuery = Germplasm.FIND_PROGENITOR2;
            } else if (pro_no > 2) {
                progenitorQuery = Germplasm.FIND_PROGENITOR;
            }

            Query query = getSession().getNamedQuery(progenitorQuery);
            query.setParameter("gid", gid);

            if (pro_no > 2) {
                query.setParameter("pno", pro_no);
            }

            Germplasm result = (Germplasm) query.uniqueResult();

            return result;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Germplasm Progenitor by Gid: " + ex.getMessage(), ex);
        }
    }

    public int countGermplasmDescendantByGID(Integer gid) {
        Query query = getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
        query.setParameter("gid", gid);

        BigInteger count = (BigInteger) query.uniqueResult();

        int result = count.intValue();
        return result;
    }

    public List<Germplasm> getManagementNeighbors(Integer gid) throws QueryException {
        try {
            List<Germplasm> toreturn = new ArrayList<Germplasm>();
            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_MANAGEMENT_NEIGHBORS);
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get management neighbors query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public List<Germplasm> getGroupRelatives(Integer gid) throws QueryException {
        try {
            List<Germplasm> toreturn = new ArrayList<Germplasm>();
            SQLQuery query = getSession().createSQLQuery(Germplasm.GET_GROUP_RELATIVES);
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get group relatives query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public List<Germplasm> getDerivativeChildren(Integer gid) throws QueryException {
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
        } catch (HibernateException ex) {
            throw new QueryException("Error with get derivative children query for Germplasm: " + ex.getMessage(), ex);
        }
    }

    public void validateId(Germplasm germplasm) throws QueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasm.getGid();
        if (id != null && id.intValue() > 0) {
            throw new QueryException("Cannot update a Central Database record. "
                    + "Attribute object to update must be a Local Record (ID must be negative)");
        }
    }

    /**
     * @SuppressWarnings("unchecked") public List<Germplasm>
     *                                findByExample(Germplasm sample, int start,
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
     *                                public Long countByExample(Germplasm
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
     *                                (Long) mainCriteria.uniqueResult(); return
     *                                count; }
     **/
}
