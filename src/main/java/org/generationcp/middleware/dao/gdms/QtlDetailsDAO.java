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

import java.math.BigInteger;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;


/**
 * DAO for QtlDetails objects
 * 
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
public class QtlDetailsDAO  extends GenericDAO<QtlDetails, Integer>{

    public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_MARKER_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            return query.list();
            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMarkerIDsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
    }
    
    public long countMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_MARKER_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0L;
            }
            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countMarkerIdsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
        
    }

}
