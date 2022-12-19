package org.generationcp.middleware.service.api.study;

public interface StudyTreeService {

	Integer createStudyTreeFolder(int parentId, String folderName, String programUUID);

	Integer updateStudyTreeFolder(int parentId, String newFolderName);

	void deleteStudyFolder(Integer folderId);

}
