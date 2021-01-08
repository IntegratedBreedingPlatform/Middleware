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

package org.generationcp.middleware.util;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.hibernate.Session;

/**
 * Used to handle DAO instances and sessions connecting to the database. Superclass of DataManager, Builder, Searcher and Saver classes.
 * Maintains session for local and central connection.
 *
 * @author Joyce Avestro
 */

public class DatabaseBroker {

	protected HibernateSessionProvider sessionProvider;

	protected DatabaseBroker() {

	}

	protected DatabaseBroker(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Session getCurrentSession() {
		return this.getActiveSession();
	}

	public Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}

}
