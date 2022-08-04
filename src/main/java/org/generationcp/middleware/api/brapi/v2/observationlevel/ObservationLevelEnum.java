package org.generationcp.middleware.api.brapi.v2.observationlevel;

public enum ObservationLevelEnum {
	STUDY(1, "study"),
	SUMMARY_STATISTICS(1, "summary-statistics"),
	FIELD(2, "field"),
	MEANS(3, "means"),
	BLOCK(4, "block"),
	REP(5, "rep"),
	PLOT(6, "plot"),
	SUB_PLOT(7, "sub-plot"),
	PLANT(7, "plant"),
	TIME_SERIES(7, "timeseries"),
	CUSTOM(7, "custom");

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
