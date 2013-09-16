
package org.generationcp.middleware.domain.dms;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * The different factor types used for study, dataset, trial environment, germplasm, trial design.
 *
 */
public enum FactorType {

    STUDY(Arrays.asList(
            TermId.STUDY_NAME_STORAGE.getId(),
            TermId.STUDY_TITLE_STORAGE.getId(),
            TermId.STUDY_INFO_STORAGE.getId()),
          Arrays.asList("STUDY")), 
    DATASET(Arrays.asList(
            TermId.DATASET_NAME_STORAGE.getId(),
            TermId.DATASET_TITLE_STORAGE.getId(),
            TermId.DATASET_INFO_STORAGE.getId()),
          Arrays.asList("")), 
    TRIAL_ENVIRONMENT(Arrays.asList(
            TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(),
            TermId.TRIAL_INSTANCE_STORAGE.getId(),
            TermId.LATITUDE_STORAGE.getId(),
            TermId.LONGITUDE_STORAGE.getId(),
            TermId.DATUM_STORAGE.getId(),
            TermId.ALTITUDE_STORAGE.getId()),
          Arrays.asList("TRIAL","OCC","TRIAL_NO")), 
    GERMPLASM(Arrays.asList(
            TermId.GERMPLASM_ENTRY_STORAGE.getId(),
            TermId.ENTRY_NUMBER_STORAGE.getId(),
            TermId.ENTRY_GID_STORAGE.getId(),
            TermId.ENTRY_DESIGNATION_STORAGE.getId(),
            TermId.ENTRY_CODE_STORAGE.getId()),
           Arrays.asList("ENTRY")), 
    TRIAL_DESIGN(Arrays.asList(
            TermId.TRIAL_DESIGN_INFO_STORAGE.getId()),
           Arrays.asList("PLOT"));

    private FactorType(List<Integer> factorStorages, List<String> labelList) {
        this.factorStorages = factorStorages;
        this.labelList = labelList;
    }

    private List<Integer> factorStorages;

    public List<Integer> getFactorStorages() {
        return this.factorStorages;
    }
  
    private List<String> labelList;
    
    public static FactorType getFactorTypeForLabel(String label){
    	
    	if(label.equalsIgnoreCase("STUDY")){
    		return FactorType.STUDY;
    	}
    	else if(label.equalsIgnoreCase("")){
    		return FactorType.DATASET;
    	}
    	else if(label.equalsIgnoreCase("TRIAL") || label.equalsIgnoreCase("OCC") || label.equalsIgnoreCase("TRIAL_NO")){
    		return FactorType.TRIAL_ENVIRONMENT;	
    	}
    	else if(label.equalsIgnoreCase("ENTRY")){
    		return FactorType.GERMPLASM;
    	}
    	else if(label.equalsIgnoreCase("PLOT")){
    		return FactorType.TRIAL_DESIGN;
    	}
    	
    	
    	return null;
    }
}
