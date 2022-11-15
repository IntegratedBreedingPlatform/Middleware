package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class AttributeFemaleParentExpressionTest extends TestExpression {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	AttributeFemaleParentExpression expression = new AttributeFemaleParentExpression();

	private static final Integer VARIABLE_ID = 2000;

	private static final String PREFIX = "[ATTRFP.2000]";

	private static final String COUNT = "[SEQUENCE]";

	@Test
	public void testAttributeAsPrefixDerivativeMethod() throws Exception {

		final Germplasm groupSource = new Germplasm();
		final int femaleParentGidOfGroupSource = 103;
		groupSource.setGpid1(femaleParentGidOfGroupSource);

		Mockito.when(germplasmDataManager.getAttributeValue(femaleParentGidOfGroupSource, VARIABLE_ID)).thenReturn("Mexico");
		Mockito.when(germplasmDataManager.getGermplasmByGID(104)).thenReturn(groupSource);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);

		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 105, -1, derivativeMethod.getMid());
		final DeprecatedAdvancingSource source =
				this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");

		final List<StringBuilder> values = this.createInitialValues(source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixWithoutAttributeValueDerivativeMethod() throws Exception {

		final Germplasm groupSource = new Germplasm();
		final int femaleParentGidOfGroupSource = 103;
		groupSource.setGpid1(femaleParentGidOfGroupSource);

		Mockito.when(germplasmDataManager.getAttributeValue(femaleParentGidOfGroupSource, VARIABLE_ID)).thenReturn("");
		Mockito.when(germplasmDataManager.getGermplasmByGID(104)).thenReturn(groupSource);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 105, -1, derivativeMethod.getMid());
		final DeprecatedAdvancingSource source =
				this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		final List<StringBuilder> values = this.createInitialValues(source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGpid1IsUnknownDerivativeMethod() throws Exception {

		Mockito.when(germplasmDataManager.getAttributeValue(null, VARIABLE_ID)).thenReturn("");
		Mockito.when(germplasmDataManager.getGermplasmByGID(0)).thenReturn(null);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm = this.createImportedGermplasm(1, "(AA/ABC)", "0", 0, 0, -1, derivativeMethod.getMid());
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixFemaleParentOfGroupSourceIsUnknownDerivativeMethod() throws Exception {

		final Germplasm groupSource = new Germplasm();
		final int femaleParentGidOfGroupSource = 0;
		groupSource.setGpid1(femaleParentGidOfGroupSource);

		Mockito.when(germplasmDataManager.getAttributeValue(null, VARIABLE_ID)).thenReturn("");

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm = this.createImportedGermplasm(1, "(AA/ABC)", "0", 0, 0, -1, derivativeMethod.getMid());
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixDerivativeMethodWithUnknownSourceGpid1andGpid2() throws Exception {

		final Germplasm groupSource = new Germplasm();
		groupSource.setGpid1(0);
		groupSource.setGpid2(0);

		Mockito.when(germplasmDataManager.getAttributeValue(groupSource.getGpid1(), VARIABLE_ID)).thenReturn("");
		Mockito.when(germplasmDataManager.getGermplasmByGID(1000)).thenReturn(groupSource);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 0, 0, -1, derivativeMethod.getMid());
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixDerivativeMethodWithSourceGermplasmIsGenerative() throws Exception {

		final Germplasm groupSource = new Germplasm();
		groupSource.setGpid1(1002);
		groupSource.setGpid2(1003);

		Mockito.when(germplasmDataManager.getAttributeValue(groupSource.getGpid1(), VARIABLE_ID)).thenReturn("Mexico");
		Mockito.when(germplasmDataManager.getGermplasmByGID(1000)).thenReturn(groupSource);

		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 0, 0, -1, derivativeMethod.getMid());
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		source.setSourceMethod(this.createGenerativeMethod(PREFIX, COUNT, null, "-", true));
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGenerativeMethod() throws Exception {

		Mockito.when(germplasmDataManager.getAttributeValue(104, VARIABLE_ID)).thenReturn("Mexico");

		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 105, -1, generativeMethod.getMid());
		final DeprecatedAdvancingSource source =
				this.createAdvancingSourceTestData(generativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");

		source.setFemaleGid(104);

		final List<StringBuilder> values = this.createInitialValues(source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-Mexico[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixWithoutAttributeValueGenerativeMethod() throws Exception {
		Mockito.when(germplasmDataManager.getAttributeValue(104, VARIABLE_ID)).thenReturn("");
		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 105, -1, generativeMethod.getMid());
		final DeprecatedAdvancingSource source =
				this.createAdvancingSourceTestData(generativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");

		source.setFemaleGid(104);

		final List<StringBuilder> values = this.createInitialValues(source);

		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGpid1UnknownGenerativeMethod() throws Exception {

		Mockito.when(germplasmDataManager.getAttributeValue(0, VARIABLE_ID)).thenReturn("");
		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
				this.createImportedGermplasm(1, "(AA/ABC)", "1000", 0, 0, -1, generativeMethod.getMid());
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData(generativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);

		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

}
