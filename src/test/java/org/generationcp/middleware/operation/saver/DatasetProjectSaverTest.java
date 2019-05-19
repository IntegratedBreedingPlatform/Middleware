package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatasetProjectSaverTest {

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private ProjectPropertySaver projectPropertySaver;

	@Mock
	private StandardVariableBuilder standardVariableBuilder;

	@InjectMocks
	private final DatasetProjectSaver datasetProjectSaver = new DatasetProjectSaver();

	private final Random random = new Random();
	final String programUUID = UUID.randomUUID().toString();

	@Before
	public void init() {
		when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.standardVariableBuilder.create(TermId.DATASET_NAME.getId(), this.programUUID)).thenReturn(new StandardVariable());
		when(this.standardVariableBuilder.create(TermId.DATASET_TITLE.getId(), this.programUUID)).thenReturn(new StandardVariable());
	}

	@Test
	public void testAddDataSet() {

		final int studyId = this.random.nextInt(10);
		final String datasetName = "Dataset Name";
		final String datasetTitle = "Dataset Title";

		final DmsProject study = new DmsProject();
		study.setProjectId(studyId);
		final VariableTypeList variableTypeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(datasetName);
		datasetValues.setDescription(datasetTitle);

		when(this.dmsProjectDao.getById(studyId)).thenReturn(study);
		when(this.projectPropertySaver
			.create(Mockito.any(DmsProject.class), Mockito.eq(variableTypeList), Mockito.eq(datasetValues.getVariables())))
			.thenReturn(new ArrayList<ProjectProperty>());

		final DmsProject
			createdProject =
			this.datasetProjectSaver.addDataSet(studyId, variableTypeList, datasetValues, this.programUUID, DatasetType.PLANT_SUBOBSERVATIONS);

		assertEquals(datasetName, createdProject.getName());
		assertEquals(datasetTitle, createdProject.getDescription());
		assertEquals(DatasetType.PLANT_SUBOBSERVATIONS, createdProject.getDatasetType().getDatasetTypeId().intValue());
		assertNotNull(createdProject.getProperties());
		assertNotNull(createdProject.getRelatedTos());

	}

}
