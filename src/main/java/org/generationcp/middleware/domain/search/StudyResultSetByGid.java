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

import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.operation.searcher.Searcher;

public class StudyResultSetByGid extends Searcher implements StudyResultSet {

	private int gid;
	private int numOfRows;
	
	private long countOfLocalStudiesViaStudy;
	private long countOfLocalStudiesViaPlot;
	private long countOfCentralStudiesViaStudy;
	private long countOfCentralStudiesViaPlot;
	
	private int currentRow;
	
	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByGid(GidStudyQueryFilter filter, int numOfRows,
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral)
			throws MiddlewareQueryException {

		super(sessionProviderForLocal, sessionProviderForCentral);
		
		this.gid = filter.getGid();
		this.numOfRows = numOfRows;
		
		this.countOfLocalStudiesViaStudy = countStudiesViaStudy(Database.LOCAL, gid);
		this.countOfLocalStudiesViaPlot = countStudiesViaPlot(Database.LOCAL, gid);
		this.countOfCentralStudiesViaStudy = countStudiesViaStudy(Database.CENTRAL, gid);
		this.countOfCentralStudiesViaPlot = countStudiesViaPlot(Database.CENTRAL, gid);
		
		this.currentRow = 0;
		this.bufIndex = 0;
	}

	private long countStudiesViaStudy(Database database, int gid) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			return this.getStockDao().countStudiesByGidViaStudy(gid);
		}
		return 0;
	}

	private long countStudiesViaPlot(Database database, int gid) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			return this.getStockDao().countStudiesByGidViaPlot(gid);
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
		if (currentRow < this.countOfLocalStudiesViaStudy) {
			fillBufferFromLocalStudiesViaStudy();
		}
		else if (currentRow < this.countOfLocalStudiesViaStudy + this.countOfLocalStudiesViaPlot) {
			fillBufferFromLocalStudiesViaPlot();
		}
		else if (currentRow < this.countOfLocalStudiesViaStudy + this.countOfLocalStudiesViaPlot + this.countOfCentralStudiesViaStudy) {
			fillBufferFromCentralStudiesViaStudy();
		}
		else {
			fillBufferFromCentralStudiesViaPlot();
		}
	}

	private void fillBufferFromLocalStudiesViaStudy() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.LOCAL)) {
			buffer = this.getStockDao().getStudiesByGidViaStudy(gid, currentRow, numOfRows);
			bufIndex = 0;
		}
	}

	private void fillBufferFromLocalStudiesViaPlot() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.LOCAL)) {
			int start = currentRow - (int) countOfLocalStudiesViaStudy;
			buffer = this.getStockDao().getStudiesByGidViaPlot(gid, start, numOfRows);
			bufIndex = 0;
		}
	}

	private void fillBufferFromCentralStudiesViaStudy() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.CENTRAL)) {
			int start = currentRow - (int) countOfLocalStudiesViaStudy - (int) this.countOfLocalStudiesViaPlot;
			buffer = this.getStockDao().getStudiesByGidViaStudy(gid, start, numOfRows);
			bufIndex = 0;
		}
	}

	private void fillBufferFromCentralStudiesViaPlot() throws MiddlewareQueryException {
		if (this.setWorkingDatabase(Database.CENTRAL)) {
			int start = currentRow - (int) countOfLocalStudiesViaStudy - (int) this.countOfLocalStudiesViaPlot - (int) this.countOfCentralStudiesViaStudy;
			buffer = this.getStockDao().getStudiesByGidViaPlot(gid, start, numOfRows);
			bufIndex = 0;
		}
	}

	@Override
	public long size() {
		return this.countOfLocalStudiesViaStudy +
			   this.countOfLocalStudiesViaPlot +
			   this.countOfCentralStudiesViaStudy +
			   this.countOfCentralStudiesViaPlot;
	}
}
