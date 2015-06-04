/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.searcher;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudySearcherByParentFolder extends Searcher {

	public StudySearcherByParentFolder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<StudyReference> searchStudies(ParentFolderStudyQueryFilter filter, int start, int numOfRows)
			throws MiddlewareQueryException {
		int folderId = filter.getFolderId();
		List<StudyReference> studyRefs = new ArrayList<StudyReference>();
		List<DmsProject> projects = this.getDmsProjectDao().getProjectsByFolder(folderId, start, numOfRows);
		for (DmsProject project : projects) {
			studyRefs.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
		}
		return studyRefs;
	}

	public long countStudies(ParentFolderStudyQueryFilter filter) throws MiddlewareQueryException {
		int folderId = filter.getFolderId();
		long count = 0;
		count = this.getDmsProjectDao().countProjectsByFolder(folderId);
		return count;
	}
}
