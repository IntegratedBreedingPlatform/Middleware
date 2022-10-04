package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;

public interface ExpressionDataProcessor {

    /**
     * Method that should be implemented if the expression needs environment level data. The required data
     * should be set into the provided AdvancingSource  object
     * @param source
     * @param advanceStudyRequest
     * @param conditions
     * @param constants
     */
    void processEnvironmentLevelData(AdvancingSource source, AdvanceStudyRequest advanceStudyRequest, List<MeasurementVariable> conditions,
        List<MeasurementVariable> constants);


    /**
     * Method that should be implemented if the expression needs plot level data. The required data should be set
     * into the provided AdvancingSource object
     *  @param source
     * @param row
     */
    void processPlotLevelData(AdvancingSource source, ObservationUnitRow row);

}
