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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.fieldbook.NonEditableFactors;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
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
    	List<StudyDetails> studyDetailList =  getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.T);
        List<StudyDetails> newList = new ArrayList<StudyDetails>();
    	for(StudyDetails detail : studyDetailList){
    		if(detail.hasRows())
    			newList.add(detail);
    	}
    	return newList;
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
    public List<Location> getAllBreedingLocations()throws MiddlewareQueryException{
        return getLocationDataManager().getAllBreedingLocations();
    }
    
    @Override 
    public List<Location> getAllSeedLocations()throws MiddlewareQueryException{
        Integer seedLType = getLocationDataManager().getUserDefinedFieldIdOfCode(
                UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
        return getLocationDataManager().getLocationsByType(seedLType);
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

	@SuppressWarnings("unchecked")
	@Override
    public void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        long startTime = System.currentTimeMillis();

        try {
            trans = session.beginTransaction();
            
            List<Integer> deletedVariateIds = getDeletedVariateIds(workbook.getVariates());
            
            List<MeasurementVariable> variates = workbook.getVariates();
            List<MeasurementVariable> factors = workbook.getFactors();
            List<MeasurementRow> observations = workbook.getObservations();
            
            int i = 0;
            getWorkbookSaver().saveWorkbookVariables(workbook);
            getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);
            
            final Map<String, ?> variableMap = getWorkbookSaver().saveVariables(workbook);
            
            // unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
            Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
            Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
            
            // unpack maps
            // Strings
            List<String> trialHeaders = headerMap.get("trialHeaders");
            
            //VariableTypeLists
            VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
            
            //get the trial dataset id
            Integer trialDatasetId = workbook.getTrialDatasetId();
            if (trialDatasetId == null) {
                trialDatasetId = getWorkbookBuilder().getTrialDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
            }             
            
            //save trial observations
            getWorkbookSaver().saveTrialObservations(workbook);
            
            Integer measurementDatasetId = workbook.getMeasurementDatesetId();
            if (measurementDatasetId == null) {
            	measurementDatasetId = getWorkbookBuilder().getMeasurementDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
            }
    
            //save factors
            getWorkbookSaver().createStocksIfNecessary(measurementDatasetId, workbook, effectVariables, trialHeaders);
            
            if (factors != null) {
            	for (MeasurementVariable factor : factors) {
            		if (NonEditableFactors.find(factor.getTermId()) == null) {
            			for (MeasurementRow row : observations){
	                        for (MeasurementData field : row.getDataList()){
	                            if (factor.getName().equals(field.getLabel())){
	                            	if (factor.getStoredIn() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
	                            		getExperimentPropertySaver().saveOrUpdateProperty(getExperimentDao().getById(row.getExperimentId()), factor.getTermId(), field.getValue());
	                            	} 
	                            }
	                        }
            			}
            		}
            	}
            }
            
            //save variates
            if (variates != null){
                for (MeasurementVariable variate : variates){
                	if (deletedVariateIds != null && !deletedVariateIds.isEmpty()
                			&& deletedVariateIds.contains(variate.getTermId())) {
                		//skip this was already deleted.
                	}
                	else {
	                    for (MeasurementRow row : observations){
	                        for (MeasurementData field : row.getDataList()){
	                            if (variate.getName().equals(field.getLabel())){
	                            	Phenotype phenotype = getPhenotypeDao().getPhenotypeByProjectExperimentAndType(measurementDatasetId, row.getExperimentId(), variate.getTermId());
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
            }
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, LOG);
        }
        
        LOG.debug("========== saveMeasurementRows Duration (ms): " 
                + ((System.currentTimeMillis() - startTime)/60));
        
    }

	@Override
	public List<Method> getAllBreedingMethods(boolean filterOutGenerative) throws MiddlewareQueryException {
		List<Method> methodList = filterOutGenerative ? getGermplasmDataManager().getAllMethodsNotGenerative() : getGermplasmDataManager().getAllMethods();
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
	public List<Method> getFavoriteBreedingMethods(List<Integer> methodIds, boolean filterOutGenerative)
			throws MiddlewareQueryException {
		 List<Method> methodList = new ArrayList<Method>();
		 	List<Integer> validMethodClasses = new ArrayList<Integer>();
	     	validMethodClasses.addAll(Method.BULKED_CLASSES);
	     	validMethodClasses.addAll(Method.NON_BULKED_CLASSES);
	        for(int i = 0 ; i < methodIds.size() ; i++){
	            Integer methodId = methodIds.get(i);
	            Method method = getGermplasmDataManager().getMethodByID(methodId);
                // filter out generative method types
	            
	            if (method!= null) {
	            	if(filterOutGenerative){
	    	            if (method.getMtype() == null || !method.getMtype().equals("GEN")) {
	    	            	 if(method.getGeneq() != null && validMethodClasses.contains(method.getGeneq())){
	                            methodList.add(method);
	    	            	 }
	    	            } 
	            	}else{
	            		methodList.add(method);
	            	}
	            }
	            
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

    public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
        return getUserDataManager().getAllPersonsOrderedByLocalCentral();
    }

    public int countPlotsWithRecordedVariatesInDataset(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
//        StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, StudyType.N, nurseryId);
        
//        int dataSetId = 0;
//        
//        //get observation dataset
//        List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(nurseryId);
//        if (datasetRefList != null) {
//            for (DatasetReference datasetRef : datasetRefList) {
//                if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName()) || 
//                        datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
//                    dataSetId = datasetRef.getId();
//                }
//            }
//        }
//        
//        //if not found in the list using the name, get dataset with Plot Data type
//        if (dataSetId == 0) {
//            DataSet dataset = getStudyDataManager().findOneDataSetByType(nurseryId, DataSetType.PLOT_DATA);
//            if (dataset != null){
//                dataSetId = dataset.getId();
//            }
//        }
        
//        return getStudyDataManager().countPlotsWithPlantsSelectedofDataset(dataSetId, variateIds);
        return getStudyDataManager().countPlotsWithRecordedVariatesInDataset(datasetId, variateIds);
    }
    
    @Override
    public List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds, List<Integer> propertyIds, boolean isRemoveProperties) 
            throws MiddlewareQueryException {
    	List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
    	
    	List<CVTerm> variables = new ArrayList<CVTerm>();
    	
    	Set<Integer> variableIds = new HashSet<Integer>();
    	
    	addAllVariableIdsInMode(variableIds, storedInIds, Database.CENTRAL);
    	addAllVariableIdsInMode(variableIds, storedInIds, Database.LOCAL);
    	
    	if (propertyIds != null && propertyIds.size() > 0) {
    	        Set<Integer> propertyVariableList = new HashSet<Integer>(); 
    	        createPropertyList(propertyVariableList, propertyIds, Database.CENTRAL);
    	        createPropertyList(propertyVariableList, propertyIds, Database.LOCAL);
    	        filterByProperty(variableIds, propertyVariableList, isRemoveProperties);
    	}
    	
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
    
    private void createPropertyList(Set<Integer> propertyVariableList
            , List<Integer> propertyIds, Database database) throws MiddlewareQueryException{
        setWorkingDatabase(database);
        for (Integer propertyId : propertyIds) {
            propertyVariableList.addAll(getCvTermRelationshipDao()
                        .getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), propertyId));
        }
    }
    
    private void filterByProperty(Set<Integer> variableIds
            , Set<Integer> variableListByProperty, boolean isRemoveProperties) 
                    throws MiddlewareQueryException{        
        //delete variables not in the list of filtered variables by property
        Iterator<Integer> iter = variableIds.iterator();
        boolean inList = false;
        
        if (isRemoveProperties) {
            //remove variables having the specified properties from the list
            while (iter.hasNext()) {
                Integer id = iter.next();
                for (Integer variable : variableListByProperty) {
                    if (id.equals(variable)) {
                        iter.remove();
                    }
                }
            }
        } else {
            //remove variables not in the property list
            while (iter.hasNext()) {
                inList = false;
                Integer id = iter.next();
                for (Integer variable : variableListByProperty) {
                    if (id.equals(variable)) {
                        inList = true;
                    }
                }
                if (inList == false) {
                    iter.remove();
                }
            }
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
	
	@Override
	public List<StandardVariableReference> getAllTreatmentLevels(List<Integer> hiddenFields) throws MiddlewareQueryException {
		List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getCvTermDao().getAllTreatmentFactors(hiddenFields, true));
		list.addAll(getCvTermDao().getAllTreatmentFactors(hiddenFields, false));
		setWorkingDatabase(Database.LOCAL);
		list.addAll(getCvTermDao().getAllTreatmentFactors(hiddenFields, true));
		list.addAll(getCvTermDao().getAllTreatmentFactors(hiddenFields, false));
		
		Collections.sort(list);
		return list;
	}
	
	@Override
	public List<StandardVariable> getPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields) throws MiddlewareQueryException {
		List<StandardVariable> treatmentPairs = new ArrayList<StandardVariable>();
		
		setWorkingDatabase(Database.CENTRAL);
		treatmentPairs.addAll(getCvTermDao().getAllPossibleTreatmentPairs(cvTermId, propertyId, hiddenFields));
		setWorkingDatabase(Database.LOCAL);
		treatmentPairs.addAll(getCvTermDao().getAllPossibleTreatmentPairs(cvTermId, propertyId, hiddenFields));
		
		List<Integer> termIds = new ArrayList<Integer>();
		Map<Integer, CVTerm> termMap = new HashMap<Integer, CVTerm>();
		
		for (StandardVariable pair : treatmentPairs) {
			termIds.add(pair.getProperty().getId());
			termIds.add(pair.getScale().getId());
			termIds.add(pair.getMethod().getId());
		}
		
		List<CVTerm> terms = new ArrayList<CVTerm>();
		setWorkingDatabase(Database.CENTRAL);
		terms.addAll(getCvTermDao().getByIds(termIds));
		setWorkingDatabase(Database.LOCAL);
		terms.addAll(getCvTermDao().getByIds(termIds));
		
		for (CVTerm term : terms) {
			termMap.put(term.getCvTermId(), term);
		}
		
		for (StandardVariable pair : treatmentPairs) {
			pair.getProperty().setName(termMap.get(pair.getProperty().getId()).getName());
			pair.getProperty().setDefinition(termMap.get(pair.getProperty().getId()).getDefinition());
			pair.getScale().setName(termMap.get(pair.getScale().getId()).getName());
			pair.getScale().setDefinition(termMap.get(pair.getScale().getId()).getDefinition());
			pair.getMethod().setName(termMap.get(pair.getMethod().getId()).getName());
			pair.getMethod().setDefinition(termMap.get(pair.getMethod().getId()).getDefinition());
		}
		
		return treatmentPairs;
	}
	
	@Override
	public TermId getStudyType(int studyId) throws MiddlewareQueryException {
		setWorkingDatabase(studyId);
		String value = getProjectPropertyDao().getValueByProjectIdAndTypeId(studyId, TermId.STUDY_TYPE.getId());
		if (value != null && NumberUtils.isNumber(value)) {
			return TermId.getById(Integer.valueOf(value));
		}
		return null;
	}

	@Override
	public List<FolderReference> getRootFolders(Database instance)
			throws MiddlewareQueryException {
		return getStudyDataManager().getRootFolders(instance);
	}

	@Override
	public List<Reference> getChildrenOfFolder(int folderId)
			throws MiddlewareQueryException {
		return getStudyDataManager().getChildrenOfFolder(folderId);
	}

	@Override
	public boolean isStudy(int id) throws MiddlewareQueryException {
		return getStudyDataManager().isStudy(id);
	}
	
	@Override
	public Location getLocationById(int id) throws MiddlewareQueryException {
		return getLocationDataManager().getLocationByID(id);
	}
	
	@Override
	public Person getPersonById(int id) throws MiddlewareQueryException {
		return getUserDataManager().getPersonById(id);
	}
	
	@Override
	public int getMeasurementDatasetId(int studyId, String studyName) throws MiddlewareQueryException {
		return getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
	}
	
	@Override
	public long countObservations(int datasetId) throws MiddlewareQueryException {
		return getExperimentBuilder().count(datasetId);
	}
	
	@Override
	public long countStocks(int datasetId) throws MiddlewareQueryException {
		return getStockBuilder().countStocks(datasetId);
	}
	
	@Override
	public boolean hasFieldMap(int datasetId) throws MiddlewareQueryException {
		return getExperimentBuilder().hasFieldmap(datasetId);
	}

	@Override
	public GermplasmList getGermplasmListById(Integer listId)
			throws MiddlewareQueryException {
		return getGermplasmListManager().getGermplasmListById(listId);
	}

	@Override
	public String getOwnerListName(Integer userId) throws MiddlewareQueryException {
		
		 User user=getUserDataManager().getUserById(userId);
        if(user != null){
            int personId=user.getPersonid();
            Person p =getUserDataManager().getPersonById(personId);
    
            if(p!=null){
                return p.getFirstName()+" "+p.getMiddleName() + " "+p.getLastName();
            }else{
                return user.getName();
            }
        } else {
            return "";
        }
	}
	
	@Override
	public StudyDetails getStudyDetails(Database database, StudyType studyType, int studyId) throws MiddlewareQueryException {
		return getStudyDataManager().getStudyDetails(database, studyType, studyId);
	}
	
	@Override
	public String getBlockId(int datasetId, String trialInstance) throws MiddlewareQueryException {
		setWorkingDatabase(datasetId);
		return getGeolocationPropertyDao().getValueOfTrialInstance(datasetId, TermId.BLOCK_ID.getId(), trialInstance);
	}
	
	@Override
	public String getFolderNameById(Integer folderId) throws MiddlewareQueryException {
	    return getStudyDataManager().getFolderNameById(folderId);
	}
	
	private List<Integer> getDeletedVariateIds(List<MeasurementVariable> variables) {
		List<Integer> ids = new ArrayList<Integer>();
		if (variables != null) {
			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.DELETE) {
					ids.add(variable.getTermId());
				}
			}
		}
		return ids;
	}
	
	@Override
	public boolean checkIfStudyHasFieldmap(int studyId) throws MiddlewareQueryException {
		return getExperimentBuilder().checkIfStudyHasFieldmap(studyId);
	}
	
	@Override
    public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
        return getStudyDataManager().checkIfStudyHasMeasurementData(datasetId, variateIds);
	}
	
    @Override
	public int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
	    return getStudyDataManager().countVariatesWithData(datasetId, variateIds);
	}
	
	@Override
	public void deleteObservationsOfStudy(int datasetId) throws MiddlewareQueryException {
	    requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction(); 

    	    getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
    	    trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with deleteObservationsOfStudy(): " + e.getMessage(), e, LOG);
        }
	}
	
	@Override
	public List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList, List<MeasurementVariable> variateList)
	throws MiddlewareQueryException {
		return getWorkbookBuilder().buildTrialObservations(trialDatasetId, factorList, variateList);
	}
	
	@Override
	public List<Integer> getGermplasmIdsByName(String name) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		List<Integer> gids = getNameDao().getGidsByName(name);
		setWorkingDatabase(Database.CENTRAL);
		gids.addAll(getNameDao().getGidsByName(name));
		return gids;
	}
	
	@Override
	public Integer addGermplasmName(String nameValue, int gid, int userId, int nameTypeId,int locationId, Integer date) throws MiddlewareQueryException {
		Name name = new Name(null, gid, nameTypeId, 0, userId, nameValue, locationId, date, 0);
		return getGermplasmDataManager().addGermplasmName(name);
	}
	
	@Override
	public Integer addGermplasm(String nameValue, int userId) throws MiddlewareQueryException {
		Name name = new Name(null, null, 1, 1, userId, nameValue, 0, 0, 0);
		Integer date = Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, userId, 0, 0, date, name);
		return getGermplasmDataManager().addGermplasm(germplasm, name);
	}
	
	@Override
	public Integer addGermplasm(Germplasm germplasm, Name name) throws MiddlewareQueryException {
        return getGermplasmDataManager().addGermplasm(germplasm, name);
	}
	
	@Override
	public Integer getProjectIdByName(String name) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		Integer id = getDmsProjectDao().getProjectIdByName(name);
		if (id == null) {
			setWorkingDatabase(Database.CENTRAL);
			id = getDmsProjectDao().getProjectIdByName(name);
		}
		return id;
	}
	
	@Override
	public MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(String property, String scale, String method, PhenotypicType role) 
	throws MiddlewareQueryException {
		MeasurementVariable variable = null;
		StandardVariable standardVariable = null;
		Integer id = getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (id != null) {
			standardVariable = getStandardVariableBuilder().create(id);
			return getMeasurementVariableTransformer().transform(standardVariable, false);
		}
		return variable;
	}

    @Override
    public void setTreatmentFactorValues(List<TreatmentVariable> treatmentFactors, int measurementDatasetID) throws MiddlewareQueryException {
        getWorkbookBuilder().setTreatmentFactorValues(treatmentFactors, measurementDatasetID);
    }

    @Override
	public Workbook getCompleteDataset(int datasetId, boolean isTrial) throws MiddlewareQueryException {
		return getDataSetBuilder().buildCompleteDataset(datasetId, isTrial);
	}
	
	@Override
	public List<UserDefinedField> getGermplasmNameTypes() throws MiddlewareQueryException {
	    return getGermplasmListManager().getGermplasmNameTypes();
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGids(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, List<Name>> map = new HashMap<Integer, List<Name>>();
		
		setWorkingDatabase(Database.CENTRAL);
		map.putAll(getNameDao().getNamesByGidsInMap(gids));
		setWorkingDatabase(Database.LOCAL);
		Map<Integer, List<Name>> locals = getNameDao().getNamesByGidsInMap(gids);
		if (locals != null && !locals.isEmpty()) {
			for (Integer key : locals.keySet()) {
				List<Name> names = locals.get(key);
				if (map.containsKey(key)) {
					map.get(key).addAll(names);
				}
				else {
					map.put(key, names);
				}
			}
		}
		
		return map;
	}

	@Override
	public int countGermplasmListDataByListId(Integer listId)
			throws MiddlewareQueryException {
		return (int)getGermplasmListManager().countGermplasmListDataByListId(listId);
	}
	
	@Override
	public Method getMethodById(int id) throws MiddlewareQueryException {
	    return getGermplasmDataManager().getMethodByID(id);
	}
	
	@Override
	public Method getMethodByCode(String code) throws MiddlewareQueryException {
	    return getGermplasmDataManager().getMethodByCode(code);
	}
	
	@Override
	public Method getMethodByName(String name) throws MiddlewareQueryException {
	   return getGermplasmDataManager().getMethodByName(name); 
	}
	
	@Override
	public void deleteStudy(int studyId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction(); 

            getStudyDestroyer().deleteStudy(studyId);
		
            trans.commit();
         } catch (Exception e) {
             rollbackTransaction(trans);
             logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, LOG);
         }
	}
	
	@Override
	public List<GermplasmList> getGermplasmListsByProjectId(int projectId, GermplasmListType type) throws MiddlewareQueryException {
		setWorkingDatabase(projectId);
		return getGermplasmListDAO().getByProjectIdAndType(projectId, type);
	}
	
	@Override
	public List<ListDataProject> getListDataProject(int listId) throws MiddlewareQueryException {
		setWorkingDatabase(listId);
		return getListDataProjectDAO().getByListId(listId);
	}

	@Override
	public void deleteListDataProjects(int projectId, GermplasmListType type) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		List<GermplasmList> lists = getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		if (lists != null && !lists.isEmpty()) {
			for (GermplasmList list : lists) {
				getListDataProjectDAO().deleteByListId(list.getId());
			}
		}
	}

	@Override
	public int saveOrUpdateListDataProject(int projectId,
			GermplasmListType type, Integer originalListId,
			List<ListDataProject> listDatas) throws MiddlewareQueryException {
		
        requireLocalDatabaseInstance();
        int listId = 0;
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction(); 

    		listId = getListDataProjectSaver().saveOrUpdateListDataProject(projectId, type, originalListId, listDatas);
		
            trans.commit();
        } catch (Exception e) {
        	e.printStackTrace();
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saveOrUpdateListDataProject(): " + e.getMessage(), e, LOG);
        }
        return listId;
	}
}
