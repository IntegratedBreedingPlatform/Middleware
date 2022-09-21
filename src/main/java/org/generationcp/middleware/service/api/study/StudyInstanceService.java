package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.dms.InstanceDescriptorData;
import org.generationcp.middleware.domain.dms.InstanceObservationData;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Optional;

public interface StudyInstanceService {

	List<StudyInstance> createStudyInstances(
		CropType crop, int studyId, int datasetId, int locationId,
		Integer numberOfInstancesToGenerate);

	List<StudyInstance> getStudyInstances(int studyId);

	void deleteStudyInstances(Integer studyId, List<Integer> instanceIds);

	Optional<StudyInstance> getStudyInstance(int studyId, Integer instanceId);

	InstanceObservationData addInstanceObservation(InstanceObservationData instanceObservationData);

	InstanceObservationData updateInstanceObservation(InstanceObservationData instanceObservationData);

	Optional<InstanceObservationData> getInstanceObservation(Integer instanceId, final Integer observationDataId, final Integer variableId);

	InstanceDescriptorData addInstanceDescriptorData(InstanceDescriptorData instanceDescriptorData);

	InstanceDescriptorData updateInstanceDescriptorData(InstanceDescriptorData instanceDescriptorData);

	Optional<InstanceDescriptorData> getInstanceDescriptorData(
		Integer instanceId, final Integer descriptorDataId,
		final Integer variableId);

	/**
	 * Returns the Optional<Integer> (datasetId) of dataset to which the studyDbId (nd_geolocation_id) belongs to.
	 * In Brapi, studyDbId is the environment/instance (nd_geolocation_id)
	 *
	 * @param studyDbId
	 * @param datasetTypeEnum
	 * @return
	 */

	Optional<Integer> getDatasetIdForInstanceIdAndDatasetType(Integer instanceId, DatasetTypeEnum datasetTypeEnum);

	void deleteInstanceGeoreferences(Integer instanceId);
}
