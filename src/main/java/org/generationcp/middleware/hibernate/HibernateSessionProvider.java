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

package org.generationcp.middleware.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implementations of this interface allows you to get a Hibernate {@link Session}.
 *
 * @author Glenn Marintes
 */
public interface HibernateSessionProvider {

	/**
	 * Get a Hibernate {@link Session}.
	 * 
	 * @return the Session
	 */
	Session getSession();

	/**
	 * Close this {@link HibernateSessionProvider}.<br>
	 * Implementations should clear resources used by this {@link HibernateSessionProvider}.
	 */
	void close();
	
	SessionFactory getSessionFactory();
}
