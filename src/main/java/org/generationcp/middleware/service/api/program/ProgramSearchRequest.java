package org.generationcp.middleware.service.api.program;

import org.pojomatic.Pojomatic;

import java.util.Objects;

public class ProgramSearchRequest {

    private String commonCropName;
    private String programDbId;
    private String programName;
    private String abbreviation;
    private Integer loggedInUserId;

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

    public Integer getLoggedInUserId() {
        return this.loggedInUserId;
    }

    public void setLoggedInUserId(final Integer loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public String getProgramName() {
        return this.programName;
    }

    public void setProgramName(final String programName) {
        this.programName = programName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ProgramSearchRequest that = (ProgramSearchRequest) o;
        return Objects.equals(this.commonCropName, that.commonCropName) && Objects.equals(this.programDbId, that.programDbId) && Objects.equals(this.programName, that.programName) && Objects.equals(this.abbreviation, that.abbreviation) && Objects.equals(this.loggedInUserId, that.loggedInUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commonCropName, this.programDbId, this.programName, this.abbreviation, this.loggedInUserId);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }
}
