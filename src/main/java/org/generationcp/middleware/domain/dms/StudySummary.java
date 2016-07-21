package org.generationcp.middleware.domain.dms;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.oms.StudyType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudySummary implements Serializable {

    private Integer studyDbid;

    private String name;

    private String type;

    private List<String> years = Lists.newArrayList();

    private List<String> seasons = Lists.newArrayList();

    private Integer locationId;

    private String programDbId;

    private Map<String, String> optionalInfo = new HashMap<>();

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

    public String getType() {
        return type;
    }

    public StudySummary setType(final String type) {
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

    public StudySummary addYear (final String year){
        this.years.add(year);
        return this;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public StudySummary setSeasons(final List<String> seasons) {
        this.seasons = seasons;
        return this;
    }

    public StudySummary addSeason (final String season){
        this.seasons.add(season);
        return this;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public StudySummary setLocationId(final Integer locationId) {
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

    public Map<String, String> getOptionalInfo() {
        return optionalInfo;
    }

    public StudySummary setOptionalInfo(final Map<String, String> optionalInfo) {
        this.optionalInfo = optionalInfo;
        return this;
    }
}
