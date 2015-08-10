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

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.BreedersQueryDao;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.dao.ListDataPropertyDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.ProgramPresetDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.UserProgramTreeStateDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPhenotypeDao;
import org.generationcp.middleware.dao.dms.ExperimentProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.ExperimentStockDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.LocationSearchDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.ProjectRelationshipDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
import org.generationcp.middleware.dao.dms.StudySearchDao;
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
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDetailsDAO;
import org.generationcp.middleware.dao.gdms.MtaDAO;
import org.generationcp.middleware.dao.gdms.MtaMetadataDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.dao.gdms.TrackDataDAO;
import org.generationcp.middleware.dao.gdms.TrackMarkerDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.TermPropertyBuilder;
import org.hibernate.Session;

/**
 * Used to handle DAO instances and sessions connecting to the database. Superclass of DataManager, Builder, Searcher and Saver classes.
 * Maintains session for local and central connection.
 * 
 * @author Joyce Avestro
 */

public class DatabaseBroker {

	protected HibernateSessionProvider sessionProvider;
	protected String databaseName;
	private GermplasmListDAO germplasmListDao;

	protected static final int JDBC_BATCH_SIZE = 50;

	// StudyDataManager DAO
	private ProjectPropertyDao projectPropertyDao;

	protected DatabaseBroker() {

	}

	protected DatabaseBroker(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	protected DatabaseBroker(HibernateSessionProvider sessionProvider, String databaseName) {
		this.sessionProvider = sessionProvider;
		this.databaseName = databaseName;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Session getCurrentSession() {
		return this.getActiveSession();
	}

	protected Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}

	protected DmsProjectDao getDmsProjectDao() {
		DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.getActiveSession());
		return dmsProjectDao;
	}

