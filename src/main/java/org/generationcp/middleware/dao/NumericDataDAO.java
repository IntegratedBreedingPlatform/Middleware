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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.NumericData;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericDataPK;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class NumericDataDAO extends GenericDAO<NumericData, NumericDataPK>{

    public List<Integer> getObservationUnitIdsByTraitScaleMethodAndValueCombinations(List<TraitCombinationFilter> filters, int start,
            int numOfRows) {
        Criteria crit = getSession().createCriteria(NumericData.class);
        crit.createAlias("variate", "variate");
        crit.setProjection(Projections.distinct(Projections.property("id.observationUnitId")));

        // keeps track if at least one filter was added
        boolean filterAdded = false;

        for (TraitCombinationFilter combination : filters) {
            Object value = combination.getValue();

            // accept only Double objects
            if (value instanceof Double || value instanceof NumericRange) {
                crit.add(Restrictions.eq("variate.traitId", combination.getTraitId()));
                crit.add(Restrictions.eq("variate.scaleId", combination.getScaleId()));
                crit.add(Restrictions.eq("variate.methodId", combination.getMethodId()));

                if (value instanceof NumericRange) {
                    NumericRange range = (NumericRange) value;
                    if (range.getStart() != null) {
                        crit.add(Restrictions.gt("value", range.getStart()));
                    }

                    if (range.getEnd() != null) {
                        crit.add(Restrictions.lt("value", range.getEnd()));
                    }
                } else {
                    crit.add(Restrictions.eq("value", combination.getValue()));
                }

                filterAdded = true;
            }
        }

        if (filterAdded) {
            // if there is at least one filter, execute query and return results
            crit.setFirstResult(start);
            crit.setMaxResults(numOfRows);
            return crit.list();
        } else {
            // return empty list if no filter was added
            return new ArrayList<Integer>();
        }
    }

    public List<NumericDataElement> getValuesByOunitIDList(List<Integer> ounitIdList) throws QueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(NumericData.GET_BY_OUNIT_ID_LIST);
            query.setParameterList("ounitIdList", ounitIdList);

            List<NumericDataElement> dataValues = new ArrayList<NumericDataElement>();

            List results = query.list();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer ounitId = (Integer) result[0];
                    Integer variateId = (Integer) result[1];
                    String variateName = (String) result[2];
                    Double value = (Double) result[3];

                    NumericDataElement dataElement = new NumericDataElement(ounitId, variateId, variateName, value);

                    dataValues.add(dataElement);
                }
            }

            return dataValues;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Numeric Data Values by list of Observation Unit IDs query: " + ex.getMessage());
        }
    }
}
