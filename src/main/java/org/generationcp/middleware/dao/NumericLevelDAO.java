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
import org.generationcp.middleware.pojos.NumericLevel;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.NumericLevelPK;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

public class NumericLevelDAO extends GenericDAO<NumericLevel, NumericLevelPK>{

    @SuppressWarnings("rawtypes")
    public List<NumericLevelElement> getValuesByOunitIDList(List<Integer> ounitIdList) throws QueryException {
        
        List<NumericLevelElement> levelValues = new ArrayList<NumericLevelElement>();

        if (ounitIdList == null || ounitIdList.isEmpty()){
            return levelValues;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(NumericLevel.GET_BY_OUNIT_ID_LIST);
            query.setParameterList("ounitIdList", ounitIdList);

            List results = query.list();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer ounitId = (Integer) result[0];
                    Integer factorId = (Integer) result[1];
                    String factorName = (String) result[2];
                    Double value = (Double) result[3];

                    NumericLevelElement levelElement = new NumericLevelElement(ounitId, factorId, factorName, value);

                    levelValues.add(levelElement);
                }
            }

            return levelValues;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Numeric Level Values by list of Observation Unit IDs query: " + ex.getMessage(), ex);
        }
    }
}
