package org.generationcp.middleware.api.genotype;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.genotype.GenotypeDTO;
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
import java.util.List;

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
	private DmsProject plot;
	private Geolocation geolocation;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study =
			this.testDataInitializer.createDmsProject(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				this.daoFactory.getDmsProjectDAO().getById(1), null);
		this.plot = this.testDataInitializer
			.createDmsProject(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), this.study, this.study,
				DatasetTypeEnum.PLOT_DATA);

		this.user = this.testDataInitializer.createUserForTesting();

		this.geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
	}

	@Test
	public void testImportSampleGenotypes() {

		final List<ExperimentModel>
			experimentModels = this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, this.geolocation, 1);

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
		final List<GenotypeDTO> genotypeDTOList = this.sampleGenotypeService.searchSampleGenotypes(sampleGenotypeSearchRequestDTO, null);

		Assert.assertEquals(1, genotypeDTOList.size());
		Assert.assertEquals(1, this.sampleGenotypeService.countSampleGenotypes(sampleGenotypeSearchRequestDTO));
		Assert.assertEquals(3, this.sampleGenotypeService.countSampleGenotypesBySampleList(sampleList.getId()));
		final GenotypeDTO genotypeDTO = genotypeDTOList.get(0);
		Assert.assertEquals("C",
			genotypeDTO.getGenotypeDataList().stream().filter(g -> g.getVariableName().equals(DEFAULT_MARKER_1)).findAny().get()
				.getValue());
		Assert.assertEquals("G",
			genotypeDTO.getGenotypeDataList().stream().filter(g -> g.getVariableName().equals(DEFAULT_MARKER_2)).findAny().get()
				.getValue());
		Assert.assertEquals("T",
			genotypeDTO.getGenotypeDataList().stream().filter(g -> g.getVariableName().equals(DEFAULT_MARKER_3)).findAny().get()
				.getValue());
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
