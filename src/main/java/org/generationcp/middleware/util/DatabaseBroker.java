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

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmSearchDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.ExtendedMarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerAliasDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.MtaDAO;
import org.generationcp.middleware.dao.gdms.MtaMetadataDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.dao.gdms.TrackDataDAO;
import org.generationcp.middleware.dao.gdms.TrackMarkerDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.StandardVariableDao;
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

	public StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.getActiveSession());
	}

	public CVDao getCvDao() {
		final CVDao cvDao = new CVDao();
		cvDao.setSession(this.getActiveSession());
		return cvDao;
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

	public GeolocationDao getGeolocationDao() {
		final GeolocationDao geolocationDao = new GeolocationDao();
		geolocationDao.setSession(this.getActiveSession());
		return geolocationDao;
	}

	public PhenotypeDao getPhenotypeDao() {
		final PhenotypeDao phenotypeDao = new PhenotypeDao();
		phenotypeDao.setSession(this.getActiveSession());
		return phenotypeDao;
	}

	public PhenotypeOutlierDao getPhenotypeOutlierDao() {
		final PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
		phenotypeOutlierDao.setSession(this.getActiveSession());
		return phenotypeOutlierDao;
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		final CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.getActiveSession());
		return cvTermSynonymDao;
	}

	public AccMetadataSetDAO getAccMetadataSetDao() {
		final AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
		accMetadataSetDao.setSession(this.getActiveSession());
		return accMetadataSetDao;
	}

	public AlleleValuesDAO getAlleleValuesDao() {
		final AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
		alleleValuesDao.setSession(this.getActiveSession());
		return alleleValuesDao;
	}

	public CharValuesDAO getCharValuesDao() {
		final CharValuesDAO charValuesDao = new CharValuesDAO();
		charValuesDao.setSession(this.getActiveSession());
		return charValuesDao;
	}

	public DartValuesDAO getDartValuesDao() {
		final DartValuesDAO dartValuesDao = new DartValuesDAO();
		dartValuesDao.setSession(this.getActiveSession());
		return dartValuesDao;
	}

	public DatasetDAO getDatasetDao() {
		final DatasetDAO datasetDao = new DatasetDAO();
		datasetDao.setSession(this.getActiveSession());
		return datasetDao;
	}

	public DatasetUsersDAO getDatasetUsersDao() {
		final DatasetUsersDAO datasetUsersDao = new DatasetUsersDAO();
		datasetUsersDao.setSession(this.getActiveSession());
		return datasetUsersDao;
	}

	public MapDAO getMapDao() {
		final MapDAO mapDao = new MapDAO();
		mapDao.setSession(this.getActiveSession());
		return mapDao;
	}

	public MappingDataDAO getMappingDataDao() {
		final MappingDataDAO mappingDataDao = new MappingDataDAO();
		mappingDataDao.setSession(this.getActiveSession());
		return mappingDataDao;
	}

	public MappingPopDAO getMappingPopDao() {
		final MappingPopDAO mappingPopDao = new MappingPopDAO();
		mappingPopDao.setSession(this.getActiveSession());
		return mappingPopDao;
	}

	public MappingPopValuesDAO getMappingPopValuesDao() {
		final MappingPopValuesDAO mappingPopValuesDao = new MappingPopValuesDAO();
		mappingPopValuesDao.setSession(this.getActiveSession());
		return mappingPopValuesDao;
	}

	public MarkerAliasDAO getMarkerAliasDao() {
		final MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
		markerAliasDao.setSession(this.getActiveSession());
		return markerAliasDao;
	}

	public MarkerDAO getMarkerDao() {
		final MarkerDAO markerDao = new MarkerDAO();
		markerDao.setSession(this.getActiveSession());
		return markerDao;
	}

	public MarkerDetailsDAO getMarkerDetailsDao() {
		final MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
		markerDetailsDao.setSession(this.getActiveSession());
		return markerDetailsDao;
	}

	public MarkerInfoDAO getMarkerInfoDao() {
		final MarkerInfoDAO markerInfoDao = new MarkerInfoDAO();
		markerInfoDao.setSession(this.getActiveSession());
		return markerInfoDao;
	}

	public ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
		final ExtendedMarkerInfoDAO extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
		extendedMarkerInfoDAO.setSession(this.getActiveSession());
		return extendedMarkerInfoDAO;
	}

	public MarkerMetadataSetDAO getMarkerMetadataSetDao() {
		final MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
		markerMetadataSetDao.setSession(this.getActiveSession());
		return markerMetadataSetDao;
	}

	public MarkerOnMapDAO getMarkerOnMapDao() {
		final MarkerOnMapDAO markerOnMapDao = new MarkerOnMapDAO();
		markerOnMapDao.setSession(this.getActiveSession());
		return markerOnMapDao;
	}

	public MarkerUserInfoDAO getMarkerUserInfoDao() {
		final MarkerUserInfoDAO markerUserInfoDao = new MarkerUserInfoDAO();
		markerUserInfoDao.setSession(this.getActiveSession());
		return markerUserInfoDao;
	}

	public QtlDAO getQtlDao() {
		final QtlDAO qtlDao = new QtlDAO();
		qtlDao.setSession(this.getActiveSession());
		return qtlDao;
	}

	public QtlDetailsDAO getQtlDetailsDao() {
		final QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
		qtlDetailsDao.setSession(this.getActiveSession());
		return qtlDetailsDao;
	}

	public MtaDAO getMtaDao() {
		final MtaDAO mtaDao = new MtaDAO();
		mtaDao.setSession(this.getActiveSession());
		return mtaDao;
	}

	public MtaMetadataDAO getMtaMetadataDao() {
		final MtaMetadataDAO mtaMetadataDao = new MtaMetadataDAO();
		mtaMetadataDao.setSession(this.getActiveSession());
		return mtaMetadataDao;
	}

	public TrackDataDAO getTrackDataDao() {
		final TrackDataDAO trackDataDao = new TrackDataDAO();
		trackDataDao.setSession(this.getActiveSession());
		return trackDataDao;
	}

	public TrackMarkerDAO getTrackMarkerDao() {
		final TrackMarkerDAO trackMarkerDao = new TrackMarkerDAO();
		trackMarkerDao.setSession(this.getActiveSession());
		return trackMarkerDao;
	}

	public GermplasmDAO getGermplasmDao() {
		final GermplasmDAO germplasmDao = new GermplasmDAO();
		germplasmDao.setSession(this.getActiveSession());
		return germplasmDao;
	}

	public GermplasmSearchDAO getGermplasmSearchDao() {
		final GermplasmSearchDAO germplasmSearchDao = new GermplasmSearchDAO();
		germplasmSearchDao.setSession(this.getActiveSession());
		return germplasmSearchDao;
	}

	/**
	 * Parse hibernate query result value to boolean with null check
	 * 
	 * @param val value
	 * @return boolean
	 */
	protected boolean typeSafeObjectToBoolean(final Object val) {
		if (val == null) {
			  return false;
		  }
		if (val instanceof Integer) {
			  return (Integer) val != 0;
		  }
		if (val instanceof Boolean) {
			  return (Boolean) val;
		  }
		return false;
	}

	/**
	 * Parse hibernate query result value to Integer with null check
	 * 
	 * @param val value
	 * @return boolean
	 */
	protected Integer typeSafeObjectToInteger(final Object val) {
		if (val == null) {
			  return null;
		  }
		if (val instanceof Integer) {
			  return (Integer) val;
		  }
		if (val instanceof String) {
			  return Integer.valueOf((String) val);
		  }
		throw new NumberFormatException("Can not cast " + val.getClass() + " to Integer for value: " + val);
	}
}
