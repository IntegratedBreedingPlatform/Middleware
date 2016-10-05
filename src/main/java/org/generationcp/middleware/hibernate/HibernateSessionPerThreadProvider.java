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

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * <p>
 * A {@link HibernateSessionProvider} implementation that follows the Session-Per-Thread model.
 * </p>
 * <p>
 * {@link HibernateSessionProvider#getSession()} is implemented to open a new session if no session has been previously created for the
 * current thread.
 * </p>
 * <p>
 * Users of this {@link HibernateSessionProvider} must call the {@link HibernateSessionPerThreadProvider#close()} to close the
 * {@link Session} before the calling {@link Thread} ends.
 * </p>
 *
 * @author Glenn Marintes
 */
public class HibernateSessionPerThreadProvider implements HibernateSessionProvider, Serializable {

	private static final long serialVersionUID = 5411397700593790798L;

	private SessionFactory sessionFactory;


	public HibernateSessionPerThreadProvider() {
	}

	public HibernateSessionPerThreadProvider(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Session getSession() {
		return SessionFactoryUtils.getSession(sessionFactory, false);
	}

	/**
	 * <p>
	 * This implementation will close the {@link Session} for the calling {@link Thread} only.
	 * </p>
	 * <p>
	 * Users of {@link HibernateSessionPerThreadProvider} must be careful that they are calling
	 * {@link HibernateSessionPerThreadProvider#close()} from the right thread.
	 * </p>
	 * <p>
	 * IMPORTANT: Calling this method WILL NOT CLOSE all open sessions. To avoid leaking Sessions, always call this method for each thread
	 * that has called {@link HibernateSessionPerThreadProvider#getSession()}.
	 * </p>
	 */
	@Override
	public void close() {
		//TODO:TX we need to get rid of this method.
	}
}
