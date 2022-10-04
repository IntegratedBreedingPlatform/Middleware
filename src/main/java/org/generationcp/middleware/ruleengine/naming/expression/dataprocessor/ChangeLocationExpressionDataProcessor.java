package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ChangeLocationExpressionDataProcessor implements ExpressionDataProcessor {

    @Override
    public void processEnvironmentLevelData(final AdvancingSource source, final AdvanceStudyRequest advanceStudyRequest,
        final List<MeasurementVariable> conditions,
        final List<MeasurementVariable> constants) {
        // no-op
    }

    @Override
    public void processPlotLevelData(AdvancingSource source, ObservationUnitRow row) {
		if(source.getTrailInstanceObservation() != null && !CollectionUtils.isEmpty(source.getTrailInstanceObservation().getDataList())){
            for(MeasurementData measurementData : source.getTrailInstanceObservation().getDataList()){
                if(measurementData.getMeasurementVariable().getTermId() == TermId.LOCATION_ID.getId()){
                    String locationIdString = measurementData.getValue();
                    Integer locationId = StringUtils.isEmpty(locationIdString) ? null : Integer.valueOf(locationIdString);
                    source.setHarvestLocationId(locationId);
                    break;
                }
            }
        }
    }

}
