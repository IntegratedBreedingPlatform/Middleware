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
package org.generationcp.middleware.v2.search;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.StudyReference;

public interface StudyResultSet {

	boolean hasMore();
	
	StudyReference next() throws MiddlewareQueryException;
	
	long size();
}
