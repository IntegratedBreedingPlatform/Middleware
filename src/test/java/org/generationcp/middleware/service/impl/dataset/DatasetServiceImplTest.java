package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
	public void testGetDatasets() {
		final List<DatasetDTO> datasetDTOs1 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs2 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs3 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs4 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		DatasetDTO datasetDTOs;

		datasetDTOs = createDatasets(25020, 25019, "IBP-2015-ENVIRONMENT", 10080);
		datasetDTOs1.add(datasetDTOs);
		datasetDTOs = createDatasets(25021, 25019, "IBP-2015-PLOTDATA", 10090);
		datasetDTOs1.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs1);

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);
		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTOs = createDatasets(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", 10094);
		datasetDTOs2.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs2);
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTOs = createDatasets(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", 10094);
		datasetDTOs3.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs3);
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTOs = createDatasets(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", 10094);
		datasetDTOs4.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs4);
		Mockito.when(this.dmsProjectDao.getDatasets(25023)).thenReturn(datasetDTOs4);

		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, new TreeSet<Integer>());
		assertThat(datasetDTOList, equalTo(result));
	}

	@Test
	public void testGetDatasetsFilteringByDatasetTypeId() {
		final List<DatasetDTO> datasetDTOs1 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs2 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs3 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs4 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		DatasetDTO datasetDTOs;

		datasetDTOs = createDatasets(25020, 25019, "IBP-2015-ENVIRONMENT", 10080);
		datasetDTOs1.add(datasetDTOs);
		datasetDTOs = createDatasets(25021, 25019, "IBP-2015-PLOTDATA", 10090);
		datasetDTOs1.add(datasetDTOs);

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);

		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTOs = createDatasets(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", 10094);
		datasetDTOs2.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs2);
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTOs = createDatasets(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", 10094);
		datasetDTOs3.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs3);
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTOs = createDatasets(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", 10094);
		datasetDTOs4.add(datasetDTOs);
		datasetDTOList.addAll(datasetDTOs4);
		Mockito.when(this.dmsProjectDao.getDatasets(25023)).thenReturn(datasetDTOs4);

		final Set<Integer> datasetTypeIds = new TreeSet<>();
		datasetTypeIds.add(10094);
		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, datasetTypeIds);
		assertThat(datasetDTOList, equalTo(result));
	}

	private static DatasetDTO createDatasets(final Integer datasetId, final Integer parentDatasetId, final String name,
		final Integer datasetTypeId) {
		final DatasetDTO datasetDTO = new DatasetDTO();
		datasetDTO.setDatasetId(datasetId);
		datasetDTO.setDatasetTypeId(datasetTypeId);
		datasetDTO.setName(name);
		datasetDTO.setParentDatasetId(parentDatasetId);
		return datasetDTO;

	}
}
