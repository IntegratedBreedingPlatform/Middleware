package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.service.impl.study.SamplePlateInfo;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SampleListService {

	SampleList createSampleList(final SampleListDTO sampleListDto);

	/**
	 * Create a sample list folder
	 * Sample List folder name must be unique across the elements in the parent folder
	 *
	 * @param folderName
	 * @param parentId
	 * @param username
	 * @param programUUID
	 * @return SampleList (Saved Folder)
	 */
	Integer createSampleListFolder(final String folderName, final Integer parentId, final String username, final String programUUID);

	/**
	 * Update sample list folder name
	 * New folder name should be unique across the elements in the parent folder
	 *
	 * @param folderId
	 * @param newFolderName
	 * @return SampleList
	 */
	SampleList updateSampleListFolderName(final Integer folderId, final String newFolderName);

	/**
	 * Move a folder to another folder
	 * sampleListId must exist (could be a folder or a list), newParentFolderId must exist and must be a folder
	 * newParentFolderId folder must not contain another sample list or folder with the name that the one that needs to be moved
	 * isCropList set to true if the list will be moved to the crop list folder.
	 *
	 * @param sampleListId
	 * @param newParentFolderId
	 * @param isCropList
	 * @param programUUID
	 */
	SampleList moveSampleList(final Integer sampleListId, final Integer newParentFolderId, final boolean isCropList,
			final String programUUID);

	/**
	 * Delete a folder
	 * Folder ID must exist and it can not contain any child
	 *
	 * @param folderId
	 */
	void deleteSampleListFolder(final Integer folderId);

	long countSamplesByUIDs(Set<String> sampleUIDs, Integer listId);

	long countSamplesByDatasetId(Integer datasetId);

	List<SampleListDTO> getSampleLists(final List<Integer> datasetIds);

	SampleList getSampleList(final Integer sampleListId);

	List<SampleDetailsDTO> getSampleDetailsDTOs(final Integer sampleListId);

	/**
	 * Returns a list of {@code SampleList} child records given a parent id. Retrieval from the database is done by batch (as specified
	 * in batchSize) to reduce the load in instances where there is a large volume of child folders to be retrieved. Though retrieval is by
	 * batch, this method still returns all of the child folders as a single list.
	 *
	 * @param parentId    - the ID of the parent to retrieve the child lists
	 * @param programUUID - the program UUID of the program where to retrieve the child lists
	 * @param batchSize   - the number of records to be retrieved per iteration
	 * @return Returns a List of SampleList POJOs for the child lists
	 */
	List<SampleList> getSampleListByParentFolderIdBatched(final Integer parentId, final String programUUID, final int batchSize);

	/**
	 * Retrieves number of children in one go for lists ids provide. Note non folder list ids are filtered out.
	 * This helps avoiding the need to query metadata in a loop for each folder
	 *
	 * @param sampleLists ids for which we should retrieve metadata
	 */
	Map<Integer, ListMetadata> getListMetadata(final List<SampleList> sampleLists);

	SampleList getLastSavedSampleListByUserId(final Integer userId, final String programUuid);

	/**
	 * Returns the Top Level sample List Folders present in the program of the specified database. Retrieval from the database is done by
	 * batch (as specified in batchSize) to reduce the load in instances where there is a large volume of top level folders to be retrieved.
	 * Though retrieval is by batch, this method still returns all of the top level folders as a single list.
	 *
	 * @param programUUID - the program UUID
	 * @return - List of GermplasmList POJOs
	 */
	List<SampleList> getAllSampleTopLevelLists(final String programUUID);

	/**
	 * Returns sample lists that matches the specified search term.
	 * <p>
	 * The search results will show lists in which:
	 * <p>
	 * The list name contains the search term
	 * The list contains samples with names that contain the search term
	 * The list contains samples with Sample UIDs that contain the search term
	 * <p>
	 * If exactMatch is True, this will return results that match the search term exactly
	 *
	 * @param searchString
	 * @param exactMatch
	 * @param programUUID
	 * @return
	 */
	List<SampleList> searchSampleLists(final String searchString, final boolean exactMatch, final String programUUID, final Pageable pageable);

	void updateSamplePlateInfo(Integer sampleListId, Map<String, SamplePlateInfo> plateInfoMap);

	String getObservationVariableName(Integer sampleListId);

	List<Sample> getSampleListEntries(Integer sampleListId, List<Integer> sampleIds);

	void deleteSampleListEntries(Integer sampleListId, List<Integer> selectedEntries);
}
