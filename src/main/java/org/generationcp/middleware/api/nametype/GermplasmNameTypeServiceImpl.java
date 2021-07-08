package org.generationcp.middleware.api.nametype;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Util;

import java.util.List;
import java.util.Optional;

public class GermplasmNameTypeServiceImpl implements GermplasmNameTypeService {

	private final DaoFactory daoFactory;

	public GermplasmNameTypeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmNameTypeDTO> searchNameTypes(final String name) {
		return this.daoFactory.getUserDefinedFieldDAO().searchNameTypes(name);
	}

	@Override
	public Optional<GermplasmNameTypeDTO> getNameTypeByCode(final String code) {
		final UserDefinedField nameType = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), code);
		if (nameType != null) {
			return Optional.of(new GermplasmNameTypeDTO(nameType.getFldno(), nameType.getFcode(), nameType.getFname()));
		}
		return Optional.empty();
	}

	@Override
	public Integer createNameType(final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO) {
		final UserDefinedField userDefinedField = new UserDefinedField();
		userDefinedField.setFtable(UDTableType.NAMES_NAME.getTable());
		userDefinedField.setFtype(UDTableType.NAMES_NAME.getType());
		userDefinedField.setFcode(germplasmNameTypeRequestDTO.getCode().toUpperCase());
		userDefinedField.setFname(germplasmNameTypeRequestDTO.getName());
		userDefinedField.setFfmt("-");
		userDefinedField.setFdesc(germplasmNameTypeRequestDTO.getDescription());
		userDefinedField.setLfldno(0);
		userDefinedField.setFuid(ContextHolder.getLoggedInUserId());
		userDefinedField.setFdate(Util.getCurrentDateAsIntegerValue());
		userDefinedField.setScaleid(0);
		daoFactory.getUserDefinedFieldDAO().save(userDefinedField);
		return userDefinedField.getFldno();
	}
}
