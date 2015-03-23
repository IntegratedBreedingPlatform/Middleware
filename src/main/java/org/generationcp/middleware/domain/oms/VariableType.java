package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.domain.common.IdName;

import java.util.HashMap;
import java.util.Map;

/**
 * Variable Type of a Variable. Ontology variable can have zero or more variable types associated to it.
 */
public enum VariableType {
    ANALYSIS(1, "Analysis"),
    TRIAL_CONDITION(2, "Trial Condition"),
    NURSERY_CONDITION(3, "Nursery Condition"),
    GERMPLASM_DESCRIPTOR(4, "Germplasm Descriptor"),
    STUDY_DETAIL(5, "Study Detail"),
    ENVIRONMENT_DETAIL(6, "Environment Detail"),
    SELECTION_METHOD(7, "Selection Method"),
    TRAIT(8, "Trait"),
    TREATMENT_FACTOR(9, "Treatment Factor")
    ;

    private Integer id;
    private String name;


    private VariableType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    private static final Map<Integer, DataType> byId = new HashMap<>();
    private static final Map<String, DataType> byName = new HashMap<>();

    static {
        for (DataType e : DataType.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }

            if (byName.put(e.getName(), e) != null) {
                throw new IllegalArgumentException("duplicate name: " + e.getName());
            }
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() { return this.name; }

    public static DataType getById(Integer id) {
        return byId.get(id);
    }

    public static DataType getByName(String name) { return byName.get(name); }

    public IdName toIdName(){
        return new IdName(getId(), getName());
    }

    @Override
    public String toString() {
        return "DataType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}