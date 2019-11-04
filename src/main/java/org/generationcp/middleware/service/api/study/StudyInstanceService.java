package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;

public interface StudyInstanceService {

	StudyInstance createStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber);

	List<StudyInstance> getStudyInstances(int studyId);

	void removeStudyInstance(CropType crop, Integer datasetId, String instanceNumber);
}
