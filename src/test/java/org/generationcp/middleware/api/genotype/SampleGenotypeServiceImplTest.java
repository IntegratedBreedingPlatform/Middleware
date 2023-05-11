
package org.generationcp.middleware.api.genotype;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GenotypeDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.SampleGenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeVariablesSearchFilter;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SampleGenotypeServiceImplTest extends IntegrationTestBase {

	public static final String DEFAULT_MARKER_1 = "PZE-101093951";
	public static final String DEFAULT_MARKER_2 = "PZE-0186065237";
	public static final String DEFAULT_MARKER_3 = "PZE-0186365075";

	@Resource
	private SampleGenotypeService sampleGenotypeService;

	private IntegrationTestDataInitializer testDataInitializer;
	private DaoFactory daoFactory;
	private WorkbenchUser user;
	private DmsProject study;
	private DmsProject plotDataset;
	private DmsProject subObservationDataset;
	private Geolocation trialInstance1;
	private Geolocation trialInstance2;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study =
			this.testDataInitializer.createDmsProject(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				this.daoFactory.getDmsProjectDAO().getById(1), null);
		this.plotDataset = this.testDataInitializer
			.createDmsProject(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), this.study, this.study,
				DatasetTypeEnum.PLOT_DATA);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.GID.getId(), TermId.GID.name(), VariableType.GERMPLASM_DESCRIPTOR,
			null, 4);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.DESIG.getId(), TermId.DESIG.name(),
			VariableType.GERMPLASM_DESCRIPTOR, null, 5);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.TRIAL_INSTANCE_FACTOR.name(),
			VariableType.ENVIRONMENT_DETAIL, null, 3);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.REP_NO.getId(), TermId.REP_NO.name(),
			VariableType.EXPERIMENTAL_DESIGN, null, 8);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.PLOT_NO.getId(), TermId.PLOT_NO.name(),
			VariableType.EXPERIMENTAL_DESIGN, null, 9);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.OBS_UNIT_ID.getId(), TermId.OBS_UNIT_ID.name(),
			VariableType.EXPERIMENTAL_DESIGN, null, 10);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name(),
			VariableType.ENVIRONMENT_DETAIL, null, 6);
		this.testDataInitializer.addProjectProp(this.plotDataset, TermId.ENTRY_TYPE.getId(), TermId.ENTRY_TYPE.name(),
			VariableType.ENVIRONMENT_DETAIL, null, 7);
		this.subObservationDataset = this.testDataInitializer
			.createDmsProject(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), this.study, this.plotDataset,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);

		this.user = this.testDataInitializer.createUserForTesting();

		this.trialInstance1 = this.testDataInitializer.createTestGeolocation("1", 101);
		this.trialInstance2 = this.testDataInitializer.createTestGeolocation("2", 101);
	}

	@Test
	public void testImportSampleGenotypes() {

		final List<ExperimentModel>
			experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add one sample in the list
		final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, this.user.getUserid());

		Assert.assertEquals("1 sample should be created", 1, samples.size());

		final Sample sample = samples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_3, "T"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		this.sessionProvder.getSession().flush();

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(1,
			this.sampleGenotypeService.countFilteredSampleGenotypes(new SampleGenotypeSearchRequestDTO(this.study.getProjectId())));
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
	}

	@Test
	public void testImportSampleGenotypes_GenotypesAlreadyExist() {
		final List<ExperimentModel>
			experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add one sample in the list
		final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, this.user.getUserid());

		Assert.assertEquals("1 sample should be created", 1, samples.size());

		final Sample sample = samples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_2, "G"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);
		this.sessionProvder.getSession().flush();

		// Re-import the same Sample Genotype records and add new a new one
		final List<SampleGenotypeImportRequestDto> genotypesWithDifferentValue = new ArrayList<>();
		genotypesWithDifferentValue.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_1, "A"));
		genotypesWithDifferentValue.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_2, "B"));
		genotypesWithDifferentValue.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_3, "C"));
		this.sampleGenotypeService.importSampleGenotypes(genotypesWithDifferentValue);
		this.sessionProvder.getSession().flush();

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(1,
			this.sampleGenotypeService.countFilteredSampleGenotypes(new SampleGenotypeSearchRequestDTO(this.study.getProjectId())));
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("A", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("B", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());

	}

	@Test
	public void testSearchSampleGenotypes_FilterByDatasetId() {

		// Create 2 Datasets that will contain samples associated to their observation units
		final List<ExperimentModel>
			plotExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);
		final List<ExperimentModel>
			subObservationExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.subObservationDataset, plotExperimentModels.get(0),
				this.trialInstance1, 1);

		final SampleList observationSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		final SampleList subobservationSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add samples in the list
		final List<Sample> plotObservationSamples =
			this.testDataInitializer.addSamples(plotExperimentModels, observationSampleList, this.user.getUserid());
		final List<Sample> subObservationSamples =
			this.testDataInitializer.addSamples(subObservationExperimentModels, subobservationSampleList, this.user.getUserid());

		final Sample observationSample = plotObservationSamples.get(0);
		final Sample subObservationSample = subObservationSamples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(observationSample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(observationSample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(observationSample, DEFAULT_MARKER_3, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(subObservationSample, DEFAULT_MARKER_1, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(subObservationSample, DEFAULT_MARKER_2, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(subObservationSample, DEFAULT_MARKER_3, "G"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		this.sessionProvder.getSession().flush();

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		sampleGenotypeSearchRequestDTO.getFilter().setDatasetId(this.plotDataset.getProjectId());
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(1, this.sampleGenotypeService.countFilteredSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(2,
			this.sampleGenotypeService.countFilteredSampleGenotypes(new SampleGenotypeSearchRequestDTO(this.study.getProjectId())));
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypesBySampleList(observationSampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
	}

	@Test
	public void testSearchSampleGenotypes_FilterByGeolocationId() {

		// Create 2 trial instances in a dataset that will contain samples associated to their observation units
		final List<ExperimentModel>
			firstInstanceExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);
		final List<ExperimentModel>
			secondInstanceExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance2, 1);

		final SampleList firstInstanceSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		final SampleList secondInstanceSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());
		// Add samples in the list
		final List<Sample> firstInstanceSamples =
			this.testDataInitializer.addSamples(firstInstanceExperimentModels, firstInstanceSampleList, this.user.getUserid());
		final List<Sample> secondInstanceSamples =
			this.testDataInitializer.addSamples(secondInstanceExperimentModels, secondInstanceSampleList, this.user.getUserid());

		final Sample firstInstanceSample = firstInstanceSamples.get(0);
		final Sample secondInstanceSample = secondInstanceSamples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_3, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_1, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_2, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_3, "G"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		this.sessionProvder.getSession().flush();

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		sampleGenotypeSearchRequestDTO.getFilter().setInstanceIds(Arrays.asList(this.trialInstance1.getLocationId()));
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(1, this.sampleGenotypeService.countFilteredSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(2,
			this.sampleGenotypeService.countFilteredSampleGenotypes(new SampleGenotypeSearchRequestDTO(this.study.getProjectId())));
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypesBySampleList(firstInstanceSampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
	}

	@Test
	public void testSearchSampleGenotypes_WithFilters() {

		// Create 2 trial instances in a dataset that will contain samples associated to their observation units
		final List<ExperimentModel>
			firstInstanceExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);
		this.testDataInitializer.saveExperimentProperty(firstInstanceExperimentModels.get(0), TermId.REP_NO.getId(), "1");
		final Germplasm germplasm = firstInstanceExperimentModels.get(0).getStock().getGermplasm();
		final List<ExperimentModel>
			secondInstanceExperimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance2, 1);

		final SampleList firstInstanceSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());
		final SampleList secondInstanceSampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add samples in the list
		final List<Sample> firstInstanceSamples =
			this.testDataInitializer.addSamples(firstInstanceExperimentModels, firstInstanceSampleList, this.user.getUserid());
		final List<Sample> secondInstanceSamples =
			this.testDataInitializer.addSamples(secondInstanceExperimentModels, secondInstanceSampleList, this.user.getUserid());

		final Sample firstInstanceSample = firstInstanceSamples.get(0);
		final Sample secondInstanceSample = secondInstanceSamples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(firstInstanceSample, DEFAULT_MARKER_3, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_1, "T"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_2, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(secondInstanceSample, DEFAULT_MARKER_3, "G"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		this.sessionProvder.getSession().flush();

		final List<MeasurementVariable> measurementVariables =
			this.sampleGenotypeService.getSampleGenotypeColumns(this.study.getProjectId(),
				Collections.singletonList(firstInstanceSampleList.getId()));
		final Map<Integer, String> variableNameMap = measurementVariables.stream().filter(var -> var.getVariableType() != null)
			.collect(Collectors.toMap(MeasurementVariable::getTermId, MeasurementVariable::getName));
		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		sampleGenotypeSearchRequestDTO.getFilter().setInstanceIds(Arrays.asList(this.trialInstance1.getLocationId()));
		sampleGenotypeSearchRequestDTO.getFilter().setFilteredTextValues(new HashMap<>());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.GID.getId()), germplasm.getGid().toString());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.DESIG.getId()), germplasm.getPreferredName().getNval());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), this.trialInstance1.getDescription());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.ENTRY_NO.getId()), firstInstanceExperimentModels.get(0).getStock().getUniqueName());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.OBS_UNIT_ID.getId()), firstInstanceExperimentModels.get(0).getObsUnitId());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.TAKEN_BY.getId()), this.user.getPerson().getFullName());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.SAMPLE_NAME.getId()), firstInstanceSample.getSampleName());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues()
			.put(String.valueOf(TermId.SAMPLE_UUID.getId()), firstInstanceSample.getSampleBusinessKey());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredTextValues().put(String.valueOf(TermId.SAMPLING_DATE.getId()),
			Util.formatDateAsStringValue(firstInstanceSample.getSamplingDate(), Util.FRONTEND_DATE_FORMAT));
		sampleGenotypeSearchRequestDTO.getFilter().setFilteredValues(new HashMap<>());
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredValues()
			.put(String.valueOf(TermId.REP_NO.getId()), Collections.singletonList("1"));
		sampleGenotypeSearchRequestDTO.getFilter().getFilteredValues()
			.put(String.valueOf(TermId.PLOT_NO.getId()), Collections.singletonList("1"));
		final Map<String, String> variableMap = measurementVariables.stream().filter(var -> var.getVariableType() != null)
			.collect(Collectors.toMap(var -> String.valueOf(var.getTermId()), var -> var.getVariableType().name()));
		sampleGenotypeSearchRequestDTO.getFilter().setVariableTypeMap(variableMap);
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);
		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(1, this.sampleGenotypeService.countFilteredSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(2,
			this.sampleGenotypeService.countFilteredSampleGenotypes(new SampleGenotypeSearchRequestDTO(this.study.getProjectId())));
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypesBySampleList(firstInstanceSampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
		Assert.assertEquals(germplasm.getGid().toString(),
			sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.GID.getId())).getValue());
		Assert.assertEquals(germplasm.getPreferredName().getNval(),
			sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.DESIG.getId())).getValue());
		Assert.assertEquals(this.trialInstance1.getDescription(),
			sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId())).getValue());
		Assert.assertEquals(firstInstanceExperimentModels.get(0).getStock().getUniqueName(),
			sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.ENTRY_NO.getId())).getValue());
		Assert.assertEquals(firstInstanceExperimentModels.get(0).getObsUnitId(),
			sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.OBS_UNIT_ID.getId())).getValue());
		Assert.assertEquals("1", sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.REP_NO.getId())).getValue());
		Assert.assertEquals("1", sampleGenotypeDTO.getGenotypeDataMap().get(variableNameMap.get(TermId.PLOT_NO.getId())).getValue());
		Assert.assertEquals(this.user.getPerson().getFirstName() + " " + this.user.getPerson().getLastName(),
			sampleGenotypeDTO.getTakenBy());
		Assert.assertEquals(firstInstanceSample.getSampleName(), sampleGenotypeDTO.getSampleName());
		Assert.assertEquals(firstInstanceSample.getSampleBusinessKey(), sampleGenotypeDTO.getSampleUUID());
		Assert.assertEquals(Util.formatDateAsStringValue(firstInstanceSample.getSamplingDate(), Util.FRONTEND_DATE_FORMAT),
			Util.formatDateAsStringValue(sampleGenotypeDTO.getSamplingDate(), Util.FRONTEND_DATE_FORMAT));
	}

	@Test
	public void testGetSampleGenotypesVariables() {

		final List<ExperimentModel>
			experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add one sample in the list
		final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, this.user.getUserid());

		Assert.assertEquals("1 sample should be created", 1, samples.size());

		final Sample sample = samples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_3, "T"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		// Get the marker genotype variables available in the study.
		final SampleGenotypeVariablesSearchFilter filter = new SampleGenotypeVariablesSearchFilter();
		filter.setStudyId(this.study.getProjectId());
		filter.setDatasetIds(Arrays.asList(this.plotDataset.getProjectId()));
		final Map<Integer, MeasurementVariable> variableMap =
			this.sampleGenotypeService.getSampleGenotypeVariables(filter);
		Assert.assertEquals(3, variableMap.size());
		Assert.assertTrue(
			variableMap.values().stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_1))
				.findAny().isPresent());
		Assert.assertTrue(
			variableMap.values().stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_2))
				.findAny().isPresent());
		Assert.assertTrue(
			variableMap.values().stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_3))
				.findAny().isPresent());
	}

	@Test
	public void testGetSampleGenotypeColumns() {
		final List<ExperimentModel>
			experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plotDataset, null, this.trialInstance1, 1);

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add one sample in the list
		final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, this.user.getUserid());

		Assert.assertEquals("1 sample should be created", 1, samples.size());

		final Sample sample = samples.get(0);

		// Create Sample Genotype records
		final List<SampleGenotypeImportRequestDto> genotypes = new ArrayList<>();
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_1, "C"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_2, "G"));
		genotypes.add(this.createSampleGenotypeImportRequestDto(sample, DEFAULT_MARKER_3, "T"));
		this.sampleGenotypeService.importSampleGenotypes(genotypes);

		final List<MeasurementVariable> columns =
			this.sampleGenotypeService.getSampleGenotypeColumns(this.study.getProjectId(), Collections.singletonList(sampleList.getId()));
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_1))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_2))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(DEFAULT_MARKER_3))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(TermId.TAKEN_BY.name()))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(TermId.SAMPLE_NAME.name()))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(TermId.SAMPLE_UUID.name()))
				.findAny().isPresent());
		Assert.assertTrue(
			columns.stream().filter(measurementVariable -> measurementVariable.getName().equalsIgnoreCase(TermId.SAMPLING_DATE.name()))
				.findAny().isPresent());
		for (final Integer variableId : GenotypeDao.STANDARD_SAMPLE_GENOTYPE_TABLE_VARIABLE_IDS) {
			Assert.assertTrue(
				columns.stream().filter(measurementVariable -> measurementVariable.getTermId() == variableId)
					.findAny().isPresent());
		}
	}

	private SampleGenotypeImportRequestDto createSampleGenotypeImportRequestDto(final Sample sample, final String markerVariableName,
		final String value) {

		final CVTerm markerVariable = this.daoFactory.getCvTermDao().getByName(markerVariableName);
		Assert.assertNotNull("Marker variable should exist", markerVariable);

		final SampleGenotypeImportRequestDto sampleGenotypeImportRequestDto = new SampleGenotypeImportRequestDto();
		sampleGenotypeImportRequestDto.setSampleUID(String.valueOf(sample.getSampleBusinessKey()));
		sampleGenotypeImportRequestDto.setVariableId(String.valueOf(markerVariable.getCvTermId()));
		sampleGenotypeImportRequestDto.setValue(value);
		return sampleGenotypeImportRequestDto;
	}

}
