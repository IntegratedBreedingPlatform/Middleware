
package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoubleHaploidSourceExpression extends BaseExpression {

	public static final String KEY = "[DHSOURCE]";

	// TODO: it's possible to refactor to avoid hitting the DB for each line
	@Autowired
	protected KeySequenceRegisterService keySequenceRegisterService;

	// This setter is only used to inject this service only in test
	public void setKeySequenceRegisterService(final KeySequenceRegisterService keySequenceRegisterService) {
		this.keySequenceRegisterService = keySequenceRegisterService;
	}

	/**
	 * Method to append '@' + [lastUsedSequence] in designation column ex. @1, @2 etc.
	 * @param values       Designation column value
	 * @param source       Advancing Source object contains information about source
	 * @param capturedText
	 */
	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		for (final StringBuilder value : values) {
			final int checkIndex = value.lastIndexOf("@0");
			if (checkIndex != -1) {
				synchronized (DoubleHaploidSourceExpression.class) {
					final String keyPrefix = value.substring(0, checkIndex + 1);
					// Get last sequence number for KeyPrefix with synchronization at class level
					final int lastUsedSequence = this.keySequenceRegisterService.incrementAndGetNextSequence(keyPrefix);
					this.replaceExistingSuffixValue(value, checkIndex + 1);
					this.replaceExpressionWithValue(value, String.valueOf(lastUsedSequence));
				}

			} else {
				// If designation does not contains @0 string then keep its value as it is
				this.replaceExpressionWithValue(value, "");
			}
		}
	}

	@Override
	public String getExpressionKey() {
		return KEY;
	}

	/**
	 * Replace the existing suffix value '0' from designation with empty String
	 *
	 * @param container  designation value
	 * @param startIndex starting index of 0
	 */
	private void replaceExistingSuffixValue(final StringBuilder container, final int startIndex) {
		final int endIndex = startIndex + 1;
		container.replace(startIndex, endIndex, "");
	}
}
