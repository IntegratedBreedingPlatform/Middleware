package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GermplasmNameServiceImpl implements GermplasmNameService {

	private final DaoFactory daoFactory;

	public GermplasmNameServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Name getNameByNameId(final Integer nameId) {
		return daoFactory.getNameDao().getById(nameId);
	}

	@Override
	public Name getPreferredName(final Integer gid) {
		final List<Name> names = daoFactory.getNameDao().getByGIDWithListTypeFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public UserDefinedField getNameType(final Integer nameTypeId) {
		return this.daoFactory.getUserDefinedFieldDAO().getById(nameTypeId);
	}

	@Override
	public void deleteName(final Integer nameId) {
		final Name name = daoFactory.getNameDao().getById(nameId);
		daoFactory.getNameDao().makeTransient(name);
	}

	@Override
	public void updateName(final GermplasmNameRequestDto germplasmNameRequestDto){
		final Name name = daoFactory.getNameDao().getById(germplasmNameRequestDto.getId());

		if (germplasmNameRequestDto.getTypeId() != null) {
			name.setTypeId(germplasmNameRequestDto.getTypeId());
		}

		if(!StringUtils.isBlank(germplasmNameRequestDto.getName())){
			name.setNval(germplasmNameRequestDto.getName());
		}

		if (germplasmNameRequestDto.getDate() != null) {
			name.setNdate(germplasmNameRequestDto.getDate());
		}

		if (germplasmNameRequestDto.getLocationId() != null) {
			name.setLocationId(germplasmNameRequestDto.getLocationId());
		}

		if (germplasmNameRequestDto.getStatus() != null) {
			name.setNstat(germplasmNameRequestDto.getStatus());
		}
		daoFactory.getNameDao().save(name);
	}

	@Override
	public Integer createName(final GermplasmNameRequestDto germplasmNameRequestDto, final Integer userid) {
		final Name name = new Name();
		name.setGermplasmId(germplasmNameRequestDto.getGid());
		name.setTypeId(germplasmNameRequestDto.getTypeId());
		name.setNval(germplasmNameRequestDto.getName());
		name.setNdate(germplasmNameRequestDto.getDate());
		name.setLocationId(germplasmNameRequestDto.getLocationId());
		name.setNstat(germplasmNameRequestDto.getStatus());
		name.setUserId(userid);
		name.setReferenceId(0);
		daoFactory.getNameDao().save(name);
		return name.getNid();
	}
}
