
package org.generationcp.middleware.service.pedigree;

import java.util.concurrent.ExecutionException;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class PedigreeTree {

	private final LoadingCache<GermplasmKey, Optional<Germplasm>> germplasmCache;
	private final LoadingCache<MethodKey, Optional<Method>> methodCache;
	private final String cropName;

	public PedigreeTree(final LoadingCache<GermplasmKey, Optional<Germplasm>> germplasmCache, final LoadingCache<MethodKey, Optional<Method>> methodCache,
			final String cropName) {
		this.germplasmCache = germplasmCache;
		this.methodCache = methodCache;
		this.cropName = cropName;
	}

	GermplasmNode buildPedigreeTree(final Integer gid) {
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
			final Optional<Germplasm> germplasm = this.germplasmCache.get(new GermplasmKey(this.cropName, gid));
			if(germplasm.isPresent()) {
				final GermplasmNode germplasmNode = new GermplasmNode(germplasm.get());
				final Optional<Method> method = this.methodCache.get(new MethodKey(this.cropName, germplasmNode.getGermplasm().getMethodId()));
				if(method.isPresent()) {
					germplasmNode.setMethod(method.get());
				}
				this.buildAncestoryGraph(germplasmNode);
				return germplasmNode;
			}

		}
		return null;
	}

	private void buildAncestoryGraph(final GermplasmNode germplasmNode) throws ExecutionException {
		Preconditions.checkNotNull(germplasmNode);
		final Germplasm rootGermplasm = germplasmNode.getGermplasm();
		germplasmNode.setFemaleParent(this.buildGermplasmNode(rootGermplasm.getGpid1()));
		germplasmNode.setMaleParent(this.buildGermplasmNode(rootGermplasm.getGpid2()));
	}

}
