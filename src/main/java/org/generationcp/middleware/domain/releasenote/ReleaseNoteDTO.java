package org.generationcp.middleware.domain.releasenote;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class ReleaseNoteDTO {

	private Integer id;
	private String version;
	private Date releaseDate;

	public ReleaseNoteDTO() {
	}

	public ReleaseNoteDTO(final Integer id, final String version, final Date releaseDate) {
		this.id = id;
		this.version = version;
		this.releaseDate = releaseDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final Date releaseDate) {
		this.releaseDate = releaseDate;
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
