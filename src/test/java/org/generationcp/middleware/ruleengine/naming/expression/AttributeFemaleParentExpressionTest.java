package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class AttributeFemaleParentExpressionTest extends TestExpression {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmService germplasmService;

	@InjectMocks
	AttributeFemaleParentExpression expression = new AttributeFemaleParentExpression();

	private static final Integer VARIABLE_ID = 2000;
	private static final String PREFIX = "[ATTRFP.2000]";
	private static final String COUNT = "[SEQUENCE]";
	private static final String DESIGNATION = "(AA/ABC)";

	@Test
	public void testAttributeAsPrefixDerivativeMethod() {

		final int femaleParentGidOfGroupSource = 103;
		final BasicGermplasmDTO groupSource = new BasicGermplasmDTO();
		groupSource.setGpid1(femaleParentGidOfGroupSource);

		Mockito.when(this.germplasmDataManager.getAttributeValue(femaleParentGidOfGroupSource, VARIABLE_ID)).thenReturn("Mexico");
		Mockito.when(this.germplasmService.getBasicGermplasmByGids(ArgumentMatchers.anySet())).thenReturn(Arrays.asList(groupSource));

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);

		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 104, 105, -1, derivativeMethod.getMid());
		final AdvancingSource source =
				this.createAdvancingSourceTestData(originGermplasm, new Method(), derivativeMethod, DESIGNATION, "Dry", 2);

		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixWithoutAttributeValueDerivativeMethod() {

		final int femaleParentGidOfGroupSource = 103;
		final BasicGermplasmDTO groupSource = new BasicGermplasmDTO();
		groupSource.setGpid1(femaleParentGidOfGroupSource);

		Mockito.when(this.germplasmDataManager.getAttributeValue(femaleParentGidOfGroupSource, VARIABLE_ID)).thenReturn("");
		Mockito.when(this.germplasmService.getBasicGermplasmByGids(ArgumentMatchers.anySet())).thenReturn(Arrays.asList(groupSource));

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 104, 105, -1, derivativeMethod.getMid());
		final AdvancingSource source =
				this.createAdvancingSourceTestData(originGermplasm, new Method(), derivativeMethod, DESIGNATION, "Dry", 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGpid1IsUnknownDerivativeMethod() {

		Mockito.when(this.germplasmDataManager.getAttributeValue(null, VARIABLE_ID)).thenReturn("");
		Mockito.when(this.germplasmService.getBasicGermplasmByGids(ArgumentMatchers.anySet())).thenReturn(null);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm = this.createBasicGermplasmDTO(0, 0, 0, -1, derivativeMethod.getMid());
		AdvancingSource
			source = this.createAdvancingSourceTestData(originGermplasm, new Method(), derivativeMethod, DESIGNATION, "Dry", 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixFemaleParentOfGroupSourceIsUnknownDerivativeMethod() {

		Mockito.when(this.germplasmDataManager.getAttributeValue(null, VARIABLE_ID)).thenReturn("");

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm = this.createBasicGermplasmDTO(0, 0, 0, -1, derivativeMethod.getMid());
		AdvancingSource
			source = this.createAdvancingSourceTestData(originGermplasm, new Method(), derivativeMethod, DESIGNATION, "Dry", 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixDerivativeMethodWithUnknownSourceGpid1andGpid2() {

		final BasicGermplasmDTO groupSource = new BasicGermplasmDTO();
		groupSource.setGpid1(0);
		groupSource.setGpid2(0);

		Mockito.when(this.germplasmDataManager.getAttributeValue(groupSource.getGpid1(), VARIABLE_ID)).thenReturn("");
		Mockito.when(this.germplasmService.getBasicGermplasmByGids(ArgumentMatchers.anySet())).thenReturn(Arrays.asList(groupSource));

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 0, 0, -1, derivativeMethod.getMid());
		AdvancingSource
			source = this.createAdvancingSourceTestData(originGermplasm, new Method(), derivativeMethod, DESIGNATION, "Dry", 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixDerivativeMethodWithSourceGermplasmIsGenerative() {

		final BasicGermplasmDTO groupSource = new BasicGermplasmDTO();
		groupSource.setGpid1(1002);
		groupSource.setGpid2(1003);

		Mockito.when(this.germplasmDataManager.getAttributeValue(groupSource.getGpid1(), VARIABLE_ID)).thenReturn("Mexico");
		Mockito.when(this.germplasmService.getBasicGermplasmByGids(ArgumentMatchers.anySet())).thenReturn(Arrays.asList(groupSource));

		final Method originGermplasmMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 0, 0, -1, derivativeMethod.getMid());
		AdvancingSource
			source = this.createAdvancingSourceTestData(originGermplasm, originGermplasmMethod, derivativeMethod, DESIGNATION, "Dry", 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGenerativeMethod() {

		Mockito.when(germplasmDataManager.getAttributeValue(104, VARIABLE_ID)).thenReturn("Mexico");

		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 104, 105, -1, generativeMethod.getMid());
		final AdvancingSource source =
				this.createAdvancingSourceTestData(originGermplasm, new Method(), generativeMethod, DESIGNATION, "Dry", 2);
		source.setFemaleGid(104);

		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixWithoutAttributeValueGenerativeMethod() {
		Mockito.when(germplasmDataManager.getAttributeValue(104, VARIABLE_ID)).thenReturn("");
		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 104, 105, -1, generativeMethod.getMid());
		final AdvancingSource source =
				this.createAdvancingSourceTestData(originGermplasm, new Method(), generativeMethod, DESIGNATION, "Dry", 2);

		source.setFemaleGid(104);

		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGpid1UnknownGenerativeMethod() {

		Mockito.when(germplasmDataManager.getAttributeValue(0, VARIABLE_ID)).thenReturn("");
		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final BasicGermplasmDTO originGermplasm =
				this.createBasicGermplasmDTO(1000, 0, 0, -1, generativeMethod.getMid());
		AdvancingSource
			source = this.createAdvancingSourceTestData(originGermplasm, new Method(), generativeMethod, DESIGNATION, "Dry", 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

}
