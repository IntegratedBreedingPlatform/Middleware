package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;

public interface GermplasmNameService {

	Name getNameByNameId(Integer nameId);

	UserDefinedField getNameType(String nameTypeCode);

	void deleteName(Integer nameId);

	void updateName(GermplasmNameRequestDto germplasmNameRequestDto, Integer gid, Integer nameId);

	Integer createName(Integer userid, GermplasmNameRequestDto germplasmNameRequestDto, Integer gid);
}
