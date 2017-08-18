
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.samplelist.SampleListDTO;

public interface SampleListService {

	Integer createOrUpdateSampleList(SampleListDTO sampleListDto);

	/**
	 * Create a sample list folder
	 * Sample List folder name must be unique across the elements in the parent folder
	 * @param folderName
	 * @param parentId
	 * @return Folder ID
	 * @throws Exception
	 */
	Integer createSampleListFolder(final String folderName, final Integer parentId) throws Exception;

	/**
	 * Update sample list folder name
	 * New folder name should be unique across the elements in the parent folder
	 * @param folderId
	 * @param newFolderName
	 * @throws Exception
	 */
	void updateSampleListFolderName(final Integer folderId, final String newFolderName) throws Exception;

	/**
	 * Move a folder to another folder
	 * FolderID must exist, newParentID must exist
	 * newParentID folder must not contain another sample list with the name that the one that needs to be moved
	 * @param folderId
	 * @param newParentId
	 * @throws Exception
	 */
	void moveSampleListFolder(final Integer folderId, final Integer newParentId) throws Exception;

	/**
	 * Delete a folder
	 * Folder ID must exist and it can not contain any child
	 * @param folderId
	 * @throws Exception
	 */
	void deleteSampleListFolder(final Integer folderId) throws Exception;

}
