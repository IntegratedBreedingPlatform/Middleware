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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.generationcp.middleware.pojos.UserDefinedField;
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

    private AttributeDAO attributeDao;
    private BibrefDAO bibrefDao;
    private GermplasmDAO germplasmDao;
    private LocationDAO locationDao;
    private MethodDAO methodDao;
    private NameDAO nameDao;
    private ProgenitorDAO progenitorDao;
    private UserDefinedFieldDAO userDefinedFieldDao;
	
	
    
    public GermplasmDataManagerImpl() {
    }

    public GermplasmDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GermplasmDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }
    
    private AttributeDAO getAttributeDao() {
        if (attributeDao == null) {
            attributeDao = new AttributeDAO();
        }
        attributeDao.setSession(getActiveSession());
        return attributeDao;
    }

    private BibrefDAO getBibrefDao() {
        if (bibrefDao == null) {
            bibrefDao = new BibrefDAO();
        }
        bibrefDao.setSession(getActiveSession());
        return bibrefDao;
    }

    private GermplasmDAO getGermplasmDao() {
        if (germplasmDao == null) {
            germplasmDao = new GermplasmDAO();
        }
        germplasmDao.setSession(getActiveSession());
        return germplasmDao;
    }

    private LocationDAO getLocationDao() {
        if (locationDao == null) {
            locationDao = new LocationDAO();
        }
        locationDao.setSession(getActiveSession());
        return locationDao;
    }

    private MethodDAO getMethodDao() {
        if (methodDao == null) {
            methodDao = new MethodDAO();
        }
        methodDao.setSession(getActiveSession());
        return methodDao;
    }

    private NameDAO getNameDao() {
        if (nameDao == null) {
            nameDao = new NameDAO();
        }
        nameDao.setSession(getActiveSession());
        return nameDao;
    }

    private ProgenitorDAO getProgenitorDao() {
        if (progenitorDao == null) {
            progenitorDao = new ProgenitorDAO();
        }
        progenitorDao.setSession(getActiveSession());
        return progenitorDao;
    }

    private UserDefinedFieldDAO getUserDefinedFieldDao() {
        if (userDefinedFieldDao == null) {
            userDefinedFieldDao = new UserDefinedFieldDAO();
        }
        userDefinedFieldDao.setSession(getActiveSession());
        return userDefinedFieldDao;
    }

	private LocationDAO getLocationDAO() {
		if (locationDao == null) {
			locationDao = new LocationDAO();
		}
		locationDao.setSession(getActiveSession());
		return locationDao;
	}    
    
    @Override
    public List<Location> getAllLocations(int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Location>) getFromCentralAndLocal(getLocationDao(), start, numOfRows);
    }

    @Override
    public long countAllLocations() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getLocationDao());
    }

    @Override
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
    public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByName", "getByName");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { name, op },
                new Class[] { String.class, Operation.class });
    }



    @Override
    public long countLocationsByName(String name, Operation op) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByName", new Object[] { name, op }, new Class[] { String.class,
                Operation.class });
    }

    @Override
    public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
        return (List<Location>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getByCountry", new Object[] { country },
                new Class[] { Country.class });
    }

    @Override
    public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByCountry", "getByCountry");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { country },
                new Class[] { Country.class });
    }
        
    @Override
    public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
        return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByCountry", new Object[] { country },
                new Class[] { Country.class });
    }

    @Override
    public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
        return (List<Location>) getAllFromCentralAndLocalByMethod(getLocationDao(), "getByType", new Object[] { type },
                new Class[] { Integer.class });
    }

    @Override
    public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByType", "getByType");
        return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[] { type },
                new Class[] { Integer.class });
    }

    @Override
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
        List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
        return super.countAllFromCentralAndLocalByMethod(getGermplasmDao(), "countByName", new Object[] { names, operation }, new Class[] {
                List.class, Operation.class });
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
        return (List<Name>) super.getFromInstanceByIdAndMethod(getNameDao(), gid, "getByGIDWithFilters", 
                new Object[]{gid, status, type}, new Class[]{Integer.class, Integer.class, GermplasmNameType.class});
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
                        + newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "): The specified Germplasm Name does not exist.", LOG);
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
    public Method getMethodByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return (Method) getMethodDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        return (List<Method>) getAllFromCentralAndLocalByMethod(getMethodDao(), "getAllMethod", new Object[] {}, new Class[] {});
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
    public Country getCountryById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getCountryDao().getById(id, false);
        }
        return null;
    }

    @Override
    public Location getLocationByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getLocationDao().getById(id, false);
        }
        return null;
    }

    @Override
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
    public Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber) throws MiddlewareQueryException {
        if (setWorkingDatabase(gid)) {
            return getGermplasmDao().getProgenitorByGID(gid, progenitorNumber);
        }
        return null;
    }

    @Override
    public List<Object[]> getDescendants(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        List<Object[]> result = new ArrayList<Object[]>();
        Object[] germplasmList;

        if (setWorkingDatabase(gid)) {
            List<Germplasm> germplasmDescendant = getGermplasmDao().getGermplasmDescendantByGID(gid, start, numOfRows);
            for (Germplasm g : germplasmDescendant) {
                germplasmList = new Object[2];
                if (g.getGpid1().equals(gid)) {
                    germplasmList[0] = 1;
                } else if (g.getGpid2().equals(gid)) {
                    germplasmList[0] = 2;
                } else {
                    germplasmList[0] = getProgenitorDao().getByGIDAndPID(g.getGid(), gid).getProgntrsPK().getPno().intValue();
                }
                germplasmList[1] = g;

                result.add(germplasmList);
            }
        }
        return result;
    }

    @Override
    public long countDescendants(Integer gid) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getGermplasmDao(), gid, "countGermplasmDescendantByGID", new Object[] { gid },
                new Class[] { Integer.class });
    }

    @Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) throws MiddlewareQueryException {
        return generatePedigreeTree(gid, level, false);
    }
    
    @Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level, Boolean includeDerivativeLines) throws MiddlewareQueryException {
        GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
        // set root node
        Germplasm root = getGermplasmWithPrefName(gid);

        if (root != null) {
            GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
            rootNode.setGermplasm(root);
            if (level > 1) {
            	if(includeDerivativeLines == true)
                    rootNode = addParents(rootNode, level);
            	else
            		rootNode = addParentsExcludeDerivativeLines(rootNode, level);
            }
            tree.setRoot(rootNode);
            return tree;
        }
        return null;
    }
   
    
    /**
     * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents 
     * to the node recursively until the specified level of the tree is reached.
     * 
     * @param node
     * @param level
     * @return the given GermplasmPedigreeTreeNode with its parents added to it
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode addParents(GermplasmPedigreeTreeNode node, int level) throws MiddlewareQueryException {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            if (germplasmOfNode.getGnpgs() == -1) {
                // get and add the source germplasm
                Germplasm parent = getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (parent != null) {
                    GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
                    nodeForParent.setGermplasm(parent);
                    node.getLinkedNodes().add(addParents(nodeForParent, level - 1));
                }
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // get and add female parent
                Germplasm femaleParent = getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                if (femaleParent != null) {
                    GermplasmPedigreeTreeNode nodeForFemaleParent = new GermplasmPedigreeTreeNode();
                    nodeForFemaleParent.setGermplasm(femaleParent);
                    node.getLinkedNodes().add(addParents(nodeForFemaleParent, level - 1));
                }

                // get and add male parent
                Germplasm maleParent = getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (maleParent != null) {
                    GermplasmPedigreeTreeNode nodeForMaleParent = new GermplasmPedigreeTreeNode();
                    nodeForMaleParent.setGermplasm(maleParent);
                    node.getLinkedNodes().add(addParents(nodeForMaleParent, level - 1));
                }

                if (germplasmOfNode.getGnpgs() > 2) {
                    // if there are more parents, get and add each of them
                    List<Germplasm> otherParents = new ArrayList<Germplasm>();

                    if (germplasmOfNode.getGid() < 0 && setWorkingDatabase(Database.LOCAL)) {
                        otherParents = getGermplasmDao().getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    } else if (germplasmOfNode.getGid() > 0 && setWorkingDatabase(Database.CENTRAL)) {
                        otherParents = getGermplasmDao().getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    }

                    for (Germplasm otherParent : otherParents) {
                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
                        nodeForOtherParent.setGermplasm(otherParent);
                        node.getLinkedNodes().add(addParents(nodeForOtherParent, level - 1));
                    }
                }
            }
            return node;
        }
    }
    

    /**
     * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents 
     * to the node recursively excluding derivative lines until the specified level of 
     * the tree is reached.
     * 
     * @param node
     * @param level
     * @return the given GermplasmPedigreeTreeNode with its parents added to it
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode addParentsExcludeDerivativeLines(GermplasmPedigreeTreeNode node, int level) throws MiddlewareQueryException {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            
            if(germplasmOfNode.getGid() <0)
            	setWorkingDatabase(Database.LOCAL);
            else
            	setWorkingDatabase(Database.CENTRAL);
            
            if (germplasmOfNode.getGnpgs() == -1) {
                // get and add the source germplasm
            	
                if(germplasmOfNode.getGpid1() <0)
                	setWorkingDatabase(Database.LOCAL);
                else
                	setWorkingDatabase(Database.CENTRAL);
                
                Germplasm parent = getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                
                if (parent != null) {

                    if(parent.getGpid1() <0)
                    	setWorkingDatabase(Database.LOCAL);
                    else
                    	setWorkingDatabase(Database.CENTRAL);
                	
                	Germplasm grandParent1 = getGermplasmWithPrefName(parent.getGpid1());	
                	if(grandParent1 != null){
                		GermplasmPedigreeTreeNode nodeForGrandParent1 = new GermplasmPedigreeTreeNode();
                		nodeForGrandParent1.setGermplasm(grandParent1);
                		node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForGrandParent1, level - 1));
                	}
                	
                    if(parent.getGpid2() <0)
                    	setWorkingDatabase(Database.LOCAL);
                    else
                    	setWorkingDatabase(Database.CENTRAL);
                	
                	Germplasm grandParent2 = getGermplasmWithPrefName(parent.getGpid2());	
                	if(grandParent2 != null){
                		GermplasmPedigreeTreeNode nodeForGrandParent2 = new GermplasmPedigreeTreeNode();
                		nodeForGrandParent2.setGermplasm(grandParent2);
                		node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForGrandParent2, level - 1));
                	}
                    	
                }
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // get and add female parent
                Germplasm femaleParent = getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                if (femaleParent != null) {
                    GermplasmPedigreeTreeNode nodeForFemaleParent = new GermplasmPedigreeTreeNode();
                    nodeForFemaleParent.setGermplasm(femaleParent);
                    node.getLinkedNodes().add(addParents(nodeForFemaleParent, level - 1));
                }

                // get and add male parent
                Germplasm maleParent = getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (maleParent != null) {
                    GermplasmPedigreeTreeNode nodeForMaleParent = new GermplasmPedigreeTreeNode();
                    nodeForMaleParent.setGermplasm(maleParent);
                    node.getLinkedNodes().add(addParents(nodeForMaleParent, level - 1));
                }

                if (germplasmOfNode.getGnpgs() > 2) {
                    // if there are more parents, get and add each of them
                    List<Germplasm> otherParents = new ArrayList<Germplasm>();

                    if (germplasmOfNode.getGid() < 0 && setWorkingDatabase(Database.LOCAL)) {
                        otherParents = getGermplasmDao().getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    } else if (germplasmOfNode.getGid() > 0 && setWorkingDatabase(Database.CENTRAL)) {
                        otherParents = getGermplasmDao().getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    }

                    for (Germplasm otherParent : otherParents) {
                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
                        nodeForOtherParent.setGermplasm(otherParent);
                        node.getLinkedNodes().add(addParents(nodeForOtherParent, level - 1));
                    }
                }        	
            }
            return node;
        }
    }
    
    

    @Override
    public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countManagementNeighbors", "getManagementNeighbors");
        return (List<Germplasm>) super.getFromCentralAndLocalByMethod(getGermplasmDao(), methods, start, numOfRows, new Object[] { gid },
                new Class[] { Integer.class });
    }

    @Override
    public long countManagementNeighbors(Integer gid) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getGermplasmDao(), "countManagementNeighbors", new Object[] { gid },
                new Class[] { Integer.class });
    }

    @Override
    public long countGroupRelatives(Integer gid) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getGermplasmDao(), gid, "countGroupRelatives", new Object[] { gid },
                new Class[] { Integer.class });
    }

    @Override
    public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) throws MiddlewareQueryException {
        return (List<Germplasm>) super.getFromInstanceByIdAndMethod(getGermplasmDao(), gid, "getGroupRelatives", 
                new Object[]{gid, start, numRows}, new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public List<Germplasm> getGenerationHistory(Integer gid) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();

        Germplasm currentGermplasm = getGermplasmWithPrefName(gid);
        if (currentGermplasm != null) {
            toreturn.add(currentGermplasm);

            while (currentGermplasm.getGnpgs() == -1) {
                // trace back the sources
                Integer sourceId = currentGermplasm.getGpid2();
                currentGermplasm = getGermplasmWithPrefName(sourceId);

                if (currentGermplasm != null) {
                    toreturn.add(currentGermplasm);
                } else {
                    break;
                }
            }
        }
        return toreturn;
    }


    private GermplasmPedigreeTree getNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward, char methodType)
    		throws MiddlewareQueryException {
        GermplasmPedigreeTree neighborhood = new GermplasmPedigreeTree();

        // get the root of the neighborhood
        Object[] traceResult = traceRoot(gid, numberOfStepsBackward, methodType);

        if (traceResult != null) {
            Germplasm root = (Germplasm) traceResult[0];
            Integer stepsLeft = (Integer) traceResult[1];

            GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
            rootNode.setGermplasm(root);

            // get the derived lines from the root until the whole neighborhood is created
            int treeLevel = numberOfStepsBackward - stepsLeft + numberOfStepsForward;
            rootNode = getDerivedLines(rootNode, treeLevel, methodType);

            neighborhood.setRoot(rootNode);

            return neighborhood;
        } else {
            return null;
        }
    }

    @Override
    public GermplasmPedigreeTree getMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
    		throws MiddlewareQueryException {
        
    	return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'M');
    }
    
    @Override
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException {
    	
    	return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'D');
    }

    /**
     * Recursive function which gets the root of a derivative neighborhood by
     * tracing back through the source germplasms. The function stops when the
     * steps are exhausted or a germplasm created by a generative method is
     * encountered, whichever comes first.
     * 
     * @param gid
     * @param steps
     * @return Object[] - first element is the Germplasm POJO, second is an
     *         Integer which is the number of steps left to take
     * @throws MiddlewareQueryException
     */
    private Object[] traceRoot(Integer gid, int steps, char methodType) throws MiddlewareQueryException {
        Germplasm germplasm = getGermplasmWithPrefName(gid);
        
        if (germplasm == null) {
            return null;
        } else if (steps == 0 || germplasm.getGnpgs() != -1) {
            return new Object[] { germplasm, Integer.valueOf(steps) };
        } else {
        	int nextStep = steps;
        	
        	//for MAN neighborhood, move the step count only if the ancestor is a MAN.
        	//otherwise, skip through the ancestor without changing the step count
        	if (methodType == 'M') {
        		Method method = getMethodDao().getById(germplasm.getMethodId(), false);
        		if (method != null && "MAN".equals(method.getMtype())) {
        			nextStep--;
        		}
        	
        	//for DER neighborhood, always move the step count
        	} else {
        		nextStep--;
        	}
        	
            Object[] returned = traceRoot(germplasm.getGpid2(), nextStep, methodType);
            if (returned != null) {
                return returned;
            } else {
                return new Object[] { germplasm, Integer.valueOf(steps) };
            }
        }
    }

    /**
     * Recursive function to get the derived lines given a Germplasm. This
     * constructs the derivative neighborhood.
     * 
     * @param node
     * @param steps
     * @return
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode getDerivedLines(GermplasmPedigreeTreeNode node, int steps, char methodType) throws MiddlewareQueryException {
        if (steps <= 0) {
            return node;
        } else {
            List<Germplasm> derivedGermplasms = new ArrayList<Germplasm>();
            Integer gid = node.getGermplasm().getGid();

            if (gid < 0 && setWorkingDatabase(Database.LOCAL)) {
                derivedGermplasms = getGermplasmDao().getChildren(gid, methodType);
            } else if (gid > 0 && setWorkingDatabase(Database.CENTRAL)) {
                derivedGermplasms = getGermplasmDao().getChildren(gid, methodType);

                if (setWorkingDatabase(Database.LOCAL)) {
                    derivedGermplasms.addAll(getGermplasmDao().getChildren(gid, methodType));
                }
            }

            for (Germplasm g : derivedGermplasms) {
                GermplasmPedigreeTreeNode derivedNode = new GermplasmPedigreeTreeNode();
                derivedNode.setGermplasm(g);
                node.getLinkedNodes().add(getDerivedLines(derivedNode, steps - 1, methodType));
            }

            return node;
        }
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
                    + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with gid: " + gid, LOG);
        }

        // check if the germplasm record identified by progenitorId exists
        Germplasm parent = getGermplasmByGID(progenitorId);
        if (parent == null) {
            logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
                    + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with progenitorId: " + progenitorId, LOG);
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
                        + "): The gid supplied as parameter does not refer to a local record. Only local records may be updated.", LOG);
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
                    + ", progenitorNumber=" + progenitorNumber + "): Invalid progenitor number: " + progenitorNumber, LOG);
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
        List<Integer> idGermplasmsSaved = new ArrayList<Integer>();
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
                idGermplasmsSaved.add(germplasmSaved.getGid());
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
        return idGermplasmsSaved;
    }

    @Override
    public List<GidNidElement> getGidAndNidByGermplasmNames(List<String> germplasmNames) throws MiddlewareQueryException {
        return (List<GidNidElement>) super.getAllFromCentralAndLocalByMethod(getNameDao(), "getGidAndNidByGermplasmNames",
                new Object[] { germplasmNames }, new Class[]{List.class});
    }

    @Override
    public List<Country> getAllCountry() throws MiddlewareQueryException {
        return (List<Country>) super.getAllFromCentralAndLocalByMethod(getCountryDao(), "getAllCountry", new Object[] {}, new Class[] {});
    }

    @Override
    public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        return (List<Location>) super.getAllFromCentralAndLocalByMethod(getLocationDao(), "getByCountryAndType", new Object[] { country,
                type}, new Class[]{Country.class, Integer.class});
    }
    
    @Override
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
            GermplasmCrossElement cross = expandGermplasmCross(startElement, level);
            return cross.toString();
        } else {
            return null;
        }
    }

    private GermplasmCrossElement expandGermplasmCross(GermplasmCrossElement element, int level) throws MiddlewareQueryException {
        if (level == 0) {
            //if the level is zero then there is no need to expand and the element
            //should be returned as is
            return element;
        } else {
            if (element instanceof SingleGermplasmCrossElement) {
                SingleGermplasmCrossElement singleGermplasm = (SingleGermplasmCrossElement) element;
                Germplasm germplasmToExpand = singleGermplasm.getGermplasm();

                if (germplasmToExpand.getGnpgs() < 0) {
                    //for germplasms created via a derivative or maintenance method
                    //skip and then expand on the gpid1 parent
                    if (germplasmToExpand.getGpid1() != 0 && germplasmToExpand.getGpid1() != null) {
                        SingleGermplasmCrossElement nextElement = new SingleGermplasmCrossElement();
                        nextElement.setGermplasm(getGermplasmWithPrefName(germplasmToExpand.getGpid1()));
                        return expandGermplasmCross(nextElement, level);
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
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstParent, germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondParent, germplasmToExpand.getGpid2());
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);

                            //expand the parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstParent = expandGermplasmCross(firstParentElem, level - 1);
                            GermplasmCrossElement expandedSecondParent = expandGermplasmCross(secondParentElem, level - 1);

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
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstParent, germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmByGID(germplasmToExpand.getGpid2());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondParent, germplasmToExpand.getGpid2());

                            Germplasm firstGrandParent = getGermplasmWithPrefName(firstParent.getGpid1());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstGrandParent, firstParent.getGpid1());
                            SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                            firstGrandParentElem.setGermplasm(firstGrandParent);
                            Germplasm secondGrandParent = getGermplasmWithPrefName(firstParent.getGpid2());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondGrandParent, secondParent.getGpid2());
                            SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                            secondGrandParentElem.setGermplasm(secondGrandParent);

                            Germplasm thirdGrandParent = getGermplasmWithPrefName(secondParent.getGpid1());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(thirdGrandParent, secondParent.getGpid1());
                            SingleGermplasmCrossElement thirdGrandParentElem = new SingleGermplasmCrossElement();
                            thirdGrandParentElem.setGermplasm(thirdGrandParent);
                            Germplasm fourthGrandParent = getGermplasmWithPrefName(secondParent.getGpid2());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(fourthGrandParent, secondParent.getGpid2());
                            SingleGermplasmCrossElement fourthGrandParentElem = new SingleGermplasmCrossElement();
                            fourthGrandParentElem.setGermplasm(fourthGrandParent);

                            //expand the grand parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1);
                            GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1);
                            GermplasmCrossElement expandedThirdGrandParent = expandGermplasmCross(thirdGrandParentElem, level - 1);
                            GermplasmCrossElement expandedFourthGrandParent = expandGermplasmCross(fourthGrandParentElem, level - 1);

                            //create the cross object for the first pair of grand parents
                            GermplasmCross firstCross = new GermplasmCross();
                            firstCross.setFirstParent(expandedFirstGrandParent);
                            firstCross.setSecondParent(expandedSecondGrandParent);
                            //compute the number of crosses before this cross
                            int numOfCrossesForFirst = 0;
                            if (expandedFirstGrandParent instanceof GermplasmCross) {
                                numOfCrossesForFirst = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                            }
                            firstCross.setNumberOfCrossesBefore(numOfCrossesForFirst);

                            //create the cross object for the second pair of grand parents
                            GermplasmCross secondCross = new GermplasmCross();
                            secondCross.setFirstParent(expandedThirdGrandParent);
                            secondCross.setSecondParent(expandedFourthGrandParent);
                            //compute the number of crosses before this cross
                            int numOfCrossesForSecond = 0;
                            if (expandedThirdGrandParent instanceof GermplasmCross) {
                                numOfCrossesForSecond = ((GermplasmCross) expandedThirdGrandParent).getNumberOfCrossesBefore() + 1;
                            }

                            //create the cross of the two sets of grandparents, this will be returned
                            cross.setFirstParent(firstCross);
                            cross.setSecondParent(secondCross);
                            //compute the number of crosses before the cross to be returned
                            int numOfCrosses = numOfCrossesForFirst + 1;
                            if (expandedSecondGrandParent instanceof GermplasmCross) {
                                numOfCrosses = numOfCrosses + ((GermplasmCross) expandedSecondGrandParent).getNumberOfCrossesBefore() + 1;
                            }
                            cross.setNumberOfCrossesBefore(numOfCrosses);

                        } else if (methodName.contains("three-way cross")) {
                            //get the two parents first
                            Germplasm firstParent = getGermplasmByGID(germplasmToExpand.getGpid1());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstParent, germplasmToExpand.getGpid1());
                            Germplasm secondParent = getGermplasmByGID(germplasmToExpand.getGpid2());
                            GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondParent, germplasmToExpand.getGpid2());

                            //check for the parent generated by a cross, the other one should be a derived germplasm
                            if (firstParent.getGnpgs() > 0) {
                                // the first parent is the one created by a cross
                                Germplasm firstGrandParent = getGermplasmWithPrefName(firstParent.getGpid1());
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstGrandParent, firstParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent = getGermplasmWithPrefName(firstParent.getGpid2());
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondGrandParent, firstParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1);

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
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondParent, germplasmToExpand.getGpid2());
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);

                                // create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(secondParentElem);
                                //compute the number of crosses before this cross
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            } else {
                                // the second parent is the one created by a cross
                                Germplasm firstGrandParent = getGermplasmWithPrefName(secondParent.getGpid1());
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstGrandParent, secondParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent = getGermplasmWithPrefName(secondParent.getGpid2());
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(secondGrandParent, secondParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1);

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
                                GermplasmDataManagerUtil.checkIfGermplasmIsNull(firstParent, germplasmToExpand.getGpid1());
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);

                                //create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(firstParentElem);
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            }

                        }

                        return cross;
                    } else {
                        logAndThrowException("Error with expanding cross, can not find method with id: " + germplasmToExpand.getMethodId(),
                                LOG);
                    }
                }
            } else {
                logAndThrowException("expandGermplasmCross was incorrectly called", LOG);
            }
        }
        return element;
    }

    
    @Override
    public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
    	Database centralInstance = Database.CENTRAL;
    	Database localInstance = Database.LOCAL;
    	
    	List<Location> allLocations = getFromInstanceByMethod(getLocationDAO(), centralInstance, "getAllBreedingLocations", new Object[] {}, new Class[] {});
    	allLocations.addAll(getFromInstanceByMethod(getLocationDAO(), localInstance, "getAllBreedingLocations", new Object[] {}, new Class[] {}));
    	
    	return allLocations;
    }
    
    
    @Override
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
    
}
