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

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
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

	public DmsProjectDao getDmsProjectDao() {
		final DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.getActiveSession());
		return dmsProjectDao;
	}

	public StockDao getStockDao() {
		final StockDao stockDao = new StockDao();
		stockDao.setSession(this.getActiveSession());
		return stockDao;
	}

	public GeolocationPropertyDao getGeolocationPropertyDao() {
		final GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
		geolocationPropertyDao.setSession(this.getActiveSession());
		return geolocationPropertyDao;
	}

	public ExperimentDao getExperimentDao() {
		final ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(this.getActiveSession());
		return experimentDao;
	}

	public ExperimentPropertyDao getExperimentPropertyDao() {
		final ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
		experimentPropertyDao.setSession(this.getActiveSession());
		return experimentPropertyDao;
	}

	public StockPropertyDao getStockPropertyDao() {
		final StockPropertyDao stockPropertyDao = new StockPropertyDao();
		stockPropertyDao.setSession(this.getActiveSession());
		return stockPropertyDao;
	}

	public ProjectPropertyDao getProjectPropertyDao() {
		final ProjectPropertyDao projectPropertyDao = new ProjectPropertyDao();
		projectPropertyDao.setSession(this.getActiveSession());
		return projectPropertyDao;
	}

	public PhenotypeDao getPhenotypeDao() {
		final PhenotypeDao phenotypeDao = new PhenotypeDao();
		phenotypeDao.setSession(this.getActiveSession());
		return phenotypeDao;
	}

}
