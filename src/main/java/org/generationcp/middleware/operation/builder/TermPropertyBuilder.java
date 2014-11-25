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

import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

import java.util.ArrayList;
import java.util.List;

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
        setWorkingDatabase(Database.LOCAL);
        return getCvTermPropertyDao().getByCvTermId(cvTermId);
	}
	
	public List<CVTermProperty> findPropertiesByType(int cvTermId, int typeId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        return getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId);
	}
	
	public CVTermProperty findPropertyByType(int cvTermId, int typeId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        return getCvTermPropertyDao().getOneByCvTermAndType(cvTermId, typeId);
    }
}
