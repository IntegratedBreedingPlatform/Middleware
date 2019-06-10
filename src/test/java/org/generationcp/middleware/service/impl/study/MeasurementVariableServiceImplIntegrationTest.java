package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * The class <code>MeasurementVariableServiceImplIntegrationTest</code> contains tests for the class <code>{@link MeasurementVariableServiceImpl}</code>.
 */
public class MeasurementVariableServiceImplIntegrationTest extends IntegrationTestBase {

	private DaoFactory daoFactory;

	private DmsProjectDao dmsProjectDao;
	private CVTermDao cvTermDao;
	private ProjectPropertyDao projectPropertyDao;

	private MeasurementVariableService measurementVariableService;

	private DmsProject study;
	private DmsProject plot;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.measurementVariableService = new MeasurementVariableServiceImpl(this.sessionProvder.getSession());
		this.dmsProjectDao = this.daoFactory.getDmsProjectDAO();
		this.cvTermDao = this.daoFactory.getCvTermDao();
		this.projectPropertyDao = this.daoFactory.getProjectPropertyDAO();

		this.study = this.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.plot = this.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
	}

	@Test
	public void testGetVariables() {

		final String trait1Name = "Trait1";
		final String trait2Name = "Trait2";
		final CVTerm trait1 = this.createVariable(trait1Name);
		final CVTerm trait2 = this.createVariable(trait2Name);

		this.addProjectProp(this.plot, trait1.getCvTermId(), trait1Name, VariableType.TRAIT);
		this.addProjectProp(this.plot, trait2.getCvTermId(), trait2Name, VariableType.TRAIT);

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
		final CVTerm trait1 = this.createVariable(variable1Name);
		final CVTerm trait2 = this.createVariable(variable2Name);

		this.addProjectProp(this.plot, trait1.getCvTermId(), variable1Name, VariableType.TRAIT);
		this.addProjectProp(this.plot, trait2.getCvTermId(), variable2Name, VariableType.EXPERIMENTAL_DESIGN);

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

	private DmsProject createDmsProject(final String name, final String description, final DmsProject study, final DmsProject parent,
		final DatasetTypeEnum datasetTypeEnum) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setStudy(study);
		dmsProject.setParent(parent);
		if (datasetTypeEnum != null) {
			dmsProject.setDatasetType(new DatasetType(datasetTypeEnum.getId()));
		}
		this.dmsProjectDao.save(dmsProject);
		this.dmsProjectDao.refresh(dmsProject);
		return dmsProject;
	}

	private CVTerm createVariable(final String name) {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		trait.setName(name);
		this.cvTermDao.save(trait);
		return trait;
	}

	private void addProjectProp(final DmsProject project, final int variableId, final String alias, final VariableType variableType) {

		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setVariableId(variableId);
		projectProperty.setProject(project);
		projectProperty.setRank(1);
		projectProperty.setAlias(alias);
		projectProperty.setTypeId(variableType.getId());

		this.projectPropertyDao.save(projectProperty);

	}

}
