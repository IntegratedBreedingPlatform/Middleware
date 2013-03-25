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
import org.generationcp.middleware.dao.StudyEffectDAO;
import org.generationcp.middleware.dao.TraitDAO;
import org.generationcp.middleware.dao.VariateDAO;
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

public class StudyDataManagerImpl extends DataManager implements StudyDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

    private CharacterDataDAO characterDataDao;
    private CharacterLevelDAO characterLevelDao;
    private FactorDAO factorDao;
    private NumericDataDAO numericDataDao;
    private NumericLevelDAO numericLevelDao;
    private OindexDAO oIndexDao;
    private RepresentationDAO representationDao;
    private StudyDAO studyDao;
    private StudyEffectDAO studyEffectDao;
    private TraitDAO traitDao;
    private VariateDAO variateDao;

    public StudyDataManagerImpl() {
    }

    public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    private CharacterDataDAO getCharacterDataDao() {
        if (characterDataDao == null) {
            characterDataDao = new CharacterDataDAO();
        }
        characterDataDao.setSession(getActiveSession());
        return characterDataDao;
    }

    private CharacterLevelDAO getCharacterLevelDao() {
        if (characterLevelDao == null) {
            characterLevelDao = new CharacterLevelDAO();
        }
        characterLevelDao.setSession(getActiveSession());
        return characterLevelDao;
    }

    private FactorDAO getFactorDao() {
        if (factorDao == null) {
            factorDao = new FactorDAO();
        }
        factorDao.setSession(getActiveSession());
        return factorDao;
    }

    private NumericDataDAO getNumericDataDao() {
        if (numericDataDao == null) {
            numericDataDao = new NumericDataDAO();
        }
        numericDataDao.setSession(getActiveSession());
        return numericDataDao;
    }

    private NumericLevelDAO getNumericLevelDao() {
        if (numericLevelDao == null) {
            numericLevelDao = new NumericLevelDAO();
        }
        numericLevelDao.setSession(getActiveSession());
        return numericLevelDao;
    }

    private OindexDAO getOindexDao() {
        if (oIndexDao == null) {
            oIndexDao = new OindexDAO();
        }
        oIndexDao.setSession(getActiveSession());
        return oIndexDao;
    }

    private RepresentationDAO getRepresentationDao() {
        if (representationDao == null) {
            representationDao = new RepresentationDAO();
        }
        representationDao.setSession(getActiveSession());
        return representationDao;
    }

    private StudyDAO getStudyDao() {
        if (studyDao == null) {
            studyDao = new StudyDAO();
        }
        studyDao.setSession(getActiveSession());
        return studyDao;
    }

    private StudyEffectDAO getStudyEffectDao() {
        if (studyEffectDao == null) {
            studyEffectDao = new StudyEffectDAO();
        }
        studyEffectDao.setSession(getActiveSession());
        return studyEffectDao;
    }

    private TraitDAO getTraitDao() {
        if (traitDao == null) {
            traitDao = new TraitDAO();
        }
        traitDao.setSession(getActiveSession());
        return traitDao;
    }

    private VariateDAO getVariateDao() {
        if (variateDao == null) {
            variateDao = new VariateDAO();
        }
        variateDao.setSession(getActiveSession());
        return variateDao;
    }

    @Override
    public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows, Database instance)
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
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countByName(name, op);
        }
        return 0;
    }

    @Override
    public List<Study> getStudyBySeason(Season season, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getStudyDao().getBySeason(season, start, numOfRows);
        }
        return new ArrayList<Study>();
    }

    @Override
    public long countStudyBySeason(Season season, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countBySeason(season);
        }
        return 0;
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
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countBySDate(sdate, op);
        }
        return 0;
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
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countByEDate(edate, op);
        }
        return 0;
    }

    @Override
    public List<Study> getStudyByCountry(String country, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Study> studyList = new ArrayList<Study>();
        if (setWorkingDatabase(instance)) {
            StudyDAO dao = getStudyDao();
            if (op == Operation.EQUAL) {
                studyList = dao.getByCountryUsingEqual(country, start, numOfRows);
            } else if (op == Operation.LIKE) {
                studyList = dao.getByCountryUsingLike(country, start, numOfRows);
            }
        }
        return studyList;
    }

    @Override
    public long countStudyByCountry(String country, Operation op, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countByCountry(country, op);
        }
        return 0;
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
        List<Study> topLevelStudies = new ArrayList<Study>();
        if (setWorkingDatabase(instance)) {
            topLevelStudies = getStudyDao().getTopLevelStudies(start, numOfRows);
        }
        return topLevelStudies;
    }

    public long countAllTopLevelStudies(Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countAllTopLevelStudies();
        }
        return 0;
    }

    @Override
    public long countAllStudyByParentFolderID(Integer parentFolderId, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getStudyDao().countAllStudyByParentFolderID(parentFolderId);
        }
        return 0;
    }

    @Override
    public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws MiddlewareQueryException {
        List<Study> studies = new ArrayList<Study>();
        if (setWorkingDatabase(parentFolderId)) {
            studies = getStudyDao().getByParentFolderID(parentFolderId, start, numOfRows);
        }
        return studies;
    }

    @Override
    public List<Variate> getVariatesByStudyID(Integer studyId) throws MiddlewareQueryException {
        List<Variate> variates = new ArrayList<Variate>();

        if (setWorkingDatabase(studyId)) {
            variates = getVariateDao().getByStudyID(studyId);
        }
        return variates;
    }

    @Override
    public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws MiddlewareQueryException {
        List<StudyEffect> studyEffects = new ArrayList<StudyEffect>();
        if (setWorkingDatabase(studyId)) {
            studyEffects = getStudyEffectDao().getByStudyID(studyId);
        }
        return studyEffects;
    }

    @Override
    public List<Factor> getFactorsByStudyID(Integer studyId) throws MiddlewareQueryException {
        List<Factor> factors = new ArrayList<Factor>();
        if (setWorkingDatabase(studyId)) {
            factors = getFactorDao().getByStudyID(studyId);
        }
        return factors;
    }

    @Override
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException {
        List<Representation> representations = new ArrayList<Representation>();
        if (setWorkingDatabase(effectId)) {
            representations = getRepresentationDao().getRepresentationByEffectID(effectId);
        }
        return representations;
    }

    @Override
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException {
        List<Representation> representations = new ArrayList<Representation>();
        if (setWorkingDatabase(studyId)) {
            representations = getRepresentationDao().getRepresentationByStudyID(studyId);
        }
        return representations;
    }

    @Override
    public List<Factor> getFactorsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        List<Factor> factors = new ArrayList<Factor>();
        if (setWorkingDatabase(representationId)) {
            factors = getFactorDao().getByRepresentationID(representationId);
        }
        return factors;
    }

    @Override
    public long countOunitIDsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        if (setWorkingDatabase(representationId)) {
            return getOindexDao().countOunitIDsByRepresentationId(representationId);
        }
        return 0;
    }

    @Override
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws MiddlewareQueryException {
        List<Integer> ounitIDs = new ArrayList<Integer>();
        if (setWorkingDatabase(representationId)) {
            ounitIDs = getOindexDao().getOunitIDsByRepresentationId(representationId, start, numOfRows);
        }
        return ounitIDs;
    }

    @Override
    public List<Variate> getVariatesByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        List<Variate> variates = new ArrayList<Variate>();
        if (setWorkingDatabase(representationId)) {
            variates = getVariateDao().getByRepresentationId(representationId);
        }
        return variates;
    }

    @Override
    public List<NumericDataElement> getNumericDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        List<NumericDataElement> numDataValues = new ArrayList<NumericDataElement>();
        if (setWorkingDatabase(sampleId)) {
            numDataValues = getNumericDataDao().getValuesByOunitIDList(ounitIdList);
        }
        return numDataValues;
    }

    @Override
    public List<CharacterDataElement> getCharacterDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        List<CharacterDataElement> charDataValues = new ArrayList<CharacterDataElement>();
        if (setWorkingDatabase(sampleId)) {
            charDataValues = getCharacterDataDao().getValuesByOunitIDList(ounitIdList);
        }
        return charDataValues;
    }

    @Override
    public List<NumericLevelElement> getNumericLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        List<NumericLevelElement> numLevelValues = new ArrayList<NumericLevelElement>();
        if (setWorkingDatabase(sampleId)) {
            numLevelValues = getNumericLevelDao().getValuesByOunitIDList(ounitIdList);
        }
        return numLevelValues;
    }

    @Override
    public List<CharacterLevelElement> getCharacterLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        // Get 1st element from list to check whether the list is for the Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);

        List<CharacterLevelElement> charLevelValues = new ArrayList<CharacterLevelElement>();
        if (setWorkingDatabase(sampleId)) {
            charLevelValues = getCharacterLevelDao().getValuesByOunitIDList(ounitIdList);
        }
        return charLevelValues;
    }

    @Override
    public List<DatasetCondition> getConditionsByRepresentationId(Integer representationId) throws MiddlewareQueryException {

        List<DatasetCondition> toreturn = new ArrayList<DatasetCondition>();
        if (setWorkingDatabase(representationId)) {
            OindexDAO oindexDao = getOindexDao();
            NumericLevelDAO numericLevelDao = getNumericLevelDao();
            CharacterLevelDAO characterLevelDao = getCharacterLevelDao();
            List<Object[]> factorIdsAndLevelNos = oindexDao.getFactorIdAndLevelNoOfConditionsByRepresentationId(representationId);
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
                count = getCharacterLevelDao().countStudyInformationByGID(gid) + getNumericLevelDao().countStudyInformationByGID(gid);
            }
        } else {
        	
        	return countAllFromCentralAndLocalByMethod(getCharacterLevelDao(), "countStudyInformationByGID", new Object[] {gid}, new Class[] {Long.class});
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
    public List<CharacterLevel> getCharacterLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) throws MiddlewareQueryException {
        List<CharacterLevel> charLevelValues = new ArrayList<CharacterLevel>();
        if (setWorkingDatabase(datasetId)) {
            charLevelValues = getCharacterLevelDao().getByFactorAndDatasetID(factor, datasetId);
        }
        return charLevelValues;
    }

    @Override
    public List<NumericLevel> getNumericLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) throws MiddlewareQueryException {
        List<NumericLevel> numLevelValues = new ArrayList<NumericLevel>();
        if (setWorkingDatabase(datasetId)) {
            numLevelValues = getNumericLevelDao().getByFactorAndDatasetID(factor, datasetId);
        }
        return numLevelValues;
    }

    @Override
    public boolean hasValuesByVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            RepresentationDAO dao = getRepresentationDao();
            if (isVariateNumeric(variateId)) {
                hasValues = dao.hasValuesByNumVariateAndDataset(variateId, datasetId);
            } else if (!isVariateNumeric(variateId)) {
                hasValues = dao.hasValuesByNumVariateAndDataset(variateId, datasetId);
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
                hasValues = dao.hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(labelId, Double.parseDouble(value), variateId,
                        datasetId);
            } else if (isVariateNumeric(variateId) && !isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId, datasetId);
            } else if (!isVariateNumeric(variateId) && isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(labelId, Double.parseDouble(value), variateId,
                        datasetId);
            } else if (!isVariateNumeric(variateId) && !isLabelNumeric(labelId)) {
                hasValues = dao.hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(labelId, value, variateId, datasetId);
            } else {
                logAndThrowException(
                        "Database Error: either the variate or label selected have no datatypes specified in the database.", LOG);
            }
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao()
                    .hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId, datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId,
                    datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(labelId, value, variateId,
                    datasetId);
        }
        return hasValues;
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        boolean hasValues = false;
        if (setWorkingDatabase(datasetId)) {
            hasValues = getRepresentationDao().hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(labelId, value, variateId,
                    datasetId);
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