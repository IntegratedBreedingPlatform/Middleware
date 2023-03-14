
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/21/13 Time: 2:29 PM To change this template use File | Settings | File Templates.
 */
public class WorkbenchSidebarCategoryLinkDAO extends GenericDAO<WorkbenchSidebarCategoryLink, Integer> {

	public WorkbenchSidebarCategoryLinkDAO(final Session session) {
		super(session);
	}
}
