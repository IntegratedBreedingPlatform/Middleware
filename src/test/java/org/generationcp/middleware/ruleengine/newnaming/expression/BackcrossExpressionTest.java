package org.generationcp.middleware.ruleengine.newnaming.expression;

import junit.framework.Assert;
import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BackcrossExpressionTest extends org.generationcp.middleware.ruleengine.newnaming.expression.TestExpression {

	private static final String DESIGNATION = "GERMPLASM_TEST";

	@Mock
	private PedigreeDataManager pedigreeDataManager;

	@InjectMocks
	private BackcrossExpression expression = new BackcrossExpression();

	@Test
	public void testResolveBackcrossParentFemale() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.FEMALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, "-", null, "[BC]", null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-F", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentMale() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.MALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, "-", null, "[BC]", null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-M", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentMaleWithLiteralStringBeforeAndAfterTheProcessCode() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.MALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, "-", null, "AA[BC]CC", null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-AAMCC", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentWithNoRecurringParent() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid)).thenReturn(PedigreeDataManagerImpl.NONE);

		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, "-", null, "[BC]", null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-", values.get(0).toString());

	}


}
