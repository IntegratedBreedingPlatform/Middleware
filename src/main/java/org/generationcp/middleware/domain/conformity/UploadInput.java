
package org.generationcp.middleware.domain.conformity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 5/14/2014 Time: 8:54 AM
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

		this.entries = new HashMap<Integer, ConformityGermplasmInput>();
	}

	public void addEntry(ConformityGermplasmInput input) {
		this.entries.put(input.getGid(), input);
	}

	public Integer getParentAGID() {
		return this.parentAGID;
	}

	public void setParentAGID(Integer parentAGID) {
		this.parentAGID = parentAGID;
	}

	public Integer getParentBGID() {
		return this.parentBGID;
	}

	public void setParentBGID(Integer parentBGID) {
		this.parentBGID = parentBGID;
	}

	public Map<Integer, ConformityGermplasmInput> getEntries() {
		return this.entries;
	}

	public void setEntries(Map<Integer, ConformityGermplasmInput> entries) {
		this.entries = entries;
	}

	public boolean isParentInputAvailable() {
		return this.entries.containsKey(this.parentAGID) && this.entries.containsKey(this.parentBGID);
	}

	public ConformityGermplasmInput getParentAInput() {
		return this.entries.get(this.parentAGID);
	}

	public ConformityGermplasmInput getParentBInput() {
		return this.entries.get(this.parentBGID);
	}
}
