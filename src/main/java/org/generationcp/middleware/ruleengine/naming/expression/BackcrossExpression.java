package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackcrossExpression extends BaseExpression {

	public static final String KEY = "[BC]";
	static final String MALE_RECURRENT_SUFFIX = "M";
	static final String FEMALE_RECURRENT_SUFFIX = "F";

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {

		String output = "";

		final int computation = pedigreeDataManager.calculateRecurrentParent(source.getMaleGid(), source.getFemaleGid());

		if (PedigreeDataManagerImpl.FEMALE_RECURRENT == computation) {
			output += FEMALE_RECURRENT_SUFFIX;
		} else if (PedigreeDataManagerImpl.MALE_RECURRENT == computation) {
			output += MALE_RECURRENT_SUFFIX;
		}

		for (StringBuilder value : values) {

			this.replaceExpressionWithValue(value, output);

		}

	}

	@Override
	public String getExpressionKey() {
		return BackcrossExpression.KEY;
	}
}
