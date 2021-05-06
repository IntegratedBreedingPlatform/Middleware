package org.generationcp.middleware.service.api.program;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Objects;

@AutoProperty
public class ProgramSearchRequest {

    private String commonCropName;
    private String programDbId;
    private String programName;
    private String programNameContainsString;
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

    public String getProgramNameContainsString() {
        return this.programNameContainsString;
    }

    public void setProgramNameContainsString(final String programNameContainsString) {
        this.programNameContainsString = programNameContainsString;
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }


    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }
}