	protected CVTermDao getCvTermDao() {
		CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.getActiveSession());
		return cvTermDao;
	}

	protected StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.getActiveSession());
	}

	protected BreedersQueryDao getBreedersQueryDao() {
		return new BreedersQueryDao(this.getActiveSession());
	}

	protected CVTermRelationshipDao getCvTermRelationshipDao() {
		CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.getActiveSession());
		return cvTermRelationshipDao;
	}

	protected CountryDAO getCountryDao() {
		CountryDAO countryDao = new CountryDAO();
		countryDao.setSession(this.getActiveSession());
		return countryDao;
	}

	protected UserDAO getUserDao() {
		UserDAO userDao = new UserDAO();
		userDao.setSession(this.getActiveSession());
		return userDao;
	}

	protected CVDao getCvDao() {
		CVDao cvDao = new CVDao();
		cvDao.setSession(this.getActiveSession());
		return cvDao;
	}

	protected StockDao getStockDao() {
		StockDao stockDao = new StockDao();
		stockDao.setSession(this.getActiveSession());
		return stockDao;
	}

	protected StudySearchDao getStudySearchDao() {
		StudySearchDao studySearchDao = new StudySearchDao();
		studySearchDao.setSession(this.getActiveSession());
		return studySearchDao;
	}

	protected LocationSearchDao getLocationSearchDao() {
		LocationSearchDao dao = new LocationSearchDao();
		dao.setSession(this.getActiveSession());
		return dao;
	}

	protected GeolocationPropertyDao getGeolocationPropertyDao() {
		GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
		geolocationPropertyDao.setSession(this.getActiveSession());
		return geolocationPropertyDao;
	}

	protected ExperimentDao getExperimentDao() {
		ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(this.getActiveSession());
		return experimentDao;
	}

	protected ExperimentPropertyDao getExperimentPropertyDao() {
		ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
		experimentPropertyDao.setSession(this.getActiveSession());
		return experimentPropertyDao;
	}

	protected StockPropertyDao getStockPropertyDao() {
		StockPropertyDao stockPropertyDao = new StockPropertyDao();
		stockPropertyDao.setSession(this.getActiveSession());
		return stockPropertyDao;
	}

	protected ExperimentStockDao getExperimentStockDao() {
		ExperimentStockDao experimentStockDao = new ExperimentStockDao();
		experimentStockDao.setSession(this.getActiveSession());
		return experimentStockDao;
	}

	protected ExperimentProjectDao getExperimentProjectDao() {
		ExperimentProjectDao experimentProjectDao = new ExperimentProjectDao();
		experimentProjectDao.setSession(this.getActiveSession());
		return experimentProjectDao;
	}

	public ProjectPropertyDao getProjectPropertyDao() {
		if (this.projectPropertyDao == null) {
			this.projectPropertyDao = new ProjectPropertyDao();
		}
		this.projectPropertyDao.setSession(this.getActiveSession());
		return this.projectPropertyDao;
	}

	protected ProjectRelationshipDao getProjectRelationshipDao() {
		ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(this.getActiveSession());
		return projectRelationshipDao;
	}

	protected GeolocationDao getGeolocationDao() {
		GeolocationDao geolocationDao = new GeolocationDao();
		geolocationDao.setSession(this.getActiveSession());
		return geolocationDao;
	}

	protected PhenotypeDao getPhenotypeDao() {
		PhenotypeDao phenotypeDao = new PhenotypeDao();
		phenotypeDao.setSession(this.getActiveSession());
		return phenotypeDao;
	}

	protected PhenotypeOutlierDao getPhenotypeOutlierDao() {
		PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
		phenotypeOutlierDao.setSession(this.getActiveSession());
		return phenotypeOutlierDao;
	}

	protected ExperimentPhenotypeDao getExperimentPhenotypeDao() {
		ExperimentPhenotypeDao experimentPhenotypeDao = new ExperimentPhenotypeDao();
		experimentPhenotypeDao.setSession(this.getActiveSession());
		return experimentPhenotypeDao;
	}

	protected CvTermPropertyDao getCvTermPropertyDao() {
		CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.getActiveSession());
		return cvTermPropertyDao;
	}

	protected CvTermSynonymDao getCvTermSynonymDao() {
		CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.getActiveSession());
		return cvTermSynonymDao;
	}

	protected NameDAO getNameDao() {
		NameDAO nameDao = new NameDAO();
		nameDao.setSession(this.getActiveSession());
		return nameDao;
	}

	protected AccMetadataSetDAO getAccMetadataSetDao() {
		AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
		accMetadataSetDao.setSession(this.getActiveSession());
		return accMetadataSetDao;
	}

	protected AlleleValuesDAO getAlleleValuesDao() {
		AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
		alleleValuesDao.setSession(this.getActiveSession());
		return alleleValuesDao;
	}

	protected CharValuesDAO getCharValuesDao() {
		CharValuesDAO charValuesDao = new CharValuesDAO();
		charValuesDao.setSession(this.getActiveSession());
		return charValuesDao;
	}

	protected DartValuesDAO getDartValuesDao() {
		DartValuesDAO dartValuesDao = new DartValuesDAO();
		dartValuesDao.setSession(this.getActiveSession());
		return dartValuesDao;
	}

	protected DatasetDAO getDatasetDao() {
		DatasetDAO datasetDao = new DatasetDAO();
		datasetDao.setSession(this.getActiveSession());
		return datasetDao;
	}

	protected DatasetUsersDAO getDatasetUsersDao() {
		DatasetUsersDAO datasetUsersDao = new DatasetUsersDAO();
		datasetUsersDao.setSession(this.getActiveSession());
		return datasetUsersDao;
	}

	protected MapDAO getMapDao() {
		MapDAO mapDao = new MapDAO();
		mapDao.setSession(this.getActiveSession());
		return mapDao;
	}

	protected MappingDataDAO getMappingDataDao() {
		MappingDataDAO mappingDataDao = new MappingDataDAO();
		mappingDataDao.setSession(this.getActiveSession());
		return mappingDataDao;
	}

	protected MappingPopDAO getMappingPopDao() {
		MappingPopDAO mappingPopDao = new MappingPopDAO();
		mappingPopDao.setSession(this.getActiveSession());
		return mappingPopDao;
	}

	protected MappingPopValuesDAO getMappingPopValuesDao() {
		MappingPopValuesDAO mappingPopValuesDao = new MappingPopValuesDAO();
		mappingPopValuesDao.setSession(this.getActiveSession());
		return mappingPopValuesDao;
	}

	protected MarkerAliasDAO getMarkerAliasDao() {
		MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
		markerAliasDao.setSession(this.getActiveSession());
		return markerAliasDao;
	}

	protected MarkerDAO getMarkerDao() {
		MarkerDAO markerDao = new MarkerDAO();
		markerDao.setSession(this.getActiveSession());
		return markerDao;
	}

	protected MarkerDetailsDAO getMarkerDetailsDao() {
		MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
		markerDetailsDao.setSession(this.getActiveSession());
		return markerDetailsDao;
	}

	protected MarkerInfoDAO getMarkerInfoDao() {
		MarkerInfoDAO markerInfoDao = new MarkerInfoDAO();
		markerInfoDao.setSession(this.getActiveSession());
		return markerInfoDao;
	}

	protected ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
		ExtendedMarkerInfoDAO extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
		extendedMarkerInfoDAO.setSession(this.getActiveSession());
		return extendedMarkerInfoDAO;
	}

	protected MarkerMetadataSetDAO getMarkerMetadataSetDao() {
		MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
		markerMetadataSetDao.setSession(this.getActiveSession());
		return markerMetadataSetDao;
	}

	protected MarkerOnMapDAO getMarkerOnMapDao() {
		MarkerOnMapDAO markerOnMapDao = new MarkerOnMapDAO();
		markerOnMapDao.setSession(this.getActiveSession());
		return markerOnMapDao;
	}

	protected MarkerUserInfoDAO getMarkerUserInfoDao() {
		MarkerUserInfoDAO markerUserInfoDao = new MarkerUserInfoDAO();
		markerUserInfoDao.setSession(this.getActiveSession());
		return markerUserInfoDao;
	}

	protected MarkerUserInfoDetailsDAO getMarkerUserInfoDetailsDao() {
		MarkerUserInfoDetailsDAO markerUserInfoDetailsDao = new MarkerUserInfoDetailsDAO();
		markerUserInfoDetailsDao.setSession(this.getActiveSession());
		return markerUserInfoDetailsDao;
	}

	protected QtlDAO getQtlDao() {
		QtlDAO qtlDao = new QtlDAO();
		qtlDao.setSession(this.getActiveSession());
		return qtlDao;
	}

	protected QtlDetailsDAO getQtlDetailsDao() {
		QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
		qtlDetailsDao.setSession(this.getActiveSession());
		return qtlDetailsDao;
	}

	protected MtaDAO getMtaDao() {
		MtaDAO mtaDao = new MtaDAO();
		mtaDao.setSession(this.getActiveSession());
		return mtaDao;
	}

	protected MtaMetadataDAO getMtaMetadataDao() {
		MtaMetadataDAO mtaMetadataDao = new MtaMetadataDAO();
		mtaMetadataDao.setSession(this.getActiveSession());
		return mtaMetadataDao;
	}

	protected TrackDataDAO getTrackDataDao() {
		TrackDataDAO trackDataDao = new TrackDataDAO();
		trackDataDao.setSession(this.getActiveSession());
		return trackDataDao;
	}

	protected TrackMarkerDAO getTrackMarkerDao() {
		TrackMarkerDAO trackMarkerDao = new TrackMarkerDAO();
		trackMarkerDao.setSession(this.getActiveSession());
		return trackMarkerDao;
	}

	protected AttributeDAO getAttributeDao() {
		AttributeDAO attributeDao = new AttributeDAO();
		attributeDao.setSession(this.getActiveSession());
		return attributeDao;
	}

	protected BibrefDAO getBibrefDao() {
		BibrefDAO bibrefDao = new BibrefDAO();
		bibrefDao.setSession(this.getActiveSession());
		return bibrefDao;
	}

	protected GermplasmDAO getGermplasmDao() {
		GermplasmDAO germplasmDao = new GermplasmDAO();
		germplasmDao.setSession(this.getActiveSession());
		return germplasmDao;
	}

	protected LocationDAO getLocationDao() {
		LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	protected LocdesDAO getLocDesDao() {
		LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	protected MethodDAO getMethodDao() {
		MethodDAO methodDao = new MethodDAO();
		methodDao.setSession(this.getActiveSession());
		return methodDao;
	}

	protected ProgenitorDAO getProgenitorDao() {
		ProgenitorDAO progenitorDao = new ProgenitorDAO();
		progenitorDao.setSession(this.getActiveSession());
		return progenitorDao;
	}

	protected UserDefinedFieldDAO getUserDefinedFieldDao() {
		UserDefinedFieldDAO userDefinedFieldDao = new UserDefinedFieldDAO();
		userDefinedFieldDao.setSession(this.getActiveSession());
		return userDefinedFieldDao;
	}

	protected LocationDAO getLocationDAO() {
		LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	protected LocdesDAO getLocdesDao() {
		LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		ProgramFavoriteDAO programFavoriteDao = new ProgramFavoriteDAO();
		programFavoriteDao.setSession(this.getActiveSession());
		return programFavoriteDao;
	}

	protected GermplasmListDAO getGermplasmListDAO() {
		if (this.germplasmListDao == null) {
			this.germplasmListDao = new GermplasmListDAO();
		}
		this.germplasmListDao.setSession(this.getActiveSession());
		return this.germplasmListDao;
	}

	public void setGermplasmListDao(GermplasmListDAO germplasmListDao) {
		this.germplasmListDao = germplasmListDao;
	}

	protected GermplasmListDataDAO getGermplasmListDataDAO() {
		GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(this.getActiveSession());
		return germplasmListDataDao;
	}

	protected ListDataPropertyDAO getListDataPropertyDAO() {
		ListDataPropertyDAO listDataPropertyDao = new ListDataPropertyDAO();
		listDataPropertyDao.setSession(this.getActiveSession());
		return listDataPropertyDao;
	}

	protected ListDataProjectDAO getListDataProjectDAO() {
		ListDataProjectDAO listDataProjectDao = new ListDataProjectDAO();
		listDataProjectDao.setSession(this.getActiveSession());
		return listDataProjectDao;
	}

	protected LotDAO getLotDao() {
		LotDAO lotDao = new LotDAO();
		lotDao.setSession(this.getActiveSession());
		return lotDao;
	}

	protected PersonDAO getPersonDao() {
		PersonDAO personDao = new PersonDAO();
		personDao.setSession(this.getActiveSession());
		return personDao;
	}

	public TransactionDAO getTransactionDao() {
		TransactionDAO transactionDao = new TransactionDAO();
		transactionDao.setSession(this.getActiveSession());
		return transactionDao;
	}

	public StockTransactionDAO getStockTransactionDAO() {
		StockTransactionDAO stockTransactionDAO = new StockTransactionDAO();
		stockTransactionDAO.setSession(this.getActiveSession());
		return stockTransactionDAO;
	}

	protected TermPropertyBuilder getTermPropertyBuilder() {
		return new TermPropertyBuilder(this.sessionProvider);
	}

	// ================================ InventoryDataManager DAO Methods =============================
	protected ProgramPresetDAO getProgramPresetDAO() {
		ProgramPresetDAO programPresetDAO = new ProgramPresetDAO();
		programPresetDAO.setSession(this.getActiveSession());
		return programPresetDAO;
	}

	// ===========================================================================================

	protected void clearSessions() {
		if (this.sessionProvider != null) {
			this.sessionProvider.getSession().clear();
		}
	}

	protected void flushSessions() {
		if (this.sessionProvider != null) {
			this.sessionProvider.getSession().flush();
		}
	}

	public UserProgramTreeStateDAO getUserProgramTreeStateDAO() {
		UserProgramTreeStateDAO userProgramTreeStateDAO = new UserProgramTreeStateDAO();
		userProgramTreeStateDAO.setSession(getActiveSession());
		return userProgramTreeStateDAO;
	}

	public void setProjectPropertyDao(ProjectPropertyDao projectPropertyDao) {
		this.projectPropertyDao = projectPropertyDao;
	}

}
