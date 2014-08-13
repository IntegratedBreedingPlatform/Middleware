/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.germplasm.BackcrossElement;
import org.generationcp.middleware.pojos.germplasm.GermplasmCross;
import org.generationcp.middleware.pojos.germplasm.GermplasmCrossElement;
import org.generationcp.middleware.pojos.germplasm.SingleGermplasmCrossElement;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the GermplasmDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 * 
 * @author Kevin Manansala, Lord Hendrix Barboza
 * 
 */
@SuppressWarnings("unchecked")
public class GermplasmDataManagerImpl extends DataManager implements GermplasmDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(GermplasmDataManagerImpl.class);

    public GermplasmDataManagerImpl() {
    }

    public GermplasmDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GermplasmDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }
    
    @Override
    @Deprecated
    public List<Location> getAllLocations(int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Location>) getFromCentralAndLocal(getLocationDao(), start, numOfRows);
    }
    
    @Override
    @Deprecated
    public List<Location> getAllLocations() throws MiddlewareQueryException{
        List<Location> locations = getAllFromCentralAndLocal(getLocationDao());
        Collections.sort(locations);
        return locations;
    }
    
    @Override
    @Deprecated
    public List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return this.getLocationDao().getAll(start, numOfRows);
        }
        return new ArrayList<Location>();
    }

    @Override
    @Deprecated
    public long countAllLocations() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getLocationDao());
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        List<Location> locations = new ArrayList<Location>();
        if (setWorkingDatabase(Database.LOCAL)) {
            locations.addAll(getLocationDao().getByName(name, op));
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            locations.addAll(getLocationDao().getByName(name, op));
        }
        return locations;
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByName", "getByName");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { name, op },
                new Class[] { String.class, Operation.class });
    }



    @Override
    @Deprecated
    public long countLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByName", new Object[] { name, op }, new Class[] { String.class,
                Operation.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
        return (List<Location>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getByCountry", new Object[] { country },
                new Class[] { Country.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByCountry", "getByCountry");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { country },
                new Class[] { Country.class });
    }
        
    @Override
    @Deprecated
    public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByCountry", new Object[] { country },
                new Class[] { Country.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
        return (List<Location>) getAllFromCentralAndLocalByMethod(getLocationDao(), "getByType", new Object[] { type },
                new Class[] { Integer.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByType", "getByType");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { type },
                new Class[] { Integer.class });
    }

    @Override
    @Deprecated
    public long countLocationsByType(Integer type) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByType", new Object[] { type }, new Class[] { Integer.class });
    }

    @Override
    public List<Germplasm> getAllGermplasm(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        return (List<Germplasm>) super.getFromInstanceByMethod(getGermplasmDao(), instance, "getAll", 
                new Object[]{start, numOfRows}, new Class[]{Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countAllGermplasm(Database instance) throws MiddlewareQueryException {
        return super.countFromInstance(getGermplasmDao(), instance);
    }

    @Override
    public List<Germplasm> getGermplasmByPrefName(String name, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        return (List<Germplasm>) getFromInstanceByMethod(getGermplasmDao(), instance, "getByPrefName", new Object[] { name, start,
                numOfRows }, new Class[] { String.class, Integer.TYPE, Integer.TYPE });
    }

    @Override
    public long countGermplasmByPrefName(String name) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getGermplasmDao(), "countByPrefName", new Object[] { name },
                new Class[] { String.class });
    }

    @Override
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, GetGermplasmByNameModes mode, Operation op,
            Integer status, GermplasmNameType type, Database instance) throws MiddlewareQueryException {
        String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
        return (List<Germplasm>) super.getFromInstanceByMethod(getGermplasmDao(), instance, "getByName", new Object[] { nameToUse, op,
                status, type, start, numOfRows }, new Class[] { String.class, Operation.class, Integer.class, GermplasmNameType.class,
                Integer.TYPE, Integer.TYPE });
    }

    @Override
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
        List<String> methods = Arrays.asList("countByName", "getByName");
        return (List<Germplasm>) getFromCentralAndLocalByMethod(getGermplasmDao(), methods, start, numOfRows, new Object[] { names, op },
                new Class[] { List.class, Operation.class });
    }

    @Override
    public long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
            Database instance) throws MiddlewareQueryException {
        String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
        return super.countFromInstanceByMethod(getGermplasmDao(), instance, "countByName", new Object[] { nameToUse, op, status, type },
                new Class[] { String.class, Operation.class, Integer.class, GermplasmNameType.class });
    }

    @Override
    public long countGermplasmByName(String name, Operation operation) throws MiddlewareQueryException {
        long count = 0;
        
        setWorkingDatabase(Database.LOCAL);
        count = getGermplasmDao().countByName(name, operation, null, null);
        
        setWorkingDatabase(Database.CENTRAL);
        count = count + getGermplasmDao().countByName(name, operation, null, null);
        
        return count;
    }
    
    @Override
	public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
    	List<Germplasm> germplasms = new ArrayList<Germplasm>();
    	
    	//get first all the IDs from LOCAL and CENTRAL
    	List<Integer> germplasmIds = new ArrayList<Integer>();
    	setWorkingDatabase(Database.LOCAL);
    	germplasmIds.addAll(getGermplasmDao().getIdsByName(name, start, numOfRows));
    	setWorkingDatabase(Database.CENTRAL);
    	germplasmIds.addAll(getGermplasmDao().getIdsByName(name, start, numOfRows));
    	
    	// check if there is a LOCAL germplasm
    	boolean hasLocalIds = false;
    	for(Integer id : germplasmIds){
    		if(id < 0){
    			hasLocalIds = true;
    			break;
    		}
    	}
    	// check if there is a CENTRAL germplasm
    	boolean hasCentralIds = false;
    	for(Integer id : germplasmIds){
    		if(id < 0){
    			hasCentralIds = true;
    			break;
    		}
    	}
    	
    	if(hasLocalIds){
    		setWorkingDatabase(Database.LOCAL);
    		germplasms.addAll(getGermplasmDao().getGermplasmByIds(germplasmIds, start, numOfRows));
    	}
    	
    	if(hasCentralIds){
    		setWorkingDatabase(Database.CENTRAL);
    		germplasms.addAll(getGermplasmDao().getGermplasmByIds(germplasmIds, start, numOfRows));
    	}
    	
    	return germplasms;
    }

    @Override
    public List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Germplasm> germplasms = new ArrayList<Germplasm>();
        if (setWorkingDatabase(instance)) {
            GermplasmDAO dao = getGermplasmDao();
            if (op == Operation.EQUAL) {
                germplasms = dao.getByLocationNameUsingEqual(name, start, numOfRows);
            } else if (op == Operation.LIKE) {
                germplasms = dao.getByLocationNameUsingLike(name, start, numOfRows);
            }
        }
        return germplasms;
    }

    @Override
    public long countGermplasmByLocationName(String name, Operation op, Database instance) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(instance)) {
            GermplasmDAO dao = getGermplasmDao();
            if (op == Operation.EQUAL) {
                count = dao.countByLocationNameUsingEqual(name);
            } else if (op == Operation.LIKE) {
                count = dao.countByLocationNameUsingLike(name);
            }
        }
        return count;
    }

    @Override
    public List<Germplasm> getGermplasmByMethodName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Germplasm> germplasms = new ArrayList<Germplasm>();

        if (setWorkingDatabase(instance)) {
            GermplasmDAO dao = getGermplasmDao();
            if (op == Operation.EQUAL) {
                germplasms = dao.getByMethodNameUsingEqual(name, start, numOfRows);
            } else if (op == Operation.LIKE) {
                germplasms = dao.getByMethodNameUsingLike(name, start, numOfRows);
            }
        }
        return germplasms;
    }

    @Override
    public long countGermplasmByMethodName(String name, Operation op, Database instance) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(instance)) {
            GermplasmDAO dao = getGermplasmDao();
            if (op == Operation.EQUAL) {
                count = dao.countByMethodNameUsingEqual(name);
            } else if (op == Operation.LIKE) {
                count = dao.countByMethodNameUsingLike(name);
            }
        }
        return count;
    }

    @Override
    public Germplasm getGermplasmByGID(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return (Germplasm) getGermplasmDao().getById(gid, false);
        }
        return null;
    }

    @Override
    public Germplasm getGermplasmWithPrefName(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return (Germplasm) getGermplasmDao().getByGIDWithPrefName(gid);
        }
        return null;
    }

    @Override
    public Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return (Germplasm) getGermplasmDao().getByGIDWithPrefAbbrev(gid);
        }
        return null;
    }

    @Override
    public Name getGermplasmNameByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return (Name) getNameDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException {      
        return super.getAllFromCentralAndLocalByMethod(getNameDao(), "getByGIDWithFilters", 
                new Object[]{gid, status, type}, new Class[]{Integer.class, Integer.class, GermplasmNameType.class});
//        return (List<Name>) super.getFromInstanceByIdAndMethod(getNameDao(), gid, "getByGIDWithFilters", 
//                new Object[]{gid, status, type}, new Class[]{Integer.class, Integer.class, GermplasmNameType.class});
    }

    @Override
    public Name getPreferredNameByGID(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            List<Name> names = getNameDao().getByGIDWithFilters(gid, 1, null);
            if (!names.isEmpty()) {
                return names.get(0);
            }
        }
        return null;
    }
    
    @Override
    public String getPreferredNameValueByGID(Integer gid) throws MiddlewareQueryException{
        if (setWorkingDatabase(gid)) {
            List<Name> names = getNameDao().getByGIDWithFilters(gid, 8, null);
            if (!names.isEmpty()) {
                return names.get(0).getNval();
            }
        }
        return null;
    }

    @Override
    public Name getPreferredAbbrevByGID(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            List<Name> names = getNameDao().getByGIDWithFilters(gid, 2, null);
            if (!names.isEmpty()) {
                return names.get(0);
            }
        }
        return null;
    }
    
    @Override
    public Name getPreferredIdByGID(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            List<Name> names = getNameDao().getByGIDWithFilters(gid, 8, null);
            if (!names.isEmpty()) {
                return names.get(0);
            }
        }
        return null;
    }
    
    @Override
    public List<Name> getPreferredIdsByListId(Integer listId) throws MiddlewareQueryException {
        if (setWorkingDatabase(listId)) {
            return getNameDao().getPreferredIdsByListId(listId);
        }
        return null;
    }

    @Override
    public Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
        }
        return null;
    }

    @Override
    public Integer updateGermplasmPrefName(Integer gid, String newPrefName) throws MiddlewareQueryException {
        updateGermplasmPrefNameAbbrev(gid, newPrefName, "Name");
        return gid;
    }

    @Override
    public Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev) throws MiddlewareQueryException {
        updateGermplasmPrefNameAbbrev(gid, newPrefAbbrev, "Abbreviation");
        return gid;
    }

    private void updateGermplasmPrefNameAbbrev(Integer gid, String newPrefValue, String nameOrAbbrev) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin update transaction
            trans = session.beginTransaction();
            NameDAO dao = getNameDao();

            // check for a name record with germplasm = gid, and nval = newPrefName
            Name newPref = getNameByGIDAndNval(gid, newPrefValue, GetGermplasmByNameModes.NORMAL);
            // if a name record with the specified nval exists,
            if (newPref != null) {
                // get germplasm's existing preferred name/abbreviation, set as
                // alternative name, change nstat to 0
                Name oldPref = null;
                int newNstat = 0; // nstat to be assigned to newPref: 1 for
                // Name, 2 for Abbreviation

                if ("Name".equals(nameOrAbbrev)) {
                    oldPref = getPreferredNameByGID(gid);
                    newNstat = 1;
                } else if ("Abbreviation".equals(nameOrAbbrev)) {
                    oldPref = getPreferredAbbrevByGID(gid);
                    newNstat = 2;
                }

                if (oldPref != null) {
                    oldPref.setNstat(0);
                    dao.validateId(oldPref); // check if old Name is a local DB record
                    dao.saveOrUpdate(oldPref);
                }

                newPref.setNstat(newNstat); // update specified name as the new preferred name/abbreviation
                dao.validateId(newPref); // check if new Name is a local DB record
                dao.saveOrUpdate(newPref); // save the new name's status to the database
            } else {
                // throw exception if no Name record with specified value does not exist
                logAndThrowException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue="
                        + newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "): The specified Germplasm Name does not exist.", new Throwable(), LOG);
            }

            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue="
                    + newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "):  " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
    }

    @Override
    public Integer addGermplasmName(Name name) throws MiddlewareQueryException {
        List<Name> names = new ArrayList<Name>();
        names.add(name);
        List<Integer> ids = addOrUpdateGermplasmName(names, Operation.ADD);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasmName(List<Name> names) throws MiddlewareQueryException {
        return addOrUpdateGermplasmName(names, Operation.ADD);
    }

    @Override
    public Integer updateGermplasmName(Name name) throws MiddlewareQueryException {
        List<Name> names = new ArrayList<Name>();
        names.add(name);
        List<Integer> ids = addOrUpdateGermplasmName(names, Operation.UPDATE);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmName(List<Name> names) throws MiddlewareQueryException {
        return addOrUpdateGermplasmName(names, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmName(List<Name> names, Operation operation) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        int namesSaved = 0;
        List<Integer> idNamesSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            NameDAO dao = getNameDao();

            for (Name name : names) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("nid");
                    name.setNid(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if Name is a local DB record. Throws exception if
                    // Name is a central DB record.
                    dao.validateId(name);
                }
                Name recordAdded = dao.saveOrUpdate(name);
                idNamesSaved.add(recordAdded.getNid());
                namesSaved++;
                if (namesSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error while saving Germplasm Name: GermplasmDataManager.addOrUpdateGermplasmName(names=" + names
                    + ", operation=" + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idNamesSaved;
    }

    @Override
    public List<Attribute> getAttributesByGID(Integer gid) throws MiddlewareQueryException {
        return (List<Attribute>) super.getFromInstanceByIdAndMethod(getAttributeDao(), gid, "getByGID", 
                new Object[]{gid}, new Class[]{Integer.class});
    }
    
    @Override
    public List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList) throws MiddlewareQueryException {
        return (List<UserDefinedField>) super.getAllFromCentralAndLocalByMethod(getAttributeDao(), "getAttributeTypesByGIDList",
                new Object[] { gidList }, new Class[] { List.class });
    }
    
    @Override
    public Map<Integer, String> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList) throws MiddlewareQueryException {
        Map<Integer, String> returnMap = new HashMap<Integer, String>();
        // initialize map with GIDs
        for (Integer gid : gidList) {
            returnMap.put(gid, "-");
        }
        
        // retrieve attribute values
        List<Attribute> attributeList = super.getAllFromCentralAndLocalByMethod(getAttributeDao(), "getAttributeValuesByTypeAndGIDList",
                new Object[] { attributeType, gidList }, new Class[] { Integer.class, List.class });
        for (Attribute attribute : attributeList) {
            returnMap.put(attribute.getGermplasmId(), attribute.getAval());
        }
        
        return returnMap;
    }

    @Override
    public Method getMethodByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return (Method) getMethodDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Method> getMethodsByIDs(List<Integer> ids) throws MiddlewareQueryException {
        List<Method> results = new ArrayList<Method>();

        List<Integer> pos = new ArrayList<Integer>();
        List<Integer> negs = new ArrayList<Integer>();

        //separate ids from local and central
        for(Integer id : ids){
            if(id > 0){
                pos.add(id);
            } else {
                negs.add(id);
            }
        }

        if (!pos.isEmpty() && setWorkingDatabase(Database.CENTRAL)) {
            results.addAll(getMethodDao().getMethodsByIds(pos));
        }


        if (!negs.isEmpty() && setWorkingDatabase(Database.LOCAL)) {
            results.addAll(getMethodDao().getMethodsByIds(negs));
        }

        return results;
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        return (List<Method>) getAllFromCentralAndLocalByMethod(getMethodDao(), "getAllMethod", new Object[] {}, new Class[] {});
    }

    @Override
    public List<Method> getAllMethodsNotGenerative() throws MiddlewareQueryException {
        return (List<Method>) getAllFromCentralAndLocalByMethod(getMethodDao(), "getAllMethodsNotGenerative", new Object[] {}, new Class[] {});
    }

    @Override
    public long countAllMethods() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getMethodDao());
    }

    @Override
    public List<Method> getMethodsByType(String type) throws MiddlewareQueryException {
        return (List<Method>) super.getAllFromCentralAndLocalByMethod(getMethodDao(), "getByType", new Object[] { type },
                new Class[] { String.class });
    }

    @Override
    public List<Method> getMethodsByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByType", "getByType");
        return (List<Method>) super.getFromCentralAndLocalByMethod(getMethodDao(), methods, start, numOfRows, new Object[] { type },
                new Class[] { String.class });
    }

    @Override
    public long countMethodsByType(String type) throws MiddlewareQueryException {
        return super
                .countAllFromCentralAndLocalByMethod(getMethodDao(), "countByType", new Object[] { type }, new Class[] { String.class });
    }

    @Override
    public List<Method> getMethodsByGroup(String group) throws MiddlewareQueryException {
        return (List<Method>) super.getAllFromCentralAndLocalByMethod(getMethodDao(), "getByGroup", new Object[] { group },
                new Class[] { String.class });
    }

    @Override
    public List<Method> getMethodsByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByGroup", "getByGroup");
        return (List<Method>) super.getFromCentralAndLocalByMethod(getMethodDao(), methods, start, numOfRows, new Object[] { group },
                new Class[] { String.class });
    }

    @Override
    public List<Method> getMethodsByGroupAndType(String group, String type) throws MiddlewareQueryException {
        return (List<Method>) super.getAllFromCentralAndLocalByMethod(getMethodDao(), "getByGroupAndType", new Object[] { group, type },
                new Class[] { String.class, String.class });
    }
    
    @Override
    public List<Method> getMethodsByGroupAndTypeAndName(String group, String type, String name) throws MiddlewareQueryException {
        return (List<Method>) super.getAllFromCentralAndLocalByMethod(getMethodDao(), "getByGroupAndTypeAndName", new Object[] { group, type, name },
                new Class[] { String.class, String.class, String.class });
    }

    @Override
    public long countMethodsByGroup(String group) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMethodDao(), "countByGroup", new Object[] { group },
                new Class[] { String.class });
    }

    @Override
    public Integer addMethod(Method method) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer methodId = null;
        try {
            trans = session.beginTransaction();
            MethodDAO dao = getMethodDao();

            // Auto-assign negative IDs for new local DB records
            Integer negativeId = dao.getNegativeId("mid");
            method.setMid(negativeId);

            Method recordSaved = dao.saveOrUpdate(method);
            methodId = recordSaved.getMid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }
        return methodId;
    }


    @Override
    public Method editMethod(Method method) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Method recordSaved = null;

        try {

            if (method.getMid() == null || method.getMid() > 0)
                throw new Exception("method has no Id or is not a local method");

            trans = session.beginTransaction();
            MethodDAO dao = getMethodDao();

            recordSaved = dao.merge(method);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }

        return recordSaved;
    }

    @Override
    public List<Integer> addMethod(List<Method> methods) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        List<Integer> idMethodsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            MethodDAO dao = getMethodDao();

            for (Method method : methods) {
                // Auto-assign negative IDs for new local DB records
                Integer negativeId = dao.getNegativeId("mid");
                method.setMid(negativeId);

                Method recordSaved = dao.saveOrUpdate(method);
                idMethodsSaved.add(recordSaved.getMid());
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving a list of Methods: GermplasmDataManager.addMethod(methods=" + methods
                    + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idMethodsSaved;
    }

    @Override
    public void deleteMethod(Method method) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getMethodDao().makeTransient(method);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting Method: GermplasmDataMananger.deleteMethod(method=" + method + "): " + e.getMessage(),
                    e, LOG);
        } finally {
            session.flush();
        }
    }

    @Override
    public UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return (UserDefinedField) getUserDefinedFieldDao().getById(id, false);
        }
        return null;
    }

    @Override
    @Deprecated
    public Country getCountryById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getCountryDao().getById(id, false);
        }
        return null;
    }

    @Override
    @Deprecated
    public Location getLocationByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getLocationDao().getById(id, false);
        }
        return null;
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByIDs(List<Integer> ids) throws  MiddlewareQueryException {
        List<Location> results = new ArrayList<Location>();

        List<Integer> pos = new ArrayList<Integer>();
        List<Integer> negs = new ArrayList<Integer>();

        //separate ids from local and central
        for(Integer id : ids){
            if(id > 0){
                pos.add(id);
            } else {
                negs.add(id);
            }
        }

        if (pos != null && setWorkingDatabase(Database.CENTRAL) && pos.size()>0) {
            results.addAll(getLocationDao().getLocationByIds(pos));
        }

        if (negs != null && setWorkingDatabase(Database.LOCAL) && negs.size()>0) {
           results.addAll(getLocationDao().getLocationByIds(negs));
        }
        
        //loc.getLname();
        
        Collections.sort(results, new Comparator<Object>() {  
            @Override  
            public int compare(Object obj1, Object obj2) {  
                Location loc1 = (Location)obj1;  
                Location loc2 = (Location)obj2;  
                return loc1.getLname().compareToIgnoreCase(loc2.getLname());  
            }  
        });  
        
        return results;
    }

    @Override
    @Deprecated
    public List<LocationDetails> getLocationDetailsByLocationIDs(List<Integer> ids) throws  MiddlewareQueryException {
        List<LocationDetails> results = new ArrayList<LocationDetails>();

        List<Integer> pos = new ArrayList<Integer>();
        List<Integer> negs = new ArrayList<Integer>();

        //separate ids from local and central
        for(Integer id : ids){
            if(id > 0){
                pos.add(id);
            } else {
                negs.add(id);
            }
        }

        if (pos != null && pos.size() > 0 && setWorkingDatabase(Database.CENTRAL)) {
            results.addAll(getLocationDao().getLocationDetails(pos,0,pos.size()));
        }

        if (negs != null && negs.size() > 0 && setWorkingDatabase(Database.LOCAL)) {
              List<Location> locations = getLocationDao().getLocationByIds(negs);

              for (Location l : locations) {
                  Country c = this.getCountryById(l.getCntryid());
                  UserDefinedField udf = this.getUserDefinedFieldByID(l.getLtype());

                  // hack, reset working database to local
                  //setWorkingDatabase(Database.LOCAL);

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
    @Deprecated
    public Integer addLocation(Location location) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idLocationSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LocationDAO dao = getLocationDao();

            // Auto-assign negative IDs for new local DB records
            Integer negativeId = dao.getNegativeId("locid");
            location.setLocid(negativeId);

            Location recordSaved = dao.saveOrUpdate(location);
            idLocationSaved = recordSaved.getLocid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Location: GermplasmDataManager.addLocation(location=" + location + "): "
                    + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idLocationSaved;
    }

    @Override
    @Deprecated
    public List<Integer> addLocation(List<Location> locations) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        List<Integer> idLocationsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LocationDAO dao = getLocationDao();

            for (Location location : locations) {

                // Auto-assign negative IDs for new local DB records
                Integer negativeId = dao.getNegativeId("locid");
                location.setLocid(negativeId);

                Location recordSaved = dao.saveOrUpdate(location);
                idLocationsSaved.add(recordSaved.getLocid());
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Locations: GermplasmDataManager.addLocation(locations=" + locations
                    + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idLocationsSaved;
    }

    @Override
    @Deprecated
    public void deleteLocation(Location location) throws MiddlewareQueryException {
        Session session = getCurrentSessionForLocal();
        requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getLocationDao().makeTransient(location);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting Location: GermplasmDataManager.deleteLocation(location=" + location
                    + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
    }

    @Override
    public Bibref getBibliographicReferenceByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getBibrefDao().getById(id, false);
        }
        return null;
    }

    @Override
    public Integer addBibliographicReference(Bibref bibref) throws MiddlewareQueryException {
        Session session = getCurrentSessionForLocal();
        requireLocalDatabaseInstance();
        Transaction trans = null;

        Integer idBibrefSaved = null;
        try {
            trans = session.beginTransaction();
            BibrefDAO dao = getBibrefDao();

            // Auto-assign negative IDs for new local DB records
            Integer negativeId = dao.getNegativeId("refid");
            bibref.setRefid(negativeId);

            Bibref recordSaved = dao.saveOrUpdate(bibref);
            idBibrefSaved = recordSaved.getRefid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Bibliographic Reference: GermplasmDataManager.addBibliographicReference(bibref="
                            + bibref + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idBibrefSaved;
    }

    @Override
    public Integer addGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(attribute);
        List<Integer> ids = addGermplasmAttribute(attributes);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException {
        return addOrUpdateAttributes(attributes, Operation.ADD);
    }

    @Override
    public Integer updateGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(attribute);
        List<Integer> ids = updateGermplasmAttribute(attributes);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException {
        return addOrUpdateAttributes(attributes, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateAttributes(List<Attribute> attributes, Operation operation) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        List<Integer> idAttributesSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            AttributeDAO dao = getAttributeDao();

            for (Attribute attribute : attributes) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("aid");
                    attribute.setAid(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if Attribute is a local DB record. Throws exception
                    // if Attribute is a central DB record.
                    dao.validateId(attribute);
                }
                Attribute recordSaved = dao.saveOrUpdate(attribute);
                idAttributesSaved.add(recordSaved.getAid());
            }
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Attribute: GermplasmDataManager.addOrUpdateAttributes(attributes="
                    + attributes + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idAttributesSaved;
    }

    @Override
    public Attribute getAttributeById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getAttributeDao().getById(id, false);
        }
        return null;
    }

    @Override
    public Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        // check if the germplasm record identified by gid exists
        Germplasm child = getGermplasmByGID(gid);
        if (child == null) {
            logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
                    + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with gid: " + gid, new Throwable(), LOG);
        }

        // check if the germplasm record identified by progenitorId exists
        Germplasm parent = getGermplasmByGID(progenitorId);
        if (parent == null) {
            logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
                    + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with progenitorId: " + progenitorId, new Throwable(), LOG);
        }

        // check progenitor number
        if (progenitorNumber == 1 || progenitorNumber == 2) {
            // check if given gid refers to a local record
            if (gid < 0) {
                // proceed with update
                if (progenitorNumber == 1) {
                    child.setGpid1(progenitorId);
                } else {
                    child.setGpid2(progenitorId);
                }

                List<Germplasm> germplasms = new ArrayList<Germplasm>();
                germplasms.add(child);
                addOrUpdateGermplasms(germplasms, Operation.UPDATE);
            } else {
                logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
                        + ", progenitorNumber=" + progenitorNumber
                        + "): The gid supplied as parameter does not refer to a local record. Only local records may be updated.", new Throwable(), LOG);
            }
        } else if (progenitorNumber > 2) {
            ProgenitorDAO dao = getProgenitorDao();

            // check if there is an existing Progenitor record
            ProgenitorPK id = new ProgenitorPK(gid, progenitorNumber);
            Progenitor p = dao.getById(id, false);

            if (p != null) {
                // update the existing record
                p.setPid(progenitorId);

                List<Progenitor> progenitors = new ArrayList<Progenitor>();
                progenitors.add(p);
                int updated = addOrUpdateProgenitors(progenitors);
                if (updated == 1) {
                    return progenitorId;
                }
            } else {
                // create new Progenitor record
                Progenitor newRecord = new Progenitor(id);
                newRecord.setPid(progenitorId);

                List<Progenitor> progenitors = new ArrayList<Progenitor>();
                progenitors.add(newRecord);
                int added = addOrUpdateProgenitors(progenitors);
                if (added == 1) {
                    return progenitorId;
                }
            }
        } else {
            logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
                    + ", progenitorNumber=" + progenitorNumber + "): Invalid progenitor number: " + progenitorNumber, new Throwable(), LOG);
        }

        return progenitorId;
    }

    private List<Integer> addOrUpdateGermplasms(List<Germplasm> germplasms, Operation operation) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        int germplasmsSaved = 0;
        List<Integer> idGermplasmsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            GermplasmDAO dao = getGermplasmDao();

            for (Germplasm germplasm : germplasms) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("gid");
                    germplasm.setGid(negativeId);
                    germplasm.setLgid(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if Germplasm is a local DB record. Throws exception if Germplasm is a central DB record.
                    dao.validateId(germplasm);
                }
                Germplasm recordSaved = dao.saveOrUpdate(germplasm);
                idGermplasmsSaved.add(recordSaved.getGid());
                germplasmsSaved++;
                if (germplasmsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Germplasm: GermplasmDataManager.addOrUpdateGermplasms(germplasms="
                    + germplasms + ", operation=" + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idGermplasmsSaved;
    }

    private int addOrUpdateProgenitors(List<Progenitor> progenitors) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        int progenitorsSaved = 0;
        try {
            trans = session.beginTransaction();
            ProgenitorDAO dao = getProgenitorDao();

            for (Progenitor progenitor : progenitors) {
                dao.saveOrUpdate(progenitor);
                progenitorsSaved++;
            }
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Progenitor: GermplasmDataManager.addOrUpdateProgenitors(progenitors="
                    + progenitors + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return progenitorsSaved;
    }

    @Override
    public Integer updateGermplasm(Germplasm germplasm) throws MiddlewareQueryException {
        List<Germplasm> germplasms = new ArrayList<Germplasm>();
        germplasms.add(germplasm);
        List<Integer> ids = updateGermplasm(germplasms);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasm(List<Germplasm> germplasms) throws MiddlewareQueryException {
    	if(germplasms!=null) {
    		List<Integer> gids = new ArrayList<Integer>(); 
    		for (Germplasm germplasm : germplasms) {
    			if(germplasm.getGid().equals(germplasm.getGrplce())) {//deleted
    				gids.add(germplasm.getGid());
    			}
			}
    		if(gids!=null && !gids.isEmpty()) {
    			setWorkingDatabase(Database.LOCAL);
    			getTransactionDao().cancelUnconfirmedTransactionsForGermplasms(gids);
    		}
    	}
    	return addOrUpdateGermplasms(germplasms, Operation.UPDATE);
    }

    @Override
    public Integer addGermplasm(Germplasm germplasm, Name preferredName) throws MiddlewareQueryException {
        Map<Germplasm, Name> germplasmNameMap = new HashMap<Germplasm, Name>();
        germplasm.setGid(Integer.valueOf(1));
        germplasmNameMap.put(germplasm, preferredName);
        List<Integer> ids = addGermplasm(germplasmNameMap);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        int germplasmsSaved = 0;
        List<Integer> isGermplasmsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            GermplasmDAO dao = getGermplasmDao();
            NameDAO nameDao = getNameDao();

            for (Germplasm germplasm : germplasmNameMap.keySet()) {
                Name name = germplasmNameMap.get(germplasm);

                // Auto-assign negative IDs for new local DB records
                Integer negativeId = dao.getNegativeId("gid");
                germplasm.setGid(negativeId);
                germplasm.setLgid(Integer.valueOf(0));

                Integer nameId = nameDao.getNegativeId("nid");
                name.setNid(nameId);
                name.setNstat(Integer.valueOf(1));
                name.setGermplasmId(negativeId);

                Germplasm germplasmSaved = dao.save(germplasm);
                isGermplasmsSaved.add(germplasmSaved.getGid());
                nameDao.save(name);
                germplasmsSaved++;

                if (germplasmsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Germplasm: GermplasmDataManager.addGermplasm(germplasmNameMap="
                    + germplasmNameMap + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return isGermplasmsSaved;
    }
    
    public Integer addUserDefinedField(UserDefinedField field) throws MiddlewareQueryException{
    	requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer isUdfldSaved = 0;
        try {
        	trans = session.beginTransaction();
            UserDefinedFieldDAO dao =  getUserDefinedFieldDao();
            
            // Auto-assign negative IDs for new local DB records
            Integer negativeId = dao.getNegativeId("fldno");
            field.setFldno(negativeId);
            UserDefinedField udflds = dao.save(field);
            isUdfldSaved++;

            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedField(fields="
                    + isUdfldSaved + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        
        return isUdfldSaved;
    }
    
    public List<Integer> addUserDefinedFields(List<UserDefinedField> fields) throws MiddlewareQueryException{
    	requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        List<Integer> isUdfldSaved = new ArrayList<Integer>();
        try {
        	trans = session.beginTransaction();
            UserDefinedFieldDAO dao =  getUserDefinedFieldDao();
            
            int udfldSaved = 0;
            for (UserDefinedField field : fields) {

                // Auto-assign negative IDs for new local DB records
                Integer negativeId = dao.getNegativeId("fldno");
                field.setFldno(negativeId);
                
                UserDefinedField udflds = dao.save(field);
                isUdfldSaved.add(udflds.getFldno());
                udfldSaved++;

                if (udfldSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedFields(fields="
                    + fields + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        
        return isUdfldSaved;
    }
    
    public Integer addAttribute(Attribute attr) throws MiddlewareQueryException{
    	requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer isAttrSaved = 0;
        try {
        	trans = session.beginTransaction();
            AttributeDAO dao =  getAttributeDao();
            
            // Auto-assign negative IDs for new local DB records
            Integer negativeId = dao.getNegativeId("aid");
            attr.setAid(negativeId);
            Attribute newAttr = dao.save(attr);
            isAttrSaved++;

            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Attribute: GermplasmDataManager.addAttribute(addAttribute="
                    + attr + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        
        return isAttrSaved;
    }
    
    public List<Integer> addAttributes(List<Attribute> attrs) throws MiddlewareQueryException{
    	requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        List<Integer> isAttrSaved = new ArrayList<Integer>();
        try {
        	trans = session.beginTransaction();
        	AttributeDAO dao =  getAttributeDao();
            
            int attrSaved = 0;
            for (Attribute attr : attrs) {

                // Auto-assign negative IDs for new local DB records
                Integer negativeId = dao.getNegativeId("aid");
                attr.setAid(negativeId);
                
                Attribute newAttr = dao.save(attr);
                isAttrSaved.add(newAttr.getAid());
                attrSaved++;

                if (attrSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addAttributes(attrs="
                    + isAttrSaved + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        
        return isAttrSaved;
    }

    @Override
    public List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(List<String> germplasmNames, GetGermplasmByNameModes mode) throws MiddlewareQueryException {
    	List<String> namesToUse = GermplasmDataManagerUtil.getNamesToUseByMode(germplasmNames, mode);
        return (List<GermplasmNameDetails>) super.getAllFromCentralAndLocalByMethod(getNameDao(), "getGermplasmNameDetailsByNames",
                new Object[] { namesToUse, mode }, new Class[]{List.class, GetGermplasmByNameModes.class});
    }
    
    @Override
    @Deprecated
    public List<Country> getAllCountry() throws MiddlewareQueryException {
        return (List<Country>) super.getAllFromCentralAndLocalByMethod(getCountryDao(), "getAllCountry", new Object[] {}, new Class[] {});
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getByCountryAndType", new Object[] { country,
                type}, new Class[]{Country.class, Integer.class});
    }
    
    @Override
    @Deprecated
    public List<Location> getLocationsByNameCountryAndType(String name,Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getByNameCountryAndType", new Object[] { name,country,
                type}, new Class[]{String.class,Country.class, Integer.class});
    }
    
    @Override
    @Deprecated
    public List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<LocationDetails>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getLocationDetails", new Object[] { locationId,
            start,numOfRows}, new Class[]{Integer.class,Integer.class,Integer.class});
        
    }

    @Override
    public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType)
            throws MiddlewareQueryException {
        return (List<UserDefinedField>) super.getAllFromCentralAndLocalByMethod(getUserDefinedFieldDao(), "getByFieldTableNameAndType",
                new Object[] { tableName, fieldType }, new Class[] { String.class, String.class });
    }

    @Override
    public List<Method> getMethodsByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
        return (List<Method>) super.getAllFromCentralAndLocalByMethod(getMethodDao(), "getByGroupIncludesGgroup", new Object[] { group }, new Class[]{String.class});
    }

    @Override
    public String getCrossExpansion(Integer gid, int level) throws MiddlewareQueryException {
        Germplasm germplasm = getGermplasmWithPrefName(gid);
        if (germplasm != null) {
            SingleGermplasmCrossElement startElement = new SingleGermplasmCrossElement();
            startElement.setGermplasm(germplasm);
            GermplasmCrossElement cross = expandGermplasmCross(startElement, level, false);
            return cross.toString();
        } else {
            return null;
        }
    }

    private GermplasmCrossElement expandGermplasmCross(GermplasmCrossElement element, int level, boolean forComplexCross) throws MiddlewareQueryException {
        if (level == 0) {
            //if the level is zero then there is no need to expand and the element
            //should be returned as is
            return element;
        } else {
            if (element instanceof SingleGermplasmCrossElement) {
                SingleGermplasmCrossElement singleGermplasm = (SingleGermplasmCrossElement) element;
                Germplasm germplasmToExpand = singleGermplasm.getGermplasm();
                
                if(germplasmToExpand == null){
                    return singleGermplasm;
                }

                if (germplasmToExpand.getGnpgs() < 0) {
                    //for germplasms created via a derivative or maintenance method
                    //skip and then expand on the gpid1 parent
                    if (germplasmToExpand.getGpid1() != null && germplasmToExpand.getGpid1() != 0 && !forComplexCross) {
                        SingleGermplasmCrossElement nextElement = new SingleGermplasmCrossElement();
                        Germplasm gpid1Germplasm = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                        if(gpid1Germplasm != null){
                            nextElement.setGermplasm(gpid1Germplasm);
                            return expandGermplasmCross(nextElement, level, forComplexCross);
                        } else{
                            return element;
                        }
                    } else {
                        return element;
                    }
                } else {
                    GermplasmCross cross = new GermplasmCross();

                    Method method = getMethodByID(germplasmToExpand.getMethodId());
                    if (method != null) {
                        String methodName = method.getMname();
                        if (methodName != null) {
                            methodName = methodName.toLowerCase();
                        } else {
                            methodName = "";
                        }

                        if (methodName.contains("single cross")) {
                            //get the immediate parents
                            Germplasm firstParent = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            
                            Germplasm secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);

                            //expand the parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstParent = expandGermplasmCross(firstParentElem, level - 1, forComplexCross);
                            GermplasmCrossElement expandedSecondParent = expandGermplasmCross(secondParentElem, level - 1, forComplexCross);

                            //get the number of crosses in the first parent
                            int numOfCrosses = 0;
                            if (expandedFirstParent instanceof GermplasmCross) {
                                numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
                            }

                            cross.setFirstParent(expandedFirstParent);
                            cross.setSecondParent(expandedSecondParent);
                            cross.setNumberOfCrossesBefore(numOfCrosses);

                        } else if (methodName.contains("double cross")) {
                            //get the grandparents on both sides
                            Germplasm firstParent = getGermplasmByGID(germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmByGID(germplasmToExpand.getGpid2());
                            
                            Germplasm firstGrandParent = null;
                            SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                            Germplasm secondGrandParent = null;
                            SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                            if(firstParent != null){
                                firstGrandParent = getGermplasmWithPrefName(firstParent.getGpid1());
                                firstGrandParentElem.setGermplasm(firstGrandParent);
                                
                                secondGrandParent = getGermplasmWithPrefName(firstParent.getGpid2());
                                secondGrandParentElem.setGermplasm(secondGrandParent);
                            }

                            Germplasm thirdGrandParent = null;
                            SingleGermplasmCrossElement thirdGrandParentElem = new SingleGermplasmCrossElement();
                            Germplasm fourthGrandParent = null;
                            SingleGermplasmCrossElement fourthGrandParentElem = new SingleGermplasmCrossElement();
                            if(secondParent != null){
                                thirdGrandParent = getGermplasmWithPrefName(secondParent.getGpid1());
                                thirdGrandParentElem.setGermplasm(thirdGrandParent);
                                fourthGrandParent = getGermplasmWithPrefName(secondParent.getGpid2());
                                fourthGrandParentElem.setGermplasm(fourthGrandParent);
                            }

                            //expand the grand parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstGrandParent = null;
                            GermplasmCrossElement expandedSecondGrandParent = null;
                            GermplasmCrossElement expandedThirdGrandParent = null;
                            GermplasmCrossElement expandedFourthGrandParent =  null;
                            
                            if(firstParent != null){
                                expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);
                            }
                            
                            if(secondParent != null){
                                expandedThirdGrandParent = expandGermplasmCross(thirdGrandParentElem, level - 1, forComplexCross);
                                expandedFourthGrandParent = expandGermplasmCross(fourthGrandParentElem, level - 1, forComplexCross);
                            }

                            //create the cross object for the first pair of grand parents
                            GermplasmCross firstCross = new GermplasmCross();
                            int numOfCrossesForFirst = 0;
                            if(firstParent != null){
                                firstCross.setFirstParent(expandedFirstGrandParent);
                                firstCross.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this cross
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForFirst = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                firstCross.setNumberOfCrossesBefore(numOfCrossesForFirst);
                            }

                            //create the cross object for the second pair of grand parents
                            GermplasmCross secondCross = new GermplasmCross();
                            if(secondParent != null){
                                secondCross.setFirstParent(expandedThirdGrandParent);
                                secondCross.setSecondParent(expandedFourthGrandParent);
                                //compute the number of crosses before this cross
                                int numOfCrossesForSecond = 0;
                                if (expandedThirdGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForSecond = ((GermplasmCross) expandedThirdGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                secondCross.setNumberOfCrossesBefore(numOfCrossesForSecond);
                            } 

                            //create the cross of the two sets of grandparents, this will be returned
                            if(firstParent != null){
                                cross.setFirstParent(firstCross);
                            } else{
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                cross.setFirstParent(firstParentElem);
                            }
                            
                            if(secondParent != null){
                                cross.setSecondParent(secondCross);
                            } else{
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                cross.setSecondParent(secondParentElem);
                            }
                            
                            //compute the number of crosses before the cross to be returned
                            int numOfCrosses = 0;
                            if(firstParent != null){
                                numOfCrosses = numOfCrossesForFirst + 1;
                                if (expandedSecondGrandParent instanceof GermplasmCross) {
                                    numOfCrosses = numOfCrosses + ((GermplasmCross) expandedSecondGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                            }
                            cross.setNumberOfCrossesBefore(numOfCrosses);

                        } else if (methodName.contains("three-way cross")) {
                            //get the two parents first
                            Germplasm firstParent = getGermplasmByGID(germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmByGID(germplasmToExpand.getGpid2());
                            
                            //check for the parent generated by a cross, the other one should be a derived germplasm
                            if (firstParent != null && firstParent.getGnpgs() > 0) {
                                // the first parent is the one created by a cross
                                Germplasm firstGrandParent = getGermplasmWithPrefName(firstParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent = getGermplasmWithPrefName(firstParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

                                //make the cross object for the grand parents
                                GermplasmCross crossForGrandParents = new GermplasmCross();
                                crossForGrandParents.setFirstParent(expandedFirstGrandParent);
                                crossForGrandParents.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this one
                                int numOfCrossesForGrandParents = 0;
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForGrandParents = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

                                //make the element for the second parent
                                secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);

                                // create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(secondParentElem);
                                //compute the number of crosses before this cross
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            } else if(secondParent != null){
                                // the second parent is the one created by a cross
                                Germplasm firstGrandParent = getGermplasmWithPrefName(secondParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent = getGermplasmWithPrefName(secondParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

                                //make the cross object for the grand parents
                                GermplasmCross crossForGrandParents = new GermplasmCross();
                                crossForGrandParents.setFirstParent(expandedFirstGrandParent);
                                crossForGrandParents.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this one
                                int numOfCrossesForGrandParents = 0;
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForGrandParents = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

                                //make the element for the first parent
                                firstParent = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);

                                //create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(firstParentElem);
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            } else{
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                
                                cross.setFirstParent(firstParentElem);
                                cross.setSecondParent(secondParentElem);
                                cross.setNumberOfCrossesBefore(0);
                            }

                        } else if(methodName.contains("backcross")){
                            BackcrossElement backcross = new BackcrossElement();
                            
                            Germplasm firstParent = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            
                            boolean itsABackCross = false;
                            
                            //determine which is the recurrent parent
                            if(firstParent != null && secondParent != null){
                                SingleGermplasmCrossElement recurringParentElem = new SingleGermplasmCrossElement();
                                SingleGermplasmCrossElement parentElem = new SingleGermplasmCrossElement();
                                if(secondParent.getGnpgs() >= 2){
                                    if(firstParent.getGid().equals(secondParent.getGpid1())
                                            || firstParent.getGid().equals(secondParent.getGpid2())){
                                        itsABackCross = true;
                                        backcross.setRecurringParentOnTheRight(false);
                                        
                                        recurringParentElem.setGermplasm(firstParent);
                                        
                                        Germplasm toCheck = null;
                                        if(firstParent.getGid().equals(secondParent.getGpid1()) && secondParent.getGpid2() != null){
                                            toCheck = getGermplasmWithPrefName(secondParent.getGpid2());
                                        } else if(firstParent.getGid().equals(secondParent.getGpid2()) && secondParent.getGpid1() != null){
                                            toCheck = getGermplasmWithPrefName(secondParent.getGpid1());
                                        }
                                        Object numOfDosesAndOtherParent[] = determineNumberOfRecurringParent(firstParent.getGid(), toCheck);
                                        parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
                                        
                                        backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
                                    }
                                } else if(firstParent.getGnpgs() >= 2){
                                    if(secondParent.getGid().equals(firstParent.getGpid1())
                                            || secondParent.getGid().equals(firstParent.getGpid2())){
                                        itsABackCross = true;
                                        backcross.setRecurringParentOnTheRight(true);
                                        
                                        recurringParentElem.setGermplasm(secondParent);
                                        
                                        Germplasm toCheck = null;
                                        if(secondParent.getGid().equals(firstParent.getGpid1()) && firstParent.getGpid2() != null){
                                            toCheck = getGermplasmWithPrefName(firstParent.getGpid2());
                                        } else if(secondParent.getGid().equals(firstParent.getGpid2()) && firstParent.getGpid1() != null){
                                            toCheck = getGermplasmWithPrefName(firstParent.getGpid1());
                                        }
                                        Object numOfDosesAndOtherParent[] = determineNumberOfRecurringParent(secondParent.getGid(), toCheck);
                                        parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
                                        
                                        backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
                                    }
                                } else{
                                    itsABackCross = false;
                                }
                                
                                if(itsABackCross){
                                    GermplasmCrossElement expandedRecurringParent = expandGermplasmCross(recurringParentElem, level - 1, forComplexCross);
                                    backcross.setRecurringParent(expandedRecurringParent);
                                    
                                    GermplasmCrossElement expandedParent = expandGermplasmCross(parentElem, level -1, forComplexCross);
                                    backcross.setParent(expandedParent);
                                    
                                    return backcross;
                                }
                            }
                            
                            if(!itsABackCross){
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                
                                cross.setFirstParent(firstParentElem);
                                cross.setSecondParent(secondParentElem);
                                cross.setNumberOfCrossesBefore(0);
                            }
                        } else if(methodName.contains("cross") && methodName.contains("complex")){
                            //get the immediate parents
                            Germplasm firstParent = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            
                            Germplasm secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);

                            //expand the parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstParent = expandGermplasmCross(firstParentElem, level, true);
                            GermplasmCrossElement expandedSecondParent = expandGermplasmCross(secondParentElem, level, true);

                            //get the number of crosses in the first parent
                            int numOfCrosses = 0;
                            if (expandedFirstParent instanceof GermplasmCross) {
                                numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
                            }

                            cross.setFirstParent(expandedFirstParent);
                            cross.setSecondParent(expandedSecondParent);
                            cross.setNumberOfCrossesBefore(numOfCrosses);
                        } else if (methodName.contains("cross")){
                            Germplasm firstParent = getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);
                            
                            cross.setFirstParent(firstParentElem);
                            cross.setSecondParent(secondParentElem);
                            cross.setNumberOfCrossesBefore(0);
                        } else{
                            SingleGermplasmCrossElement crossElement = new SingleGermplasmCrossElement();
                            crossElement.setGermplasm(germplasmToExpand);
                            return crossElement;
                        }

                        return cross;
                    } else {
                        logAndThrowException("Error with expanding cross, can not find method with id: " + germplasmToExpand.getMethodId(), 
                                new Throwable(), LOG);
                    }
                }
            } else {
                logAndThrowException("expandGermplasmCross was incorrectly called", new Throwable(), LOG);
            }
        }
        return element;
    }
    
    /**
     * 
     * @param recurringParentGid
     * @param toCheck
     * @return an array of 2 Objects, first is an Integer which is the number of doses of the recurring parent, and the other is a Germplasm object
     * representing the parent crossed with the recurring parent.
     */
    private Object[] determineNumberOfRecurringParent(Integer recurringParentGid, Germplasm toCheck) throws MiddlewareQueryException{
        Object toreturn[] = new Object[2];
        if(toCheck == null){
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = null;
        } else if(toCheck.getGpid1() != null && !toCheck.getGpid1().equals(recurringParentGid)
                && toCheck.getGpid2() != null && !toCheck.getGpid2().equals(recurringParentGid)){
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = toCheck;
        } else if(toCheck.getGpid1() != null && toCheck.getGpid1().equals(recurringParentGid)){
            Germplasm nextToCheck = null;
            if(toCheck.getGpid2() != null){
                nextToCheck = getGermplasmWithPrefName(toCheck.getGpid2());
            }
            Object returned[] = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        } else if(toCheck.getGpid2() != null && toCheck.getGpid2().equals(recurringParentGid)){
            Germplasm nextToCheck = null;
            if(toCheck.getGpid1() != null){
                nextToCheck = getGermplasmWithPrefName(toCheck.getGpid1());
            }
            Object returned[] = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        }
        else{
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = toCheck;
        }
        
        return toreturn;
    }
    
    @Override
    @Deprecated
    public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
        Database centralInstance = Database.CENTRAL;
        Database localInstance = Database.LOCAL;
        
        List<Location> allLocations = getFromInstanceByMethod(getLocationDAO(), centralInstance, "getAllBreedingLocations", new Object[] {}, new Class[] {});
        allLocations.addAll(getFromInstanceByMethod(getLocationDAO(), localInstance, "getAllBreedingLocations", new Object[] {}, new Class[] {}));
        
        return allLocations;
    }
    
    
    @Override
    @Deprecated
    public Long countAllBreedingLocations() throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDAO(), "countAllBreedingLocations", new Object[] {}, new Class[] {});
    }

    @Override
    public String getNextSequenceNumberForCrossName(String prefix, Database instance)
            throws MiddlewareQueryException {
        String nextSequenceStr = "1";
        
        if (setWorkingDatabase(instance, getGermplasmDao())){
            nextSequenceStr =  getGermplasmDao().getNextSequenceNumberForCrossName(prefix);
            
        }
        
        return nextSequenceStr;
    }    
    
    @Override
    public String getNextSequenceNumberForCrossName(String prefix)
            throws MiddlewareQueryException {
        String localNextSequenceStr = "1";
        String centralNextSequenceStr = "1";
        
        centralNextSequenceStr = getNextSequenceNumberForCrossName(prefix, Database.CENTRAL);
        localNextSequenceStr = getNextSequenceNumberForCrossName(prefix, Database.LOCAL);
        
        Integer centralNextSequenceInt = Integer.parseInt(centralNextSequenceStr);
        Integer localNextSequenceInt = Integer.parseInt(localNextSequenceStr);
        
        return (centralNextSequenceInt > localNextSequenceInt) ? 
                centralNextSequenceStr : localNextSequenceStr;
    }

    @Override
    public Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();
        
        List<Integer> positiveGIDs = new ArrayList<Integer>();
        List<Integer> negativeGIDs = new ArrayList<Integer>();
        
        for(Integer gid : gids){
            if(gid > 0){
                positiveGIDs.add(gid);
            } else {
                negativeGIDs.add(gid);
            }
        }
        
        if(!positiveGIDs.isEmpty()){
            NameDAO dao = getNameDao();
            requireCentralDatabaseInstance();
            dao.setSession(getCurrentSessionForCentral());
            Map<Integer, String> resultsFromCentral = dao.getPrefferedIdsByGIDs(positiveGIDs);
            
            for(Integer gid : resultsFromCentral.keySet()){
                toreturn.put(gid, resultsFromCentral.get(gid));
            }
        }
        
        if(!negativeGIDs.isEmpty()){
            NameDAO dao = getNameDao();
            requireLocalDatabaseInstance();
            dao.setSession(getCurrentSessionForLocal());
            Map<Integer, String> resultsFromLocal = dao.getPrefferedIdsByGIDs(negativeGIDs);
            
            for(Integer gid : resultsFromLocal.keySet()){
                toreturn.put(gid, resultsFromLocal.get(gid));
            }
        }
        
        return toreturn;
    }       
    
    @Override
    public List<Germplasm> getGermplasmByLocationId(String name, int locationID) throws MiddlewareQueryException {
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        if (setWorkingDatabase(Database.LOCAL)) {
            germplasmList.addAll(getGermplasmDao().getByLocationId(name, locationID));
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            germplasmList.addAll(getGermplasmDao().getByLocationId(name, locationID));
        }
        return germplasmList;
    }
    
    @Override
    public Germplasm getGermplasmWithMethodType(Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return (Germplasm) getGermplasmDao().getByGIDWithMethodType(gid);
        }
        return null;
    }
    
    @Override
    public List<Germplasm> getGermplasmByGidRange(int startGID, int endGID) throws MiddlewareQueryException {
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        
        //assumes the lesser value be the start of the range
        if(endGID < startGID){ 
            int temp = endGID;
            endGID = startGID;
            startGID = temp;
        }
        
        if (setWorkingDatabase(startGID)) {
            germplasmList.addAll(getGermplasmDao().getByGIDRange(startGID, endGID));
            return germplasmList;
        }
        
        return null;
    }
    
    @Override 
    public List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException{
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        
        if (setWorkingDatabase(Database.LOCAL)) {
            germplasmList.addAll(getGermplasmDao().getByGIDList(gids));
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            germplasmList.addAll(getGermplasmDao().getByGIDList(gids));
        }
        
        return germplasmList;
    }
    
    @Override
    public Map<Integer, String> getPreferredNamesByGids (List<Integer> gids) throws MiddlewareQueryException{
         Map<Integer, String> toreturn = new HashMap<Integer, String>();
         
         List<Integer> positiveGIDs = new ArrayList<Integer>();
         List<Integer> negativeGIDs = new ArrayList<Integer>();
         
         for(Integer gid : gids){
             if(gid > 0){
                 positiveGIDs.add(gid);
             } else {
                 negativeGIDs.add(gid);
             }
         }
         
         if(!positiveGIDs.isEmpty()){
             NameDAO dao = getNameDao();
             requireCentralDatabaseInstance();
             dao.setSession(getCurrentSessionForCentral());
             Map<Integer, String> resultsFromCentral = dao.getPrefferedNamesByGIDs(positiveGIDs);
             
             for(Integer gid : resultsFromCentral.keySet()){
                 toreturn.put(gid, resultsFromCentral.get(gid));
             }
         }
         
         if(!negativeGIDs.isEmpty()){
             NameDAO dao = getNameDao();
             requireLocalDatabaseInstance();
             dao.setSession(getCurrentSessionForLocal());
             Map<Integer, String> resultsFromLocal = dao.getPrefferedNamesByGIDs(negativeGIDs);
             
             for(Integer gid : resultsFromLocal.keySet()){
                 toreturn.put(gid, resultsFromLocal.get(gid));
             }
         }
         
         return toreturn;
    }
    
    @Override
    public Map<Integer, String> getLocationNamesByGids (List<Integer> gids) throws MiddlewareQueryException{
         Map<Integer, String> toreturn = new HashMap<Integer, String>();
         
         List<Integer> positiveGIDs = new ArrayList<Integer>();
         List<Integer> negativeGIDs = new ArrayList<Integer>();
         
         //separate ids from local and central
         for(Integer gid : gids){
             if(gid > 0){
                 positiveGIDs.add(gid);
             } else {
                 negativeGIDs.add(gid);
             }
         }
         
         //get data from local
         Map<Integer, LocationDto> resultsFromLocal = new HashMap<Integer, LocationDto>();
         if(!negativeGIDs.isEmpty()){
             if (setWorkingDatabase(Database.LOCAL)) {
                 resultsFromLocal = getLocationDao().getLocationNamesByGIDs(negativeGIDs);
             }
         }
         
         //query central location references for local GIDs and then add to map
         List<Integer> centralLocationIds = new ArrayList<Integer>();
         for (LocationDto location : resultsFromLocal.values()){
             Integer locId = location.getId();
             if (locId != null && locId > 0 && !centralLocationIds.contains(locId)){
                 centralLocationIds.add(locId);
             }
         }
         if (setWorkingDatabase(Database.CENTRAL)){
             Map<Integer, String> centralLocations = new HashMap<Integer, String>();
             if (!centralLocationIds.isEmpty()){
                 centralLocations = getLocationDao().getLocationNamesByLocationIDs(centralLocationIds);
             }
             for (Integer gid : resultsFromLocal.keySet()){
                 LocationDto location = resultsFromLocal.get(gid);
                 Integer locationId = location.getId();
                 String locationName = location.getLocationName();
                 if (locationId > 0){
                     locationName = centralLocations.get(locationId);
                 }
                 toreturn.put(gid, locationName);
             }
         }

         
         //get data from central and add it to the map
         if(!positiveGIDs.isEmpty()){
             if (setWorkingDatabase(Database.CENTRAL)) {
                 toreturn.putAll(getLocationDao().getLocationNamesMapByGIDs(positiveGIDs));
             }
         }
         
        
         
         
         return toreturn;
    }

    @Override
    public List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents, boolean searchPublicData)
            throws MiddlewareQueryException{
        List<Germplasm> resultsFromCentral;
        List<Germplasm> resultsFromLocal;
        List<Germplasm> combinedResults = new ArrayList<Germplasm>();

        if(searchPublicData) {
	        if (setWorkingDatabase(Database.CENTRAL)) {
	            resultsFromCentral = getGermplasmDao().searchForGermplasms(q, o, includeParents, true, getCurrentSessionForLocal());
	            combinedResults.addAll(resultsFromCentral);
	        }
	    }
        
        if (setWorkingDatabase(Database.LOCAL)) {
            resultsFromLocal = getGermplasmDao().searchForGermplasms(q, o, includeParents, false, null);
            combinedResults.addAll(resultsFromLocal);
        }

        return combinedResults;
    }
   
    
    public Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, Integer> resultsFromCentral;
        Map<Integer, Integer> resultsFromLocal;
        Map<Integer, Integer> combinedResults = new HashMap<Integer, Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            resultsFromCentral = getGermplasmDao().getGermplasmDatesByGids(gids);
            combinedResults.putAll(resultsFromCentral);
        }
        
        if (setWorkingDatabase(Database.LOCAL)) {
            resultsFromLocal = getGermplasmDao().getGermplasmDatesByGids(gids);
            combinedResults.putAll(resultsFromLocal);
        }

        return combinedResults;
    }
    
    public Map<Integer, Object> getMethodsByGids(List<Integer> gids) throws MiddlewareQueryException {
        
        Map<Integer, Object> results = new HashMap<Integer, Object>();
        
        Map<Integer, Integer> centralMethodIds = new HashMap<Integer, Integer>();
        Map<Integer, Integer> localMethodIds = new HashMap<Integer, Integer>();
        
        Map<Integer, Integer> combinedMethodIds = new HashMap<Integer, Integer>();
        
        if (setWorkingDatabase(Database.CENTRAL)) {
            centralMethodIds = getGermplasmDao().getMethodIdsByGids(gids);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            localMethodIds = getGermplasmDao().getMethodIdsByGids(gids);
        }
        
        combinedMethodIds.putAll(centralMethodIds);
        combinedMethodIds.putAll(localMethodIds);
        
        
        if (setWorkingDatabase(Database.CENTRAL)) {
            for(Map.Entry<Integer,Integer> entry: combinedMethodIds.entrySet()){
                if(entry.getValue()>=0){
                    Method method = getMethodDao().getById(entry.getValue(), false);
                    results.put(entry.getKey(), method);
                }
            }
        }

        if (setWorkingDatabase(Database.LOCAL)) {
            for(Map.Entry<Integer,Integer> entry: combinedMethodIds.entrySet()){
                if(entry.getValue()<0){
                    Method method = getMethodDao().getById(entry.getValue(), false);
                    results.put(entry.getKey(), method);
                }
            }
        }

        return results;
    }

	@Override
	public Integer getNextNegativeId() throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
			 return getGermplasmDao().getNegativeId("gid");
			
	}

	@Override
	public List<Term> getMethodClasses() throws MiddlewareQueryException {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(TermId.BULKING_BREEDING_METHOD_CLASS.getId());
		ids.add(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());
		ids.add(TermId.SEED_INCREASE_METHOD_CLASS.getId());
		ids.add(TermId.SEED_ACQUISITION_METHOD_CLASS.getId());
		ids.add(TermId.CULTIVAR_FORMATION_METHOD_CLASS.getId());
		ids.add(TermId.CROSSING_METHODS_CLASS.getId());
		ids.add(TermId.MUTATION_METHODS_CLASS.getId());
		ids.add(TermId.GENETIC_MODIFICATION_CLASS.getId());
		ids.add(TermId.CYTOGENETIC_MANIPULATION.getId());
		
		return getTermBuilder().getTermsByIds(ids);
		
	}    
	
	@Override
	public Method getMethodByCode(String code) throws MiddlewareQueryException {
	    Method method = new Method();
	    if (setWorkingDatabase(Database.CENTRAL)) {
	        method = getMethodDao().getByCode(code);
	    }
	    if (method == null || method.getMid() == null) {
	        setWorkingDatabase(Database.LOCAL);
	        method = getMethodDao().getByCode(code);
	    }
	    return method;
	}
	
	@Override
	public Method getMethodByName(String name) throws MiddlewareQueryException {
	    List<Method> methods = new ArrayList<Method>();
        if (setWorkingDatabase(Database.CENTRAL)) {
            methods = getMethodDao().getByName(name);
        }
        if (methods == null || methods.size() == 0) {
            setWorkingDatabase(Database.LOCAL);
            methods = getMethodDao().getByName(name);
        }
        if (methods != null && methods.size() > 0) {
            Method method = methods.get(0); 
            return method;
        } else {
            return new Method();
        }
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		return this.getProgramFavoriteDao().getProgramFavorites(type);
	}

	@Override
	public int countProgramFavorites(FavoriteType type)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		return this.getProgramFavoriteDao().countProgramFavorites(type);
	}
	
	@Override
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, int max)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		return this.getProgramFavoriteDao().getProgramFavorites(type, max);
	}

	@Override
	public void saveProgramFavorites(List<ProgramFavorite> list)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
		Transaction trans = null;

		int favoriteSaved = 0;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();

			for (ProgramFavorite favorite : list) {

				Integer negativeId = dao.getNegativeId("id");
				favorite.setProgramFavoriteId(negativeId);
				dao.save(favorite);
				favoriteSaved++;

				if (favoriteSaved % JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorites(list="
					+ list + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}

	}

	@Override
	public void saveProgramFavorite(ProgramFavorite favorite)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();
			Integer negativeId = dao.getNegativeId("id");
			favorite.setProgramFavoriteId(negativeId);
			dao.save(favorite);
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorite(favorite="
					+ favorite + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}
		
	}

	@Override
	public void deleteProgramFavorites(List<ProgramFavorite> list)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
		Transaction trans = null;

		int favoriteDeleted = 0;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();

			for (ProgramFavorite favorite : list) {

				dao.makeTransient(favorite);

				if (favoriteDeleted % JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.deleteProgramFavorites(list="
					+ list + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}
		
	}

	@Override
	public void deleteProgramFavorite(ProgramFavorite favorite)
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();
			dao.makeTransient(favorite);
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while deleting ProgramFavorite: GermplasmDataManager.deleteProgramFavorite(favorite="
					+ favorite + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}
		
	}

	@Override
	public int getMaximumSequence(boolean isBulk, String prefix, String suffix, int count) throws MiddlewareQueryException {
		return getNameBuilder().getMaximumSequence(isBulk, prefix, suffix, count);
	}
}
