package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.search.SearchRequest;
import org.hibernate.Session;

public class SearchRequestDAO extends GenericDAO<SearchRequest, Integer> {

	public SearchRequestDAO(final Session session) {
		super(session);
	}
}
