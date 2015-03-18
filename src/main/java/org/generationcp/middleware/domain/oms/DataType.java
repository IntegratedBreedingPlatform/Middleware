package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.domain.common.IdName;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

    NUMERIC_VARIABLE(1110, "Numeric Variable")
    , DATE_TIME_VARIABLE(1117, "Date Time Variable")
    , CHARACTER_VARIABLE(1120, "Character Variable")
    , CATEGORICAL_VARIABLE(1130, "Categorical Variable");

    private Integer id;
    private String name;


    private DataType(Integer id, String name) {
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