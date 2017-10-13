
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.SampleList;

import java.util.List;
import java.util.Map;

public interface SampleListService {

	SampleList createSampleList(SampleListDTO sampleListDto);

	/**
	 * Create a sample list folder
	 * Sample List folder name must be unique across the elements in the parent folder
	 * @param folderName
	 * @param parentId
	 * @param createdBy
	 * @return SampleList (Saved Folder)
	 * @throws Exception
	 */
	Integer createSampleListFolder(final String folderName, final Integer parentId,final String createdBy) throws Exception;

	/**
	 * Update sample list folder name
	 * New folder name should be unique across the elements in the parent folder
	 * @param folderId
	 * @param newFolderName
	 * @return SampleList
	 * @throws Exception
	 */
	SampleList updateSampleListFolderName(final Integer folderId, final String newFolderName) throws Exception;

	/**
	 * Move a folder to another folder
	 * sampleListId must exist (could be a folder or a list), newParentFolderId must exist and must be a folder
	 * newParentFolderId folder must not contain another sample list or folder with the name that the one that needs to be moved
	 * @param sampleListId
	 * @param newParentFolderId
	 * @throws Exception
	 */
	SampleList moveSampleList (final Integer sampleListId, final Integer newParentFolderId) throws Exception;

	/**
	 * Delete a folder
	 * Folder ID must exist and it can not contain any child
	 * @param folderId
	 * @throws Exception
	 */
	void deleteSampleListFolder(final Integer folderId) throws Exception;

	/**
	 * Returns the Top Level sample List Folders present in the program of the specified database. Retrieval from the database is done by
	 * batch (as specified in batchSize) to reduce the load in instances where there is a large volume of top level folders to be retrieved.
	 * Though retrieval is by batch, this method still returns all of the top level folders as a single list.
	 *
	 * @param programUUID - the program UUID
	 * @return - List of SampleList POJOs
	 */
	List<SampleList> getAllTopLevelLists(String programUUID);

	/**
	 * Returns a list of {@code SampleList} child records given a parent id. Retrieval from the database is done by batch (as specified
	 * in batchSize) to reduce the load in instances where there is a large volume of child folders to be retrieved. Though retrieval is by
	 * batch, this method still returns all of the child folders as a single list.
	 *
	 * @param parentId - the ID of the parent to retrieve the child lists
	 * @param programUUID - the program UUID of the program where to retrieve the child lists
	 * @param batchSize - the number of records to be retrieved per iteration
	 * @return Returns a List of SampleList POJOs for the child lists
	 */
	List<SampleList> getSampleListByParentFolderIdBatched(Integer parentId, String programUUID, int batchSize);

	/**
	 * Retrieves number of children in one go for lists ids provide. Note non folder list ids are filtered out.
	 * This helps avoiding the need to query metadata in a loop for each folder
	 * @param sampleLists ids for which we should retrieve metadata
	 */
	Map<Integer, GermplasmFolderMetadata> getFolderMetadata(List<SampleList> sampleLists);

}
