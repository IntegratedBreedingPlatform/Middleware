package org.generationcp.middleware.service.impl.dataset;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
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

}
