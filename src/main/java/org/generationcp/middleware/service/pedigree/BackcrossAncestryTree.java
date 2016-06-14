
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.service.pedigree.cache.keys.CropMethodKey;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;

import com.google.common.base.Optional;

public class BackcrossAncestryTree {

	private final FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCache;
	private final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCache;
	private final String cropName;
	private final AncestryTreeService ancestryTreeService;

	public BackcrossAncestryTree(final FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCropBasedCache,
			final FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCropBasedCache, final String cropName) {
		this.germplasmCache = germplasmCropBasedCache;
		this.methodCache = methodCropBasedCache;
		this.cropName = cropName;
		this.ancestryTreeService = new AncestryTreeService(this.germplasmCache, this.methodCache, cropName);

	}

	public GermplasmNode generateBackcrossAncestryTree(final Germplasm germplasm, final int level) {

		final Integer femaleParent = germplasm.getGpid1();
		final Integer maleParent = germplasm.getGpid2();

		// In order to have a backcross we must have two parents
		if (femaleParent != null && maleParent != null && femaleParent > 0 && maleParent > 0) {
			// Establish recurring parent
			final Optional<Germplasm> recurringParent = this.findRecurringParent(femaleParent, maleParent);
			if (recurringParent.isPresent()) {
				final Integer recurringParentId = recurringParent.get().getGid();
				final GermplasmNode recurringParentNode = this.ancestryTreeService.buildAncestryTree(recurringParentId, level - 1);
				return this.generateBackcrossTree(recurringParentId, germplasm, recurringParentNode, level);
			}
		}

		final GermplasmNode rootGermplasm = new GermplasmNode(germplasm);
		this.getMethodName(germplasm, rootGermplasm);

		// TODO: Did not understand why this was done in the original algorithm.
		if (germplasm.getGpid1() > 0) {
			rootGermplasm.setFemaleParent(this.ancestryTreeService.buildAncestryTree(germplasm.getGpid1(), level - 1));
		}

		if (germplasm.getGpid2() > 0) {
			rootGermplasm.setMaleParent(this.ancestryTreeService.buildAncestryTree(germplasm.getGpid2(), level - 1));
		}

		return rootGermplasm;

	}

	//TODO you do not need the recurringParentGid
	private GermplasmNode generateBackcrossTree(final Integer recurringParentGid, final Germplasm germplasm,
			final GermplasmNode recurringParentNode, final int level) {

		final GermplasmNode germplasmNode = new GermplasmNode(germplasm);

		this.getMethodName(germplasm, germplasmNode);

		final Integer otherParentFemaleParentGid = germplasm.getGpid1();
		final Integer otherParentMaleParentGid = germplasm.getGpid2();
		// Donor parent is found
		if (otherParentFemaleParentGid != null && !otherParentFemaleParentGid.equals(recurringParentGid) && otherParentMaleParentGid != null
				&& !otherParentMaleParentGid.equals(recurringParentGid)) {
			// Build the ancestry tree for the donor parent
			return this.ancestryTreeService.buildAncestryTree(germplasm.getGid(), level - 1);
		}

		// Male traversal
		if (otherParentFemaleParentGid != null && otherParentFemaleParentGid.equals(recurringParentGid)) {
			final Germplasm maleGermplasm = this.germplasmCache.get(new CropGermplasmKey(this.cropName, otherParentMaleParentGid)).get();
			GermplasmNode generateBackcrossTree = this.generateBackcrossTree(recurringParentGid, maleGermplasm, recurringParentNode, level);
			germplasmNode.setFemaleParent(recurringParentNode);
			germplasmNode.setMaleParent(generateBackcrossTree);
		}

		// Female Traversal
		if (otherParentMaleParentGid != null && otherParentMaleParentGid.equals(recurringParentGid)) {
			final Germplasm female = this.germplasmCache.get(new CropGermplasmKey(this.cropName, otherParentFemaleParentGid)).get();
			GermplasmNode generateBackcrossTree = this.generateBackcrossTree(recurringParentGid, female, recurringParentNode, level);
			germplasmNode.setFemaleParent(generateBackcrossTree);
			germplasmNode.setMaleParent(recurringParentNode);
		}

		return germplasmNode;
	}

	private void getMethodName(final Germplasm germplasm, final GermplasmNode germplasmNode) {
		final Optional<Method> method = this.methodCache.get(new CropMethodKey(this.cropName, germplasm.getMethodId()));
		if (method.isPresent()) {
			germplasmNode.setMethod(method.get());
		}
	}

	/**
	 * @param femaleParent female parent
	 * @param maleParent male parent
	 * @return {@link Optional<GermplasmNode>} which is or recurring parent. If the optional call is empty then no recurring parent was
	 *         found.
	 */
	private Optional<Germplasm> findRecurringParent(final Integer femaleParent, final Integer maleParent) {
		final Germplasm femaleParentGermplasm = this.germplasmCache.get(new CropGermplasmKey(this.cropName, femaleParent)).get();
		final Germplasm maleParentGermplasm = this.germplasmCache.get(new CropGermplasmKey(this.cropName, maleParent)).get();
		Germplasm recurringParent = null;
		if (maleParentGermplasm.getGnpgs() >= 2 && (femaleParentGermplasm.getGid().equals(maleParentGermplasm.getGpid1())
				|| femaleParentGermplasm.getGid().equals(maleParentGermplasm.getGpid2()))) {

			recurringParent = femaleParentGermplasm;
		} else if (femaleParentGermplasm.getGnpgs() >= 2 && (maleParentGermplasm.getGid().equals(femaleParentGermplasm.getGpid1())
				|| maleParentGermplasm.getGid().equals(femaleParentGermplasm.getGpid2()))) {
			recurringParent = maleParentGermplasm;
		}
		return Optional.fromNullable(recurringParent);
	}

}
