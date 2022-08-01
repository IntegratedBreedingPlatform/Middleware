package org.generationcp.middleware.api.location;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.program.ProgramBasicDetailsDto;
import org.generationcp.middleware.api.program.ProgramFavoriteService;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	private ProgramFavoriteService programFavoriteService;

	private final DaoFactory daoFactory;

	public LocationServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public LocationDTO getLocation(final Integer locationId) {
		return this.daoFactory.getLocationDAO().getLocationDTO(locationId);
	}

	@Override
	public List<LocationTypeDTO> getLocationTypes() {
		final List<UserDefinedField> userDefinedFields = this.daoFactory.getUserDefinedFieldDAO()
			.getByFieldTableNameAndType(UDTableType.LOCATION_LTYPE.getTable(), ImmutableSet.of(UDTableType.LOCATION_LTYPE.getType()));
		final List<LocationTypeDTO> locationTypes = new ArrayList<>();
		for (final UserDefinedField userDefinedField : userDefinedFields) {
			locationTypes.add(new LocationTypeDTO(userDefinedField));
		}
		return locationTypes;
	}

	@Override
	public List<LocationDTO> searchLocations(
		final LocationSearchRequest locationSearchRequest,
		final Pageable pageable, final String programUUID) {
		return this.daoFactory.getLocationDAO().searchLocations(locationSearchRequest, pageable, programUUID);
	}

	@Override
	public long countFilteredLocations(final LocationSearchRequest locationSearchRequest, final String programUUID) {
		return this.daoFactory.getLocationDAO().countSearchLocation(locationSearchRequest, programUUID);
	}

	@Override
	public List<org.generationcp.middleware.api.location.Location> getLocations(
		final LocationSearchRequest locationSearchRequest,
		final Pageable pageable) {
		return this.daoFactory.getLocationDAO().getLocations(locationSearchRequest, pageable);
	}

	@Override
	public void deleteLocation(final Integer locationId) {
		final Location location = this.daoFactory.getLocationDAO().getById(locationId);
		if (LocdesType.FIELD.getId().equals(location.getLtype())) {
			this.deleteFieldLocation(Arrays.asList(location.getLocid()));
		} else if (LocdesType.BLOCK.getId().equals(location.getLtype())) {
			this.deleteBlockLocation(Arrays.asList(location.getLocid()));
		} else {
			final List<Locdes> fieldTypeLocations =
				this.daoFactory.getLocDesDao().getLocdes(null, Arrays.asList(location.getLocid().toString()));
			if (!fieldTypeLocations.isEmpty()) {
				this.deleteFieldLocation(fieldTypeLocations.stream().map(Locdes::getLocationId).collect(Collectors.toList()));
				// Delete Field Location
				this.daoFactory.getLocationDAO()
					.deleteByLocationIds(fieldTypeLocations.stream().map(Locdes::getLocationId).collect(Collectors.toList()));
			}
		}
		final Set<Integer> entityIds = ImmutableSet.of(locationId);
		this.daoFactory.getProgramFavoriteDao().deleteProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, entityIds);
		this.daoFactory.getLocationDAO().makeTransient(location);
	}

	private void deleteBlockLocation(final List<Integer> locationIds) {
		// Get the Block Parents
		final List<Integer> blockParentIds = this.daoFactory.getLocDesDao().getLocdes(locationIds, null)
			.stream().map(Locdes::getLocationId).collect(Collectors.toList());

		// Delete Block Parents
		if (!blockParentIds.isEmpty()) {
			this.daoFactory.getLocDesDao().deleteByLocationIds(blockParentIds);
		}
	}

	private void deleteFieldLocation(final List<Integer> locationIds) {
		// Get the Block Parents
		final List<Integer> blockParentIds = this.daoFactory.getLocDesDao().getLocdes(null, locationIds.stream()
			.map(Object::toString).collect(Collectors.toList()))
			.stream().map(Locdes::getLocationId).collect(Collectors.toList());

		// Delete Blocks & Block Parents
		if (!CollectionUtils.isEmpty(blockParentIds)) {
			this.daoFactory.getLocDesDao().deleteByLocationIds(blockParentIds);
			this.daoFactory.getLocationDAO().deleteByLocationIds(blockParentIds);
		}

		// Delete Field Parents
		final List<Integer> fieldParentIds = this.daoFactory.getLocDesDao().getLocdes(locationIds, null)
			.stream().map(Locdes::getLocationId).collect(Collectors.toList());

		if (!fieldParentIds.isEmpty()) {
			this.daoFactory.getLocDesDao().deleteByLocationIds(fieldParentIds);
		}
	}

	@Override
	public void deleteBlockFieldLocationByBlockId(final List<Integer> blockLocIds) {
		final List<Integer> blockParentIds = this.daoFactory.getLocDesDao()
			.getBlockParentsToDelete(blockLocIds);

		// Delete Block Records
		this.daoFactory.getLocationDAO().deleteByLocationIds(blockLocIds);
		this.daoFactory.getLocDesDao().deleteByLocationIds(blockLocIds);

		// Delete Field/Block Parent Records
		if (!blockParentIds.isEmpty()) {
			this.daoFactory.getLocationDAO().deleteByLocationIds(blockParentIds);
			this.daoFactory.getLocDesDao().deleteByLocationIds(blockParentIds);
		}
	}

	@Override
	public LocationDTO createLocation(final LocationRequestDto locationRequestDto) {

		final Country country = (locationRequestDto.getCountryId() == null) ? null :
			this.daoFactory.getCountryDao().getById(locationRequestDto.getCountryId());
		final Location province = (locationRequestDto.getProvinceId() == null) ? null :
			this.daoFactory.getLocationDAO().getById(locationRequestDto.getProvinceId());

		final Location newLocation = new Location(null, locationRequestDto.getType(),
			0, locationRequestDto.getName(), locationRequestDto.getAbbreviation(),
			0, 0, province, country, 0);

		if (locationRequestDto.getLatitude() != null) {
			newLocation.setLatitude(locationRequestDto.getLatitude());
		}

		if (locationRequestDto.getLongitude() != null) {
			newLocation.setLongitude(locationRequestDto.getLongitude());
		}

		if (locationRequestDto.getAltitude() != null) {
			newLocation.setAltitude(locationRequestDto.getAltitude());
		}

		final Location location = this.daoFactory.getLocationDAO().saveOrUpdate(newLocation);
		return new LocationDTO(location);
	}

	@Override
	public void updateLocation(final Integer locationId, final LocationRequestDto locationRequestDto) {
		final Location location = this.daoFactory.getLocationDAO().getById(locationId);

		location.setLname(locationRequestDto.getName());
		location.setLabbr(locationRequestDto.getAbbreviation());
		location.setLtype(locationRequestDto.getType());

		final Country country = this.daoFactory.getCountryDao().getById(locationRequestDto.getCountryId());
		location.setCountry(country);

		final Location province = this.daoFactory.getLocationDAO().getById(locationRequestDto.getProvinceId());
		location.setProvince(province);

		location.setLatitude(locationRequestDto.getLatitude());
		location.setLongitude(locationRequestDto.getLongitude());
		location.setAltitude(locationRequestDto.getAltitude());

		this.daoFactory.getLocationDAO().saveOrUpdate(location);
	}

	@Override
	public boolean isDefaultCountryLocation(final Integer locationId) {
		final Country country = this.daoFactory.getCountryDao().getById(locationId);
		return country != null;
	}

	@Override
	public boolean blockIdIsUsedInFieldMap(final List<Integer> blockIds) {
		return this.daoFactory.getLocationDAO().blockIdIsUsedInFieldMap(blockIds);
	}

	@Override
	public List<LocationDTO> getCountries() {
		return this.daoFactory.getLocationDAO().getAllCountries();
	}

	@Override
	public ProgramLocationDefault saveProgramLocationDefault(final String programUUID, final Integer breedingLocationId,
		final Integer storageLocationId) {
		//Save location defaults as program favorites
		final Set<Integer> favoriteLocations = new HashSet<>(Arrays.asList(breedingLocationId, storageLocationId));
		this.programFavoriteService.addProgramFavorites(programUUID, ProgramFavorite.FavoriteType.LOCATION, favoriteLocations);

		return this.daoFactory.getProgramLocationDefaultDAO()
			.save(new ProgramLocationDefault(programUUID, breedingLocationId, storageLocationId));
	}

	@Override
	public void updateProgramLocationDefault(final String programUUID, final ProgramBasicDetailsDto programBasicDetailsDto) {
		final ProgramLocationDefault programLocationDefault = this.daoFactory.getProgramLocationDefaultDAO().getByProgramUUID(programUUID);
		final Set<Integer> defaultLocations = new HashSet<>();
		if(programBasicDetailsDto.getBreedingLocationDefaultId() != null) {
			defaultLocations.add(programBasicDetailsDto.getBreedingLocationDefaultId());
			programLocationDefault.setBreedingLocationId(programBasicDetailsDto.getBreedingLocationDefaultId());
		}
		if(programBasicDetailsDto.getStorageLocationDefaultId() != null) {
			defaultLocations.add(programBasicDetailsDto.getStorageLocationDefaultId());
			programLocationDefault.setStorageLocationId(programBasicDetailsDto.getStorageLocationDefaultId());
		}
		final List<ProgramFavorite> programFavorites =
			this.programFavoriteService.getProgramFavorites(programUUID, ProgramFavorite.FavoriteType.LOCATION, defaultLocations);
		final List<Integer> programFavoriteLocationIds = programFavorites.stream().map(ProgramFavorite::getEntityId).collect(Collectors.toList());

		final Set<Integer> favoriteLocations = new HashSet<>();
		for(final Integer defaultLocation: defaultLocations) {
			// Check if the location to be set as default is not yet a program favorite
			if(!programFavoriteLocationIds.contains(defaultLocation)) {
				favoriteLocations.add(defaultLocation);
			}
		}

		//Save location defaults as program favorites
		this.programFavoriteService.addProgramFavorites(programUUID, ProgramFavorite.FavoriteType.LOCATION, favoriteLocations);
		this.daoFactory.getProgramLocationDefaultDAO().update(programLocationDefault);
	}

	@Override
	public ProgramLocationDefault getProgramLocationDefault(final String programUUID) {
		return this.daoFactory.getProgramLocationDefaultDAO().getByProgramUUID(programUUID);
	}

	@Override
	public LocationDTO getDefaultBreedingLocation(final String programUUID) {
		final ProgramLocationDefault programLocationDefault = this.daoFactory.getProgramLocationDefaultDAO().getByProgramUUID(programUUID);
		return this.getLocation(programLocationDefault.getBreedingLocationId());
	}

	@Override
	public LocationDTO getDefaultStorageLocation(final String programUUID) {
		final ProgramLocationDefault programLocationDefault = this.daoFactory.getProgramLocationDefaultDAO().getByProgramUUID(programUUID);
		return this.getLocation(programLocationDefault.getStorageLocationId());
	}

	@Override
	public boolean isProgramBreedingLocationDefault(final Integer locationId) {
		return this.daoFactory.getProgramLocationDefaultDAO().isProgramBreedingLocationDefault(locationId);
	}

	@Override
	public boolean isProgramStorageLocationDefault(final Integer locationId) {
		return this.daoFactory.getProgramLocationDefaultDAO().isProgramStorageLocationDefault(locationId);
	}

	@Override
	public List<Location> getAllLocations() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getAll();
		Collections.sort(locations);
		return locations;
	}

	@Override
	public List<Location> getLocationsByIDs(final List<Integer> ids) {
		return this.daoFactory.getLocationDAO().getLocationByIds(ids);
	}

	@Override
	public List<Location> getLocationsByName(final String name, final Operation op) {
		final List<Location> locations = new ArrayList<>();
		locations.addAll(this.daoFactory.getLocationDAO().getByName(name, op));
		return locations;
	}

	@Override
	public String retrieveLocIdOfUnspecifiedLocation() {
		String unspecifiedLocationId = "";
		final List<Location> locations = this.getLocationsByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			unspecifiedLocationId = String.valueOf(locations.get(0).getLocid());
		}
		return unspecifiedLocationId;
	}

	@Override
	public List<Location> getLocationsByName(final String name, final int start, final int numOfRows, final Operation op) {
		return this.daoFactory.getLocationDAO().getByName(name, op, start, numOfRows);
	}

	@Override
	public Location getLocationByID(final Integer id) {
		return this.daoFactory.getLocationDAO().getById(id, false);
	}

	@Override
	public int addLocationAndLocdes(final Location location, final Locdes locdes) {

		Integer idLocationSaved = null;
		try {

			// Auto-assign IDs for new DB records
			final LocationDAO locationDao = this.daoFactory.getLocationDAO();
			final Location recordSaved = locationDao.saveOrUpdate(location);
			idLocationSaved = recordSaved.getLocid();

			final LocdesDAO locdesDao = this.daoFactory.getLocDesDao();
			locdes.setLocationId(idLocationSaved);
			locdesDao.saveOrUpdate(locdes);

		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while saving Location: addLocationAndLocdes(" + "location=" + location + ", locdes=" + locdes + "): "
					+ e.getMessage(), e);
		}
		return idLocationSaved;
	}

	@Override
	public List<Location> getAllBreedingLocations() {
		return this.daoFactory.getLocationDAO().getAllBreedingLocations();
	}

	@Override
	public List<Location> getAllFieldLocations(final int locationId) {
		final Integer fieldParentFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
		final Integer fieldLtypeFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		return this.daoFactory.getLocationDAO().getLocationsByDTypeAndLType(String.valueOf(locationId), fieldParentFldId, fieldLtypeFldId);
	}

	@Override
	public List<Location> getAllBlockLocations(final int fieldId) {
		final Integer blockParentFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode());
		final Integer blockLtypeFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());
		return this.daoFactory.getLocationDAO().getLocationsByDTypeAndLType(String.valueOf(fieldId), blockParentFldId, blockLtypeFldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(final int blockId) {
		int rowsInBlock = 0;
		int rowsInPlot = 0;
		int rangesInBlock = 0;
		int plantingOrder = 0;
		int machineRowCapacity = 0;
		Integer fieldId = null;
		boolean isNew = true;

		final Map<String, UserDefinedField> dTypes = this.getUserDefinedFieldMapOfCodeByUDTableType(UDTableType.LOCDES_DTYPE);

		final List<Locdes> locdesOfLocation = this.daoFactory.getLocDesDao().getByLocation(blockId);
		final List<String> deletedPlots = new ArrayList<>();

		for (final Locdes locdes : locdesOfLocation) {
			if (locdes != null) {
				final int typeId = locdes.getTypeId();
				final String value = locdes.getDval();
				if (typeId == dTypes.get(LocdesType.ROWS_IN_BLOCK.getCode()).getFldno()) {
					rowsInBlock = this.getNumericValue(value);
				} else if (typeId == dTypes.get(LocdesType.ROWS_IN_PLOT.getCode()).getFldno()) {
					rowsInPlot = this.getNumericValue(value);
				} else if (typeId == dTypes.get(LocdesType.RANGES_IN_BLOCK.getCode()).getFldno()) {
					rangesInBlock = this.getNumericValue(value);
				} else if (typeId == dTypes.get(LocdesType.PLANTING_ORDER.getCode()).getFldno()) {
					plantingOrder = this.getNumericValue(value);
				} else if (typeId == dTypes.get(LocdesType.MACHINE_ROW_CAPACITY.getCode()).getFldno()) {
					machineRowCapacity = this.getNumericValue(value);
				} else if (typeId == dTypes.get(LocdesType.DELETED_PLOTS.getCode()).getFldno()) {
					deletedPlots.add(value);
				} else if (typeId == dTypes.get(LocdesType.BLOCK_PARENT.getCode()).getFldno()) {
					fieldId = this.getNumericValue(value);
				}
			}
		}

		if (rowsInBlock > 0 || rangesInBlock > 0 || rowsInPlot > 0) {
			isNew = false;
		}

		return new FieldmapBlockInfo(blockId, rowsInBlock, rangesInBlock, rowsInPlot, plantingOrder, machineRowCapacity, isNew,
			deletedPlots, fieldId);
	}

	@Override
	public List<Location> getAllFields() {
		final Integer fieldLtype = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		final Integer relationshipType = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
		final List<Location> locations = this.daoFactory.getLocationDAO().getByTypeWithParent(fieldLtype, relationshipType);

		final Set<Integer> parentIds = new HashSet<>();
		for (final Location location : locations) {
			parentIds.add(location.getParentLocationId());
		}

		final Map<Integer, Location> namesMap = new HashMap<>();
		if (!parentIds.isEmpty()) {
			namesMap.putAll(this.daoFactory.getLocationDAO().getNamesByIdsIntoMap(parentIds));
		}

		for (final Location location : locations) {
			final Optional<Location> tempParent = Optional.ofNullable(namesMap.get(location.getParentLocationId()));
			if (tempParent.isPresent()) {
				location.setParentLocationName(tempParent.get().getLname());
				location.setParentLocationAbbr(tempParent.get().getLabbr());
			}

		}
		return locations;
	}

	private int getNumericValue(final String strValue) {
		int value = 0;
		try {
			value = Integer.parseInt(strValue);
		} catch (final NumberFormatException e) {
			value = 0; // if it's nut a number, set to 0
		}
		return value;
	}

	@Override
	public List<Location> getAllBreedingLocations(final List<Integer> locationIds) {
		return this.daoFactory.getLocationDAO().getBreedingLocations(locationIds);
	}

	@Override
	public List<Location> getAllSeedingLocations(final List<Integer> locationIds) {
		final Integer seedLType = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.daoFactory.getLocationDAO().getSeedingLocations(locationIds, seedLType);
	}

	@Override
	public List<Locdes> getLocdes(final List<Integer> locIds, final List<String> dvals) {
		return this.daoFactory.getLocDesDao().getLocdes(locIds, dvals);
	}

	@Override
	public Integer getUserDefinedFieldIdOfCode(final UDTableType tableType, final String code) {
		final Map<String, UserDefinedField> types = new HashMap<>();

		final List<UserDefinedField> dTypeFields =
			this.getUserDefinedFieldByFieldTableNameAndType(tableType.getTable(), tableType.getType());
		for (final UserDefinedField dTypeField : dTypeFields) {
			types.put(dTypeField.getFcode(), dTypeField);
		}

		return types.get(code) != null ? types.get(code).getFldno() : null;
	}

	private Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(final UDTableType type) {
		final Map<String, UserDefinedField> types = new HashMap<>();

		final List<UserDefinedField> dTypeFields = this.getUserDefinedFieldByFieldTableNameAndType(type.getTable(), type.getType());
		for (final UserDefinedField dTypeField : dTypeFields) {
			types.put(dTypeField.getFcode(), dTypeField);
		}
		return types;
	}

	private List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(final String tableName, final String fieldType) {
		return this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(tableName, ImmutableSet.of(fieldType));
	}

}
