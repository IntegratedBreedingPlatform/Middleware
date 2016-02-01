
package org.generationcp.middleware.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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

	private String profile;
	private int defaultLevel;
	private Properties props;

	private static final Logger LOG = LoggerFactory.getLogger(CrossExpansionProperties.class);

	private LoadingCache<String, List<Integer>> nameTypeOrderCache;

	private LoadingCache<String, Integer> generationLevelCache;

	public CrossExpansionProperties() {
		try {
			final Resource resource = new ClassPathResource("crossing.properties");
			this.props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (final IOException e) {
			final String errorMessage = "Unable to access crossing.properties. Please contact your administrator for further assistance.";
			CrossExpansionProperties.LOG.error(
					errorMessage, e);
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
				CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1000, TimeUnit.MINUTES)
				.build(new CacheLoader<String, List<Integer>>() {

					@Override
					public List<Integer> load(final String cropName) {
						return CrossExpansionProperties.this.getNameTypeStoppingRulesByCrop(cropName);
					}

				});

		this.generationLevelCache =
				CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1000, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {

					@Override
					public Integer load(final String cropName) {
						return CrossExpansionProperties.this.getGenerationLevelStoppingRulesByCrop(cropName);
					}

				});
	}

	private int getGenerationLevelStoppingRulesByCrop(final String cropName) {
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

	private List<Integer> getNameTypeStoppingRulesByCrop(final String cropName) {
		final String propertyValue =
				CrossExpansionProperties.this.props.getProperty(cropName + "." + CrossExpansionProperties.NAME_TYPE_ORDER);
		final List<Integer> codes = new ArrayList<Integer>();

		if (StringUtils.isNotBlank(propertyValue)) {
			final String[] stringCodes = propertyValue.trim().split(",");
			for (final String code : stringCodes) {
				try {
					codes.add(Integer.parseInt(code));
				} catch (final Exception e) {
					final String errorMessage =
							String.format(cropName + "." + CrossExpansionProperties.NAME_TYPE_ORDER
									+ " property is configured incorrectly with a value of '%s'. "
									+ "Please contact your administrator for further assistance", propertyValue);
					CrossExpansionProperties.LOG.error(errorMessage, e);
					throw new MiddlewareException(errorMessage, e);
				}
			}

		}
		return codes;
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

	public List<Integer> getNameTypeOrder(final String cropName) {
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



}
