package org.generationcp.middleware.ruleengine.generator;

import org.generationcp.middleware.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.middleware.ruleengine.service.KeyTemplateProvider;

import java.util.Map;

/**
 * Common interface to generate "key codes". Key codes are essentially business keys and are usually constructed from real world entities
 * and events e.g. Germplasm, Nurseries, Locations, Planting Seasons, Plot Numbers, Breeding Program Codes, People etc.
 * 
 */
public interface KeyCodeGenerationService {

	/**
	 * Generate key code. First calls the supplied template provider to locate the template. Then replaces each placeholder within the
	 * template with a value by calling the corresponding key component value resolver.
	 * 
	 * @param keyTemplateProvider - Implementation of a {@link KeyTemplateProvider} that knows how to locate the template for the key. This
	 *        parameters must not be {@code null}.
	 * @param keyComponentValueResolvers - Registrty map of {@link KeyComponentValueResolver}'s that know how to resolve values for each of
	 *        the {@link KeyComponent} place holder present in the template. It is responsibility of the clients of the service to ensure
	 *        that each placeholder in the template has a corresponding value resolver, the service does not validate this at the moment. If
	 *        value resolvers are missing for some placeholders in the template, those placeholders will come out as it is in template. This
	 *        parameter must not be {@code null}.
	 * @return The generated key code.
	 */
	String generateKey(KeyTemplateProvider keyTemplateProvider, Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers);
}
