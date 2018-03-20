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
import org.generationcp.middleware.dao.GermplasmSearchDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.dao.ListDataPropertyDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.NamingConfigurationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.ProgramPresetDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
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
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
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

	protected static final int JDBC_BATCH_SIZE = 50;

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

	public Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}

	public DmsProjectDao getDmsProjectDao() {
		DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.getActiveSession());
		return dmsProjectDao;
	}

	public CVTermDao getCvTermDao() {
		CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.getActiveSession());
		return cvTermDao;
	}

	public StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.getActiveSession());
	}

	public BreedersQueryDao getBreedersQueryDao() {
		return new BreedersQueryDao(this.getActiveSession());
	}

	public CVTermRelationshipDao getCvTermRelationshipDao() {
		CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.getActiveSession());
		return cvTermRelationshipDao;
	}

	public CountryDAO getCountryDao() {
		CountryDAO countryDao = new CountryDAO();
		countryDao.setSession(this.getActiveSession());
		return countryDao;
	}

	public UserDAO getUserDao() {
		UserDAO userDao = new UserDAO();
		userDao.setSession(this.getActiveSession());
		return userDao;
	}

	public CVDao getCvDao() {
		CVDao cvDao = new CVDao();
		cvDao.setSession(this.getActiveSession());
		return cvDao;
	}

	public StockDao getStockDao() {
		StockDao stockDao = new StockDao();
		stockDao.setSession(this.getActiveSession());
		return stockDao;
	}

	public StudySearchDao getStudySearchDao() {
		StudySearchDao studySearchDao = new StudySearchDao();
		studySearchDao.setSession(this.getActiveSession());
		return studySearchDao;
	}

	public LocationSearchDao getLocationSearchDao() {
		LocationSearchDao dao = new LocationSearchDao();
		dao.setSession(this.getActiveSession());
		return dao;
	}

	public GeolocationPropertyDao getGeolocationPropertyDao() {
		GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
		geolocationPropertyDao.setSession(this.getActiveSession());
		return geolocationPropertyDao;
	}

	public ExperimentDao getExperimentDao() {
		ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(this.getActiveSession());
		return experimentDao;
	}

	public ExperimentPropertyDao getExperimentPropertyDao() {
		ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
		experimentPropertyDao.setSession(this.getActiveSession());
		return experimentPropertyDao;
	}

	public StockPropertyDao getStockPropertyDao() {
		StockPropertyDao stockPropertyDao = new StockPropertyDao();
		stockPropertyDao.setSession(this.getActiveSession());
		return stockPropertyDao;
	}

	public ExperimentStockDao getExperimentStockDao() {
		ExperimentStockDao experimentStockDao = new ExperimentStockDao();
		experimentStockDao.setSession(this.getActiveSession());
		return experimentStockDao;
	}

	public ExperimentProjectDao getExperimentProjectDao() {
		ExperimentProjectDao experimentProjectDao = new ExperimentProjectDao();
		experimentProjectDao.setSession(this.getActiveSession());
		return experimentProjectDao;
	}

	public ProjectPropertyDao getProjectPropertyDao() {
		ProjectPropertyDao projectPropertyDao = new ProjectPropertyDao();
		projectPropertyDao.setSession(this.getActiveSession());
		return projectPropertyDao;
	}

	public ProjectRelationshipDao getProjectRelationshipDao() {
		ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(this.getActiveSession());
		return projectRelationshipDao;
	}

	public GeolocationDao getGeolocationDao() {
		GeolocationDao geolocationDao = new GeolocationDao();
		geolocationDao.setSession(this.getActiveSession());
		return geolocationDao;
	}

	public PhenotypeDao getPhenotypeDao() {
		PhenotypeDao phenotypeDao = new PhenotypeDao();
		phenotypeDao.setSession(this.getActiveSession());
		return phenotypeDao;
	}

	public PhenotypeOutlierDao getPhenotypeOutlierDao() {
		PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
		phenotypeOutlierDao.setSession(this.getActiveSession());
		return phenotypeOutlierDao;
	}

	public ExperimentPhenotypeDao getExperimentPhenotypeDao() {
		ExperimentPhenotypeDao experimentPhenotypeDao = new ExperimentPhenotypeDao();
		experimentPhenotypeDao.setSession(this.getActiveSession());
		return experimentPhenotypeDao;
	}

	public CvTermPropertyDao getCvTermPropertyDao() {
		CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.getActiveSession());
		return cvTermPropertyDao;
	}

	public VariableOverridesDao getVariableProgramOverridesDao() {
		VariableOverridesDao variableOverridesDao = new VariableOverridesDao();
		variableOverridesDao.setSession(this.getActiveSession());
		return variableOverridesDao;
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.getActiveSession());
		return cvTermSynonymDao;
	}

	public NameDAO getNameDao() {
		NameDAO nameDao = new NameDAO();
		nameDao.setSession(this.getActiveSession());
		return nameDao;
	}

	public AccMetadataSetDAO getAccMetadataSetDao() {
		AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
		accMetadataSetDao.setSession(this.getActiveSession());
		return accMetadataSetDao;
	}

	public AlleleValuesDAO getAlleleValuesDao() {
		AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
		alleleValuesDao.setSession(this.getActiveSession());
		return alleleValuesDao;
	}

	public CharValuesDAO getCharValuesDao() {
		CharValuesDAO charValuesDao = new CharValuesDAO();
		charValuesDao.setSession(this.getActiveSession());
		return charValuesDao;
	}

	public DartValuesDAO getDartValuesDao() {
		DartValuesDAO dartValuesDao = new DartValuesDAO();
		dartValuesDao.setSession(this.getActiveSession());
		return dartValuesDao;
	}

	public DatasetDAO getDatasetDao() {
		DatasetDAO datasetDao = new DatasetDAO();
		datasetDao.setSession(this.getActiveSession());
		return datasetDao;
	}

	public DatasetUsersDAO getDatasetUsersDao() {
		DatasetUsersDAO datasetUsersDao = new DatasetUsersDAO();
		datasetUsersDao.setSession(this.getActiveSession());
		return datasetUsersDao;
	}

	public MapDAO getMapDao() {
		MapDAO mapDao = new MapDAO();
		mapDao.setSession(this.getActiveSession());
		return mapDao;
	}

	public MappingDataDAO getMappingDataDao() {
		MappingDataDAO mappingDataDao = new MappingDataDAO();
		mappingDataDao.setSession(this.getActiveSession());
		return mappingDataDao;
	}

	public MappingPopDAO getMappingPopDao() {
		MappingPopDAO mappingPopDao = new MappingPopDAO();
		mappingPopDao.setSession(this.getActiveSession());
		return mappingPopDao;
	}

	public MappingPopValuesDAO getMappingPopValuesDao() {
		MappingPopValuesDAO mappingPopValuesDao = new MappingPopValuesDAO();
		mappingPopValuesDao.setSession(this.getActiveSession());
		return mappingPopValuesDao;
	}

	public MarkerAliasDAO getMarkerAliasDao() {
		MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
		markerAliasDao.setSession(this.getActiveSession());
		return markerAliasDao;
	}

	public MarkerDAO getMarkerDao() {
		MarkerDAO markerDao = new MarkerDAO();
		markerDao.setSession(this.getActiveSession());
		return markerDao;
	}

	public MarkerDetailsDAO getMarkerDetailsDao() {
		MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
		markerDetailsDao.setSession(this.getActiveSession());
		return markerDetailsDao;
	}

	public MarkerInfoDAO getMarkerInfoDao() {
		MarkerInfoDAO markerInfoDao = new MarkerInfoDAO();
		markerInfoDao.setSession(this.getActiveSession());
		return markerInfoDao;
	}

	public ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
		ExtendedMarkerInfoDAO extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
		extendedMarkerInfoDAO.setSession(this.getActiveSession());
		return extendedMarkerInfoDAO;
	}

	public MarkerMetadataSetDAO getMarkerMetadataSetDao() {
		MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
		markerMetadataSetDao.setSession(this.getActiveSession());
		return markerMetadataSetDao;
	}

	public MarkerOnMapDAO getMarkerOnMapDao() {
		MarkerOnMapDAO markerOnMapDao = new MarkerOnMapDAO();
		markerOnMapDao.setSession(this.getActiveSession());
		return markerOnMapDao;
	}

	public MarkerUserInfoDAO getMarkerUserInfoDao() {
		MarkerUserInfoDAO markerUserInfoDao = new MarkerUserInfoDAO();
		markerUserInfoDao.setSession(this.getActiveSession());
		return markerUserInfoDao;
	}

	public MarkerUserInfoDetailsDAO getMarkerUserInfoDetailsDao() {
		MarkerUserInfoDetailsDAO markerUserInfoDetailsDao = new MarkerUserInfoDetailsDAO();
		markerUserInfoDetailsDao.setSession(this.getActiveSession());
		return markerUserInfoDetailsDao;
	}

	public QtlDAO getQtlDao() {
		QtlDAO qtlDao = new QtlDAO();
		qtlDao.setSession(this.getActiveSession());
		return qtlDao;
	}

	public QtlDetailsDAO getQtlDetailsDao() {
		QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
		qtlDetailsDao.setSession(this.getActiveSession());
		return qtlDetailsDao;
	}

	public MtaDAO getMtaDao() {
		MtaDAO mtaDao = new MtaDAO();
		mtaDao.setSession(this.getActiveSession());
		return mtaDao;
	}

	public MtaMetadataDAO getMtaMetadataDao() {
		MtaMetadataDAO mtaMetadataDao = new MtaMetadataDAO();
		mtaMetadataDao.setSession(this.getActiveSession());
		return mtaMetadataDao;
	}

	public TrackDataDAO getTrackDataDao() {
		TrackDataDAO trackDataDao = new TrackDataDAO();
		trackDataDao.setSession(this.getActiveSession());
		return trackDataDao;
	}

	public TrackMarkerDAO getTrackMarkerDao() {
		TrackMarkerDAO trackMarkerDao = new TrackMarkerDAO();
		trackMarkerDao.setSession(this.getActiveSession());
		return trackMarkerDao;
	}

	public AttributeDAO getAttributeDao() {
		AttributeDAO attributeDao = new AttributeDAO();
		attributeDao.setSession(this.getActiveSession());
		return attributeDao;
	}

	public BibrefDAO getBibrefDao() {
		BibrefDAO bibrefDao = new BibrefDAO();
		bibrefDao.setSession(this.getActiveSession());
		return bibrefDao;
	}

	public GermplasmDAO getGermplasmDao() {
		GermplasmDAO germplasmDao = new GermplasmDAO();
		germplasmDao.setSession(this.getActiveSession());
		return germplasmDao;
	}

	public GermplasmSearchDAO getGermplasmSearchDao() {
		GermplasmSearchDAO germplasmSearchDao = new GermplasmSearchDAO();
		germplasmSearchDao.setSession(this.getActiveSession());
		return germplasmSearchDao;
	}

	public LocationDAO getLocationDao() {
		LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	public LocdesDAO getLocDesDao() {
		LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	public MethodDAO getMethodDao() {
		MethodDAO methodDao = new MethodDAO();
		methodDao.setSession(this.getActiveSession());
		return methodDao;
	}

	public ProgenitorDAO getProgenitorDao() {
		ProgenitorDAO progenitorDao = new ProgenitorDAO();
		progenitorDao.setSession(this.getActiveSession());
		return progenitorDao;
	}

	public UserDefinedFieldDAO getUserDefinedFieldDao() {
		UserDefinedFieldDAO userDefinedFieldDao = new UserDefinedFieldDAO();
		userDefinedFieldDao.setSession(this.getActiveSession());
		return userDefinedFieldDao;
	}

	public LocationDAO getLocationDAO() {
		LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	public LocdesDAO getLocdesDao() {
		LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		ProgramFavoriteDAO programFavoriteDao = new ProgramFavoriteDAO();
		programFavoriteDao.setSession(this.getActiveSession());
		return programFavoriteDao;
	}

	public GermplasmListDAO getGermplasmListDAO() {
		GermplasmListDAO germplasmListDao = new GermplasmListDAO();
		germplasmListDao.setSession(this.getActiveSession());
		return germplasmListDao;
	}

	public SampleListDao getSampleListDAO() {
		SampleListDao sampleListDao = new SampleListDao();
		sampleListDao.setSession(this.getActiveSession());
		return sampleListDao;
	}

	public GermplasmListDataDAO getGermplasmListDataDAO() {
		GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(this.getActiveSession());
		return germplasmListDataDao;
	}

	public ListDataPropertyDAO getListDataPropertyDAO() {
		ListDataPropertyDAO listDataPropertyDao = new ListDataPropertyDAO();
		listDataPropertyDao.setSession(this.getActiveSession());
		return listDataPropertyDao;
	}

	public ListDataProjectDAO getListDataProjectDAO() {
		ListDataProjectDAO listDataProjectDao = new ListDataProjectDAO();
		listDataProjectDao.setSession(this.getActiveSession());
		return listDataProjectDao;
	}

	public LotDAO getLotDao() {
		LotDAO lotDao = new LotDAO();
		lotDao.setSession(this.getActiveSession());
		return lotDao;
	}

	public PersonDAO getPersonDao() {
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

	public TermPropertyBuilder getTermPropertyBuilder() {
		return new TermPropertyBuilder(this.sessionProvider);
	}

	public SampleDao getSampleDao() {
		SampleDao sampleDao = new SampleDao();
		sampleDao.setSession(this.getActiveSession());
		return sampleDao;
	}

	// ================================ InventoryDataManager DAO Methods =============================
	public ProgramPresetDAO getProgramPresetDAO() {
		ProgramPresetDAO programPresetDAO = new ProgramPresetDAO();
		programPresetDAO.setSession(this.getActiveSession());
		return programPresetDAO;
	}

	public UserProgramTreeStateDAO getUserProgramTreeStateDAO() {
		UserProgramTreeStateDAO userProgramTreeStateDAO = new UserProgramTreeStateDAO();
		userProgramTreeStateDAO.setSession(this.getActiveSession());
		return userProgramTreeStateDAO;
	}

	public NamingConfigurationDAO getNamingConfigurationDAO() {
		final NamingConfigurationDAO namingConfigurationDAO = new NamingConfigurationDAO();
		namingConfigurationDAO.setSession(this.getActiveSession());
		return namingConfigurationDAO;
	}


	/**
	 * Parse hibernate query result value to boolean with null check
	 * 
	 * @param val value
	 * @return boolean
	 */
	protected boolean typeSafeObjectToBoolean(Object val) {
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
	protected Integer typeSafeObjectToInteger(Object val) {
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
