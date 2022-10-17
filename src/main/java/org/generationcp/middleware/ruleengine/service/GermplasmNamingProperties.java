
package org.generationcp.middleware.ruleengine.service;

/**
 * This bean is typically populated in Spring application context using values in crossing.properties file.
 */
public class GermplasmNamingProperties {

	private String germplasmOriginStudiesDefault;
	private String germplasmOriginStudiesWheat;
	private String germplasmOriginStudiesMaize;

	private String breedersCrossIDStudy;

	public String getGermplasmOriginStudiesDefault() {
		return this.germplasmOriginStudiesDefault;
	}

	public void setGermplasmOriginStudiesDefault(final String germplasmOriginStudiesDefault) {
		this.germplasmOriginStudiesDefault = germplasmOriginStudiesDefault;
	}

	public String getGermplasmOriginStudiesWheat() {
		return this.germplasmOriginStudiesWheat;
	}

	public void setGermplasmOriginStudiesWheat(final String germplasmOriginStudiesWheat) {
		this.germplasmOriginStudiesWheat = germplasmOriginStudiesWheat;
	}

	public String getGermplasmOriginStudiesMaize() {
		return this.germplasmOriginStudiesMaize;
	}

	public void setGermplasmOriginStudiesMaize(final String germplasmOriginStudiesMaize) {
		this.germplasmOriginStudiesMaize = germplasmOriginStudiesMaize;
	}

	public String getBreedersCrossIDStudy(){
		return this.breedersCrossIDStudy;
	}

	public void setBreedersCrossIDStudy(final String breedersCrossIDStudy){
		this.breedersCrossIDStudy = breedersCrossIDStudy;
	}

}
