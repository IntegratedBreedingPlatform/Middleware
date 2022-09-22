package org.generationcp.middleware.service.impl.observationunit;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelEnum;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelFilter;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationUnitServiceImpl;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ObservationUnitServiceImplTest {

	@Mock
	private StudyInstanceService studyInstanceService;

	@Mock
	private DatasetService datasetService;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private ExperimentDao experimentDao;

	@InjectMocks
	private ObservationUnitServiceImpl observationUnitService;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(this.observationUnitService, "studyInstanceService", this.studyInstanceService);
		ReflectionTestUtils.setField(this.observationUnitService, "datasetService", this.datasetService);
		ReflectionTestUtils.setField(this.observationUnitService, "daoFactory", this.daoFactory);
		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		Mockito.when(this.daoFactory.getExperimentDao()).thenReturn(this.experimentDao);
	}
	@Test
	public void testGetObservationLevels_EmptyFilters() {
		final List<ObservationLevel> observationLevelList = this.observationUnitService.getObservationLevels(new ObservationLevelFilter());
		Assert.assertEquals(ObservationLevelEnum.values().length, observationLevelList.size());
	}

	@Test
	public void testGetObservationLevels_WithStudyDbIdFilterAndHasNoExperimentalDesignGenerated() {
		final int studyId = Integer.valueOf(RandomStringUtils.randomNumeric(5));
		final int instanceId = Integer.valueOf(RandomStringUtils.randomNumeric(5));
		final ObservationLevelFilter filter = new ObservationLevelFilter();
		filter.setStudyDbId(String.valueOf(instanceId));
		filter.setTrialDbId(String.valueOf(studyId));
		final StudyInstance studyInstance = new StudyInstance();
		studyInstance.setHasExperimentalDesign(false);
		Mockito.when(this.studyInstanceService.getStudyInstance(studyId, instanceId)).thenReturn(Optional.of(studyInstance));
		final List<ObservationLevel> observationLevelList = this.observationUnitService.getObservationLevels(filter);
		Assert.assertEquals(2, observationLevelList.size());
		Mockito.verify(this.studyInstanceService).getStudyInstance(studyId, instanceId);
		Mockito.verify(this.datasetService, Mockito.never()).getDatasets(studyId,
			Sets.newHashSet(DatasetTypeEnum.PLOT_DATA.getId()));
		Mockito.verify(this.daoFactory, Mockito.never()).getDmsProjectDAO();
	}

	@Test
	public void testGetObservationLevels_WithStudyDbIdFilterWithExperimentalDesignGenerated() {
		final int studyId = Integer.valueOf(RandomStringUtils.randomNumeric(5));
		final int instanceId = Integer.valueOf(RandomStringUtils.randomNumeric(5));
		final ObservationLevelFilter filter = new ObservationLevelFilter();
		filter.setStudyDbId(String.valueOf(instanceId));
		filter.setTrialDbId(String.valueOf(studyId));
		final StudyInstance studyInstance = new StudyInstance();
		studyInstance.setHasExperimentalDesign(true);
		Mockito.when(this.studyInstanceService.getStudyInstance(studyId, instanceId)).thenReturn(Optional.of(studyInstance));
		final DatasetDTO datasetDTO = new DatasetDTO();
		datasetDTO.setDatasetId(Integer.valueOf(RandomStringUtils.randomNumeric(5)));
		Mockito.when(this.datasetService.getDatasets(studyId, Sets.newHashSet(DatasetTypeEnum.PLOT_DATA.getId())))
			.thenReturn(Collections.singletonList(datasetDTO));

		this.observationUnitService.getObservationLevels(filter);
		Mockito.verify(this.studyInstanceService).getStudyInstance(studyId, instanceId);
		Mockito.verify(this.dmsProjectDao).getDatasetTypeIdsOfEnvironment(instanceId);
		Mockito.verify(this.datasetService).getDatasets(studyId, Sets.newHashSet(DatasetTypeEnum.PLOT_DATA.getId()));
		Mockito.verify(this.projectPropertyDao).getByProjectId(datasetDTO.getDatasetId());
	}

	@Test
	public void testGetObservationLevels_WithTrialDbId() {
		final int studyId = Integer.valueOf(RandomStringUtils.randomNumeric(5));
		final ObservationLevelFilter filter = new ObservationLevelFilter();
		filter.setTrialDbId(String.valueOf(studyId));
		final DatasetDTO datasetDTO = new DatasetDTO();
		datasetDTO.setDatasetTypeId(DatasetTypeEnum.PLOT_DATA.getId());
		datasetDTO.setDatasetId(Integer.valueOf(RandomStringUtils.randomNumeric(5)));
		Mockito.when(this.dmsProjectDao.getDatasets(studyId))
			.thenReturn(Collections.singletonList(datasetDTO));

		this.observationUnitService.getObservationLevels(filter);
		Mockito.verify(this.dmsProjectDao).getDatasets(studyId);
		Mockito.verify(this.experimentDao).hasFieldLayout(datasetDTO.getDatasetId());
		Mockito.verify(this.projectPropertyDao).getByProjectId(datasetDTO.getDatasetId());
	}
}
