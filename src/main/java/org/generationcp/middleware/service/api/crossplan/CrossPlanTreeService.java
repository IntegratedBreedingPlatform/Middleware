package org.generationcp.middleware.service.api.crossplan;

import org.generationcp.middleware.domain.dms.Reference;

import java.util.List;

public interface CrossPlanTreeService {

    Integer createCrossPlanTreeFolder(int parentId, String folderName, String programUUID);

    Integer updateCrossPlanTreeFolder(int parentId, String newFolderName);

    void deleteCrossPlanFolder(Integer folderId);

    Integer moveCrossPlanNode(int itemId, int newParentFolderId);

    List<Reference> getChildrenOfFolder(int folderId, String programUUID);
}
