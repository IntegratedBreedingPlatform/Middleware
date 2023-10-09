package org.generationcp.middleware.api.crossplan;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty

public class CrossPlanSearchRequest extends SearchRequestDto {

    private List<Integer> crossPlanIds;

    private SqlTextFilter crossPlanNameFilter;

    private String ownerName;

    private String notes;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date crossPlanStartDateFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date crossPlanStartDateTo;

    private String parentFolderName;

    public List<Integer> getCrossPlanIds() {
        return crossPlanIds;
    }

    public void setCrossPlanIds(List<Integer> crossPlanIds) {
        this.crossPlanIds = crossPlanIds;
    }

    public SqlTextFilter getCrossPlanNameFilter() {
        return crossPlanNameFilter;
    }

    public void setCrossPlanNameFilter(SqlTextFilter crossPlanNameFilter) {
        this.crossPlanNameFilter = crossPlanNameFilter;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCrossPlanStartDateFrom() {
        return crossPlanStartDateFrom;
    }

    public void setCrossPlanStartDateFrom(Date crossPlanStartDateFrom) {
        this.crossPlanStartDateFrom = crossPlanStartDateFrom;
    }

    public Date getCrossPlanStartDateTo() {
        return crossPlanStartDateTo;
    }

    public void setCrossPlanStartDateTo(Date crossPlanStartDateTo) {
        this.crossPlanStartDateTo = crossPlanStartDateTo;
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
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
