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
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

import java.util.*;

/**
 * DAO class for {@link org.generationcp.middleware.pojos.oms.CVTerm}.
 */
@SuppressWarnings("unchecked")
public class PropertyDao extends GenericDAO<CVTerm, Integer> {

    /**
     * This will fetch entire Property by propertyId*
     * @param propertyId
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public Property getPropertyById(int propertyId) throws MiddlewareQueryException {

        try {

            SQLQuery query = getSession().createSQLQuery(
                    "select {p.*}, {tp.*}, {c.*}  from cvterm p" +
                            " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() +
                            " join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id inner join cvterm c on c.cvterm_id = cvtr.object_id " +
                            " where cvtr.type_id = " + TermId.IS_A.getId() + " and p.cvterm_id = :propertyId")
                    .addEntity("p", CVTerm.class)
                    .addEntity("tp", CVTermProperty.class)
                    .addEntity("c", CVTerm.class);

            query.setParameter("propertyId", propertyId);
            List<Object[]> result = query.list();

            if (result != null && !result.isEmpty()) {
                Property p = null;
                for (Object[] row : result) {
                    if(p == null){
                        p = new Property(TermBuilder.mapCVTermToTerm((CVTerm) row[0]));
                        if(row[1] instanceof CVTermProperty){
                            p.setCropOntologyId(((CVTermProperty) row[1]).getValue());
                        }
                    }
                    if(row.length <= 1) continue;
                    Term pc = row[1] instanceof CVTerm ? TermBuilder.mapCVTermToTerm((CVTerm) row[1]) : TermBuilder.mapCVTermToTerm((CVTerm) row[2]);
                    p.addClass(pc);
                }
                return p;
            }

        } catch (HibernateException e) {
            logAndThrowException("Error at getStandadardVariableIdByTermId :" + e.getMessage(), e);
        }
        return null;
    }
}
