package org.generationcp.middleware.domain.search_request.brapi.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmSearchRequestDto {

	private int page;
	private int pageSize;

	private List<String> accessionNumbers;
	private List<String> commonCropNames;
	private List<String> germplasmDbIds;
	private List<String> germplasmGenus;
	private List<String> germplasmNames;
	private List<String> germplasmPUIs;
	private List<String> germplasmSpecies;

	public GermplasmSearchRequestDto() {
		this.commonCropNames = Lists.newArrayList();
		this.germplasmDbIds = Lists.newArrayList();
		this.germplasmNames = Lists.newArrayList();
		this.accessionNumbers = Lists.newArrayList();
		this.germplasmGenus = Lists.newArrayList();
		this.germplasmPUIs = Lists.newArrayList();
		this.germplasmSpecies = Lists.newArrayList();
	}

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
