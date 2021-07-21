package org.generationcp.middleware.api.nametype;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GermplasmNameTypeService {

	List<GermplasmNameTypeDTO> searchNameTypes(String name);

	Optional<GermplasmNameTypeDTO> getNameTypeByCode(String code);

	Optional<GermplasmNameTypeDTO> getNameTypeById(Integer id);

	Integer createNameType(GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO);

	List<GermplasmNameTypeDTO> getNameTypes(Pageable pageable);

	long countAllNameTypes();

	List<GermplasmNameTypeDTO> filterGermplasmNameTypes(Set<String> codes);

	List<GermplasmNameTypeDTO> filterGermplasmNameTypesByName(String name);

	List<GermplasmNameTypeDTO> getNameTypesByGIDList(List<Integer> gidList);

	boolean existNameTypeUsedInListDataProp(String nameType);

	void updateNameType(GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO, Integer nameTypeId);

	void deleteNameType(Integer nameTypeId);
}
