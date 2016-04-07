package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.pojos.UserDefinedField;

public interface UserDefinedFieldsDataManager {



	List<UserDefinedField> getNotCodeNamesFactor(List<String> codedIds);
}
