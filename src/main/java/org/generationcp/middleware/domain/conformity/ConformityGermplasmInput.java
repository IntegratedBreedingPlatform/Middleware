package org.generationcp.middleware.domain.conformity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class ConformityGermplasmInput {

    private String name;
    private Integer gid;
    private Integer lineNumber;

    private Map<String, String> markerValues;

    public ConformityGermplasmInput() {
        this(null, null);
    }

    public ConformityGermplasmInput(String name, Integer gid) {
        this.name = name;
        this.gid = gid;

        this.markerValues = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMarkerValues() {
        return markerValues;
    }

    public void setMarkerValues(Map<String, String> markerValues) {
        this.markerValues = markerValues;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
}
