package org.generationcp.middleware.domain.search_request.brapi.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmSearchRequestDto extends SearchRequestDto {

	private List<String> accessionNumbers;
	private List<String> commonCropNames;
	private List<String> germplasmDbIds;
	private List<String> germplasmGenus;
	private List<String> germplasmNames;
	private List<String> germplasmPUIs;
	private List<String> germplasmSpecies;
	private String preferredName;
	private String studyDbId;
	private String parentDbId;
	private String progenyDbId;
	private String externalReferenceId;
	private String externalReferenceSource;

	public GermplasmSearchRequestDto() {
		this.accessionNumbers = Lists.newArrayList();
		this.commonCropNames = Lists.newArrayList();
		this.germplasmDbIds = Lists.newArrayList();
		this.germplasmGenus = Lists.newArrayList();
		this.germplasmNames = Lists.newArrayList();
		this.germplasmPUIs = Lists.newArrayList();
		this.germplasmSpecies = Lists.newArrayList();
	}

	public String getPreferredName() {
		return this.preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public List<String> getAccessionNumbers() {
		return this.accessionNumbers;
	}

	public void setAccessionNumbers(final List<String> accessionNumbers) {
		this.accessionNumbers = accessionNumbers;
	}

	public List<String> getCommonCropNames() {
		return this.commonCropNames;
	}

	public void setCommonCropNames(final List<String> commonCropNames) {
		this.commonCropNames = commonCropNames;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<String> getGermplasmGenus() {
		return this.germplasmGenus;
	}

	public void setGermplasmGenus(final List<String> germplasmGenus) {
		this.germplasmGenus = germplasmGenus;
	}

	public List<String> getGermplasmNames() {
		return this.germplasmNames;
	}

	public void setGermplasmNames(final List<String> germplasmNames) {
		this.germplasmNames = germplasmNames;
	}

	public List<String> getGermplasmPUIs() {
		return this.germplasmPUIs;
	}

	public void setGermplasmPUIs(final List<String> germplasmPUIs) {
		this.germplasmPUIs = germplasmPUIs;
	}

	public List<String> getGermplasmSpecies() {
		return this.germplasmSpecies;
	}

	public void setGermplasmSpecies(final List<String> germplasmSpecies) {
		this.germplasmSpecies = germplasmSpecies;
	}

	public String getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public String getParentDbId() {
		return this.parentDbId;
	}

	public void setParentDbId(final String parentDbId) {
		this.parentDbId = parentDbId;
	}

	public String getProgenyDbId() {
		return this.progenyDbId;
	}

	public void setProgenyDbId(final String progenyDbId) {
		this.progenyDbId = progenyDbId;
	}

	public String getExternalReferenceId() {
		return this.externalReferenceId;
	}

	public void setExternalReferenceId(final String externalReferenceId) {
		this.externalReferenceId = externalReferenceId;
	}

	public String getExternalReferenceSource() {
		return this.externalReferenceSource;
	}

	public void setExternalReferenceSource(final String externalReferenceSource) {
		this.externalReferenceSource = externalReferenceSource;
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

	public boolean noFiltersSpecified() {
		return this.accessionNumbers.isEmpty() && this.commonCropNames.isEmpty() && this.germplasmDbIds.isEmpty() && this.germplasmGenus
			.isEmpty() && this.germplasmNames.isEmpty()
			&& this.germplasmPUIs.isEmpty() && this.germplasmSpecies.isEmpty() && StringUtils.isEmpty(this.preferredName) && StringUtils
			.isEmpty(this.studyDbId) &&
			StringUtils.isEmpty(this.parentDbId) && StringUtils.isEmpty(this.progenyDbId) && StringUtils
			.isEmpty(this.externalReferenceId) && StringUtils.isEmpty(this.externalReferenceSource);
	}
}
