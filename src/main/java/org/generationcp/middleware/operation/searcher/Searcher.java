/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.searcher;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides searcher classes that can be used to retrieve data from IBDBv2 schema. Creates searcher classes based on the given local/central
 * session parameters.
 *
 * @author Donald Barre
 */
public abstract class Searcher extends DatabaseBroker {

	protected Searcher(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

}
