package org.generationcp.middleware.v2.domain;

import java.util.Arrays;
import java.util.List;

public enum FactorType {

	STUDY (Arrays.asList(TermId.STUDY_NAME_STORAGE.getId(), TermId.STUDY_TITLE_STORAGE.getId(), TermId.STUDY_INFO_STORAGE.getId()))
	, DATAEST (Arrays.asList(TermId.DATASET_NAME_STORAGE.getId(), TermId.DATASET_TITLE_STORAGE.getId(), TermId.DATASET_INFO_STORAGE.getId()))
	, TRIAL_ENVIRONMENT (Arrays.asList(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), TermId.TRIAL_INSTANCE_STORAGE.getId(), 
			TermId.LATITUDE_STORAGE.getId(), TermId.LONGITUDE_STORAGE.getId(), TermId.DATUM_STORAGE.getId(),
			TermId.ALTITUDE_STORAGE.getId()))
	, GERMPLASM (Arrays.asList(TermId.GERMPLASM_ENTRY_STORAGE.getId(), TermId.ENTRY_NUMBER_STORAGE.getId(), TermId.ENTRY_GID_STORAGE.getId(),
			TermId.ENTRY_DESIGNATION_STORAGE.getId(), TermId.ENTRY_CODE_STORAGE.getId()))
	, TRIAL_DESIGN (Arrays.asList(TermId.TRIAL_DESIGN_INFO_STORAGE.getId()))
	;
	
	private FactorType(List<Integer> factorStorages) {
		this.factorStorages = factorStorages;
	}
	
	private List<Integer> factorStorages;
	
	public List<Integer> getFactorStorages() {
		return this.factorStorages;
	}
}
