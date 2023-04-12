package org.generationcp.middleware.domain.search_request.brapi.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
public class TrialSearchRequestDTO extends SearchRequestDto {

	private int page;
	private int pageSize;

	private Boolean active;
	private List<String> commonCropNames;
	private List<String> contactDbIds;
	private List<String> externalReferenceIds;
	private List<String> externalReferenceSources;
	private List<String> locationDbIds;
	private List<String> locationNames;
	private List<String> programDbIds;
	private List<String> programNames;
	private List<String> studyDbIds;
	private List<String> studyNames;
	private List<String> trialDbIds;
	private List<String> trialNames;
	private List<String> trialPUIs;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date searchDateRangeStart;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date searchDateRangeEnd;

	public int getPage() {
		return this.page;
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public List<String> getCommonCropNames() {
		return this.commonCropNames;
	}

	public void setCommonCropNames(final List<String> commonCropNames) {
		this.commonCropNames = commonCropNames;
	}

	public List<String> getContactDbIds() {
		return this.contactDbIds;
	}

	public void setContactDbIds(final List<String> contactDbIds) {
		this.contactDbIds = contactDbIds;
	}

	public List<String> getExternalReferenceIds() {
		return this.externalReferenceIds;
	}

	public void setExternalReferenceIds(final List<String> externalReferenceIds) {
		this.externalReferenceIds = externalReferenceIds;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<String> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getLocationNames() {
		return this.locationNames;
	}

	public void setLocationNames(final List<String> locationNames) {
		this.locationNames = locationNames;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getProgramNames() {
		return this.programNames;
	}

	public void setProgramNames(final List<String> programNames) {
		this.programNames = programNames;
	}

	public Date getSearchDateRangeStart() {
		return this.searchDateRangeStart;
	}

	public void setSearchDateRangeStart(final Date searchDateRangeStart) {
		this.searchDateRangeStart = searchDateRangeStart;
	}

	public Date getSearchDateRangeEnd() {
		return this.searchDateRangeEnd;
	}

	public void setSearchDateRangeEnd(final Date searchDateRangeEnd) {
		this.searchDateRangeEnd = searchDateRangeEnd;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getStudyNames() {
		return this.studyNames;
	}

	public void setStudyNames(final List<String> studyNames) {
		this.studyNames = studyNames;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getTrialNames() {
		return this.trialNames;
	}

	public void setTrialNames(final List<String> trialNames) {
		this.trialNames = trialNames;
	}

	public List<String> getTrialPUIs() {
		return this.trialPUIs;
	}

	public void setTrialPUIs(final List<String> trialPUIs) {
		this.trialPUIs = trialPUIs;
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
