package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GermplasmListGeneratorDTO extends GermplasmListBasicInfoDTO{

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
			return this.data;
		}

		public void setData(final Map<Integer, GermplasmListObservationDto> data) {
			this.data = data;
		}
	}

	// for direct import scenarios
	private List<GermplasmEntryDTO> entries;
	// Retrieve entries from filter or list of selected ids
	private SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite;

	public GermplasmListGeneratorDTO() {
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
