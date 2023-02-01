
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.api.study.StudyDTO;
import org.generationcp.middleware.api.study.StudyDetailsDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.api.study.StudySearchResponse;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudyService {

	boolean hasCrossesOrSelections(int studyId);

	String getProgramUUID(Integer studyIdentifier);

	/**
	 * @param ids     of the variables that i need to check data
	 * @param studyId id for the study (Nursery / Trial)
	 * @return the true if any id have data on the study
	 */
	boolean hasMeasurementDataEntered(List<Integer> ids, int studyId);

	Map<Integer, String> getGenericGermplasmDescriptors(int studyIdentifier);

	Map<Integer, String> getAdditionalDesignFactors(int studyIdentifier);

	Integer getPlotDatasetId(int studyId);

	Integer getEnvironmentDatasetId(int studyId);

	boolean studyHasGivenDatasetType(Integer studyId, Integer datasetTypeId);

	List<GermplasmStudyDto> getGermplasmStudies(Integer gid);

	@Deprecated
	List<StudyDTO> getFilteredStudies(String programUUID, StudySearchRequest studySearchRequest, Pageable pageable);

	@Deprecated
	long countFilteredStudies(String programUUID, StudySearchRequest studySearchRequest);

	/**
	 * Soft-delete all program studies
	 *
	 * @param programUUID Program UUID of the studies to be deleted
	 */
	void deleteProgramStudies(String programUUID);

	void deleteStudy(int studyId);

	long countStudiesByGids(List<Integer> gids);

	long countPlotsByGids(List<Integer> gids);

	boolean isLocationUsedInStudy(Integer locationId);

	void deleteNameTypeFromStudies(Integer nameTypeId);

	List<StudySearchResponse> searchStudies(String programUUID, StudySearchRequest studySearchRequest, Pageable pageable);

	long countSearchStudies(String programUUID, StudySearchRequest studySearchRequest);

	Optional<FolderReference> getFolderByParentAndName(Integer parentId, String folderName, String programUUID);

	StudyDetailsDTO getStudyDetails(final String programUUID, Integer studyId);

}
