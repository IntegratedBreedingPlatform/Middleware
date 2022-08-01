/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager;

import com.google.common.collect.ImmutableSet;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the LocationDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 *
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
@Transactional
public class LocationDataManagerImpl extends DataManager implements LocationDataManager {

	private DaoFactory daoFactory;

	public LocationDataManagerImpl() {
	}

	public LocationDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Location> getAllLocations() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getAll();
		Collections.sort(locations);
		return locations;
	}

	@Override
	public List<Location> getLocationsByName(final String name, final Operation op) {
		final List<Location> locations = new ArrayList<>();
		locations.addAll(this.daoFactory.getLocationDAO().getByName(name, op));
		return locations;
	}

	@Override
	public List<Location> getLocationsByName(final String name, final int start, final int numOfRows, final Operation op) {
		return this.daoFactory.getLocationDAO().getByName(name, op, start, numOfRows);
	}

	@Override
	public Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(final UDTableType type) {
		final Map<String, UserDefinedField> types = new HashMap<>();

		final List<UserDefinedField> dTypeFields = this.getUserDefinedFieldByFieldTableNameAndType(type.getTable(), type.getType());
		for (final UserDefinedField dTypeField : dTypeFields) {
			types.put(dTypeField.getFcode(), dTypeField);
		}
		return types;
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

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(final String tableName, final String fieldType) {
		return this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(tableName, ImmutableSet.of(fieldType));
	}

	@Override
	public Location getLocationByID(final Integer id) {
		return this.daoFactory.getLocationDAO().getById(id, false);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Location> getLocationsByIDs(final List<Integer> ids) {
		return this.daoFactory.getLocationDAO().getLocationByIds(ids);
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

	public String retrieveLocIdOfUnspecifiedLocation() {
		String unspecifiedLocationId = "";
		final List<Location> locations = this.getLocationsByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			unspecifiedLocationId = String.valueOf(locations.get(0).getLocid());
		}
		return unspecifiedLocationId;
	}

	@Override
	public List<Locdes> getLocdes(final List<Integer> locIds, final List<String> dvals) {
		return this.daoFactory.getLocDesDao().getLocdes(locIds, dvals);
	}
}
