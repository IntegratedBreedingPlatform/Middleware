package org.generationcp.middleware.domain.germplasm.importation;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.util.CollectionUtils;

import java.util.List;

@AutoProperty
public class GermplasmMatchRequestDto {

	private List<String> germplasmUUIDs;

	private List<String> names;

	public List<String> getGermplasmUUIDs() {
		return germplasmUUIDs;
	}

	public void setGermplasmUUIDs(final List<String> germplasmUUIDs) {
		this.germplasmUUIDs = germplasmUUIDs;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(final List<String> names) {
		this.names = names;
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

	public boolean isValid() {
		if (CollectionUtils.isEmpty(germplasmUUIDs) && CollectionUtils.isEmpty(names)) {
			return false;
		}
		return true;
	}

}
