
package org.generationcp.middleware.service.pedigree;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.CrossExpansionProperties;

import com.google.common.base.Optional;

public class FixedLineNameResolver {

	private CrossExpansionProperties crossExpansionProperties;
	private PedigreeDataManagerFactory pedigreeDataManagerFactory;
	private String cropName;

	public FixedLineNameResolver(final CrossExpansionProperties crossExpansionProperties,
			final PedigreeDataManagerFactory pedigreeDataManagerFactory, final String cropName) {
		this.crossExpansionProperties = crossExpansionProperties;
		this.pedigreeDataManagerFactory = pedigreeDataManagerFactory;
		this.cropName = cropName;
	}

	Optional<String> nameTypeBasedResolution(final GermplasmNode germplasmNode) {
		final Germplasm germplasm;
		if(germplasmNode != null && (germplasm = germplasmNode.getGermplasm()) != null) {
			final Integer gid = germplasm.getGid();
			if (gid != null && gid > 0) {
				final List<Integer> nameTypeOrder = crossExpansionProperties.getNameTypeOrder(cropName);
				final List<Name> namesByGID =
						pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(gid, null, nameTypeOrder);
				for (final Integer integer : nameTypeOrder) {
					if (namesByGID != null) {
						for (final Name name : namesByGID) {
							if (integer.equals(name.getTypeId())) {
								return Optional.fromNullable(name.getNval());
							}
						}
					}
				}
			}
		}
		return Optional.fromNullable(null);

	}


	public CrossExpansionProperties getCrossExpansionProperties() {
		return crossExpansionProperties;
	}


	public void setCrossExpansionProperties(CrossExpansionProperties crossExpansionProperties) {
		this.crossExpansionProperties = crossExpansionProperties;
	}


	public String getCropName() {
		return cropName;
	}


	public void setCropName(String cropName) {
		this.cropName = cropName;
	}


}
