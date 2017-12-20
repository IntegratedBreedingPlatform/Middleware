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

import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.searcher.Searcher;

public class StudyResultSetByGid extends Searcher implements StudyResultSet {

	private final int gid;
	private final int numOfRows;

	private final long countOfStudies;

	private int currentRow;

	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByGid(GidStudyQueryFilter filter, int numOfRows, HibernateSessionProvider sessionProvider) {

		super(sessionProvider);

		this.gid = filter.getGid();
		this.numOfRows = numOfRows;

		this.countOfStudies = this.countStudies(this.gid);

		this.currentRow = 0;
		this.bufIndex = 0;
	}

	private long countStudies(int gid) {
		return this.getStockDao().countStudiesByGid(gid);
	}

	@Override
	public boolean hasMore() {
		return this.currentRow < this.size();
	}

	@Override
	public StudyReference next() {
		if (this.isEmptyBuffer()) {
			this.fillBuffer();
		}
		this.currentRow++;
		return this.buffer.get(this.bufIndex++);
	}

	private boolean isEmptyBuffer() {
		return this.buffer == null || this.bufIndex >= this.buffer.size();
	}

	private void fillBuffer() {
		int start = this.currentRow - (int) this.countOfStudies;
		this.buffer = this.getStockDao().getStudiesByGid(this.gid, start, this.numOfRows);
		this.bufIndex = 0;
	}

	@Override
	public long size() {
		return this.countOfStudies;
	}
}
