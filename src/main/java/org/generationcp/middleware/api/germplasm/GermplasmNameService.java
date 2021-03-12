package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;

public interface GermplasmNameService {

	Name getNameByNameId(Integer nameId);

	UserDefinedField getNameType(final Integer nameTypeId);

	void deleteName(Integer nameId);

	void updateName(final GermplasmNameRequestDto germplasmNameRequestDto);

	Integer createName(final GermplasmNameRequestDto germplasmNameRequestDto, final Integer userid);
}
