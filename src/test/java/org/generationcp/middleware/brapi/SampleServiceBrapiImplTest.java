package org.generationcp.middleware.brapi;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.search_request.brapi.v2.SampleSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleExternalReference;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.api.brapi.SampleServiceBrapi;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class SampleServiceBrapiImplTest extends IntegrationTestBase {

    private static final SimpleDateFormat DATE_FORMAT = Util.getSimpleDateFormat("yyyy-MM-dd");

    private IntegrationTestDataInitializer testDataInitializer;
    private DaoFactory daoFactory;
    private DmsProject study;
    private DmsProject plot;

    @Resource
    private SampleServiceBrapi sampleServiceBrapi;

    @Before
    public void setUp() {
        this.daoFactory = new DaoFactory(this.sessionProvder);
        this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
        this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null,
                this.daoFactory.getDmsProjectDAO().getById(1), null);
        this.plot = this.testDataInitializer
                .createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);


        this.sessionProvder.getSession().flush();
    }

    @Test
    public void testGetSampleObservationDto_Success() {
        final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
        final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 1);

        final WorkbenchUser user = this.testDataInitializer.createUserForTesting();
        final SampleList sampleList = this.testDataInitializer.createTestSampleList("MyList", user.getUserid());
        final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, user.getUserid());

        final SampleExternalReference sampleExternalReference = new SampleExternalReference();
        sampleExternalReference.setSample(samples.get(0));
        sampleExternalReference.setReferenceId("refId");
        sampleExternalReference.setSource("refSource");
        this.daoFactory.getSampleExternalReferenceDAO().save(sampleExternalReference);

        this.sessionProvder.getSession().flush();
        final Sample sample = samples.get(0);
        final ExperimentModel model = experimentModels.get(0);
        final String germplasmUUID = model.getStock().getGermplasm().getGermplasmUUID();
        final SampleSearchRequestDTO requestDTO = new SampleSearchRequestDTO();
        requestDTO.setSampleDbIds(Collections.singletonList(sample.getSampleBusinessKey()));
        requestDTO.setGermplasmDbIds(germplasmUUID != null ? Collections.singletonList(germplasmUUID) : Collections.emptyList());
        requestDTO.setObservationUnitDbIds(Collections.singletonList(model.getObsUnitId()));
        requestDTO.setPlateDbIds(Collections.singletonList(sample.getPlateId()));
        requestDTO.setStudyDbIds(Collections.singletonList(geolocation.getLocationId().toString()));
        requestDTO.setExternalReferenceIDs(Collections.singletonList(sampleExternalReference.getReferenceId()));
        requestDTO.setExternalReferenceSources(Collections.singletonList(sampleExternalReference.getSource()));

        final List<SampleObservationDto> sampleDtos = this.sampleServiceBrapi.getSampleObservations(requestDTO, null);
        Assert.assertEquals(1, sampleDtos.size());
        final SampleObservationDto sampleObservationDto = sampleDtos.get(0);
        Assert.assertEquals(germplasmUUID, sampleObservationDto.getGermplasmDbId());
        Assert.assertEquals(model.getObsUnitId(), sampleObservationDto.getObservationUnitDbId());
        Assert.assertEquals(sample.getPlateId(), sampleObservationDto.getPlateDbId());
        Assert.assertEquals(sample.getSampleBusinessKey(), sampleObservationDto.getSampleDbId());
        Assert.assertEquals(DATE_FORMAT.format(sample.getSamplingDate()),
                DATE_FORMAT.format(sampleObservationDto.getSampleTimestamp()));
        Assert.assertEquals(sample.getTakenBy(), sampleObservationDto.getTakenById());
        Assert.assertEquals(this.study.getProjectId().toString(), sampleObservationDto.getTrialDbId());
        Assert.assertEquals(geolocation.getLocationId().toString(), sampleObservationDto.getStudyDbId());
        Assert.assertEquals(sample.getSampleNumber(), sampleObservationDto.getPlateIndex());
        Assert.assertEquals(sample.getSampleName(), sampleObservationDto.getSampleName());
    }

}
