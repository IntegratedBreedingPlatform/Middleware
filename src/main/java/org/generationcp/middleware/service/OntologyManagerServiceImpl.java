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

import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.OntologyManagerService;

import java.util.List;

public class OntologyManagerServiceImpl extends Service implements OntologyManagerService {

    public OntologyManagerServiceImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareException {
        return getOntologyBasicDataManager().getAllTraitClass();
    }

    @Override
    public Term getTermById(Integer termId) throws MiddlewareException {
        return getOntologyBasicDataManager().getTermById(termId);
    }

    @Override
    public Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException {
        return getOntologyBasicDataManager().getTermByNameAndCvId(name, cvId);
    }

    @Override
    public List<Term> getTermByCvId(int cvId) throws MiddlewareException {
        return getOntologyBasicDataManager().getTermByCvId(cvId);
    }

    @Override
    public boolean isTermReferred(int termId) throws MiddlewareException {
        return getOntologyBasicDataManager().isTermReferred(termId);
    }

    @Override
    public Method getMethod(int id) throws MiddlewareException {
        return getOntologyMethodDataManager().getMethod(id);
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareException {
        return getOntologyMethodDataManager().getAllMethods();
    }

    @Override
    public void addMethod(Method method) throws MiddlewareException {
        getOntologyMethodDataManager().addMethod(method);
    }

    @Override
    public void updateMethod(Method method) throws MiddlewareException {
        getOntologyMethodDataManager().updateMethod(method);
    }

    @Override
    public void deleteMethod(int id) throws MiddlewareException {
        getOntologyMethodDataManager().deleteMethod(id);
    }

    @Override
    public Property getProperty(int id) throws MiddlewareException {
        return getOntologyPropertyDataManager().getProperty(id);
    }

    @Override
    public List<Property> getAllProperties() throws MiddlewareException {
        return getOntologyPropertyDataManager().getAllProperties();
    }

    @Override
    public List<Property> getAllPropertiesWithClass(String className) throws MiddlewareException {
        return getOntologyPropertyDataManager().getAllPropertiesWithClass(className);
    }

    @Override
    public void addProperty(Property property) throws MiddlewareException {
        getOntologyPropertyDataManager().addProperty(property);
    }

    @Override
    public void updateProperty(Property property) throws MiddlewareException {
        getOntologyPropertyDataManager().updateProperty(property);
    }

    @Override
    public void deleteProperty(Integer propertyId) throws MiddlewareException {
        getOntologyPropertyDataManager().deleteProperty(propertyId);
    }

    @Override
    public Scale getScaleById(int scaleId) throws MiddlewareException {
        return getOntologyScaleDataManager().getScaleById(scaleId);
    }

    @Override
    public List<Scale> getAllScales() throws MiddlewareException {
        return getOntologyScaleDataManager().getAllScales();
    }

    @Override
    public void addScale(Scale scale) throws MiddlewareException {
        getOntologyScaleDataManager().addScale(scale);
    }

    @Override
    public void updateScale(Scale scale) throws MiddlewareException {
        getOntologyScaleDataManager().updateScale(scale);
    }

    @Override
    public void deleteScale(int scaleId) throws MiddlewareException {
        getOntologyScaleDataManager().deleteScale(scaleId);
    }

    @Override
    public List<OntologyVariableSummary> getWithFilter(String programUuid, Boolean favorites, Integer methodId, Integer propertyId, Integer scaleId) throws MiddlewareException {
        return getOntologyVariableDataManager().getWithFilter(programUuid, favorites, methodId, propertyId, scaleId);
    }

    @Override
    public OntologyVariable getVariable(String programUuid, Integer id) throws MiddlewareException {
        return getOntologyVariableDataManager().getVariable(programUuid, id);
    }

    @Override
    public void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {
        getOntologyVariableDataManager().addVariable(variableInfo);
    }

    @Override
    public void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {
        getOntologyVariableDataManager().updateVariable(variableInfo);
    }

    @Override
    public void deleteVariable(Integer id) throws MiddlewareException {
        getOntologyVariableDataManager().deleteVariable(id);
    }

    @Override
    public int getObservationsByVariableId(Integer variableId) throws MiddlewareException {
        return getOntologyBasicDataManager().getVariableObservations(variableId);
    }
}
