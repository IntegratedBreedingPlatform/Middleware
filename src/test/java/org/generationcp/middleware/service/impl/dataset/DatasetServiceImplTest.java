package org.generationcp.middleware.service.impl.dataset;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
	
	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@InjectMocks
	private DatasetServiceImpl datasetService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.datasetService.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
	}

	@Test
	public void testCountPhenotypes() {
		final long count = 5;
		Mockito.when(this.phenotypeDao.countPhenotypesForDataset(Matchers.anyInt(), Matchers.anyListOf(Integer.class))).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countPhenotypes(123, Arrays.asList(11, 22)));
	}

	@Test
	public void testAddVariable() {
		final Random ran = new Random();
		final Integer datasetId = ran.nextInt();
		final Integer nextRank = ran.nextInt();
		Mockito.doReturn(nextRank).when(this.projectPropertyDao).getNextRank(datasetId);
		final Integer traitId = ran.nextInt();
		final String alias = RandomStringUtils.randomAlphabetic(20);

		this.datasetService.addVariable(datasetId, traitId, VariableType.TRAIT, alias);
		final ArgumentCaptor<ProjectProperty> projectPropertyCaptor = ArgumentCaptor.forClass(ProjectProperty.class);
		Mockito.verify(this.projectPropertyDao).save(projectPropertyCaptor.capture());
		final ProjectProperty datasetVariable = projectPropertyCaptor.getValue();
		Assert.assertEquals(datasetId, datasetVariable.getProject().getProjectId());
		Assert.assertEquals(VariableType.TRAIT.getId(), datasetVariable.getTypeId());
		Assert.assertEquals(nextRank, datasetVariable.getRank());
		Assert.assertEquals(traitId, datasetVariable.getVariableId());
		Assert.assertEquals(alias, datasetVariable.getAlias());
	}

	@Test
	public void testGetDatasets() {
		final List<DatasetDTO> datasetDTOs1 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs2 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs3 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs4 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		DatasetDTO datasetDTO;

		datasetDTO = createDataset(25020, 25019, "IBP-2015-ENVIRONMENT", 10080);
		datasetDTOs1.add(datasetDTO);
		datasetDTO = createDataset(25021, 25019, "IBP-2015-PLOTDATA", 10090);
		datasetDTOs1.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs1);

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);
		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTO = createDataset(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", 10094);
		datasetDTOs2.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs2);
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTO = createDataset(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", 10094);
		datasetDTOs3.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs3);
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTO = createDataset(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", 10094);
		datasetDTOs4.add(datasetDTO);
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
		DatasetDTO datasetDTO;

		datasetDTO = createDataset(25020, 25019, "IBP-2015-ENVIRONMENT", 10080);
		datasetDTOs1.add(datasetDTO);
		datasetDTO = createDataset(25021, 25019, "IBP-2015-PLOTDATA", 10090);
		datasetDTOs1.add(datasetDTO);

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);
		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTO = createDataset(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", 10094);
		datasetDTOs2.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs2);
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTO = createDataset(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", 10094);
		datasetDTOs3.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs3);
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTO = createDataset(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", 10094);
		datasetDTOs4.add(datasetDTO);
		datasetDTOList.addAll(datasetDTOs4);
		Mockito.when(this.dmsProjectDao.getDatasets(25023)).thenReturn(datasetDTOs4);

		final Set<Integer> datasetTypeIds = new TreeSet<>();
		datasetTypeIds.add(10094);
		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, datasetTypeIds);
		assertThat(datasetDTOList, equalTo(result));
	}

	private static DatasetDTO createDataset(final Integer datasetId, final Integer parentDatasetId, final String name,
		final Integer datasetTypeId) {
		final DatasetDTO datasetDTO = new DatasetDTO();
		datasetDTO.setDatasetId(datasetId);
		datasetDTO.setDatasetTypeId(datasetTypeId);
		datasetDTO.setName(name);
		datasetDTO.setParentDatasetId(parentDatasetId);
		return datasetDTO;

	}
}
