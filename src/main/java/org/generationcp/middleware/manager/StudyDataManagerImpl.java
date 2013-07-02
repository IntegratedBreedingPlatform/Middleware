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
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.CharacterDataDAO;
import org.generationcp.middleware.dao.CharacterLevelDAO;
import org.generationcp.middleware.dao.FactorDAO;
import org.generationcp.middleware.dao.NumericDataDAO;
import org.generationcp.middleware.dao.NumericLevelDAO;
import org.generationcp.middleware.dao.OindexDAO;
import org.generationcp.middleware.dao.RepresentationDAO;
import org.generationcp.middleware.dao.StudyDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterLevel;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.DatasetCondition;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericLevel;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.StudyInfo;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the StudyDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 */ 
@SuppressWarnings("unchecked")
public class StudyDataManagerImpl extends DataManager implements StudyDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

    public StudyDataManagerImpl() {
    }

    public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }


    @Override
    public List<Integer> getGIDSByPhenotypicData(
    		List<TraitCombinationFilter> filters, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        // TODO Local-Central: Verify if existing implementation for CENTRAL is  also applicable to LOCAL
        List<Integer> toReturn = new ArrayList<Integer>();

        if (setWorkingDatabase(instance)) {
            NumericDataDAO dataNDao = getNumericDataDao();
            CharacterDataDAO dataCDao = getCharacterDataDao();
            Set<Integer> ounitIds = new HashSet<Integer>();

            // first get the observation unit ids for the rows in datasets which has the data specified in the filter
            // check numeric data
            ounitIds.addAll(dataNDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));
            // check character data
            ounitIds.addAll(dataCDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));

            // use the retrieved observation unit ids to get the GIDs being
            // observed in the rows in datasets identified by the observation unit ids
            if (!ounitIds.isEmpty()) {
                FactorDAO factorDao = getFactorDao();
                Set<Integer> gids = factorDao.getGIDSByObservationUnitIds(ounitIds, start, numOfRows * 2);
                toReturn.addAll(gids);
            }
        }
        return toReturn;
    }

    @Override
    public List<Study> getStudyByName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {

        List<Study> studyList = new ArrayList<Study>();
        try {
            if (setWorkingDatabase(instance)) {
                StudyDAO dao = getStudyDao();
                if (op == Operation.EQUAL) {
                    studyList = dao.getByNameUsingEqual(name, start, numOfRows);
                } else if (op == Operation.LIKE) {
                    studyList = dao.getByNameUsingLike(name, start, numOfRows);
                }
            }
        } catch (Exception e) {
            logAndThrowException("Error in getStudyByName(name = " + name + ")", e, LOG);
        }
        return studyList;
    }

    @Override
    public long countStudyByName(String name, Operation op, Database instance) throws MiddlewareQueryException {
    	
    	return countFromInstanceByMethod(getStudyDao(), instance, "countByName", 
    				new Object[] {name, op}, new Class[] {String.class, Operation.class});
    }

    @Override
    public List<Study> getStudyBySeason(Season season, int start, int numOfRows, Database instance) 
    		throws MiddlewareQueryException {
    	
    	return (List<Study>) getFromInstanceByMethod(getStudyDao(), instance, "getBySeason", 
    				new Object[] {season,  start, numOfRows}, new Class[] {Season.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countStudyBySeason(Season season, Database instance) throws MiddlewareQueryException {
    	
    	return countFromInstanceByMethod(getStudyDao(), instance, "countBySeason", new Object[] {season}, 
    			new Class[] {Season.class});
    }

    @Override
    public List<Study> getStudyBySDate(Integer sdate, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Study> studyList = new ArrayList<Study>();
        if (setWorkingDatabase(instance)) {
            if (op == Operation.EQUAL) {
                studyList = getStudyDao().getBySDateUsingEqual(sdate, start, numOfRows);
            }
        }
        return studyList;

    }

    @Override
    public long countStudyBySDate(Integer sdate, Operation op, Database instance) throws MiddlewareQueryException {
    	
    	return countFromInstanceByMethod(getStudyDao(), instance, "countBySDate", 
    				new Object[] {sdate, op}, new Class[] {Integer.class, Operation.class});
    }

    @Override
    public List<Study> getStudyByEDate(Integer edate, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Study> studyList = new ArrayList<Study>();
        if (setWorkingDatabase(instance)) {
            if (op == Operation.EQUAL) {
                studyList = getStudyDao().getByEDateUsingEqual(edate, start, numOfRows);
            }
        }
        return studyList;
    }

    @Override
    public long countStudyByEDate(Integer edate, Operation op, Database instance) throws MiddlewareQueryException {
    	
    	return countFromInstanceByMethod(getStudyDao(), instance, "countByEDate", 
    				new Object[] {edate,  op}, new Class[] {Integer.class, Operation.class});
    }

    @Override
    public List<Study> getStudyByCountry(String country, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
    	
    	String methodName = op == Operation.LIKE ? "getByCountryUsingLike" : "getByCountryUsingEqual";
    	return (List<Study>) getFromInstanceByMethod(getStudyDao(), instance, methodName, 
    				new Object[] {country, start, numOfRows}, new Class[] {String.class, Integer.TYPE, Integer.TYPE});
    	
        /*List<Study> studyList = new ArrayList<Study>();
        if (setWorkingDatabase(instance)) {
            StudyDAO dao = getStudyDao();
            if (op == Operation.EQUAL) {
                studyList = dao.getByCountryUsingEqual(country, start, numOfRows);
            } else if (op == Operation.LIKE) {
                studyList = dao.getByCountryUsingLike(country, start, numOfRows);
            }
        }
        return studyList;*/
    }

    @Override
    public long countStudyByCountry(String country, Operation op, Database instance) throws MiddlewareQueryException {
    	
    	return countFromInstanceByMethod(getStudyDao(), instance, "countByCountry", 
    				new Object[] {country,  op}, new Class[] {String.class, Operation.class});
    }

    @Override
    public Study getStudyByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return (Study) getStudyDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Study> getAllTopLevelStudies(int start, int numOfRows, Database instance) throws MiddlewareQueryException {

    	return (List<Study>) getFromInstanceByMethod(getStudyDao(), instance, "getTopLevelStudies", 
    				new Object[] {start, numOfRows}, new Class[] {Integer.TYPE, Integer.TYPE});
    }

    public long countAllTopLevelStudies(Database instance) throws MiddlewareQueryException {

    	return countFromInstanceByMethod(getStudyDao(), instance, "countAllTopLevelStudies", null, null);
    }

    @Override
    public long countAllStudyByParentFolderID(Integer parentFolderId, Database instance) throws MiddlewareQueryException {

    	return countFromInstanceByMethod(getStudyDao(), instance, "countAllStudyByParentFolderID", 
    				new Object[] {parentFolderId}, new Class[] {Integer.class});
    }

    @Override
    public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws MiddlewareQueryException {

    	return (List<Study>) getFromInstanceByIdAndMethod(getStudyDao(), parentFolderId, "getByParentFolderID", 
    				new Object[] {parentFolderId, start, numOfRows}, 
    				new Class[] {Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public List<Variate> getVariatesByStudyID(Integer studyId) throws MiddlewareQueryException {
    	
    	return (List<Variate>) getFromInstanceByIdAndMethod(getVariateDao(), studyId, 
    			"getByStudyID", new Object[] {studyId}, new Class[] {Integer.class});
    }

    @Override
    public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws MiddlewareQueryException {

    	return (List<StudyEffect>) getFromInstanceByIdAndMethod(getStudyEffectDao(), studyId, 
    			"getByStudyID", new Object[] {studyId}, new Class[] {Integer.class});
    }

    @Override
    public List<Factor> getFactorsByStudyID(Integer studyId) throws MiddlewareQueryException {
    	
    	return (List<Factor>) getFromInstanceByIdAndMethod(getFactorDao(), studyId, 
    			"getByStudyID", new Object[] {studyId}, new Class[] {Integer.class});
    }

    @Override
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException {
    	
    	return (List<Representation>) getFromInstanceByIdAndMethod(getRepresentationDao(), effectId, 
    			"getRepresentationByEffectID", new Object[] {effectId}, new Class[] {Integer.class});
    }

    @Override
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException {
    	
    	return (List<Representation>) getFromInstanceByIdAndMethod(getRepresentationDao(), studyId, 
    			"getRepresentationByStudyID", new Object[] {studyId}, new Class[] {Integer.class});
    }

    @Override
    public List<Factor> getFactorsByRepresentationId(Integer representationId) throws MiddlewareQueryException {

    	return (List<Factor>) getFromInstanceByIdAndMethod(getFactorDao(), representationId, "getByRepresentationID", 
    				new Object[] {representationId}, new Class[] {Integer.class});
    }

    @Override
    public long countOunitIDsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
    	
    	return countFromInstanceByIdAndMethod(getOindexDao(), representationId, "countOunitIDsByRepresentationId", 
    				new Object[] {representationId}, new Class[] {Integer.class});
    }

    @Override
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) 
    		throws MiddlewareQueryException {
    	
    	return (List<Integer>) getFromInstanceByIdAndMethod(getOindexDao(), representationId, "getOunitIDsByRepresentationId", 
    				new Object[] {representationId, start, numOfRows},
    				new Class[] {Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public List<Variate> getVariatesByRepresentationId(Integer representationId) throws MiddlewareQueryException {

    	return (List<Variate>) getFromInstanceByIdAndMethod(getVariateDao(), representationId, "getByRepresentationId", 
    				new Object[] {representationId}, new Class[] {Integer.class});
    }

    @Override
    public List<NumericDataElement> getNumericDataValuesByOunitIdList(List<Integer> ounitIdList) 
    		throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        return (List<NumericDataElement>) getFromInstanceByIdAndMethod(getNumericDataDao(), sampleId, "getValuesByOunitIDList", 
        			new Object[] {ounitIdList}, new Class[] {List.class});
    }

    @Override
    public List<CharacterDataElement> getCharacterDataValuesByOunitIdList(List<Integer> ounitIdList) 
    		throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        return (List<CharacterDataElement>)getFromInstanceByIdAndMethod(getCharacterDataDao(), sampleId, 
        		"getValuesByOunitIDList", new Object[] {ounitIdList}, new Class[] {List.class});
    }

    @Override
    public List<NumericLevelElement> getNumericLevelValuesByOunitIdList(List<Integer> ounitIdList) 
    		throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        return (List<NumericLevelElement>) getFromInstanceByIdAndMethod(getNumericLevelDao(), sampleId, 
        		"getValuesByOunitIDList", new Object[] {ounitIdList}, new Class[] {List.class});
    }

    @Override
    public List<CharacterLevelElement> getCharacterLevelValuesByOunitIdList(List<Integer> ounitIdList) 
    		throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);
        
        return (List<CharacterLevelElement>) getFromInstanceByIdAndMethod(getCharacterLevelDao(), sampleId, 
        		"getValuesByOunitIDList", new Object[] {ounitIdList}, new Class[] {List.class});
    }

    @Override
    public List<DatasetCondition> getConditionsByRepresentationId(Integer representationId) throws MiddlewareQueryException {

        List<DatasetCondition> toreturn = new ArrayList<DatasetCondition>();
        if (setWorkingDatabase(representationId)) {
            OindexDAO oindexDao = getOindexDao();
            NumericLevelDAO numericLevelDao = getNumericLevelDao();
            CharacterLevelDAO characterLevelDao = getCharacterLevelDao();
            List<Object[]> factorIdsAndLevelNos = oindexDao.getFactorIdAndLevelNoOfConditionsByRepresentationId(
            													representationId);
            for (Object[] ids : factorIdsAndLevelNos) {
                Integer factorid = (Integer) ids[0];
                Integer levelno = (Integer) ids[1];
                toreturn.addAll(numericLevelDao.getConditionAndValueByFactorIdAndLevelNo(factorid, levelno));
                toreturn.addAll(characterLevelDao.getConditionAndValueByFactorIdAndLevelNo(factorid, levelno));
            }
        }
        return toreturn;
    }

    @Override
    public String getMainLabelOfFactorByFactorId(Integer factorid) throws MiddlewareQueryException {
        if (setWorkingDatabase(factorid)) {
            return getFactorDao().getMainLabel(factorid);
        }
        return null;
    }

    @Override
    public long countStudyInformationByGID(Long gid) throws MiddlewareQueryException {
        long count = 0;
        if (gid < 0) {
            requireLocalDatabaseInstance();
            if (setWorkingDatabase(getCurrentSessionForLocal())) {
                count = getCharacterLevelDao().countStudyInformationByGID(gid) +
                		getNumericLevelDao().countStudyInformationByGID(gid);
            }
        } else {
        	
        	return countAllFromCentralAndLocalByMethod(getCharacterLevelDao(), 
        			"countStudyInformationByGID", new Object[] {gid}, new Class[] {Long.class});
        }
        return count;
    }

    @Override
    public List<StudyInfo> getStudyInformationByGID(Long gid) throws MiddlewareQueryException {
        List<StudyInfo> toreturn = new ArrayList<StudyInfo>();
        if (gid < 0) {
            requireLocalDatabaseInstance();
            if (setWorkingDatabase(getCurrentSessionForLocal())) {
                toreturn.addAll(getCharacterLevelDao().getStudyInformationByGID(gid));
                toreturn.addAll(getNumericLevelDao().getStudyInformationByGID(gid));
            }
        } else {
            if (setWorkingDatabase(getCurrentSessionForLocal())) {
                toreturn.addAll(getCharacterLevelDao().getStudyInformationByGID(gid));
                toreturn.addAll(getNumericLevelDao().getStudyInformationByGID(gid));
            }
            if (setWorkingDatabase(getCurrentSessionForCentral())) {
                toreturn.addAll(getCharacterLevelDao().getStudyInformationByGID(gid));
                toreturn.addAll(getNumericLevelDao().getStudyInformationByGID(gid));
            }
        }
        return toreturn;
    }

    @Override
    public Trait getReplicationTrait() throws MiddlewareQueryException {
        //the REPLICATION trait should be in the central IBDB
        requireCentralDatabaseInstance();
        return getTraitDao().getReplicationTrait();
    }

    @Override
    public Trait getBlockTrait() throws MiddlewareQueryException {
        //the BLOCK trait should be in the central IBDB
        requireCentralDatabaseInstance();
        return getTraitDao().getBlockTrait();
    }

    @Override
    public Trait getEnvironmentTrait() throws MiddlewareQueryException {
        //the ENVIRONMENT trait should be in the central IBDB
        requireCentralDatabaseInstance();
        return getTraitDao().getEnvironmentTrait();
    }

    @Override
    public Trait getDesignTrait() throws MiddlewareQueryException {
        //the DESIGN trait should be in the central IBDB
        requireCentralDatabaseInstance();
        return getTraitDao().getDesignTrait();
    }

    @Override
    public Factor getFactorOfDatasetByTraitid(Integer representationId, Integer traitid) throws MiddlewareQueryException {
        if (setWorkingDatabase(representationId)) {
            return getFactorDao().getFactorOfDatasetGivenTraitid(representationId, traitid);
        }
        return null;
    }

    @Override
    public List<CharacterLevel> getCharacterLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) 
    		throws MiddlewareQueryException {

    	return (List<CharacterLevel>) getFromInstanceByIdAndMethod(getCharacterLevelDao(), datasetId, "getByFactorAndDatasetID", 
    				new Object[] {factor,  datasetId}, new Class[] {Factor.class, Integer.class});
    }

	@Override
    public List<NumericLevel> getNumericLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) 
    		throws MiddlewareQueryException {
        
    	return (List<NumericLevel>) getFromInstanceByIdAndMethod(getNumericLevelDao(), datasetId, "getByFactorAndDatasetID", 
    				new Object[] {factor,  datasetId}, new Class[] {Factor.class, Integer.class});
    }

    @Override
    public boolean hasValuesByVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            RepresentationDAO dao = getRepresentationDao();
            if (isVariateNumeric(variateId)) {
                hasValues = dao.hasValuesByNumVariateAndDataset(variateId, datasetId);
            } else if (!isVariateNumeric(variateId)) {
                hasValues = dao.hasValuesByCharVariateAndDataset(variateId, datasetId);
            } else {
                logAndThrowException("Database Error: the variate selected has no datatype specified in the database.", LOG);
            }
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByNumVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByNumVariateAndDataset(variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByCharVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByCharVariateAndDataset(variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByLabelAndLabelValueAndVariateAndDataset(int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {

        boolean hasValues = false;

        if (setWorkingDatabase(datasetId)) {
            RepresentationDAO dao = getRepresentationDao();
            if (isVariateNumeric(variateId) && isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(
                		labelId, Double.parseDouble(value), variateId, datasetId);
            } else if (isVariateNumeric(variateId) && !isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(
                		labelId, value, variateId, datasetId);
            } else if (!isVariateNumeric(variateId) && isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(
                		labelId, Double.parseDouble(value), variateId, datasetId);
            } else if (!isVariateNumeric(variateId) && !isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(
                		labelId, value, variateId, datasetId);
            } else {
                logAndThrowException(
                    "Database Error: either the variate or label selected have no datatypes specified in the database.", LOG);
            }
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(
    		int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao()
                    .hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(
    		int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(
            		labelId, value, variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(
    		int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(
            		labelId, value, variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(
    		int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(
            		labelId, value, variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean isLabelNumeric(int labelId) throws MiddlewareQueryException {
        if (setWorkingDatabase(labelId)) {
            return getFactorDao().isLabelNumeric(labelId);
        }
        return false;
    }

    @Override
    public boolean isVariateNumeric(int variateId) throws MiddlewareQueryException {
        if (setWorkingDatabase(variateId)) {
            return getVariateDao().isVariateNumeric(variateId);
        }
        return false;
    }
}