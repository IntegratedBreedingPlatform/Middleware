package org.generationcp.middleware.service.pedigree;

/**
 * Pojo to hold the pedigree string and number of crosses that resulted in the pedigree string.
 *
 */
public class PedigreeString {

	/**
	 * A pedigree string.
	 */
	String pedigree;

	/**
	 * Number of crosses that resulted in the pedigree string
	 */
	int numberOfCrosses;

	public String getPedigree() {
		return pedigree;
	}

	public void setPedigree(String currentString) {
		this.pedigree = currentString;
	}

	public int getNumberOfCrosses() {
		return numberOfCrosses;
	}

	public void setNumberOfCrosses(int numberOfCrosses) {
		this.numberOfCrosses = numberOfCrosses;
	}


}