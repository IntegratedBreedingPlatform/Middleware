package org.generationcp.middleware.domain.conformity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 5/14/2014
 * Time: 8:54 AM
 */
public class UploadInput {

    private Integer parentAGID;
    private Integer parentBGID;

    private Map<Integer, ConformityGermplasmInput> entries;

    public UploadInput() {
        this(null, null);
    }

    public UploadInput(Integer parentAGID, Integer parentBGID) {
        this.parentAGID = parentAGID;
        this.parentBGID = parentBGID;

        entries = new HashMap<Integer, ConformityGermplasmInput>();
    }

    public void addEntry(ConformityGermplasmInput input) {
        entries.put(input.getGid(), input);
    }

    public Integer getParentAGID() {
        return parentAGID;
    }

    public void setParentAGID(Integer parentAGID) {
        this.parentAGID = parentAGID;
    }

    public Integer getParentBGID() {
        return parentBGID;
    }

    public void setParentBGID(Integer parentBGID) {
        this.parentBGID = parentBGID;
    }

    public Map<Integer, ConformityGermplasmInput> getEntries() {
        return entries;
    }

    public void setEntries(Map<Integer, ConformityGermplasmInput> entries) {
        this.entries = entries;
    }

    public boolean isParentInputAvailable() {
        return entries.containsKey(parentAGID) && entries.containsKey(parentBGID);
    }

    public ConformityGermplasmInput getParentAInput() {
        return entries.get(parentAGID);
    }

    public ConformityGermplasmInput getParentBInput() {
        return entries.get(parentBGID);
    }
}
