package org.generationcp.middleware.service.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.util.CrossExpansionProperties;

/*
 * Currently this service is configured to work with Fieldbook and BreedingManager.  
 * It is important that for the managerFactory.getPedigreeService to work properly that the pedigree.profile should BE SET properly when setting up DynamicManagerFactoryProviderConcurrency
 */
public interface PedigreeService {
	/**
     * Gets the cross expansion.
     *
     * @param gid the gid
     * @param level the level
     * @return The cross expansion based on the given gid and level
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException;
    String getCrossExpansion(Integer gid, Integer level, CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException;
}
