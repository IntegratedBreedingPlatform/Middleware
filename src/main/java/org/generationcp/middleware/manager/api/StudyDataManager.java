package org.generationcp.middleware.manager.api;

import java.util.List;

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
	 */
	public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows);
}
