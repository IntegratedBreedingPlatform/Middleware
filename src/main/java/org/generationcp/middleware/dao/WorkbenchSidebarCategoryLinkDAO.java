package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/21/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkbenchSidebarCategoryLinkDAO extends GenericDAO<WorkbenchSidebarCategoryLink,Integer> {

    public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(WorkbenchSidebarCategory category, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchSidebarCategoryLink.class);

            criteria.add(Restrictions.eq("workbenchSidebarCategory.sidebarCategoryId", category.getSidebarCategoryId()));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getAllWorkbenchSidebarLinksByCategoryId(category=" + category
                    + ") query from Location: " + e.getMessage(), e);
        }
        return new ArrayList<WorkbenchSidebarCategoryLink>();
    }

}
