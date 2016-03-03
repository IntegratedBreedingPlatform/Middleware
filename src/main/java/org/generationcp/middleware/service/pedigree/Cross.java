package org.generationcp.middleware.service.pedigree;


public class Cross implements BreedingMethodProcessor {

	final InbredProcessor inbredProcessor = new InbredProcessor();

	@Override
	public PedigreeString processGermplasmNode(GermplasmNode germplasmNode, Integer level, FixedLineNameResolver fixedLineNameResolver) {
		final PedigreeString femaleLeafPedigreeString =
				inbredProcessor.processGermplasmNode(germplasmNode.getFemaleParent(), level - 1, fixedLineNameResolver);
		final PedigreeString maleLeafPedigreeString =
				inbredProcessor.processGermplasmNode(germplasmNode.getMaleParent(), level - 1, fixedLineNameResolver);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleLeafPedigreeString, maleLeafPedigreeString));
		return pedigreeString;
	}

}
