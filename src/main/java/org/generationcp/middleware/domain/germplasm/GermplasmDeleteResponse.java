package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmDeleteResponse {

	private Set<Integer> deletedGermplasm;
	private Set<Integer> germplasmWithErrors;

	public GermplasmDeleteResponse(final Set<Integer> germplasmWithErrors, final Set<Integer> deletedGermplasm) {
		this.germplasmWithErrors = germplasmWithErrors;
		this.deletedGermplasm = deletedGermplasm;
	}

	public Set<Integer> getDeletedGermplasm() {
		return this.deletedGermplasm;
	}

	public void setDeletedGermplasm(final Set<Integer> deletedGermplasm) {
		this.deletedGermplasm = deletedGermplasm;
	}

	public Set<Integer> getGermplasmWithErrors() {
		return this.germplasmWithErrors;
	}

	public void setGermplasmWithErrors(final Set<Integer> germplasmWithErrors) {
		this.germplasmWithErrors = germplasmWithErrors;
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
