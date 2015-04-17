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

import org.generationcp.middleware.dao.*;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.*;
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

import java.util.*;

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
    
    public GermplasmDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public GermplasmDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
        super(sessionProvider, databaseName);
    }
    
    @Override
    @Deprecated
    public List<Location> getAllLocations() throws MiddlewareQueryException{
        List<Location> locations = getLocationDao().getAll();
        Collections.sort(locations);
        return locations;
    }
    
    @Override
    @Deprecated
    public List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException {
        return this.getLocationDao().getAll(start, numOfRows);
    }

    @Override
    @Deprecated
    public long countAllLocations() throws MiddlewareQueryException {
        return countAll(getLocationDao());
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        List<Location> locations = new ArrayList<Location>();
        locations.addAll(getLocationDao().getByName(name, op));
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
        return countAllByMethod(getLocationDao(), "countByName", new Object[] { name, op }, new Class[] { String.class,
                Operation.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao(), "getByCountry", new Object[] { country },
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
        return countAllByMethod(getLocationDao(), "countByCountry", new Object[] { country },
                new Class[] { Country.class });
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
        return (List<Location>) getAllByMethod(getLocationDao(), "getByType", new Object[] { type },
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
        return countAllByMethod(getLocationDao(), "countByType", new Object[] { type }, new Class[] { Integer.class });
    }

    @Override
    public List<Germplasm> getAllGermplasm(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        return (List<Germplasm>) super.getFromInstanceByMethod(getGermplasmDao(), instance, "getAll", 
                new Object[]{start, numOfRows}, new Class[]{Integer.TYPE, Integer.TYPE});
    }

    @Override
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, GetGermplasmByNameModes mode, Operation op,
            Integer status, GermplasmNameType type, Database instance) throws MiddlewareQueryException {
        String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
        return (List<Germplasm>) getGermplasmDao().getByName(nameToUse, op,
                status, type, start, numOfRows);
    }

    @Override
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
		return getGermplasmDao().getByName(names,op,start,numOfRows);
    }

    @Override
    public long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
            Database instance) throws MiddlewareQueryException {
        String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
        return getGermplasmDao().countByName(nameToUse, op, status, type);
    }

    @Override
    public long countGermplasmByName(String name, Operation operation) throws MiddlewareQueryException {
        List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
		return getGermplasmDao().countByName(names,operation);
    }
    
    @Deprecated
    @Override
	public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
    	List<Germplasm> germplasms = new ArrayList<Germplasm>();
    	//get first all the IDs
    	List<Integer> germplasmIds = new ArrayList<Integer>();
    	germplasmIds.addAll(getGermplasmDao().getIdsByName(name, start, numOfRows));    	
		germplasms.addAll(getGermplasmDao().getGermplasmByIds(germplasmIds, start, numOfRows));
    	return germplasms;
    }

    @Override
    public List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Germplasm> germplasms = new ArrayList<Germplasm>();
        GermplasmDAO dao = getGermplasmDao();
        if (op == Operation.EQUAL) {
            germplasms = dao.getByLocationNameUsingEqual(name, start, numOfRows);
        } else if (op == Operation.LIKE) {
            germplasms = dao.getByLocationNameUsingLike(name, start, numOfRows);
        }
        return germplasms;
    }

    @Override
    public long countGermplasmByLocationName(String name, Operation op, Database instance) throws MiddlewareQueryException {
        long count = 0;
        GermplasmDAO dao = getGermplasmDao();
        if (op == Operation.EQUAL) {
            count = dao.countByLocationNameUsingEqual(name);
        } else if (op == Operation.LIKE) {
            count = dao.countByLocationNameUsingLike(name);
        }
        return count;
    }

    @Override
    public List<Germplasm> getGermplasmByMethodName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException {
        List<Germplasm> germplasms = new ArrayList<Germplasm>();
        GermplasmDAO dao = getGermplasmDao();
        if (op == Operation.EQUAL) {
            germplasms = dao.getByMethodNameUsingEqual(name, start, numOfRows);
        } else if (op == Operation.LIKE) {
            germplasms = dao.getByMethodNameUsingLike(name, start, numOfRows);
        }
        return germplasms;
    }

    @Override
    public long countGermplasmByMethodName(String name, Operation op, Database instance) throws MiddlewareQueryException {
        long count = 0;
        GermplasmDAO dao = getGermplasmDao();
        if (op == Operation.EQUAL) {
            count = dao.countByMethodNameUsingEqual(name);
        } else if (op == Operation.LIKE) {
            count = dao.countByMethodNameUsingLike(name);
        }
        return count;
    }

    @Override
    public Germplasm getGermplasmByGID(Integer gid) throws MiddlewareQueryException {
    	Integer updatedGid = gid;
    	Germplasm germplasm = null;
    	do {
    		germplasm = getGermplasmDao().getById(updatedGid, false);
    		if(germplasm!=null) {
    			updatedGid = germplasm.getGrplce();
    		}
    	} while(germplasm!=null && !new Integer(0).equals(updatedGid));
    	return germplasm;
    }

    @Override
    public Germplasm getGermplasmWithPrefName(Integer gid) throws MiddlewareQueryException {
    	Germplasm germplasm = getGermplasmByGID(gid);
    	if(germplasm!=null) {
    		Name preferredName = getPreferredNameByGID(germplasm.getGid());
    		germplasm.setPreferredName(preferredName);
    	}
        return germplasm;
    }

    @Override
    public Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
        return (Germplasm) getGermplasmDao().getByGIDWithPrefAbbrev(gid);
    }

    @Override
    public Name getGermplasmNameByID(Integer id) throws MiddlewareQueryException {
        return (Name) getNameDao().getById(id, false);
    }

    @Override
    public List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException {      
		return getNameDao().getByGIDWithFilters(gid, status, type);
    }

    @Override
    public Name getPreferredNameByGID(Integer gid) throws MiddlewareQueryException {
    	List<Name> names = getNameDao().getByGIDWithFilters(gid, 1, null);
        if (!names.isEmpty()) {
            return names.get(0);
        }
        return null;
    }
    
    @Override
    public String getPreferredNameValueByGID(Integer gid) throws MiddlewareQueryException{
    	List<Name> names = getNameDao().getByGIDWithFilters(gid, 1, null);
        if (!names.isEmpty()) {
            return names.get(0).getNval();
        }
        return null;
    }

    @Override
    public Name getPreferredAbbrevByGID(Integer gid) throws MiddlewareQueryException {
        List<Name> names = getNameDao().getByGIDWithFilters(gid, 2, null);
        if (!names.isEmpty()) {
            return names.get(0);
        }
        return null;
    }
    
    @Override
    public Name getPreferredIdByGID(Integer gid) throws MiddlewareQueryException {
    	List<Name> names = getNameDao().getByGIDWithFilters(gid, 8, null);
        if (!names.isEmpty()) {
            return names.get(0);
        }
        return null;
    }
    
    @Override
    public List<Name> getPreferredIdsByListId(Integer listId) throws MiddlewareQueryException {
        return getNameDao().getPreferredIdsByListId(listId);
    }

    @Override
    public Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode) throws MiddlewareQueryException {
        return getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
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
        Session session = getCurrentSession();
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
                int newNstat = 0; 
                // nstat to be assigned to newPref: 1 for Name, 2 for Abbreviation
                if ("Name".equals(nameOrAbbrev)) {
                    oldPref = getPreferredNameByGID(gid);
                    newNstat = 1;
                } else if ("Abbreviation".equals(nameOrAbbrev)) {
                    oldPref = getPreferredAbbrevByGID(gid);
                    newNstat = 2;
                }

                if (oldPref != null) {
                    oldPref.setNstat(0);
                    dao.saveOrUpdate(oldPref);
                }
                // update specified name as the new preferred name/abbreviation then save the new name's status to the database
                newPref.setNstat(newNstat); 
                dao.saveOrUpdate(newPref); 
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
        return !ids.isEmpty() ? ids.get(0) : null;
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
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmName(List<Name> names) throws MiddlewareQueryException {
        return addOrUpdateGermplasmName(names, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmName(List<Name> names, Operation operation) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        int namesSaved = 0;
        List<Integer> idNamesSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            NameDAO dao = getNameDao();

            for (Name name : names) {
                if (operation == Operation.ADD) {
                    // Auto-assign IDs for new records
                    Integer nextId = dao.getNextId("nid");
                    name.setNid(nextId);
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
    	return getAttributeDao().getByGID(gid);
    }
    
    @Override
    public List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList) throws MiddlewareQueryException {
        return (List<UserDefinedField>) super.getAllByMethod(getAttributeDao(), "getAttributeTypesByGIDList",
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
        List<Attribute> attributeList = super.getAllByMethod(getAttributeDao(), "getAttributeValuesByTypeAndGIDList",
                new Object[] { attributeType, gidList }, new Class[] { Integer.class, List.class });
        for (Attribute attribute : attributeList) {
            returnMap.put(attribute.getGermplasmId(), attribute.getAval());
        }
        
        return returnMap;
    }

    @Override
    public Method getMethodByID(Integer id) throws MiddlewareQueryException {
        return (Method) getMethodDao().getById(id, false);
    }

    @Override
    public List<Method> getMethodsByIDs(List<Integer> ids) throws MiddlewareQueryException {
        List<Method> results = new ArrayList<Method>();

        if (!ids.isEmpty()) {
            results.addAll(getMethodDao().getMethodsByIds(ids));
        }

        return results;
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        return (List<Method>) getAllByMethod(getMethodDao(), "getAllMethod", new Object[] {}, new Class[] {});
    }

    @Override
    public List<Method> getAllMethodsNotGenerative() throws MiddlewareQueryException {
        return (List<Method>) getAllByMethod(getMethodDao(), "getAllMethodsNotGenerative", new Object[] {}, new Class[] {});
    }

    @Override
    public long countAllMethods() throws MiddlewareQueryException {
        return countAll(getMethodDao());
    }
    
    @Override
    public List<Method> getMethodsByUniqueID(String programUUID) throws MiddlewareQueryException {
    	return (List<Method>) super.getAllByMethod(getMethodDao(), "getByUniqueID", new Object[] { programUUID },
                new Class[] { String.class });
    }
    
    @Override
    public long countMethodsByUniqueID(String programUUID) throws MiddlewareQueryException {
        return super
                .countAllByMethod(getMethodDao(), "countByUniqueID", new Object[] { programUUID }, new Class[] { String.class });
    }

    @Override
    public List<Method> getMethodsByType(String type) throws MiddlewareQueryException {
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByType", new Object[] { type },
                new Class[] { String.class });
    }
    
    @Override
    public List<Method> getMethodsByType(String type, String programUUID) throws MiddlewareQueryException {
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByType", new Object[] { type, programUUID },
                new Class[] { String.class, String.class });
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
                .countAllByMethod(getMethodDao(), "countByType", new Object[] { type }, new Class[] { String.class });
    }
    
    @Override
    public long countMethodsByType(String type, String programUUID) throws MiddlewareQueryException {
        return super
                .countAllByMethod(getMethodDao(), "countByType", new Object[] { type, programUUID }, new Class[] { String.class, String.class });
    }


    @Override
    public List<Method> getMethodsByGroup(String group) throws MiddlewareQueryException {
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByGroup", new Object[] { group },
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
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByGroupAndType", new Object[] { group, type },
                new Class[] { String.class, String.class });
    }
    
    @Override
    public List<Method> getMethodsByGroupAndTypeAndName(String group, String type, String name) throws MiddlewareQueryException {
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByGroupAndTypeAndName", new Object[] { group, type, name },
                new Class[] { String.class, String.class, String.class });
    }

    @Override
    public long countMethodsByGroup(String group) throws MiddlewareQueryException {
        return super.countAllByMethod(getMethodDao(), "countByGroup", new Object[] { group },
                new Class[] { String.class });
    }

    @Override
    public Integer addMethod(Method method) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer methodId = null;
        try {
            trans = session.beginTransaction();
            MethodDAO dao = getMethodDao();

            // Auto-assign IDs for new records
            Integer nextId = dao.getNextId("mid");
            method.setMid(nextId);

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
        Session session = getCurrentSession();
        Transaction trans = null;

        Method recordSaved = null;

        try {

            if (method.getMid() == null) {
                throw new MiddlewareQueryException("method has no Id or is not a local method");
            }

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
        Session session = getCurrentSession();
        Transaction trans = null;

        List<Integer> idMethodsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            MethodDAO dao = getMethodDao();

            for (Method method : methods) {
                // Auto-assign IDs for new DB records
                Integer nextId = dao.getNextId("mid");
                method.setMid(nextId);

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
        Session session = getCurrentSession();
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
        return (UserDefinedField) getUserDefinedFieldDao().getById(id, false);
    }

    @Override
    @Deprecated
    public Country getCountryById(Integer id) throws MiddlewareQueryException {
        return getCountryDao().getById(id, false);
    }

    @Override
    @Deprecated
    public Location getLocationByID(Integer id) throws MiddlewareQueryException {
        return getLocationDao().getById(id, false);
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByIDs(List<Integer> ids) throws  MiddlewareQueryException {
        List<Location> results = new ArrayList<Location>();

        if (ids != null && !ids.isEmpty()) {
           results.addAll(getLocationDao().getLocationByIds(ids));
        }
        
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
    public Bibref getBibliographicReferenceByID(Integer id) throws MiddlewareQueryException {
        return getBibrefDao().getById(id, false);
    }

    @Override
    public Integer addBibliographicReference(Bibref bibref) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer idBibrefSaved = null;
        try {
            trans = session.beginTransaction();
            BibrefDAO dao = getBibrefDao();

            // Auto-assign IDs for new DB records
            Integer nextId = dao.getNextId("refid");
            bibref.setRefid(nextId);

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
        return !ids.isEmpty() ? ids.get(0) : null;
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
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException {
        return addOrUpdateAttributes(attributes, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateAttributes(List<Attribute> attributes, Operation operation) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        List<Integer> idAttributesSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            AttributeDAO dao = getAttributeDao();

            for (Attribute attribute : attributes) {
                if (operation == Operation.ADD) {
                    // Auto-assign IDs for new DB records
                    Integer nextId = dao.getNextId("aid");
                    attribute.setAid(nextId);
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
        return getAttributeDao().getById(id, false);
    }

    @Override
    public Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws MiddlewareQueryException {

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
            if (progenitorNumber == 1) {
                child.setGpid1(progenitorId);
            } else {
                child.setGpid2(progenitorId);
            }

            List<Germplasm> germplasms = new ArrayList<Germplasm>();
            germplasms.add(child);
            addOrUpdateGermplasms(germplasms, Operation.UPDATE);
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
        Session session = getCurrentSession();
        Transaction trans = null;

        int germplasmsSaved = 0;
        List<Integer> idGermplasmsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            GermplasmDAO dao = getGermplasmDao();

            for (Germplasm germplasm : germplasms) {
                if (operation == Operation.ADD) {
                    // Auto-assign IDs for new DB records
                    Integer nextId = dao.getNextId("gid");
                    germplasm.setGid(nextId);
                    germplasm.setLgid(nextId);
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
        Session session = getCurrentSession();
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
        return !ids.isEmpty() ? ids.get(0) : null;
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
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        int germplasmsSaved = 0;
        List<Integer> isGermplasmsSaved = new ArrayList<Integer>();
        try {
            trans = session.beginTransaction();
            GermplasmDAO dao = getGermplasmDao();
            NameDAO nameDao = getNameDao();

            for (Germplasm germplasm : germplasmNameMap.keySet()) {
                Name name = germplasmNameMap.get(germplasm);

                // Auto-assign IDs for new DB records
                Integer nextId = dao.getNextId("gid");
                germplasm.setGid(nextId);
                
                if(germplasm.getLgid() > 0){
                	germplasm.setLgid(Integer.valueOf(0));
                }

                Integer nameId = nameDao.getNextId("nid");
                name.setNid(nameId);
                name.setNstat(Integer.valueOf(1));
                name.setGermplasmId(nextId);

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
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
        	trans = session.beginTransaction();
            UserDefinedFieldDAO dao =  getUserDefinedFieldDao();
            
            // Auto-assign IDs for new DB records
            Integer nextId = dao.getNextId("fldno");
            field.setFldno(nextId);
            dao.save(field);

            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedField(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        
        return field.getFldno();
    }
    
    public List<Integer> addUserDefinedFields(List<UserDefinedField> fields) throws MiddlewareQueryException{
        Session session = getCurrentSession();
        Transaction trans = null;

        List<Integer> isUdfldSaved = new ArrayList<Integer>();
        try {
        	trans = session.beginTransaction();
            UserDefinedFieldDAO dao =  getUserDefinedFieldDao();
            
            int udfldSaved = 0;
            for (UserDefinedField field : fields) {

                // Auto-assign IDs for new DB records
                Integer nextId = dao.getNextId("fldno");
                field.setFldno(nextId);
                
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
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer isAttrSaved = 0;
        try {
        	trans = session.beginTransaction();
            AttributeDAO dao =  getAttributeDao();
            
            // Auto-assign IDs for new DB records
            Integer nextId = dao.getNextId("aid");
            attr.setAid(nextId);
            dao.save(attr);
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
        Session session = getCurrentSession();
        Transaction trans = null;

        List<Integer> isAttrSaved = new ArrayList<Integer>();
        try {
        	trans = session.beginTransaction();
        	AttributeDAO dao =  getAttributeDao();
            
            int attrSaved = 0;
            for (Attribute attr : attrs) {

                // Auto-assign IDs for new DB records
                Integer nextId = dao.getNextId("aid");
                attr.setAid(nextId);
                
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
        return (List<GermplasmNameDetails>) super.getAllByMethod(getNameDao(), "getGermplasmNameDetailsByNames",
                new Object[] { namesToUse, mode }, new Class[]{List.class, GetGermplasmByNameModes.class});
    }
    
    @Override
    @Deprecated
    public List<Country> getAllCountry() throws MiddlewareQueryException {
        return (List<Country>) super.getAllByMethod(getCountryDao(), "getAllCountry", new Object[] {}, new Class[] {});
    }

    @Override
    @Deprecated
    public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao(), "getByCountryAndType", new Object[] { country,
                type}, new Class[]{Country.class, Integer.class});
    }
    
    @Override
    @Deprecated
    public List<Location> getLocationsByNameCountryAndType(String name,Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllByMethod(getLocationDao(), "getByNameCountryAndType", new Object[] { name,country,
                type}, new Class[]{String.class,Country.class, Integer.class});
    }
    
    @Override
    @Deprecated
    public List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<LocationDetails>) super.getAllByMethod(getLocationDao(), "getLocationDetails", new Object[] { locationId,
            start,numOfRows}, new Class[]{Integer.class,Integer.class,Integer.class});
        
    }

    @Override
    public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType)
            throws MiddlewareQueryException {
        return (List<UserDefinedField>) super.getAllByMethod(getUserDefinedFieldDao(), "getByFieldTableNameAndType",
                new Object[] { tableName, fieldType }, new Class[] { String.class, String.class });
    }

    @Override
    public List<Method> getMethodsByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
        return (List<Method>) super.getAllByMethod(getMethodDao(), "getByGroupIncludesGgroup", new Object[] { group }, new Class[]{String.class});
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
                                        Object[] numOfDosesAndOtherParent = determineNumberOfRecurringParent(firstParent.getGid(), toCheck);
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
                                        Object[] numOfDosesAndOtherParent = determineNumberOfRecurringParent(secondParent.getGid(), toCheck);
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
        Object[] toreturn = new Object[2];
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
            Object[] returned = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        } else if(toCheck.getGpid2() != null && toCheck.getGpid2().equals(recurringParentGid)){
            Germplasm nextToCheck = null;
            if(toCheck.getGpid1() != null){
                nextToCheck = getGermplasmWithPrefName(toCheck.getGpid1());
            }
            Object[] returned = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        } else{
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = toCheck;
        }
        
        return toreturn;
    }
    
    @Override
    @Deprecated
    public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
        return getFromInstanceByMethod(getLocationDAO(), Database.LOCAL, "getAllBreedingLocations", new Object[] {}, new Class[] {});
    } 
    
    @Override
    public String getNextSequenceNumberForCrossName(String prefix) throws MiddlewareQueryException {
        String nextSequenceStr = "1";
        nextSequenceStr =  getGermplasmDao().getNextSequenceNumberForCrossName(prefix);
        return nextSequenceStr;
    }

    @Override
    public Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();
        
        if(!gids.isEmpty()){
            Map<Integer, String> results = getNameDao().getPrefferedIdsByGIDs(gids);
            for(Integer gid : results.keySet()){
                toreturn.put(gid, results.get(gid));
            }
        }
        return toreturn;
    }       
    
    @Override
    public List<Germplasm> getGermplasmByLocationId(String name, int locationID) throws MiddlewareQueryException {
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        germplasmList.addAll(getGermplasmDao().getByLocationId(name, locationID));
        return germplasmList;
    }
    
    @Override
    public Germplasm getGermplasmWithMethodType(Integer gid) throws MiddlewareQueryException {
    	return (Germplasm) getGermplasmDao().getByGIDWithMethodType(gid);
    }
    
    @Override
    public List<Germplasm> getGermplasmByGidRange(int startGIDParam, int endGIDParam) throws MiddlewareQueryException {
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        
        int startGID = startGIDParam;
        int endGID = endGIDParam;
        //assumes the lesser value be the start of the range
        if(endGID < startGID){ 
            int temp = endGID;
            endGID = startGID;
            startGID = temp;
        }
        
        germplasmList.addAll(getGermplasmDao().getByGIDRange(startGID, endGID));
        return germplasmList;
    }
    
    @Override 
    public List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException{
        List<Germplasm> germplasmList = new ArrayList<Germplasm>();
        germplasmList.addAll(getGermplasmDao().getByGIDList(gids));
        return germplasmList;
    }
    
    @Override
    public Map<Integer, String> getPreferredNamesByGids (List<Integer> gids) throws MiddlewareQueryException{
         Map<Integer, String> toreturn = new HashMap<Integer, String>();
                 
         if(!gids.isEmpty()){
             Map<Integer, String> results = getNameDao().getPrefferedNamesByGIDs(gids);
             for(Integer gid : results.keySet()){
                 toreturn.put(gid, results.get(gid));
             }
         }
         
         return toreturn;
    }
    
    @Override
    public Map<Integer, String> getLocationNamesByGids (List<Integer> gids) throws MiddlewareQueryException{
    	return getLocationDao().getLocationNamesMapByGIDs(gids);
    }
    
    @Override
    public List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents)
            throws MiddlewareQueryException{
    	return getGermplasmDao().searchForGermplasms(q, o, includeParents, true, null);
    }

    @Deprecated
    @Override
    public List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents, boolean searchPublicData)
            throws MiddlewareQueryException{
    	return searchForGermplasm(q,o,includeParents);
    }
   
    
    public Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids) throws MiddlewareQueryException {
        return getGermplasmDao().getGermplasmDatesByGids(gids);
    }
    
    public Map<Integer, Object> getMethodsByGids(List<Integer> gids) throws MiddlewareQueryException {
        
        Map<Integer, Object> results = new HashMap<Integer, Object>();
        Map<Integer, Integer> methodIds = new HashMap<Integer, Integer>();
        
        methodIds = getGermplasmDao().getMethodIdsByGids(gids);
        for(Map.Entry<Integer,Integer> entry: methodIds.entrySet()) {
        	Method method = getMethodDao().getById(entry.getValue(), false);
        	results.put(entry.getKey(), method);
        }

        return results;
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
	public Method getMethodByCode(String code, String programUUID) throws MiddlewareQueryException {
	    Method method = new Method();
        method = getMethodDao().getByCode(code, programUUID);
	    return method;
	}
	
	@Override
	public Method getMethodByCode(String code) throws MiddlewareQueryException {
	    Method method = new Method();
        method = getMethodDao().getByCode(code);
	    return method;
	}
	
	@Override
	public Method getMethodByName(String name) throws MiddlewareQueryException {
	    List<Method> methods = new ArrayList<Method>();
        methods = getMethodDao().getByName(name);
        if (methods != null && !methods.isEmpty()) {
            return methods.get(0);
        } else {
            return new Method();
        }
	}
	
	@Override
	public Method getMethodByName(String name, String programUUID) throws MiddlewareQueryException {
	    List<Method> methods = new ArrayList<Method>();
        methods = getMethodDao().getByName(name, programUUID);
        if (methods != null && !methods.isEmpty()) {
            return methods.get(0);
        } else {
            return new Method();
        }
	}

	@Override
	public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid)
			throws MiddlewareQueryException {
		return getGermplasmDao().getProgenitorsByGIDWithPrefName(gid);
	}
    
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, String programUUID)
			throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().getProgramFavorites(type,programUUID);
	}

	@Override
	public int countProgramFavorites(FavoriteType type)
			throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().countProgramFavorites(type);
	}
	
	@Override
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, int max, String programUUID)
			throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().getProgramFavorites(type, max, programUUID);
	}

	@Override
	public void saveProgramFavorites(List<ProgramFavorite> list)
			throws MiddlewareQueryException {
		Session session = getCurrentSession();
		Transaction trans = null;

		int favoriteSaved = 0;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();

			for (ProgramFavorite favorite : list) {

				Integer nextId = dao.getNextId("id");
				favorite.setProgramFavoriteId(nextId);
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
		Session session = getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = getProgramFavoriteDao();
			Integer nextId = dao.getNextId("id");
			favorite.setProgramFavoriteId(nextId);
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
		Session session = getCurrentSession();
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
		Session session = getCurrentSession();
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
	
	@Override
	public boolean checkIfMatches(String name) throws MiddlewareQueryException {
		return getNameDao().checkIfMatches(name);
	}

	@Override
	public List<Method> getProgramMethods(String programUUID)
			throws MiddlewareQueryException {
		return getMethodDao().getProgramMethods(programUUID);
	}
	
	@Override
	public void deleteProgramMethodsByUniqueId(String programUUID) throws MiddlewareQueryException {
		Session session = getCurrentSession();
		Transaction trans = null;
		MethodDAO methodDao = getMethodDao();
		int deleted = 0;
		try {
			trans = session.beginTransaction();
			List<Method> list = getProgramMethods(programUUID);
			for (Method method : list) {
				methodDao.makeTransient(method);
				if (deleted % JDBC_BATCH_SIZE == 0) {
					methodDao.flush();
					methodDao.clear();
				}
			}
			trans.commit();
		} catch (Exception e) {
			rollbackTransaction(trans);
			logAndThrowException("Error encountered while deleting methods: GermplasmDataManager.deleteProgramMethodsByUniqueId(uniqueId="
					+ programUUID + "): " + e.getMessage(), e, LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public Germplasm getGermplasmByLocalGid(Integer lgid)
			throws MiddlewareQueryException {
		return this.getGermplasmDao().getByLGid(lgid);
	}
	
}
