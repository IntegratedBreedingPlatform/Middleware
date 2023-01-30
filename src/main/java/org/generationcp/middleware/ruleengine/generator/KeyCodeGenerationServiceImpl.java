
package org.generationcp.middleware.ruleengine.generator;

import com.google.common.base.Strings;
import org.generationcp.middleware.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.middleware.ruleengine.service.KeyTemplateProvider;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCodeGenerationServiceImpl implements KeyCodeGenerationService {


	@Override
	public String generateKey(final KeyTemplateProvider keyTemplateProvider,
		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers) {

		String key = keyTemplateProvider.getKeyTemplate();
		final Set<KeyComponent> keySet = keyComponentValueResolvers.keySet();
		final Iterator<KeyComponent> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			final KeyComponent keyComponent = iterator.next();

			final Pattern pattern = Pattern.compile("(\\[" + keyComponent.name() + "\\])");
			final Matcher matcher = pattern.matcher(key);

			final KeyComponentValueResolver keyComponentValueResolver = keyComponentValueResolvers.get(keyComponent);
			final String resolvedValue = keyComponentValueResolver.resolve();

			if (!keyComponentValueResolver.isOptional()) {
				key = matcher.replaceAll(Strings.nullToEmpty(resolvedValue));
			} else {
				if (!Strings.isNullOrEmpty(resolvedValue)) {
					key = matcher.replaceAll("-" + resolvedValue);
				} else {
					key = matcher.replaceAll("");
				}
			}
		}
		return key;
	}

}
