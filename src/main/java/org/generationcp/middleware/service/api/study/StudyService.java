
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;

import java.util.List;
import java.util.Map;

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
}
