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
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.*;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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

    protected HibernateSessionProvider sessionProvider;
    protected String databaseName;
    private GermplasmListDAO germplasmListDao;

    protected static final int JDBC_BATCH_SIZE = 50;
  
    //StudyDataManager DAO
    private ProjectPropertyDao projectPropertyDao;

    protected DatabaseBroker(){
    	
    }

    protected DatabaseBroker(HibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    protected DatabaseBroker(HibernateSessionProvider sessionProvider, String databaseName) {
        this.sessionProvider = sessionProvider;
        this.databaseName = databaseName;
    }

    public HibernateSessionProvider getSessionProvider() {
        return sessionProvider;
    }
    
    public void setSessionProvider(HibernateSessionProvider sessionProvider) {
    	this.sessionProvider = sessionProvider;
    }
    
    public Session getCurrentSession() {
        return getActiveSession();
    }
    
    protected Session getActiveSession() {
    	if (sessionProvider != null) {
            return sessionProvider.getSession();
    	}
    	return null;
	}
    
 
    /**
     * Rolls back a given transaction
     * 
     * @param trans current transaction
     */
    public void rollbackTransaction(Transaction trans) {
        if (trans != null) {
            trans.rollback();
        }
    }


    protected DmsProjectDao getDmsProjectDao() {
	    DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}
    
    protected CVTermDao getCvTermDao() {
    	CVTermDao cvTermDao = new CVTermDao();
    	cvTermDao.setSession(getActiveSession());
		return cvTermDao;
	}

    protected StandardVariableDao getStandardVariableDao() {
    	return new StandardVariableDao(getActiveSession());
    }
    
    protected BreedersQueryDao getBreedersQueryDao() {
    	return new BreedersQueryDao(getActiveSession());
    }

    protected CVTermRelationshipDao getCvTermRelationshipDao() {
    	CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
    	cvTermRelationshipDao.setSession(getActiveSession());
		return cvTermRelationshipDao;
	}
    
    protected CountryDAO getCountryDao() {
    	CountryDAO countryDao = new CountryDAO();
    	countryDao.setSession(getActiveSession());
    	return countryDao;
    }
    
    protected UserDAO getUserDao() {
    	UserDAO userDao = new UserDAO();
    	userDao.setSession(getActiveSession());
    	return userDao;
    }
    
    protected CVDao getCvDao() {
    	CVDao cvDao = new CVDao();
    	cvDao.setSession(getActiveSession());
    	return cvDao;
    }
    
    protected StockDao getStockDao() {
    	StockDao stockDao = new StockDao();
    	stockDao.setSession(getActiveSession());
    	return stockDao;
    }
    
    protected StudySearchDao getStudySearchDao() {
    	StudySearchDao studySearchDao = new StudySearchDao();
    	studySearchDao.setSession(getActiveSession());
    	return studySearchDao;
    }
    
    protected LocationSearchDao getLocationSearchDao() {
    	LocationSearchDao dao = new LocationSearchDao();
    	dao.setSession(getActiveSession());
    	return dao;
    }
    
    protected GeolocationPropertyDao getGeolocationPropertyDao() {
    	GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
    	geolocationPropertyDao.setSession(getActiveSession());
    	return geolocationPropertyDao;
    }
    
    protected ExperimentDao getExperimentDao() {
    	ExperimentDao experimentDao = new ExperimentDao();
    	experimentDao.setSession(getActiveSession());
    	return experimentDao;
    }
    
    protected ExperimentPropertyDao getExperimentPropertyDao() {
    	ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
    	experimentPropertyDao.setSession(getActiveSession());
    	return experimentPropertyDao;
    }
    
    protected StockPropertyDao getStockPropertyDao() {
    	StockPropertyDao stockPropertyDao = new StockPropertyDao();
    	stockPropertyDao.setSession(getActiveSession());
    	return stockPropertyDao;
    }

    protected ExperimentStockDao getExperimentStockDao() {
    	ExperimentStockDao experimentStockDao = new ExperimentStockDao();
    	experimentStockDao.setSession(getActiveSession());
    	return experimentStockDao;
    }
    
    protected ExperimentProjectDao getExperimentProjectDao() {
    	ExperimentProjectDao experimentProjectDao = new ExperimentProjectDao();
    	experimentProjectDao.setSession(getActiveSession());
    	return experimentProjectDao;
    }
    
    public ProjectPropertyDao getProjectPropertyDao() {
    	 if (projectPropertyDao == null) {
             projectPropertyDao = new ProjectPropertyDao();
         }
		projectPropertyDao.setSession(getActiveSession());
		return projectPropertyDao;
	}
    
    protected ProjectRelationshipDao getProjectRelationshipDao() {
    	ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(getActiveSession());
		return projectRelationshipDao;
	}
    
    protected GeolocationDao getGeolocationDao() {
    	GeolocationDao geolocationDao = new GeolocationDao();
    	geolocationDao.setSession(getActiveSession());
    	return geolocationDao;
    }

    protected PhenotypeDao getPhenotypeDao() {
    	PhenotypeDao phenotypeDao = new PhenotypeDao();
    	phenotypeDao.setSession(getActiveSession());
    	return phenotypeDao;
    }
    
    protected PhenotypeOutlierDao getPhenotypeOutlierDao() {
    	PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
    	phenotypeOutlierDao.setSession(getActiveSession());
    	return phenotypeOutlierDao;
    }

    protected ExperimentPhenotypeDao getExperimentPhenotypeDao() {
    	ExperimentPhenotypeDao experimentPhenotypeDao = new ExperimentPhenotypeDao();
    	experimentPhenotypeDao.setSession(getActiveSession());
    	return experimentPhenotypeDao;
    }

    protected CvTermPropertyDao getCvTermPropertyDao() {
    	CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
    	cvTermPropertyDao.setSession(getActiveSession());
    	return cvTermPropertyDao;
    }

    protected final VariableProgramOverridesDao getVariableProgramOverridesDao() {
        VariableProgramOverridesDao variableProgramOverridesDao = new VariableProgramOverridesDao();
        variableProgramOverridesDao.setSession(getActiveSession());
        return variableProgramOverridesDao;
    }

    protected CvTermSynonymDao getCvTermSynonymDao() {
    	CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
    	cvTermSynonymDao.setSession(getActiveSession());
    	return cvTermSynonymDao;
    }
    
    protected NameDAO getNameDao() {
    	NameDAO nameDao = new NameDAO();
        nameDao.setSession(getActiveSession());
        return nameDao;
    }

    protected AccMetadataSetDAO getAccMetadataSetDao() {
    	AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
        accMetadataSetDao.setSession(getActiveSession());
        return accMetadataSetDao;
    }

    protected AlleleValuesDAO getAlleleValuesDao() {
    	AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
        alleleValuesDao.setSession(getActiveSession());
        return alleleValuesDao;
    }

    protected CharValuesDAO getCharValuesDao() {
    	CharValuesDAO  charValuesDao = new CharValuesDAO();
        charValuesDao.setSession(getActiveSession());
        return charValuesDao;
    }

    protected DartValuesDAO getDartValuesDao() {
    	DartValuesDAO dartValuesDao = new DartValuesDAO();
        dartValuesDao.setSession(getActiveSession());
        return dartValuesDao;
    }

    protected DatasetDAO getDatasetDao() {
    	DatasetDAO datasetDao = new DatasetDAO();
        datasetDao.setSession(getActiveSession());
        return datasetDao;
    }

    protected DatasetUsersDAO getDatasetUsersDao() {
    	DatasetUsersDAO    datasetUsersDao = new DatasetUsersDAO();
        datasetUsersDao.setSession(getActiveSession());
        return datasetUsersDao;
    }

    protected MapDAO getMapDao() {
    	MapDAO mapDao = new MapDAO();
        mapDao.setSession(getActiveSession());
        return mapDao;
    }

    protected MappingDataDAO getMappingDataDao() {
    	MappingDataDAO mappingDataDao = new MappingDataDAO();
        mappingDataDao.setSession(getActiveSession());
        return mappingDataDao;
    }

    protected MappingPopDAO getMappingPopDao() {
    	MappingPopDAO mappingPopDao = new MappingPopDAO();
        mappingPopDao.setSession(getActiveSession());
        return mappingPopDao;
    }

    protected MappingPopValuesDAO getMappingPopValuesDao() {
    	MappingPopValuesDAO mappingPopValuesDao = new MappingPopValuesDAO();
        mappingPopValuesDao.setSession(getActiveSession());
        return mappingPopValuesDao;
    }

    protected MarkerAliasDAO getMarkerAliasDao() {
    	MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
        markerAliasDao.setSession(getActiveSession());
        return markerAliasDao;
    }

    protected MarkerDAO getMarkerDao() {
    	MarkerDAO markerDao = new MarkerDAO();
        markerDao.setSession(getActiveSession());
        return markerDao;
    }

    protected MarkerDetailsDAO getMarkerDetailsDao() {
    	MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
        markerDetailsDao.setSession(getActiveSession());
        return markerDetailsDao;
    }

    protected MarkerInfoDAO getMarkerInfoDao() {
    	MarkerInfoDAO markerInfoDao = new MarkerInfoDAO();
        markerInfoDao.setSession(getActiveSession());
        return markerInfoDao;
    }

    protected ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
    	ExtendedMarkerInfoDAO extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
        extendedMarkerInfoDAO.setSession(getActiveSession());
        return extendedMarkerInfoDAO;
    }

    protected MarkerMetadataSetDAO getMarkerMetadataSetDao() {
    	MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
        markerMetadataSetDao.setSession(getActiveSession());
        return markerMetadataSetDao;
    }

    protected MarkerOnMapDAO getMarkerOnMapDao() {
    	MarkerOnMapDAO markerOnMapDao = new MarkerOnMapDAO();
        markerOnMapDao.setSession(getActiveSession());
        return markerOnMapDao;
    }

    protected MarkerUserInfoDAO getMarkerUserInfoDao() {
    	MarkerUserInfoDAO markerUserInfoDao = new MarkerUserInfoDAO();
        markerUserInfoDao.setSession(getActiveSession());
        return markerUserInfoDao;
    }
    
    protected MarkerUserInfoDetailsDAO getMarkerUserInfoDetailsDao() {
    	MarkerUserInfoDetailsDAO markerUserInfoDetailsDao = new MarkerUserInfoDetailsDAO();
        markerUserInfoDetailsDao.setSession(getActiveSession());
        return markerUserInfoDetailsDao;
    }

    protected QtlDAO getQtlDao() {
    	QtlDAO qtlDao = new QtlDAO();
        qtlDao.setSession(getActiveSession());
        return qtlDao;
    }

    protected QtlDetailsDAO getQtlDetailsDao() {
    	QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
        qtlDetailsDao.setSession(getActiveSession());
        return qtlDetailsDao;
    }
    
    protected MtaDAO getMtaDao() {
    	MtaDAO mtaDao = new MtaDAO();
        mtaDao.setSession(getActiveSession());
        return mtaDao;
    }

    protected MtaMetadataDAO getMtaMetadataDao() {
    	MtaMetadataDAO  mtaMetadataDao = new MtaMetadataDAO();
        mtaMetadataDao.setSession(getActiveSession());
        return mtaMetadataDao;
    }

    protected TrackDataDAO getTrackDataDao() {
    	TrackDataDAO trackDataDao = new TrackDataDAO();
        trackDataDao.setSession(getActiveSession());
        return trackDataDao;
    }

    protected TrackMarkerDAO getTrackMarkerDao() {
    	TrackMarkerDAO trackMarkerDao = new TrackMarkerDAO();
        trackMarkerDao.setSession(getActiveSession());
        return trackMarkerDao;
    }

    protected AttributeDAO getAttributeDao() {
    	AttributeDAO attributeDao = new AttributeDAO();
        attributeDao.setSession(getActiveSession());
        return attributeDao;
    }

    protected BibrefDAO getBibrefDao() {
    	BibrefDAO bibrefDao = new BibrefDAO();
        bibrefDao.setSession(getActiveSession());
        return bibrefDao;
    }

    protected GermplasmDAO getGermplasmDao() {
    	GermplasmDAO germplasmDao = new GermplasmDAO();
        germplasmDao.setSession(getActiveSession());
        return germplasmDao;
    }

    protected LocationDAO getLocationDao() {
    	LocationDAO  locationDao = new LocationDAO();
        locationDao.setSession(getActiveSession());
        return locationDao;
    }

    protected LocdesDAO getLocDesDao() {
    	LocdesDAO locdesDao = new LocdesDAO();
        locdesDao.setSession(getActiveSession());
        return locdesDao;
    }

    protected MethodDAO getMethodDao() {
    	MethodDAO methodDao = new MethodDAO();
        methodDao.setSession(getActiveSession());
        return methodDao;
    }

    protected ProgenitorDAO getProgenitorDao() {
    	ProgenitorDAO progenitorDao = new ProgenitorDAO();
        progenitorDao.setSession(getActiveSession());
        return progenitorDao;
    }

    protected UserDefinedFieldDAO getUserDefinedFieldDao() {
    	UserDefinedFieldDAO userDefinedFieldDao = new UserDefinedFieldDAO();
        userDefinedFieldDao.setSession(getActiveSession());
        return userDefinedFieldDao;
    }

    protected LocationDAO getLocationDAO() {
    	LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(getActiveSession());
		return locationDao;
	}    
    
    protected LocdesDAO getLocdesDao() {
    	LocdesDAO locdesDao = new LocdesDAO();
        locdesDao.setSession(getActiveSession());
        return locdesDao;
    }
    
	public ProgramFavoriteDAO getProgramFavoriteDao() {
		ProgramFavoriteDAO programFavoriteDao = new ProgramFavoriteDAO();
		programFavoriteDao.setSession(getActiveSession());
		return programFavoriteDao;
	}

    protected GermplasmListDAO getGermplasmListDAO() {
    	if(germplasmListDao == null) {
    		germplasmListDao = new GermplasmListDAO();
		}
		germplasmListDao.setSession(getActiveSession());
		return germplasmListDao;
	}
    
    public void setGermplasmListDao(GermplasmListDAO germplasmListDao) {
		this.germplasmListDao = germplasmListDao;
	}
	
	protected GermplasmListDataDAO getGermplasmListDataDAO() {
		GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(getActiveSession());
		return germplasmListDataDao;
	}

	protected ListDataPropertyDAO getListDataPropertyDAO() {
    	ListDataPropertyDAO listDataPropertyDao = new ListDataPropertyDAO();
		listDataPropertyDao.setSession(getActiveSession());
		return listDataPropertyDao;
	}
    
    protected ListDataProjectDAO getListDataProjectDAO() {
    	ListDataProjectDAO listDataProjectDao = new ListDataProjectDAO(); 
    	listDataProjectDao.setSession(getActiveSession());
    	return listDataProjectDao;
    }
    
    protected LotDAO getLotDao() {
    	LotDAO lotDao = new LotDAO();
        lotDao.setSession(getActiveSession());
        return lotDao;
    }

    protected PersonDAO getPersonDao() {
    	PersonDAO personDao = new PersonDAO();
        personDao.setSession(getActiveSession());
        return personDao;
    }

    public TransactionDAO getTransactionDao() {
    	TransactionDAO transactionDao = new TransactionDAO();
        transactionDao.setSession(getActiveSession());
        return transactionDao;
    }

     public StockTransactionDAO getStockTransactionDAO() {
         StockTransactionDAO stockTransactionDAO = new StockTransactionDAO();
         stockTransactionDAO.setSession(getActiveSession());
         return stockTransactionDAO;
     }
    
    protected TermPropertyBuilder getTermPropertyBuilder() {
        return new TermPropertyBuilder(sessionProvider);
    }

    //================================  InventoryDataManager DAO Methods =============================
    protected ProgramPresetDAO getProgramPresetDAO() {
    	ProgramPresetDAO programPresetDAO = new ProgramPresetDAO();
        programPresetDAO.setSession(getActiveSession());
        return programPresetDAO;
    }

    //===========================================================================================
    
    protected void clearSessions() {
    	if (sessionProvider != null) {
    		this.sessionProvider.getSession().clear();
    	}
    }
    
    protected void flushSessions() {
    	if (sessionProvider != null) {
    		this.sessionProvider.getSession().flush();
    	}
    }
	
	public void setProjectPropertyDao(ProjectPropertyDao projectPropertyDao){
		this.projectPropertyDao = projectPropertyDao;
	}

    /**
     * Parse hibernate query result value to boolean with null check
     * @param val value
     * @return boolean
     */
    protected boolean typeSafeObjectToBoolean(Object val){
        if(val == null) return false;
        if(val instanceof Integer) return (Integer) val != 0;
        if(val instanceof Boolean) return (Boolean) val;
        return false;
    }

    /**
     * Parse hibernate query result value to Integer with null check
     * @param val value
     * @return boolean
     */
    protected Integer typeSafeObjectToInteger(Object val) {
        if(val == null) return null;
        if(val instanceof Integer) return (Integer) val;
        if(val instanceof String ) return Integer.valueOf((String) val);
        throw new NumberFormatException("Can not cast " + val.getClass() + " to Integer for value: " + val);
    }
}
