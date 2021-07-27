package org.generationcp.middleware.api.germplasmlist.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
public class GermplasmListSearchRequest extends SearchRequestDto {

	private SqlTextFilter listNameFilter;
	private String parentFolderName;
	private String description;
	private String ownerName;
	private List<String> listTypes;
	private Integer numberOfEntriesFrom;
	private Integer numberOfEntriesTo;
	private Boolean locked;
	private String notes;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date listDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date listDateTo;

	public SqlTextFilter getListNameFilter() {
		return listNameFilter;
	}

	public void setListNameFilter(final SqlTextFilter listNameFilter) {
		this.listNameFilter = listNameFilter;
	}

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(final String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	public List<String> getListTypes() {
		return listTypes;
	}

	public Integer getNumberOfEntriesFrom() {
		return numberOfEntriesFrom;
	}

	public void setNumberOfEntriesFrom(final Integer numberOfEntriesFrom) {
		this.numberOfEntriesFrom = numberOfEntriesFrom;
	}

	public Integer getNumberOfEntriesTo() {
		return numberOfEntriesTo;
	}

	public void setNumberOfEntriesTo(final Integer numberOfEntriesTo) {
		this.numberOfEntriesTo = numberOfEntriesTo;
	}

	public void setListTypes(final List<String> listTypes) {
		this.listTypes = listTypes;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Date getListDateFrom() {
		return listDateFrom;
	}

	public void setListDateFrom(final Date listDateFrom) {
		this.listDateFrom = listDateFrom;
	}

	public Date getListDateTo() {
		return listDateTo;
	}

	public void setListDateTo(final Date listDateTo) {
		this.listDateTo = listDateTo;
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
