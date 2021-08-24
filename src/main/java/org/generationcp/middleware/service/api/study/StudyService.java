
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.api.study.StudyDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyService {

	boolean hasCrossesOrSelections(int studyId);

	String getProgramUUID(Integer studyIdentifier);

	/**
	 * @param ids     of the variables that i need to check data
	 * @param studyId id for the study (Nursery / Trial)
	 * @return the true if any id have data on the study
	 */
	boolean hasMeasurementDataEntered(List<Integer> ids, int studyId);

	List<String> getGenericGermplasmDescriptors(int studyIdentifier);

	List<String> getAdditionalDesignFactors(int studyIdentifier);

	Integer getPlotDatasetId(int studyId);

	Integer getEnvironmentDatasetId(int studyId);

	boolean studyHasGivenDatasetType(Integer studyId, Integer datasetTypeId);

	List<GermplasmStudyDto> getGermplasmStudies(Integer gid);

	List<StudyDTO> getFilteredStudies(String programUUID, StudySearchRequest studySearchRequest, Pageable pageable);

	long countFilteredStudies(String programUUID, StudySearchRequest studySearchRequest);
	
}
