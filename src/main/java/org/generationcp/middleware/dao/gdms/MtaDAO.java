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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Mta}.
 *
 * @author Joyce Avestro
 */

public class MtaDAO extends GenericDAO<Mta, Integer>{
    
    public static final String GET_MTAS_BY_TRAIT = 
            "SELECT mta_id "
            + "     ,marker_id "
            + "     ,dataset_id "
            + "     ,map_id "
            + "     ,linkage_group "
            + "     ,position "
            + "     ,tid "
            + "     ,effect "
            + "     ,CONCAT(hv_allele, '') "
            + "     ,CONCAT(experiment, '') "
            + "     ,score_value "
            + "     ,r_square "
            + "FROM gdms_mta "
            + "WHERE tid = :traitId ";

    @SuppressWarnings("unchecked")
    public List<Mta> getMtasByTrait(Integer traitId) throws MiddlewareQueryException {
      List<Mta> toReturn = new ArrayList<Mta>();
    	
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("tId", traitId));
			toReturn = criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMtasByTrait(traitId=" + traitId + ") query from gdms_mta: "
                    + e.getMessage(), e);
        }
        return toReturn; 
    }
    
    public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
        try {
            this.flush();

            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_mta WHERE dataset_id = " + datasetId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch (HibernateException e) {
            logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MtaDAO: " + e.getMessage(), e);
        }
    }



}
