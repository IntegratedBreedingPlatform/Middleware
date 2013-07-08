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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.Study;
import org.generationcp.middleware.domain.StudyReference;
import org.generationcp.middleware.domain.TermId;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudySearcherByGid extends Searcher {

	public StudySearcherByGid(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) { 
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public List<StudyReference> searchStudies(GidStudyQueryFilter filter, int start, int numOfRows) throws MiddlewareQueryException {
		int gid = filter.getGid();
		List<StudyReference> studyRefs = new ArrayList<StudyReference>();
		
			Set<Study> studies = new HashSet<Study>();
			List<DmsProject> projects = getProjectSearcher().searchStudiesByFactor(TermId.GID.getId(), Integer.toString(gid));
			for (DmsProject project : projects)	 {
				//studies.add(getStudyBuilder().createStudy(project));
			}
		
		return studyRefs;
	}
	
	public long countStudies(GidStudyQueryFilter filter) throws MiddlewareQueryException {
		int gid = filter.getGid();
		long count = 0;
		if (setWorkingDatabase(gid)) {
			//count = getDmsProjectDao().countProjectsByFolder(folderId);
		}
		return count;
	}
}
