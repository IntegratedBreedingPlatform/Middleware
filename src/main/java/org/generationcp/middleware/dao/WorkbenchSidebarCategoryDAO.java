
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/21/13 Time: 2:28 PM To change this template use File | Settings | File Templates.
 */
public class WorkbenchSidebarCategoryDAO extends GenericDAO<WorkbenchSidebarCategory, Integer> {

	public List<WorkbenchSidebarCategory> getCategoriesByLinkIds(final List<Integer> linkIds) {
		final List<WorkbenchSidebarCategory> workbenchSidebarCategories = new ArrayList<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchSidebarCategory.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			criteria.createAlias("workbenchSidebarCategoryLinks", "workbenchSidebarCategoryLinks");
			criteria.add(Restrictions.in("workbenchSidebarCategoryLinks.sidebarCategoryLinkId", linkIds));
			criteria.addOrder(Order.asc("sidebarCategoryId"));
			criteria.addOrder(Order.asc("workbenchSidebarCategoryLinks.workbenchSidebarCategory"));
			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getCategoriesByLinkIds(links=" + linkIds.toString() + ") query from Sidebar: "
				+ e.getMessage(), e);
		}

		return workbenchSidebarCategories;
	}

}
