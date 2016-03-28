package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.pojos.Name;

public interface NamesDataManager {

	List<Name> getNamesByNvalInTypeList(String name, List<Integer> typeList);
}
