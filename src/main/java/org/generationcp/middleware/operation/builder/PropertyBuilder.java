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

import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;

import java.util.List;


public class PropertyBuilder extends Builder{

    public PropertyBuilder(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        List<Property> properties = getCvTermDao().getAllPropertiesWithTraitClass();
        
        setWorkingDatabase(Database.LOCAL);
        properties.addAll(getCvTermDao().getAllPropertiesWithTraitClass());
        
        return properties;
    }
}
