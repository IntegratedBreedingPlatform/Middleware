package org.generationcp.middleware.service.impl.dataset;

import java.util.Arrays;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
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
	public void testCountPhenotypesByInstance() {
		final long count = 6;
		Mockito.when(this.phenotypeDao.countPhenotypesForDatasetAndInstance(Matchers.anyInt(), Matchers.anyInt())).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countPhenotypesByInstance(1, 2));
	}

}
