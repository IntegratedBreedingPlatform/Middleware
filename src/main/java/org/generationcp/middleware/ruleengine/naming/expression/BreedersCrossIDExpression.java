package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.generator.BreedersCrossIDGenerator;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

@Component
public class BreedersCrossIDExpression extends BaseExpression {

	@Autowired
	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	public static final String KEY = "[CIMCRS]";

	public BreedersCrossIDExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource source, final String capturedText) {

		/**
		 * Refer NamingConventionServiceImpl.addImportedGermplasmToList method
		 * It requires AdvancingStudy as well, here we are not able to get AdvancingStudy instance
		 * Basic Implementation has been added to calculate SelectionNumber
		 */
		for (final StringBuilder container : values) {
			final String newValue = this.breedersCrossIDGenerator
				.generateBreedersCrossID(source.getStudyId(), source.getEnvironmentDatasetId(), source.getConditions(),
					fromMeasurementRow(source.getTrailInstanceObservation()));
			this.replaceExpressionWithValue(container, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return BreedersCrossIDExpression.KEY;
	}
}
