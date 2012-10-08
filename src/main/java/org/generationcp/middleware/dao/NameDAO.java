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
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class NameDAO extends GenericDAO<Name, Integer>{

    @SuppressWarnings("unchecked")
    public List<Name> getByGIDWithFilters(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException {
        try {
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
            throw new MiddlewareQueryException("Error with getByGIDWithFilters(gid=" + gid + ", status=" + status + ", type=" + type
                    + ") query from Name " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Name getByGIDAndNval(Integer gid, String nval) throws MiddlewareQueryException {
        try {
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
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByGIDAndNval(gid=" + gid + ", nval=" + nval + ") query from Name "
                    + e.getMessage(), e);
        }
    }

    public void validateId(Name name) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = name.getNid();
        if (id != null && id.intValue() > 0) {
            throw new MiddlewareQueryException("Error with validateId(name=" + name + "): Cannot update a Central Database record. "
                    + "Name object to update must be a Local Record (ID must be negative)");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        try {

            if (nIds == null || nIds.isEmpty()) {
                return new ArrayList<Name>();
            }

            Criteria crit = getSession().createCriteria(Name.class);
            crit.add(Restrictions.in("nid", nIds));
            List<Name> names = crit.list();
            if (names.isEmpty()) {
                return null;
            }
            return names;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getNamesByNameIds(nIds=" + nIds + ") query from Name " + e.getMessage(), e);
        }
    }

    public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Name.class);
            crit.add(Restrictions.eq("nid", nId));

            Name name = (Name) crit.uniqueResult();

            return name;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getNameByNameId(nId=" + nId + ") query from Name " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the gId and nId pairs for the given germplasm names
     * 
     * @param germplasmNames the list of germplasm names
     * @return the list of GidNidElement (gId and nId pairs)
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<GidNidElement> getGidAndNidByGermplasmNames(List<String> germplasmNames) throws MiddlewareQueryException {

        List<GidNidElement> toReturn = new ArrayList<GidNidElement>();

        if (germplasmNames == null || germplasmNames.isEmpty()) {
            return toReturn;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Name.GET_GID_AND_NID_BY_GERMPLASM_NAME);
            query.setParameterList("germplasmNameList", germplasmNames);
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer gId = (Integer) result[0];
                    Integer nId = (Integer) result[1];
                    GidNidElement element = new GidNidElement(gId, nId);
                    toReturn.add(element);
                }
            }

            return toReturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getGidAndNidByGermplasmNames(germplasmNames=" + germplasmNames
                    + ") query from Name " + e.getMessage(), e);
        }
    }

}