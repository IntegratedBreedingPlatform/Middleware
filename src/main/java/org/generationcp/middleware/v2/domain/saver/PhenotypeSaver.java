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
package org.generationcp.middleware.v2.domain.saver;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentPhenotype;
import org.generationcp.middleware.v2.pojos.Phenotype;

public class PhenotypeSaver extends Saver {

	public PhenotypeSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void savePhenotypes(ExperimentModel experimentModel, VariableList variates) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		if (variates != null && variates.getVariables() != null && variates.getVariables().size() > 0) {
			for (Variable variable : variates.getVariables()) {
				savePhenotype(experimentModel.getNdExperimentId(), variable);
			}
		}
	}
	
	private void savePhenotype(int experimentId, Variable variable) throws MiddlewareQueryException {
		Phenotype phenotype = createPhenotype(variable);
		if (phenotype != null) {
			getPhenotypeDao().save(phenotype);
			saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
		}
	}
	
	private Phenotype createPhenotype(Variable variable) throws MiddlewareQueryException {
		Phenotype phenotype = null;
		
		if (TermId.OBSERVATION_VARIATE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
			phenotype = getPhenotypeObject(phenotype);
			phenotype.setValue(variable.getValue());
			phenotype.setObservableId(variable.getVariableType().getId());
			phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
			phenotype.setName(String.valueOf(variable.getVariableType().getId()));
		}
		else if (TermId.CATEGORICAL_VARIATE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
			phenotype = getPhenotypeObject(phenotype);
			phenotype.setcValue(new Integer(variable.getValue()));
			phenotype.setObservableId(variable.getVariableType().getId());
			phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
			phenotype.setName(String.valueOf(variable.getVariableType().getId()));
		}
		
		return phenotype;
	}
	
	private Phenotype getPhenotypeObject(Phenotype phenotype) throws MiddlewareQueryException {
		if (phenotype == null) {
			phenotype = new Phenotype();
			phenotype.setPhenotypeId(getPhenotypeDao().getNegativeId("phenotypeId"));
		}
		return phenotype;
	}
	
	private void saveExperimentPhenotype(int experimentId, int phenotypeId) throws MiddlewareQueryException {
		ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		
		experimentPhenotype.setExperimentPhenotypeId(getExperimentPhenotypeDao().getNegativeId("experimentPhenotypeId"));
		experimentPhenotype.setExperiment(experimentId);
		experimentPhenotype.setPhenotype(phenotypeId);
		
		getExperimentPhenotypeDao().save(experimentPhenotype);
	}
}
