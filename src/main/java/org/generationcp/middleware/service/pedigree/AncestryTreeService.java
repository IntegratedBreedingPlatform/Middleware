
package org.generationcp.middleware.service.pedigree;

import java.util.concurrent.ExecutionException;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.service.pedigree.cache.keys.CropMethodKey;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class AncestryTreeService {

	private final FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCache;
	private final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCache;
	private final String cropName;

	public AncestryTreeService(final FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCropBasedCache, final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCropBasedCache,
			final String cropName) {
		this.germplasmCache = germplasmCropBasedCache;
		this.methodCache = methodCropBasedCache;
		this.cropName = cropName;
	}

	GermplasmNode buildAncestryTree(final Integer gid) {
		Preconditions.checkNotNull(gid);

		try {
			final GermplasmNode rootGermplasmNode = this.buildGermplasmNode(gid);
			// post condition check
			Preconditions.checkNotNull(rootGermplasmNode);
			return rootGermplasmNode;
		} catch (final ExecutionException e) {
			throw new MiddlewareQueryException("Unable to create pedigree string", e);
		}
	}

	private GermplasmNode buildGermplasmNode(final Integer gid) throws ExecutionException {
		if (gid != null && gid > 0) {
			final Optional<Germplasm> germplasm = this.germplasmCache.get(new CropGermplasmKey(this.cropName, gid));
			if(germplasm.isPresent()) {
				final GermplasmNode germplasmNode = new GermplasmNode(germplasm.get());
				final Optional<Method> method = this.methodCache.get(new CropMethodKey(this.cropName, germplasmNode.getGermplasm().getMethodId()));
				if(method.isPresent()) {
					germplasmNode.setMethod(method.get());
				}
				this.buildAncestoryTree(germplasmNode);
				return germplasmNode;
			}

		}
		return null;
	}

	private void buildAncestoryTree(final GermplasmNode germplasmNode) throws ExecutionException {
		Preconditions.checkNotNull(germplasmNode);
		final Germplasm rootGermplasm = germplasmNode.getGermplasm();
		germplasmNode.setFemaleParent(this.buildGermplasmNode(rootGermplasm.getGpid1()));
		germplasmNode.setMaleParent(this.buildGermplasmNode(rootGermplasm.getGpid2()));
	}

}
