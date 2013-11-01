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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

public class TermPropertyBuilder extends Builder {

	public TermPropertyBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public TermProperty get(int termPropertyId) throws MiddlewareQueryException {
	    TermProperty term = null;
		if (setWorkingDatabase(termPropertyId)) {
			term = create(getCvTermPropertyDao().getById(termPropertyId));
		}
		return term;
	}
	
	public TermProperty create(CVTermProperty cVTermProperty){
		TermProperty termProperty = null;
		if (cVTermProperty != null){
			termProperty = new TermProperty(cVTermProperty.getCvTermPropertyId(), cVTermProperty.getTypeId(), cVTermProperty.getValue(),
			        cVTermProperty.getRank());
		}
		return termProperty;
	}
	
	public List<TermProperty> create(List<CVTermProperty> cvTermProperties) {
	    List<TermProperty> properties = new ArrayList<TermProperty>();
	    
	    if (cvTermProperties != null && !cvTermProperties.isEmpty()) {
	        for (CVTermProperty cvTermProperty : cvTermProperties) {
	            properties.add(create(cvTermProperty));
	        }
	    }
	    
	    return properties;
	}
	
	public List<CVTermProperty> findProperties(int cvTermId) throws MiddlewareQueryException {
	    List<CVTermProperty> properties = new ArrayList<CVTermProperty>();
	    
	    Database database = getActiveDatabase();
        setWorkingDatabase(Database.LOCAL);
        properties.addAll(getCvTermPropertyDao().getByCvTermId(cvTermId));
	    if (cvTermId > 0) {
	        setWorkingDatabase(Database.CENTRAL);
	        properties.addAll(getCvTermPropertyDao().getByCvTermId(cvTermId));
	    }
	    setWorkingDatabase(database);
	    
	    return properties;
	}
	
	public List<CVTermProperty> findPropertiesByType(int cvTermId, int typeId) throws MiddlewareQueryException {
	    List<CVTermProperty> properties = new ArrayList<CVTermProperty>();
	    
        Database database = getActiveDatabase();
        setWorkingDatabase(Database.LOCAL);
        properties.addAll(getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId));
	    if (cvTermId > 0) {
	        setWorkingDatabase(Database.CENTRAL);
	        properties.addAll(getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId));
	    }
        setWorkingDatabase(database);
        
        return properties;
	}
	
	public CVTermProperty findPropertyByType(int cvTermId, int typeId) throws MiddlewareQueryException {
        CVTermProperty property = null;
        
        Database database = getActiveDatabase();
        setWorkingDatabase(Database.LOCAL);
        property = getCvTermPropertyDao().getOneByCvTermAndType(cvTermId, typeId);
        if (cvTermId > 0 && property == null) {
            setWorkingDatabase(Database.CENTRAL);
            property = getCvTermPropertyDao().getOneByCvTermAndType(cvTermId, typeId);
        }
        setWorkingDatabase(database);
        
        return property;
    }
}
