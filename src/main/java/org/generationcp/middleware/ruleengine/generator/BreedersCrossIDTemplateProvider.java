package org.generationcp.middleware.ruleengine.generator;

import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.ruleengine.service.KeyTemplateProvider;

public class BreedersCrossIDTemplateProvider implements KeyTemplateProvider {

	private final GermplasmNamingProperties germplasmNamingProperties;

	public BreedersCrossIDTemplateProvider(final GermplasmNamingProperties germplasmNamingProperties){
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

	@Override
	public String getKeyTemplate() {
		String breedersCrossIDTemplate = "";
		breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDStudy();
		return breedersCrossIDTemplate;
	}
}
