/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the LocationDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 *
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
public class LocationDataManagerImpl extends DataManager implements LocationDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(LocationDataManagerImpl.class);

    public LocationDataManagerImpl() {
    }

    public LocationDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public List<Location> getAllLocations() throws MiddlewareQueryException {
        List<Location> locations = getLocationDao().getAll();
        Collections.sort(locations);
        return locations;
    }


    @Override
    public List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException {
        return this.getLocationDao().getAll(start, numOfRows);
    }

    @Override
    public long countAllLocations() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getLocationDao());
    }
    
    @Override
    public List<Location> getLocationsByUniqueID(String programUUID) throws MiddlewareQueryException {
        List<Location> locations = new ArrayList<Location>();
        locations.addAll(getLocationDao().getByUniqueID(programUUID));
        return locations;
    }
    
    @Override
    public long countLocationsByUniqueID(String programUUID) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByUniqueID", new Object[]{ programUUID }, new Class[]{String.class});
    }
    
    @Override
    public List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        List<Location> locations = new ArrayList<Location>();
        locations.addAll(getLocationDao().getByName(name, op));
        return locations;
    }

    @Override
    public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByName", "getByName");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{name, op},
                new Class[]{String.class, Operation.class});
    }


    @Override
    public long countLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByName", new Object[]{name, op}, new Class[]{String.class,
                Operation.class});
    }

    @Override
    public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao(), "getByCountry", new Object[]{country},
                new Class[]{Country.class});
    }

    @Override
    public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByCountry", "getByCountry");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{country},
                new Class[]{Country.class});
    }

    @Override
    public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByCountry", new Object[]{country},
                new Class[]{Country.class});
    }

    @Override
    public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
        return (List<Location>) getAllByMethod(getLocationDao(), "getByType", new Object[]{type},
                new Class[]{Integer.class});
    }
    
    @Override
    public List<Location> getLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException {
        return (List<Location>) getAllByMethod(getLocationDao(), "getByType", new Object[]{type, programUUID},
                new Class[]{Integer.class, String.class});
    }

    @Override
    public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByType", "getByType");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{type},
                new Class[]{Integer.class});
    }

    @Override
    public long countLocationsByType(Integer type) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByType", new Object[]{type}, new Class[]{Integer.class});
    }
    
    @Override
    public long countLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByType", new Object[]{type,programUUID}, new Class[]{Integer.class, String.class});
    }

    @Override
    public UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException {
        return (UserDefinedField) getUserDefinedFieldDao().getById(id, false);
    }

    @Override
    public Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(UDTableType type)
            throws MiddlewareQueryException {
        Map<String, UserDefinedField> types = new HashMap<String, UserDefinedField>();

        List<UserDefinedField> dTypeFields = getUserDefinedFieldByFieldTableNameAndType(type.getTable(), type.getType());
        for (UserDefinedField dTypeField : dTypeFields) {
            types.put(dTypeField.getFcode(), dTypeField);
        }
        return types;
    }

    @Override
    public Integer getUserDefinedFieldIdOfCode(UDTableType tableType, String code)
            throws MiddlewareQueryException {
        Map<String, UserDefinedField> types = new HashMap<String, UserDefinedField>();

        List<UserDefinedField> dTypeFields = getUserDefinedFieldByFieldTableNameAndType(
                tableType.getTable(), tableType.getType());
        for (UserDefinedField dTypeField : dTypeFields) {
            types.put(dTypeField.getFcode(), dTypeField);
        }

        return types.get(code) != null ? types.get(code).getFldno() : null;
    }


    @Override
    public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType)
            throws MiddlewareQueryException {
        return (List<UserDefinedField>) super.getAllByMethod(getUserDefinedFieldDao()
                , "getByFieldTableNameAndType", new Object[]{tableName, fieldType}
                , new Class[]{String.class, String.class});
    }

    @Override
    public Country getCountryById(Integer id) throws MiddlewareQueryException {
        return getCountryDao().getById(id, false);
    }

    @Override
    public Location getLocationByID(Integer id) throws MiddlewareQueryException {
        return getLocationDao().getById(id, false);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Location> getLocationsByIDs(List<Integer> ids) throws MiddlewareQueryException {
        List<Location> results = new ArrayList<Location>();

        if (ids != null && ids.size() > 0) {
            results.addAll(getLocationDao().getLocationByIds(ids));
        }

        Collections.sort(results, new Comparator() {
            @Override
            public int compare(Object obj1, Object obj2) {
                Location loc1 = (Location) obj1;
                Location loc2 = (Location) obj2;
                return loc1.getLname().compareToIgnoreCase(loc2.getLname());
            }
        });

        return results;
    }

    @Override
    public List<LocationDetails> getLocationDetailsByLocationIDs(List<Integer> ids) throws MiddlewareQueryException {
        List<LocationDetails> results = new ArrayList<LocationDetails>();

        if (ids != null && ids.size() > 0) {
            List<Location> locations = getLocationDao().getLocationByIds(ids);

            for (Location l : locations) {
                Country c = this.getCountryById(l.getCntryid());
                UserDefinedField udf = this.getUserDefinedFieldByID(l.getLtype());

                LocationDetails ld = new LocationDetails();
                ld.setCntryid(l.getCntryid());
                ld.setCountry_full_name(c.getIsofull());
                ld.setLocation_abbreviation(l.getLabbr());
                ld.setLocation_name(l.getLname());
                ld.setLtype(l.getLtype());
                ld.setLocation_type(udf.getFname());
                ld.setLocation_description(udf.getFdesc());

                results.add(ld);
            }
        }

        return results;
    }


    @Override
    public Integer addLocation(Location location) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer idLocationSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LocationDAO dao = getLocationDao();

            // Auto-assign IDs for new DB records
            Integer nextId = dao.getNextId("locid");
            location.setLocid(nextId);

            Location recordSaved = dao.saveOrUpdate(location);
            idLocationSaved = recordSaved.getLocid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Location: LocationDataManager.addLocation(location=" + location + "): "
                    + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idLocationSaved;
    }

    @Override
    public List<Integer> addLocation(List<Location> locations) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        List<Integer> idLocationsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LocationDAO dao = getLocationDao();

            for (Location location : locations) {

                // Auto-assign IDs for new DB records
                Integer nextID = dao.getNextId("locid");
                location.setLocid(nextID);

                Location recordSaved = dao.saveOrUpdate(location);
                idLocationsSaved.add(recordSaved.getLocid());
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Locations: LocationDataManager.addLocation(locations=" + locations
                    + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idLocationsSaved;
    }

    @Override
    public int addLocationAndLocdes(Location location, Locdes locdes) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer idLocationSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Auto-assign IDs for new DB records
            LocationDAO locationDao = getLocationDao();
            Integer nextId = locationDao.getNextId("locid");
            location.setLocid(nextId);
            Location recordSaved = locationDao.saveOrUpdate(location);
            idLocationSaved = recordSaved.getLocid();

            LocdesDAO locdesDao = getLocdesDao();
            nextId = locdesDao.getNextId("ldid");
            locdes.setLdid(nextId);
            locdes.setLocationId(idLocationSaved);
            locdesDao.saveOrUpdate(locdes);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Location: addLocationAndLocdes(" +
                    "location=" + location + ", locdes=" + locdes + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idLocationSaved;
    }

    @Override
    public void deleteLocation(Location location) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getLocationDao().makeTransient(location);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting Location: LocationDataManager.deleteLocation(location=" + location
                    + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
    }

    @Override
    public List<Country> getAllCountry() throws MiddlewareQueryException {
        return (List<Country>) super.getAllByMethod(getCountryDao(), "getAllCountry", new Object[]{}, new Class[]{});
    }

    @Override
    public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao(), "getByCountryAndType", new Object[]{country,
                type}, new Class[]{Country.class, Integer.class});
    }

    @Override
    public List<Location> getLocationsByNameCountryAndType(String name, Country country, Integer type)
            throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao()
                , "getByNameCountryAndType", new Object[]{name, country,
                type}, new Class[]{String.class, Country.class, Integer.class});
    }

    @Override
    public List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<LocationDetails>) super.getAllByMethod(getLocationDao()
                , "getLocationDetails", new Object[]{locationId,
                start, numOfRows}, new Class[]{Integer.class, Integer.class, Integer.class});

    }

    @Override
    public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {

        List<Location> allLocations = getFromInstanceByMethod(getLocationDAO()
                , Database.LOCAL, "getAllBreedingLocations", new Object[]{}, new Class[]{});

        return allLocations;
    }


    @Override
    public Long countAllBreedingLocations() throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDAO()
                , "countAllBreedingLocations", new Object[]{}, new Class[]{});
    }

    @Override
    public Integer getNextNegativeId() throws MiddlewareQueryException {
        return getLocationDao().getNextId("locid");

    }

    @Override
    public List<Location> getAllFieldLocations(int locationId)
            throws MiddlewareQueryException {
        Integer fieldParentFldId = getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
        Integer fieldLtypeFldId = getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());

        return super.getAllByMethod(getLocationDao(), "getLocationsByDTypeAndLType"
                , new Object[]{String.valueOf(locationId), fieldParentFldId, fieldLtypeFldId}
                , new Class[]{String.class, Integer.class, Integer.class});
    }

    @Override
    public List<Location> getAllBlockLocations(int fieldId)
            throws MiddlewareQueryException {
        Integer blockParentFldId = getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode());
        Integer blockLtypeFldId = getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());

        return super.getAllByMethod(getLocationDao(), "getLocationsByDTypeAndLType"
                , new Object[]{String.valueOf(fieldId), blockParentFldId, blockLtypeFldId}
                , new Class[]{String.class, Integer.class, Integer.class});
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

        Map<String, UserDefinedField> dTypes = getUserDefinedFieldMapOfCodeByUDTableType(UDTableType.LOCDES_DTYPE);

        List<Locdes> locdesOfLocation = getLocdesDao().getByLocation(blockId);
        List<String> deletedPlots = new ArrayList<String>();

        for (Locdes locdes : locdesOfLocation) {
            if (locdes != null) {
                int typeId = locdes.getTypeId();
                String value = locdes.getDval();
                if (typeId == dTypes.get(LocdesType.ROWS_IN_BLOCK.getCode()).getFldno()) {
                    rowsInBlock = getNumericValue(value);
                } else if (typeId == dTypes.get(LocdesType.ROWS_IN_PLOT.getCode()).getFldno()) {
                    rowsInPlot = getNumericValue(value);
                } else if (typeId == dTypes.get(LocdesType.RANGES_IN_BLOCK.getCode()).getFldno()) {
                    rangesInBlock = getNumericValue(value);
                } else if (typeId == dTypes.get(LocdesType.PLANTING_ORDER.getCode()).getFldno()) {
                    plantingOrder = getNumericValue(value);
                } else if (typeId == dTypes.get(LocdesType.MACHINE_ROW_CAPACITY.getCode()).getFldno()) {
                    machineRowCapacity = getNumericValue(value);
                } else if (typeId == dTypes.get(LocdesType.DELETED_PLOTS.getCode()).getFldno()) {
                    deletedPlots.add(value);
                } else if (typeId == dTypes.get(LocdesType.BLOCK_PARENT.getCode()).getFldno()) {
                    fieldId = getNumericValue(value);
                }
            }
        }

        if (rowsInBlock > 0 || rangesInBlock > 0 || rowsInPlot > 0) {
            isNew = false;
        }

        return new FieldmapBlockInfo(blockId, rowsInBlock, rangesInBlock, rowsInPlot, plantingOrder, machineRowCapacity, isNew, deletedPlots, fieldId);
    }

    @Override
    public List<Location> getAllFields() throws MiddlewareQueryException {
        Integer fieldLtype = getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
        Integer relationshipType = getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.FIELD_PARENT.getCode());
        List<Location> locations = super.getAllByMethod(getLocationDao(), "getByTypeWithParent"
                , new Object[]{fieldLtype, relationshipType}, new Class[]{Integer.class, Integer.class});

        Set<Integer> parentIds = new HashSet<Integer>();
        for (Location location : locations) {
            parentIds.add(location.getParentLocationId());
        }

        Map<Integer, String> namesMap = new HashMap<Integer, String>();
        if (!parentIds.isEmpty()) {
            namesMap.putAll(getLocationDao().getNamesByIdsIntoMap(parentIds));
        }

        for (Location location : locations) {
            location.setParentLocationName(namesMap.get(location.getParentLocationId()));
        }

        return locations;
    }

    @Override
    public List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException {
        List<Location> provinces = super.getAllByMethod(getLocationDao(), "getAllProvincesByCountry", new Object[]{countryId}, new Class[]{Integer.class});

        return provinces;
    }

    @Override
    public List<Location> getAllProvinces() throws MiddlewareQueryException {
        List<Location> provinces = super.getAllByMethod(getLocationDao(), "getAllProvinces", new Object[]{}, new Class[]{});
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
	public List<Location> getProgramLocations(String programUUID)
			throws MiddlewareQueryException {
		return getLocationDao().getProgramLocations(programUUID);
	}
	
    @Override
	public void deleteProgramLocationsByUniqueId(String programUUID) throws MiddlewareQueryException {
		Session session = getCurrentSession();
		Transaction trans = null;
		LocationDAO locationDao = getLocationDao();
		int deleted = 0;
		try {
			trans = session.beginTransaction();
			List<Location> list = getProgramLocations(programUUID);
			for (Location location : list) {
				locationDao.makeTransient(location);
				if (deleted % JDBC_BATCH_SIZE == 0) {
					locationDao.flush();
					locationDao.clear();
				}
			}
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while deleting locations: GermplasmDataManager.deleteProgramLocationsByUniqueId(uniqueId="
					+ programUUID + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}
	}

}
