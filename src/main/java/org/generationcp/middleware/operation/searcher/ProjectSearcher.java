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

public abstract class ProjectSearcher extends Searcher {

	protected ProjectSearcher(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}
}
