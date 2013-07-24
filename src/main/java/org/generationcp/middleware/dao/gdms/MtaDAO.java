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
package org.generationcp.middleware.dao.gdms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Mta;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Mta}.
 *
 * @author Joyce Avestro
 */

public class MtaDAO extends GenericDAO<Mta, Integer>{
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Mta> getMtasByTrait(Integer traitId) throws MiddlewareQueryException {
        List<Mta> toReturn = new ArrayList<Mta>();
        try {
            if (traitId != null){
                SQLQuery query = getSession().createSQLQuery(Mta.GET_MTAS_BY_TRAIT);
                query.setParameter("traitId", traitId);
                List results = (List<Mta>) query.list();
                
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer mtaId = (Integer) result[0];
                        Integer markerId = (Integer) result[1];
                        Integer datasetId = (Integer) result[2];
                        Integer mapId = (Integer) result[3];
                        String linkageGroup = (String) result[4];
                        Float position = (Float) result[5];
                        Integer tId = (Integer) result[6];
                        Integer effect = (Integer) result[7];
                        String hvAllele = (String) result[8];
                        String experiment = (String) result[9];
                        Float scoreValue = (Float) result[10];
                        Float rSquare = (Float) result[11];
                        
                        Mta element = new Mta(mtaId, markerId, datasetId, mapId, linkageGroup, 
                                position, tId, effect, hvAllele, experiment, scoreValue, rSquare);
                        
                        toReturn.add(element);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMtasByTrait(traitId=" + traitId + ") query from gdms_mta: "
                    + e.getMessage(), e);
        }
        return toReturn; 
    }


}
