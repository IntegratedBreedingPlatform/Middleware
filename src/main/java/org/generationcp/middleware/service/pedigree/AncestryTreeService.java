
package org.generationcp.middleware.service.pedigree;

import java.util.concurrent.ExecutionException;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.service.pedigree.cache.keys.CropMethodKey;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class AncestryTreeService {

	/**
	 * This cache is filled by a stored procedure that retrieves germplasm and its ancestry tree.
	 */
	private final GermplasmCache germplasmAncestryCache;
	
	/**
	 * Cache from which we can fetch breeding methods. We do not expect this cache to be too large.
	 */
	private final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> breedingMethodCache;
	private final String cropName;

	public AncestryTreeService(final GermplasmCache germplasmAncestryCache, final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCropBasedCache,
			final String cropName) {
		this.germplasmAncestryCache = germplasmAncestryCache;
		this.breedingMethodCache = methodCropBasedCache;
		this.cropName = cropName;
	}

	GermplasmNode buildAncestryTree(final Integer gid, final int level) {
		
		Preconditions.checkNotNull(gid);
		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.service.pedigree.AncestryTreeService.buildAncestryTree(Integer, int)");

		try {
			final GermplasmNode rootGermplasmNode = this.buildGermplasmNode(gid, level);
			// post condition check
			Preconditions.checkNotNull(rootGermplasmNode);
			return rootGermplasmNode;
		} catch (final ExecutionException e) {
			throw new MiddlewareQueryException("Unable to create pedigree string", e);
		} finally {
			monitor.stop();
		}
	}

	private GermplasmNode buildGermplasmNode(final Integer gid, final int level) throws ExecutionException {
		if (gid != null && gid > 0) {
			final Optional<Germplasm> germplasm = this.germplasmAncestryCache.getGermplasm(new CropGermplasmKey(this.cropName, gid));
			if(germplasm.isPresent()) {
				final GermplasmNode germplasmNode = new GermplasmNode(germplasm.get());
				final Optional<Method> method = this.breedingMethodCache.get(new CropMethodKey(this.cropName, germplasmNode.getGermplasm().getMethod().getMid()));
				if(method.isPresent()) {
					germplasmNode.setMethod(method.get());
				}
				final String mname = method.get().getMname();
				if(StringUtils.isNotBlank(mname) && mname.toLowerCase().contains("backcross")) {
					final BackcrossAncestryTree backcrossAncestryTree = new BackcrossAncestryTree(
						this.germplasmAncestryCache,
						this.breedingMethodCache,
						this.cropName);
					return backcrossAncestryTree.generateBackcrossAncestryTree(germplasm.get(), level);
				}
				this.buildAncestoryTree(germplasmNode, level);
				return germplasmNode;
			}

		}
		return null;
	}

	private void buildAncestoryTree(final GermplasmNode germplasmNode, final int level) throws ExecutionException {

		Preconditions.checkNotNull(germplasmNode);
		
		// If we have reached a negative level time to stop traversing the tree
		if(level < 0) {
			return;
		}

		// 
		final Germplasm rootGermplasm = germplasmNode.getGermplasm();
		
		// If a generative germplasm, decrease level and traverse
		if(rootGermplasm.getGnpgs() > 0) {
			germplasmNode.setFemaleParent(this.buildGermplasmNode(rootGermplasm.getGpid1(), level - 1));
		} else {
		// Do not decrease level for derivative germplasm	
			germplasmNode.setFemaleParent(this.buildGermplasmNode(rootGermplasm.getGpid1(), level));
		}
		
		// Male germplasm to be traversed normally according to level
		germplasmNode.setMaleParent(this.buildGermplasmNode(rootGermplasm.getGpid2(), level - 1));
		
	}

}
