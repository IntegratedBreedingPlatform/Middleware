
package org.generationcp.middleware.ruleengine.util;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public interface ExpressionHelperCallback {

	public void evaluateCapturedExpression(String capturedText, String originalInput, int start, int end);
}
