
package org.generationcp.middleware.domain.dms;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * The different phenotypic types used in standard variable. 
 * For factor type, these are used: STUDY, DATASET, TRIAL_ENVIRONMENT, GERMPLASM, TRIAL_DESIGN
 * For role type, these are used: TRIAL_ENVIRONMENT, GERMPLASM, TRIAL_DESIGN, VARIATE
 *
 */
public enum PhenotypicType {

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
           Arrays.asList("ENTRY", "ENTRY_NO")), 
    TRIAL_DESIGN(Arrays.asList(
            TermId.TRIAL_DESIGN_INFO_STORAGE.getId()),
           Arrays.asList("PLOT", "PLOT_NO")),
    VARIATE(Arrays.asList(
		   TermId.OBSERVATION_VARIATE.getId(),
		   TermId.CATEGORICAL_VARIATE.getId()),
		   Arrays.asList("STUDY", "PLOT", "TRIAL"))
           ;


    private List<Integer> typeStorages;
    private List<String> labelList;
    
    private PhenotypicType(List<Integer> typeStorages, List<String> labelList) {
        this.typeStorages = typeStorages;
        this.labelList = labelList;
    }

	public List<Integer> getTypeStorages() {
        return this.typeStorages;
    }

	public List<String> getLabelList() {
		return labelList;
	}
    
    public static PhenotypicType getPhenotypicTypeForLabel(String label){
    	
    	if (label != null){    		
    		for (PhenotypicType type : PhenotypicType.values()){
    			List<String> labelList = type.getLabelList();
    			for (String factorLabel : labelList){
    				if (factorLabel.toUpperCase().equals(label.toUpperCase())){
    					return type;
    				}
    			}
    		}
    	}
    	    	
    	return null;
    }
    
    public static PhenotypicType getPhenotypicTypeById(Integer termId) {
        for (PhenotypicType type : PhenotypicType.values()) {
            for (Integer id : type.getTypeStorages()) {
                if (id.equals(termId)) {
                    return type;
                }
            }
        }
        return null;
    }
}
