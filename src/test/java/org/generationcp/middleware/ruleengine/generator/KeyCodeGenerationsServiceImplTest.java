
package org.generationcp.middleware.ruleengine.generator;

import org.generationcp.middleware.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.middleware.ruleengine.service.KeyTemplateProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class KeyCodeGenerationsServiceImplTest {

	// Template with one optional component ([SELECTION_NUMBER])
	final KeyTemplateProvider oneOptionalComponentTemplateProvider = new KeyTemplateProvider() {

		@Override
		public String getKeyTemplate() {
			return "[LOCATION]-[PLOTNO][SELECTION_NUMBER]";
		}
	};

	// Template with all required components
	final KeyTemplateProvider allRequiredComponentTemplateProvider = new KeyTemplateProvider() {

		@Override
		public String getKeyTemplate() {
			return "[LOCATION]-[PLOTNO]-[SEASON]";
		}
	};

	final KeyComponentValueResolver locationValueResolver = new KeyComponentValueResolver() {

		@Override
		public String resolve() {
			return "INDIA";
		}

		@Override
		public boolean isOptional() {
			return false;
		}
	};

	final KeyComponentValueResolver plotNumberResolver = new KeyComponentValueResolver() {

		@Override
		public String resolve() {
			return "123";
		}

		@Override
		public boolean isOptional() {
			return false;
		}
	};

	final KeyComponentValueResolver seasonResolver = new KeyComponentValueResolver() {

		@Override
		public String resolve() {
			return "DrySeason";
		}

		@Override
		public boolean isOptional() {
			return false;
		}
	};

	final KeyComponentValueResolver optionalSelectionNumberWithValueResolver = new KeyComponentValueResolver() {

		@Override
		public String resolve() {
			return "Plant1";
		}

		@Override
		public boolean isOptional() {
			return true;
		}
	};

	final KeyComponentValueResolver optionalSelectionNumberWithoutValueResolver = new KeyComponentValueResolver() {

		@Override
		public String resolve() {
			return null;
		}

		@Override
		public boolean isOptional() {
			return true;
		}
	};

	@Test
	public void testGenerateKeyAllRequiredComponents() {

		KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.LOCATION, this.locationValueResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, this.plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SEASON, this.seasonResolver);

		String keyCode = service.generateKey(this.allRequiredComponentTemplateProvider, keyComponentValueResolvers);

		Assert.assertEquals("INDIA-123-DrySeason", keyCode);
	}

	@Test
	public void testGenerateKeyOneOptionalComponentWithValue() {

		KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.LOCATION, this.locationValueResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, this.plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, this.optionalSelectionNumberWithValueResolver);

		String keyCode = service.generateKey(this.oneOptionalComponentTemplateProvider, keyComponentValueResolvers);

		Assert.assertEquals("INDIA-123-Plant1", keyCode);
	}

	@Test
	public void testGenerateKeyOneOptionalComponentWithoutValue() {

		KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.LOCATION, this.locationValueResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, this.plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, this.optionalSelectionNumberWithoutValueResolver);

		String keyCode = service.generateKey(this.oneOptionalComponentTemplateProvider, keyComponentValueResolvers);

		Assert.assertEquals("INDIA-123", keyCode);
	}

}
