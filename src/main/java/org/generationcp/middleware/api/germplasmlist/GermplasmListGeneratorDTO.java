package org.generationcp.middleware.api.germplasmlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GermplasmListGeneratorDTO {

	public static class GermplasmEntryDTO {

		private Integer entryNo;
		private Integer gid;
		private String entryCode;
		private String seedSource;
		private String groupName;

		/**
		 * key: variableId
		 */
		private Map<Integer, GermplasmListObservationDto> data = Collections.emptyMap();

		public Integer getEntryNo() {
			return this.entryNo;
		}

		public void setEntryNo(final Integer entryNo) {
			this.entryNo = entryNo;
		}

		public Integer getGid() {
			return this.gid;
		}

		public void setGid(final Integer gid) {
			this.gid = gid;
		}

		public String getEntryCode() {
			return this.entryCode;
		}

		public void setEntryCode(final String entryCode) {
			this.entryCode = entryCode;
		}

		public String getSeedSource() {
			return this.seedSource;
		}

		public void setSeedSource(final String seedSource) {
			this.seedSource = seedSource;
		}

		public String getGroupName() {
			return this.groupName;
		}

		public void setGroupName(final String groupName) {
			this.groupName = groupName;
		}

		public Map<Integer, GermplasmListObservationDto> getData() {
			return data;
		}

		public void setData(final Map<Integer, GermplasmListObservationDto> data) {
			this.data = data;
		}
	}

	// for response
	private Integer id;
	private String name;
	private String description;
	private String type;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date date;
	private String notes;

	private String parentFolderId;

	// for direct import scenarios
	private List<GermplasmEntryDTO> entries;
	// Retrieve entries from filter or list of selected ids
	private SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getParentFolderId() {
		return this.parentFolderId;
	}

	public void setParentFolderId(final String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public List<GermplasmEntryDTO> getEntries() {
		return this.entries;
	}

	public void setEntries(final List<GermplasmEntryDTO> entries) {
		this.entries = entries;
	}

	public SearchCompositeDto<GermplasmSearchRequest, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}
}
