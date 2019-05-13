package org.generationcp.middleware.domain.search_request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
	private Integer gid;
	private String preferredName;
	private String pui;

	public GermplasmSearchRequestDto() {
	}

	public GermplasmSearchRequestDto(final Integer gid, final String preferredName, final String pui) {
		super();
		this.gid = gid;
		this.preferredName = preferredName;
		this.pui = pui;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getPreferredName() {
		return this.preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public String getPui() {
		return this.pui;
	}

	public void setPui(final String pui) {
		this.pui = pui;
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
