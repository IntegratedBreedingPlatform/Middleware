package org.generationcp.middleware.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.components.validator.ExecutionException;
import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CodeNamesLocator {
	private Properties namesProperties;
	UserDefinedFieldsDataManager manager;

	@Autowired
	public CodeNamesLocator(@Qualifier("codeNamesProperties") Properties namesProperties, UserDefinedFieldsDataManager manager) {
		this.namesProperties = namesProperties;
		this.manager = manager;
	}

	public List<UserDefinedField> locateNonCodeNames(){
		List<Integer> ids = getCodedNamesIds();

		List<UserDefinedField> codedNamesFactor = manager.getNotCodeNamesFactor(ids);

		return codedNamesFactor;
	}

	public List<Integer> getCodedNamesIds() {

		String codeNamesIds = namesProperties.getProperty("code.names.ids");
		List<Integer> ids = new ArrayList<>();
		for (String stringId : codeNamesIds.split(",")) {
			if (!stringId.isEmpty()) {
				ids.add(Integer.parseInt(stringId));
			}
		}
		return ids;
	}

}
