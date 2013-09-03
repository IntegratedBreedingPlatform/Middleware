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

import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.hibernate.Session;


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
    public TrialEnvironments getAllTrialEnvironments() throws MiddlewareQueryException {
        return getTrialEnvironmentBuilder().getAllTrialEnvironments();
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
        
        if (traitIds.size() != germplasmIds.size() || germplasmIds.size() != environmentIds.size() 
                || traitIds.size() != environmentIds.size()){
            throw new MiddlewareQueryException("Lists must be of the same size.");
        }
        return getTraitBuilder().getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
    }
    
    @Override
    public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException{
        return getTraitBuilder().getObservationsForTraits(traitIds, environmentIds);
    	
    }

}
