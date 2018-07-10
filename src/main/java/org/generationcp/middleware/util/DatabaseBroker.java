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

import org.generationcp.middleware.dao.*;
import org.generationcp.middleware.dao.dms.*;
import org.generationcp.middleware.dao.gdms.*;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.*;
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

	protected DatabaseBroker(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	protected DatabaseBroker(final HibernateSessionProvider sessionProvider, final String databaseName) {
		this.sessionProvider = sessionProvider;
		this.databaseName = databaseName;
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

	public CVTermDao getCvTermDao() {
		final CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.getActiveSession());
		return cvTermDao;
	}

	public StudyTypeDAO getStudyTypeDao() {
		final StudyTypeDAO studyTypeDAO = new StudyTypeDAO();
		studyTypeDAO.setSession(this.getActiveSession());
		return studyTypeDAO;
	}

	public StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.getActiveSession());
	}

	public BreedersQueryDao getBreedersQueryDao() {
		return new BreedersQueryDao(this.getActiveSession());
	}

	public CVTermRelationshipDao getCvTermRelationshipDao() {
		final CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.getActiveSession());
		return cvTermRelationshipDao;
	}

	public CountryDAO getCountryDao() {
		final CountryDAO countryDao = new CountryDAO();
		countryDao.setSession(this.getActiveSession());
		return countryDao;
	}

	public UserDAO getUserDao() {
		final UserDAO userDao = new UserDAO();
		userDao.setSession(this.getActiveSession());
		return userDao;
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

	public StudySearchDao getStudySearchDao() {
		final StudySearchDao studySearchDao = new StudySearchDao();
		studySearchDao.setSession(this.getActiveSession());
		return studySearchDao;
	}

	public LocationSearchDao getLocationSearchDao() {
		final LocationSearchDao dao = new LocationSearchDao();
		dao.setSession(this.getActiveSession());
		return dao;
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

	public ExperimentStockDao getExperimentStockDao() {
		final ExperimentStockDao experimentStockDao = new ExperimentStockDao();
		experimentStockDao.setSession(this.getActiveSession());
		return experimentStockDao;
	}

	public ProjectPropertyDao getProjectPropertyDao() {
		final ProjectPropertyDao projectPropertyDao = new ProjectPropertyDao();
		projectPropertyDao.setSession(this.getActiveSession());
		return projectPropertyDao;
	}

	public ProjectRelationshipDao getProjectRelationshipDao() {
		final ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(this.getActiveSession());
		return projectRelationshipDao;
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

	public CvTermPropertyDao getCvTermPropertyDao() {
		final CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.getActiveSession());
		return cvTermPropertyDao;
	}

	public VariableOverridesDao getVariableProgramOverridesDao() {
		final VariableOverridesDao variableOverridesDao = new VariableOverridesDao();
		variableOverridesDao.setSession(this.getActiveSession());
		return variableOverridesDao;
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		final CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.getActiveSession());
		return cvTermSynonymDao;
	}

	public NameDAO getNameDao() {
		final NameDAO nameDao = new NameDAO();
		nameDao.setSession(this.getActiveSession());
		return nameDao;
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

	public MarkerUserInfoDetailsDAO getMarkerUserInfoDetailsDao() {
		final MarkerUserInfoDetailsDAO markerUserInfoDetailsDao = new MarkerUserInfoDetailsDAO();
		markerUserInfoDetailsDao.setSession(this.getActiveSession());
		return markerUserInfoDetailsDao;
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

	public AttributeDAO getAttributeDao() {
		final AttributeDAO attributeDao = new AttributeDAO();
		attributeDao.setSession(this.getActiveSession());
		return attributeDao;
	}

	public BibrefDAO getBibrefDao() {
		final BibrefDAO bibrefDao = new BibrefDAO();
		bibrefDao.setSession(this.getActiveSession());
		return bibrefDao;
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

	public LocationDAO getLocationDao() {
		final LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	public LocdesDAO getLocDesDao() {
		final LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	public MethodDAO getMethodDao() {
		final MethodDAO methodDao = new MethodDAO();
		methodDao.setSession(this.getActiveSession());
		return methodDao;
	}

	public ProgenitorDAO getProgenitorDao() {
		final ProgenitorDAO progenitorDao = new ProgenitorDAO();
		progenitorDao.setSession(this.getActiveSession());
		return progenitorDao;
	}

	public UserDefinedFieldDAO getUserDefinedFieldDao() {
		final UserDefinedFieldDAO userDefinedFieldDao = new UserDefinedFieldDAO();
		userDefinedFieldDao.setSession(this.getActiveSession());
		return userDefinedFieldDao;
	}

	public LocationDAO getLocationDAO() {
		final LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.getActiveSession());
		return locationDao;
	}

	public LocdesDAO getLocdesDao() {
		final LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.getActiveSession());
		return locdesDao;
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		final ProgramFavoriteDAO programFavoriteDao = new ProgramFavoriteDAO();
		programFavoriteDao.setSession(this.getActiveSession());
		return programFavoriteDao;
	}

	public GermplasmListDAO getGermplasmListDAO() {
		final GermplasmListDAO germplasmListDao = new GermplasmListDAO();
		germplasmListDao.setSession(this.getActiveSession());
		return germplasmListDao;
	}

	public SampleListDao getSampleListDAO() {
		final SampleListDao sampleListDao = new SampleListDao();
		sampleListDao.setSession(this.getActiveSession());
		return sampleListDao;
	}

	public GermplasmListDataDAO getGermplasmListDataDAO() {
		final GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(this.getActiveSession());
		return germplasmListDataDao;
	}

	public ListDataPropertyDAO getListDataPropertyDAO() {
		final ListDataPropertyDAO listDataPropertyDao = new ListDataPropertyDAO();
		listDataPropertyDao.setSession(this.getActiveSession());
		return listDataPropertyDao;
	}

	public ListDataProjectDAO getListDataProjectDAO() {
		final ListDataProjectDAO listDataProjectDao = new ListDataProjectDAO();
		listDataProjectDao.setSession(this.getActiveSession());
		return listDataProjectDao;
	}

	public LotDAO getLotDao() {
		final LotDAO lotDao = new LotDAO();
		lotDao.setSession(this.getActiveSession());
		return lotDao;
	}

	public PersonDAO getPersonDao() {
		final PersonDAO personDao = new PersonDAO();
		personDao.setSession(this.getActiveSession());
		return personDao;
	}

	public TransactionDAO getTransactionDao() {
		final TransactionDAO transactionDao = new TransactionDAO();
		transactionDao.setSession(this.getActiveSession());
		return transactionDao;
	}

	public StockTransactionDAO getStockTransactionDAO() {
		final StockTransactionDAO stockTransactionDAO = new StockTransactionDAO();
		stockTransactionDAO.setSession(this.getActiveSession());
		return stockTransactionDAO;
	}

	public TermPropertyBuilder getTermPropertyBuilder() {
		return new TermPropertyBuilder(this.sessionProvider);
	}

	public SampleDao getSampleDao() {
		final SampleDao sampleDao = new SampleDao();
		sampleDao.setSession(this.getActiveSession());
		return sampleDao;
	}

	// ================================ InventoryDataManager DAO Methods =============================
	public ProgramPresetDAO getProgramPresetDAO() {
		final ProgramPresetDAO programPresetDAO = new ProgramPresetDAO();
		programPresetDAO.setSession(this.getActiveSession());
		return programPresetDAO;
	}

	public UserProgramTreeStateDAO getUserProgramTreeStateDAO() {
		final UserProgramTreeStateDAO userProgramTreeStateDAO = new UserProgramTreeStateDAO();
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
