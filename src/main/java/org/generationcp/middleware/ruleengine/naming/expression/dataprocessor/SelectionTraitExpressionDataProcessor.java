
package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SelectionTraitExpressionDataProcessor implements ExpressionDataProcessor {

	public static final String SELECTION_TRAIT_PROPERTY = "Selection Criteria";

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Override
	public void processEnvironmentLevelData(final AdvancingSource source, final AdvanceStudyRequest advanceStudyRequest,
		final List<MeasurementVariable> conditions,
		final List<MeasurementVariable> constants) {
		final List<MeasurementVariable> possibleEnvironmentSources = new ArrayList<>(conditions);
		if (!CollectionUtils.isEmpty(constants)) {
			possibleEnvironmentSources.addAll(constants);
		}
		for (final MeasurementVariable condition : possibleEnvironmentSources) {
			if (SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(condition.getProperty())) {
				setSelectionTraitValue(condition.getValue(), source, condition.getTermId(), condition.getPossibleValues());
			}
		}
	}

	// TODO: implement it!
	@Override
	public void processPlotLevelData(final AdvancingSource source, final ObservationUnitRow row) {
//		final List<MeasurementData> rowData = row.getVariables().values();

//		final List<MeasurementData> rowData = row.getDataList();
//		if(source.getTrailInstanceObservation() != null){
//			rowData.addAll(source.getTrailInstanceObservation().getDataList());
//		}
//
//		for (final MeasurementData measurementData : rowData) {
//			if (SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(measurementData.getMeasurementVariable().getProperty())) {
//				setSelectionTraitValue(measurementData.getValue(), source, measurementData.getMeasurementVariable().getTermId(),
//					measurementData.getMeasurementVariable().getPossibleValues());
//			}
//		}
	}

	protected void setSelectionTraitValue(final String categoricalValue, final AdvancingSource source, final int termID, final List<ValueReference> possibleValuesForSelectionTraitProperty){
		if(StringUtils.isNumeric(categoricalValue)){
			source.setSelectionTraitValue(extractValue(categoricalValue, termID));
		}
		else{
			if(possibleValuesForSelectionTraitProperty != null && !possibleValuesForSelectionTraitProperty.isEmpty()){
				for(final ValueReference valueReference : possibleValuesForSelectionTraitProperty){
					if(Objects.equals(valueReference.getDescription(), categoricalValue)){
						source.setSelectionTraitValue(extractValue(String.valueOf(valueReference.getId()), termID));
					}
				}
			}
		}
	}

	protected String extractValue(final String value, final Integer variableTermID) {
		if (!StringUtils.isNumeric(value)) {
			// this case happens when a character value is provided as an out of bounds value
			return value;
		}

		// TODO: should I use another method???? -> this is called once for each variable which has a SELECTION_TRAIT_PROPERTY property

		final String categoricalValue =
				this.ontologyVariableDataManager.retrieveVariableCategoricalNameValue(ContextHolder.getCurrentProgramOptional().orElse(null),
					variableTermID, Integer.parseInt(value), true);

		if (categoricalValue == null) {
			// this case happens when a numeric value is provided as an out of bounds value
			return value;
		} else {
			return categoricalValue;
		}
	}
}
