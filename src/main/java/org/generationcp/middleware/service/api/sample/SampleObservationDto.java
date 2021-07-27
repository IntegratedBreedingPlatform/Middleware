package org.generationcp.middleware.service.api.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class SampleObservationDto implements Serializable {

    private static final long serialVersionUID = 2340381705850740790L;

    @JsonView(BrapiView.BrapiV2.class)
    private Map<String, String> additionalInfo;

    @JsonView(BrapiView.BrapiV2.class)
    private Integer column;

    @JsonView(BrapiView.BrapiV2.class)
    private List<ExternalReferenceDTO> externalReferences;

    private String germplasmDbId;
    private String observationUnitDbId;
    private String plateDbId;

    @JsonView(BrapiView.BrapiV2.class)
    private String plateName;

    @JsonView(BrapiView.BrapiV2.class)
    private String programDbId;

    @JsonView(BrapiView.BrapiV2.class)
    private String row;

    @JsonView(BrapiView.BrapiV2.class)
    private String sampleBarcode;

    @JsonView(BrapiView.BrapiV2.class)
    private String sampleDescription;

    @JsonView(BrapiView.BrapiV2.class)
    private String sampleGroupDbId;

    @JsonView(BrapiView.BrapiV2.class)
    private String sampleName;

    @JsonView(BrapiView.BrapiV2.class)
    private String samplePUI;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date sampleTimestamp;

    private String sampleType;
    private String studyDbId;
    private String takenBy;
    private String tissueType;

    @JsonView(BrapiView.BrapiV2.class)
    private String trialDbId;

    @JsonView(BrapiView.BrapiV2.class)
    private String well;

    private String sampleDbId;

    @JsonView(BrapiView.BrapiV1_3.class)
    private String plantDbId;

    @JsonView(BrapiView.BrapiV1_3.class)
    private String notes;

    @JsonView(BrapiView.BrapiV1_3.class)
    private Integer plateIndex;

    @JsonView(BrapiView.BrapiV1_3.class)
    private String plotDbId;

    public SampleObservationDto() {

    }

    public SampleObservationDto(final String studyDbId, final String obsUnitId, final String plantId, final String sampleDbId) {
        this.studyDbId = studyDbId;
        this.observationUnitDbId = obsUnitId;
        this.plantDbId = plantId;
        this.sampleDbId = sampleDbId;
    }

    public String getStudyDbId() {
        return this.studyDbId;
    }

    public void setStudyDbId(final String studyDbId) {
        this.studyDbId = studyDbId;
    }

    public String getObservationUnitDbId() {
        return this.observationUnitDbId;
    }

    public void setObservationUnitDbId(final String observationUnitDbId) {
        this.observationUnitDbId = observationUnitDbId;
    }

    public String getPlantDbId() {
        return this.plantDbId;
    }

    public void setPlantDbId(final String plantDbId) {
        this.plantDbId = plantDbId;
    }

    public String getSampleDbId() {
        return this.sampleDbId;
    }

    public void setSampleDbId(final String sampleDbId) {
        this.sampleDbId = sampleDbId;
    }

    public String getTakenBy() {
        return this.takenBy;
    }

    public void setTakenBy(final String takenBy) {
        this.takenBy = takenBy;
    }

    public String getSampleType() {
        return this.sampleType;
    }

    public void setSampleType(final String sampleType) {
        this.sampleType = sampleType;
    }

    public String getTissueType() {
        return this.tissueType;
    }

    public void setTissueType(final String tissueType) {
        this.tissueType = tissueType;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public String getGermplasmDbId() {
        return this.germplasmDbId;
    }

    public void setGermplasmDbId(final String germplasmDbId) {
        this.germplasmDbId = germplasmDbId;
    }

    public String getPlateDbId() {
        return this.plateDbId;
    }

    public void setPlateDbId(final String plateDbId) {
        this.plateDbId = plateDbId;
    }

    public Integer getPlateIndex() {
        return this.plateIndex;
    }

    public void setPlateIndex(final Integer plateIndex) {
        this.plateIndex = plateIndex;
    }

    public String getPlotDbId() {
        return this.plotDbId;
    }

    public void setPlotDbId(final String plotDbId) {
        this.plotDbId = plotDbId;
    }

    public Date getSampleTimestamp() {
        return this.sampleTimestamp;
    }

    public void setSampleTimestamp(final Date sampleTimestamp) {
        this.sampleTimestamp = sampleTimestamp;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public List<ExternalReferenceDTO> getExternalReferences() {
        return externalReferences;
    }

    public void setExternalReferences(List<ExternalReferenceDTO> externalReferences) {
        this.externalReferences = externalReferences;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getProgramDbId() {
        return programDbId;
    }

    public void setProgramDbId(String programDbId) {
        this.programDbId = programDbId;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getSampleBarcode() {
        return sampleBarcode;
    }

    public void setSampleBarcode(String sampleBarcode) {
        this.sampleBarcode = sampleBarcode;
    }

    public String getSampleDescription() {
        return sampleDescription;
    }

    public void setSampleDescription(String sampleDescription) {
        this.sampleDescription = sampleDescription;
    }

    public String getSampleGroupDbId() {
        return sampleGroupDbId;
    }

    public void setSampleGroupDbId(String sampleGroupDbId) {
        this.sampleGroupDbId = sampleGroupDbId;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSamplePUI() {
        return samplePUI;
    }

    public void setSamplePUI(String samplePUI) {
        this.samplePUI = samplePUI;
    }

    public String getTrialDbId() {
        return trialDbId;
    }

    public void setTrialDbId(String trialDbId) {
        this.trialDbId = trialDbId;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
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