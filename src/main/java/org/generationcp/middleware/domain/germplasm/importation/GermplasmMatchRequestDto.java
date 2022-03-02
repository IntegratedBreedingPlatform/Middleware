package org.generationcp.middleware.domain.germplasm.importation;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
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
	private SqlTextFilter locationName;
	private SqlTextFilter locationAbbreviation;
	private List<String> methods;
	private List<String> nameTypes;

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

	public SqlTextFilter getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final SqlTextFilter locationName) {
		this.locationName = locationName;
	}

	public SqlTextFilter getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final SqlTextFilter locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public List<String> getMethods() {
		return this.methods;
	}

	public void setMethods(final List<String> methods) {
		this.methods = methods;
	}

	public List<String> getNameTypes() {
		return this.nameTypes;
	}

	public void setNameTypes(final List<String> nameTypes) {
		this.nameTypes = nameTypes;
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
		// GID, GUUID, PUI and NAMES are the MAIN match parameters; hence lack of any of these renders request invalid
		// Location, Name type and method will merely restrict any initial matches from the main filters
		if (CollectionUtils.isEmpty(this.germplasmPUIs) && CollectionUtils.isEmpty(this.germplasmUUIDs) && CollectionUtils
			.isEmpty(this.gids) && CollectionUtils.isEmpty(this.names)) {
			return false;
		}
		return true;
	}

	public boolean restrictingFiltersSpecified() {
		// Location and method filters will merely restrict any initial matches from the main filters
		return this.locationAbbreviation != null || this.locationName != null || !CollectionUtils.isEmpty(this.methods);
	}



}
