package org.generationcp.middleware.domain.oms;

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

    static {
        for (DataType e : DataType.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public static DataType getById(Integer id) {
        return byId.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}