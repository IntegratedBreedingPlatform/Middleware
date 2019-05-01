package org.generationcp.middleware.service.impl.derived_variables;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DerivedVariableServiceImplTest {

	public static final int VARIABLE1_TERMID = 123;
	public static final int VARIABLE2_TERMID = 456;
	public static final int VARIABLE3_TERMID = 789;
	public static final int VARIABLE4_TERMID = 999;

	@Mock
	private FormulaService formulaService;

	@Mock
	private DatasetService datasetService;

	@Mock
	private PhenotypeDao phenotypeDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private DaoFactory factory;

	@InjectMocks
	private final DerivedVariableServiceImpl derivedVariableService = new DerivedVariableServiceImpl();

	private final Random random = new Random();

	@Before
	public void setup() {
		this.derivedVariableService.setDaoFactory(this.factory);
		when(this.factory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.factory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
	}

	@Test
	public void testDependencyVariablesInputVariablesAreNotPresent() {

		final List<MeasurementVariable> traits = new ArrayList<>();
		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Only add variables that are not formula/input variables.
		traits.add(trait1);
		traits.add(trait2);

		final Set<FormulaVariable> formulaVariables = this.createFormulaVariables();

		final int datasetId = this.random.nextInt(10);
		when(this.datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId()))).thenReturn(traits);
		when(this.formulaService.getAllFormulaVariables(new HashSet<Integer>(Arrays.asList(VARIABLE1_TERMID, VARIABLE2_TERMID))))
			.thenReturn(formulaVariables);

		final Set<String> dependencies = this.derivedVariableService.getDependencyVariables(datasetId);

		assertEquals(formulaVariables.size(), dependencies.size());
		for (final FormulaVariable formulaVariable : formulaVariables) {
			dependencies.contains(formulaVariable.getName());
		}

	}

	@Test
	public void testDependencyVariablesInputVariablesArePresent() {

		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		final MeasurementVariable trait3 = new MeasurementVariable();
		final MeasurementVariable trait4 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Add the formula/input variables to the list of available variables in a dataset
		trait3.setTermId(VARIABLE3_TERMID);
		trait4.setTermId(VARIABLE4_TERMID);

		final int datasetId = this.random.nextInt(10);
		when(this.datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId())))
			.thenReturn(Arrays.asList(trait1, trait2, trait3, trait4));
		when(this.formulaService.getAllFormulaVariables(
			new HashSet<Integer>(Arrays.asList(VARIABLE1_TERMID, VARIABLE2_TERMID, VARIABLE3_TERMID, VARIABLE4_TERMID))))
			.thenReturn(this.createFormulaVariables());

		final Set<String> dependencies = this.derivedVariableService.getDependencyVariables(datasetId);

		assertTrue(dependencies.isEmpty());

	}

	@Test
	public void testDependencyVariablesForSpecificTraitInputVariablesAreNotPresent() {

		final List<MeasurementVariable> traits = new ArrayList<>();
		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Only add variables that are not formula/input variables.
		traits.add(trait1);
		traits.add(trait2);

		final Set<FormulaVariable> formulaVariables = this.createFormulaVariables();

		final int datasetId = this.random.nextInt(10);
		when(this.datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId()))).thenReturn(traits);
		when(this.formulaService.getAllFormulaVariables(new HashSet<Integer>(Arrays.asList(VARIABLE1_TERMID))))
			.thenReturn(formulaVariables);

		final Set<String> dependencies = this.derivedVariableService.getDependencyVariables(datasetId, VARIABLE1_TERMID);

		assertEquals(formulaVariables.size(), dependencies.size());
		for (final FormulaVariable formulaVariable : formulaVariables) {
			dependencies.contains(formulaVariable.getName());
		}

	}

	@Test
	public void testDependencyVariablesForSpecificTraitInputVariablesArePresent() {

		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		final MeasurementVariable trait3 = new MeasurementVariable();
		final MeasurementVariable trait4 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Add the formula/input variables to the list of available variables in a dataset
		trait3.setTermId(VARIABLE3_TERMID);
		trait4.setTermId(VARIABLE4_TERMID);

		final int datasetId = this.random.nextInt(10);
		when(this.datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId())))
			.thenReturn(Arrays.asList(trait1, trait2, trait3, trait4));
		when(this.formulaService.getAllFormulaVariables(
			new HashSet<Integer>(Arrays.asList(VARIABLE1_TERMID))))
			.thenReturn(this.createFormulaVariables());

		final Set<String> dependencies = this.derivedVariableService.getDependencyVariables(datasetId, VARIABLE1_TERMID);

		assertTrue(dependencies.isEmpty());

	}

	@Test
	public void testCountCalculatedVariablesInDatasets() {

		final int expectedCount = this.random.nextInt();
		final Set<Integer> datasetIds = new HashSet<Integer>(Arrays.asList(1));
		when(this.dmsProjectDao.countCalculatedVariablesInDatasets(datasetIds)).thenReturn(expectedCount);
		assertEquals(expectedCount, this.derivedVariableService.countCalculatedVariablesInDatasets(datasetIds));

	}

	@Test
	@Ignore // FIXME IBP-2634
	public void testSaveCalculatedResultUpdatePhenotype() {

		final int variableTermId = this.random.nextInt(10);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermId);

		final String value = RandomStringUtils.random(10);
		final Integer categoricalId = this.random.nextInt(10);
		final Integer observationUnitId = this.random.nextInt(10);
		final Integer observationId = this.random.nextInt(10);

		final Phenotype existingPhenotype = new Phenotype();
		existingPhenotype.setObservableId(observationId);
		when(this.phenotypeDao.getById(observationId)).thenReturn(existingPhenotype);

		this.derivedVariableService.saveCalculatedResult(value, categoricalId, observationUnitId, observationId, measurementVariable);

		verify(this.phenotypeDao).update(existingPhenotype);
		verify(this.datasetService).updateDependentPhenotypesStatus(variableTermId, observationUnitId);
		assertEquals(value, existingPhenotype.getValue());
		assertEquals(categoricalId, existingPhenotype.getcValueId());
		assertTrue(existingPhenotype.isChanged());
		assertNull(existingPhenotype.getValueStatus());

	}

	@Test
	public void testSaveCalculatedResultCreatePhenotype() {

		final int variableTermId = this.random.nextInt(10);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermId);

		final String value = RandomStringUtils.random(10);
		final Integer categoricalId = this.random.nextInt(10);
		final Integer observationUnitId = this.random.nextInt(10);

		// When observationId is null, it means there's no phenotype existing yet.
		final Integer observationId = null;

		this.derivedVariableService.saveCalculatedResult(value, categoricalId, observationUnitId, observationId, measurementVariable);

		final ArgumentCaptor<Phenotype> captor = ArgumentCaptor.forClass(Phenotype.class);
		verify(this.phenotypeDao).save(captor.capture());
		verify(this.datasetService).updateDependentPhenotypesStatus(variableTermId, observationUnitId);

		final Phenotype phenotypeToBeSaved = captor.getValue();
		assertNotNull(phenotypeToBeSaved.getCreatedDate());
		assertNotNull(phenotypeToBeSaved.getUpdatedDate());
		assertEquals(categoricalId, phenotypeToBeSaved.getcValueId());
		assertEquals(variableTermId, phenotypeToBeSaved.getObservableId().intValue());
		assertEquals(value, phenotypeToBeSaved.getValue());
		assertEquals(observationUnitId, phenotypeToBeSaved.getExperiment().getNdExperimentId());
		assertEquals(String.valueOf(variableTermId), phenotypeToBeSaved.getName());

	}

	private Set<FormulaVariable> createFormulaVariables() {

		final Set<FormulaVariable> formulaVariables = new HashSet<>();

		final FormulaVariable formulaVariable1 = new FormulaVariable();
		formulaVariable1.setId(VARIABLE3_TERMID);
		formulaVariable1.setName("VARIABLE3");
		formulaVariable1.setTargetTermId(VARIABLE1_TERMID);

		final FormulaVariable formulaVariable2 = new FormulaVariable();
		formulaVariable2.setId(VARIABLE4_TERMID);
		formulaVariable2.setName("VARIABLE4");
		formulaVariable2.setTargetTermId(VARIABLE2_TERMID);

		formulaVariables.add(formulaVariable1);
		formulaVariables.add(formulaVariable2);

		return formulaVariables;

	}

}
