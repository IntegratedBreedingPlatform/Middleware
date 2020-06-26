package org.generationcp.middleware.service.api.study.generation;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExperimentDesignService {

	void deleteStudyExperimentDesign(int studyId);

	void saveExperimentDesign(CropType crop, int studyId, List<MeasurementVariable> variables, Map<Integer, List<ObservationUnitRow>> instanceRowsMap);

	Optional<Integer> getStudyExperimentDesignTypeTermId(int studyId);
}
