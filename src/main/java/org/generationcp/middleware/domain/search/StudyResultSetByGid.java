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
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.operation.searcher.Searcher;

import java.util.List;

//TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
public class StudyResultSetByGid extends Searcher implements StudyResultSet {

	private int gid;
	private int numOfRows;
	
	private long countOfLocalStudies;
	private long countOfCentralStudies;
	
	private int currentRow;
	
	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByGid(GidStudyQueryFilter filter, int numOfRows, HibernateSessionProvider sessionProviderForLocal)
			throws MiddlewareQueryException {

		super(sessionProviderForLocal);
		
		this.gid = filter.getGid();
		this.numOfRows = numOfRows;
		
		this.countOfLocalStudies = countStudies(Database.LOCAL, gid);
		this.countOfCentralStudies = countStudies(Database.CENTRAL, gid);
		
		this.currentRow = 0;
		this.bufIndex = 0;
	}
	
	private long countStudies(Database database, int gid) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			return this.getStockDao().countStudiesByGid(gid);
		}
		return 0;
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
		if (currentRow < this.countOfLocalStudies) {
			fillBufferFromLocalStudies();
		}
		else {
			fillBufferFromCentralStudies();
		}
	}

	private void fillBufferFromLocalStudies() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.LOCAL)) {
			int start = currentRow - (int) countOfLocalStudies;
			buffer = this.getStockDao().getStudiesByGid(gid, start, numOfRows);
			bufIndex = 0;
		}
	}

	private void fillBufferFromCentralStudies() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.CENTRAL)) {
			int start = currentRow - (int) countOfLocalStudies;
			buffer = this.getStockDao().getStudiesByGid(gid, start, numOfRows);
			bufIndex = 0;
		}
	}

	@Override
	public long size() {
		return this.countOfLocalStudies +
			   this.countOfCentralStudies;
	}
}
