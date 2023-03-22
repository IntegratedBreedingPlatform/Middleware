
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DoubleHaploidSourceExpression extends BaseExpression {

	public static final String KEY = "[DHSOURCE]";

	@Autowired
	protected KeySequenceRegisterService keySequenceRegisterService;

	// This setter is only used to inject this service only in test
	public void setKeySequenceRegisterService(final KeySequenceRegisterService keySequenceRegisterService) {
		this.keySequenceRegisterService = keySequenceRegisterService;
	}

	/**
	 * Method to append '@' + [lastUsedSequence] in designation column ex. @1, @2 etc.
	 *
	 * @param values          Designation column value
	 * @param advancingSource Advancing Source object contains information about source
	 * @param capturedText
	 */
	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		for (final StringBuilder value : values) {
			final int checkIndex = value.lastIndexOf("@0");
			if (checkIndex != -1) {
				synchronized (DoubleHaploidSourceExpression.class) {
					final String keyPrefix = value.substring(0, checkIndex + 1);
					int nextNumberInSequence = 0;

					// check if action is preview, do not increment DB value if true
					if (advancingSource.isPreview()) {
						final Map<String, Integer> keySequenceMap = advancingSource.getKeySequenceMap();

						// check if keyPrefix is previously used in the same preview action
						if (keySequenceMap.containsKey(keyPrefix)) {
							nextNumberInSequence = keySequenceMap.get(keyPrefix) + 1;
						}
						// otherwise, retrieve next sequence from DB
						else {
							nextNumberInSequence = this.keySequenceRegisterService.getNextSequenceUsingNativeSQL(keyPrefix);
						}

						keySequenceMap.put(keyPrefix, nextNumberInSequence);
					} else {
						// Get last sequence number for KeyPrefix with synchronization at class level
						nextNumberInSequence = this.keySequenceRegisterService.incrementAndGetNextSequenceUsingNativeSQL(keyPrefix);
					}
					this.replaceExistingSuffixValue(value, checkIndex + 1);
					this.replaceExpressionWithValue(value, String.valueOf(nextNumberInSequence));
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
