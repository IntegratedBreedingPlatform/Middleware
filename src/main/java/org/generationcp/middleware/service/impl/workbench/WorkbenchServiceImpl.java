package org.generationcp.middleware.service.impl.workbench;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.service.api.workbench.WorkbenchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class WorkbenchServiceImpl implements WorkbenchService {

	private HibernateSessionProvider sessionProvider;
	private WorkbenchDaoFactory workbenchDaoFactory;

	public WorkbenchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.sessionProvider);
	}
	@Override
	public WorkbenchSidebarCategoryLink getWorkbenchSidebarLinksByCategoryId(final Integer workbenchSidebarCategoryLink) {
		return this.workbenchDaoFactory.getWorkbenchSidebarCategoryLinkDao().getById(workbenchSidebarCategoryLink);
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

}
