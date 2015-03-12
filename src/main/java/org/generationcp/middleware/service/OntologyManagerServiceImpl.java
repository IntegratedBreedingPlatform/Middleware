/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.service;

import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.OntologyManagerService;

import java.util.List;

public class OntologyManagerServiceImpl extends Service implements OntologyManagerService {

    public OntologyManagerServiceImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareQueryException {
        return getOntologyBasicDataManager().getAllTraitClass();
    }

    @Override
    public List<Term> getDataTypes() throws MiddlewareQueryException {
        return getOntologyBasicDataManager().getDataTypes();
    }

    @Override
    public Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareQueryException {
        return getOntologyBasicDataManager().getTermByNameAndCvId(name, cvId);
    }

    @Override
    public Method getMethod(int id) throws MiddlewareQueryException {
        return getOntologyMethodDataManager().getMethod(id);
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        return getOntologyMethodDataManager().getAllMethods();
    }

    @Override
    public void addMethod(Method method) throws MiddlewareQueryException {
        getOntologyMethodDataManager().addMethod(method);
    }

    @Override
    public void updateMethod(Method method) throws MiddlewareQueryException, MiddlewareException {
        getOntologyMethodDataManager().updateMethod(method);
    }

    @Override
    public void deleteMethod(int id) throws MiddlewareQueryException {
        getOntologyMethodDataManager().deleteMethod(id);
    }
}
