
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyService {

	boolean hasCrossesOrSelections(int studyId);

	String getProgramUUID(Integer studyIdentifier);

	TrialObservationTable getTrialObservationTable(int studyIdentifier);

	/**
	 * @param studyIdentifier id for the study (Nursery / Trial)
	 * @param instanceDbId    id for a Trial instance of a Trial (Nursery has 1 instance). If present studyIdentifier will not be used
	 * @return
	 */
	TrialObservationTable getTrialObservationTable(int studyIdentifier, Integer instanceDbId);

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

	List<StudySummary> getStudies(StudySearchFilter studySearchFilter, Pageable pageable);

	long countStudies(StudySearchFilter studySearchFilter);

	boolean studyHasGivenDatasetType(Integer studyId, Integer datasetTypeId);

	List<GermplasmStudyDto> getGermplasmStudies(Integer gid);

	List<StudySummary> saveStudies(String crop, List<TrialImportRequestDTO> trialImportRequestDtoList, Integer userId);
}
