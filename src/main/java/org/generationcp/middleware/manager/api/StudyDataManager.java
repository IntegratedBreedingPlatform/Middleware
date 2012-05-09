package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;

/**
 * This is the API for retrieving phenotypic data stored as
 * Studies and datasets.
 * 
 * @author Kevin Manansala
 *
 */
public interface StudyDataManager
{
	/**
	 * Returns a List of GIDs identifying Germplasms exhibiting specific values of traits as observed
	 * in studies.  The search filters are composed of combinations of trait, scale, method and
	 * value specified by the users.
	 * 
	 * The start and numOfRows will be used to limit results of the queries used to retrieve the
	 * GIDs.  They do not however depict the size of the List of Integers returned by this
	 * function.  It is recommended to check the size of the List to get the actual count
	 * of GIDs it contains.
	 * 
	 * @param filters
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return
	 * @throws QueryException 
	 */
	public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows) throws QueryException;
	
	/**
	 * Returns all Factor records which belong to the Study identified by the given id.
	 * 
	 * @param studyId - id of the Study
	 * @return List of Factor POJOs
	 */
	public List<Factor> getFactorsByStudyID(Integer studyId) throws QueryException;

	/**
	 * Returns all Variate records which belong to the Study identified by the given id.
	 * 
	 * @param studyId - id of the Study
	 * @return List of Variate POJOs
	 */
	public List<Variate> getVariatesByStudyID(Integer studyId) throws QueryException;
	
	/**
	 * Returns all the Effect records which belong to the Study identified by the given id.
	 * 
	 * @param studyId - id of the Study
	 * @return List of StudyEffect POJOs
	 */
	public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws QueryException;

	/**
	 * Returns a List of {@code Study} objects that are top-level studies, or studies that do
	 * not have parent folders.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param instance - can be Database.LOCAL or Database.CENTRAL
	 * @return The list of all the top-level studies
	 * @throws QueryException
	 */
	public List<Study> getAllTopLevelStudies(int start, int numOfRows, Database instance) throws QueryException;

	/**
	 * Returns a List of {@code Study} objects that belong to the specified Parent Folder ID.
	 * 
	 * @param parentFolderId - the parent folder's studyid
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return The list of all the studies belonging to the specified parent folder. Returns {@code null} if
	 * there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start,
			int numOfRows) throws QueryException;
}
