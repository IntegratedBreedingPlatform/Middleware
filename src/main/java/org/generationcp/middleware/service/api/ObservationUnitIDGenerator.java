
package org.generationcp.middleware.service.api;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.UIDGenerator;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

public class ObservationUnitIDGenerator {

	public static final UIDGenerator.UID_ROOT UID_ROOT = UIDGenerator.UID_ROOT.OBSERVATION_UNIT;
	public static final int SUFFIX_LENGTH = 8;

	public static void generateObservationUnitIds(final CropType crop, final List<ExperimentModel> experiments) {
		UIDGenerator.<ExperimentModel>generate(crop, experiments, UID_ROOT, SUFFIX_LENGTH,
			new UIDGenerator.UIDAdapter<ExperimentModel>() {

				@Override
				public String getUID(final ExperimentModel entry) {
					return entry.getObsUnitId();
				}

				@Override
				public void setUID(final ExperimentModel entry, final String uid) {
					entry.setObsUnitId(uid);
				}
			});

	}
}
