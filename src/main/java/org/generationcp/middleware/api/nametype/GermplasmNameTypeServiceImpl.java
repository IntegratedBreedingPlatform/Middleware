package org.generationcp.middleware.api.nametype;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GermplasmNameTypeServiceImpl implements GermplasmNameTypeService {

	private final DaoFactory daoFactory;
	private static final String DEFAULT_FFMT_FIELD = "-";

	@Autowired
	protected UserService userService;

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
		userDefinedField.setFfmt(GermplasmNameTypeServiceImpl.DEFAULT_FFMT_FIELD);
		userDefinedField.setFdesc(germplasmNameTypeRequestDTO.getDescription());
		userDefinedField.setLfldno(0);
		userDefinedField.setFuid(ContextHolder.getLoggedInUserId());
		userDefinedField.setFdate(Util.getCurrentDateAsIntegerValue());
		userDefinedField.setScaleid(0);
		daoFactory.getUserDefinedFieldDAO().save(userDefinedField);
		return userDefinedField.getFldno();
	}

	@Override
	public List<GermplasmNameTypeDTO> getNameTypes(final Pageable pageable) {
		final Map<Integer, String> userMap = this.userService.getAllUserIDFullNameMap();

		final List<UserDefinedField> userDefinedFields = daoFactory.getUserDefinedFieldDAO().getNameTypes(pageable);
		if(!userDefinedFields.isEmpty()){
			return userDefinedFields.stream().map(
				userDefinedField ->
					new GermplasmNameTypeDTO(userDefinedField.getFldno(), userDefinedField.getFcode(),
					userDefinedField.getFname(), userDefinedField.getFdesc(), userMap.get(userDefinedField.getFuid()), userDefinedField.getFdate())
			).collect(
				Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public long countAllNameTypes() {
		return daoFactory.getUserDefinedFieldDAO().countAllNameTypes();
	}

	@Override
	public List<GermplasmNameTypeDTO> filterGermplasmNameTypes(final Set<String> codes) {
		return this.daoFactory.getUserDefinedFieldDAO().getByCodes(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()), codes)
			.stream()
			.map(userDefinedField -> {
				final GermplasmNameTypeDTO germplasmNameTypeDTO = new GermplasmNameTypeDTO();
				germplasmNameTypeDTO.setId(userDefinedField.getFldno());
				germplasmNameTypeDTO.setName(userDefinedField.getFname());
				germplasmNameTypeDTO.setCode(userDefinedField.getFcode());
				germplasmNameTypeDTO.setDescription(userDefinedField.getFdesc());
				return germplasmNameTypeDTO;
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<GermplasmNameTypeDTO> filterGermplasmNameTypesByName(final String name) {
		return this.daoFactory.getUserDefinedFieldDAO().getByName(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()), name)
			.stream()
			.map(userDefinedField -> {
				final GermplasmNameTypeDTO germplasmNameTypeDTO = new GermplasmNameTypeDTO();
				germplasmNameTypeDTO.setId(userDefinedField.getFldno());
				germplasmNameTypeDTO.setName(userDefinedField.getFname());
				germplasmNameTypeDTO.setCode(userDefinedField.getFcode());
				return germplasmNameTypeDTO;
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<GermplasmNameTypeDTO> getNameTypesByGIDList(final List<Integer> gidList) {
		return this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gidList).stream()
			.map(userDefinedField -> {
				final GermplasmNameTypeDTO germplasmNameTypeDTO = new GermplasmNameTypeDTO();
				germplasmNameTypeDTO.setId(userDefinedField.getFldno());
				germplasmNameTypeDTO.setName(userDefinedField.getFname());
				germplasmNameTypeDTO.setCode(userDefinedField.getFcode());
				return germplasmNameTypeDTO;
			})
			.collect(Collectors.toList());
	}

}
