package org.generationcp.middleware.api.brapi.v2.germplasm;

import org.pojomatic.Pojomatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GermplasmUpdateRequest extends GermplasmImportRequest{

	public GermplasmUpdateRequest() {
		super();
	}

	public GermplasmUpdateRequest(final String accessionNumber, final String acquisitionDate, final String breedingMethodDbId,
		final String commonCropName,
		final String countryOfOriginCode, final String defaultDisplayName, final String genus, final String germplasmOrigin,
		final String instituteCode,
		final String instituteName, final String pedigree, final String seedSource, final String species, final String speciesAuthority,
		final String subtaxa, final String subtaxaAuthority) {
		super(accessionNumber, acquisitionDate, breedingMethodDbId, commonCropName, countryOfOriginCode, defaultDisplayName, genus,
			germplasmOrigin, instituteCode,
			instituteName, pedigree, seedSource, species, speciesAuthority, subtaxa, subtaxaAuthority);
	}


}
