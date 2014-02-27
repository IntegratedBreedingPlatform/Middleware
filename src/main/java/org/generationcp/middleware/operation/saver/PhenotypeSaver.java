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

package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.util.DatabaseBroker;

public class PhenotypeSaver extends Saver{

    public PhenotypeSaver(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public void savePhenotypes(ExperimentModel experimentModel, VariableList variates) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        int i=0;
        if (variates != null && variates.getVariables() != null && variates.getVariables().size() > 0) {
            for (Variable variable : variates.getVariables()) {
                save(experimentModel.getNdExperimentId(), variable);
                if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0){ // batch save
                    getPhenotypeDao().flush();
                    getPhenotypeDao().clear();
                }
            }
        }
    }
    
    public void save(int experimentId, Variable variable) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        Phenotype phenotype = createPhenotype(variable);
        if (phenotype != null) {
            getPhenotypeDao().save(phenotype);
            saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
        }
    }
    
    public void saveOrUpdate(int experimentId, Integer variableId, int storedIn, String value, Phenotype phenotype)
            throws MiddlewareQueryException {
        phenotype = createPhenotype(variableId, storedIn, value, phenotype);
        saveOrUpdate(experimentId, phenotype);
    }

    public void savePhenotype(int experimentId, Variable variable) throws MiddlewareQueryException {
        Phenotype phenotype = createPhenotype(variable);
        if (phenotype != null) {
            getPhenotypeDao().save(phenotype);
            saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
        }
    }
    
    private Phenotype createPhenotype(Variable variable) throws MiddlewareQueryException {
        Phenotype phenotype = null;
        
        if (variable.getValue() != null && !"".equals(variable.getValue().trim())) {
	        if (TermId.OBSERVATION_VARIATE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
	            phenotype = getPhenotypeObject(phenotype);
	            phenotype.setValue(variable.getValue());
	            phenotype.setObservableId(variable.getVariableType().getId());
	            phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
	            phenotype.setName(String.valueOf(variable.getVariableType().getId()));
	        }
	        else if (TermId.CATEGORICAL_VARIATE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
	            phenotype = getPhenotypeObject(phenotype);
	            if(variable.getValue()!=null && !variable.getValue().equals("")) {
	                phenotype.setcValue(Double.valueOf(variable.getValue()).intValue()); 
	            }           
	            phenotype.setObservableId(variable.getVariableType().getId());
	            phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
	            phenotype.setName(String.valueOf(variable.getVariableType().getId()));
	        }
        }
        return phenotype;
    }
    
    private void saveOrUpdate(int experimentId, Phenotype phenotype) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        if (phenotype != null) {
            getPhenotypeDao().merge(phenotype);
            saveOrUpdateExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
        }
    }

    private Phenotype createPhenotype(Integer variableId, int storedIn, String value, Phenotype phenotype)
            throws MiddlewareQueryException {
    	
    	if ((value == null || "".equals(value.trim())) && (phenotype == null || phenotype.getPhenotypeId() == null) ){
    		return null;
    	}

    	phenotype = getPhenotypeObject(phenotype);

        if (TermId.OBSERVATION_VARIATE.getId() == storedIn) {
            phenotype.setValue(value);
        } else if (TermId.CATEGORICAL_VARIATE.getId() == storedIn) {
            if (value != null && !value.equals("")) {
                phenotype.setcValue(Double.valueOf(value).intValue());
            }
        }
        phenotype.setObservableId(variableId);
        phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
        phenotype.setName(String.valueOf(variableId));

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
        getExperimentPhenotypeDao().save(createExperimentPhenotype(experimentId, phenotypeId));
    }
    
    private void saveOrUpdateExperimentPhenotype(int experimentId, int phenotypeId) throws MiddlewareQueryException {
        getExperimentPhenotypeDao().merge(createExperimentPhenotype(experimentId, phenotypeId));
    }

    private ExperimentPhenotype createExperimentPhenotype(int experimentId, int phenotypeId) throws MiddlewareQueryException {
        ExperimentPhenotype experimentPhenotype = getExperimentPhenotypeDao()
                .getbyExperimentAndPhenotype(experimentId, phenotypeId);

        if (experimentPhenotype == null || experimentPhenotype.getExperimentPhenotypeId() == null) {
            experimentPhenotype = new ExperimentPhenotype();
            experimentPhenotype.setExperimentPhenotypeId(getExperimentPhenotypeDao().getNegativeId(
                    "experimentPhenotypeId"));
            experimentPhenotype.setExperiment(experimentId);
            experimentPhenotype.setPhenotype(phenotypeId);
        }
        return experimentPhenotype;

    }

}
