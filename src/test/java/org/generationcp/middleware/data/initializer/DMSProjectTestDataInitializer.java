package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.dms.DmsProject;

public class DMSProjectTestDataInitializer {

	public static DmsProject testCreateDMSProject(final int projectId, final String projectName, final String description, final String uuid) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(projectId);
		dmsProject.setName(projectName);
		dmsProject.setDescription(description);
		dmsProject.setProgramUUID(uuid);
		return dmsProject;
	}
}
