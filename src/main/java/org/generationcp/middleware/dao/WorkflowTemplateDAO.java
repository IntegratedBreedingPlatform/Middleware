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

import java.util.List;

import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

public class WorkflowTemplateDAO extends GenericDAO<WorkflowTemplate, Long>{

    public List<WorkflowTemplate> findAll()  throws QueryException{
        return findAll(null, null);
    }

    public List<WorkflowTemplate> findAll(Integer start, Integer numOfRows) throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(WorkflowTemplate.class);
            if (start != null) {
                criteria.setFirstResult(start);
            }
            if (numOfRows != null) {
                criteria.setMaxResults(numOfRows);
            }
            @SuppressWarnings("unchecked")
            List<WorkflowTemplate> templates = criteria.list();

            return templates;
        } catch (HibernateException e) {
            throw new QueryException("Error with find all workflow templates: " + e.getMessage(), e);
        }
    }
}
