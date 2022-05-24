package org.generationcp.middleware.api.location;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class LocationServiceImpl implements LocationService {

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
	public List<LocationDTO> searchLocations(final LocationSearchRequest locationSearchRequest,
		final Pageable pageable, final String programUUID) {
		return this.daoFactory.getLocationDAO().searchLocations(locationSearchRequest, pageable, programUUID);
	}

	@Override
	public long countFilteredLocations(final LocationSearchRequest locationSearchRequest, final String programUUID) {
		return this.daoFactory.getLocationDAO().countSearchLocation(locationSearchRequest, programUUID);
	}

	@Override
	public List<org.generationcp.middleware.api.location.Location> getLocations(final LocationSearchRequest locationSearchRequest,
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
	public LocationDTO createLocation(final LocationRequestDto locationRequestDto) {

		final Country country = (locationRequestDto.getCountryId() == null) ? null :
			this.daoFactory.getCountryDao().getById(locationRequestDto.getCountryId());
		final Location province = (locationRequestDto.getProvinceId() == null) ? null :
			this.daoFactory.getLocationDAO().getById(locationRequestDto.getProvinceId());

		final Location newLocation = new Location(null, locationRequestDto.getType(),
			0, locationRequestDto.getName(), locationRequestDto.getAbbreviation(),
			0, 0, province, country, 0);
		newLocation.setLdefault(false);

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
	public ProgramLocationDefault saveProgramLocationDefault(final String programUUID, final Integer locationId) {
		return this.daoFactory.getProgramLocationDefaultDAO().save(new ProgramLocationDefault(programUUID, locationId));
	}

	@Override
	public void updateProgramLocationDefault(final String programUUID, final Integer locationId) {
		final ProgramLocationDefault programLocationDefault = this.daoFactory.getProgramLocationDefaultDAO().getByprogramUUID(programUUID);
		programLocationDefault.setLocationId(locationId);
		this.daoFactory.getProgramLocationDefaultDAO().update(programLocationDefault);
	}

	@Override
	public ProgramLocationDefault getProgramLocationDefault(final String programUUID) {
		return this.daoFactory.getProgramLocationDefaultDAO().getByprogramUUID(programUUID);
	}

	@Override
	public LocationDTO getDefaultLocation(final String programUUID) {
		final ProgramLocationDefault programLocationDefault = this.daoFactory.getProgramLocationDefaultDAO().getByprogramUUID(programUUID);
		return this.getLocation(programLocationDefault.getLocationId());
	}
}
