package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Optional;

public interface StudyEnvironmentService {

	List<StudyInstance> createStudyEnvironments(CropType crop, int studyId, int datasetId, Integer numberOfEnvironmentsToGenerate);

	List<StudyInstance> getStudyEnvironments(int studyId);

	void deleteStudyEnvironments(Integer studyId, List<Integer> environmentIds);

	Optional<StudyInstance> getStudyEnvironments(int studyId, Integer environmentId);

}
