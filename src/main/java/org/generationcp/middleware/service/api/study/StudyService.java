
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;

import java.util.List;
import java.util.Set;

public interface StudyService {

	List<StudySummary> search(final StudySearchParameters serchParameters);

	boolean hasMeasurementDataOnEnvironment(final int studyIdentifier, final int instanceId);

	boolean hasCrossesOrSelections(final int studyId);

	int countTotalObservationUnits(final int studyIdentifier, final int instanceId);

	List<ObservationDto> getObservations(final int studyIdentifier, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	String getProgramUUID(Integer studyIdentifier);

	TrialObservationTable getTrialObservationTable(int studyIdentifier);

	/**
	 * @param studyIdentifier id for the study (Nursery / Trial)
	 * @param instanceDbId    id for a Trial instance of a Trial (Nursery has 1 instance). If present studyIdentifier will not be used
	 * @return
	 */
	TrialObservationTable getTrialObservationTable(int studyIdentifier, Integer instanceDbId);

	/**
	 * @param geolocationId
	 * @return StudyDetailsDto
	 */
	StudyDetailsDto getStudyDetailsByInstance(Integer geolocationId);

	/**
	 * @param ids     of the variables that i need to check data
	 * @param studyId id for the study (Nursery / Trial)
	 * @return the true if any id have data on the study
	 */
	boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId);

	/**
	 * Retrieves Phenotypes given certain search parameters
	 * specified in https://brapi.docs.apiary.io/#reference/phenotypes/phenotype-search V1.1
	 *
	 * @param pageSize
	 * @param pageNumber
	 * @param requestDTO
	 * @return List of phenotypes
	 */
	List<PhenotypeSearchDTO> searchPhenotypes(final Integer pageSize, final Integer pageNumber, final PhenotypeSearchRequestDTO requestDTO);

	/**
	 * Retrieves a count of how many phenotypes match with the search parameters
	 *
	 * @param requestDTO
	 * @return Number of phenotypes
	 */
	long countPhenotypes(final PhenotypeSearchRequestDTO requestDTO);

	List<String> getGenericGermplasmDescriptors(final int studyIdentifier);

	List<String> getAdditionalDesignFactors(final int studyIdentifier);

	Integer getPlotDatasetId(final int studyId);

	Integer getEnvironmentDatasetId(final int studyId);

	List<StudyDto> getStudies(StudySearchFilter studySearchFilter);

	long countStudies(StudySearchFilter studySearchFilter);

	boolean studyHasGivenDatasetType(Integer studyId, Integer datasetTypeId);
}
