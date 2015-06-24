
package org.generationcp.middleware.domain.oms;

import java.util.HashMap;
import java.util.Map;

/**
 * Variable Type of a Variable. Ontology variable can have zero or more variable types associated to it.
 */
public enum VariableType {
	ANALYSIS(1801, "Analysis", "Variable to be used only in analysis (for example derived variables)."), TRIAL_CONDITION(1802, "Trial Condition",
			"Observations made of conditions in an individual environment involved in a trial."), NURSERY_CONDITION(1803, "Nursery Condition",
			"Observations made of conditions in a nursery."), GERMPLASM_DESCRIPTOR(1804, "Germplasm Descriptor",
			"Information to be recorded about each germplasm in a study."), STUDY_DETAIL(1805, "Study Detail",
			"Administrative details to be tracked per study."), ENVIRONMENT_DETAIL(1806, "Environment Detail",
			"Administrative details to be tracked per environment."), SELECTION_METHOD(1807, "Selection Method",
			"How material is chosen for advancing to the next generation."), TRAIT(1808, "Trait",
			"Characteristics of a germplasm to be recorded during a study."), TREATMENT_FACTOR(1809, "Treatment Factor",
			"Treatments to be applied to members of a trial."), EXPERIMENTAL_DESIGN(1810, "Experimental Design",
			"Design to be applied to experiments.");

	private Integer id;
	private String name;
	private String description;

	VariableType(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
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

	public static VariableType getById(Integer id) {
		return VariableType.byId.get(id);
	}

	public static VariableType getByName(String name) {
		return VariableType.byName.get(name);
	}

	@Override
	public String toString() {
		return "VariableType{" + "id=" + this.id + ", name='" + this.name + '\'' + ", description='" + this.description + '\'' + '}';
	}
}
