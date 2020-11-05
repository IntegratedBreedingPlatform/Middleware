
package org.generationcp.middleware.service.api;

import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.CrossExpansionProperties;

/*
 * It is important that for the
 * managerFactory.getPedigreeService to work properly that the pedigree.profile should BE SET properly when setting up
 * DynamicManagerFactoryProviderConcurrency
 */
public interface PedigreeService {

	/**
	 * This enables crop specific pedigree expansion
	 * 
	 * @return the name of the crop for which the Pedigree is being generated
	 */
	String getCropName();

	/**
	 * Gets the cross expansion i.e build the pedigree string.
	 *
	 * @param gid the gid
	 * @return The cross expansion based on the given gid and level
	 */
	String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the cross expansion i.e build the pedigree string.
	 *
	 * @param gids Set of gids to build a pedigree string for. The max set size that can be passed in is 5000.
	 * @param level the number of generation traversed to generate the pedigree
	 * @param level the level
	 * @return The cross expansion based on the given gid and level
	 */
	Map<Integer, String> getCrossExpansions(Set<Integer> gids, Integer level, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the cross expansion i.e build the pedigree string.
	 * This method is a workaround for the 5000 limitation. It invokes {@link #getCrossExpansions(Set, Integer, CrossExpansionProperties)}
	 * with batches of 5000 gids until it gets the full list.
	 *
	 * TODO continue investigation of the 5000 limitation.
	 *
	 * @param gids Set of gids to build a pedigree string for.
	 * @param level the number of generation traversed to generate the pedigree
	 * @param level the level
	 * @return The cross expansion based on the given gid and level
	 */
	Map<Integer, String> getCrossExpansionsBulk(Set<Integer> gids, Integer level, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the cross expansion i.e build the pedigree string.
	 * 
	 * @param gid the GID for which we are generating the pedigree
	 * @param level the number of generation traversed to generate the pedigree
	 * @param crossExpansionProperties cross expansion properties configured.
	 * @return the generated string
	 */
	String getCrossExpansion(Integer gid, Integer level, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the cross expansion i.e build the pedigree string.
	 * 
	 * @param germplasm A preconstructed germplasm object. We need this when generating the cross from the crossing manager.
	 * @param level the number of levels we are suppose to do the expansion for
	 * @param crossExpansionProperties default expansion properties
	 * @return The cross expansion based on the given gid and level
	 */
	String getCrossExpansion(Germplasm germplasm, Integer level, CrossExpansionProperties crossExpansionProperties);

}
