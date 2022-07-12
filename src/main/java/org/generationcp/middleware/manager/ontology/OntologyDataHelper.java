package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.ontology.VariableType;

/**
 * This class is used for the basic ontology helper methods
 */
public class OntologyDataHelper {

	public static final String INVENTORY_AMOUNT = "Inventory amount";
	public static final String SELECTIONS = "Selections";
	public static final String BREEDING_METHOD = "Breeding method";

	/**
	 * Return VariableType based on PhenotypicType and property name.
	 * @param phenotypicType PhenotypicType member based in which VariableType will be returned.
	 * @param propertyName name of the property to determine if variable type is Selection method or trait. This will be used if PhenotypicType is Variate.
	 * @return variable type based on input.
	 */
	public static VariableType mapFromPhenotype(PhenotypicType phenotypicType, String propertyName){
		VariableType variableType = null;

		if (PhenotypicType.STUDY == phenotypicType || PhenotypicType.DATASET == phenotypicType) {
			variableType = VariableType.STUDY_DETAIL;
		} else if (PhenotypicType.TRIAL_ENVIRONMENT == phenotypicType) {
			variableType = VariableType.ENVIRONMENT_DETAIL;
		} else if (PhenotypicType.GERMPLASM == phenotypicType) {
			variableType = VariableType.GERMPLASM_DESCRIPTOR;
		} else if (PhenotypicType.ENTRY_DETAIL == phenotypicType) {
			variableType = VariableType.ENTRY_DETAIL;
		} else if (PhenotypicType.TRIAL_DESIGN == phenotypicType) {
			variableType = VariableType.EXPERIMENTAL_DESIGN;
		} else if (PhenotypicType.VARIATE == phenotypicType) {
			boolean isSelectionMethod = INVENTORY_AMOUNT.equals(propertyName) ||
					SELECTIONS.equals(propertyName) ||
					BREEDING_METHOD.equals(propertyName);

			variableType = isSelectionMethod ? VariableType.SELECTION_METHOD : VariableType.TRAIT;
		}
		return variableType;
	}
}
