package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;

public interface StudyInstanceService {

	StudyInstance createStudyInstance(final CropType crop, final int datasetId, final int instanceNumber);

	List<StudyInstance> getStudyInstances(int studyId);

}
