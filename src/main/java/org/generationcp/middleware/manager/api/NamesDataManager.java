package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.pojos.Name;

public interface NamesDataManager {

	List<Name> getNamesByNvalInFCodeList(String name, List<String> typeList);

	List<Name> getNameByGIDAndCodedName(Integer gid, List<String> fCodecodedNames);
}
