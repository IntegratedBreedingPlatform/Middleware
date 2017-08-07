package org.generationcp.middleware.manager.ontology;

import java.util.Objects;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.ontology.VariableType;

/**
 * This class is used for the basic ontology helper methods
 */
public class OntologyDataHelper {

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
		} else if (PhenotypicType.TRIAL_DESIGN == phenotypicType) {
			variableType = VariableType.EXPERIMENTAL_DESIGN;
		} else if (PhenotypicType.VARIATE == phenotypicType) {
			boolean isSelectionMethod = "Inventory amount".equals(propertyName) ||
					"Selections".equals(propertyName) ||
					"Breeding method".equals(propertyName);

			variableType = isSelectionMethod ? VariableType.SELECTION_METHOD : VariableType.TRAIT;
		}
		return variableType;
	}
}
