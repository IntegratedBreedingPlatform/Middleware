
package org.generationcp.middleware.domain.oms;

public enum SummaryStatistic {

	NUM_VALUES("NumValues"), NUM_MISSING("NumMissing"), MEAN("Mean"), VARIANCE("Variance"), SD("SD"), MIN("Min"), MAX("Max"), RANGE("Range"), MEDIAN(
			"Median"), LOWER_QUARTILE("LowerQuartile"), UPPER_QUARTILE("UpperQuartile"), MEAN_REP("MeanRep"), MIN_REP("MinRep"), MAX_REP(
			"MaxRep"), MEAN_SED("MeanSED"), MIN_SED("MinSED"), MAX_SED("MaxSED"), MEAN_LSD("MeanLSD"), MIN_LSD("MinLSD"), MAX_LSD("MaxLSD"), CV(
			"CV"), HERITABILITY("Heritability"), WALD_STATISTIC("WaldStatistic"), WALD_DF("WaldDF"), PVALUE("Pvalue");

	private final String name;

	private SummaryStatistic(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
