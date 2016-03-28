package org.generationcp.middleware.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.middleware.components.validator.ExecutionException;
import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CodeNamesLocator {

	public static final String COULD_NOT_READ_CONFIGURATION_FILE = "Could not read configuration file";
	private Properties namesProperties;
	UserDefinedFieldsDataManager manager;

	@Autowired
	public CodeNamesLocator(@Qualifier("codeNamesProperties") Properties namesProperties, UserDefinedFieldsDataManager manager) {
		this.namesProperties = namesProperties;
		this.manager = manager;
	}
	//This will solve first item in the ticket
	public List<UserDefinedField> locateNonCodeNames() throws ExecutionException {
		List<Integer> ids = getCodedNamesIds();

		List<UserDefinedField> codedNamesFactor = manager.getNotCodeNamesFactor(ids);

		return codedNamesFactor;
	}

	public List<Integer> getCodedNamesIds() throws ExecutionException {
		try{
			String codeNamesIds = namesProperties.getProperty("code.names.ids");
			List<Integer> ids = new ArrayList<>();
			for (String stringId : codeNamesIds.split(",")) {
				if(!stringId.isEmpty()){
					ids.add(Integer.parseInt(stringId));
				}
			}
			return ids;

		}catch (Exception ex){
			ex.printStackTrace();
			throw new ExecutionException(COULD_NOT_READ_CONFIGURATION_FILE);
		}
	}

}
