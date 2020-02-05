package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.api.study.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
@Transactional
public class BMSAPIStudyServiceMock implements StudyService {

    @Autowired
    private StudyService studyServiceImpl;

    @Override
    public List<StudySummary> search(StudySearchParameters serchParameters) {
        return null;
    }

    @Override
    public boolean hasMeasurementDataOnEnvironment(int studyIdentifier, int instanceId) {
        return false;
    }

    @Override
    public boolean hasAdvancedOrCrossesList(int studyId) {
        return false;
    }

    @Override
    public int countTotalObservationUnits(int studyIdentifier, int instanceId) {
        return 0;
    }

    @Override
    public List<ObservationDto> getObservations(int studyIdentifier, int instanceId, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        return null;
    }

    @Override
    public List<ObservationDto> getSingleObservation(int studyIdentifier, int measurementIdentifier) {
        return null;
    }

    @Override
    public ObservationDto updataObservation(Integer studyIdentifier, ObservationDto middlewareMeasurement) {
        return null;
    }

    @Override
    public List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer) {
        return null;
    }

    @Override
    public String getProgramUUID(Integer studyIdentifier) {
        return null;
    }

    @Override
    public TrialObservationTable getTrialObservationTable(int studyIdentifier) {
        return null;
    }

    @Override
    public TrialObservationTable getTrialObservationTable(int studyIdentifier, Integer instanceDbId) {
        return null;
    }

    @Override
    public StudyDetailsDto getStudyDetailsForGeolocation(Integer geolocationId) {
        return this.studyServiceImpl.getStudyDetailsForGeolocation(geolocationId);
    }

    @Override
    public boolean hasMeasurementDataEntered(List<Integer> ids, int studyId) {
        return false;
    }

    @Override
    public List<PhenotypeSearchDTO> searchPhenotypes(Integer pageSize, Integer pageNumber, PhenotypeSearchRequestDTO requestDTO) {
        return null;
    }

    @Override
    public long countPhenotypes(PhenotypeSearchRequestDTO requestDTO) {
        return 0;
    }

    @Override
    public List<String> getGenericGermplasmDescriptors(int studyIdentifier) {
        return null;
    }

    @Override
    public List<String> getAdditionalDesignFactors(int studyIdentifier) {
        return null;
    }

    @Override
    public Integer getPlotDatasetId(int studyId) {
        return null;
    }

    @Override
    public Integer getEnvironmentDatasetId(int studyId) {
        return null;
    }
}
