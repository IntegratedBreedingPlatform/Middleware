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
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.service.api.FieldbookService;

public class FieldbookServiceImpl extends Service implements FieldbookService {

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
    public FieldMapInfo getFieldMapInfoOfTrial(int trialId) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(trialId, StudyType.T);
    }
    
    @Override 
    public FieldMapInfo getFieldMapInfoOfNursery(int nurseryId) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(nurseryId, StudyType.N);
    }

    @Override 
    public List<Location> getAllLocations()throws MiddlewareQueryException{
    	GermplasmDataManager germplasmDataManager = getGermplasmDataManager();
    	return germplasmDataManager.getAllLocations();
    }

    @Override
    public void saveOrUpdateFieldmapProperties(FieldMapInfo info) throws MiddlewareQueryException {
     
        getStudyDataManager().saveOrUpdateFieldmapProperties(info);
    
    }

    
    
    @Override
    public Study getStudy(int studyId) throws MiddlewareQueryException  {
        Study study = getStudyDataManager().getStudy(studyId);
        return study;
    }

    @Override           
    public List<Location> getFavoriteLocationByProjectId(List<Long> locationIds) throws MiddlewareQueryException {
        // TODO Auto-generated method stub
        
        List<Location> locationList = new ArrayList();
        
        for(int i = 0 ; i < locationIds.size() ; i++){
            Integer locationId = Integer.valueOf(locationIds.get(i).toString());
            Location location = getGermplasmDataManager().getLocationByID(locationId);
            locationList.add(location);
        }
        
        return locationList;
    }

    //TODO: REMOVE THIS, THIS IS JUST FOR TESTING
    @Override
    public int getGeolocationId(int projectId) throws MiddlewareQueryException {
        setWorkingDatabase(projectId);
        Set<Integer> geolocations = getGeolocationDao().getLocationIds(projectId);
        if (geolocations == null || geolocations.size() == 0) {
            throw new MiddlewareQueryException("error in test data");
        }
        return geolocations.iterator().next();
    }
    @Override
    public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
        return getStudyDataManager().getDatasetReferences(studyId);
    }
}
