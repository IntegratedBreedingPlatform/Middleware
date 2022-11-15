
package org.generationcp.middleware.ruleengine.naming.newrules;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.pojo.AdvanceGermplasmChangeDetail;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/17/2015 Time: 3:17 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class EnforceUniqueNameRuleTest {

	public static final int TEST_MAX_SEQUENCE = 0;
	public static final int ENFORCE_RULE_INDEX = 1;

	private EnforceUniqueNameRule dut;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private DeprecatedAdvancingSource source;

	@Mock
	private Method breedingMethod;

	@Mock
	private NamingRuleExecutionContext context;

	private List<String> tempData;

	@Before
	public void setUp() throws Exception {
		this.dut = new EnforceUniqueNameRule();
	}

	@Test
	public void testUniqueNameCheckNoMatch() throws MiddlewareQueryException, RuleException {

		this.setupTestExecutionContext();
		Mockito.when(this.germplasmDataManager.checkIfMatches(Matchers.anyString())).thenReturn(false);
		this.dut.runRule(this.context);

		Mockito.verify(this.context, Mockito.never()).setCurrentData(this.tempData);
		Mockito.verify(this.source, Mockito.never()).setCurrentMaxSequence(EnforceUniqueNameRuleTest.TEST_MAX_SEQUENCE + 1);
		Mockito.verify(this.source, Mockito.never()).setChangeDetail(Matchers.any(AdvanceGermplasmChangeDetail.class));
	}

	@Test
	public void testUniqueNameCheckMatchFoundNoCount() throws MiddlewareQueryException, RuleException {

		this.setupTestExecutionContext();
		Mockito.when(this.germplasmDataManager.checkIfMatches(Matchers.anyString())).thenReturn(true);
		this.dut.runRule(this.context);

		// verify that current rule execution state is pointed back to previous stored data
		Mockito.verify(this.context).setCurrentData(this.tempData);
		Mockito.verify(this.source).setChangeDetail(Matchers.any(AdvanceGermplasmChangeDetail.class));

		// verify that force unique name generation flag is set
		Mockito.verify(this.source).setForceUniqueNameGeneration(true);
		// verify that default count rule is provided so that count rule execution will proceed
		Mockito.verify(this.breedingMethod).setCount(CountRule.DEFAULT_COUNT);
	}

	@Test
	public void testUniqueNameCheckMatchFoundFlagSet() throws MiddlewareQueryException, RuleException {

		this.setupTestExecutionContext();
		Mockito.when(this.germplasmDataManager.checkIfMatches(Matchers.anyString())).thenReturn(true);
		Mockito.when(this.source.isForceUniqueNameGeneration()).thenReturn(true);
		this.dut.runRule(this.context);

		// verify that current rule execution state is pointed back to previous stored data
		Mockito.verify(this.context).setCurrentData(this.tempData);
		Mockito.verify(this.source).setChangeDetail(Matchers.any(AdvanceGermplasmChangeDetail.class));

		// verify that max sequence is incremented
		Mockito.verify(this.source).setCurrentMaxSequence(EnforceUniqueNameRuleTest.TEST_MAX_SEQUENCE + 1);

	}

	@Test
	public void testUniqueNameCheckMatchFoundIsBulking() throws MiddlewareQueryException, RuleException {

		this.setupTestExecutionContext();
		Mockito.when(this.germplasmDataManager.checkIfMatches(Matchers.anyString())).thenReturn(true);

		this.dut.runRule(this.context);

		// verify that current rule execution state is pointed back to previous stored data
		Mockito.verify(this.context).setCurrentData(this.tempData);
		Mockito.verify(this.source).setChangeDetail(Matchers.any(AdvanceGermplasmChangeDetail.class));

		// verify that force unique name generation flag is set
		Mockito.verify(this.source).setForceUniqueNameGeneration(true);
	}

	@Test
	public void testUniqueNameCheckMatchFoundHasCountNonBulking() throws MiddlewareQueryException, RuleException {

		this.setupTestExecutionContext();
		Mockito.when(this.germplasmDataManager.checkIfMatches(Matchers.anyString())).thenReturn(true);
		Mockito.when(this.breedingMethod.getCount()).thenReturn(CountRule.DEFAULT_COUNT);

		this.dut.runRule(this.context);

		// verify that max sequence is incremented
		Mockito.verify(this.source).setCurrentMaxSequence(EnforceUniqueNameRuleTest.TEST_MAX_SEQUENCE + 1);

		// verify that current rule execution state is pointed back to previous stored data
		Mockito.verify(this.context).setCurrentData(this.tempData);
		Mockito.verify(this.source).setChangeDetail(Matchers.any(AdvanceGermplasmChangeDetail.class));

		// verify that flag is not set so as to preserve previous count rule logic when incrementing the count
		Mockito.verify(this.source, Mockito.never()).setForceUniqueNameGeneration(true);
	}

	@Test
	public void testGetNextStepKeyNoMatchFound() throws Exception {
		this.setupTestExecutionContext();

		// when no duplicate is found, a change detail object is not created in the advancing source. we simulate that state here
		Mockito.when(this.source.getChangeDetail()).thenReturn(null);

		String nextKey = this.dut.getNextRuleStepKey(this.context);

		Assert.assertNull("Expected next key is null because unique name check is last in sequence and unique name check should pass",
				nextKey);
	}

	@Test
	public void testGetNextStepKeyDuplicateFoundCheckFail() throws Exception {
		this.setupTestExecutionContext();
		AdvanceGermplasmChangeDetail detail = Mockito.mock(AdvanceGermplasmChangeDetail.class);

		Mockito.when(this.source.getChangeDetail()).thenReturn(detail);
		// if a duplicate has been found in previous steps, and a passing name has not yet been found, then the new advance name should
		// still be null on the germplasm change detail object
		Mockito.when(detail.getNewAdvanceName()).thenReturn(null);

		String nextKey = this.dut.getNextRuleStepKey(this.context);

		Assert.assertNotNull("Duplicate has been found and check still fails, so next key should not be null", nextKey);
		Assert.assertEquals("Rule does not pass execution control to CountRule even after failing the check", CountRule.KEY, nextKey);
	}

	@Test
	public void testGetNextStepKeyDuplicateFoundCheckPass() throws Exception {
		this.setupTestExecutionContext();
		AdvanceGermplasmChangeDetail detail = Mockito.mock(AdvanceGermplasmChangeDetail.class);

		Mockito.when(this.source.getChangeDetail()).thenReturn(detail);
		// if a duplicate has been found in previous steps, and a passing name has been found, then the new advance name should not be null
		Mockito.when(detail.getNewAdvanceName()).thenReturn(new String());

		String nextKey = this.dut.getNextRuleStepKey(this.context);

		Assert.assertNull("Duplicate has been found and but check passes, so next key should be null", nextKey);

	}

	protected void setupTestExecutionContext() {
		List<String> dummySequenceOrder = new ArrayList<>();
		dummySequenceOrder.add(CountRule.KEY);
		dummySequenceOrder.add(EnforceUniqueNameRule.KEY);

		List<String> dummyInitialState = new ArrayList<>();
		dummyInitialState.add("ETFN 1-1");
		dummyInitialState.add("ETFN 1-2");

		this.tempData = new ArrayList<>();
		this.tempData.add("ETFN 1-");

		Mockito.when(this.context.getAdvancingSource()).thenReturn(this.source);
		Mockito.when(this.context.getGermplasmDataManager()).thenReturn(this.germplasmDataManager);
		Mockito.when(this.context.getExecutionOrder()).thenReturn(dummySequenceOrder);
		Mockito.when(this.context.getCurrentData()).thenReturn(dummyInitialState);
		Mockito.when(this.context.getTempData()).thenReturn(this.tempData);
		Mockito.when(this.context.getMessageSource()).thenReturn(Mockito.mock(MessageSource.class));

		Mockito.when(this.source.getBreedingMethod()).thenReturn(this.breedingMethod);
		Mockito.when(this.source.getCurrentMaxSequence()).thenReturn(EnforceUniqueNameRuleTest.TEST_MAX_SEQUENCE);

		Mockito.when(this.context.getCurrentExecutionIndex()).thenReturn(EnforceUniqueNameRuleTest.ENFORCE_RULE_INDEX);

	}
}
