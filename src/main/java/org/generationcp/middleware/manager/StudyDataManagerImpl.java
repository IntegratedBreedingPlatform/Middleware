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

public class StudyDataManagerImpl extends DataManager implements StudyDataManager{

    public StudyDataManagerImpl() {
    }

    public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        // TODO Local-Central: Verify if existing implementation for CENTRAL is  also applicable to LOCAL
        Session session = getSession(instance);

        if (session != null) {

            NumericDataDAO dataNDao = new NumericDataDAO();
            dataNDao.setSession(session);

            CharacterDataDAO dataCDao = new CharacterDataDAO();
            dataCDao.setSession(session);

            Set<Integer> ounitIds = new HashSet<Integer>();

            // first get the observation unit ids for the rows in datasets which
            // has the data specified in the filter
            // check numeric data
            ounitIds.addAll(dataNDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));
            // check character data
            ounitIds.addAll(dataCDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));

            // use the retrieved observation unit ids to get the GIDs being
            // observed in the rows in datasets identified by the
            // observation unit ids
            if (!ounitIds.isEmpty()) {
                FactorDAO factorDao = new FactorDAO();
                factorDao.setSession(session);

                Set<Integer> gids = factorDao.getGIDSByObservationUnitIds(ounitIds, start, numOfRows * 2);
                List<Integer> toreturn = new ArrayList<Integer>();
                toreturn.addAll(gids);
                return toreturn;
            } else {
                return new ArrayList<Integer>();
            }

        } else {
            return new ArrayList<Integer>();
        }

    }

    @Override
    public List<Study> getStudyByName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> studyList = null;
        if (op == Operation.EQUAL) {
            studyList = dao.getByNameUsingEqual(name, start, numOfRows);
        } else if (op == Operation.LIKE) {
            studyList = dao.getByNameUsingLike(name, start, numOfRows);
        }

        return studyList;

    }

    @Override
    public long countStudyByName(String name, Operation op, Database instance) throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countByName(name, op);

    }
    
    @Override
    public List<Study> getStudyBySeason(Season season, int start, int numOfRows, Database instance) throws MiddlewareQueryException{
        
        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        return dao.getBySeason(season, start, numOfRows);

    }

    @Override
    public long countStudyBySeason(Season season, Database instance) throws MiddlewareQueryException{

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countBySeason(season);

    }

    @Override
    public List<Study> getStudyBySDate(Integer sdate, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> studyList = null;
        if (op == Operation.EQUAL) {
            studyList = dao.getBySDateUsingEqual(sdate, start, numOfRows);
        }

        return studyList;

    }

    @Override
    public long countStudyBySDate(Integer sdate, Operation op, Database instance) throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countBySDate(sdate, op);

    }
    
    
    @Override
    public List<Study> getStudyByEDate(Integer edate, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> studyList = null;
        if (op == Operation.EQUAL) {
            studyList = dao.getByEDateUsingEqual(edate, start, numOfRows);
        }

        return studyList;

    }

    @Override
    public long countStudyByEDate(Integer edate, Operation op, Database instance) throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countByEDate(edate, op);

    }

    @Override
    public List<Study> getStudyByCountry(String country, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> studyList = null;
        if (op == Operation.EQUAL) {
            studyList = dao.getByCountryUsingEqual(country, start, numOfRows);
        } else if (op == Operation.LIKE) {
            studyList = dao.getByCountryUsingLike(country, start, numOfRows);
        }

        return studyList;

    }


    @Override
    public long countStudyByCountry(String country, Operation op, Database instance) throws MiddlewareQueryException {

        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countByCountry(country, op);

    }
    
    @Override
    public Study getStudyByID(Integer id) throws MiddlewareQueryException {
        StudyDAO dao = new StudyDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return (Study) dao.getById(id, false);
    }

    @Override
    public List<Study> getAllTopLevelStudies(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        StudyDAO dao = new StudyDAO();

        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> topLevelStudies = dao.getTopLevelStudies(start, numOfRows);

        return topLevelStudies;
    }

    public long countAllTopLevelStudies(Database instance) throws MiddlewareQueryException {
        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countAllTopLevelStudies();
    }

    @Override
    public long countAllStudyByParentFolderID(Integer parentFolderId, Database instance) throws MiddlewareQueryException {
        StudyDAO dao = new StudyDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countAllStudyByParentFolderID(parentFolderId);

    }

    @Override
    public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws MiddlewareQueryException {
        StudyDAO dao = new StudyDAO();

        Session session = getSession(parentFolderId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Study>();
        }

        List<Study> studies = dao.getByParentFolderID(parentFolderId, start, numOfRows);

        return studies;
    }

    @Override
    public List<Variate> getVariatesByStudyID(Integer studyId) throws MiddlewareQueryException {
        VariateDAO variateDao = new VariateDAO();
        Session session = getSession(studyId);

        if (session != null) {
            variateDao.setSession(session);
        } else {
            return new ArrayList<Variate>();
        }

        return (List<Variate>) variateDao.getByStudyID(studyId);
    }

    @Override
    public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws MiddlewareQueryException {
        StudyEffectDAO studyEffectDao = new StudyEffectDAO();
        Session session = getSession(studyId);

        if (session != null) {
            studyEffectDao.setSession(session);
        } else {
            return new ArrayList<StudyEffect>();
        }

        return (List<StudyEffect>) studyEffectDao.getByStudyID(studyId);
    }

    @Override
    public List<Factor> getFactorsByStudyID(Integer studyId) throws MiddlewareQueryException {
        FactorDAO factorDao = new FactorDAO();
        Session session = getSession(studyId);

        if (session != null) {
            factorDao.setSession(session);
        } else {
            return new ArrayList<Factor>();
        }

        return (List<Factor>) factorDao.getByStudyID(studyId);
    }

    @Override
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException {
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(effectId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return new ArrayList<Representation>();
        }

        return (List<Representation>) representationDao.getRepresentationByEffectID(effectId);
    }

    @Override
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException {
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(studyId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return new ArrayList<Representation>();
        }

        List<Representation> representations = representationDao.getRepresentationByStudyID(studyId);
        return representations;
    }

    @Override
    public List<Factor> getFactorsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        FactorDAO dao = new FactorDAO();

        Session session = getSession(representationId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Factor>();
        }

        List<Factor> factors = dao.getByRepresentationID(representationId);

        return factors;
    }

    @Override
    public long countOunitIDsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        OindexDAO dao = new OindexDAO();

        Session session = getSession(representationId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countOunitIDsByRepresentationId(representationId);
    }

    @Override
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws MiddlewareQueryException {
        OindexDAO dao = new OindexDAO();

        Session session = getSession(representationId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer>();
        }

        List<Integer> ounitIDs = dao.getOunitIDsByRepresentationId(representationId, start, numOfRows);

        return ounitIDs;
    }

    @Override
    public List<Variate> getVariatesByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        VariateDAO dao = new VariateDAO();

        Session session = getSession(representationId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Variate>();
        }

        List<Variate> variates = dao.getByRepresentationId(representationId);

        return variates;
    }

    @Override
    public List<NumericDataElement> getNumericDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        NumericDataDAO dao = new NumericDataDAO();

        // get 1st element from list to check whether the list is for the
        // Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);
        Session session = getSession(sampleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<NumericDataElement>();
        }

        List<NumericDataElement> numDataValues = dao.getValuesByOunitIDList(ounitIdList);

        return numDataValues;
    }

    @Override
    public List<CharacterDataElement> getCharacterDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        CharacterDataDAO dao = new CharacterDataDAO();

        // get 1st element from list to check whether the list is for the
        // Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);
        Session session = getSession(sampleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<CharacterDataElement>();
        }

        List<CharacterDataElement> charDataValues = dao.getValuesByOunitIDList(ounitIdList);

        return charDataValues;
    }

    @Override
    public List<NumericLevelElement> getNumericLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        NumericLevelDAO dao = new NumericLevelDAO();

        // get 1st element from list to check whether the list is for the
        // Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);
        Session session = getSession(sampleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<NumericLevelElement>();
        }

        List<NumericLevelElement> numLevelValues = dao.getValuesByOunitIDList(ounitIdList);

        return numLevelValues;
    }

    @Override
    public List<CharacterLevelElement> getCharacterLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException {
        CharacterLevelDAO dao = new CharacterLevelDAO();

        // get 1st element from list to check whether the list is for the
        // Central instance or the Local instance
        Integer sampleId = ounitIdList.get(0);
        Session session = getSession(sampleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<CharacterLevelElement>();
        }

        List<CharacterLevelElement> charLevelValues = dao.getValuesByOunitIDList(ounitIdList);

        return charLevelValues;
    }

    @Override
    public List<DatasetCondition> getConditionsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        List<DatasetCondition> toreturn = new ArrayList<DatasetCondition>();

        OindexDAO oindexDao = new OindexDAO();
        NumericLevelDAO numericLevelDao = new NumericLevelDAO();
        CharacterLevelDAO characterLevelDao = new CharacterLevelDAO();
        Session session = getSession(representationId);

        if (session != null) {
            oindexDao.setSession(session);
            numericLevelDao.setSession(session);
            characterLevelDao.setSession(session);

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
        FactorDAO dao = new FactorDAO();
        Session session = getSession(factorid);

        if (session != null) {
            dao.setSession(session);

            return dao.getMainLabel(factorid);
        }

        return null;
    }

    @Override
    public long countStudyInformationByGID(Long gid) throws MiddlewareQueryException {
        try {
            CharacterLevelDAO characterLevelDao = new CharacterLevelDAO();
            NumericLevelDAO numericLevelDao = new NumericLevelDAO();

            if (gid < 0) {
                requireLocalDatabaseInstance();

                characterLevelDao.setSession(getCurrentSessionForLocal());
                numericLevelDao.setSession(getCurrentSessionForLocal());

                long count = characterLevelDao.countStudyInformationByGID(gid) + numericLevelDao.countStudyInformationByGID(gid);

                return count;
            } else {
                long count = 0;

                if (this.getCurrentSessionForLocal() != null) {
                    characterLevelDao.setSession(getCurrentSessionForLocal());
                    numericLevelDao.setSession(getCurrentSessionForLocal());

                    count = characterLevelDao.countStudyInformationByGID(gid) + numericLevelDao.countStudyInformationByGID(gid);
                }

                if (this.getCurrentSessionForCentral() != null) {
                    characterLevelDao.setSession(getCurrentSessionForCentral());
                    numericLevelDao.setSession(getCurrentSessionForCentral());

                    count = count + characterLevelDao.countStudyInformationByGID(gid) + numericLevelDao.countStudyInformationByGID(gid);
                }

                return count;
            }
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in count study information by GID: StudyDataManager.countStudyInformationByGID(gid="
                    + gid + "): " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudyInfo> getStudyInformationByGID(Long gid) throws MiddlewareQueryException {
        try {
            List<StudyInfo> toreturn = new ArrayList<StudyInfo>();

            CharacterLevelDAO characterLevelDao = new CharacterLevelDAO();
            NumericLevelDAO numericLevelDao = new NumericLevelDAO();

            if (gid < 0) {
                requireLocalDatabaseInstance();

                characterLevelDao.setSession(getCurrentSessionForLocal());
                numericLevelDao.setSession(getCurrentSessionForLocal());

                toreturn.addAll(characterLevelDao.getStudyInformationByGID(gid));
                toreturn.addAll(numericLevelDao.getStudyInformationByGID(gid));
            } else {
                if (this.getCurrentSessionForLocal() != null) {
                    characterLevelDao.setSession(getCurrentSessionForLocal());
                    numericLevelDao.setSession(getCurrentSessionForLocal());

                    toreturn.addAll(characterLevelDao.getStudyInformationByGID(gid));
                    toreturn.addAll(numericLevelDao.getStudyInformationByGID(gid));
                }

                if (this.getCurrentSessionForCentral() != null) {
                    characterLevelDao.setSession(getCurrentSessionForCentral());
                    numericLevelDao.setSession(getCurrentSessionForCentral());

                    toreturn.addAll(characterLevelDao.getStudyInformationByGID(gid));
                    toreturn.addAll(numericLevelDao.getStudyInformationByGID(gid));
                }
            }

            return toreturn;
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in get study information by GID: StudyDataManager.getStudyInformationByGID(gid="
                    + gid + "): " + e.getMessage(), e);
        }
    }
    
    @Override
    public Trait getReplicationTrait() throws MiddlewareQueryException {
        try {
            TraitDAO dao = new TraitDAO();
            
            //the REPLICATION trait should be in the central IBDB
            requireCentralDatabaseInstance();
            
            dao.setSession(getCurrentSessionForCentral());
            return dao.getReplicationTrait();
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in getting REPLICATION trait: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Trait getBlockTrait() throws MiddlewareQueryException {
        try {
            TraitDAO dao = new TraitDAO();
            
            //the BLOCK trait should be in the central IBDB
            requireCentralDatabaseInstance();
            
            dao.setSession(getCurrentSessionForCentral());
            return dao.getBlockTrait();
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in getting BLOCK trait: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Trait getEnvironmentTrait() throws MiddlewareQueryException {
        try {
            TraitDAO dao = new TraitDAO();
            
            //the ENVIRONMENT trait should be in the central IBDB
            requireCentralDatabaseInstance();
            
            dao.setSession(getCurrentSessionForCentral());
            return dao.getEnvironmentTrait();
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in getting ENVIRONMENT trait: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Trait getDesignTrait() throws MiddlewareQueryException {
        try {
            TraitDAO dao = new TraitDAO();
            
            //the DESIGN trait should be in the central IBDB
            requireCentralDatabaseInstance();
            
            dao.setSession(getCurrentSessionForCentral());
            return dao.getDesignTrait();
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in getting DESIGN trait: " + e.getMessage(), e);
        }
    }    
    
    @Override
    public Factor getFactorOfDatasetByTraitid(Integer representationId, Integer traitid) throws MiddlewareQueryException {
        try {
            FactorDAO dao = new FactorDAO();
            //if the representation id is positive the dataset should be from central IBDB
            //otherwise the dataset is from local IBDB
            Session session = getSession(representationId);
            
            if(session != null){
                dao.setSession(session);
                return dao.getFactorOfDatasetGivenTraitid(representationId, traitid);
            } else{
                throw new MiddlewareQueryException("Error in getting factor of dataset by traitid: Cannot get Session to use.");
            }
        } catch (Exception e) {
            throw new MiddlewareQueryException("Error in getting factor of dataset by traitid: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CharacterLevel> getCharacterLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) throws MiddlewareQueryException {
        try{
            CharacterLevelDAO dao = new CharacterLevelDAO();
            Session session = getSession(datasetId);
            
            if(session != null){
                dao.setSession(session);
                return dao.getByFactorAndDatasetID(factor, datasetId);
            } else{
                throw new MiddlewareQueryException("Error in getting character levels by factor and dataset id: Cannot get Session to use.");
            }
        } catch(Exception ex){
            throw new MiddlewareQueryException("Error in getting character levels by factor and dataset id: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public List<NumericLevel> getNumericLevelsByFactorAndDatasetId(Factor factor, Integer datasetId) throws MiddlewareQueryException {
        try{
            NumericLevelDAO dao = new NumericLevelDAO();
            Session session = getSession(datasetId);
            
            if(session != null){
                dao.setSession(session);
                return dao.getByFactorAndDatasetID(factor, datasetId);
            } else{
                throw new MiddlewareQueryException("Error in getting character levels by factor and dataset id: Cannot get Session to use.");
            }
        } catch(Exception ex){
            throw new MiddlewareQueryException("Error in getting character levels by factor and dataset id: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean hasValuesByNumVariateAndDataset(int variateId,
            int datasetId) throws MiddlewareQueryException {
        
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByNumVariateAndDataset(variateId, datasetId);
    }

    @Override
    public boolean hasValuesByCharVariateAndDataset(int variateId,
            int datasetId) throws MiddlewareQueryException {

        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByCharVariateAndDataset(variateId, datasetId);
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(
            int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId, datasetId);
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(
            int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(labelId, value, variateId, datasetId);
    }

    @Override
    public boolean hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(
            int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(labelId, value, variateId, datasetId);
    }

    @Override
    public boolean hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(
            int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        
        RepresentationDAO representationDao = new RepresentationDAO();
        Session session = getSession(datasetId);

        if (session != null) {
            representationDao.setSession(session);
        } else {
            return false;
        }

        return representationDao.hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(labelId, value, variateId, datasetId);
    }

    @Override
    public boolean isLabelNumeric(int labelId) throws MiddlewareQueryException {

        FactorDAO factorDAO = new FactorDAO();
        Session session = getSession(labelId);

        if (session != null) {
            factorDAO.setSession(session);
        } else {
            return false;
        }

        return factorDAO.isLabelNumeric(labelId);
    }

    @Override
    public boolean isVariateNumeric(int variateId)
            throws MiddlewareQueryException {

        VariateDAO variateDAO = new VariateDAO();
        Session session = getSession(variateId);

        if (session != null) {
            variateDAO.setSession(session);
        } else {
            return false;
        }

        return variateDAO.isVariateNumeric(variateId);
    }
}