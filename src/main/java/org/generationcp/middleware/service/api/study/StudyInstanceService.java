package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

public interface StudyInstanceService {

	StudyInstance createStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber);

	void removeStudyInstance(CropType crop, Integer datasetId, String instanceNumber);
}
