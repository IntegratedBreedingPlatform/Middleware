
package org.generationcp.middleware.domain.ontology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;

/**
 * Variable Type of a Variable. Ontology variable can have zero or more variable types associated to it.
 */
public enum VariableType {
	ANALYSIS(1, "Analysis",
			"Variable to be used only in analysis (for example derived variables).",
			PhenotypicType.VARIATE),
	TRIAL_CONDITION(2, "Trial Condition",
			"Observations made of conditions in an individual environment involved in a trial.",
			PhenotypicType.VARIATE),
	NURSERY_CONDITION(3, "Nursery Condition",
			"Observations made of conditions in a nursery.",
			PhenotypicType.VARIATE),
	GERMPLASM_DESCRIPTOR(4, "Germplasm Descriptor",
			"Information to be recorded about each germplasm in a study.",
			PhenotypicType.GERMPLASM),
	STUDY_DETAIL(5, "Study Detail",
			"Administrative details to be tracked per study.",
			PhenotypicType.STUDY),
	ENVIRONMENT_DETAIL(6, "Environment Detail",
			"Administrative details to be tracked per environment.",
			PhenotypicType.TRIAL_ENVIRONMENT),
	SELECTION_METHOD(7, "Selection Method",
			"How material is chosen for advancing to the next generation.",
			PhenotypicType.VARIATE),
	TRAIT(8, "Trait",
			"Characteristics of a germplasm to be recorded during a study.",
			PhenotypicType.VARIATE),
	TREATMENT_FACTOR(9, "Treatment Factor",
			"Treatments to be applied to members of a trial.",
			PhenotypicType.TRIAL_DESIGN),
	EXPERIMENTAL_DESIGN(10, "Experimental Design",
			"Experimental Design Variables",
			PhenotypicType.TRIAL_DESIGN);

	private Integer id;
	private String name;
	private String description;
	private PhenotypicType role;

	VariableType(Integer id, String name, String description, PhenotypicType role) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.role = role;
	}

	private static final Map<Integer, VariableType> byId = new HashMap<>();
	private static final Map<String, VariableType> byName = new HashMap<>();

	static {
		for (VariableType e : VariableType.values()) {
			if (VariableType.byId.put(e.getId(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getId());
			}

			if (VariableType.byName.put(e.getName(), e) != null) {
				throw new IllegalArgumentException("duplicate name: " + e.getName());
			}
		}
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public PhenotypicType getRole() {
		return this.role;
	}

	public static VariableType getById(Integer id) {
		return VariableType.byId.get(id);
	}

	public static VariableType getByName(String name) {
		return VariableType.byName.get(name);
	}

	public static Set<Integer> getVariableTypesIdsByPhenotype(PhenotypicType phenotype) {
		Set<Integer> variableTypes = new HashSet<>();
		for (VariableType variableType : VariableType.values()) {
			if (variableType.getRole().equals(phenotype)) {
				variableTypes.add(variableType.getId());
			}
		}
		return variableTypes;
	}

	@Override
	public String toString() {
		return "VariableType{" + "id=" + this.id + ", name='" + this.name + '\'' + ", description='" + this.description + '\'' + '}';
	}
}
