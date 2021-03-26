package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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

	private Name getPreferredName(final Integer gid) {
		final List<Name> names = daoFactory.getNameDao().getByGIDWithListTypeFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public UserDefinedField getNameType(final String nameTypeCode) {
		return this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), nameTypeCode);
	}

	@Override
	public void deleteName(final Integer nameId) {
		final Name name = daoFactory.getNameDao().getById(nameId);
		daoFactory.getNameDao().makeTransient(name);
	}

	@Override
	public void updateName(final GermplasmNameRequestDto germplasmNameRequestDto, final Integer gid, final Integer nameId) {
		if (germplasmNameRequestDto.isPreferredName() != null && germplasmNameRequestDto.isPreferredName()) {
			final Name preferredName = this.getPreferredName(gid);
			if (preferredName != null) {
				preferredName.setNstat(0);
				daoFactory.getNameDao().save(preferredName);
			}
		}

		final Name name = daoFactory.getNameDao().getById(nameId);
		if (!StringUtils.isBlank(germplasmNameRequestDto.getNameTypeCode())) {
			final UserDefinedField userDefinedField = this.getNameType(germplasmNameRequestDto.getNameTypeCode());
			name.setTypeId(userDefinedField.getFldno());
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
			name.setNstat(germplasmNameRequestDto.isPreferredName() ? 1 : 0);
		}
		daoFactory.getNameDao().save(name);
	}

	@Override
	public Integer createName(final Integer userid, final GermplasmNameRequestDto germplasmNameRequestDto, final Integer gid) {
		if (germplasmNameRequestDto.isPreferredName()) {
			final Name preferredName = this.getPreferredName(gid);
			preferredName.setNstat(0);
			daoFactory.getNameDao().save(preferredName);
		}

		final Name name = new Name();
		name.setGermplasmId(gid);
		final UserDefinedField userDefinedField = this.getNameType(germplasmNameRequestDto.getNameTypeCode());
		name.setTypeId(userDefinedField.getFldno());
		name.setNval(germplasmNameRequestDto.getName());
		name.setNdate(Integer.valueOf(germplasmNameRequestDto.getDate()));
		name.setLocationId(germplasmNameRequestDto.getLocationId());
		name.setNstat(germplasmNameRequestDto.isPreferredName() ? 1 : 0);
		name.setUserId(userid);
		name.setReferenceId(0);
		daoFactory.getNameDao().save(name);
		return name.getNid();
	}
}
