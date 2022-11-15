
package org.generationcp.middleware.ruleengine.naming.newrules;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.ruleengine.naming.newservice.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.context.MessageSource;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/14/2015 Time: 12:53 AM
 */
public class NamingRuleExecutionContext extends OrderedRuleExecutionContext {

	private ProcessCodeService processCodeService;
	private AbstractAdvancingSource advancingSource;
	private GermplasmDataManager germplasmDataManager;
	private List<String> currentData;
	private MessageSource messageSource;

	private List<String> tempData;

	public NamingRuleExecutionContext(final List<String> executionOrder, final ProcessCodeService processCodeService,
		final AbstractAdvancingSource advancingSource, final GermplasmDataManager germplasmDataManager, final List<String> currentData) {
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

	public ProcessCodeService getProcessCodeService() {
		return this.processCodeService;
	}

	public void setProcessCodeService(final ProcessCodeService processCodeService) {
		this.processCodeService = processCodeService;
	}

	public AbstractAdvancingSource getAdvancingSource() {
		return this.advancingSource;
	}

	public void setAdvancingSource(final AbstractAdvancingSource advancingSource) {
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
