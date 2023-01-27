package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CrossTypeExpression extends BaseExpression {

	public static final String KEY = "[CRSTYP]";

	private static final String SINGLE_CROSS = "Single cross";
	private static final String DOUBLE_CROSS = "Double cross";
	private static final String TOP_CROSS_MAIZE = "Test cross";
	private static final String TOP_CROSS_WHEAT = "Three-way cross";
	private static final String BACK_CROSS = "Backcross";

	public static final String WHEAT = "wheat";
	public static final String MAIZE = "maize";

	@Autowired
	private GermplasmService germplasmService;

	public CrossTypeExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		String crossTypeAbbreviation = "";
		final Method breedingMethod = advancingSource.getBreedingMethod();

		if (breedingMethod.getMname().equals(SINGLE_CROSS)) {
			crossTypeAbbreviation = "S";
		} else if (breedingMethod.getMname().equals(DOUBLE_CROSS)) {
			crossTypeAbbreviation = "D";
		} else if (breedingMethod.getMname().equals(BACK_CROSS)) {
			crossTypeAbbreviation = getRecurrentParentType(advancingSource.getOriginGermplasm());
		} else if (this.isTopCrossMethod(breedingMethod)) {
			crossTypeAbbreviation = "T";
		}

		for (final StringBuilder container : values) {

			this.replaceExpressionWithValue(container, crossTypeAbbreviation);
		}
	}

	private String getRecurrentParentType(final BasicGermplasmDTO originGermplasm) {

		if (originGermplasm.getGpid1() == null || originGermplasm.getGpid2() == null) {
			return "";
		}

		final Set<Integer> parentGids = new HashSet<>();
		parentGids.add(originGermplasm.getGpid1());
		parentGids.add(originGermplasm.getGpid2());
		final List<BasicGermplasmDTO> parentGermplasmsByGids = this.germplasmService.getBasicGermplasmByGids(parentGids);

		final BasicGermplasmDTO femaleParent = parentGermplasmsByGids.get(originGermplasm.getGpid1());
		final BasicGermplasmDTO maleParent = parentGermplasmsByGids.get(originGermplasm.getGpid2());

		if (maleParent.getGnpgs() >= 2
			&& (femaleParent.getGid().equals(maleParent.getGpid1()) || femaleParent.getGid().equals(maleParent.getGpid2()))) {

			return "F";
		} else if (femaleParent.getGnpgs() >= 2
			&& (maleParent.getGid().equals(femaleParent.getGpid1()) || maleParent.getGid().equals(femaleParent.getGpid2()))) {
			return "M";
		}

		return "";
	}

	private boolean isTopCrossMethod(final Method breedingMethod) {
		final String cropName = ContextHolder.getCurrentCrop();
		return cropName.equalsIgnoreCase(WHEAT) && breedingMethod.getMname().equals(TOP_CROSS_WHEAT)
			|| cropName.equalsIgnoreCase(MAIZE) && breedingMethod.getMname().equals(TOP_CROSS_MAIZE);
	}

	@Override
	public String getExpressionKey() {
		return CrossTypeExpression.KEY;
	}
}
