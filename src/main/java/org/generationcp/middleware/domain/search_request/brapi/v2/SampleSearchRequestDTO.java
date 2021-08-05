package org.generationcp.middleware.domain.search_request.brapi.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleSearchRequestDTO {

    private String sampleDbId;
    private String observationUnitDbId;
    private String plateDbId;
    private String germplasmDbId;
    private String studyDbId;
    private String externalReferenceID;
    private String externalReferenceSource;

    public SampleSearchRequestDTO() {

    }

    public SampleSearchRequestDTO(final String sampleDbId, final String observationUnitDbId, final String plateDbId, final String germplasmDbId, final String studyDbId, final String externalReferenceID, final String externalReferenceSource) {
        this.sampleDbId = sampleDbId;
        this.observationUnitDbId = observationUnitDbId;
        this.plateDbId = plateDbId;
        this.germplasmDbId = germplasmDbId;
        this.studyDbId = studyDbId;
        this.externalReferenceID = externalReferenceID;
        this.externalReferenceSource = externalReferenceSource;
    }

    public String getSampleDbId() {
        return this.sampleDbId;
    }

    public void setSampleDbId(final String sampleDbId) {
        this.sampleDbId = sampleDbId;
    }

    public String getObservationUnitDbId() {
        return this.observationUnitDbId;
    }

    public void setObservationUnitDbId(final String observationUnitDbId) {
        this.observationUnitDbId = observationUnitDbId;
    }

    public String getPlateDbId() {
        return this.plateDbId;
    }

    public void setPlateDbId(final String plateDbId) {
        this.plateDbId = plateDbId;
    }

    public String getGermplasmDbId() {
        return this.germplasmDbId;
    }

    public void setGermplasmDbId(final String germplasmDbId) {
        this.germplasmDbId = germplasmDbId;
    }

    public String getStudyDbId() {
        return this.studyDbId;
    }

    public void setStudyDbId(final String studyDbId) {
        this.studyDbId = studyDbId;
    }

    public String getExternalReferenceID() {
        return this.externalReferenceID;
    }

    public void setExternalReferenceID(final String externalReferenceID) {
        this.externalReferenceID = externalReferenceID;
    }

    public String getExternalReferenceSource() {
        return this.externalReferenceSource;
    }

    public void setExternalReferenceSource(final String externalReferenceSource) {
        this.externalReferenceSource = externalReferenceSource;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }
}
