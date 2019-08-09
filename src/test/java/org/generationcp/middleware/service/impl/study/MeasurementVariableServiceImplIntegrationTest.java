package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * The class <code>MeasurementVariableServiceImplIntegrationTest</code> contains tests for the class <code>{@link MeasurementVariableServiceImpl}</code>.
 */
public class MeasurementVariableServiceImplIntegrationTest extends IntegrationTestBase {

	private DmsProjectDao dmsProjectDao;
	private MeasurementVariableService measurementVariableService;
	private IntegrationTestDataInitializer testDataInitializer;

	private DmsProject study;
	private DmsProject plot;

	@Before
	public void setUp() {

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.measurementVariableService = new MeasurementVariableServiceImpl(this.sessionProvder.getSession());
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
	}

	@Test
	public void testGetVariables() {

		final String trait1Name = "Trait1";
		final String trait2Name = "Trait2";
		final CVTerm trait1 = this.testDataInitializer.createTrait(trait1Name);
		final CVTerm trait2 = this.testDataInitializer.createTrait(trait2Name);

		this.testDataInitializer.addProjectProp(this.plot, trait1.getCvTermId(), trait1Name, VariableType.TRAIT, "", 1);
		this.testDataInitializer.addProjectProp(this.plot, trait2.getCvTermId(), trait2Name, VariableType.TRAIT, "", 2);

		final List<MeasurementVariableDto> returnedTraits =
			this.measurementVariableService.getVariables(this.study.getProjectId(), VariableType.TRAIT.getId());

		Assert.assertEquals(2, returnedTraits.size());
		Assert.assertEquals(trait1.getCvTermId(), returnedTraits.get(0).getId());
		Assert.assertEquals(trait1Name, returnedTraits.get(0).getName());
		Assert.assertEquals(trait2.getCvTermId(), returnedTraits.get(1).getId());
		Assert.assertEquals(trait2Name, returnedTraits.get(1).getName());

	}

	@Test
	public void testGetVariablesForDataset() {

		final String variable1Name = "TraitVariable";
		final String variable2Name = "DesignVariable";
		final CVTerm trait1 = this.testDataInitializer.createTrait(variable1Name);
		final CVTerm trait2 = this.testDataInitializer.createTrait(variable2Name);

		this.testDataInitializer.addProjectProp(this.plot, trait1.getCvTermId(), variable1Name, VariableType.TRAIT, "", 1 );
		this.testDataInitializer.addProjectProp(this.plot, trait2.getCvTermId(), variable2Name, VariableType.EXPERIMENTAL_DESIGN, "", 2);

		final List<MeasurementVariableDto> returnedVariablesFromPlotDataset = this.measurementVariableService
			.getVariablesForDataset(this.plot.getProjectId(), VariableType.TRAIT.getId(), VariableType.EXPERIMENTAL_DESIGN.getId());

		Assert.assertEquals(2, returnedVariablesFromPlotDataset.size());
		Assert.assertEquals(trait1.getCvTermId(), returnedVariablesFromPlotDataset.get(0).getId());
		Assert.assertEquals(variable1Name, returnedVariablesFromPlotDataset.get(0).getName());
		Assert.assertEquals(trait2.getCvTermId(), returnedVariablesFromPlotDataset.get(1).getId());
		Assert.assertEquals(variable2Name, returnedVariablesFromPlotDataset.get(1).getName());

		final List<MeasurementVariableDto> returnedVariablesFromStudyDataset = this.measurementVariableService
			.getVariablesForDataset(this.study.getProjectId(), VariableType.TRAIT.getId(), VariableType.EXPERIMENTAL_DESIGN.getId());
		Assert.assertTrue(returnedVariablesFromStudyDataset.isEmpty());

	}

}
