package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeLocationExpression extends BaseExpression {

	private static final Logger LOG = LoggerFactory.getLogger(ChangeLocationExpression.class);

	public static final String KEY = "[CLABBR]";

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		for (final StringBuilder container : values) {

			try {
				String suffixValue = "";
				if (advancingSource.getHarvestLocationId() != null && !advancingSource.getOriginGermplasm().getLocationId()
					.equals(advancingSource.getHarvestLocationId())) {
					suffixValue = advancingSource.getLocationAbbreviation();
				}

				this.replaceExpressionWithValue(container, suffixValue);
			} catch (final MiddlewareQueryException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public String getExpressionKey() {
		return KEY;
	}
}
