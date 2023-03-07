package org.generationcp.middleware.domain.genotype;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenotypeSearchRequestDTO extends SearchRequestDto {

    private int studyId;

    private GenotypeSearchRequestDTO.Filter filter;

    public int getStudyId() {
        return this.studyId;
    }

    public void setStudyId(final int studyId) {
        this.studyId = studyId;
    }

    public GenotypeSearchRequestDTO.Filter getFilter() {
        return this.filter;
    }

    public void setFilter(final GenotypeSearchRequestDTO.Filter filter) {
        this.filter = filter;
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

    public static class Filter {
        public static final String GID = "gid";
        public static final String DESIGNATION = "designation";
        public static final String PLOT_NO = "plotNumber";
        public static final String SAMPLE_NO = "sampleNumber";
        public static final String SAMPLE_NAME = "sampleName";
        public static final String VARIABLE_ID = "variableId";
        public static final String VARIABLE_NAME = "variableName";
        public static final String VALUE = "value";

        public static final List<String> SORTABLE_FIELDS = Collections.unmodifiableList(Arrays
                .asList(GID, DESIGNATION, PLOT_NO, SAMPLE_NO, SAMPLE_NAME, VARIABLE_NAME, VALUE));

        private List<Integer> gidList;

        private String designation;

        private List<Integer> plotNumberList;

        private List<Integer> sampleNumberList;

        private String sampleName;

        private List<Integer> variableIdsList;

        private String value;

        public List<Integer> getGidList() {
            return gidList;
        }

        public void setGidList(List<Integer> gidList) {
            this.gidList = gidList;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public List<Integer> getPlotNumberList() {
            return plotNumberList;
        }

        public void setPlotNumberList(List<Integer> plotNumberList) {
            this.plotNumberList = plotNumberList;
        }

        public List<Integer> getSampleNumberList() {
            return sampleNumberList;
        }

        public void setSampleNumberList(List<Integer> sampleNumberList) {
            this.sampleNumberList = sampleNumberList;
        }

        public String getSampleName() {
            return sampleName;
        }

        public void setSampleName(String sampleName) {
            this.sampleName = sampleName;
        }

        public List<Integer> getVariableIdsList() {
            return variableIdsList;
        }

        public void setVariableIdsList(List<Integer> variableIdsList) {
            this.variableIdsList = variableIdsList;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
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
}
