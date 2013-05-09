package org.generationcp.middleware.v2.search;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.StudyReference;

public interface StudyResultSet {

	boolean hasMore();
	
	StudyReference next() throws MiddlewareQueryException;
	
	long size();
}
