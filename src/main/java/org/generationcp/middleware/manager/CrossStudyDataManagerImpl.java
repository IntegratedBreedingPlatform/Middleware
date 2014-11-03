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

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Implementation of the CrossStudyDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 * 
 */
public class CrossStudyDataManagerImpl extends DataManager implements CrossStudyDataManager{

    public CrossStudyDataManagerImpl() {
    }

    public CrossStudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public CrossStudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }


    @Override
    public TrialEnvironments getAllTrialEnvironments(boolean includePublicData) throws MiddlewareQueryException {
        return getTrialEnvironmentBuilder().getAllTrialEnvironments(includePublicData);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public TrialEnvironments getTrialEnvironments(int start, int numOfRows) throws MiddlewareQueryException {
    	List<String> methodNames = Arrays.asList("countAllTrialEnvironments", "getTrialEnvironments");
    	List<TrialEnvironment> environmentList =  getFromCentralAndLocalByMethod(getGeolocationDao(), methodNames, 
    			start, numOfRows, new Object[]{}, new Class[]{});
    	
    	return getTrialEnvironmentBuilder().buildTrialEnvironments(environmentList);
    }
    
    @Override
    public long countAllTrialEnvironments() throws MiddlewareQueryException {
    	return getTrialEnvironmentBuilder().countAllTrialEnvironments();
    }

    @Override
    public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> trialEnvtIds) throws MiddlewareQueryException {
        return getTrialEnvironmentBuilder().getPropertiesForTrialEnvironments(trialEnvtIds);
    }

    @Override
    public List<StudyReference> getStudiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
        return getStudyNodeBuilder().getStudiesForTrialEnvironments(environmentIds);
    }
    
    @Override
    public List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        return getTraitBuilder().getTraitsForNumericVariates(environmentIds);
    }

    @Override
    public List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException{
        return getTraitBuilder().getTraitsForCharacterVariates(environmentIds);
    }
    
    @Override
    public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException{
        return getTraitBuilder().getTraitsForCategoricalVariates(environmentIds);
    }

    @Override
    public List<GermplasmPair> getEnvironmentsForGermplasmPairs(List<GermplasmPair> germplasmPairs) throws MiddlewareQueryException{
        return getTrialEnvironmentBuilder().getEnvironmentForGermplasmPairs(germplasmPairs);
    }

    @Override
    public List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds, 
            List<Integer> environmentIds) throws MiddlewareQueryException{
        return getTraitBuilder().getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
    }
    
    @Override
    public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException{
        return getTraitBuilder().getObservationsForTraits(traitIds, environmentIds);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds, int start, int numOfRows) throws MiddlewareQueryException{
        List<String> methods = Arrays.asList("countObservationForTraits", "getObservationForTraits");
        Object[] parameters = new Object[] { traitIds, environmentIds};
    	return (List<Observation>) getFromCentralAndLocalByMethod(
    			getPhenotypeDao(), methods, start, numOfRows, 
    			parameters, parameters, new Class[] { List.class, List.class});
    }
    
    @Override
    public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException{
    	return getTraitBuilder().getObservationsForTrait(traitId, environmentIds);
    }

    @Override
    public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException{
    	return getTrialEnvironmentBuilder().getEnvironmentsForTraits(traitIds);
    }
    
    @Override
    public List<GermplasmLocationInfo> getGermplasmLocationInfoByEnvironmentIds(Set<Integer> environmentIds) throws MiddlewareQueryException {
    	List<GermplasmLocationInfo> result = new ArrayList<GermplasmLocationInfo>();
    	if(environmentIds != null && !environmentIds.isEmpty()) {
	    	if(setWorkingDatabase(Database.CENTRAL)) {
	    		result.addAll(getBreedersQueryDao().getGermplasmLocationInfoByEnvironmentIds(environmentIds));
	    	}
	    	if(setWorkingDatabase(Database.LOCAL)) {
	    		result.addAll(getBreedersQueryDao().getGermplasmLocationInfoByEnvironmentIds(environmentIds));
	    	}
    	}
    	return result;
    }

	@Override
	public List<Integer> getTrialEnvironmentIdsForGermplasm(Set<Integer> gids) throws MiddlewareQueryException {
    	List<Integer> result = new ArrayList<Integer>();
    	if(gids != null && !gids.isEmpty()) {
	    	if(setWorkingDatabase(Database.CENTRAL)) {
	    		result.addAll(getBreedersQueryDao().getTrialEnvironmentIdsForGermplasm(gids));
	    	}
	    	if(setWorkingDatabase(Database.LOCAL)) {
	    		result.addAll(getBreedersQueryDao().getTrialEnvironmentIdsForGermplasm(gids));
	    	}
    	}
    	return result;
	}
}
