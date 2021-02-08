package org.generationcp.middleware.domain.germplasm.importation;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmImportResponseDto {

	public enum Status {
		FOUND, CREATED
	}

	public GermplasmImportResponseDto() {
	}

	public GermplasmImportResponseDto(final Status status, final List<Integer> gids) {
		this.status = status;
		this.gids = gids;
	}

	private Status status;
	private List<Integer> gids;

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public List<Integer> getGids() {
		return gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
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
