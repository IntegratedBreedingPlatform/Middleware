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
 * A {@link HibernateSessionProvider} implementation that is aimed to achieve the Session-Per-Request model.
 * </p>
 * <p>
 * {@link HibernateSessionProvider#getSession()} is implemented to open a new session if no session has been previously created.
 * </p>
 * <p>
 * When this {@link HibernateSessionProvider} is closed, the associated {@link Session} is also closed.
 * </p>
 *
 * @author Glenn Marintes
 */
public class HibernateSessionPerRequestProvider implements HibernateSessionProvider, Serializable {

	private static final long serialVersionUID = 6165749383476030243L;

	private SessionFactory sessionFactory;

	public HibernateSessionPerRequestProvider() {
	}

	public HibernateSessionPerRequestProvider(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public synchronized SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public synchronized void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public synchronized Session getSession() {
		return SessionFactoryUtils.getSession(sessionFactory, false);
	}

	@Override
	public void close() {
		//TODO:TX we need to get rid of this method.
	}
}
