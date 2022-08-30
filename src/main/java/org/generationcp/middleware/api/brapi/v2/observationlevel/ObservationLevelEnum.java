package org.generationcp.middleware.api.brapi.v2.observationlevel;

/**
 * https://wiki.brapi.org/index.php/Observation_Levels#Accepted_Levels
 * Sticking to uppercase for backwards compatibility, although they are lowercase in the standard
 */
public enum ObservationLevelEnum {

	// BRAPI Standard accepted levels
	STUDY(1, "STUDY"),
	FIELD(2, "FIELD"),
	REP(5, "REP"),
	BLOCK(4, "BLOCK"),
	PLOT(6, "PLOT"),
	SUB_PLOT(7, "SUB-PLOT"),
	PLANT(7, "PLANT"),

	// BMS Implementation additional levels
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
