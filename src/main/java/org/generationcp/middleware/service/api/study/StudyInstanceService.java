package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.dms.DescriptorData;
import org.generationcp.middleware.domain.dms.ObservationData;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Optional;

public interface StudyInstanceService {

	List<StudyInstance> createStudyInstances(CropType crop, int studyId, int datasetId, Integer numberOfInstancesToGenerate);

	List<StudyInstance> getStudyInstances(int studyId);

	void deleteStudyInstances(Integer studyId, List<Integer> instanceIds);

	Optional<StudyInstance> getStudyInstance(int studyId, Integer instanceId);

	ObservationData addInstanceObservation(ObservationData observationData);

	ObservationData updateInstanceObservation(ObservationData observationData);

	Optional<ObservationData> getInstanceObservation(Integer instanceId, final Integer observationDataId, final Integer variableId);

	DescriptorData addInstanceDescriptor(DescriptorData descriptorData);

	DescriptorData updateInstanceDescriptor(DescriptorData descriptorData);

	Optional<DescriptorData> getInstanceDescriptor(Integer instanceId, final Integer descriptorDataId, final Integer variableId);


}
