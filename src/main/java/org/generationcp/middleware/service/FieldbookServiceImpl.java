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
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.FieldbookService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FieldbookServiceImpl extends Service implements FieldbookService {
    
    private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

    public FieldbookServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    @Override
    public List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException{
        return getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.N);
    }
    
    @Override 
    public List<StudyDetails> getAllLocalTrialStudyDetails() throws MiddlewareQueryException{
        return getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.T);
    }

    @Override
    public List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(trialIdList, StudyType.T);
    }
    
    @Override 
    public List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(nurseryIdList, StudyType.N);
    }

    @Override 
    public List<Location> getAllLocations()throws MiddlewareQueryException{
    	GermplasmDataManager germplasmDataManager = getGermplasmDataManager();
    	return germplasmDataManager.getAllLocations();
    }

    @Override
    public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, String fieldmapUUID) throws MiddlewareQueryException {
     
        getStudyDataManager().saveOrUpdateFieldmapProperties(info, fieldmapUUID);
    
    }

    
    
    @Override
    public Study getStudy(int studyId) throws MiddlewareQueryException  {
    	//not using the variable type
        return getStudyDataManager().getStudy(studyId, false);
    }

    @Override           
    public List<Location> getFavoriteLocationByProjectId(List<Long> locationIds) throws MiddlewareQueryException {
        List<Location> locationList = new ArrayList<Location>();
        
        for(int i = 0 ; i < locationIds.size() ; i++){
            Integer locationId = Integer.valueOf(locationIds.get(i).toString());
            Location location = getGermplasmDataManager().getLocationByID(locationId);
            locationList.add(location);
        }
        
        return locationList;
    }
    
    @Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId) throws MiddlewareQueryException {
        return getStudyDataManager().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId);
    }

    @Override
    public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
        return getStudyDataManager().getDatasetReferences(studyId);
    }

	@Override
	public int getNextGermplasmId() throws MiddlewareQueryException {
		return getGermplasmDataManager().getNextNegativeId().intValue();
	}

	@Override
	public Integer getGermplasmIdByName(String name)
			throws MiddlewareQueryException {
		
		 List<Germplasm> germplasmList = getGermplasmDataManager().getGermplasmByName(name, 0, 1, Operation.EQUAL);
		 Integer gid = null;
		 if(germplasmList != null && germplasmList.size() > 0){
			 gid = germplasmList.get(0).getGid();
		 }
		 return gid;
	}

	@Override
    public Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role)
            throws MiddlewareQueryException {
        return getOntologyDataManager().getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
    }
    
	@Override
    public Workbook getNurseryDataSet(int id) throws MiddlewareQueryException {
        Workbook workbook = getWorkbookBuilder().create(id);                        
        return workbook;
    }

	@Override
    public void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            
            List<MeasurementVariable> variates = workbook.getVariates();
            List<MeasurementRow> observations = workbook.getObservations();
            
            if (variates != null){
                for (MeasurementVariable variate : variates){
                    for (MeasurementRow row : observations){
                        for (MeasurementData field : row.getDataList()){
                            if (variate.getName().equals(field.getLabel())){
                                Phenotype phenotype = getPhenotypeDao().getById(field.getPhenotypeId());
                                if (phenotype == null){
                                    phenotype = new Phenotype();
                                    phenotype.setPhenotypeId(getPhenotypeDao().getNegativeId("phenotypeId"));
                                }
                                getPhenotypeSaver().saveOrUpdate((int) row.getExperimentId(), variate.getTermId(), 
                                            variate.getStoredIn(), field.getValue(), phenotype);
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

        try {
            trans = session.beginTransaction();
            
            /* call Save Listnms;
            * For each entry in the advance list table
            * if (gid != null) 
            *   germplasm = findByGid(gid)
            *   if (germplasm == null)
            *      germplasm = findByName(table.desig)
            *      
            *  if (germplasm != null) 
            *      call Save ListData using gid from germplasm.gid
            *  else 
            *      call Save Germplasm - note new gid generated
            *  call Save Names using NType = 1027, NVal = table.desig, NStat = 0         // FB-level
            *  call Save Names using NType = 1028, NVal = table.germplasmBCID, NStat = 1 // FB-level
            *  call Save Names using NType = 1029, NVal = table.cross, NStat = 0         // FB-level    
            *  call Save ListData
            */
            
            // Save germplasm list
            listId = germplasmListDao.getNegativeId("id");
            germplasmList.setId(listId);
            germplasmListDao.saveOrUpdate(germplasmList);


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
                        List<Name> names = germplasms.get(germplasm);
                        String germplasmName = null;

                        if (names != null){ 
                            if (names.size() > 1){ // crop == CIMMYT WHEAT (crop with more than one name saved)
                                // Germplasm name is the Names entry with NType = 1027, NVal = table.desig, NStat = 0
                                for (Name name: names){
                                    if (name.getTypeId() == GermplasmNameType.UNRESOLVED_NAME.getUserDefinedFieldID() 
                                            && name.getNstat() == 0){
                                        germplasmName = name.getNval();
                                        break;
                                    }
                                }
                            }else if (names.size() == 1){ // other crops
                                germplasmName = names.get(0).getNval();
                            }
                            
                        }

                        List<Germplasm> germplasmsFound = getGermplasmDataManager()
                                .getGermplasmByName(germplasmName, 0, 1, Operation.EQUAL);
                        
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
                        nameDao.saveOrUpdate(name);
                    }
                    
                    // Save germplasm
                    germplasm.setGid(gId);
                    germplasm.setLgid(Integer.valueOf(0));
                    germplasmDao.saveOrUpdate(germplasm);

                } 
                               
                // Save germplasmListData
                Integer germplasmListDataId = getGermplasmListDataDAO().getNegativeId("id");
                germplasmListData.setId(germplasmListDataId);
                germplasmListData.setGid(germplasm.getGid());
                germplasmListData.setList(germplasmList);
                getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
                
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms="
                    + germplasms + ", germplasmList=" + germplasmList + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
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
    
    private List<Name> getByGidAndNtype(int gid, GermplasmNameType nType) throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        List<Name> names = getNameDao().getByGIDWithFilters(gid, null, nType);
        if (names == null || names.isEmpty()) {
            setWorkingDatabase(Database.LOCAL);
            names = getNameDao().getByGIDWithFilters(gid, null, nType);
        }
        return names;
    }

    @Override
    public Method getBreedingMethodById(int mid) throws MiddlewareQueryException {
        return getGermplasmDataManager().getMethodByID(mid);
    }
    
    @Override
    public Germplasm getGermplasmByGID(int gid) throws MiddlewareQueryException {
        return getGermplasmDataManager().getGermplasmByGID(gid);
    }
	
}
