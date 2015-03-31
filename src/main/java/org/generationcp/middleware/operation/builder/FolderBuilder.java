/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;


public class FolderBuilder extends Builder{

    public FolderBuilder(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);
    }

    public List<FolderReference> buildFolderTree() throws MiddlewareQueryException {
        List<FolderReference> folderTree = new ArrayList<FolderReference>();
        List<FolderReference> allFolders = getDmsProjectDao().getAllFolders();
        folderTree = buildTree(allFolders, DmsProject.SYSTEM_FOLDER_ID);
        return folderTree;
    }
    
    private List<FolderReference> buildTree(List<FolderReference> allFolders, Integer parentId) {
        List<FolderReference> folderTree = new ArrayList<FolderReference>();

        for (FolderReference aFolder : allFolders) {
            if (aFolder.getParentFolderId() != null && aFolder.getParentFolderId().equals(parentId)) {
                folderTree.add(aFolder);
            }
        }
        
        if (!folderTree.isEmpty()) {
            for (FolderReference aChildFolder : folderTree) {
                aChildFolder.setSubFolders(buildTree(allFolders, aChildFolder.getId()));
            }
        }
        
        return folderTree;
    }
}
