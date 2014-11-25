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
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class TrialEnvironmentBuilder extends Builder {

    private static final Logger LOG = LoggerFactory.getLogger(TrialEnvironmentBuilder.class);
    
	public TrialEnvironmentBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public TrialEnvironments getTrialEnvironmentsInDataset(int studyId, int datasetId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
			DmsProject project = getDataSetBuilder().getTrialDataset(studyId, datasetId);
			DataSet dataSet = getDataSetBuilder().build(project.getProjectId());
		    Study study = getStudyBuilder().createStudy(dataSet.getStudyId());
		
		    VariableTypeList trialEnvironmentVariableTypes = getTrialEnvironmentVariableTypes(study, dataSet);
		    Set<Geolocation> locations = getGeoLocations(datasetId);
		
		    return buildTrialEnvironments(locations, trialEnvironmentVariableTypes);
		}
		return new TrialEnvironments();
	}

	private VariableTypeList getTrialEnvironmentVariableTypes(Study study, DataSet dataSet) {
		VariableTypeList trialEnvironmentVariableTypes = new VariableTypeList();
		trialEnvironmentVariableTypes.addAll(study.getVariableTypesByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		trialEnvironmentVariableTypes.addAll(dataSet.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		return trialEnvironmentVariableTypes;
	}

	private Set<Geolocation> getGeoLocations(int datasetId) throws MiddlewareQueryException {
		return getGeolocationDao().findInDataSet(datasetId);
	}

	private TrialEnvironments buildTrialEnvironments(Set<Geolocation> locations,
			                                         VariableTypeList trialEnvironmentVariableTypes) {
		
		TrialEnvironments trialEnvironments = new TrialEnvironments();
		for (Geolocation location : locations) {
			VariableList variables = new VariableList();
			for (VariableType variableType : trialEnvironmentVariableTypes.getVariableTypes()) {
				Variable variable = new Variable(variableType, getValue(location, variableType));
				variables.add(variable);
			}
			trialEnvironments.add(new TrialEnvironment(location.getLocationId(), variables));
		}
		return trialEnvironments;
	}

	private String getValue(Geolocation location, VariableType variableType) {
		String value = null;
		int storedInId = variableType.getStandardVariable().getStoredIn().getId();
		if (storedInId == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
			value = location.getDescription();
		}
		else if (storedInId == TermId.LATITUDE_STORAGE.getId()) {
			value = location.getLatitude() == null ? null : Double.toString(location.getLatitude());
		}
		else if (storedInId == TermId.LONGITUDE_STORAGE.getId()) {
			value = location.getLongitude() == null ? null : Double.toString(location.getLongitude());
		}
	    else if (storedInId == TermId.DATUM_STORAGE.getId()) {
	    	value = location.getGeodeticDatum();
	    }
		else if (storedInId == TermId.ALTITUDE_STORAGE.getId()) {
			value = location.getAltitude() == null ? null : Double.toString(location.getAltitude());
		}
		else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			value = getPropertyValue(variableType.getId(), location.getProperties());
		}
		return value;
	}

	private String getPropertyValue(int id, List<GeolocationProperty> properties) {
		String value = null;
		if (properties != null) {
		    for (GeolocationProperty property : properties) {
		    	if (property.getTypeId() == id) {
		    		value = property.getValue();
		    		break;
		    	}
		    }
		}
		return value;
	}
	
	public TrialEnvironments getAllTrialEnvironments(boolean includePublicData) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		setWorkingDatabase(Database.LOCAL);
		environments.addAll(getGeolocationDao().getAllTrialEnvironments());		
		return environments;
	}
	
	public long countAllTrialEnvironments() throws MiddlewareQueryException{
		setWorkingDatabase(Database.LOCAL);
		return getGeolocationDao().countAllTrialEnvironments();
	}
	
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		return  getGeolocationDao().getPropertiesForTrialEnvironments(environmentIds);
	}

	//TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
    public List<GermplasmPair> getEnvironmentForGermplasmPairs(List<GermplasmPair> germplasmPairs) throws MiddlewareQueryException {
        List<TrialEnvironment> trialEnvironments = new ArrayList<TrialEnvironment>();
        
        // Get gids for local and gids for central. Local may contain (+) and (-) gids
        Set<Integer> centralGids = new HashSet<Integer>();
        Set<Integer> localGids = new HashSet<Integer>();
        for (GermplasmPair pair : germplasmPairs){
            int gid = pair.getGid1();
            for (int i = 0; i < 2; i++){
                if (gid < 0){
                    localGids.add(gid);                    
                } else {
                    centralGids.add(gid);
                    localGids.add(gid);
                }
                gid = pair.getGid2();
            }
        }
        
        
        // GET DETAILS FROM CENTRAL
        setWorkingDatabase(Database.CENTRAL);
        
        // Step 1: Get Trial Environments for each GID (Map<GID, EnvironmentIds>)
        Map<Integer, Set<Integer>> centralGermplasmEnvironments = getExperimentStockDao().getEnvironmentsOfGermplasms(centralGids);

        // Step 2: Get the trial environment details
        Set<Integer> centralEnvironmentIds = getEnvironmentIdsFromMap(centralGermplasmEnvironments);
        Set<TrialEnvironment> centralTrialEnvironmentDetails = new HashSet<TrialEnvironment>();
        centralTrialEnvironmentDetails.addAll(getGeolocationDao().getTrialEnvironmentDetails(centralEnvironmentIds));

        // Step 3: Get environment traits
        trialEnvironments = getPhenotypeDao().getEnvironmentTraits(centralTrialEnvironmentDetails);

        // GET DETAILS FROM LOCAL
        setWorkingDatabase(Database.LOCAL);
        
        // Step 1: Get Trial Environments for each GID
        Map<Integer, Set<Integer>> localGermplasmEnvironments = getExperimentStockDao().getEnvironmentsOfGermplasms(localGids);

        // Step 2: Get the trial environment details
        Set<Integer> localEnvironmentIds = getEnvironmentIdsFromMap(localGermplasmEnvironments);
        Set<TrialEnvironment> localTrialEnvironmentDetails = new HashSet<TrialEnvironment>();
        localTrialEnvironmentDetails.addAll(getGeolocationDao().getTrialEnvironmentDetails(localEnvironmentIds));
        
        // Step 3: Get environment traits
        List<TrialEnvironment> localTrialEnvironments = getPhenotypeDao().getEnvironmentTraits(localTrialEnvironmentDetails);

        // STEP 3B: Get the trait id and name from central (ONLY FOR LOCAL)
        List<Integer> traitIds = new ArrayList<Integer>();
        
        for (TrialEnvironment env : localTrialEnvironments){
            List<TraitInfo> localTraits = env.getTraits();
            if (localTraits != null){
                for (TraitInfo trait : localTraits){
                    traitIds.add(trait.getId());
                }
            }
        }
        
        if (traitIds.size() > 0){
            setWorkingDatabase(Database.CENTRAL);
            List<TraitInfo> localTraitDetails = getCvTermDao().getTraitInfo(traitIds);
    
            for (TrialEnvironment env : localTrialEnvironments){
                List<TraitInfo> localTraits = env.getTraits();
                List<TraitInfo> newLocalTraits = new ArrayList<TraitInfo>();
                if (localTraits != null){
                    for (TraitInfo trait : localTraits){
                        for (TraitInfo traitDetails : localTraitDetails){
                            if (trait.equals(traitDetails)){
                                newLocalTraits.add(traitDetails);
                                break;
                            }
                        }
                    }
                    env.setTraits(newLocalTraits);
                }
            }
        }
        
        // Consolidate local and central trial environments with trait details
        trialEnvironments.addAll(localTrialEnvironments);
        
        // Step 4: Build germplasm pairs. Get what's common between GID1 AND GID2
        for (GermplasmPair pair : germplasmPairs){
            int gid1 = pair.getGid1();
            int gid2 = pair.getGid2();
            
            Set<Integer> g1Environments = centralGermplasmEnvironments.get(gid1);
            if (g1Environments != null) {
            	g1Environments.addAll(localGermplasmEnvironments.get(gid1));
            } else {
            	g1Environments = localGermplasmEnvironments.get(gid1);
            }
            Set<Integer> g2Environments = centralGermplasmEnvironments.get(gid2);
            if (g2Environments != null) {
            	g2Environments.addAll(localGermplasmEnvironments.get(gid2));
            } else {
            	g2Environments = localGermplasmEnvironments.get(gid2);
            }

            TrialEnvironments environments = new TrialEnvironments();
            
            if (g1Environments != null && g2Environments != null){
	            for (Integer env1 : g1Environments){
	                for (Integer env2 : g2Environments){
	
	                    if (env1.equals(env2)){
	                        int index = trialEnvironments.indexOf(new TrialEnvironment(env1));
	                        if (index > -1){
	                            TrialEnvironment newEnv = trialEnvironments.get(index);
	                            // If the environment has no traits, do not include in the list of common environments
	                            if (newEnv != null && newEnv.getTraits() != null && newEnv.getTraits().size() > 0){ 
	                                environments.add(newEnv);
	                            }
	                        }
	                    }
	                }
	            }
            }
            
            pair.setTrialEnvironments(environments);
        }
        
        return germplasmPairs;
    }
    

    private Set<Integer> getEnvironmentIdsFromMap(Map<Integer, Set<Integer>> germplasmEnvironments){
        Set<Integer> idsToReturn = new HashSet<Integer>();
        
        for (Entry<Integer, Set<Integer>> environmentIds : germplasmEnvironments.entrySet()){
            Set<Integer> ids = environmentIds.getValue();
            for (Integer id : ids){
                idsToReturn.add(id);
            }            
        }
        return idsToReturn;
        
    }
    
    public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException {
    	
		TrialEnvironments environments = new TrialEnvironments();
		setWorkingDatabase(Database.LOCAL);
		environments.addAll(getGeolocationDao().getEnvironmentsForTraits(traitIds));
		
		return environments;
	}

}
