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

package org.generationcp.middleware.domain.search;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyResultSetByParentFolder extends Searcher implements StudyResultSet {

	private final ParentFolderStudyQueryFilter filter;
	private final int numOfRows;
	private final long size;
	private long count;
	private final List<StudyReference> studyReferences = new ArrayList<StudyReference>();
	private int startIndex;
	private int index;

	public StudyResultSetByParentFolder(ParentFolderStudyQueryFilter filter, int numOfRows, HibernateSessionProvider sessionProviderForLocal)
			throws MiddlewareQueryException {

		super(sessionProviderForLocal);
		this.filter = filter;
		this.numOfRows = numOfRows;
		this.size = this.countStudies();
		this.count = 0;
		this.startIndex = 0;
	}

	@Override
	public boolean hasMore() {
		return this.count < this.size;
	}

	@Override
	public StudyReference next() throws MiddlewareQueryException {
		this.count++;
		if (this.isEmpty(this.studyReferences)) {
			this.getNextStudies();
		}
		return this.studyReferences.get(this.index++);
	}

	private boolean isEmpty(List<StudyReference> studyReferences) {
		return studyReferences == null || studyReferences.size() == 0 || this.index == this.numOfRows;
	}

	@Override
	public long size() {
		return this.size;
	}

	private void getNextStudies() throws MiddlewareQueryException {
		this.index = 0;
		int folderId = this.filter.getFolderId();
		this.studyReferences.clear();
		List<DmsProject> projects = this.getDmsProjectDao().getProjectsByFolder(folderId, this.startIndex, this.numOfRows);
		for (DmsProject project : projects) {
			this.studyReferences.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
		}
		this.startIndex += this.numOfRows;
	}

	private long countStudies() throws MiddlewareQueryException {
		int folderId = this.filter.getFolderId();
		long count = 0;
		count = this.getDmsProjectDao().countProjectsByFolder(folderId);
		return count;
	}
}
