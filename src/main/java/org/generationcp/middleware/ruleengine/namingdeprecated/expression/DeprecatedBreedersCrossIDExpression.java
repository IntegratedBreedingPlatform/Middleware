package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.generator.BreedersCrossIDGenerator;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

@Deprecated
@Component
public class DeprecatedBreedersCrossIDExpression extends DeprecatedBaseExpression {

	@Autowired
	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	public static final String KEY = "[CIMCRS]";

	public DeprecatedBreedersCrossIDExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {

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
		return DeprecatedBreedersCrossIDExpression.KEY;
	}
}
