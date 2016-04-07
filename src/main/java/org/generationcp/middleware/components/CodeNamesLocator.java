package org.generationcp.middleware.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This component handles the configuration mechanism that allows the location of coded names.
 */
@Component
public class CodeNamesLocator {

	private static final String CODED_NAMES_PROPERTY = "coded.names";
	private static final String REGEX_SEPARATOR = ",";
	/**
	 * Even though the coded names are not strictly related to the crossing mechanism, it was decided to use the crossing properties file
	 * to store the coded names in order to have a small amount  of properties files.
	 */
	private Properties crossingProperties;
	UserDefinedFieldsDataManager manager;

	@Autowired
	public CodeNamesLocator(@Qualifier("crossingProperties") Properties crossingProperties, UserDefinedFieldsDataManager manager) {
		this.crossingProperties = crossingProperties;
		this.manager = manager;
	}

	public List<UserDefinedField> locateNonCodeNamesForCrop(String cropName) {


		List<String> names = locateCodeNamesForCrop(cropName);

		List<UserDefinedField> codedNamesFactor = manager.getNotCodeNamesFactor(names);

		return codedNamesFactor;
	}



	public List<String> locateCodeNamesForCrop(String cropName) {
		final String propertyValue = crossingProperties.getProperty(cropName + "." + CODED_NAMES_PROPERTY);
		final List<String> codes = new ArrayList<String>();

		if (StringUtils.isNotBlank(propertyValue)) {
			final String[] stringCodes = propertyValue.trim().split(REGEX_SEPARATOR);
			for (final String code : stringCodes) {
				if (StringUtils.isNotBlank(code)) {
					codes.add(code.trim());
				}
			}

		}
		return codes;
	}



}
