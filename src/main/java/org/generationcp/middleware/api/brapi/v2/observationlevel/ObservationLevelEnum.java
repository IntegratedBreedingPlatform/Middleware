package org.generationcp.middleware.api.brapi.v2.observationlevel;

/**
 * https://wiki.brapi.org/index.php/Observation_Levels#Accepted_Levels
 */
public enum ObservationLevelEnum {

	// BRAPI Standard accepted levels
	// These are in lowercase to conform to the standard
	STUDY(1, "study"),
	FIELD(2, "field"),
	REP(5, "rep"),
	BLOCK(4, "block"),
	PLOT(6, "plot"),
	SUB_PLOT(7, "sub-plot"),
	PLANT(7, "plant"),

	// BMS Implementation additional levels
	// These are in uppercase for BMS compatibility
	TIMESERIES(7, "TIMESERIES"),
	CUSTOM(7, "CUSTOM"),
	SUMMARY_STATISTICS(1, "SUMMARY_STATISTICS"),
	MEANS(3, "MEANS");

	private final Integer levelOrder;
	private final String levelName;

	ObservationLevelEnum(final Integer levelOrder, final String levelName) {
		this.levelOrder = levelOrder;
		this.levelName = levelName;
	}

	public Integer getLevelOrder() {
		return this.levelOrder;
	}

	public String getLevelName() {
		return this.levelName;
	}
}
