
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.BranchingRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;
import org.generationcp.middleware.ruleengine.pojo.AdvanceGermplasmChangeDetail;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/17/2015 Time: 1:50 PM
 */

@Component
public class EnforceUniqueNameRule extends BranchingRule<NamingRuleExecutionContext> {

	public static final String KEY = "Unique";

	@Override
	public Object runRule(final NamingRuleExecutionContext context) throws RuleException {

		final List<String> currentData = context.getCurrentData();
		final GermplasmDataManager germplasmDataManager = context.getGermplasmDataManager();
		final AdvancingSource source = context.getAdvancingSource();

		// as per agreement, unique name checking can be limited to only the first entry for the germplasm
		final String nameForChecking = currentData.get(0);
		try {
			final boolean duplicateExists = germplasmDataManager.checkIfMatches(nameForChecking);

			if (!duplicateExists) {
				// if necessary, update change detail object
				this.updateChangeDetailForAdvancingSource(context);

			} else {
				this.processNonUniqueName(context, source);
			}

		} catch (final MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

		// this rule does not actually do any processing on the data
		return null;
	}

	protected void processNonUniqueName(final NamingRuleExecutionContext context, final AdvancingSource advancingSource) {
		// if a duplicate is found, initialize an AdvanceGermplasmChangeDetail object containing the original duplicate, for confirmation
		// later on with the user
		this.initializeChangeDetailForAdvancingSource(context);

		// restore rule execution state to a previous temp save point
		context.setCurrentData(context.getTempData());

		if (!advancingSource.isForceUniqueNameGeneration()) {
			// if there is no current count expression, use the default to provide incrementing support
			if (advancingSource.getBreedingMethod().getCount() == null || advancingSource.getBreedingMethod().getCount().isEmpty()) {
				advancingSource.getBreedingMethod().setCount(CountRule.DEFAULT_COUNT);
				advancingSource.setForceUniqueNameGeneration(true);
			} else if (advancingSource.isBulkingMethod()) {
				advancingSource.setForceUniqueNameGeneration(true);
			} else {
				// simply increment the sequence used to generate the count. no other flags set so as to preserve previously used logic
				advancingSource.setCurrentMaxSequence(advancingSource.getCurrentMaxSequence() + 1);
			}
		} else {
			// if force unique name generation flag is set, then simply increment the current sequence used for generating the count
			advancingSource.setCurrentMaxSequence(advancingSource.getCurrentMaxSequence() + 1);
		}
	}

	@Override
	public String getNextRuleStepKey(final NamingRuleExecutionContext context) {
		final AdvancingSource source = context.getAdvancingSource();

		final AdvanceGermplasmChangeDetail changeDetailObject = source.getChangeDetail();

		if (changeDetailObject == null || changeDetailObject.getNewAdvanceName() != null) {
			return super.getNextRuleStepKey(context);
		} else {
			this.prepareContextForBranchingToKey(context, CountRule.KEY);
			return CountRule.KEY;
		}
	}

	protected void initializeChangeDetailForAdvancingSource(final NamingRuleExecutionContext context) {
		AdvanceGermplasmChangeDetail changeDetail = context.getAdvancingSource().getChangeDetail();

		// change detail object only needs to be initialized once per advancing source
		if (changeDetail == null) {
			final String offendingName = context.getCurrentData().get(0);
			changeDetail = new AdvanceGermplasmChangeDetail();
			changeDetail.setOldAdvanceName(offendingName);
			changeDetail.setQuestionText(context.getMessageSource().getMessage("advance.study.duplicate.question.text",
					new String[] {offendingName}, LocaleContextHolder.getLocale()));

			context.getAdvancingSource().setChangeDetail(changeDetail);
		}
	}

	protected void updateChangeDetailForAdvancingSource(final NamingRuleExecutionContext context) {
		final AdvanceGermplasmChangeDetail changeDetail = context.getAdvancingSource().getChangeDetail();

		if (changeDetail != null) {

			// provide change detail object with the resulting name that passes the uniqueness check
			final String passingName = context.getCurrentData().get(0);
			changeDetail.setNewAdvanceName(passingName);
			final Locale locale = LocaleContextHolder.getLocale();
			changeDetail.setAddSequenceText(context.getMessageSource().getMessage("advance.study.duplicate.add.sequence.text",
					new String[] {passingName}, locale));
		}
	}

	@Override
	public String getKey() {
		return EnforceUniqueNameRule.KEY;
	}

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}
	
}
