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
package org.generationcp.middleware.v2.domain.searcher;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.search.filter.ParentFolderStudyQueryFilter;

public class StudySearcherByParentFolder extends Searcher {

	public StudySearcherByParentFolder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) { 
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public List<StudyReference> searchStudies(ParentFolderStudyQueryFilter filter, int start, int numOfRows) throws MiddlewareQueryException {
		int folderId = filter.getFolderId();
		List<StudyReference> studyRefs = new ArrayList<StudyReference>();
		if (setWorkingDatabase(folderId)) {
			List<DmsProject> projects = (List<DmsProject>) getDmsProjectDao().getProjectsByFolder(folderId, start, numOfRows);
			for (DmsProject project : projects) {
				studyRefs.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
			}
		}
		return studyRefs;
	}
	
	public long countStudies(ParentFolderStudyQueryFilter filter) throws MiddlewareQueryException {
		int folderId = filter.getFolderId();
		long count = 0;
		if (setWorkingDatabase(folderId)) {
			count = getDmsProjectDao().countProjectsByFolder(folderId);
		}
		return count;
	}
}
