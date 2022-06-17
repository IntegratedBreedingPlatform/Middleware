
package org.generationcp.middleware.domain.ontology;

import org.generationcp.middleware.domain.dms.PhenotypicType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Variable Type of a Variable. Ontology variable can have zero or more variable types associated to it.
 */
public enum VariableType {
	ANALYSIS(1801, "Analysis",
			"Variable to be used only in analysis (for example derived variables).",
			PhenotypicType.VARIATE),
	ENVIRONMENT_CONDITION(1802, "Environment Condition",
			"Observations made of conditions in an individual environment involved in a study.",
			PhenotypicType.VARIATE),
	GERMPLASM_DESCRIPTOR(1804, "Germplasm Descriptor",
			"Information to be recorded about each germplasm in a study.",
			PhenotypicType.GERMPLASM),
	STUDY_DETAIL(1805, "Study Detail",
			"Administrative details to be tracked per study.",
			PhenotypicType.STUDY),
	ENVIRONMENT_DETAIL(1806, "Environment Detail",
			"Administrative details to be tracked per environment.",
			PhenotypicType.TRIAL_ENVIRONMENT),
	SELECTION_METHOD(1807, "Selection Method",
			"How material is chosen for advancing to the next generation.",
			PhenotypicType.VARIATE),
	TRAIT(1808, "Trait",
			"Characteristics of a germplasm to be recorded during a study.",
			PhenotypicType.VARIATE),
	TREATMENT_FACTOR(1809, "Treatment Factor",
			"Treatments to be applied to members of a trial.",
			PhenotypicType.TRIAL_DESIGN),
	EXPERIMENTAL_DESIGN(1810, "Experimental Design",
			"Experimental Design Variables",
			PhenotypicType.TRIAL_DESIGN),
	ANALYSIS_SUMMARY(1811, "Analysis Summary",
			"Analysis Summary Variables", PhenotypicType.VARIATE),
	OBSERVATION_UNIT(1812, "Observation Unit",
		"Observation Unit Variables", PhenotypicType.UNASSIGNED),
	GERMPLASM_PASSPORT(1813, "Germplasm Passport", "Germplasm Passport", PhenotypicType.UNASSIGNED),
	GERMPLASM_ATTRIBUTE(1814, "Germplasm Attribute", "Germplasm Attribute", PhenotypicType.UNASSIGNED),
	ENTRY_DETAIL(1815, "Entry Detail", "Variables that describes list entries", PhenotypicType.UNASSIGNED),
	INVENTORY_ATTRIBUTE(1816, "Inventory Attribute", "Inventory Attribute", PhenotypicType.UNASSIGNED);


	private final Integer id;
	private final String name;
	private final String description;
	private final PhenotypicType role;

	VariableType(final Integer id, final String name, final String description, final PhenotypicType role) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.role = role;
	}

	private static final Map<Integer, VariableType> byId = new HashMap<>();
	private static final Map<String, VariableType> byName = new HashMap<>();

	static {
		for (final VariableType e : VariableType.values()) {
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

	public static VariableType getById(final Integer id) {
		return VariableType.byId.get(id);
	}

	public static VariableType getByName(final String name) {
		return VariableType.byName.get(name);
	}

	public static Set<Integer> getVariableTypesIdsByPhenotype(final PhenotypicType phenotype) {
		final Set<Integer> variableTypes = new HashSet<>();
		for (final VariableType variableType : VariableType.values()) {
			if (variableType.getRole().equals(phenotype)) {
				variableTypes.add(variableType.getId());
			}
		}
		return variableTypes;
	}

	public static List<Integer> ids() {
		final List<Integer> variableTypeIds = new ArrayList<>();
		for (final VariableType variableType : VariableType.values()) {
			variableTypeIds.add(variableType.getId());
		}
		return variableTypeIds;
	}

	@Override
	public String toString() {
		return "VariableType{" + "id=" + this.id + ", name='" + this.name + '\'' + ", description='" + this.description + '\'' + '}';
	}
	
	/**
	 * Returns list of Variable Types that are "reserved" or for special use only and not intended
	 * to be used when creating a trial or nursery plot (eg. "Analysis" and "Analysis Summary" variable types)
	 * @return
	 */
	public static List<VariableType> getReservedVariableTypes(){
		return Arrays.asList(ANALYSIS, ANALYSIS_SUMMARY);
	}

	public static List<VariableType> getGermplasmAttributeVariableTypes() {
		return Arrays.asList(GERMPLASM_ATTRIBUTE, GERMPLASM_PASSPORT);
	}

}
