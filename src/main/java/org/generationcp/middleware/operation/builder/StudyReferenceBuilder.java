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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyReferenceBuilder extends Builder {

	public StudyReferenceBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<StudyReference> build(final Collection<DmsProject> projects) {
		final List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		if (projects != null && !projects.isEmpty()) {
			for (final DmsProject project : projects) {
				studyReferences.add(new StudyReference(project.getProjectId(), project.getName(), project.getDescription()));
			}
		}
		return studyReferences;
	}
}
