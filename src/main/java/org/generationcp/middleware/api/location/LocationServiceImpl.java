package org.generationcp.middleware.api.location;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
	public List<Location> getFilteredLocations(final LocationSearchRequest locationSearchRequest, final Pageable pageable) {
		this.retrieveFavoriteLocationsIfApplicable(locationSearchRequest);
		return this.daoFactory.getLocationDAO()
			.filterLocations(locationSearchRequest, pageable);
	}

	@Override
	public long countFilteredLocations(final LocationSearchRequest locationSearchRequest) {
		this.retrieveFavoriteLocationsIfApplicable(locationSearchRequest);
		return this.daoFactory.getLocationDAO().countFilterLocations(locationSearchRequest);
	}

	@Override
	public List<Integer> getFavoriteProjectLocationIds(final String programUUID) {
		final List<ProgramFavorite> programFavorites =
			this.daoFactory.getProgramFavoriteDao()
				.getProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, Integer.MAX_VALUE, programUUID);
		return programFavorites.stream().map(ProgramFavorite::getEntityId).collect(Collectors.toList());
	}

	private void retrieveFavoriteLocationsIfApplicable(final LocationSearchRequest locationSearchRequest) {
		if (!StringUtils.isEmpty(locationSearchRequest.getProgramUUID()) && locationSearchRequest.getFavoritesOnly()) {
			locationSearchRequest.getLocationIds().addAll(this.getFavoriteProjectLocationIds(locationSearchRequest.getProgramUUID()));
		}
	}
}
