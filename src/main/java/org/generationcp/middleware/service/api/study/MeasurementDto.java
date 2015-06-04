
package org.generationcp.middleware.service.api.study;

/**
 * A trait and its associated measurement.
 *
 */
public class MeasurementDto {

	private TraitDto trait;

	private Integer phenotypeId;

	private String triatValue;

	public MeasurementDto(final TraitDto trait, final Integer phenotypeId, final String triatValue) {
		this.trait = trait;
		this.phenotypeId = phenotypeId;
		this.triatValue = triatValue;
	}

	/**
	 * @return the trait
	 */
	public TraitDto getTrait() {
		return this.trait;
	}

	/**
	 * @param trait the trait to set
	 */
	public void setTrait(TraitDto trait) {
		this.trait = trait;
	}

	/**
	 * @return the phenotypeId
	 */
	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	/**
	 * @param phenotypeId the phenotypeId to set
	 */
	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	/**
	 * @return the triatValue
	 */
	public String getTriatValue() {
		return this.triatValue;
	}

	/**
	 * @param triatValue the triatValue to set
	 */
	public void setTriatValue(String triatValue) {
		this.triatValue = triatValue;
	}

}
