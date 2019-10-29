package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;

public interface StudyInstanceService {

	void createStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber);

	void removeStudyInstance(CropType crop, Integer datasetId, String instanceNumber);
}
