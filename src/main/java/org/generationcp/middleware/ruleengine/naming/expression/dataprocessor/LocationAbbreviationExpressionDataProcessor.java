package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class LocationAbbreviationExpressionDataProcessor implements ExpressionDataProcessor {

    @Resource
    private LocationService locationService;

    @Override
    public void processEnvironmentLevelData(final AdvancingSource source, final AdvanceStudyRequest advanceStudyRequest,
        final List<MeasurementVariable> conditions,
        final List<MeasurementVariable> constants) {
        // no-op
    }

    @Override
    public void processPlotLevelData(AdvancingSource source, ObservationUnitRow row) {
        if(source.getTrailInstanceObservation() != null &&
                source.getTrailInstanceObservation().getDataList() != null &&  !source.getTrailInstanceObservation().getDataList().isEmpty()){
                for(MeasurementData measurementData : source.getTrailInstanceObservation().getDataList()){
                if(measurementData.getMeasurementVariable().getTermId() == TermId.LOCATION_ID.getId()){
                    String locationAbbreviation = null;
                    String locationIdString = measurementData.getValue();
                    Integer locationId = StringUtils.isEmpty(locationIdString) ? null : Integer.valueOf(locationIdString);
                    if(locationId != null){
                        Location location = locationService.getLocationByID(locationId);
                        locationAbbreviation = location.getLabbr();
                    }
                    source.setLocationAbbreviation(locationAbbreviation);
                    break;
                }
            }
        }
    }

}
