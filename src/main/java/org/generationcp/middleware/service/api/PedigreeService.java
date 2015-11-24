
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.CrossExpansionProperties;

/*
 * Currently this service is configured to work with Fieldbook and BreedingManager. It is important that for the
 * managerFactory.getPedigreeService to work properly that the pedigree.profile should BE SET properly when setting up
 * DynamicManagerFactoryProviderConcurrency
 */
public interface PedigreeService {

	/**
	 * Gets the cross expansion.
	 * 
	 * @param gid the gid
	 * @param level the level
	 * @return The cross expansion based on the given gid and level
	 */
	String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties);

	String getCrossExpansion(Integer gid, Integer level, CrossExpansionProperties crossExpansionProperties);

	/**
	 * 
	 * @param germplasm A preconstructed germplasm object. We need this when generating the cross from the crossing manager.
	 * @param level the number of levels we are suppose to do the expansion for
	 * @param crossExpansionProperties default expansion properties
	 * @return The cross expansion based on the given gid and level
	 */
	String getCrossExpansion(Germplasm germplasm, Integer level, CrossExpansionProperties crossExpansionProperties);

}
