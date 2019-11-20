package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;

public interface StudyInstanceService {

	StudyInstance createStudyInstance(final CropType crop, final int studyId, final int datasetId);

	List<StudyInstance> getStudyInstances(int studyId);

	void deleteStudyInstance(final Integer studyId, final Integer instanceId);

}
