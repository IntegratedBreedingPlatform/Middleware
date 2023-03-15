
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SequenceExpression extends BaseExpression implements Expression {

	// Insert double black slash since we're replacing by regular expressions
	private static final String KEY = "\\[SEQUENCE\\]";
	private static final Pattern PATTERN = Pattern.compile(SequenceExpression.KEY);

	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	public SequenceExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {

		final List<StringBuilder> newNames = new ArrayList<>();

		for (final StringBuilder value : values) {
			if (advancingSource.getPlantsSelected() != null && advancingSource.getPlantsSelected() > 0) {
				synchronized (SequenceExpression.class) {
					final int iterationCount = advancingSource.isBulkingMethod() ? 1 : advancingSource.getPlantsSelected();
					for (int i = 0; i < iterationCount; i++) {
						final StringBuilder newName = new StringBuilder(value);
						final String upperCaseValue = value.toString().toUpperCase();

						final Pattern pattern = Pattern.compile(this.getExpressionKey());
						final Matcher matcher = pattern.matcher(upperCaseValue);
						if (matcher.find()) {
							final String keyPrefix = upperCaseValue.substring(0, matcher.start());
							int nextNumberInSequence = 0;

							// check if action is preview, do not increment sequence value in the database if true
							if (advancingSource.isPreview()) {
								final Map<String, Integer> keySequenceMap = advancingSource.getKeySequenceMap();

								// check if keyPrefix is previously used in the same preview action
								if (keySequenceMap.containsKey(keyPrefix)) {
									nextNumberInSequence = keySequenceMap.get(keyPrefix) + 1;
								}
								// otherwise, retrieve next sequence from DB
								else {
									nextNumberInSequence = this.germplasmNamingService.getNextSequenceUsingNativeSQL(keyPrefix);
								}

								keySequenceMap.put(keyPrefix, nextNumberInSequence);
							} else {
								nextNumberInSequence =
									this.germplasmNamingService.getNextNumberAndIncrementSequenceUsingNativeSQL(keyPrefix);
							}

							final String numberString = this.germplasmNamingService
								.getNumberWithLeadingZeroesAsString(nextNumberInSequence, this.getNumberOfDigits(value));

							this.replaceRegularExpressionKeyWithValue(newName, numberString);
							newNames.add(newName);
						}

					}
				}

			} else {
				this.replaceRegularExpressionKeyWithValue(value, "");
				newNames.add(value);
			}
		}

		values.clear();
		values.addAll(newNames);
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}

	public Integer getNumberOfDigits(final StringBuilder container) {
		return 1;
	}

	void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getPattern(), container, value);
	}

	Pattern getPattern() {
		return SequenceExpression.PATTERN;
	}
}
