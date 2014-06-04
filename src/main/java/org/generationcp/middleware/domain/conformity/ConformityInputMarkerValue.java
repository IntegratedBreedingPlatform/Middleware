package org.generationcp.middleware.domain.conformity;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class ConformityInputMarkerValue {
    private String name;
    private String value;


    public ConformityInputMarkerValue() {
    }

    public ConformityInputMarkerValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
