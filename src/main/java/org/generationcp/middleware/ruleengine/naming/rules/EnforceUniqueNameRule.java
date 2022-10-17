
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.BranchingRule;
import org.generationcp.middleware.ruleengine.RuleException;
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
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {

		List<String> currentData = context.getCurrentData();
		GermplasmDataManager germplasmDataManager = context.getGermplasmDataManager();
		AdvancingSource source = context.getAdvancingSource();

		// as per agreement, unique name checking can be limited to only the first entry for the germplasm
		String nameForChecking = currentData.get(0);
		try {
			boolean duplicateExists = germplasmDataManager.checkIfMatches(nameForChecking);

			if (!duplicateExists) {
				// if necessary, update change detail object
				this.updateChangeDetailForAdvancingSource(context);

			} else {
				this.processNonUniqueName(context, source);
			}

		} catch (MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

		// this rule does not actually do any processing on the data
		return null;
	}

	protected void processNonUniqueName(NamingRuleExecutionContext context, AdvancingSource source) {
		// if a duplicate is found, initialize an AdvanceGermplasmChangeDetail object containing the original duplicate, for confirmation
		// later on with the user
		this.initializeChangeDetailForAdvancingSource(context);

		// restore rule execution state to a previous temp save point
		context.setCurrentData(context.getTempData());

		if (!source.isForceUniqueNameGeneration()) {
			// if there is no current count expression, use the default to provide incrementing support
			if (source.getBreedingMethod().getCount() == null || source.getBreedingMethod().getCount().isEmpty()) {
				source.getBreedingMethod().setCount(CountRule.DEFAULT_COUNT);
				source.setForceUniqueNameGeneration(true);
			} else if (source.isBulk()) {
				source.setForceUniqueNameGeneration(true);
			} else {
				// simply increment the sequence used to generate the count. no other flags set so as to preserve previously used logic
				source.setCurrentMaxSequence(source.getCurrentMaxSequence() + 1);
			}
		} else {
			// if force unique name generation flag is set, then simply increment the current sequence used for generating the count
			source.setCurrentMaxSequence(source.getCurrentMaxSequence() + 1);
		}
	}

	@Override
	public String getNextRuleStepKey(NamingRuleExecutionContext context) {
		AdvancingSource source = context.getAdvancingSource();

		AdvanceGermplasmChangeDetail changeDetailObject = source.getChangeDetail();

		if (changeDetailObject == null || changeDetailObject.getNewAdvanceName() != null) {
			return super.getNextRuleStepKey(context);
		} else {
			this.prepareContextForBranchingToKey(context, CountRule.KEY);
			return CountRule.KEY;
		}
	}

	protected void initializeChangeDetailForAdvancingSource(NamingRuleExecutionContext context) {
		AdvanceGermplasmChangeDetail changeDetail = context.getAdvancingSource().getChangeDetail();

		// change detail object only needs to be initialized once per advancing source
		if (changeDetail == null) {
			String offendingName = context.getCurrentData().get(0);
			changeDetail = new AdvanceGermplasmChangeDetail();
			changeDetail.setOldAdvanceName(offendingName);
			changeDetail.setQuestionText(context.getMessageSource().getMessage("advance.study.duplicate.question.text",
					new String[] {offendingName}, LocaleContextHolder.getLocale()));

			context.getAdvancingSource().setChangeDetail(changeDetail);
		}
	}

	protected void updateChangeDetailForAdvancingSource(NamingRuleExecutionContext context) {
		AdvanceGermplasmChangeDetail changeDetail = context.getAdvancingSource().getChangeDetail();

		if (changeDetail != null) {

			// provide change detail object with the resulting name that passes the uniqueness check
			String passingName = context.getCurrentData().get(0);
			changeDetail.setNewAdvanceName(passingName);
			Locale locale = LocaleContextHolder.getLocale();
			changeDetail.setAddSequenceText(context.getMessageSource().getMessage("advance.study.duplicate.add.sequence.text",
					new String[] {passingName}, locale));
		}
	}

	@Override
	public String getKey() {
		return EnforceUniqueNameRule.KEY;
	}
}
