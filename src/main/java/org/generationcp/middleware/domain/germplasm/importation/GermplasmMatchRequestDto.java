package org.generationcp.middleware.domain.germplasm.importation;

import com.google.common.collect.Lists;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.util.CollectionUtils;

import java.util.List;

@AutoProperty
public class GermplasmMatchRequestDto {

	private List<String> germplasmPUIs = Lists.newArrayList();

	private List<String> germplasmUUIDs = Lists.newArrayList();

	private List<String> names = Lists.newArrayList();

	private List<Integer> gids = Lists.newArrayList();

	public List<String> getGermplasmPUIs() {
		return this.germplasmPUIs;
	}

	public void setGermplasmPUIs(final List<String> germplasmPUIs) {
		this.germplasmPUIs = germplasmPUIs;
	}

	public List<String> getNames() {
		return this.names;
	}

	public void setNames(final List<String> names) {
		this.names = names;
	}

	public List<String> getGermplasmUUIDs() {
		return this.germplasmUUIDs;
	}

	public void setGermplasmUUIDs(final List<String> germplasmUUIDs) {
		this.germplasmUUIDs = germplasmUUIDs;
	}

	public List<Integer> getGids() {
		return this.gids;
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

	public boolean isValid() {
		if (CollectionUtils.isEmpty(this.germplasmPUIs) && CollectionUtils.isEmpty(this.germplasmUUIDs) && CollectionUtils.isEmpty(this.gids) && CollectionUtils.isEmpty(this.names)) {
			return false;
		}
		return true;
	}

}
