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

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class MethodBuilder extends Builder {

	private static final int METHOD_CV_ID = 1020;

	private DaoFactory daoFactory;

	public MethodBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public Term findMethodById(int id) throws MiddlewareQueryException {
		Term term = this.getTermBuilder().get(id);
		if (term != null) {
			if (term.getVocabularyId() != MethodBuilder.METHOD_CV_ID) {
				term = null;
			}
		}
		return term;
	}

	public Term findMethodByName(String name) throws MiddlewareQueryException {
		return this.mapToTerm(daoFactory.getCvTermDao().getByNameAndCvId(name, MethodBuilder.METHOD_CV_ID));
	}

	private Term mapToTerm(CVTerm cvTerm) {
		Term term = null;
		if (cvTerm != null) {
			term = new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
			term.setObsolete(cvTerm.isObsolete());
			term.setVocabularyId(cvTerm.getCv());
		}
		return term;
	}
}
