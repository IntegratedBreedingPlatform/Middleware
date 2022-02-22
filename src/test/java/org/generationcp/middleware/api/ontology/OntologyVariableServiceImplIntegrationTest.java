package org.generationcp.middleware.api.ontology;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OntologyVariableServiceImplIntegrationTest extends IntegrationTestBase {

	public static final String BLUES = "BLUEs";
	public static final String BLUPS = "BLUPs";
	public static final String HERITABILITY = "Heritability";
	public static final String PVALUE = "PValue";
	public static final String CV = "CV";
	private DaoFactory daoFactory;

	@Autowired
	private OntologyVariableService ontologyVariableService;

	private String variableName;
	private String variableDescription;
	private String variableProperty;
	private String variableScale;
	private String variableMethod;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.variableName = RandomStringUtils.randomAlphanumeric(10);
		this.variableDescription = RandomStringUtils.randomAlphanumeric(10);
		this.variableProperty = RandomStringUtils.randomAlphanumeric(10);
		this.variableScale = RandomStringUtils.randomAlphanumeric(10);
		this.variableMethod = RandomStringUtils.randomAlphanumeric(10);
	}

	@Test
	public void testCreateAnalysisVariablesForMeans() {
		final Variable traitVariable =
			this.createTestVariable();
		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		final List<Variable> result =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS.getName());

		assertEquals(2, result.size());
		this.assertAnalysisVariable(traitVariable, result, BLUES);
		this.assertAnalysisVariable(traitVariable, result, BLUPS);
	}

	@Test
	public void testCreateAnalysisVariablesForMeans_AnalysisVariablesAlreadyPresent() {
		final Variable traitVariable =
			this.createTestVariable();
		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		final List<Variable> result =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS.getName());
		assertEquals(2, result.size());

		// Create analysis variables again
		final List<Variable> result2 =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS.getName());
		assertEquals(2, result2.size());
		this.assertAnalysisVariable(traitVariable, result2, BLUES);
		this.assertAnalysisVariable(traitVariable, result2, BLUPS);
	}

	@Test
	public void testCreateAnalysisVariablesForSummary() {
		final Variable traitVariable = this.createTestVariable();
		final List<String> analysisNames = Arrays.asList(HERITABILITY, PVALUE, CV);
		final List<Variable> result =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS_SUMMARY.getName());
		assertEquals(3, result.size());
		this.assertAnalysisVariable(traitVariable, result, HERITABILITY);
		this.assertAnalysisVariable(traitVariable, result, PVALUE);
		this.assertAnalysisVariable(traitVariable, result, CV);

	}

	@Test
	public void testCreateAnalysisVariablesForSummary_AnalysisVariablesAlreadyPresent() {
		final Variable traitVariable = this.createTestVariable();
		final List<String> analysisNames = Arrays.asList(HERITABILITY, PVALUE, CV);
		final List<Variable> result =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS_SUMMARY.getName());
		assertEquals(3, result.size());

		// Create analysis variables again
		final List<Variable> result2 =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS_SUMMARY.getName());
		assertEquals(3, result2.size());
		this.assertAnalysisVariable(traitVariable, result2, HERITABILITY);
		this.assertAnalysisVariable(traitVariable, result2, PVALUE);
		this.assertAnalysisVariable(traitVariable, result2, CV);
	}

	@Test
	public void testCreateAnalysisVariables_MultipleVariables() {
		final Variable traitVariable1 = this.createTestVariable();
		final Variable traitVariable2 = this.createTestVariable();
		final Variable traitVariable3 = this.createTestVariable();

		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		final List<Variable> result =
			this.ontologyVariableService.createAnalysisVariables(
				Arrays.asList(traitVariable1.getId(), traitVariable2.getId(), traitVariable3.getId()),
				analysisNames,
				VariableType.ANALYSIS.getName());

		assertEquals(6, result.size());
	}

	private Variable createTestVariable() {
		// Create traitVariable

		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), CvId.VARIABLES);
		final CVTerm property = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		final CVTerm method = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());
		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());
		// Assign traitVariable type
		this.daoFactory.getCvTermPropertyDao()
			.save(cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.TRAIT.getName(), 0);
		final Variable variable = new Variable();

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(cvTermVariable.getCvTermId());
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter).values().stream().findFirst().get();

	}

	private void assertAnalysisVariable(final Variable trait, final List<Variable> result, final String analysisName) {
		final Optional<Variable> variable = result.stream().filter(v -> v.getMethod().getName().equalsIgnoreCase(analysisName)).findAny();
		assertTrue(variable.isPresent());
		assertTrue(variable.get().getName().equalsIgnoreCase(trait.getName() + "_" + analysisName));
		assertEquals(trait.getDefinition(), variable.get().getDefinition());
		assertEquals(trait.getProperty().getName(), variable.get().getProperty().getName());
		assertEquals(trait.getScale().getName(), variable.get().getScale().getName());
		assertTrue(variable.get().getMethod().getName().equalsIgnoreCase(analysisName));
	}

}
