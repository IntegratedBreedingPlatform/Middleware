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

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.InstallationDAO;
import org.generationcp.middleware.dao.ListDataPropertyDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.dao.LotDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.TransactionDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
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
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
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
    private MarkerMetadataSetDAO markerMetadataSetDao;
    private MarkerOnMapDAO markerOnMapDao;
    private MarkerUserInfoDAO markerUserInfoDao;
    private QtlDAO qtlDao;
    private QtlDetailsDAO qtlDetailsDao;
    private MtaDAO mtaDao;
    
    // GermplasmDataManager DAOs
    private AttributeDAO attributeDao;
    private BibrefDAO bibrefDao;
    private GermplasmDAO germplasmDao;
    private LocationDAO locationDao;
    private LocdesDAO locdesDao;
    private MethodDAO methodDao;
    private ProgenitorDAO progenitorDao;
    private UserDefinedFieldDAO userDefinedFieldDao;

    // GermplasmListDataManager DAOs
    private GermplasmListDAO germplasmListDao;
	private GermplasmListDataDAO germplasmListDataDao;
	private ListDataPropertyDAO listDataPropertyDao;
	
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
        if (sessionForCentral != null) {
            return sessionForCentral;
        } else if (sessionProviderForCentral != null) {
            return sessionProviderForCentral.getSession();
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the given database instance.
     * 
     * @param instance
     * @return
     * @throws MiddlewareQueryException
     *             if a {@link Session} for the specified database instance is not available
     */
    protected Session getSession(Database instance) throws MiddlewareQueryException {
        if (instance == Database.CENTRAL) {
        	this.activeDatabase = instance;
            Session session = getCurrentSessionForCentral();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.CENTRAL): The central instance was specified "
                        + "but there is no database connection for central provided.");
            }

            return session;
        } else if (instance == Database.LOCAL) {
        	this.activeDatabase = instance;
            Session session = getCurrentSessionForLocal();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.LOCAL): The local instance was specified "
                        + "but there is no database connection for local provided.");
            }

            return session;
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the specified <code>id</code>.
     * 
     * @param id
     * @return the {@link Session} for the central database if the specified
     *         <code>id</code> is positive or equal to zero, otherwise, this
     *         method returns the {@link Session} for the local database.
     * @throws MiddlewareQueryException
     */
    protected Session getSession(int id) {
        return id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
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
     * @return
     */
    protected Session getActiveSession() {
        return activeSession;
    }

    /**
     * Sets the current active session - either local or central.
     * @param session
     */
    protected void setActiveSession(Session session) {
        this.activeSession = session;
    }

    /**
     * Sets the active session based on the given instance. <br/>
     * Returns true if the active session is not null.      <br/>
     * @param instance 
     *          The database instance - either Database.LOCAL or Database.CENTRAL
     */
    protected boolean setWorkingDatabase(Database instance) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = instance;
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = instance;
        }
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given ID.   <br/>
     * If the id is positive, the central connection is assigned as the active session. <br/> 
     * If the id is negative, the local connection is assigned as the active session.  <br/>
     * Returns true if the active session is not null. <br/>
     * 
     * @param id
     */
    protected boolean setWorkingDatabase(Integer id) {
    	if (id != null) {
	        activeSession = id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
	        activeDatabase = id >= 0 ? Database.CENTRAL : Database.LOCAL;
	        if (activeSession != null) {
	            return true;
	        }
    	}
        return false;
    }

    /**
     * Sets the active session based on the session.     <br/>
     * Returns true if the active session is not null.   <br/>
     * 
     * @param session The session to assign
     */
    protected boolean setWorkingDatabase(Session session) {
        activeSession = session;
        activeDatabase = session == getCurrentSessionForCentral() ? Database.CENTRAL : Database.LOCAL;
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given instance.     <br/>
     * Returns true if the active session is not null.          <br/>
     * @param instance  The database instance - either Database.LOCAL or Database.CENTRAL
     * @param dao   The DAO to set the active session into
     */
    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Database instance, GenericDAO dao) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = instance;
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = instance;
        }
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
        if (id < 0) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = Database.LOCAL;
        } else if (id >= 0) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = Database.CENTRAL;
        }
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
}
