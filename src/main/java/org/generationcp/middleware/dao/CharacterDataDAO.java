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
import org.generationcp.middleware.pojos.CharacterData;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterDataPK;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CharacterDataDAO extends GenericDAO<CharacterData, CharacterDataPK>{

    @SuppressWarnings("unchecked")
    public List<Integer> getObservationUnitIdsByTraitScaleMethodAndValueCombinations(List<TraitCombinationFilter> filters, int start,
            int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(CharacterData.class);
            criteria.createAlias("variate", "variate");
            criteria.setProjection(Projections.distinct(Projections.property("id.observationUnitId")));

            // keeps track if at least one filter was added
            boolean filterAdded = false;

            for (TraitCombinationFilter combination : filters) {
                Object value = combination.getValue();

                // accept only String values
                if (value instanceof String) {
                    criteria.add(Restrictions.eq("variate.traitId", combination.getTraitId()));
                    criteria.add(Restrictions.eq("variate.scaleId", combination.getScaleId()));
                    criteria.add(Restrictions.eq("variate.methodId", combination.getMethodId()));
                    criteria.add(Restrictions.eq("value", value));

                    filterAdded = true;
                }
            }

            if (filterAdded) {
                // if there is at least one filter, execute query and return results
                criteria.setFirstResult(start);
                criteria.setMaxResults((numOfRows));
                return criteria.list();
            } else {
                // return empty list if no filter was added
                return new ArrayList<Integer>();
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters=" + filters
                    + ") query from CharacterData: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    public List<CharacterDataElement> getValuesByOunitIDList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(CharacterData.GET_BY_OUNIT_ID_LIST);
            query.setParameterList("ounitIdList", ounitIdList);

            List<CharacterDataElement> dataValues = new ArrayList<CharacterDataElement>();

            List results = query.list();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer ounitId = (Integer) result[0];
                    Integer variateId = (Integer) result[1];
                    String variateName = (String) result[2];
                    String value = (String) result[3];

                    CharacterDataElement dataElement = new CharacterDataElement(ounitId, variateId, variateName, value);

                    dataValues.add(dataElement);
                }
            }

            return dataValues;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getValuesByOunitIDList(ounitIdList=" + ounitIdList + ") query from CharacterData: "
                    + e.getMessage(), e);
        }
    }
}
