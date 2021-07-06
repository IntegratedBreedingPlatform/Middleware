
package org.generationcp.middleware.service.api;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

public class ObservationUnitIDGenerator {

	public static final String MID_STRING = "P";

	public static final int SUFFIX_LENGTH = 8;

	public static void generateObservationUnitIds(final CropType crop, final List<ExperimentModel> experiments) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(experiments));

		final boolean doUseUUID = crop.isUseUUID();
		for (final ExperimentModel experiment : experiments) {
			if (experiment.getObsUnitId() == null) {
				if (doUseUUID) {
					experiment.setObsUnitId(UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					experiment.setObsUnitId(cropPrefix + MID_STRING
						+ RandomStringUtils.randomAlphanumeric(SUFFIX_LENGTH));
				}
			}
		}
	}

}
