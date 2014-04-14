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
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.FieldbookService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FieldbookServiceImpl extends Service implements FieldbookService {
    
    private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

//	private static final String DATA_TYPE_NUMERIC = "N";
	
    public FieldbookServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    @Override
    public List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException{
    	List<StudyDetails> studyDetailList =  getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.N);
    	List<StudyDetails> newList = new ArrayList<StudyDetails>();
    	for(StudyDetails detail : studyDetailList){
    		if(detail.hasRows())
    			newList.add(detail);
    	}
    	return newList;
    }
    
    @Override 
    public List<StudyDetails> getAllLocalTrialStudyDetails() throws MiddlewareQueryException{
        return getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.T);
    }

    @Override
    public List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList) 
            throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(trialIdList, StudyType.T);
    }
    
    @Override 
    public List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList) 
            throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(nurseryIdList, StudyType.N);
    }

    @Override 
    public List<Location> getAllLocations()throws MiddlewareQueryException{
    	Integer fieldLtypeFldId = getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
    	Integer blockLtypeFldId = getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());
    	
    	List<Location> locList =  getLocationDataManager().getAllLocations();
    	List<Location> newLocation = new ArrayList<Location>();
    	
    	for(Location loc : locList){
    		if((fieldLtypeFldId != null && fieldLtypeFldId.intValue() == loc.getLtype().intValue())
    				|| (blockLtypeFldId != null && blockLtypeFldId.intValue() == loc.getLtype().intValue()))
    			continue;
    		newLocation.add(loc);
    	}
    	
    	return newLocation;
    }

    @Override
    public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) 
            throws MiddlewareQueryException {
        getStudyDataManager().saveOrUpdateFieldmapProperties(info, userId, isNew);
    }
    
    @Override
    public Study getStudy(int studyId) throws MiddlewareQueryException  {
    	//not using the variable type
        return getStudyDataManager().getStudy(studyId, false);
    }

    @Override           
    public List<Location> getFavoriteLocationByProjectId(List<Long> locationIds) 
            throws MiddlewareQueryException {
    	Integer fieldLtypeFldId = getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
    	Integer blockLtypeFldId = getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());
    	
        List<Location> locationList = new ArrayList<Location>();
        
        for(int i = 0 ; i < locationIds.size() ; i++){
            Integer locationId = Integer.valueOf(locationIds.get(i).toString());
            Location location = getLocationDataManager().getLocationByID(locationId);
            
            if((fieldLtypeFldId != null && fieldLtypeFldId.intValue() == location.getLtype().intValue())
    				|| (blockLtypeFldId != null && blockLtypeFldId.intValue() == location.getLtype().intValue()))
    			continue;
            
            locationList.add(location);
        }
        
    	
        return locationList;
    }
    
    @Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId) 
            throws MiddlewareQueryException {
        return getStudyDataManager().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId);
    }

    @Override
    public List<DatasetReference> getDatasetReferences(int studyId) 
            throws MiddlewareQueryException {
        return getStudyDataManager().getDatasetReferences(studyId);
    }

	@Override
	public int getNextGermplasmId() throws MiddlewareQueryException {
		return getGermplasmDataManager().getNextNegativeId().intValue();
	}

	@Override
	public Integer getGermplasmIdByName(String name)
			throws MiddlewareQueryException {
		
		 List<Germplasm> germplasmList = getGermplasmDataManager()
		         .getGermplasmByName(name, 0, 1, Operation.EQUAL);
		 Integer gid = null;
		 if(germplasmList != null && germplasmList.size() > 0){
			 gid = germplasmList.get(0).getGid();
		 }
		 return gid;
	}

	@Override
    public Integer getStandardVariableIdByPropertyScaleMethodRole(
            String property, String scale, String method, PhenotypicType role)
            throws MiddlewareQueryException {
        return getOntologyDataManager()
                .getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
    }
    
	@Override
    public Workbook getNurseryDataSet(int id) throws MiddlewareQueryException {
        Workbook workbook = getWorkbookBuilder().create(id, StudyType.N);                        
        return workbook;
    }

	@Override
    public Workbook getTrialDataSet(int id) throws MiddlewareQueryException {
        Workbook workbook = getWorkbookBuilder().create(id, StudyType.T);                        
        return workbook;
    }

	@Override
    public void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        long startTime = System.currentTimeMillis();

        try {
            trans = session.beginTransaction();
            
            saveTrialObservations(workbook);
            
            List<MeasurementVariable> variates = workbook.getVariates();
            List<MeasurementRow> observations = workbook.getObservations();
            
            int i = 0;
            
            if (variates != null){
                for (MeasurementVariable variate : variates){
                    for (MeasurementRow row : observations){
                        for (MeasurementData field : row.getDataList()){
                            if (variate.getName().equals(field.getLabel())){
                            	Phenotype phenotype = null;
                                if (field.getValue() != null) {
                                	field.setValue(field.getValue().trim());
                                }
                                if (field.getPhenotypeId() != null) {
	                                phenotype = getPhenotypeDao().getById(field.getPhenotypeId());
                                }
                                if (phenotype == null && field.getValue() != null 
                                        && !"".equals(field.getValue().trim())){
                                    phenotype = new Phenotype();
                                    phenotype.setPhenotypeId(getPhenotypeDao().getNegativeId("phenotypeId"));
                                }
                                if (phenotype != null) {
	                                getPhenotypeSaver().saveOrUpdate((int) row.getExperimentId()
	                                        , variate.getTermId(), variate.getStoredIn()
	                                        , field.getValue(), phenotype);
	
	                                i++;
	                                if ( i % JDBC_BATCH_SIZE == 0 ) { //flush a batch of inserts and release memory
	                                    session.flush();
	                                    session.clear();
	                                }
                                }
                            }
                        }
                    }
                }
            }
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, LOG);
        }
        
        LOG.debug("========== saveMeasurementRows Duration (ms): " 
                + ((System.currentTimeMillis() - startTime)/60));
        
    }
	
	private void saveTrialObservations(Workbook workbook) throws MiddlewareQueryException, MiddlewareException {
		setWorkingDatabase(Database.LOCAL);
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			for (MeasurementRow trialObservation : workbook.getTrialObservations()) {
				getGeolocationSaver().updateGeolocationInformation(trialObservation, workbook.isNursery());
			}
		}
	}

	@Override
	public List<Method> getAllBreedingMethods() throws MiddlewareQueryException {
		List<Method> methodList = getGermplasmDataManager().getAllMethods();
		Collections.sort(methodList, new Comparator<Method>(){

			@Override
			public int compare(Method o1, Method o2) {
				 String methodName1 = o1.getMname().toUpperCase();
			      String methodName2 = o2.getMname().toUpperCase();
		 
			      //ascending order
			      return methodName1.compareTo(methodName2);
			}
			
		});
		return methodList;
	}

	@Override
	public List<Method> getFavoriteBreedingMethods(List<Integer> methodIds)
			throws MiddlewareQueryException {
		 List<Method> methodList = new ArrayList<Method>();
	        
	        for(int i = 0 ; i < methodIds.size() ; i++){
	            Integer methodId = methodIds.get(i);
	            Method method = getGermplasmDataManager().getMethodByID(methodId);
	            methodList.add(method);
	        }
	        
	        Collections.sort(methodList, new Comparator<Method>(){

				@Override
				public int compare(Method o1, Method o2) {
					 String methodName1 = o1.getMname().toUpperCase();
				      String methodName2 = o2.getMname().toUpperCase();
			 
				      //ascending order
				      return methodName1.compareTo(methodName2);
				}
				
			});
			return methodList;
	}

    @Override
    public Integer saveNurseryAdvanceGermplasmList(Map<Germplasm, List<Name>> germplasms
                            , Map<Germplasm, GermplasmListData> listDataItems
                            , GermplasmList germplasmList)
            throws MiddlewareQueryException {
        
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        Integer listId = null;

        GermplasmDAO germplasmDao = getGermplasmDao();
        NameDAO nameDao = getNameDao();
        GermplasmListDAO germplasmListDao = getGermplasmListDAO();
        
        long startTime = System.currentTimeMillis();

        try {
            trans = session.beginTransaction();
            
            // Save germplasm list
            listId = germplasmListDao.getNegativeId("id");
            germplasmList.setId(listId);
            germplasmListDao.save(germplasmList);

            int i = 0;

            // Save germplasms, names, list data
            for (Germplasm germplasm : germplasms.keySet()) {

                GermplasmListData germplasmListData = listDataItems.get(germplasm);

                Germplasm germplasmFound = null;

                // Check if germplasm exists
                if (germplasm.getGid() != null){

                    // Check if the given gid exists
                    germplasmFound = getGermplasmDataManager().getGermplasmByGID(germplasm.getGid());
                    
                    // Check if the given germplasm name exists
                    if (germplasmFound == null){
                        List<Germplasm> germplasmsFound = getGermplasmDataManager()
                                .getGermplasmByName(germplasm.getPreferredName()
                                        .getNval(), 0, 1, Operation.EQUAL);
                        
                        if (germplasmsFound.size() > 0){
                            germplasmFound = germplasmsFound.get(0);
                        }
                    } 
                }
                
                // Save germplasm and name entries if non-existing
                if (germplasmFound == null || germplasmFound.getGid() == null){
                    Integer gId = germplasmDao.getNegativeId("gid");

                    // Save name entries
                    for (Name name: germplasms.get(germplasm)){
                        Integer nameId = nameDao.getNegativeId("nid");
                        name.setNid(nameId);
                        name.setGermplasmId(gId);
                        nameDao.save(name);
                    }
                    
                    // Save germplasm
                    germplasm.setGid(gId);
                    germplasm.setLgid(gId);
                    germplasmDao.save(germplasm);

                } 
                               
                // Save germplasmListData
                Integer germplasmListDataId = getGermplasmListDataDAO().getNegativeId("id");
                germplasmListData.setId(germplasmListDataId);
                germplasmListData.setGid(germplasm.getGid());
                germplasmListData.setList(germplasmList);
                getGermplasmListDataDAO().save(germplasmListData);

                i++;
                if ( i % JDBC_BATCH_SIZE == 0 ) {  //flush a batch of inserts and release memory
                    session.flush();
                    session.clear();
                }
                
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms="
                    + germplasms + ", germplasmList=" + germplasmList + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        LOG.debug("========== saveNurseryAdvanceGermplasmList Duration (ms): " 
                    + ((System.currentTimeMillis() - startTime)/60));

        return listId;

    }
	
	@Override
    public String getCimmytWheatGermplasmNameByGid(int gid) throws MiddlewareQueryException {
        List<Name> names = getByGidAndNtype(gid, GermplasmNameType.CIMMYT_SELECTION_HISTORY);
        if (names == null || names.isEmpty()) {
            names = getByGidAndNtype(gid, GermplasmNameType.UNRESOLVED_NAME);
        }
        return (names != null && !names.isEmpty() ? names.get(0).getNval() : null);
    }
    
    private List<Name> getByGidAndNtype(int gid, GermplasmNameType nType) 
            throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        List<Name> names = getNameDao().getByGIDWithFilters(gid, null, nType);
        if (names == null || names.isEmpty()) {
            setWorkingDatabase(Database.LOCAL);
            names = getNameDao().getByGIDWithFilters(gid, null, nType);
        }
        return names;
    }
    
    @Override
    public GermplasmList getGermplasmListByName(String name) throws MiddlewareQueryException{
        List<GermplasmList> germplasmLists = getGermplasmListManager()
                .getGermplasmListByName(name, 0, 1, Operation.EQUAL, Database.CENTRAL);
        
        if (germplasmLists.size() > 0){
            return germplasmLists.get(0);
        } 
        germplasmLists = getGermplasmListManager()
                .getGermplasmListByName(name, 0, 1, Operation.EQUAL, Database.LOCAL);
            
        if (germplasmLists.size() > 0){
            return germplasmLists.get(0);
        } 
        return null;
    }


    @Override
    public Method getBreedingMethodById(int mid) throws MiddlewareQueryException {
        return getGermplasmDataManager().getMethodByID(mid);
    }
    
    @Override
    public Germplasm getGermplasmByGID(int gid) throws MiddlewareQueryException {
        return getGermplasmDataManager().getGermplasmByGID(gid);
    }
	
    @Override
    public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) 
            throws MiddlewareQueryException {
    	return getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
    }
    
    @Override
    public List<ValueReference> getDistinctStandardVariableValues(
            String property, String scale, String method, PhenotypicType role) 
    		throws MiddlewareQueryException {
    	
    	Integer stdVarId = getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
    	if (stdVarId != null) {
    		return getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
    	}
    	return new ArrayList<ValueReference>();
    }

    @Override
    public Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException {
    	return getOntologyDataManager().getAllStandardVariables();
    }
    
    @Override
    public StandardVariable getStandardVariable(int id) throws MiddlewareQueryException {
    	return getOntologyDataManager().getStandardVariable(id);
    }
    
    @Override
    public List<ValueReference> getAllNurseryTypes() throws MiddlewareQueryException{
        
        setWorkingDatabase(Database.CENTRAL);

        List<ValueReference> nurseryTypes = new ArrayList<ValueReference>();

        StandardVariable stdVar = getOntologyDataManager().getStandardVariable(TermId.NURSERY_TYPE.getId());
        List<Enumeration> validValues = stdVar.getEnumerations();

        if (validValues != null){
            for (Enumeration value : validValues){
                if (value != null){
                    nurseryTypes.add(new ValueReference(value.getId(), value.getName(), value.getDescription()));
                }
            }
        }
        
        return nurseryTypes;
    }
    
    public List<Person> getAllPersons() throws MiddlewareQueryException {
        return getUserDataManager().getAllPersons();
    }
    
    public int countPlotsWithPlantsSelectedofNursery(int nurseryId) throws MiddlewareQueryException {
        StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, StudyType.N, nurseryId);
        
        int dataSetId = 0;
        
        //get observation dataset
        List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(nurseryId);
        if (datasetRefList != null) {
            for (DatasetReference datasetRef : datasetRefList) {
                if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName()) || 
                        datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
                    dataSetId = datasetRef.getId();
                }
            }
        }
        
        //if not found in the list using the name, get dataset with Plot Data type
        if (dataSetId == 0) {
            DataSet dataset = getStudyDataManager().findOneDataSetByType(nurseryId, DataSetType.PLOT_DATA);
            if (dataset != null){
                dataSetId = dataset.getId();
            }
        }
        
        return getStudyDataManager().countPlotsWithPlantsSelectedofDataset(dataSetId);
    }
    
    @Override
    public List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds) 
            throws MiddlewareQueryException {
    	List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
    	
    	List<CVTerm> variables = new ArrayList<CVTerm>();
    	
    	Set<Integer> variableIds = new HashSet<Integer>();

    	addAllVariableIdsInMode(variableIds, storedInIds, Database.CENTRAL);
    	addAllVariableIdsInMode(variableIds, storedInIds, Database.LOCAL);
    	
    	List<Integer> variableIdList = new ArrayList<Integer>(variableIds);
    	setWorkingDatabase(Database.CENTRAL);
    	variables.addAll(getCvTermDao().getByIds(variableIdList));
    	setWorkingDatabase(Database.LOCAL);
    	variables.addAll(getCvTermDao().getByIds(variableIdList));
    	
    	for (CVTerm variable : variables) {
    		list.add(new StandardVariableReference(variable.getCvTermId()
    		        , variable.getName(), variable.getDefinition()));
    	}
    	
    	return list;
    }

    private void addAllVariableIdsInMode(Set<Integer> variableIds
            , List<Integer> storedInIds, Database database) 
                    throws MiddlewareQueryException {
    	setWorkingDatabase(database);
    	for (Integer storedInId : storedInIds) {
    		variableIds.addAll(getCvTermRelationshipDao()
    		        .getSubjectIdsByTypeAndObject(TermId.STORED_IN.getId(), storedInId));
    	}
    }
    
    public Workbook getStudyVariableSettings(int id, boolean isNursery)  throws MiddlewareQueryException {
        Workbook workbook = getWorkbookBuilder().createStudyVariableSettings(id, isNursery);                        
        return workbook;
    }

	@Override
	public List<Germplasm> getGermplasms(List<Integer> gids)
			throws MiddlewareQueryException {
		return getGermplasmDataManager().getGermplasms(gids);
	}

	@Override
	public List<Location> getAllFieldLocations(int locationId)
			throws MiddlewareQueryException {
    	return getLocationDataManager().getAllFieldLocations(locationId);
	}

	@Override
	public List<Location> getAllBlockLocations(int fieldId)
			throws MiddlewareQueryException {
        return getLocationDataManager().getAllBlockLocations(fieldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(int blockId)
			throws MiddlewareQueryException {
	    return getLocationDataManager().getBlockInformation(blockId);
	}

	@Override
	public List<Location> getAllFields() throws MiddlewareQueryException {
	    return getLocationDataManager().getAllFields();
	}

	@Override
	public int addFieldLocation(String fieldName, Integer parentLocationId, Integer currentUserId)
			throws MiddlewareQueryException {
	    LocationDataManager manager = getLocationDataManager();
	    
	    Integer lType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
	    Location location = new Location(null, lType, 0, fieldName, "-", 0, 0, 0, 0, 0);

	    Integer dType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
	    Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentLocationId), 0, 0);

	    return manager.addLocationAndLocdes(location, locdes);
	}

	@Override
	public int addBlockLocation(String blockName, Integer parentFieldId, Integer currentUserId)
			throws MiddlewareQueryException {
        LocationDataManager manager = getLocationDataManager();
        
        Integer lType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());
        Location location = new Location(null, lType, 0, blockName, "-", 0, 0, 0, 0, 0);

        Integer dType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode());
        Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentFieldId), 0, 0);
        
        return manager.addLocationAndLocdes(location, locdes);
	}    
    
	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId)
            throws MiddlewareQueryException {
		return getStudyDataManager().getAllFieldMapsInBlockByBlockId(blockId);
	}
}
