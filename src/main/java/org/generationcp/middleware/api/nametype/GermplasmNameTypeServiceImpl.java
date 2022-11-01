package org.generationcp.middleware.api.nametype;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class GermplasmNameTypeServiceImpl implements GermplasmNameTypeService {

	private final DaoFactory daoFactory;
	private static final String DEFAULT_FIELD = "-";

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
	public Optional<GermplasmNameTypeDTO> getNameTypeById(final Integer id) {
		final UserDefinedField nameType = this.daoFactory.getUserDefinedFieldDAO().getById(id);
		if (nameType != null) {
			return Optional.of(new GermplasmNameTypeDTO(nameType.getFldno(), nameType.getFcode(), nameType.getFname()));
		}
		return Optional.empty();
	}

	@Override
	public Integer createNameType(final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO) {
		final String fDesc =
			StringUtils.isBlank(germplasmNameTypeRequestDTO.getDescription()) ? GermplasmNameTypeServiceImpl.DEFAULT_FIELD :
				germplasmNameTypeRequestDTO.getDescription();

		final UserDefinedField userDefinedField = new UserDefinedField();
		userDefinedField.setFtable(UDTableType.NAMES_NAME.getTable());
		userDefinedField.setFtype(UDTableType.NAMES_NAME.getType());
		userDefinedField.setFcode(germplasmNameTypeRequestDTO.getCode());
		userDefinedField.setFname(germplasmNameTypeRequestDTO.getName());
		userDefinedField.setFfmt(GermplasmNameTypeServiceImpl.DEFAULT_FIELD);
		userDefinedField.setFdesc(fDesc);
		userDefinedField.setLfldno(0);
		userDefinedField.setFuid(ContextHolder.getLoggedInUserId());
		userDefinedField.setFdate(Util.getCurrentDateAsIntegerValue());
		userDefinedField.setScaleid(0);
		this.daoFactory.getUserDefinedFieldDAO().save(userDefinedField);
		return userDefinedField.getFldno();
	}

	@Override
	public List<GermplasmNameTypeDTO> searchNameTypes(final NameTypeMetadataFilterRequest nameTypeMetadataFilterRequest, final Pageable pageable) {
		final Map<Integer, String> userMap = this.userService.getAllUserIDFullNameMap();

		final List<UserDefinedField> userDefinedFields = this.daoFactory.getUserDefinedFieldDAO().searchNameTypes(nameTypeMetadataFilterRequest, pageable);
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
	public long countSearchNameTypes(final NameTypeMetadataFilterRequest nameTypeMetadataFilterRequest) {
		return this.daoFactory.getUserDefinedFieldDAO().countSearchNameTypes(nameTypeMetadataFilterRequest);
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

	@Override
	public void updateNameType(final Integer nameTypeId, final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO) {
		final UserDefinedField userDefinedField = this.daoFactory.getUserDefinedFieldDAO().getById(nameTypeId);

		if (!StringUtils.isBlank(germplasmNameTypeRequestDTO.getCode())) {
			userDefinedField.setFcode(germplasmNameTypeRequestDTO.getCode());
		}
		if (!StringUtils.isBlank(germplasmNameTypeRequestDTO.getName())) {
			userDefinedField.setFname(germplasmNameTypeRequestDTO.getName());
		}
		if (!StringUtils.isBlank(germplasmNameTypeRequestDTO.getDescription())) {
			userDefinedField.setFdesc(germplasmNameTypeRequestDTO.getDescription());
		}

		userDefinedField.setFuid(ContextHolder.getLoggedInUserId());
		userDefinedField.setFdate(Util.getCurrentDateAsIntegerValue());

		this.daoFactory.getUserDefinedFieldDAO().update(userDefinedField);
	}

	@Override
	public void deleteNameType(final Integer nameTypeId) {
		final UserDefinedField userDefinedField = this.daoFactory.getUserDefinedFieldDAO().getById(nameTypeId);
		this.daoFactory.getUserDefinedFieldDAO().makeTransient(userDefinedField);
	}

	@Override
	public NameTypeMetadata getNameTypeMetadata(final Integer nameTypeId) {
		final NameTypeMetadata nameTypeMetadata = new NameTypeMetadata();

		nameTypeMetadata.setGermplasmCount(this.daoFactory.getNameDao().countNameTypeInUse(nameTypeId));
		nameTypeMetadata.setStudiesCount(this.daoFactory.getProjectPropertyDAO().countNameTypeInUse(nameTypeId));
		nameTypeMetadata.setGermplasmListCount(this.daoFactory.getGermplasmListDataViewDAO().countNameTypeInUse(nameTypeId));
		return nameTypeMetadata;
	}

	@Override
	public boolean isNameTypeUsedInStudies(final Integer nameTypeId) {
		return this.daoFactory.getProjectPropertyDAO().countNameTypeInUse(nameTypeId) > 0;
	}

	@Override
	public boolean isNameTypeUsedInGermplasmList(final Integer nameTypeId) {
		return this.daoFactory.getGermplasmListDataViewDAO().countNameTypeInUse(nameTypeId) > 0;
	}

}
