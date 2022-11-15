
package org.generationcp.middleware.ruleengine.naming.expression;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GroupCountExpression extends BaseExpression {

	public static final String KEY = "[COUNT]";
	public static final Integer MINIMUM_BULK_COUNT = 2;
	public static final String BULK_COUNT_PREFIX = "B*";
	public static final String POUND_COUNT_PREFIX = "#*";
	public static final int CAPTURED_FINAL_EXPRESSION_GROUP = 1;

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		for (final StringBuilder value : values) {
			String currentValue = value.toString();

			// this code handles two variants of this process code : B*[COUNT] and #*[COUNT]. Here we retrieve the "prefix" to determine
			// which (B* or #*)
			final String countPrefix = this.getCountPrefix(currentValue);

			// when processing B*[COUNT], we count B's. for #*[COUNT], #. Here we determine which
			final String targetCountExpression = this.getTargetCountExpression(countPrefix);

			// we remove meta characters added by our process code system so that they don't interfere with processing
			final String noMetaString = removeMetaCharacters(currentValue, countPrefix, advancingSource);

			final CountResultBean result = this.countExpressionOccurence(targetCountExpression, noMetaString);
			int generatedCountValue = result.getCount();

			// if the method is a bulking method, we're expected to increment the count
			if (Boolean.TRUE.equals(advancingSource.getBreedingMethod().isBulkingMethod())) {
				generatedCountValue = result.getCount() + 1;
			}

			// we remove the captured expression entirely so that we have a blank slate when writing the resulting count
			currentValue = this.cleanupString(new StringBuilder(noMetaString), result);

			if (generatedCountValue >= MINIMUM_BULK_COUNT) {

				currentValue = currentValue + targetCountExpression + "*" + generatedCountValue;
				value.delete(0, value.length());
				value.append(currentValue);
			} else {
				value.delete(0, value.length());
				value.append(currentValue);

				// do while loop is used because there should be a -B or -# appended if the count is 0
				int i = 0;
				do {
                    if (!StringUtils.isEmpty(advancingSource.getBreedingMethod().getSeparator())) {
                        value.append(advancingSource.getBreedingMethod().getSeparator());
                    }

					value.append(targetCountExpression);
					i++;
				} while (i < result.getCount());

			}

		}
	}

	protected String removeMetaCharacters(final String value, final String countPrefix, final AdvancingSource advancingSource) {
		// we strip the B*[COUNT] or #*COUNT from the name being processed
		String valueWithoutProcessCode = value.replace(countPrefix + this.getExpressionKey(), "");

		// if in case a separator is defined for the breeding method, it will have been added to the end of the name. we'll need to remove
		// it so that it doesn't hide the character we're looking to count (which is expected to be at the end of the line)
		if (!StringUtils.isEmpty(advancingSource.getBreedingMethod().getSeparator())
				&& valueWithoutProcessCode.endsWith(advancingSource.getBreedingMethod().getSeparator())) {
			final int lastIndex = valueWithoutProcessCode.lastIndexOf(advancingSource.getBreedingMethod().getSeparator());
			valueWithoutProcessCode = valueWithoutProcessCode.substring(0, lastIndex);
		}

		return valueWithoutProcessCode;
	}

	protected String cleanupString(final StringBuilder value, final CountResultBean result) {
		value.replace(result.getStart(), result.getEnd(), "");

		return value.toString();
	}

	protected String getCountPrefix(final String input) {
		final int start = input.indexOf(GroupCountExpression.KEY);
        // the prefix is the first two characters ahead of the [COUNT]; e.g., B* or #*
		return input.substring(start - 2, start);
	}

	protected String getTargetCountExpression(final String countPrefix) {
		if (GroupCountExpression.BULK_COUNT_PREFIX.equals(countPrefix)) {
			return "B";
		} else if (GroupCountExpression.POUND_COUNT_PREFIX.equals(countPrefix)) {
			return "#";
		} else {
			throw new IllegalArgumentException("Invalid count expression");
		}
	}

	public CountResultBean countExpressionOccurence(final String expression, final String currentValue) {
		final Pattern pattern = Pattern.compile("(" + expression + "([*][1-9])*)$");
		final Matcher matcher = pattern.matcher(currentValue);

		int startIndex = 0;
		int endIndex = 0;
		int count = 0;
		if (matcher.find()) {
			count = 1;
			// if there is a *n instance found
			if (matcher.groupCount() == 2) {
				final String existingCountString = matcher.group(2);
				if (!StringUtils.isEmpty(existingCountString)) {
					final int existingCount = Integer.parseInt(existingCountString.replace("*", ""));
					count += existingCount - 1;
				}

			}

			startIndex = matcher.start(CAPTURED_FINAL_EXPRESSION_GROUP);
			endIndex = matcher.end(CAPTURED_FINAL_EXPRESSION_GROUP);
		}

		return new CountResultBean(count, startIndex, endIndex);
	}

	@Override
	public String getExpressionKey() {
		return GroupCountExpression.KEY;
	}

	static class CountResultBean {

		private final int count;
		private final int start;
		private final int end;

		public CountResultBean(final int count, final int start, final int end) {
			this.count = count;
			this.start = start;
			this.end = end;
		}

		public int getCount() {
			return this.count;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}
	}

}
