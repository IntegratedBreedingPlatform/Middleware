package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeService;
import org.generationcp.middleware.domain.germplasm.GermplasmNameDto;
import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class GermplasmNameServiceImpl implements GermplasmNameService {

	public static final String COMMON_NAMES_PREFERRED_KEY = "PREFERRED NAME";
	private final DaoFactory daoFactory;

	@Autowired
	private GermplasmNameTypeService germplasmNameTypeService;

	public GermplasmNameServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Name getNameById(final Integer nameId) {
		return this.daoFactory.getNameDao().getById(nameId);
	}

	private Name getPreferredNameOfGermplasm(final Integer gid) {
		final List<Name> names = this.daoFactory.getNameDao().getByGIDWithListTypeFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public void deleteName(final Integer nameId) {
		final Name name = this.daoFactory.getNameDao().getById(nameId);
		this.daoFactory.getNameDao().makeTransient(name);
	}

	@Override
	public void updateName(final GermplasmNameRequestDto germplasmNameRequestDto, final Integer gid, final Integer nameId) {
		if (germplasmNameRequestDto.isPreferredName() != null && germplasmNameRequestDto.isPreferredName()) {
			final Name preferredName = this.getPreferredNameOfGermplasm(gid);
			if (preferredName != null) {
				preferredName.setNstat(0);
				this.daoFactory.getNameDao().save(preferredName);
			}
		}

		final Name name = this.daoFactory.getNameDao().getById(nameId);
		if (!StringUtils.isBlank(germplasmNameRequestDto.getNameTypeCode())) {
			final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameRequestDto.getNameTypeCode()));
			final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
			name.setTypeId(germplasmNameTypeDTOs.get(0).getId());

		}

		if (!StringUtils.isBlank(germplasmNameRequestDto.getName())) {
			name.setNval(germplasmNameRequestDto.getName());
		}

		if (germplasmNameRequestDto.getDate() != null) {
			name.setNdate(Integer.valueOf(germplasmNameRequestDto.getDate()));
		}

		if (germplasmNameRequestDto.getLocationId() != null) {
			name.setLocationId(germplasmNameRequestDto.getLocationId());
		}

		if (germplasmNameRequestDto.isPreferredName() != null) {
			name.setNstat(Boolean.TRUE.equals(germplasmNameRequestDto.isPreferredName()) ? 1 : 0);
		}
		this.daoFactory.getNameDao().save(name);
	}

	@Override
	public Integer createName(final GermplasmNameRequestDto germplasmNameRequestDto, final Integer gid) {

		if (Boolean.TRUE.equals(germplasmNameRequestDto.isPreferredName())) {
			final Name preferredName = this.getPreferredNameOfGermplasm(gid);
			if (preferredName != null) {
				preferredName.setNstat(0);
				this.daoFactory.getNameDao().save(preferredName);
			}
		}

		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameRequestDto.getNameTypeCode()));
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);

		final Name name = new Name();
		name.setGermplasm(new Germplasm());
		name.getGermplasm().setGid(gid);
		name.setTypeId(germplasmNameTypeDTOs.get(0).getId());
		name.setNval(germplasmNameRequestDto.getName());
		name.setNdate(Integer.valueOf(germplasmNameRequestDto.getDate()));
		name.setLocationId(germplasmNameRequestDto.getLocationId());
		name.setNstat(Boolean.TRUE.equals(germplasmNameRequestDto.isPreferredName()) ? 1 : 0);
		name.setReferenceId(0);
		this.daoFactory.getNameDao().save(name);
		return name.getNid();
	}

	@Override
	public List<GermplasmNameDto> getGermplasmNamesByGids(final List<Integer> gids) {
		return this.daoFactory.getNameDao().getGermplasmNamesByGids(gids);
	}

	@Override
	public Map<String, Map<Integer, String>> getGermplasmCommonNamesMap(final ArrayList<Integer> gids) {
		final Map<String, Map<Integer, String>> map = new LinkedHashMap<>();
		map.putIfAbsent(COMMON_NAMES_PREFERRED_KEY, new HashMap<>());
		final List<GermplasmNameDto> names = this.getGermplasmNamesByGids(gids);

		for (final GermplasmNameDto germplasmNameDto : names) {
			final Integer gid = germplasmNameDto.getGid();
			final String name = germplasmNameDto.getName();
			final String nameTypeCode = germplasmNameDto.getNameTypeCode();

			if (germplasmNameDto.isPreferred()) {
				map.get(COMMON_NAMES_PREFERRED_KEY).put(gid, name);
			}

			map.putIfAbsent(nameTypeCode, new HashMap<>());
			map.get(nameTypeCode).put(gid, name);
		}

		// remove non-common names
		map.entrySet().removeIf(entry -> entry.getValue().keySet().size() != gids.size());

		return map;
	}

	@Override
	public List<String> getExistingGermplasmPUIs(final List<String> germplasmPUIs) {
		return this.daoFactory.getNameDao().getExistingGermplasmPUIs(germplasmPUIs);
	}

	@Override
	public boolean isNameTypeUsedAsGermplasmName(final Integer nameTypeId){
		return this.daoFactory.getNameDao().isNameTypeInUse(nameTypeId);
	}

	@Override
	public boolean isLocationUsedInGermplasmName(final Integer locationId) {
		return this.daoFactory.getNameDao().isLocationUsedInGermplasmName(locationId);
	}

}
