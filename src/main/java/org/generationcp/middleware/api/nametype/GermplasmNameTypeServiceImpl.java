package org.generationcp.middleware.api.nametype;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;

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
}
