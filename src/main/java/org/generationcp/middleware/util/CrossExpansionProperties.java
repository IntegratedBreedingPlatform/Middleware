
package org.generationcp.middleware.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CrossExpansionProperties {

	private static final String GENERATION_LEVEL = "generation.level";
	private static final String NAME_TYPE_ORDER = "nametype.order";
	private static final String BACKCROSS_NOTATION_FEMALE = "backcross.notation.female";
	private static final String BACKCROSS_NOTATION_MALE = "backcross.notation.male";

	private String profile;
	private int defaultLevel;
	private Properties props;

	private static final Logger LOG = LoggerFactory.getLogger(CrossExpansionProperties.class);

	// FIXME Caching is redundant. Please remove
	/**
	 * This cache will work because {@link CrossExpansionProperties} is/must be configured as a singleton in Spring
	 */
	private LoadingCache<String, List<String>> nameTypeOrderCache;

	/**
	 * This cache will work because {@link CrossExpansionProperties} is/must be configured as a singleton in Spring
	 */
	private LoadingCache<String, Integer> generationLevelCache;

	/**
	 * This cache will work because {@link CrossExpansionProperties} is/must be configured as a singleton in Spring
	 */
	private LoadingCache<String, ImmutablePair<String, String>> backcrossNotationCache;

	private Set<Integer> hybridBreedingMethods = new TreeSet<Integer>();

	public CrossExpansionProperties() {
		try {
			final Resource resource = new ClassPathResource("crossing.properties");
			this.props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (final IOException e) {
			final String errorMessage = "Unable to access crossing.properties. Please contact your administrator for further assistance.";
			CrossExpansionProperties.LOG.error(errorMessage, e);
			throw new MiddlewareException(errorMessage, e);
		}
		this.init();
	}

	public CrossExpansionProperties(final Properties props) {
		this.props = props;
		this.init();
	}

	private void init() {
		this.nameTypeOrderCache =
				CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES)
						.build(new CacheLoader<String, List<String>>() {

							@Override
							public List<String> load(final String cropName) {
								return CrossExpansionProperties.this.getNameTypeStoppingRulesByCrop(cropName);
							}

						});

		this.generationLevelCache =
				CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES)
						.build(new CacheLoader<String, Integer>() {

							@Override
							public Integer load(final String cropName) {
								return CrossExpansionProperties.this.getGenerationLevelStoppingRulesByCrop(cropName);
							}

						});

		this.backcrossNotationCache =
				CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES)
						.build(new CacheLoader<String, ImmutablePair<String, String>>() {

							@Override
							public ImmutablePair<String, String> load(final String cropName) {
								return CrossExpansionProperties.this.getBackcrossNotationByCrop(cropName);
							}
						});

	}

	private int getGenerationLevelStoppingRulesByCrop(final String cropName) {
		// FIXME Delivering properties without values is not a good smell.
		final String propertyValue = this.props.getProperty(cropName + "." + CrossExpansionProperties.GENERATION_LEVEL);
		if (StringUtils.isNotBlank(propertyValue)) {
			try {
				return Integer.parseInt(propertyValue.trim());
			} catch (final Exception e) {
				final String errorMessage =
						String.format(cropName + "." + CrossExpansionProperties.GENERATION_LEVEL
								+ " is configured incorrectly with a value of '%s'. "
								+ "Please contact your administrator for further assistance", propertyValue);
				CrossExpansionProperties.LOG.error(errorMessage);
				throw new MiddlewareException(errorMessage, e);
			}
		}
		return this.getDefaultLevel();
	}

	private ImmutablePair<String, String> getBackcrossNotationByCrop(final String cropName) {
		final String backcrossNotationFemaleProperty = cropName + "." + CrossExpansionProperties.BACKCROSS_NOTATION_FEMALE;
		final String backcrossNotationMaleProperty = cropName + "." + CrossExpansionProperties.BACKCROSS_NOTATION_MALE;

		final String femalePropertyValue = this.props.getProperty(backcrossNotationFemaleProperty);
		final String malePropertyValue = this.props.getProperty(backcrossNotationMaleProperty);

		if (StringUtils.isNotBlank(femalePropertyValue) && StringUtils.isNotBlank(malePropertyValue)) {
			return new ImmutablePair<String, String>(femalePropertyValue.trim(), malePropertyValue.trim());
			// If one is blank
		} else if (StringUtils.isNotBlank(femalePropertyValue) || StringUtils.isNotBlank(malePropertyValue)) {
			throw new MiddlewareException(String.format(
					"In correct configuration. Please note if you do configure a custom backcross notation you must specify both the"
							+ " male and female backcross notation. Please make sure %s and %s have been correctly configured.",
					backcrossNotationFemaleProperty, backcrossNotationMaleProperty));
		}
		return new ImmutablePair<String, String>("*", "*");
	}

	private List<String> getNameTypeStoppingRulesByCrop(final String cropName) {
		final String propertyValue =
				CrossExpansionProperties.this.props.getProperty(cropName + "." + CrossExpansionProperties.NAME_TYPE_ORDER);
		final List<String> codes = new ArrayList<String>();

		if (StringUtils.isNotBlank(propertyValue)) {
			final String[] stringCodes = propertyValue.trim().split(",");
			for (final String code : stringCodes) {
				if (StringUtils.isNotBlank(code)) {
					codes.add(code.trim());
				}
			}

		}
		return codes;
	}

	public List<String> getNameTypeOrder(final String cropName) {
		try {
			return this.nameTypeOrderCache.get(cropName);
		} catch (final ExecutionException e) {
			CrossExpansionProperties.LOG.error(
					"Unable to initialise nameTypeOrderCache. Please contact your administrator for further assistance.", e);
			throw new MiddlewareException(
					"Unable to initialise nameTypeOrderCache. Please contact your administrator for further assistance.", e);
		}
	}

	public int getCropGenerationLevel(final String cropName) {

		try {
			return this.generationLevelCache.get(cropName);
		} catch (final ExecutionException e) {
			CrossExpansionProperties.LOG.error(
					"Unable to initialise generationLevelCach. Please contact your administrator for further assistance.", e);
			throw new MiddlewareException(
					"Unable to initialise generationLevelCach. Please contact your administrator for further assistance.", e);
		}
	}

	public ImmutablePair<String, String> getBackcrossNotation(final String cropName) {

		try {
			return this.backcrossNotationCache.get(cropName);
		} catch (final ExecutionException e) {
			CrossExpansionProperties.LOG.error(
					"Unable to initialise backcrossNotationCache. Please contact your administrator for further assistance.", e);
			throw new MiddlewareException(
					"Unable to initialise backcrossNotationCache. Please contact your administrator for further assistance.", e);
		}
	}

	public int getDefaultLevel() {
		return this.defaultLevel;
	}

	public void setDefaultLevel(final int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(final String profile) {
		this.profile = profile;
	}

	public Set<Integer> getHybridBreedingMethods() {
		return this.hybridBreedingMethods;
	}

	public void setHybridBreedingMethods(Set<Integer> hybridBreedingMethods) {
		this.hybridBreedingMethods = hybridBreedingMethods;
	}
}
