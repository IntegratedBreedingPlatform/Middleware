
package org.generationcp.middleware.service.pedigree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;

public class PedigreeStringTestUtil {

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

	static GermplasmNode createGermplasmNode(final int germplasmNode, final String preferredName, final int methodId,
			final String methodName, final int numberOfProgenitor) {

		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(germplasmNode);

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

		return new GermplasmNode(germplasm);
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
		return createSingleCrossTestGermplasmNode(new ImmutablePair<Integer, String>(1, "A"), new ImmutablePair<Integer, String>(2, "B"),
				new ImmutablePair<Integer, String>(3, "C"));
	}
}
