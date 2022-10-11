
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
	public String generateKey(KeyTemplateProvider keyTemplateProvider, Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers) {

		String key = keyTemplateProvider.getKeyTemplate();
		Set<KeyComponent> keySet = keyComponentValueResolvers.keySet();
		Iterator<KeyComponent> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			KeyComponent keyComponent = iterator.next();

			Pattern pattern = Pattern.compile("(\\[" + keyComponent.name() + "\\])");
			Matcher matcher = pattern.matcher(key);

			KeyComponentValueResolver keyComponentValueResolver = keyComponentValueResolvers.get(keyComponent);
			String resolvedValue = keyComponentValueResolver.resolve();

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
