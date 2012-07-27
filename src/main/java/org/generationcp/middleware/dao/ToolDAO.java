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

import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class ToolDAO extends GenericDAO<Tool, Long>{

    public Tool findByToolName(String toolName) throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(Tool.class).add(Restrictions.eq("toolName", toolName)).setMaxResults(1);

            return (Tool) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with finding tools by name: " + e.getMessage(), e);
        }
    }

}
