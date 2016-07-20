package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.StudyType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudySummary implements Serializable {

    private Integer studyDbid;

    private String name;

    private StudyType type;

    private List<String> years;

    private List<String> seasons;

    private String locationId;

    private String programDbId;

    private Map<String, Object> optionalInfo = new HashMap<>();

    public Integer getStudyDbid() {
        return studyDbid;
    }

    public StudySummary setStudyDbid(final Integer studyDbid) {
        this.studyDbid = studyDbid;
        return this;
    }

    public String getName() {
        return name;
    }

    public StudySummary setName(final String name) {
        this.name = name;
        return this;
    }

    public StudyType getType() {
        return type;
    }

    public StudySummary setType(final StudyType type) {
        this.type = type;
        return this;
    }

    public List<String> getYears() {
        return years;
    }

    public StudySummary setYears(final List<String> years) {
        this.years = years;
        return this;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public StudySummary setSeasons(final List<String> seasons) {
        this.seasons = seasons;
        return this;
    }

    public String getLocationId() {
        return locationId;
    }

    public StudySummary setLocationId(final String locationId) {
        this.locationId = locationId;
        return this;
    }

    public String getProgramDbId() {
        return programDbId;
    }

    public StudySummary setProgramDbId(final String programDbId) {
        this.programDbId = programDbId;
        return this;
    }

    public Map<String, Object> getOptionalInfo() {
        return optionalInfo;
    }

    public StudySummary setOptionalInfo(final Map<String, Object> optionalInfo) {
        this.optionalInfo = optionalInfo;
        return this;
    }
}
