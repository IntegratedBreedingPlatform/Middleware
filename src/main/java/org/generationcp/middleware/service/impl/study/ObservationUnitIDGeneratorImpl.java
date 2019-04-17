
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ObservationUnitIDGeneratorImpl implements ObservationUnitIDGenerator {

	protected static final String MID_STRING = "P";

	protected static final int SUFFIX_LENGTH = 8;

	@Override
	public void generateObservationUnitIds(final CropType crop, final List<ExperimentModel> experiments) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(experiments));

		final boolean doUseUUID = crop.isUseUUID();
		for (final ExperimentModel experiment : experiments) {
			if (experiment.getObsUnitId() == null) {
				if (doUseUUID) {
					experiment.setObsUnitId(UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					experiment.setObsUnitId(cropPrefix + ObservationUnitIDGeneratorImpl.MID_STRING
							+ RandomStringUtils.randomAlphanumeric(ObservationUnitIDGeneratorImpl.SUFFIX_LENGTH));
				}
			}
		}
	}

}
