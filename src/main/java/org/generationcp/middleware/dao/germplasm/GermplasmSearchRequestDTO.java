package org.generationcp.middleware.dao.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@AutoProperty
public class GermplasmSearchRequestDTO implements Serializable {

	private Integer gid;
	private String preferredName;
	private String pui;
	private Integer page;
	private Integer pageSize;

	public GermplasmSearchRequestDTO() {
	}

	public GermplasmSearchRequestDTO(final Integer gid, final String preferredName, final String pui, final Integer page,
			final Integer pageSize) {
		this.gid = gid;
		this.preferredName = preferredName;
		this.pui = pui;
		this.page = page;
		this.pageSize = pageSize;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public String getPui() {
		return pui;
	}

	public void setPui(final String pui) {
		this.pui = pui;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(final Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
