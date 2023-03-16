package org.generationcp.middleware.api.genotype;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.SampleGenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(3, this.sampleGenotypeService.countSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(3, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
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

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add samples in the list
		final List<Sample> plotObservationSamples =
			this.testDataInitializer.addSamples(plotExperimentModels, sampleList, this.user.getUserid());
		final List<Sample> subObservationSamples =
			this.testDataInitializer.addSamples(subObservationExperimentModels, sampleList, this.user.getUserid());

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

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		sampleGenotypeSearchRequestDTO.getFilter().setDatasetId(this.plotDataset.getProjectId());
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(3, this.sampleGenotypeService.countFilteredSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(6, this.sampleGenotypeService.countSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(6, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
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

		final SampleList sampleList =
			this.testDataInitializer.createTestSampleList(RandomStringUtils.randomAlphabetic(10), this.user.getUserid());

		// Add samples in the list
		final List<Sample> firstInstanceSamples =
			this.testDataInitializer.addSamples(firstInstanceExperimentModels, sampleList, this.user.getUserid());
		final List<Sample> secondInstanceSamples =
			this.testDataInitializer.addSamples(secondInstanceExperimentModels, sampleList, this.user.getUserid());

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

		// Retrieve the imported Sample Genotype Records
		final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO = new SampleGenotypeSearchRequestDTO();
		sampleGenotypeSearchRequestDTO.setStudyId(this.study.getProjectId());
		sampleGenotypeSearchRequestDTO.setFilter(new SampleGenotypeSearchRequestDTO.GenotypeFilter());
		sampleGenotypeSearchRequestDTO.getFilter().setInstanceIds(Arrays.asList(this.trialInstance1.getLocationId()));
		final List<SampleGenotypeDTO>
			sampleGenotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, sampleGenotypeDTOList.size());
		Assert.assertEquals(3, this.sampleGenotypeService.countFilteredSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(6, this.sampleGenotypeService.countSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(6, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
		final SampleGenotypeDTO sampleGenotypeDTO = sampleGenotypeDTOList.get(0);
		Assert.assertEquals("C", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_1).getValue());
		Assert.assertEquals("G", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_2).getValue());
		Assert.assertEquals("T", sampleGenotypeDTO.getGenotypeDataMap().get(DEFAULT_MARKER_3).getValue());
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
		final Map<Integer, MeasurementVariable> variableMap =
			this.sampleGenotypeService.getSampleGenotypeVariables(this.study.getProjectId(), this.plotDataset.getProjectId());
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

	private SampleGenotypeImportRequestDto createSampleGenotypeImportRequestDto(final Sample sample, final String markerVariableName,
		final String value) {

		final CVTerm markerVariable = this.daoFactory.getCvTermDao().getByName(markerVariableName);
		Assert.assertNotNull("Marker variable should exist", markerVariable);

		final SampleGenotypeImportRequestDto sampleGenotypeImportRequestDto = new SampleGenotypeImportRequestDto();
		sampleGenotypeImportRequestDto.setSampleId(String.valueOf(sample.getSampleId()));
		sampleGenotypeImportRequestDto.setVariableId(String.valueOf(markerVariable.getCvTermId()));
		sampleGenotypeImportRequestDto.setValue(value);
		return sampleGenotypeImportRequestDto;
	}

}
