package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;

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
	 * Returns the study records matching the given name
	 * 
	 * @param name - search string (pattern or exact match) for the name of the study
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL like LIKE
	 * 				For LIKE operation, the parameter name may include the following:
	 * 				"%" - to indicate 0 or more characters in the pattern
	 * 				"_" - to indicate any single character in the pattern 
	 * @param instance - can be CENTRAL or LOCAL
	 * @return List of Study POJOs
	 * @throws QueryException
	 */
	public List<Study> findStudyByName(String name, int start, int numOfRows, Operation op, Database instance) throws QueryException;

	/**
	 * Returns the study records matching the given name
	 * 
	 * @param name - search string (pattern or exact match) for the name of the study
	 * @param op - can be EQUAL like LIKE
	 * 				For LIKE operation, the parameter name may include the following:
	 * 				"%" - to indicate 0 or more characters in the pattern
	 * 				"_" - to indicate any single character in the pattern 
	 * @param instance - can be CENTRAL or LOCAL
	 * @return number of Study records matching the given criteria
	 * @throws QueryException
	 */
	public int countStudyByName(String name, Operation op, Database instance) throws QueryException;

	/**
	 * Retrieves a Study record of the given id
	 * 
	 * @param id
	 * @return A Study POJO
	 * @throws QueryException
	 */
	public Study getStudyByID(Integer id) throws QueryException;

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
	 * @return The list of all the studies belonging to the specified parent folder. Returns an empty list
	 * if there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start,
			int numOfRows) throws QueryException;

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
	 * Returns all the Representation records with the given effectId.
	 * 
	 * @param effectId
	 * @return List of Representation POJOs
	 */
	public List<Representation> getRepresentationByEffectID(Integer effectId) throws QueryException;

	/**
	 * Returns a List of {@code Factor} objects that belong to the specified Representation ID.
	 * 
	 * @param representationId - the ID of the Representation
	 * @return The list of all the factors belonging to the specified Representation. Returns an empty list
	 * if there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public List<Factor> getFactorsByRepresentationId(Integer representationId) throws QueryException;
	
	/**
	 * Returns the number of OunitIDs that are associated to the specified Representation ID.
	 * 
	 * @param representationId - the ID of the Representation
	 * @return The number of all the OunitIDs associated to the specified Representation. Returns 0
	 * if there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public Long countOunitIDsByRepresentationId(Integer representationId) throws QueryException;

	/**
	 * Returns a List of OunitIDs that are associated to the specified Representation ID.
	 * 
	 * @param representationId - the ID of the Representation
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return The list of all the OunitIDs associated to the specified Representation. Returns an empty list
	 * if there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws QueryException;

	/**
	 * Returns a List of {@code Variate} objects that belong to the specified Representation ID.
	 * 
	 * @param representationId - the ID of the Representation
	 * @return The list of all the variates belonging to the specified Representation. Returns an empty list
	 * if there are no connections detected for both local and central instances.
	 * @throws QueryException
	 */
	public List<Variate> getVariatesByRepresentationId(Integer representationId) throws QueryException;

	/**
	 * Returns a list of NumericDataElements that represents the column values for each specified ounitID
	 * (for each specified row).
	 * @param ounitIdList - list of ounitIDs to get the corresponding column values. IDs in the list must
	 * be all from the Central DB or all from the Local DB.
	 * @return The list of column values / NumericDataElements for the specified ounitIDs
	 * @throws QueryException
	 */
	public List<NumericDataElement> getNumericDataValuesByOunitIdList(List<Integer> ounitIdList) 
		throws QueryException;
	
}
