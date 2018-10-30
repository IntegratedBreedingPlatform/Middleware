package org.generationcp.middleware.service.impl.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DatasetServiceImplTest {
	
	@Mock
	private DaoFactory daoFactory;
	
	@Mock
	private HibernateSessionProvider session;
	
	@Mock
	private PhenotypeDao phenotypeDao;
	
	@Mock
	private DmsProjectDao dmsProjectDao;
	
	@InjectMocks
	private DatasetServiceImpl datasetService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.datasetService.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
	}
	
	@Test
	public void testCountPhenotypes() {
		final long count = 5;
		Mockito.when(this.phenotypeDao.countPhenotypesForDataset(Matchers.anyInt(), Matchers.anyListOf(Integer.class))).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countPhenotypes(123, Arrays.asList(11, 22)));
	}

	@Test
	public void testGetDatasetByStudyId() {
		final List<DatasetDTO> datasetDTOs = createDatasets(Arrays.asList(10094, 10097));
		Mockito.when(this.dmsProjectDao.getDatasetByStudyId(Matchers.anyInt(), Matchers.anySet())).thenReturn(datasetDTOs);
		Assert.assertEquals(datasetDTOs, this.datasetService.getDatasetByStudyId(123, null));
	}

	private static List<DatasetDTO> createDatasets(final List<Integer> datasetTypes) {
		final List<DatasetDTO> datasets = new ArrayList<>();
		int num = datasetTypes.size();
		for (final Integer datasetType : datasetTypes) {
			final DataSetType dataSetType = DataSetType.findById(datasetType);
			final DatasetDTO datasetDTO = new DatasetDTO();
			datasetDTO.setDatasetTypeId(dataSetType.getId());
			datasetDTO.setName(dataSetType.name() + "_" + num);
			num--;
		}
		return datasets;
	}
}
