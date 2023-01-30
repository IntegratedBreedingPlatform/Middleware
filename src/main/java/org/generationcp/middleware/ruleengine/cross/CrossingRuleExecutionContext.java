
package org.generationcp.middleware.ruleengine.cross;

import java.util.List;

import org.generationcp.middleware.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;
import org.generationcp.middleware.ruleengine.settings.CrossSetting;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;

/**
 * Created by Daniel Villafuerte on 6/6/2015.
 */
public class CrossingRuleExecutionContext extends OrderedRuleExecutionContext {

	private GermplasmDataManager germplasmDataManager;
	private PedigreeDataManager pedigreeDataManager;

	private Integer maleGid;
	private Integer femaleGid;
	private String currentCrossName;
	private CrossSetting crossSetting;

	public CrossingRuleExecutionContext(final List<String> executionOrder, final CrossSetting crossSetting, final Integer maleGid,
			final Integer femaleGid, final GermplasmDataManager germplasmDataManager, final PedigreeDataManager pedigreeDataManager) {
		super(executionOrder);
		this.crossSetting = crossSetting;
		this.maleGid = maleGid;
		this.germplasmDataManager = germplasmDataManager;
		this.pedigreeDataManager = pedigreeDataManager;
		this.femaleGid = femaleGid;
		this.currentCrossName = "";
	}

	public CrossingRuleExecutionContext(final List<String> executionOrder) {
		super(executionOrder);
	}

	@Override
	public Object getRuleExecutionOutput() {
		return null;
	}

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}

	public Integer getFemaleGid() {
		return femaleGid;
	}

	public void setFemaleGid(final Integer femaleGid) {
		this.femaleGid = femaleGid;
	}

	public Integer getMaleGid() {
		return maleGid;
	}

	public void setMaleGid(final Integer maleGid) {
		this.maleGid = maleGid;
	}

	public String getCurrentCrossName() {
		return currentCrossName;
	}

	public void setCurrentCrossName(final String currentCrossName) {
		this.currentCrossName = currentCrossName;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return germplasmDataManager;
	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public CrossSetting getCrossSetting() {
		return crossSetting;
	}

	public void setCrossSetting(final CrossSetting crossSetting) {
		this.crossSetting = crossSetting;
	}

	public PedigreeDataManager getPedigreeDataManager() {
		return pedigreeDataManager;
	}

	public void setPedigreeDataManager(final PedigreeDataManager pedigreeDataManager) {
		this.pedigreeDataManager = pedigreeDataManager;
	}
}
