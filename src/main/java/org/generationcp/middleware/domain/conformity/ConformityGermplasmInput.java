
package org.generationcp.middleware.domain.conformity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
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
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Map<String, String> getMarkerValues() {
		return this.markerValues;
	}

	public void setMarkerValues(Map<String, String> markerValues) {
		this.markerValues = markerValues;
	}

	public Integer getsNumber() {
		return this.sNumber;
	}

	public void setsNumber(Integer sNumber) {
		this.sNumber = sNumber;
	}

	@Override
	public String toString() {
		return "ConformityGermplasmInput{" + "line='" + this.line + '\'' + ", alias='" + this.alias + '\'' + ", gid=" + this.gid
				+ ", sNumber=" + this.sNumber + ", markerValues=" + this.markerValues + '}';
	}
}
