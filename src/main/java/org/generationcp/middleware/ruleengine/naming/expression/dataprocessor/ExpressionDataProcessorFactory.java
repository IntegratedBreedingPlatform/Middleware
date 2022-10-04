package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.spring.util.ComponentFactory;

import java.util.ArrayList;
import java.util.List;

public class ExpressionDataProcessorFactory implements ComponentFactory<ExpressionDataProcessor> {
    
    private final List<ExpressionDataProcessor> dataProcessorList = new ArrayList<>();

    @Override
    public void addComponent(ExpressionDataProcessor expressionDataProcessor) {
        dataProcessorList.add(expressionDataProcessor);
    }

    public List<ExpressionDataProcessor> getDataProcessorList() {
        return dataProcessorList;
    }

    public ExpressionDataProcessor retrieveExecutorProcessor() {
        // DEV NOTE : in the future, we could possibly streamline the data processing flow by providing
        // a different processor that performs filtering. e.g. specify that some processors should only be used for
        // a target crop / program, etc
        return new ExecuteAllAvailableDataProcessor();
    }
    
    class ExecuteAllAvailableDataProcessor implements ExpressionDataProcessor {

        @Override
        public void processEnvironmentLevelData(final AdvancingSource source, final AdvanceStudyRequest advanceStudyRequest,
            final List<MeasurementVariable> conditions,
            final List<MeasurementVariable> constants) {
            dataProcessorList.forEach(expressionDataProcessor ->
                expressionDataProcessor.processEnvironmentLevelData(source, advanceStudyRequest, conditions, constants));
        }

        @Override
        public void processPlotLevelData(AdvancingSource source, ObservationUnitRow row) {
            dataProcessorList.forEach(expressionDataProcessor -> expressionDataProcessor.processPlotLevelData(source, row));
        }

    }
}
