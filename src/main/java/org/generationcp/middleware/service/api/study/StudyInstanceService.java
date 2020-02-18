package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Optional;

public interface StudyInstanceService {

	List<StudyInstance> createStudyInstances(CropType crop, int studyId, int datasetId, Integer numberOfInstancesToGenerate);

	List<StudyInstance> getStudyInstances(int studyId);

	void deleteStudyInstance(Integer studyId, Integer instanceId);

	Optional<StudyInstance> getStudyInstance(int studyId, Integer instanceId);

}
