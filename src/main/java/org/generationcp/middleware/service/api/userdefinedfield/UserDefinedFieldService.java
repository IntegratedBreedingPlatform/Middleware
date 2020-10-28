package org.generationcp.middleware.service.api.userdefinedfield;

import java.util.List;
import java.util.Map;

public interface UserDefinedFieldService {

	Map<String, Integer> getByTableAndCodesInMap(String table, List<String> codes);
}
