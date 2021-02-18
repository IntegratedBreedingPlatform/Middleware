package org.generationcp.middleware.service.api.program;

public class ProgramSearchRequest {
    // Program search request
    private String commonCropName;
    private String programDbId;
    private String programName;

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
