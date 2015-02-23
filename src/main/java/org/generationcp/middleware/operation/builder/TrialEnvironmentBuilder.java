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
		} else if (storedInId == TermId.LATITUDE_STORAGE.getId()) {
			value = location.getLatitude() == null ? null : Double.toString(location.getLatitude());
		} else if (storedInId == TermId.LONGITUDE_STORAGE.getId()) {
			value = location.getLongitude() == null ? null : Double.toString(location.getLongitude());
		} else if (storedInId == TermId.DATUM_STORAGE.getId()) {
	    	value = location.getGeodeticDatum();
	    } else if (storedInId == TermId.ALTITUDE_STORAGE.getId()) {
			value = location.getAltitude() == null ? null : Double.toString(location.getAltitude());
		} else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
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
		if(includePublicData) {
			setWorkingDatabase(Database.CENTRAL);
			environments.addAll(getGeolocationDao().getAllTrialEnvironments());
		}
		
		setWorkingDatabase(Database.LOCAL);
		List<TrialEnvironment> localEnvironments = getGeolocationDao().getAllTrialEnvironments();
		buildLocalTrialEnvironment(environments, localEnvironments);
		
		return environments;
	}
	
	public TrialEnvironments buildTrialEnvironments(List<TrialEnvironment> trialEnvironments) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		List<TrialEnvironment> localEnvironments = new ArrayList<TrialEnvironment>();
		
		// collect local environments in separate list
		for (TrialEnvironment envt : trialEnvironments){
			if (envt.getId() < 0){
				localEnvironments.add(envt);
			} else {
				environments.add(envt);
			}
		}
		
		buildLocalTrialEnvironment(environments, localEnvironments);
		
		return environments;
	}

	
	// set location name, country name and province name to local environments from central db
	private void buildLocalTrialEnvironment(TrialEnvironments environments,
			List<TrialEnvironment> localEnvironments) throws MiddlewareQueryException {
	
		if (environments != null && localEnvironments != null && !localEnvironments.isEmpty()) {
			setWorkingDatabase(Database.CENTRAL);
			Set<Integer> ids = new HashSet<Integer>();
			for (TrialEnvironment environment : localEnvironments) {
				if (environment.getLocation() != null && environment.getLocation().getId() != null 
				&& environment.getLocation().getId().intValue() >= 0) {
					ids.add(environment.getLocation().getId());
				}
			}
			List<LocationDto> newLocations = getLocationDao().getLocationDtoByIds(ids);
			for (TrialEnvironment environment : localEnvironments) {
				if (environment.getLocation() != null && newLocations != null && newLocations.indexOf(environment.getLocation().getId()) > -1) {
					LocationDto newLocation = newLocations.get(newLocations.indexOf(environment.getLocation().getId()));
					environment.getLocation().setCountryName(newLocation.getCountryName());
					environment.getLocation().setLocationName(newLocation.getLocationName());
					environment.getLocation().setProvinceName(newLocation.getProvinceName());
				}
				environments.add(environment);
			}
		}
		
	}
	
	public long countAllTrialEnvironments() throws MiddlewareQueryException{
		setWorkingDatabase(Database.CENTRAL);
		long centralCount = getGeolocationDao().countAllTrialEnvironments();
        if (LOG.isDebugEnabled()){
            LOG.debug("CENTRAL = " + centralCount);
        }
		setWorkingDatabase(Database.LOCAL);
		long localCount =  getGeolocationDao().countAllTrialEnvironments();
        if (LOG.isDebugEnabled()){
            LOG.debug("LOCAL = " + localCount);
        }
		return centralCount + localCount;
	}
	
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TrialEnvironmentProperty> properties = new ArrayList<TrialEnvironmentProperty>();
		setWorkingDatabase(Database.CENTRAL);
		properties.addAll(getGeolocationDao().getPropertiesForTrialEnvironments(environmentIds));

		setWorkingDatabase(Database.LOCAL);
		List<TrialEnvironmentProperty> localProperties = getGeolocationDao().getPropertiesForTrialEnvironments(environmentIds);
		setWorkingDatabase(Database.CENTRAL);
		Set<Integer> ids = new HashSet<Integer>();
		for (TrialEnvironmentProperty property : localProperties) {
			if (property.getId() >= 0) {
				ids.add(property.getId());
			}
		}
        if (LOG.isDebugEnabled()){
            LOG.debug("IDS ARE " + ids);
        }
		List<CVTerm> terms = getCvTermDao().getByIds(new ArrayList<Integer>(ids));
		for (TrialEnvironmentProperty property : localProperties) {
			int index = properties.indexOf(property);
			if (index > -1) {
				properties.get(index).setNumberOfEnvironments(
										properties.get(index).getNumberOfEnvironments().intValue() +
										property.getNumberOfEnvironments().intValue());
			} else {
				CVTerm term = null;
				for (CVTerm aTerm : terms) {
					if (aTerm.getCvTermId().equals(property.getId())) {
						term = aTerm;
						break;
					}
				}
				if (term != null) {
					property.setName(term.getName());
					property.setDescription(term.getDefinition());
				}
				properties.add(property);
			}
		}

		return properties;
	}

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

        //Step 3.5: Lookup central locations and traits used in local environments
        List<Integer> locationIds = new ArrayList<Integer>();
        List<Integer> traitIds = new ArrayList<Integer>();
        
        for (TrialEnvironment env : localTrialEnvironments){
            List<TraitInfo> localTraits = env.getTraits();
            if (localTraits != null){
                for (TraitInfo trait : localTraits){
                    traitIds.add(trait.getId());
                }
            }
            LocationDto location = env.getLocation();
			if (location != null && location.getId() > 0){
				locationIds.add(location.getId());
            }
        }
        
        if (!locationIds.isEmpty()){
        	setWorkingDatabase(Database.CENTRAL);
        	getGeolocationDao().setLocationNameProvinceAndCountryForLocationsIds(localTrialEnvironments, locationIds);
        }
        
        if (!traitIds.isEmpty()){
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
	                            if (newEnv != null && newEnv.getTraits() != null && !newEnv.getTraits().isEmpty()){ 
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
		setWorkingDatabase(Database.CENTRAL);
		environments.addAll(getGeolocationDao().getEnvironmentsForTraits(traitIds));
		
		setWorkingDatabase(Database.LOCAL);
		TrialEnvironments localEnvironments = getGeolocationDao().getEnvironmentsForTraits(traitIds);
		if (localEnvironments != null && localEnvironments.getTrialEnvironments() != null) {
			setWorkingDatabase(Database.CENTRAL);
			Set<Integer> ids = new HashSet<Integer>();
			for (TrialEnvironment environment : localEnvironments.getTrialEnvironments()) {
				if (environment.getLocation() != null && environment.getLocation().getId() != null 
				&& environment.getLocation().getId().intValue() >= 0) {
					ids.add(environment.getLocation().getId());
				}
			}
			List<LocationDto> newLocations = getLocationDao().getLocationDtoByIds(ids);
			for (TrialEnvironment environment : localEnvironments.getTrialEnvironments()) {
				if (environment.getLocation() != null && newLocations != null && newLocations.indexOf(environment.getLocation().getId()) > -1) {
					LocationDto newLocation = newLocations.get(newLocations.indexOf(environment.getLocation().getId()));
					environment.getLocation().setCountryName(newLocation.getCountryName());
					environment.getLocation().setLocationName(newLocation.getLocationName());
					environment.getLocation().setProvinceName(newLocation.getProvinceName());
				}
				environments.add(environment);
			}
		}
		
		return environments;
	}

}
