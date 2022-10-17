package org.generationcp.middleware.ruleengine.generator;

import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.junit.Assert;
import org.junit.Test;

public class BreedersCrossIDTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();

		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]:[LOCATION]");

		// Study
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties).getKeyTemplate());

		// Study
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties).getKeyTemplate());
	}
}
