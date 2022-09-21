package org.generationcp.middleware.api.tool;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ToolServiceImpl implements ToolService {

	private WorkbenchDaoFactory workbenchDaoFactory;

	public ToolServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public Tool getToolWithName(final String toolId) {
		return this.workbenchDaoFactory.getToolDAO().getByToolName(toolId);
	}

}
