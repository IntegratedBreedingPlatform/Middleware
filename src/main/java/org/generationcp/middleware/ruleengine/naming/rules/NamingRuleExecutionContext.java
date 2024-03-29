
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.context.MessageSource;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/14/2015 Time: 12:53 AM
 */
public class NamingRuleExecutionContext extends OrderedRuleExecutionContext {

	private ProcessCodeService processCodeService;
	private AdvancingSource advancingSource;
	private GermplasmDataManager germplasmDataManager;
	private List<String> currentData;
	private MessageSource messageSource;

	private List<String> tempData;

	public NamingRuleExecutionContext(final List<String> executionOrder, final ProcessCodeService processCodeService,
		final AdvancingSource advancingSource, final GermplasmDataManager germplasmDataManager, final List<String> currentData) {
		super(executionOrder);
		this.processCodeService = processCodeService;
		this.advancingSource = advancingSource;
		this.currentData = currentData;
		this.germplasmDataManager = germplasmDataManager;

	}

	@Override
	public Object getRuleExecutionOutput() {
		return this.currentData;
	}

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}

	public ProcessCodeService getProcessCodeService() {
		return this.processCodeService;
	}

	public void setProcessCodeService(final ProcessCodeService processCodeService) {
		this.processCodeService = processCodeService;
	}

	public AdvancingSource getAdvancingSource() {
		return this.advancingSource;
	}

	public void setAdvancingSource(final AdvancingSource advancingSource) {
		this.advancingSource = advancingSource;
	}

	public List<String> getCurrentData() {
		return this.currentData;
	}

	public void setCurrentData(final List<String> currentData) {
		this.currentData = currentData;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return this.germplasmDataManager;
	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public List<String> getTempData() {
		return this.tempData;
	}

	public void setTempData(final List<String> tempData) {
		this.tempData = tempData;
	}

	public MessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
