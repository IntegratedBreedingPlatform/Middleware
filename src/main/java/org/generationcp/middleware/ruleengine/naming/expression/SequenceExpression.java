
package org.generationcp.middleware.ruleengine.naming.expression;

import org.apache.commons.lang3.BooleanUtils;
import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SequenceExpression extends BaseExpression implements Expression {

	// Insert double black slash since we're replacing by regular expressions
	private static final String KEY = "\\[SEQUENCE\\]";
	private static final Pattern PATTERN = Pattern.compile(SequenceExpression.KEY);

	// TODO: it's possible to refactor to avoid hitting the DB for each line
	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	public SequenceExpression() {
	}

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {

		final List<StringBuilder> newNames = new ArrayList<>();

		for (final StringBuilder value : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 0) {
				synchronized (SequenceExpression.class) {
					final int iterationCount = source.isBulkingMethod() ? 1 : source.getPlantsSelected();
					for (int i = 0; i < iterationCount; i++) {
						final StringBuilder newName = new StringBuilder(value);
						final String upperCaseValue = value.toString().toUpperCase();

						final Pattern pattern = Pattern.compile(this.getExpressionKey());
						final Matcher matcher = pattern.matcher(upperCaseValue);
						if (matcher.find()) {
							final String keyPrefix = upperCaseValue.substring(0, matcher.start());

							final int nextNumberInSequence;
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
