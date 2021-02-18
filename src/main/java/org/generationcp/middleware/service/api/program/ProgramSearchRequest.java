package org.generationcp.middleware.service.api.program;

public class ProgramSearchRequest {
    // Program search request
    private String commonCropName;
    private String programDbId;
    private String programName;
    private String abbreviation;

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getCommonCropName() {
        return this.commonCropName;
    }

    public void setCommonCropName(final String commonCropName) {
        this.commonCropName = commonCropName;
    }

    public String getProgramDbId() {
        return this.programDbId;
    }

    public void setProgramDbId(final String programDbId) {
        this.programDbId = programDbId;
    }

    public String getProgramName() {
        return this.programName;
    }

    public void setProgramName(final String programName) {
        this.programName = programName;
    }

}
