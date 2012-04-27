package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.Tool;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.criterion.Restrictions;

public class ToolDAO extends GenericDAO<Tool, Long>{

    public Tool findByToolName(String toolName) {
        try {
            Criteria criteria = getSession().createCriteria(Tool.class)
                                            .add(Restrictions.eq("toolName", toolName))
                                            .setMaxResults(1);

            return (Tool) criteria.uniqueResult();
        }
        catch (HibernateException ex) {
            throw new QueryException(ex);
        }
    }

}
