
package org.generationcp.middleware.service.pedigree.string.util;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.service.pedigree.cache.keys.CropNameTypeKey;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;

import com.google.common.base.Optional;

public class FixedLineNameResolver {

	private CrossExpansionProperties crossExpansionProperties;

	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	private FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>> nameTypeBasedCache;

	private String cropName;

	public FixedLineNameResolver(final CrossExpansionProperties crossExpansionProperties,
			final PedigreeDataManagerFactory pedigreeDataManagerFactory,
			final FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>> nameTypeBasedCache,
			final String cropName) {
		this.crossExpansionProperties = crossExpansionProperties;
		this.pedigreeDataManagerFactory = pedigreeDataManagerFactory;
		this.nameTypeBasedCache = nameTypeBasedCache;
		this.cropName = cropName;
	}

	public Optional<String> nameTypeBasedResolution(final GermplasmNode germplasmNode) {
		final Germplasm germplasm;
		if (germplasmNode != null && (germplasm = germplasmNode.getGermplasm()) != null) {
			final Integer gid = germplasm.getGid();
			if (gid != null && gid > 0) {
				final Optional<List<Integer>> nameTypeOrder = nameTypeBasedCache.get(new CropNameTypeKey(crossExpansionProperties.getNameTypeOrder(cropName), cropName));
				if(nameTypeOrder.isPresent()) {
					final List<Name> namesByGID =
							pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(gid, null, nameTypeOrder.get());
					for (final Integer nameType : nameTypeOrder.get()) {
						if (namesByGID != null) {
							for (final Name name : namesByGID) {
								if (name.getTypeId().equals(nameType)) {
									return Optional.fromNullable(name.getNval());
								}
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
