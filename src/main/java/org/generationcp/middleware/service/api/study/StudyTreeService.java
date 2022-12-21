package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.dms.DmsProject;

public interface StudyTreeService {

	Integer createStudyTreeFolder(int parentId, String folderName, String programUUID);

	Integer updateStudyTreeFolder(int parentId, String newFolderName);

	void deleteStudyFolder(Integer folderId);

	Integer moveStudyFolder(int folderId, int newParentFolderId);

}
