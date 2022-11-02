package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class CodingSequenceExpression extends BaseCodingExpression {

	// Insert double black slash since we're replacing by regular expressions
	private static final String KEY = "\\[SEQUENCE\\]";
	private static final Pattern PATTERN = Pattern.compile(CodingSequenceExpression.KEY);

	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	@Override
	public void apply(final List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration) {
		final String prefix = namingConfiguration.getPrefix();
		for (final StringBuilder container : values) {
			final Integer lastUsedSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(prefix);
			final String numberString =
				this.germplasmNamingService.getNumberWithLeadingZeroesAsString(lastUsedSequence, this.getNumberOfDigits(container));
			this.replaceRegularExpressionKeyWithValue(container, numberString);
		}
	}

	@Override
	public String getExpressionKey() {
		return CodingSequenceExpression.KEY;
	}

	@Override
	public Pattern getPattern() {
		return CodingSequenceExpression.PATTERN;
	}

	public Integer getNumberOfDigits(final StringBuilder container) {
		return 1;
	}

}
