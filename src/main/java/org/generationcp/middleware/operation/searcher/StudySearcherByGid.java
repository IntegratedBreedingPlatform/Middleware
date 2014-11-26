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
package org.generationcp.middleware.operation.searcher;

import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class StudySearcherByGid extends Searcher {

	public StudySearcherByGid(HibernateSessionProvider sessionProviderForLocal) { 
		super(sessionProviderForLocal);
	}
	

	public long countStudies(GidStudyQueryFilter filter) throws MiddlewareQueryException {
		int gid = filter.getGid();
		long count = 0;
		if (setWorkingDatabase(gid)) {
			// empty block
		}
		return count;
	}
}
