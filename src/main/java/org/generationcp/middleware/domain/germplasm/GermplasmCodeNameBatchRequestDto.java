package org.generationcp.middleware.domain.germplasm;

import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmCodeNameBatchRequestDto {

	private List<Integer> gids;
	private String nameType;
	private GermplasmNameSetting germplasmCodeNameSetting;

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

	public GermplasmNameSetting getGermplasmCodeNameSetting() {
		return this.germplasmCodeNameSetting;
	}

	public void setGermplasmCodeNameSetting(final GermplasmNameSetting germplasmCodeNameSetting) {
		this.germplasmCodeNameSetting = germplasmCodeNameSetting;
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
