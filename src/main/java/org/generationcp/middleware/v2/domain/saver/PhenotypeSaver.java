package org.generationcp.middleware.v2.domain.saver;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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

	public List<ExperimentPhenotype> createExperimentPhenotypes(ExperimentModel experimentModel, VariableList variates) throws MiddlewareQueryException {
		List<ExperimentPhenotype> experimentPhenotypes = null;
		
		if (variates != null && variates.getVariables() != null && variates.getVariables().size() > 0) {
			for (Variable variable : variates.getVariables()) {
				savePhenotype(variable);
			}
		}
		
		return experimentPhenotypes;
	}
	
	public void savePhenotype(Variable variable) throws MiddlewareQueryException {
		
	}
	
	public Phenotype createPhenotype(Variable variable) throws MiddlewareQueryException {
		Phenotype phenotype = null;
		
		if (TermId.OBSERVATION_VARIATE.getId().equals(variable.getVariableType().getId())
			|| TermId.CATEGORICAL_VARIATE.getId().equals(variable.getVariableType().getId())) {
			
			phenotype = getPhenotypeObject(phenotype);
			phenotype.setValue(variable.getValue());
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
}
