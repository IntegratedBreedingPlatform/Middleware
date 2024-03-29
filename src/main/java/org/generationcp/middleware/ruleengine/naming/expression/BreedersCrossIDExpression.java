package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.generator.BreedersCrossIDGenerator;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BreedersCrossIDExpression extends BaseExpression {

	@Autowired
	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	public static final String KEY = "[CIMCRS]";

	public BreedersCrossIDExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {

		/**
		 * Refer NamingConventionServiceImpl.addImportedGermplasmToList method
		 * It requires AdvancingStudy as well, here we are not able to get AdvancingStudy instance
		 * Basic Implementation has been added to calculate SelectionNumber
		 */
		for (final StringBuilder container : values) {
			final Collection<ObservationUnitData> observations =
				CollectionUtils.isEmpty(advancingSource.getTrialInstanceObservation().getEnvironmentVariables()) ? new ArrayList<>() :
					advancingSource.getTrialInstanceObservation().getEnvironmentVariables().values();
			final String newValue = this.breedersCrossIDGenerator.generateBreedersCrossID(observations);
			this.replaceExpressionWithValue(container, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return BreedersCrossIDExpression.KEY;
	}

}
