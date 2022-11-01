
package org.generationcp.middleware.ruleengine.naming.impl;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.RulesNotConfiguredException;
import org.generationcp.middleware.ruleengine.naming.rules.EnforceUniqueNameRule;
import org.generationcp.middleware.ruleengine.naming.rules.NamingRuleExecutionContext;
import org.generationcp.middleware.ruleengine.naming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSourceList;
import org.generationcp.middleware.ruleengine.pojo.ImportedCross;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class NamingConventionServiceImpl implements NamingConventionService {

	public static final int NAME_MAX_LENGTH = 5000;

	@Resource
	private FieldbookService fieldbookMiddlewareService;

	@Resource
	private RulesService rulesService;

	@Resource
	private GermplasmDataManager germplasmDataManager;

	@Resource
	private ProcessCodeService processCodeService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private ResourceBundleMessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Deprecated
	@Override
	public void generateAdvanceListNames(final List<AdvancingSource> advancingSourceItems, final boolean checkForDuplicateName,
		final List<ImportedGermplasm> germplasmList) throws MiddlewareQueryException, RuleException {

		final TimerWatch timer = new TimerWatch("advance");
		final Locale locale = LocaleContextHolder.getLocale();

		Map<String, Integer> keySequenceMap = new HashMap<>();
		final Iterator<ImportedGermplasm> germplasmIterator = germplasmList.iterator();
		for (final AdvancingSource row : advancingSourceItems) {
			if (row.getGermplasm() != null && row.getPlantsSelected() != null && row.getBreedingMethod() != null
				&& row.getPlantsSelected() > 0 && row.getBreedingMethod().isBulkingMethod() != null) {
				row.setKeySequenceMap(keySequenceMap);

				final List<String> names;
				final RuleExecutionContext namingExecutionContext =
					this.setupNamingRuleExecutionContext(row, checkForDuplicateName);
				names = (List<String>) this.rulesService.runRules(namingExecutionContext);

				for (final String name : names) {
					final ImportedGermplasm germplasm = germplasmIterator.next();
					if (name.length() > NAME_MAX_LENGTH) {
						throw new MiddlewareQueryException("error.save.resulting.name.exceeds.limit");
					}
					germplasm.setDesig(name);
					this.assignNames(germplasm);
				}

				// Pass the key sequence map to the next entry to process
				keySequenceMap = row.getKeySequenceMap();
			}
		}
		timer.stop();
	}

	@Deprecated
	protected void assignNames(final ImportedGermplasm germplasm) {
		final List<Name> names = new ArrayList<>();

		final Name name = new Name();
		name.setTypeId(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID());
		name.setNval(germplasm.getDesig());
		name.setNstat(1);
		names.add(name);

		germplasm.setNames(names);
	}

	@Override
	public List<ImportedCross> generateCrossesList(final List<ImportedCross> importedCrosses, final AdvancingSourceList rows,
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
		for (final AdvancingSource advancingSource : rows.getRows()) {

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

	protected RuleExecutionContext setupNamingRuleExecutionContext(final AdvancingSource row, final boolean checkForDuplicateName) {
		List<String> sequenceList = Arrays.asList(this.ruleFactory.getRuleSequenceForNamespace("naming"));

		if (checkForDuplicateName) {
			// new array list is required since list generated from asList method does not support adding of more elements
			sequenceList = new ArrayList<>(sequenceList);
			sequenceList.add(EnforceUniqueNameRule.KEY);
		}

		final NamingRuleExecutionContext context =
			new NamingRuleExecutionContext(sequenceList, this.processCodeService, row, this.germplasmDataManager,
				new ArrayList<String>());
		context.setMessageSource(this.messageSource);

		return context;
	}

}
