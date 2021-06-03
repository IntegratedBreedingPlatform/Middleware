package org.generationcp.middleware.domain.germplasm;

import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmNameBatchRequestDto {

	private List<Integer> gids;
	private String nameType;
	private GermplasmNameSetting germplasmNameSetting;

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public String getNameType() {
		return this.nameType;
	}

	public void setNameType(final String nameType) {
		this.nameType = nameType;
	}

	public GermplasmNameSetting getGermplasmNameSetting() {
		return this.germplasmNameSetting;
	}

	public void setGermplasmNameSetting(final GermplasmNameSetting germplasmNameSetting) {
		this.germplasmNameSetting = germplasmNameSetting;
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
