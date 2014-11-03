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
package org.generationcp.middleware.domain.search;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.dms.DmsProject;

import java.util.ArrayList;
import java.util.List;

public class StudyResultSetByParentFolder extends Searcher implements StudyResultSet {
	
	private ParentFolderStudyQueryFilter filter;
	private int numOfRows;
	private long size;
	private long count;
	private List<StudyReference> studyReferences = new ArrayList<StudyReference>();
	private int startIndex;
	private int index;

	public StudyResultSetByParentFolder(ParentFolderStudyQueryFilter filter, int numOfRows,
			                            HibernateSessionProvider sessionProviderForLocal,
			                            HibernateSessionProvider sessionProviderForCentral) throws MiddlewareQueryException {
		
		super(sessionProviderForLocal, sessionProviderForCentral);
		this.filter = filter;
		this.numOfRows = numOfRows;
		this.size = countStudies();
		this.count = 0;
		this.startIndex = 0;
	}
	
	@Override
	public boolean hasMore() {
		return count < size;
	}

	@Override
	public StudyReference next() throws MiddlewareQueryException {
		count++;
		if (isEmpty(studyReferences)) {
			getNextStudies();
		}
	    return studyReferences.get(index++);
	}

	private boolean isEmpty(List<StudyReference> studyReferences) {
		return studyReferences == null || studyReferences.size() == 0 || index == numOfRows;
	}

	@Override
	public long size() {
		return size;
	}
	
	private void getNextStudies() throws MiddlewareQueryException {
		index = 0;
		int folderId = filter.getFolderId();
		studyReferences.clear();
		if (setWorkingDatabase(folderId)) {
			List<DmsProject> projects = (List<DmsProject>) getDmsProjectDao().getProjectsByFolder(folderId, startIndex, numOfRows);
			for (DmsProject project : projects) {
				studyReferences.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
			}
		}
		startIndex += numOfRows;
	}
	
	private long countStudies() throws MiddlewareQueryException {
		int folderId = filter.getFolderId();
		long count = 0;
		if (setWorkingDatabase(folderId)) {
			count = getDmsProjectDao().countProjectsByFolder(folderId);
		}
		return count;
	}
}
