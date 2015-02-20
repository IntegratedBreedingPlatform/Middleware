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
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.searcher.Searcher;

import java.util.List;

public class StudyResultSetByGid extends Searcher implements StudyResultSet {

	private int gid;
	private int numOfRows;
	
	private long countOfStudies;
	
	private int currentRow;
	
	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByGid(GidStudyQueryFilter filter, int numOfRows, HibernateSessionProvider sessionProvider)
			throws MiddlewareQueryException {

		super(sessionProvider);
		
		this.gid = filter.getGid();
		this.numOfRows = numOfRows;
		
		this.countOfStudies = countStudies(gid);
		
		this.currentRow = 0;
		this.bufIndex = 0;
	}
	
	private long countStudies(int gid) throws MiddlewareQueryException {
		return this.getStockDao().countStudiesByGid(gid);
	}

	@Override
	public boolean hasMore() {
		return currentRow < size();
	}

	@Override
	public StudyReference next() throws MiddlewareQueryException {
		if (isEmptyBuffer()) {
			fillBuffer();
		}
		currentRow++;
		return buffer.get(bufIndex++);
	}

	private boolean isEmptyBuffer() {
		return buffer == null || bufIndex >= buffer.size();
	}

	private void fillBuffer() throws MiddlewareQueryException {
		int start = currentRow - (int) countOfStudies;
		buffer = this.getStockDao().getStudiesByGid(gid, start, numOfRows);
		bufIndex = 0;
	}

	@Override
	public long size() {
		return this.countOfStudies;
	}
}
