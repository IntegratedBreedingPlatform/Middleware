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
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StudyReferenceBuilder extends Builder {

	public StudyReferenceBuilder(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<StudyReference> build(Collection<DmsProject> projects) {
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		if (projects != null && projects.size() > 0) {
			for (DmsProject project : projects) {
				studyReferences.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
			}
		}
		return studyReferences;
	}

	public List<StudyReference> getStudiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		return getDmsProjectDao().getStudiesByTrialEnvironments(environmentIds);
	}
}
