package org.generationcp.middleware.api.ontology;

import com.google.common.collect.Multimap;
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
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateAnalysisVariablesForMeans() {
		final Variable traitVariable =
			this.createTestVariable("testVariable");
		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS.getName());

		assertEquals(2, creatednAnalysisVariableIds.size());

		final VariableFilter variableFilter = new VariableFilter();
		creatednAnalysisVariableIds.stream().forEach(variableFilter::addVariableId);
		final List<Variable> result = this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().stream().collect(
			Collectors.toList());

		this.assertAnalysisVariable(traitVariable, result, BLUES);
		this.assertAnalysisVariable(traitVariable, result, BLUPS);
	}

	@Test
	public void testCreateAnalysisVariablesForMeans_AnalysisVariablesAlreadyPresent() {
		final Variable traitVariable =
			this.createTestVariable("testVariable");
		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
			VariableType.ANALYSIS.getName());

		// Create analysis variables again
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS.getName());

		final VariableFilter variableFilter = new VariableFilter();
		creatednAnalysisVariableIds.stream().forEach(variableFilter::addVariableId);
		final List<Variable> result = this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().stream().collect(
			Collectors.toList());

		assertEquals(2, result.size());
		this.assertAnalysisVariable(traitVariable, result, BLUES);
		this.assertAnalysisVariable(traitVariable, result, BLUPS);
	}

	@Test
	public void testCreateAnalysisVariablesForSummary() {
		final Variable traitVariable = this.createTestVariable("testVariable");
		final List<String> analysisNames = Arrays.asList(HERITABILITY, PVALUE, CV);
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS_SUMMARY.getName());

		final VariableFilter variableFilter = new VariableFilter();
		creatednAnalysisVariableIds.stream().forEach(variableFilter::addVariableId);
		final List<Variable> result = this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().stream().collect(
			Collectors.toList());

		assertEquals(3, result.size());
		this.assertAnalysisVariable(traitVariable, result, HERITABILITY);
		this.assertAnalysisVariable(traitVariable, result, PVALUE);
		this.assertAnalysisVariable(traitVariable, result, CV);

	}

	@Test
	public void testCreateAnalysisVariablesForSummary_AnalysisVariablesAlreadyPresent() {
		final Variable traitVariable = this.createTestVariable("testVariable");
		final List<String> analysisNames = Arrays.asList(HERITABILITY, PVALUE, CV);
		this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
			VariableType.ANALYSIS_SUMMARY.getName());

		// Create analysis variables again
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(Arrays.asList(traitVariable.getId()), analysisNames,
				VariableType.ANALYSIS_SUMMARY.getName());

		final VariableFilter variableFilter = new VariableFilter();
		creatednAnalysisVariableIds.stream().forEach(variableFilter::addVariableId);
		final List<Variable> result = this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().stream().collect(
			Collectors.toList());

		assertEquals(3, result.size());
		this.assertAnalysisVariable(traitVariable, result, HERITABILITY);
		this.assertAnalysisVariable(traitVariable, result, PVALUE);
		this.assertAnalysisVariable(traitVariable, result, CV);
	}

	@Test
	public void testCreateAnalysisVariables_MultipleVariables() {
		final Variable traitVariable1 = this.createTestVariable("testVariable1");
		final Variable traitVariable2 = this.createTestVariable("testVariable2");
		final Variable traitVariable3 = this.createTestVariable("testVariable3");

		final List<String> analysisNames = Arrays.asList(BLUES, BLUPS);
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(
				Arrays.asList(traitVariable1.getId(), traitVariable2.getId(), traitVariable3.getId()),
				analysisNames,
				VariableType.ANALYSIS.getName());

		assertEquals(6, creatednAnalysisVariableIds.size());
	}

	@Test
	public void testCreateAnalysisVariables_GeneratedVariableNameIsUsedByExistingVariables() {
		// Create existing variable with trait name + analysis name pattern
		final Variable existingVariable = this.createTestVariable("testVariable_BLUEs");
		final Variable traitVariable = this.createTestVariable("testVariable");

		final List<String> analysisNames = Arrays.asList(BLUES);
		final List<Integer> creatednAnalysisVariableIds =
			this.ontologyVariableService.createAnalysisVariables(
				Arrays.asList(traitVariable.getId()),
				analysisNames,
				VariableType.ANALYSIS.getName());

		assertEquals(1, creatednAnalysisVariableIds.size());

		final VariableFilter variableFilter = new VariableFilter();
		creatednAnalysisVariableIds.stream().forEach(variableFilter::addVariableId);
		final List<Variable> result = this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().stream().collect(
			Collectors.toList());

		// Newly created analysis variable should have _1 suffix
		assertEquals(traitVariable.getName() + "_" + BLUES + "_1", result.get(0).getName());

	}

	@Test
	public void testGetVariableTypesOfVariables() {
		final Variable traitVariable1 = this.createTestVariable("testVariable1");
		final Variable traitVariable2 = this.createTestVariable("testVariable2");
		final Variable traitVariable3 = this.createTestVariable("testVariable3");

		final Multimap<Integer, VariableType> result = this.ontologyVariableService.getVariableTypesOfVariables(
			Arrays.asList(traitVariable1.getId(), traitVariable2.getId(), traitVariable3.getId()));

		assertTrue(result.get(traitVariable1.getId()).contains(VariableType.TRAIT));
		assertTrue(result.get(traitVariable1.getId()).contains(VariableType.SELECTION_METHOD));
		assertTrue(result.get(traitVariable2.getId()).contains(VariableType.TRAIT));
		assertTrue(result.get(traitVariable2.getId()).contains(VariableType.SELECTION_METHOD));
		assertTrue(result.get(traitVariable3.getId()).contains(VariableType.TRAIT));
		assertTrue(result.get(traitVariable3.getId()).contains(VariableType.SELECTION_METHOD));
	}

	private Variable createTestVariable(final String variableName) {
		// Create traitVariable
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(variableName, RandomStringUtils.randomAlphanumeric(10), CvId.VARIABLES);
		final CVTerm property = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		this.daoFactory.getCvTermRelationshipDao().save(scale.getCvTermId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());
		final CVTerm method = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());

		// Assign TRAIT and SELECTION_METHOD Variable types
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.TRAIT.getName(), 0));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(),
				VariableType.SELECTION_METHOD.getName(), 0));

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
		assertEquals(trait.getScale().getDataType(), variable.get().getScale().getDataType());
		assertTrue(variable.get().getMethod().getName().equalsIgnoreCase(analysisName));
	}

}
