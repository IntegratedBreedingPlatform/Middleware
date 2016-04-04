package org.generationcp.middleware.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This component handles the configuration mechanism that allows the location of coded names.
 *
 */
@Component
public class CodeNamesLocator {

	public static final String CODE_NAMES_PROPERTY = "germplasm.code.names.ids";
	private Properties namesProperties;
	UserDefinedFieldsDataManager manager;

	@Autowired
	public CodeNamesLocator(@Qualifier("crossingProperties") Properties namesProperties, UserDefinedFieldsDataManager manager) {
		this.namesProperties = namesProperties;
		this.manager = manager;
	}

	public List<UserDefinedField> locateNonCodeNames(){
		List<Integer> ids = locateCodedNamesIds();

		List<UserDefinedField> codedNamesFactor = manager.getNotCodeNamesFactor(ids);

		return codedNamesFactor;
	}

	public List<Integer> locateCodedNamesIds() {

		String codeNamesIds = namesProperties.getProperty(CODE_NAMES_PROPERTY);
		List<Integer> ids = new ArrayList<>();
		for (String stringId : codeNamesIds.split(",")) {
			if (!stringId.isEmpty()) {
				ids.add(Integer.parseInt(stringId));
			}
		}
		return ids;
	}

}
