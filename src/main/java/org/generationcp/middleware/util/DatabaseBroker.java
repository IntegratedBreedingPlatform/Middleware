/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.util;

import org.generationcp.middleware.dao.*;
import org.generationcp.middleware.dao.dms.*;
import org.generationcp.middleware.dao.gdms.*;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.operation.builder.TermPropertyBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Used to handle DAO instances and sessions connecting to the database.
 * Superclass of DataManager, Builder, Searcher and Saver classes.
 * Maintains session for local and central connection.
 * 
 * @author Joyce Avestro
 */

public class DatabaseBroker {

    public final static String NO_LOCAL_INSTANCE_MSG = "There is no connection to a local instance.";
    public final static String NO_CENTRAL_INSTANCE_MSG = "There is no connection to a central instance.";

    /**
     * The {@link HibernateSessionProvider} for local database.
     */
    protected HibernateSessionProvider sessionProviderForLocal;

    /**
     * The {@link HibernateSessionProvider} for central database.
     */
    protected HibernateSessionProvider sessionProviderForCentral;

    /**
     * Hibernate {@link Session} for local.
     */
    private Session sessionForLocal;

    /**
     * Hibernate {@link Session} for central.
     */
    private Session sessionForCentral;
    
    /**
     * The local database name
     */
    protected String localDatabaseName;

    /**
     * The central database name
     */
    protected String centralDatabaseName;

    /** The Constant JDBC_BATCH_SIZE. */
    protected static final int JDBC_BATCH_SIZE = 50;

    /**
     * Contains the current active session - either local or central.
     */
    private Session activeSession;
    
    private Database activeDatabase;
    
    
    // GDMS DAOs
    private NameDAO nameDao;
    private AccMetadataSetDAO accMetadataSetDao;
    private AlleleValuesDAO alleleValuesDao;
    private CharValuesDAO charValuesDao;
    private DartValuesDAO dartValuesDao;
    private DatasetDAO datasetDao;
    private DatasetUsersDAO datasetUsersDao;
    private MapDAO mapDao;
    private MappingDataDAO mappingDataDao;
    private MappingPopDAO mappingPopDao;
    private MappingPopValuesDAO mappingPopValuesDao;
    private MarkerAliasDAO markerAliasDao;
    private MarkerDAO markerDao;
    private MarkerDetailsDAO markerDetailsDao;
    private MarkerInfoDAO markerInfoDao;
    private ExtendedMarkerInfoDAO extendedMarkerInfoDAO;
    private MarkerMetadataSetDAO markerMetadataSetDao;
    private MarkerOnMapDAO markerOnMapDao;
    private MarkerUserInfoDAO markerUserInfoDao;
    private MarkerUserInfoDetailsDAO markerUserInfoDetailsDao;
    private QtlDAO qtlDao;
    private QtlDetailsDAO qtlDetailsDao;
    private MtaDAO mtaDao;
    private MtaMetadataDAO mtaMetadataDao;
    private TrackDataDAO trackDataDao;
    private TrackMarkerDAO trackMarkerDao;

    
    // GermplasmDataManager DAOs
    private AttributeDAO attributeDao;
    private BibrefDAO bibrefDao;
    private GermplasmDAO germplasmDao;
    private LocationDAO locationDao;
    private LocdesDAO locdesDao;
    private MethodDAO methodDao;
    private ProgenitorDAO progenitorDao;
    private UserDefinedFieldDAO userDefinedFieldDao;
    private ProgramFavoriteDAO programFavoriteDao;

    // GermplasmListDataManager DAOs
    private GermplasmListDAO germplasmListDao;
	private GermplasmListDataDAO germplasmListDataDao;
	private ListDataPropertyDAO listDataPropertyDao;
	private ListDataProjectDAO listDataProjectDao;
	
	// InventoryDataManager DAOs
    private LotDAO lotDao;
    private PersonDAO personDao;
    private TransactionDAO transactionDao;

    // UserDataManager DAOs
    private InstallationDAO installationDao;


