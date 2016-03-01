
package org.generationcp.middleware.util;

/**
 * Created by EfficioDaniel on 3/31/2015.
 */
public class CrossExpansionProperties {

	private String profile;
    private String restrictedNameUIDs = null;
	private int wheatLevel;
	private int defaultLevel;

	public CrossExpansionProperties() {

	}

	public int getWheatLevel() {
		return this.wheatLevel;
	}

	public void setWheatLevel(final int wheatLevel) {
		this.wheatLevel = wheatLevel;
	}

	public int getDefaultLevel() {
		return this.defaultLevel;
	}

	public void setDefaultLevel(final int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(final String profile) {
		this.profile = profile;
	}

    public String getRestrictedNameUIDs() {
        return restrictedNameUIDs;
    }

    public void setRestrictedNameUIDs(final String restrictedNameUIDs) {
        this.restrictedNameUIDs = restrictedNameUIDs;
    }
}
