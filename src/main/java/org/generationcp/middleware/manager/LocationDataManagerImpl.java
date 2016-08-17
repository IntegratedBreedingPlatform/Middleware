/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the LocationDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
@Transactional
public class LocationDataManagerImpl extends DataManager implements LocationDataManager {

	public LocationDataManagerImpl() {
	}

	public LocationDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public List<Location> getAllLocations() throws MiddlewareQueryException {
		List<Location> locations = this.getLocationDao().getAll();
		Collections.sort(locations);
		return locations;
	}

	@Override
	public List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getLocationDao().getAll(start, numOfRows);
	}

	@Override
	public long countAllLocations() throws MiddlewareQueryException {
		return this.countAll(this.getLocationDao());
	}

	@Override
	public List<Location> getLocationsByUniqueID(String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(this.getLocationDao().getByUniqueID(programUUID));
		return locations;
	}

	@Override
	public long countLocationsByUniqueID(String programUUID) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByUniqueID", new Object[] {programUUID}, new Class[] {String.class});
	}

	@Override
	public List<Location> getLocationsByName(String name, Operation op, String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(this.getLocationDao().getByNameAndUniqueID(name, op, programUUID));
		return locations;
	}

	@Override
	public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op, String programUUID)
			throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByNameAndUniqueID", "getByNameAndUniqueID");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {name, op, programUUID},
				new Class[] {String.class, Operation.class, String.class});
	}

	@Override
	public long countLocationsByName(String name, Operation op, String programUUID) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByNameAndUniqueID", new Object[] {name, op, programUUID}, new Class[] {
				String.class, Operation.class, String.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(this.getLocationDao().getByName(name, op));
		return locations;
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByName", "getByName");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {name, op}, new Class[] {
				String.class, Operation.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public long countLocationsByName(String name, Operation op) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByName", new Object[] {name, op}, new Class[] {String.class,
				Operation.class});
	}

	@Override
	public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByCountry", new Object[] {country}, new Class[] {Country.class});
	}

	@Override
	public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByCountry", "getByCountry");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {country},
				new Class[] {Country.class});
	}

	@Override
	public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByCountry", new Object[] {country}, new Class[] {Country.class});
	}

	@Override
	public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
		return this.getAllByMethod(this.getLocationDao(), "getByType", new Object[] {type}, new Class[] {Integer.class});
	}

	@Override
	public List<Location> getLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException {
		return this.getAllByMethod(this.getLocationDao(), "getByType", new Object[] {type, programUUID}, new Class[] {Integer.class,
				String.class});
	}

	@Override
	public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByType", "getByType");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {type},
				new Class[] {Integer.class});
	}

	@Override
	public long countLocationsByType(Integer type) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByType", new Object[] {type}, new Class[] {Integer.class});
	}

	@Override
	public long countLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByType", new Object[] {type, programUUID}, new Class[] {Integer.class,
				String.class});
	}

	@Override
	public UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException {
		return this.getUserDefinedFieldDao().getById(id, false);
	}

	@Override
	public Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(UDTableType type) throws MiddlewareQueryException {
		Map<String, UserDefinedField> types = new HashMap<String, UserDefinedField>();

		List<UserDefinedField> dTypeFields = this.getUserDefinedFieldByFieldTableNameAndType(type.getTable(), type.getType());
		for (UserDefinedField dTypeField : dTypeFields) {
			types.put(dTypeField.getFcode(), dTypeField);
		}
		return types;
	}

	@Override
	public Integer getUserDefinedFieldIdOfCode(UDTableType tableType, String code) throws MiddlewareQueryException {
		Map<String, UserDefinedField> types = new HashMap<String, UserDefinedField>();

		List<UserDefinedField> dTypeFields = this.getUserDefinedFieldByFieldTableNameAndType(tableType.getTable(), tableType.getType());
		for (UserDefinedField dTypeField : dTypeFields) {
			types.put(dTypeField.getFcode(), dTypeField);
		}

		return types.get(code) != null ? types.get(code).getFldno() : null;
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType)
			throws MiddlewareQueryException {
		return super.getAllByMethod(this.getUserDefinedFieldDao(), "getByFieldTableNameAndType", new Object[] {tableName, fieldType},
				new Class[] {String.class, String.class});
	}

	@Override
	public Country getCountryById(Integer id) throws MiddlewareQueryException {
		return this.getCountryDao().getById(id, false);
	}

	@Override
	public Location getLocationByID(Integer id) throws MiddlewareQueryException {
		return this.getLocationDao().getById(id, false);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Location> getLocationsByIDs(List<Integer> ids) throws MiddlewareQueryException {
		return this.getLocationDao().getLocationByIds(ids);
	}

	@Override
	public Integer addLocation(Location location) throws MiddlewareQueryException {

		Integer idLocationSaved = null;
		try {

			LocationDAO dao = this.getLocationDao();

			Location recordSaved = dao.saveOrUpdate(location);
			idLocationSaved = recordSaved.getLocid();

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Location: LocationDataManager.addLocation(location="
					+ location + "): " + e.getMessage(), e);
		}
		return idLocationSaved;
	}

	@Override
	public List<Integer> addLocation(List<Location> locations) throws MiddlewareQueryException {

		List<Integer> idLocationsSaved = new ArrayList<Integer>();
		try {

			LocationDAO dao = this.getLocationDao();

			for (Location location : locations) {
				Location recordSaved = dao.saveOrUpdate(location);
				idLocationsSaved.add(recordSaved.getLocid());
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Locations: LocationDataManager.addLocation(locations="
					+ locations + "): " + e.getMessage(), e);
		}
		return idLocationsSaved;
	}

	@Override
	public int addLocationAndLocdes(Location location, Locdes locdes) throws MiddlewareQueryException {

		Integer idLocationSaved = null;
		try {

			// Auto-assign IDs for new DB records
			LocationDAO locationDao = this.getLocationDao();
			Location recordSaved = locationDao.saveOrUpdate(location);
			idLocationSaved = recordSaved.getLocid();

			LocdesDAO locdesDao = this.getLocdesDao();
			locdes.setLocationId(idLocationSaved);
			locdesDao.saveOrUpdate(locdes);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Location: addLocationAndLocdes(" + "location=" + location
					+ ", locdes=" + locdes + "): " + e.getMessage(), e);
		}
		return idLocationSaved;
	}

	@Override
	public void deleteLocation(Location location) throws MiddlewareQueryException {

		try {

			this.getLocationDao().makeTransient(location);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered while deleting Location: LocationDataManager.deleteLocation(location="
					+ location + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Country> getAllCountry() throws MiddlewareQueryException {
		return super.getAllByMethod(this.getCountryDao(), "getAllCountry", new Object[] {}, new Class[] {});
	}

	@Override
	public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByCountryAndType", new Object[] {country, type}, new Class[] {Country.class,
				Integer.class});
	}

	@Override
	public List<Location> getLocationsByNameCountryAndType(String name, Country country, Integer type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByNameCountryAndType", new Object[] {name, country, type}, new Class[] {
				String.class, Country.class, Integer.class});
	}

	@Override
	public List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getLocationDetails", new Object[] {locationId, start, numOfRows}, new Class[] {
				Integer.class, Integer.class, Integer.class});

	}

	@Override
	public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {

		List<Location> allLocations =
				this.getFromInstanceByMethod(this.getLocationDAO(), Database.LOCAL, "getAllBreedingLocations", new Object[] {},
						new Class[] {});

		return allLocations;
	}

	@Override
	public Long countAllBreedingLocations() throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDAO(), "countAllBreedingLocations", new Object[] {}, new Class[] {});
	}

	@Override
	public List<Location> getAllFieldLocations(int locationId) throws MiddlewareQueryException {
		Integer fieldParentFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
		Integer fieldLtypeFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());

		return super.getAllByMethod(this.getLocationDao(), "getLocationsByDTypeAndLType", new Object[] {String.valueOf(locationId),
				fieldParentFldId, fieldLtypeFldId}, new Class[] {String.class, Integer.class, Integer.class});
	}

	@Override
	public List<Location> getAllBlockLocations(int fieldId) throws MiddlewareQueryException {
		Integer blockParentFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode());
		Integer blockLtypeFldId = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());

		return super.getAllByMethod(this.getLocationDao(), "getLocationsByDTypeAndLType", new Object[] {String.valueOf(fieldId),
				blockParentFldId, blockLtypeFldId}, new Class[] {String.class, Integer.class, Integer.class});
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(int blockId) throws MiddlewareQueryException {
		int rowsInBlock = 0;
		int rowsInPlot = 0;
		int rangesInBlock = 0;
		int plantingOrder = 0;
		int machineRowCapacity = 0;
		Integer fieldId = null;
		boolean isNew = true;

		Map<String, UserDefinedField> dTypes = this.getUserDefinedFieldMapOfCodeByUDTableType(UDTableType.LOCDES_DTYPE);

		List<Locdes> locdesOfLocation = this.getLocdesDao().getByLocation(blockId);
		List<String> deletedPlots = new ArrayList<String>();

		for (Locdes locdes : locdesOfLocation) {
			if (locdes != null) {
				int typeId = locdes.getTypeId();
				String value = locdes.getDval();
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
	public List<Location> getAllFields() throws MiddlewareQueryException {
		Integer fieldLtype = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		Integer relationshipType = this.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
		List<Location> locations =
				super.getAllByMethod(this.getLocationDao(), "getByTypeWithParent", new Object[] {fieldLtype, relationshipType},
						new Class[] {Integer.class, Integer.class});

		Set<Integer> parentIds = new HashSet<Integer>();
		for (Location location : locations) {
			parentIds.add(location.getParentLocationId());
		}

		Map<Integer, Location> namesMap = new HashMap<Integer, Location>();
		if (!parentIds.isEmpty()) {
			namesMap.putAll(this.getLocationDao().getNamesByIdsIntoMap(parentIds));
		}

		for (Location location : locations) {
			Location tempParent = namesMap.get(location.getParentLocationId());
			location.setParentLocationName(tempParent.getLname());
			location.setParentLocationAbbr(tempParent.getLabbr());
		}

		return locations;
	}

	@Override
	public List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException {
		List<Location> provinces =
				super.getAllByMethod(this.getLocationDao(), "getAllProvincesByCountry", new Object[] {countryId},
						new Class[] {Integer.class});

		return provinces;
	}

	@Override
	public List<Location> getAllProvinces() throws MiddlewareQueryException {
		List<Location> provinces = super.getAllByMethod(this.getLocationDao(), "getAllProvinces", new Object[] {}, new Class[] {});
		return provinces;
	}

	private int getNumericValue(String strValue) {
		int value = 0;
		try {
			value = Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			value = 0; // if it's nut a number, set to 0
		}
		return value;
	}

	@Override
	public List<Location> getProgramLocations(String programUUID) throws MiddlewareQueryException {
		return this.getLocationDao().getProgramLocations(programUUID);
	}

	@Override
	public void deleteProgramLocationsByUniqueId(String programUUID) throws MiddlewareQueryException {

		LocationDAO locationDao = this.getLocationDao();
		try {
			List<Location> list = this.getProgramLocations(programUUID);
			for (Location location : list) {
				locationDao.makeTransient(location);
			}
		} catch (Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while deleting locations: GermplasmDataManager.deleteProgramLocationsByUniqueId(uniqueId="
							+ programUUID + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Locdes> getLocdesByLocId(Integer locationId) throws MiddlewareQueryException {
		return this.getLocationDao().getLocdesByLocId(locationId);
	}

	@Override
	public void saveOrUpdateLocdesList(Integer locId, List<Locdes> locdesList) throws MiddlewareQueryException {

		if (locdesList != null && !locdesList.isEmpty()) {
			try {
				List<Locdes> existingLocdesList = this.getLocDesDao().getByLocation(locId);
				for (Locdes locdes : locdesList) {
					this.getLocdesSaver().saveOrUpdateLocdes(locdes.getLocationId(), existingLocdesList, locdes.getTypeId(),
							locdes.getDval(), locdes.getUserId());
				}
			} catch (Exception e) {
				this.logAndThrowException("Error encountered with saveOrUpdateLocdesList(): " + e.getMessage(), e);
			}
		}

	}

	@Override
	public List<Location> getAllSeedingLocations(String programUUID) {
		final Integer seedLType = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.getLocationDao().getByType(seedLType, programUUID);
	}

	@Override
	public List<Location> getAllBreedingLocations(final List<Integer> locationIds) {
		return this.getLocationDao().getBreedingLocations(locationIds);
	}

	@Override
	public List<Location> getAllSeedingLocations(final List<Integer> locationIds) {
		final Integer seedLType = this.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.getLocationDao().getSeedingLocations(locationIds, seedLType);
	}

	@Override
	public List<Location> getAllBreedingLocationsByUniqueID(String programUUID) {
		return this.getLocationDao().getBreedingLocationsByUniqueID(programUUID);
	}

}
