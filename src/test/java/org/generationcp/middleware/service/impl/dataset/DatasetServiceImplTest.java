package org.generationcp.middleware.service.impl.dataset;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;

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
		when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		when(this.daoFactory.getExperimentDAO()).thenReturn(this.experimentDao);
		when(this.daoFactory.getFormulaDAO()).thenReturn(this.formulaDao);
	}

	@Test
	public void testCountPhenotypes() {
		final long count = 5;
		when(this.phenotypeDao.countPhenotypesForDataset(Matchers.anyInt(), Matchers.anyListOf(Integer.class))).thenReturn(count);
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
	public void testAddPhenotype() {
		final Random ran = new Random();

		final Phenotype savedPhenotype = new Phenotype();
		savedPhenotype.setPhenotypeId(ran.nextInt());
		savedPhenotype.setCreatedDate(new Date("01/01/2018 12:59:59"));
		savedPhenotype.setUpdatedDate(new Date("02/02/2018 11:59:59"));

		final ObservationDto observationDto = new ObservationDto();

		observationDto.setCategoricalValueId(ran.nextInt());
		observationDto.setVariableId(ran.nextInt());
		observationDto.setValue(ran.toString());
		observationDto.setObservationUnitId(ran.nextInt());

		when(this.formulaDao.getByTargetVariableId(observationDto.getVariableId())).thenReturn(new Formula());
		when(this.phenotypeDao.save(Mockito.any(Phenotype.class))).thenReturn(savedPhenotype);

		final ObservationDto savedObservation = this.datasetService.addPhenotype(observationDto);

		final ArgumentCaptor<Phenotype> captor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).save(captor.capture());

		final Phenotype phenotypeToBeSaved = captor.getValue();

		Assert.assertEquals(phenotypeToBeSaved.getcValueId(), observationDto.getCategoricalValueId());
		Assert.assertEquals(phenotypeToBeSaved.getObservableId(), observationDto.getVariableId());
		Assert.assertEquals(phenotypeToBeSaved.getValue(), observationDto.getValue());
		Assert.assertEquals(phenotypeToBeSaved.getExperiment().getNdExperimentId(), observationDto.getObservationUnitId());
		Assert.assertEquals(Phenotype.ValueStatus.MANUALLY_EDITED, phenotypeToBeSaved.getValueStatus());
		Assert.assertEquals(savedObservation.getObservationId(), savedPhenotype.getPhenotypeId());

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DatasetServiceImpl.DATE_FORMAT);
		Assert.assertEquals(savedObservation.getCreatedDate(), dateFormat.format(savedPhenotype.getCreatedDate()));
		Assert.assertEquals(savedObservation.getUpdatedDate(), dateFormat.format(savedPhenotype.getUpdatedDate()));

	}

	@Test
	public void testUpdatePhenotype() {
		final Random ran = new Random();

		final Integer observationUnitId = ran.nextInt();
		final Integer observationId = ran.nextInt();
		final Integer categoricalValueId = ran.nextInt();
		final Integer observableId = ran.nextInt();
		final String value = ran.toString();

		final Phenotype existingPhenotype = new Phenotype();
		existingPhenotype.setPhenotypeId(observationId);
		existingPhenotype.setCreatedDate(new Date("01/01/2018 12:59:59"));
		existingPhenotype.setUpdatedDate(new Date("02/02/2018 11:59:59"));
		existingPhenotype.setExperiment(new ExperimentModel(observationUnitId));
		existingPhenotype.setObservableId(observableId);

		when(formulaDao.getByTargetVariableId(observableId)).thenReturn(new Formula());
		when(phenotypeDao.getById(observationId)).thenReturn(existingPhenotype);

		final ObservationDto savedObservation =
			this.datasetService.updatePhenotype(observationUnitId, observationId, categoricalValueId, value);

		Mockito.verify(this.phenotypeDao).update(existingPhenotype);

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DatasetServiceImpl.DATE_FORMAT);

		Assert.assertEquals(value, existingPhenotype.getValue());
		Assert.assertEquals(categoricalValueId, existingPhenotype.getcValueId());
		Assert.assertEquals(savedObservation.getObservationId(), existingPhenotype.getPhenotypeId());
		Assert.assertEquals(savedObservation.getCategoricalValueId(), existingPhenotype.getcValueId());
		Assert.assertEquals(savedObservation.getStatus(), existingPhenotype.getValueStatus().getName());
		Assert.assertEquals(savedObservation.getValue(), existingPhenotype.getValue());
		Assert.assertEquals(savedObservation.getUpdatedDate(), dateFormat.format(existingPhenotype.getUpdatedDate()));
		Assert.assertEquals(savedObservation.getCreatedDate(), dateFormat.format(existingPhenotype.getCreatedDate()));
		Assert.assertEquals(savedObservation.getObservationUnitId(), existingPhenotype.getExperiment().getNdExperimentId());

	}

	@Test
	public void testResolveObservationStatusVaribleHasFormula() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		when(formulaDao.getByTargetVariableId(variableId)).thenReturn(new Formula());

		final Phenotype phenotype = new Phenotype();
		this.datasetService.resolveObservationStatus(variableId, phenotype);

		Assert.assertEquals(Phenotype.ValueStatus.MANUALLY_EDITED, phenotype.getValueStatus());
	}

	@Test
	public void testResolveObservationStatusVaribleHasNoFormula() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();

		final Phenotype phenotype = new Phenotype();
		when(formulaDao.getByTargetVariableId(variableId)).thenReturn(new Formula());

		Assert.assertNull(phenotype.getValueStatus());
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
		Mockito.verify(this.phenotypeDao).updateOutOfSyncPhenotypes(
			observationUnitId,
			Arrays.asList(term1.getCvTermId(), term2.getCvTermId()));
	}

}
