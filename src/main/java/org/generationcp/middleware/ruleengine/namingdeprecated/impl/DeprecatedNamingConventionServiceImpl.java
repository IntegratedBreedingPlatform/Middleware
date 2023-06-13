
package org.generationcp.middleware.ruleengine.namingdeprecated.impl;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;
import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.RulesNotConfiguredException;
import org.generationcp.middleware.ruleengine.namingdeprecated.rules.DeprecatedEnforceUniqueNameRule;
import org.generationcp.middleware.ruleengine.namingdeprecated.rules.DeprecatedNamingRuleExecutionContext;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedNamingConventionService;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSourceList;
import org.generationcp.middleware.ruleengine.pojo.ImportedCross;
import org.generationcp.middleware.ruleengine.service.RulesService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.TimerWatch;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Service
@Transactional
public class DeprecatedNamingConventionServiceImpl implements DeprecatedNamingConventionService {

	@Resource
	private FieldbookService fieldbookMiddlewareService;

	@Resource
	private RulesService rulesService;

	@Resource
	private GermplasmDataManager germplasmDataManager;

	@Resource
	private DeprecatedProcessCodeService processCodeService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private ResourceBundleMessageSource messageSource;

	@Override
	public List<ImportedCross> generateCrossesList(final List<ImportedCross> importedCrosses, final DeprecatedAdvancingSourceList rows,
		final boolean checkForDuplicateName, final Workbook workbook, final List<Integer> gids) throws RuleException {

		final List<Method> methodList = this.fieldbookMiddlewareService.getAllBreedingMethods(false);
		final Map<Integer, Method> breedingMethodMap = new HashMap<>();
		for (final Method method : methodList) {
			breedingMethodMap.put(method.getMid(), method);
		}

		int index = 0;
		final TimerWatch timer = new TimerWatch("cross");

		// PreviousMaxSequence is used is the DEFAULT indexed numbering used for entries.
		// The [SEQUENCE] code does not read this number but instead queries from the DB the next available number
		int previousMaxSequence = 0;
		Map<String, Integer> keySequenceMap = new HashMap<>();
		for (final DeprecatedAdvancingSource advancingSource : rows.getRows()) {

			final ImportedCross importedCross = importedCrosses.get(index++);
			final List<String> names;
			advancingSource.setCurrentMaxSequence(previousMaxSequence);
			advancingSource.setKeySequenceMap(keySequenceMap);

			final Integer breedingMethodId = advancingSource.getBreedingMethodId();
			final Method selectedMethod = breedingMethodMap.get(breedingMethodId);

			if (!this.germplasmDataManager.isMethodNamingConfigurationValid(selectedMethod)) {
				throw new RulesNotConfiguredException(this.messageSource
					.getMessage("error.save.cross.rule.not.configured", new Object[] {selectedMethod.getMname()}, "The rules"
						+ " were not configured", LocaleContextHolder.getLocale()));
			}

			if (StringUtils.isBlank(selectedMethod.getPrefix())) {
				throw new RulesNotConfiguredException(this.messageSource
					.getMessage("error.save.cross.method.blank.prefix", new Object[] {selectedMethod.getMname()},
						LocaleContextHolder.getLocale()));
			}

			// here, we resolve the breeding method ID stored in the advancing source object into a proper breeding Method object
			advancingSource.setBreedingMethod(selectedMethod);
			//default plants selected value to 1 for list of crosses because sequence is not working if plants selected value is not set
			advancingSource.setPlantsSelected(1);

			// pass the parent gids (female and male) of the imported cross, this is required to properly resolve the Backcross process codes.
			advancingSource
				.setFemaleGid(StringUtils.isNumeric(importedCross.getFemaleGid()) ? Integer.valueOf(importedCross.getFemaleGid()) : 0);
			// Always gets the first male parent, ie. GPID2
			final String firstMaleGid = importedCross.getMaleGids().get(0).toString();
			advancingSource.setMaleGid(StringUtils.isNumeric(firstMaleGid) ? Integer.valueOf(firstMaleGid) : 0);

			final RuleExecutionContext namingExecutionContext =
				this.setupNamingRuleExecutionContext(advancingSource, checkForDuplicateName);
			names = (List<String>) this.rulesService.runRules(namingExecutionContext);

			// Save away the current max sequence once rules have been run for this entry.
			previousMaxSequence = advancingSource.getCurrentMaxSequence() + 1;
			for (final String name : names) {
				importedCross.setDesig(name);
			}
			// Pass the key sequence map to the next entry to process
			keySequenceMap = advancingSource.getKeySequenceMap();
		}
		timer.stop();
		return importedCrosses;
	}

	protected RuleExecutionContext setupNamingRuleExecutionContext(final DeprecatedAdvancingSource row,
		final boolean checkForDuplicateName) {
		List<String> sequenceList = Arrays.asList(this.ruleFactory.getRuleSequenceForNamespace(RuleExecutionNamespace.NAMING));

		if (checkForDuplicateName) {
			// new array list is required since list generated from asList method does not support adding of more elements
			sequenceList = new ArrayList<>(sequenceList);
			sequenceList.add(DeprecatedEnforceUniqueNameRule.KEY);
		}

		final DeprecatedNamingRuleExecutionContext context =
			new DeprecatedNamingRuleExecutionContext(sequenceList, this.processCodeService, row, this.germplasmDataManager,
				new ArrayList<>());
		context.setMessageSource(this.messageSource);

		return context;
	}

}
