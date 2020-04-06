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

package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.saver.ProjectPropertySaver;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides destroyer classes that can be used to delete logical/physical data in IBDBv2. Creates destroyer classes based on the given
 * local/central session parameters.
 *
 * @author Donald Barre
 */
public abstract class Destroyer extends DatabaseBroker {

	public Destroyer(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	protected final ProjectPropertySaver getProjectPropertySaver() {
		return new ProjectPropertySaver(this.sessionProvider);
	}

}
