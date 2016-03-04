
package org.generationcp.middleware.util;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by EfficioDaniel on 3/31/2015.
 */
public class CrossExpansionProperties {

	private String profile;
	private int wheatLevel;
	private int defaultLevel;

	private Set<Integer> hybridBreedingMethods = new TreeSet<Integer>();

	public CrossExpansionProperties() {

	}

	public int getWheatLevel() {
		return this.wheatLevel;
	}

	public void setWheatLevel(int wheatLevel) {
		this.wheatLevel = wheatLevel;
	}

	public int getDefaultLevel() {
		return this.defaultLevel;
	}

	public void setDefaultLevel(int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Set<Integer> getHybridBreedingMethods() {
		return this.hybridBreedingMethods;
	}

	public void setHybridBreedingMethods(Set<Integer> hybridBreedingMethods) {
		this.hybridBreedingMethods = hybridBreedingMethods;
	}
}
