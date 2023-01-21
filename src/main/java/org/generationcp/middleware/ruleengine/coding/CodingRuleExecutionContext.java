package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;

import java.util.List;

public class CodingRuleExecutionContext extends OrderedRuleExecutionContext {

	private static final String EMPTY_STRING = "";
	private final NamingConfiguration namingConfiguration;
	private Integer startNumber;
	private String currentData = EMPTY_STRING;

	public CodingRuleExecutionContext(final List<String> executionOrder, final NamingConfiguration namingConfiguration) {
		super(executionOrder);
		this.namingConfiguration = namingConfiguration;
	}

	@Override
	public Object getRuleExecutionOutput() {
		return this.currentData;
	}

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.CODING;
	}

	public NamingConfiguration getNamingConfiguration() {
		return namingConfiguration;
	}

	public Integer getStartNumber() {
		return startNumber;
	}

	public void setStartNumber(final Integer startNumber) {
		this.startNumber = startNumber;
	}

	public String getCurrentData() {
		return currentData;
	}

	public void setCurrentData(final String currentData) {
		this.currentData = currentData;
	}

	public void reset() {
		this.setCurrentData(EMPTY_STRING);
		this.setCurrentExecutionIndex(0);
	}
}
