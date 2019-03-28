
package org.generationcp.middleware.service.api;

import java.util.List;

import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;

public interface ObservationUnitIDGenerator {

	CropType getCropForProjectId(final Integer projectId);

	void generateObservationUnitIds(final CropType crop, final List<ExperimentModel> experiments);

}
