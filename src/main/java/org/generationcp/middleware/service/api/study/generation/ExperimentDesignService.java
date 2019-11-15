package org.generationcp.middleware.service.api.study.generation;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;
import java.util.Map;

public interface ExperimentDesignService {

	void deleteStudyExperimentDesign(int studyId);

	void saveExperimentDesign(CropType crop, int studyId, List<MeasurementVariable> variables, Map<Integer, List<ObservationUnitRow>> instanceRowsMap);

	Optional<Integer> getStudyExperimentDesignTypeTermId(int studyId);
}
