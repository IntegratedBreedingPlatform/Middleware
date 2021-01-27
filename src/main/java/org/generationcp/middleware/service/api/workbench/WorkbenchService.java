package org.generationcp.middleware.service.api.workbench;

import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkbenchService {

	WorkbenchSidebarCategoryLink getWorkbenchSidebarLinksByCategoryId(Integer workbenchSidebarCategoryLink);

	void close();

}
