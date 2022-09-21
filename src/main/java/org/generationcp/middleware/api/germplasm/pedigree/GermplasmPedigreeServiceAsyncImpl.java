package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;

/**
 * Not a managed bean, can be used outside a request. Instantiate with "new"
 */
public class GermplasmPedigreeServiceAsyncImpl extends GermplasmPedigreeServiceImpl {

	public GermplasmPedigreeServiceAsyncImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}
}