    protected DatabaseBroker(){
    	
    }
    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    protected DatabaseBroker(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForLocal = sessionProviderForLocal;
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    protected DatabaseBroker(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral, String localDatabaseName, String centralDatabaseName) {
        this.sessionProviderForLocal = sessionProviderForLocal;
        this.sessionProviderForCentral = sessionProviderForCentral;
        this.localDatabaseName = localDatabaseName;
        this.centralDatabaseName = centralDatabaseName;
    }
    
    public DatabaseBroker(Session sessionForLocal, Session sessionForCentral) {
        this.sessionForLocal = sessionForLocal;
        this.sessionForCentral = sessionForCentral;
    }

    public HibernateSessionProvider getSessionProviderForLocal() {
        return sessionProviderForLocal;
    }
    
    public void setSessionProviderForLocal(HibernateSessionProvider sessionProviderForLocal){
    	this.sessionProviderForLocal = sessionProviderForLocal;
    }
    
    public HibernateSessionProvider getSessionProviderForCentral() {
        return sessionProviderForCentral;
    }

    public void setSessionProviderForCentral(HibernateSessionProvider sessionProviderForCentral){
    	this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Returns the current session for local if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForLocal() {
        if (sessionForLocal != null) {
            return sessionForLocal;
        } else if (sessionProviderForLocal != null) {
            return sessionProviderForLocal.getSession();
        }

        return null;
    }

    /**
     * Returns the current session for central if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForCentral() {
    	//HACK : Diverting all calls to get central session to only ever return the local session.
        return getCurrentSessionForLocal();
    }

    protected Database getActiveDatabase() {
    	return activeDatabase;
    }

    /**
     * Checks for the existence of a local database session. Throws an exception if not found.
     * 
     */
    protected Session requireLocalDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.LOCAL)) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }
        return getCurrentSessionForLocal();
    }

    /**
     * Checks for the existence of a central database session. Throws an exception if not found.
     * 
     */
    protected void requireCentralDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.CENTRAL)) {
            throw new MiddlewareQueryException(NO_CENTRAL_INSTANCE_MSG);
        }
    }

    /**
     * Sets the session of a given DAO. Returns true if successful. 
     * 
     */
    @SuppressWarnings("rawtypes")
	protected boolean setDaoSession(GenericDAO dao, Session session) {
        if (session != null) {
            dao.setSession(session);
            return true;
        }
        return false;
    }
    
	@SuppressWarnings("rawtypes")
	protected boolean setDaoSession(GenericDAO dao, Integer id) {
		if (setWorkingDatabase(id)) {
			dao.setSession(activeSession);
			return true;
		}
		return false;
	}

    /** 
     * Retrieves the current active session - either local or central database connection.
     * 
     * @return The current active session
     */
    protected Session getActiveSession() {
        return activeSession;
    }

    protected boolean setWorkingDatabase(Database instance) {
        //HACK: Hard codding to only ever return the same (local) db session for all operations for the single (merged) DB scheme.
    	activeSession = getCurrentSessionForLocal();
        activeDatabase = Database.LOCAL;
        
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    protected boolean setWorkingDatabase(Integer id) {
    	if (id != null) {
    		//HACK: Hard codding to only ever return the same (local) db session for all operations for the single (merged) DB scheme.
	        activeSession = getCurrentSessionForLocal();
	        activeDatabase = Database.LOCAL;
	        if (activeSession != null) {
	            return true;
	        }
    	}
        return false;
    }

    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Database instance, GenericDAO dao) {
        
    	//HACK: Hard codding to only ever return the same (local) db session for all operations for the single (merged) DB scheme.
    	activeSession = getCurrentSessionForLocal();
        activeDatabase = Database.LOCAL;
        
        if (activeSession != null) {
            return setDaoSession(dao, activeSession);
        }
        return false;
    }

    /**
     * Sets the active session based on the given instance.         <br/>
     * Returns true if the active session is not null.              <br/>
     * @param id
     *          If the given id is positive, the session is set to Central.
     *          If the given id is negative, the session is set to Local.
     * @param dao 
     *          The DAO to set the active session into
     */
    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Integer id, GenericDAO dao) {
    	//HACK: Hard codding to only ever return the same (local) db session for all operations for the single (merged) DB scheme.
    	activeSession = getCurrentSessionForLocal();
        activeDatabase = Database.LOCAL;
        if (activeSession != null) {
            return setDaoSession(dao, activeSession);
        }
        return false;
    }
    
    /**
     * Rolls back a given transaction
     * 
     * @param trans
     */
    public void rollbackTransaction(Transaction trans) {
        if (trans != null) {
            trans.rollback();
        }
    }


    //================================  StudyDataManagerv2 DAO Methods =============================

    protected final DmsProjectDao getDmsProjectDao() {
	    DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}
    
    protected final CVTermDao getCvTermDao() {
    	CVTermDao cvTermDao = new CVTermDao();
    	cvTermDao.setSession(getActiveSession());
		return cvTermDao;
	}
    
    protected final StandardVariableDao getStandardVariableDao() {
    	return new StandardVariableDao(getActiveSession());
    }
    
    protected final BreedersQueryDao getBreedersQueryDao() {
    	return new BreedersQueryDao(getActiveSession());
    }

    protected final CVTermRelationshipDao getCvTermRelationshipDao() {
    	CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
    	cvTermRelationshipDao.setSession(getActiveSession());
		return cvTermRelationshipDao;
	}
    
    protected final CountryDAO getCountryDao() {
    	CountryDAO countryDao = new CountryDAO();
    	countryDao.setSession(getActiveSession());
    	return countryDao;
    }
    
    protected final UserDAO getUserDao() {
    	UserDAO userDao = new UserDAO();
    	userDao.setSession(getActiveSession());
    	return userDao;
    }
    
    protected final CVDao getCvDao() {
    	CVDao cvDao = new CVDao();
    	cvDao.setSession(getActiveSession());
    	return cvDao;
    }
    
    protected final StockDao getStockDao() {
    	StockDao stockDao = new StockDao();
    	stockDao.setSession(getActiveSession());
    	return stockDao;
    }
    
    protected final StudySearchDao getStudySearchDao() {
    	StudySearchDao studySearchDao = new StudySearchDao();
    	studySearchDao.setSession(getActiveSession());
    	return studySearchDao;
    }
    
    protected final LocationSearchDao getLocationSearchDao() {
    	LocationSearchDao dao = new LocationSearchDao();
    	dao.setSession(getActiveSession());
    	return dao;
    }
    
    protected final GeolocationPropertyDao getGeolocationPropertyDao() {
    	GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
    	geolocationPropertyDao.setSession(getActiveSession());
    	return geolocationPropertyDao;
    }
    
    protected final ExperimentDao getExperimentDao() {
    	ExperimentDao experimentDao = new ExperimentDao();
    	experimentDao.setSession(getActiveSession());
    	return experimentDao;
    }
    
    protected final ExperimentPropertyDao getExperimentPropertyDao() {
    	ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
    	experimentPropertyDao.setSession(getActiveSession());
    	return experimentPropertyDao;
    }
    
    protected final StockPropertyDao getStockPropertyDao() {
    	StockPropertyDao stockPropertyDao = new StockPropertyDao();
    	stockPropertyDao.setSession(getActiveSession());
    	return stockPropertyDao;
    }

    protected final ExperimentStockDao getExperimentStockDao() {
    	ExperimentStockDao experimentStockDao = new ExperimentStockDao();
    	experimentStockDao.setSession(getActiveSession());
    	return experimentStockDao;
    }
    
    protected final ExperimentProjectDao getExperimentProjectDao() {
    	ExperimentProjectDao experimentProjectDao = new ExperimentProjectDao();
    	experimentProjectDao.setSession(getActiveSession());
    	return experimentProjectDao;
    }
    
    protected final ProjectPropertyDao getProjectPropertyDao() {
    	ProjectPropertyDao projectPropertyDao = new ProjectPropertyDao();
		projectPropertyDao.setSession(getActiveSession());
		return projectPropertyDao;
	}
    
    protected final ProjectRelationshipDao getProjectRelationshipDao() {
    	ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(getActiveSession());
		return projectRelationshipDao;
	}
    
    protected final GeolocationDao getGeolocationDao() {
    	GeolocationDao geolocationDao = new GeolocationDao();
    	geolocationDao.setSession(getActiveSession());
    	return geolocationDao;
    }

    protected final PhenotypeDao getPhenotypeDao() {
    	PhenotypeDao phenotypeDao = new PhenotypeDao();
    	phenotypeDao.setSession(getActiveSession());
    	return phenotypeDao;
    }
    
    protected final PhenotypeOutlierDao getPhenotypeOutlierDao() {
    	PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
    	phenotypeOutlierDao.setSession(getActiveSession());
    	return phenotypeOutlierDao;
    }

    protected final ExperimentPhenotypeDao getExperimentPhenotypeDao() {
    	ExperimentPhenotypeDao experimentPhenotypeDao = new ExperimentPhenotypeDao();
    	experimentPhenotypeDao.setSession(getActiveSession());
    	return experimentPhenotypeDao;
    }

    protected final CvTermPropertyDao getCvTermPropertyDao() {
    	CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
    	cvTermPropertyDao.setSession(getActiveSession());
    	return cvTermPropertyDao;
    }

    protected final CvTermSynonymDao getCvTermSynonymDao() {
    	CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
    	cvTermSynonymDao.setSession(getActiveSession());
    	return cvTermSynonymDao;
    }
    
    //================================  GDMS DAO Methods =============================
    
    protected final NameDAO getNameDao() {
        if (nameDao == null) {
            nameDao = new NameDAO();
        }
        nameDao.setSession(getActiveSession());
        return nameDao;
    }

    protected final AccMetadataSetDAO getAccMetadataSetDao() {
        if (accMetadataSetDao == null) {
            accMetadataSetDao = new AccMetadataSetDAO();
        }
        accMetadataSetDao.setSession(getActiveSession());
        return accMetadataSetDao;
    }

    protected final AlleleValuesDAO getAlleleValuesDao() {
        if (alleleValuesDao == null) {
            alleleValuesDao = new AlleleValuesDAO();
        }
        alleleValuesDao.setSession(getActiveSession());
        return alleleValuesDao;
    }

    protected final CharValuesDAO getCharValuesDao() {
        if (charValuesDao == null) {
            charValuesDao = new CharValuesDAO();
        }
        charValuesDao.setSession(getActiveSession());
        return charValuesDao;
    }

    protected final DartValuesDAO getDartValuesDao() {
        if (dartValuesDao == null) {
            dartValuesDao = new DartValuesDAO();
        }
        dartValuesDao.setSession(getActiveSession());
        return dartValuesDao;
    }

    protected final DatasetDAO getDatasetDao() {
        if (datasetDao == null) {
            datasetDao = new DatasetDAO();
        }
        datasetDao.setSession(getActiveSession());
        return datasetDao;
    }

    protected final DatasetUsersDAO getDatasetUsersDao() {
        if (datasetUsersDao == null) {
            datasetUsersDao = new DatasetUsersDAO();
        }
        datasetUsersDao.setSession(getActiveSession());
        return datasetUsersDao;
    }

    protected final MapDAO getMapDao() {
        if (mapDao == null) {
            mapDao = new MapDAO();
        }
        mapDao.setSession(getActiveSession());
        return mapDao;
    }

    protected final MappingDataDAO getMappingDataDao() {
        if (mappingDataDao == null) {
            mappingDataDao = new MappingDataDAO();
        }
        mappingDataDao.setSession(getActiveSession());
        return mappingDataDao;
    }

    protected final MappingPopDAO getMappingPopDao() {
        if (mappingPopDao == null) {
            mappingPopDao = new MappingPopDAO();
        }
        mappingPopDao.setSession(getActiveSession());
        return mappingPopDao;
    }

    protected final MappingPopValuesDAO getMappingPopValuesDao() {
        if (mappingPopValuesDao == null) {
            mappingPopValuesDao = new MappingPopValuesDAO();
        }
        mappingPopValuesDao.setSession(getActiveSession());
        return mappingPopValuesDao;
    }

    protected final MarkerAliasDAO getMarkerAliasDao() {
        if (markerAliasDao == null) {
            markerAliasDao = new MarkerAliasDAO();
        }
        markerAliasDao.setSession(getActiveSession());
        return markerAliasDao;
    }

    protected final MarkerDAO getMarkerDao() {
        if (markerDao == null) {
            markerDao = new MarkerDAO();
        }
        markerDao.setSession(getActiveSession());
        return markerDao;
    }

    protected final MarkerDetailsDAO getMarkerDetailsDao() {
        if (markerDetailsDao == null) {
            markerDetailsDao = new MarkerDetailsDAO();
        }
        markerDetailsDao.setSession(getActiveSession());
        return markerDetailsDao;
    }

    protected final MarkerInfoDAO getMarkerInfoDao() {
        if (markerInfoDao == null) {
            markerInfoDao = new MarkerInfoDAO();
        }
        markerInfoDao.setSession(getActiveSession());
        return markerInfoDao;
    }

    protected final ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
        if (extendedMarkerInfoDAO == null) {
            extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
        }

        extendedMarkerInfoDAO.setSession(getActiveSession());
        return extendedMarkerInfoDAO;
    }

    protected final MarkerMetadataSetDAO getMarkerMetadataSetDao() {
        if (markerMetadataSetDao == null) {
            markerMetadataSetDao = new MarkerMetadataSetDAO();
        }
        markerMetadataSetDao.setSession(getActiveSession());
        return markerMetadataSetDao;
    }

    protected final MarkerOnMapDAO getMarkerOnMapDao() {
        if (markerOnMapDao == null) {
            markerOnMapDao = new MarkerOnMapDAO();
        }
        markerOnMapDao.setSession(getActiveSession());
        return markerOnMapDao;
    }

    protected final MarkerUserInfoDAO getMarkerUserInfoDao() {
        if (markerUserInfoDao == null) {
            markerUserInfoDao = new MarkerUserInfoDAO();
        }
        markerUserInfoDao.setSession(getActiveSession());
        return markerUserInfoDao;
    }
    
    protected final MarkerUserInfoDetailsDAO getMarkerUserInfoDetailsDao() {
        if (markerUserInfoDetailsDao == null) {
        	markerUserInfoDetailsDao = new MarkerUserInfoDetailsDAO();
        }
        markerUserInfoDetailsDao.setSession(getActiveSession());
        return markerUserInfoDetailsDao;
    }

    protected final QtlDAO getQtlDao() {
        if (qtlDao == null) {
            qtlDao = new QtlDAO();
        }
        qtlDao.setSession(getActiveSession());
        return qtlDao;
    }

    protected final QtlDetailsDAO getQtlDetailsDao() {
        if (qtlDetailsDao == null) {
            qtlDetailsDao = new QtlDetailsDAO();
        }
        qtlDetailsDao.setSession(getActiveSession());
        return qtlDetailsDao;
    }
    
    protected final MtaDAO getMtaDao() {
        if (mtaDao == null) {
            mtaDao = new MtaDAO();
        }
        mtaDao.setSession(getActiveSession());
        return mtaDao;
    }

    protected final MtaMetadataDAO getMtaMetadataDao() {
        if (mtaMetadataDao == null) {
            mtaMetadataDao = new MtaMetadataDAO();
        }
        mtaMetadataDao.setSession(getActiveSession());
        return mtaMetadataDao;
    }

    protected final TrackDataDAO getTrackDataDao() {
        if (trackDataDao == null) {
        	trackDataDao = new TrackDataDAO();
        }
        trackDataDao.setSession(getActiveSession());
        return trackDataDao;
    }

    protected final TrackMarkerDAO getTrackMarkerDao() {
        if (trackMarkerDao == null) {
        	trackMarkerDao = new TrackMarkerDAO();
        }
        trackMarkerDao.setSession(getActiveSession());
        return trackMarkerDao;
    }




    //================================ GermplasmDataManager DAO Methods =============================
    
    
    protected final AttributeDAO getAttributeDao() {
        if (attributeDao == null) {
            attributeDao = new AttributeDAO();
        }
        attributeDao.setSession(getActiveSession());
        return attributeDao;
    }

    protected final BibrefDAO getBibrefDao() {
        if (bibrefDao == null) {
            bibrefDao = new BibrefDAO();
        }
        bibrefDao.setSession(getActiveSession());
        return bibrefDao;
    }

    protected final GermplasmDAO getGermplasmDao() {
        if (germplasmDao == null) {
            germplasmDao = new GermplasmDAO();
        }
        germplasmDao.setSession(getActiveSession());
        return germplasmDao;
    }

    protected final LocationDAO getLocationDao() {
        if (locationDao == null) {
            locationDao = new LocationDAO();
        }
        locationDao.setSession(getActiveSession());
        return locationDao;
    }

    protected final LocdesDAO getLocDesDao() {
        if (locdesDao == null) {
            locdesDao = new LocdesDAO();
        }
        locdesDao.setSession(getActiveSession());
        return locdesDao;
    }

    protected final MethodDAO getMethodDao() {
        if (methodDao == null) {
            methodDao = new MethodDAO();
        }
        methodDao.setSession(getActiveSession());
        return methodDao;
    }

    protected final ProgenitorDAO getProgenitorDao() {
        if (progenitorDao == null) {
            progenitorDao = new ProgenitorDAO();
        }
        progenitorDao.setSession(getActiveSession());
        return progenitorDao;
    }

    protected final UserDefinedFieldDAO getUserDefinedFieldDao() {
        if (userDefinedFieldDao == null) {
            userDefinedFieldDao = new UserDefinedFieldDAO();
        }
        userDefinedFieldDao.setSession(getActiveSession());
        return userDefinedFieldDao;
    }

    protected final LocationDAO getLocationDAO() {
		if (locationDao == null) {
			locationDao = new LocationDAO();
		}
		locationDao.setSession(getActiveSession());
		return locationDao;
	}    
    
    protected final LocdesDAO getLocdesDao() {
        if (locdesDao == null) {
            locdesDao = new LocdesDAO();
        }
        locdesDao.setSession(getActiveSession());
        return locdesDao;
    }
    
    public ProgramFavoriteDAO getProgramFavoriteDao() {
		 if (programFavoriteDao == null) {
			 	programFavoriteDao = new ProgramFavoriteDAO();
	        }
		 programFavoriteDao.setSession(getActiveSession());
	        return programFavoriteDao;
	}

    //================================ GermplasmListDataManager DAO Methods =============================
    
    protected final GermplasmListDAO getGermplasmListDAO() {
		if (germplasmListDao == null) {
			germplasmListDao = new GermplasmListDAO();
		}
		germplasmListDao.setSession(getActiveSession());
		return germplasmListDao;
	}
	
	protected final GermplasmListDataDAO getGermplasmListDataDAO() {
		if (germplasmListDataDao == null) {
			germplasmListDataDao = new GermplasmListDataDAO();
		}
		germplasmListDataDao.setSession(getActiveSession());
		return germplasmListDataDao;
	}
	
    
    protected final ListDataPropertyDAO getListDataPropertyDAO() {
		if (listDataPropertyDao == null) {
			listDataPropertyDao = new ListDataPropertyDAO();
		}
		listDataPropertyDao.setSession(getActiveSession());
		return listDataPropertyDao;
	}
    
    protected final ListDataProjectDAO getListDataProjectDAO() {
    	if (listDataProjectDao == null) {
    		listDataProjectDao = new ListDataProjectDAO(); 
    	}
    	listDataProjectDao.setSession(getActiveSession());
    	return listDataProjectDao;
    }

    //================================  InventoryDataManager DAO Methods =============================
	
    protected final LotDAO getLotDao() {
        if (lotDao == null) {
            lotDao = new LotDAO();
        }
        lotDao.setSession(getActiveSession());
        return lotDao;
    }

    protected final PersonDAO getPersonDao() {
        if (personDao == null) {
            personDao = new PersonDAO();
        }
        personDao.setSession(getActiveSession());
        return personDao;
    }

    protected final TransactionDAO getTransactionDao() {
        if (transactionDao == null) {
            transactionDao = new TransactionDAO();
        }
        transactionDao.setSession(getActiveSession());
        return transactionDao;
    }    

    
    //================================  UserDataManager DAO Methods =============================


    protected final InstallationDAO getInstallationDao() {
        if (installationDao == null) {
            installationDao = new InstallationDAO();
        }
        installationDao.setSession(getActiveSession());
        return installationDao;
    }

    //===========================================================================================
    
    protected final void clearSessions() {
    	if (sessionForLocal != null) {
    		this.sessionForLocal.clear();
    	}
    	
    	if (sessionForCentral != null) {
    		this.sessionForCentral.clear();
    	}
    }
    
    protected final void flushSessions() {
    	if (sessionForLocal != null) {
    		this.sessionForLocal.flush();
    	}
    	
    	if (sessionForCentral != null) {
    		this.sessionForCentral.flush();
    	}
    }
    
    protected final TermPropertyBuilder getTermPropertyBuilder() {
        return new TermPropertyBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
	
	
}
