
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.apache.commons.lang3.BooleanUtils;
import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedGermplasmNamingService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
@Component
public class DeprecatedSequenceExpression extends DeprecatedBaseExpression implements DeprecatedExpression {

	// Insert double black slash since we're replacing by regular expressions
	private static final String KEY = "\\[SEQUENCE\\]";
	private static final Pattern PATTERN = Pattern.compile(DeprecatedSequenceExpression.KEY);


	@Autowired
	protected DeprecatedGermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final DeprecatedGermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	public DeprecatedSequenceExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {

		final List<StringBuilder> newNames = new ArrayList<>();

		for (final StringBuilder value : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 0) {
				synchronized (DeprecatedSequenceExpression.class) {
					final int iterationCount = source.isBulk() ? 1 : source.getPlantsSelected();
					for (int i = 0; i < iterationCount; i++) {
						final StringBuilder newName = new StringBuilder(value);
						final String upperCaseValue = value.toString().toUpperCase();

						final Pattern pattern = Pattern.compile(this.getExpressionKey());
						final Matcher matcher = pattern.matcher(upperCaseValue);
						if (matcher.find()) {
							final String keyPrefix = upperCaseValue.substring(0, matcher.start());

							int nextNumberInSequence;
							// In preview mode, do not increment the prefix in sequence registry
							if (BooleanUtils.isTrue(source.getDesignationIsPreviewOnly())) {
								if (source.getKeySequenceMap().containsKey(keyPrefix)) {
									nextNumberInSequence = source.getKeySequenceMap().get(keyPrefix) + 1;
								} else {
									nextNumberInSequence = this.germplasmNamingService.getNextSequence(keyPrefix);
								}
								source.getKeySequenceMap().put(keyPrefix, nextNumberInSequence);

							// Look up last sequence number from database for KeyPrefix with synchronization at class level
							} else {
								nextNumberInSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(keyPrefix);
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
		return DeprecatedSequenceExpression.KEY;
	}

	public Integer getNumberOfDigits(final StringBuilder container) {
		return 1;
	}

	void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getPattern(), container, value);
	}

	Pattern getPattern() {
		return DeprecatedSequenceExpression.PATTERN;
	}
}
