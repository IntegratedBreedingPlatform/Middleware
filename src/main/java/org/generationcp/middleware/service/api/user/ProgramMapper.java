package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.pojos.workbench.Project;

public class ProgramMapper {

	public static ProgramDto map(final Project project) {
		if (project != null) {
			final ProgramDto programDto = new ProgramDto();
			programDto.setId(project.getProjectId());
			programDto.setName(project.getProjectName());
			programDto.setCrop(new CropDto(project.getCropType()));
			programDto.setUuid(project.getUniqueID());
			return programDto;
		} else {
			return null;
		}
	}

}
