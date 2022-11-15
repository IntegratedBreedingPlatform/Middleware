package org.generationcp.middleware.ruleengine.newnaming.impl;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.newnaming.rules.EnforceUniqueNameRule;
import org.generationcp.middleware.ruleengine.newnaming.rules.NamingRuleExecutionContext;
import org.generationcp.middleware.ruleengine.newnaming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.newnaming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.ruleengine.service.RulesService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@Transactional
public class NamingConventionServiceImpl implements NamingConventionService {

	// TODO: move to common constants
	public static final int NAME_MAX_LENGTH = 5000;

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

	@Override
	public void generateAdvanceListName(final List<AdvancingSource> advancingSources) throws RuleException {

		Map<String, Integer> keySequenceMap = new HashMap<>();
		for (final AdvancingSource advancingSource : advancingSources) {

			advancingSource.setKeySequenceMap(keySequenceMap);

			final RuleExecutionContext namingExecutionContext =
				this.setupNamingRuleExecutionContext(advancingSource, false);
			final List<String> generatedNames = (List<String>) this.rulesService.runRules(namingExecutionContext);
			IntStream.range(0, generatedNames.size()).forEach(i -> {
				final String generatedName = generatedNames.get(i);
				if (generatedName.length() > NAME_MAX_LENGTH) {
					throw new MiddlewareQueryException("error.save.resulting.name.exceeds.limit");
				}
				final Germplasm germplasm = advancingSource.getAdvancedGermplasms().get(i);
				final Name derivativeName =
					new Name(null, germplasm, GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), 1, generatedName,
						germplasm.getLocationId(), germplasm.getGdate(), 0);

				germplasm.getNames().add(derivativeName);
			});

			// Pass the key sequence map to the next entry to process
			keySequenceMap = advancingSource.getKeySequenceMap();
		}
	}

	protected <T extends AbstractAdvancingSource> RuleExecutionContext setupNamingRuleExecutionContext(final T row,
		final boolean checkForDuplicateName) {
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
