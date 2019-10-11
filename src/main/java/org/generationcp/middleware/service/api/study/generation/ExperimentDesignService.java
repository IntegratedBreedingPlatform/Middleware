package org.generationcp.middleware.service.api.study.generation;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;

public interface ExperimentDesignService {

	void deleteExperimentDesign(int studyId);

	void saveExperimentDesign(CropType crop, int studyId, List<MeasurementVariable> variables, List<ObservationUnitRow> rows);

	Optional<Integer> getExperimentDesignTypeTermId(int studyId);
}
