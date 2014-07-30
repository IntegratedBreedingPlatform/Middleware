package org.generationcp.middleware.domain.conformity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class ConformityGermplasmInput {

    private String line;
    private String alias;
    private Integer gid;
    private Integer sNumber;

    private Map<String, String> markerValues;

    public ConformityGermplasmInput() {
        this(null, null, null);
    }

    public ConformityGermplasmInput(String line, String alias, Integer gid) {
        this.line = line;
        this.gid = gid;

        this.markerValues = new HashMap<String, String>();
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }



    public Map<String, String> getMarkerValues() {
        return markerValues;
    }

    public void setMarkerValues(Map<String, String> markerValues) {
        this.markerValues = markerValues;
    }

    public Integer getsNumber() {
        return sNumber;
    }

    public void setsNumber(Integer sNumber) {
        this.sNumber = sNumber;
    }

    @Override
    public String toString() {
        return "ConformityGermplasmInput{" +
                "line='" + line + '\'' +
                ", alias='" + alias + '\'' +
                ", gid=" + gid +
                ", sNumber=" + sNumber +
                ", markerValues=" + markerValues +
                '}';
    }
}
