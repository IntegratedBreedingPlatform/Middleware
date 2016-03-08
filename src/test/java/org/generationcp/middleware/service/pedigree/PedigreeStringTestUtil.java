
package org.generationcp.middleware.service.pedigree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;

public class PedigreeStringTestUtil {

	static final String BACKCROSS_METHOD_NAME = "Backcross";
	static final int BACKCROSS_METHOD_ID = 107;
	static final int SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR = 2;
	static final int BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR = -1;

	static final String BULK_OR_POPULATION_SAMPLE_METHOD_NAME = "Bulk or population sample";
	static final int BULK_OR_POPULATION_SAMPLE_METHOD_ID = 923;
	static final String SINGLE_CROSS_METHOD_NAME = "Single Cross";
	static final int SINGLE_CROSS_METHOD_ID = 101;
	public static final int THREE_WAY_CROSS_METHOD_ID = 102;
	public static final String THREE_WAY_CROSS_METHOD_ID_METHOD_NAME = "Three-way cross";
	public static final int THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR = 2;
	public static final int DOUBLE_CROSS_METHOD_ID = 103;
	public static final int DOUBLE_CROSS_METHOD_NUMBER_OF_PROGENITOR = 2;
	public static final String DOUBLE_CROSS_METHOD_NAME = "Double cross";

	static GermplasmNode createGermplasmNode(final int germplasmId, final String preferredName, final int methodId,
			final String methodName, final int numberOfProgenitor) {

		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(germplasmId);

		germplasm.setGnpgs(numberOfProgenitor);
		final Method method = new Method(methodId);
		germplasm.setMethod(method);
		germplasm.setMethodId(methodId);
		method.setMname(methodName);

		if (StringUtils.isNotBlank(preferredName)) {
			final Name prefName = new Name(1);
			prefName.setNval(preferredName);
			germplasm.setPreferredName(prefName);
		}

		final GermplasmNode germplasmNode = new GermplasmNode(germplasm);
		germplasmNode.setMethod(method);
		return germplasmNode;
	}

	static GermplasmNode createSingleCrossTestGermplasmNode(final ImmutablePair<Integer, String> parent,
			final ImmutablePair<Integer, String> female, final ImmutablePair<Integer, String> male) {

		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(parent.getLeft(), parent.getRight(),
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		parentGermplasmNode.setFemaleParent(PedigreeStringTestUtil.createGermplasmNode(female.getLeft(), female.getRight(),
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID, PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR));
		parentGermplasmNode.setMaleParent(PedigreeStringTestUtil.createGermplasmNode(male.getLeft(), male.getRight(),
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID, PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR));
		return parentGermplasmNode;

	}

	static GermplasmNode createSingleCrossTestGermplasmNode() {
		return PedigreeStringTestUtil.createSingleCrossTestGermplasmNode(new ImmutablePair<Integer, String>(1, "A"),
				new ImmutablePair<Integer, String>(2, "B"), new ImmutablePair<Integer, String>(3, "C"));
	}

	static GermplasmNode createBackCrossTestGermplasmNode(final String donorParentName, final String recurringParentName,
			final int numberOfTimeToCross, final boolean femaleDonorParent) {
		final GermplasmNode rootGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(100, "RootBackcrossNode", PedigreeStringTestUtil.BACKCROSS_METHOD_ID,
						PedigreeStringTestUtil.BACKCROSS_METHOD_NAME, 2);
		final GermplasmNode donorParent =
				PedigreeStringTestUtil.createSingleCrossTestGermplasmNode(new ImmutablePair<Integer, String>(1, donorParentName),
						new ImmutablePair<Integer, String>(2, "B"), new ImmutablePair<Integer, String>(3, "C"));
		final GermplasmNode recurringParent =
				PedigreeStringTestUtil.createGermplasmNode(4, recurringParentName,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR);

		PedigreeStringTestUtil.createBackCrossTestGermplasmNode(rootGermplasmNode, donorParent, recurringParent, numberOfTimeToCross,
				femaleDonorParent);
		return rootGermplasmNode;
	}

	private static void createBackCrossTestGermplasmNode(final GermplasmNode rootNode, final GermplasmNode donorParent,
			final GermplasmNode recurringParent, final int numberOfTimeToCross, final boolean femaleDonorParent) {

		if (numberOfTimeToCross == 0) {
			setNodeParents(rootNode, donorParent, recurringParent, femaleDonorParent);
			return;
		}

		final GermplasmNode backcrossNode =
				PedigreeStringTestUtil.createGermplasmNode(100 + numberOfTimeToCross, "BC" + numberOfTimeToCross,
						PedigreeStringTestUtil.BACKCROSS_METHOD_ID, PedigreeStringTestUtil.BACKCROSS_METHOD_NAME, 2);
		setNodeParents(rootNode, backcrossNode, recurringParent, femaleDonorParent);

		PedigreeStringTestUtil.createBackCrossTestGermplasmNode(backcrossNode, donorParent, recurringParent, numberOfTimeToCross - 1,
				femaleDonorParent);

	}

	private static void setNodeParents(final GermplasmNode rootNode, final GermplasmNode donorParent, final GermplasmNode recurringParent,
			final boolean femaleDonorParent) {
		if (femaleDonorParent) {
			setNodeParents(rootNode, donorParent, recurringParent);
		} else {
			setNodeParents(rootNode, recurringParent, donorParent);
		}
	}

	private static void setNodeParents(final GermplasmNode rootNode, final GermplasmNode femaleParent, final GermplasmNode maleParent) {
		rootNode.setFemaleParent(femaleParent);
		rootNode.setMaleParent(maleParent);
		rootNode.getGermplasm().setGpid1(femaleParent.getGermplasm().getGid());
		rootNode.getGermplasm().setGpid2(maleParent.getGermplasm().getGid());
	}
}
