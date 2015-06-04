
package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public interface MBDTDataManager {

	/**
	 *
	 * @param projectData The Project object to be saved
	 * @return the integer representing the project_id of the saved entry
	 * @throws MiddlewareQueryException if the provided project data does not have a project name
	 */
	public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException;

	/**
	 *
	 * @param projectID The project id of the entry to be retrieved
	 * @return The MBDTProjectData object representing the entry retrieved from the database
	 * @throws MiddlewareQueryException
	 */
	public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException;

	/**
	 *
	 * @return a list of MBDTProjectData objects representing all of the project data available in the database
	 * @throws MiddlewareQueryException
	 */
	public List<MBDTProjectData> getAllProjects() throws MiddlewareQueryException;

	/**
	 *
	 * @param projectName The name of the project to be searched for in the database
	 * @return An integer representing the project id of the project with the given name. Returns null if a project with the given name
	 *         cannot be found
	 * @throws MiddlewareQueryException
	 */
	public Integer getProjectIDByProjectName(String projectName) throws MiddlewareQueryException;

	/**
	 *
	 * @param projectID The project id the MBDTGeneration object will be associated to during the saving operation
	 * @param generation The MBDTGeneration object to be saved into the database
	 * @return The saved MBDTGeneration object, with a valid generation ID
	 * @throws MiddlewareQueryException if the provided project ID does not refer to a valid project
	 */
	public MBDTGeneration setGeneration(Integer projectID, MBDTGeneration generation) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The generation id of the generation object to be retrieved from the database
	 * @return The MBDTGeneration object representing the entry retrieved from the database
	 * @throws MiddlewareQueryException
	 */
	public MBDTGeneration getGeneration(Integer generationID) throws MiddlewareQueryException;

	/**
	 *
	 * @param projectID The id representing the parent project of the generation entries to be retrieved from the database
	 * @return a list of MBDTGeneration objects representing the generation entries with the given project_id as its parent
	 * @throws MiddlewareQueryException
	 */
	public List<MBDTGeneration> getAllGenerations(Integer projectID) throws MiddlewareQueryException;

	/**
	 *
	 * @param name The name of the generation to be retrieved
	 * @param projectID The ID of the project this generation is associated with
	 * @return The integer representing the ID of the generation of the given name within the specified project
	 * @throws MiddlewareQueryException
	 */

	public Integer getGenerationIDByGenerationName(String name, Integer projectID) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The generation ID that the marker entries will be associated to during the saving process
	 * @param markerIDs The List of marker IDs that will be saved to the database under the mbdt_selected_markers table
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existing entry in the mbdt_generations table
	 */
	public void setMarkerStatus(Integer generationID, List<Integer> markerIDs) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The id representing the parent generation of the marker entries to be retrieved
	 * @return A list of integers representing the marker IDs associated with the given generation ID
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existent entry in the mbdt_generations table
	 */
	public List<Integer> getMarkerStatus(Integer generationID) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The id representing the parent generation entry associated with the selected genotype entries to be retrieved
	 * @return A list of SelectedGenotype objects representing entries in the mbdt_selected_genotypes table associated with the provided
	 *         generation ID and whose type is marked as selected
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existent entry in the mbdt_generations table
	 */
	public List<SelectedGenotype> getSelectedAccession(Integer generationID) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The id representing the parent generation entry associated with selected genotype entries to be retrieved
	 * @return A list of SelectedGenotype objects representing entries in the mbdt_selected_genotypes table associated with the given
	 *         generation ID
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existent entry in the mbdt_generations table
	 */
	public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The id of the generation entry to which the saved entries / existing entries will be / are associated
	 * @param gids A list of GIDs. Existing GID entries will have their selected status toggled on / off. Non existing GID entries will be
	 *        saved to the mbdt_selected_genotypes table with sg_type = 'SR'
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existent entry in the mbdt_generations table
	 */
	public void setSelectedAccessions(Integer generationID, List<Integer> gids) throws MiddlewareQueryException;

	/**
	 *
	 * @param generationID The id representing the generation entry to which the saved entries / existing entries will be / are associated
	 * @param genotypeEnum The parent data type to which the entry will be saved / modified. Possible values are SelectedGenotypeEnum.R or
	 *        SelectedGenotypeEnum.D only.
	 * @param gids A list of GIDs. Existing GID entries will have their parent data type modified (while still retaining the selected prefix
	 *        if existing). Non existing GID entries will be saved to the mbdt_selected_genotypes table with sg_type equal to the provided
	 *        value for genotypeEnum
	 * @throws MiddlewareQueryException if the provided generation ID refers to a non existent entry in the mbdt_generations table
	 */
	public void setParentData(Integer generationID, SelectedGenotypeEnum genotypeEnum, List<Integer> gids) throws MiddlewareQueryException;

	// for test purposes
	public void clear();
}
