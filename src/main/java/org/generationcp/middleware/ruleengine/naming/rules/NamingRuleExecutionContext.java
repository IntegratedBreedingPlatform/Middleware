
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.context.MessageSource;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/14/2015 Time: 12:53 AM
 */
public class NamingRuleExecutionContext extends OrderedRuleExecutionContext {

	private ProcessCodeService processCodeService;
	private DeprecatedAdvancingSource advancingSource;
	private GermplasmDataManager germplasmDataManager;
	private List<String> currentData;
	private MessageSource messageSource;

	private List<String> tempData;

	public NamingRuleExecutionContext(List<String> executionOrder, ProcessCodeService processCodeService, DeprecatedAdvancingSource advancingSource,
			GermplasmDataManager germplasmDataManager, List<String> currentData) {
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

	public void setProcessCodeService(ProcessCodeService processCodeService) {
		this.processCodeService = processCodeService;
	}

	public DeprecatedAdvancingSource getAdvancingSource() {
		return this.advancingSource;
	}

	public void setAdvancingSource(DeprecatedAdvancingSource advancingSource) {
		this.advancingSource = advancingSource;
	}

	public List<String> getCurrentData() {
		return this.currentData;
	}

	public void setCurrentData(List<String> currentData) {
		this.currentData = currentData;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return this.germplasmDataManager;
	}

	public void setGermplasmDataManager(GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public List<String> getTempData() {
		return this.tempData;
	}

	public void setTempData(List<String> tempData) {
		this.tempData = tempData;
	}

	public MessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
