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

package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class CvTermSaver extends Saver {

	private DaoFactory daoFactory;

	public CvTermSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public Term save(String name, String definition, CvId cvId) throws MiddlewareException, MiddlewareQueryException {
		this.validateInputFields(name, definition);
		CVTermDao dao = daoFactory.getCvTermDao();
		CVTerm cvTerm = this.create(name, definition, cvId.getId(), false, false, false);
		dao.save(cvTerm);

		return new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
	}

	public Term update(Term term) throws MiddlewareException, MiddlewareQueryException {
		this.validateInputFields(term.getName(), term.getDefinition());
		CVTermDao dao = daoFactory.getCvTermDao();

		CVTerm cvTerm = dao.getById(term.getId());

		if (cvTerm != null) { // update
			cvTerm.setName(term.getName());
			cvTerm.setDefinition(term.getDefinition());
			dao.update(cvTerm);
		} else {
			throw new MiddlewareException("Error: Term not found in the local database. ");
		}
		return new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
	}

	public CVTerm create(String name, String definition, int cvId, boolean isObsolete, boolean isRelationshipType, boolean isSystem) {
		CVTerm cvTerm = new CVTerm();
		cvTerm.setName(name);
		cvTerm.setDefinition(definition);
		cvTerm.setCv(cvId);
		cvTerm.setIsObsolete(isObsolete);
		cvTerm.setIsRelationshipType(isRelationshipType);
		cvTerm.setIsSystem(isSystem);
		return cvTerm;
	}

	private void validateInputFields(String name, String definition) throws MiddlewareException {
		StringBuffer errorMessage = new StringBuffer("");
		if (name == null || name.equals("")) {
			errorMessage.append("\nname is null or empty");
		}
		if (definition == null || definition.equals("")) {
			errorMessage.append("\ndefinition is null or empty");
		}
		if (errorMessage.length() > 0) {
			throw new MiddlewareException(errorMessage.toString());
		}
	}

	public void delete(CVTerm cvTerm, CvId cvId) throws MiddlewareQueryException {
		CVTermDao dao = daoFactory.getCvTermDao();
		try {
			dao.makeTransient(cvTerm);
		} catch (MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
}
