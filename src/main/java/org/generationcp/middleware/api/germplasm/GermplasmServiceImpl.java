package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmImportRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GermplasmServiceImpl implements GermplasmService {

	private final DaoFactory daoFactory;

	private static final String DEFAULT_DASH = "-";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private GermplasmGuidGenerator germplasmGuidGenerator;

	public GermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		germplasmGuidGenerator = new GermplasmGuidGeneratorImpl();
	}

	@Override
	public Map<Integer, Integer> importGermplasmSet(final Integer userId, final String cropName,
		final GermplasmImportRequestDto germplasmImportRequestDto) {
		final Map<Integer, Integer> results = new HashMap<>();
		final Map<String, Method> methodsMapByAbbr = this.getBreedingMethodsMapByAbbr(germplasmImportRequestDto.getGermplasmSet());
		final Map<String, Integer> locationsMapByAbbr = this.getLocationsMapByAbbr(germplasmImportRequestDto.getGermplasmSet());
		final Map<String, Integer> attributesMapByName = this.getAttributesMapByName(germplasmImportRequestDto.getGermplasmSet());
		final Map<String, Integer> nameTypesMapByName = this.getNameTypesMapByName(germplasmImportRequestDto.getGermplasmSet());

		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropName);

		for (final GermplasmImportRequestDto.GermplasmDto germplasmDto : germplasmImportRequestDto.getGermplasmSet()) {
			final Germplasm germplasm = new Germplasm();

			final Method method = methodsMapByAbbr.get(germplasmDto.getBreedingMethodAbbr().toUpperCase());
			germplasm.setMethodId(method.getMid());

			//Given that parents are not provided, gnpgs = method.mprgn
			//If parents are provided and method type = GEN and mprgn = 0, then, gnpgs = number or parents provided
			germplasm.setGnpgs(method.getMprgn());

			//First iteration, parents not provided, default = 0
			germplasm.setGpid1(0);
			germplasm.setGpid2(0);

			germplasm.setGrplce(0);
			germplasm.setMgid(0);
			germplasm.setUserId(userId);
			germplasm.setLgid(0);
			germplasm.setLocationId(locationsMapByAbbr.get(germplasmDto.getLocationAbbr().toUpperCase()));
			germplasm.setDeleted(Boolean.FALSE);
			germplasm.setGdate(Util.convertDateToIntegerValue(germplasmDto.getCreationDate()));

			if (StringUtils.isEmpty(germplasmDto.getGuid())) {
				this.germplasmGuidGenerator.generateObservationUnitIds(cropType, Collections.singletonList(germplasm));
			} else {
				germplasm.setGermplasmUUID(germplasmDto.getGuid());
			}

			if (!StringUtils.isEmpty(germplasmDto.getReference())) {
				final Bibref bibref =
					new Bibref(null, DEFAULT_DASH, DEFAULT_DASH, germplasmDto.getReference(), DEFAULT_DASH, DEFAULT_DASH, DEFAULT_DASH,
						DEFAULT_DASH,
						DEFAULT_DASH, DEFAULT_DASH, DEFAULT_DASH, DEFAULT_DASH);
				this.daoFactory.getBibrefDAO().save(bibref);
				germplasm.setReferenceId(bibref.getRefid());
			}

			this.daoFactory.getGermplasmDao().save(germplasm);

			germplasmDto.getNames().forEach((k, v) -> {
				final Name name = new Name(null, germplasm.getGid(), nameTypesMapByName.get(k.toUpperCase()),
					(k.equalsIgnoreCase(germplasmDto.getPreferredName())) ? 1 : 0, userId, v, germplasm.getLocationId(),
					Util.getCurrentDateAsIntegerValue(), 0);
				this.daoFactory.getNameDao().save(name);
			});

			if (germplasmDto.getAttributes() != null) {
				germplasmDto.getAttributes().forEach((k, v) -> {
					final Attribute attribute = new Attribute(null, germplasm.getGid(), attributesMapByName.get(k.toUpperCase()), userId, v,
						germplasm.getLocationId(),
						0, Util.getCurrentDateAsIntegerValue());
					this.daoFactory.getAttributeDAO().save(attribute);
				});
			}

			results.put(germplasmDto.getClientId(), germplasm.getGid());
		}

		return results;
	}

	private Map<String, Integer> getLocationsMapByAbbr(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> locationAbbreviations = germplasmDtos.stream().map(g -> g.getLocationAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getLocationDAO().filterLocations(null, null, null, new ArrayList<>(locationAbbreviations)).stream()
			.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Method> getBreedingMethodsMapByAbbr(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> breedingMethods = germplasmDtos.stream().map(g -> g.getBreedingMethodAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethods)).stream()
			.collect(Collectors.toMap(Method::getMcode, method -> method));
	}

	private Map<String, Integer> getNameTypesMapByName(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> nameTypes = new HashSet<>();
		germplasmDtos.forEach(g -> nameTypes.addAll(g.getNames().keySet()));
		final List<UserDefinedField> nameTypesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(), Collections.singleton(UDTableType.NAMES_NAME.getType()), nameTypes);
		return nameTypesUdfldList.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getAttributesMapByName(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> attributes = new HashSet<>();
		germplasmDtos.forEach(g -> {
			if (g.getAttributes() != null && !g.getAttributes().isEmpty()) {
				attributes.addAll(g.getAttributes().keySet());
			}
		});
		if (!attributes.isEmpty()) {
			final List<UserDefinedField> attributesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
				.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
					new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType())),
					attributes);
			return attributesUdfldList.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		} else {
			return new HashMap<>();
		}
	}

}
