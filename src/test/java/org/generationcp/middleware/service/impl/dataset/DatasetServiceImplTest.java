package org.generationcp.middleware.service.impl.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
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
	
	@Mock
	private ExperimentDao experimentDao;
	
	@Mock
	private FormulaDAO formulaDao;
	
	@InjectMocks
	private DatasetServiceImpl datasetService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.datasetService.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		Mockito.when(this.daoFactory.getExperimentDAO()).thenReturn(this.experimentDao);
		Mockito.when(this.daoFactory.getFormulaDAO()).thenReturn(this.formulaDao);
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
	public void testRemoveVariables() {
		final Random ran = new Random();
		final int datasetId = ran.nextInt();
		final List<Integer> variableIds = Arrays.asList(ran.nextInt(), ran.nextInt()); 
		this.datasetService.removeVariables(datasetId, variableIds);
		Mockito.verify(this.phenotypeDao).deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
		Mockito.verify(this.projectPropertyDao).deleteProjectVariables(datasetId, variableIds);
	}
	
	@Test
	public void testIsValidObservation() {
		final Random ran = new Random();
		final int datasetId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		this.datasetService.isValidObservationUnit(datasetId, observationUnitId);
		Mockito.verify(this.experimentDao).isValidExperiment(datasetId, observationUnitId);
	}
	
	@Test
	public void testUpdateDependentPhenotypesWhenNotInputVariable() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		Mockito.doReturn(new ArrayList<Formula>()).when(this.formulaDao).getByInputId(variableId);
		this.datasetService.updateDependentPhenotypesStatus(variableId, observationUnitId);
		Mockito.verify(this.phenotypeDao, Mockito.never()).updateOutOfSyncPhenotypes(Matchers.anyInt(), Matchers.anyListOf(Integer.class));
	}

	@Test
	public void testUpdateDependentPhenotypes() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		final Formula formula1 = new Formula();
		final CVTerm term1 = new CVTerm();
		term1.setCvTermId(ran.nextInt());
		formula1.setTargetCVTerm(term1);
		final Formula formula2 = new Formula();
		final CVTerm term2 = new CVTerm();
		term2.setCvTermId(ran.nextInt());
		formula2.setTargetCVTerm(term2);
		Mockito.doReturn(Arrays.asList(formula1, formula2)).when(this.formulaDao).getByInputId(variableId);
		this.datasetService.updateDependentPhenotypesStatus(variableId, observationUnitId);
		Mockito.verify(this.phenotypeDao).updateOutOfSyncPhenotypes(observationUnitId,
				Arrays.asList(term1.getCvTermId(), term2.getCvTermId()));
	}

}
