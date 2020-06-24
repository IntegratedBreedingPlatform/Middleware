package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.dms.InstanceData;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Optional;

public interface StudyInstanceService {

	List<StudyInstance> createStudyInstances(CropType crop, int studyId, int datasetId, Integer numberOfInstancesToGenerate);

	List<StudyInstance> getStudyInstances(int studyId);

	void deleteStudyInstances(Integer studyId, List<Integer> instanceIds);

	Optional<StudyInstance> getStudyInstance(int studyId, Integer instanceId);

	InstanceData addInstanceData(InstanceData instanceData, boolean isEnvironmentCondition);

	InstanceData updateInstanceData(InstanceData instanceData, boolean isEnvironmentCondition);

	Optional<InstanceData> getInstanceData(Integer instanceId, final Integer instanceDataId, final Integer variableId, boolean isEnvironmentCondition);


}
