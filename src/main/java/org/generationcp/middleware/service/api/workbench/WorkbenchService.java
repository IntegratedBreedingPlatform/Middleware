package org.generationcp.middleware.service.api.workbench;

import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;

public interface WorkbenchService {

	WorkbenchSidebarCategoryLink getWorkbenchSidebarLinksByCategoryId(Integer workbenchSidebarCategoryLink);

}
