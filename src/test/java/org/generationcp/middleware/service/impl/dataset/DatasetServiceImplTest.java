package org.generationcp.middleware.service.impl.dataset;

import java.util.Arrays;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
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
	public void testDatasetExists() {
		final DmsProject dataset1 = new DmsProject();
		dataset1.setProjectId(11);
		final DmsProject dataset2 = new DmsProject();
		dataset2.setProjectId(12);
		final DmsProject dataset3 = new DmsProject();
		dataset3.setProjectId(13);
		Mockito.when(this.dmsProjectDao.getDatasetsByStudy(Matchers.anyInt())).thenReturn(Arrays.asList(dataset1, dataset2, dataset3));

		Assert.assertTrue(this.datasetService.datasetExists(123, 13));
		Assert.assertFalse(this.datasetService.datasetExists(123, 14));
	}

}
