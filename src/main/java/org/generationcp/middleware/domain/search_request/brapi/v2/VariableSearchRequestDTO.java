package org.generationcp.middleware.domain.search_request.brapi.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariableSearchRequestDTO extends SearchRequestDto {

    private List<String> dataTypes;
    private List<String> externalReferenceIDs;
    private List<String> externalReferenceSources;
    private List<String> methodDbIds;
    private List<String> observationVariableDbIds;
    private List<String> observationVariableNames;
    private List<String> ontologyDbIds;
    private int page;
    private int pageSize;
    private List<String> scaleDbIds;
    private List<String> studyDbId;
    private List<String> traitClasses;
    private List<String> traitDbIds;

    public List<String> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<String> getExternalReferenceIDs() {
        return externalReferenceIDs;
    }

    public void setExternalReferenceIDs(List<String> externalReferenceIDs) {
        this.externalReferenceIDs = externalReferenceIDs;
    }

    public List<String> getExternalReferenceSources() {
        return externalReferenceSources;
    }

    public void setExternalReferenceSources(List<String> externalReferenceSources) {
        this.externalReferenceSources = externalReferenceSources;
    }

    public List<String> getMethodDbIds() {
        return methodDbIds;
    }

    public void setMethodDbIds(List<String> methodDbIds) {
        this.methodDbIds = methodDbIds;
    }

    public List<String> getObservationVariableDbIds() {
        return observationVariableDbIds;
    }

    public void setObservationVariableDbIds(List<String> observationVariableDbIds) {
        this.observationVariableDbIds = observationVariableDbIds;
    }

    public List<String> getObservationVariableNames() {
        return observationVariableNames;
    }

    public void setObservationVariableNames(List<String> observationVariableNames) {
        this.observationVariableNames = observationVariableNames;
    }

    public List<String> getOntologyDbIds() {
        return ontologyDbIds;
    }

    public void setOntologyDbIds(List<String> ontologyDbIds) {
        this.ontologyDbIds = ontologyDbIds;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getScaleDbIds() {
        return scaleDbIds;
    }

    public void setScaleDbIds(List<String> scaleDbIds) {
        this.scaleDbIds = scaleDbIds;
    }

    public List<String> getStudyDbId() {
        return studyDbId;
    }

    public void setStudyDbId(List<String> studyDbId) {
        this.studyDbId = studyDbId;
    }

    public List<String> getTraitClasses() {
        return traitClasses;
    }

    public void setTraitClasses(List<String> traitClasses) {
        this.traitClasses = traitClasses;
    }

    public List<String> getTraitDbIds() {
        return traitDbIds;
    }

    public void setTraitDbIds(List<String> traitDbIds) {
        this.traitDbIds = traitDbIds;
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
