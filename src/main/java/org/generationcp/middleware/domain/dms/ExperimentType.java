/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * The different experiment types available - e.g. plot, sample, average, summary, study.
 *
 * @author joyce
 */
public enum ExperimentType {

	PLOT(TermId.PLOT_EXPERIMENT.getId()),
	SAMPLE(TermId.SAMPLE_EXPERIMENT.getId()),
	AVERAGE(TermId.AVERAGE_EXPERIMENT.getId()),
	SUMMARY(TermId.SUMMARY_EXPERIMENT.getId()),
	STUDY_INFORMATION(TermId.STUDY_EXPERIMENT.getId()),
	TRIAL_ENVIRONMENT(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()),
	SUMMARY_STATISTICS(TermId.SUMMARY_STATISTICS_EXPERIMENT.getId());

	private final Integer termId;

	ExperimentType(final Integer termId) {
		this.termId = termId;
	}

	public Integer getTermId() {
		return this.termId;
	}

}
