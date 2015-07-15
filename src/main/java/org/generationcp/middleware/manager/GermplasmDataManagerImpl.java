/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
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
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the GermplasmDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 * @author Kevin Manansala, Lord Hendrix Barboza
 *
 */
@SuppressWarnings("unchecked")
public class GermplasmDataManagerImpl extends DataManager implements GermplasmDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmDataManagerImpl.class);

	public GermplasmDataManagerImpl() {
	}

	public GermplasmDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getAllLocations() throws MiddlewareQueryException {
		List<Location> locations = this.getLocationDao().getAll();
		Collections.sort(locations);
		return locations;
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getLocationDao().getAll(start, numOfRows);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public long countAllLocations() throws MiddlewareQueryException {
		return this.countAll(this.getLocationDao());
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

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByCountry", new Object[] {country}, new Class[] {Country.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByCountry", "getByCountry");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {country},
				new Class[] {Country.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByCountry", new Object[] {country}, new Class[] {Country.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
		return this.getAllByMethod(this.getLocationDao(), "getByType", new Object[] {type}, new Class[] {Integer.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByType", "getByType");
		return this.getFromCentralAndLocalByMethod(this.getLocationDao(), methods, start, numOfRows, new Object[] {type},
				new Class[] {Integer.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public long countLocationsByType(Integer type) throws MiddlewareQueryException {
		return this.countAllByMethod(this.getLocationDao(), "countByType", new Object[] {type}, new Class[] {Integer.class});
	}

	@Override
	public List<Germplasm> getAllGermplasm(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
		return super.getFromInstanceByMethod(this.getGermplasmDao(), instance, "getAll", new Object[] {start, numOfRows}, new Class[] {
				Integer.TYPE, Integer.TYPE});
	}

	@Override
	public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, GetGermplasmByNameModes mode, Operation op,
			Integer status, GermplasmNameType type, Database instance) throws MiddlewareQueryException {
		String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
		return this.getGermplasmDao().getByName(nameToUse, op, status, type, start, numOfRows);
	}

	@Override
	public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
		List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
		return this.getGermplasmDao().getByName(names, op, start, numOfRows);
	}

	@Override
	public long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
			Database instance) throws MiddlewareQueryException {
		String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
		return this.getGermplasmDao().countByName(nameToUse, op, status, type);
	}

	@Override
	public long countGermplasmByName(String name, Operation operation) throws MiddlewareQueryException {
		List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);
		return this.getGermplasmDao().countByName(names, operation);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		// get first all the IDs
		List<Integer> germplasmIds = new ArrayList<Integer>();
		germplasmIds.addAll(this.getGermplasmDao().getIdsByName(name, start, numOfRows));
		germplasms.addAll(this.getGermplasmDao().getGermplasmByIds(germplasmIds, start, numOfRows));
		return germplasms;
	}

	@Override
	public List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
			throws MiddlewareQueryException {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		GermplasmDAO dao = this.getGermplasmDao();
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
		GermplasmDAO dao = this.getGermplasmDao();
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
		GermplasmDAO dao = this.getGermplasmDao();
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
		GermplasmDAO dao = this.getGermplasmDao();
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
			germplasm = this.getGermplasmDao().getById(updatedGid, false);
			if (germplasm != null) {
				updatedGid = germplasm.getGrplce();
			}
		} while (germplasm != null && !new Integer(0).equals(updatedGid));
		return germplasm;
	}

	@Override
	public Germplasm getGermplasmWithPrefName(Integer gid) throws MiddlewareQueryException {
		Germplasm germplasm = this.getGermplasmByGID(gid);
		if (germplasm != null) {
			Name preferredName = this.getPreferredNameByGID(germplasm.getGid());
			germplasm.setPreferredName(preferredName);
		}
		return germplasm;
	}

	@Override
	public Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
		return this.getGermplasmDao().getByGIDWithPrefAbbrev(gid);
	}

	@Override
	public Name getGermplasmNameByID(Integer id) throws MiddlewareQueryException {
		return this.getNameDao().getById(id, false);
	}

	@Override
	public List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException {
		return this.getNameDao().getByGIDWithFilters(gid, status, type);
	}

	@Override
	public Name getPreferredNameByGID(Integer gid) throws MiddlewareQueryException {
		List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public String getPreferredNameValueByGID(Integer gid) throws MiddlewareQueryException {
		List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0).getNval();
		}
		return null;
	}

	@Override
	public Name getPreferredAbbrevByGID(Integer gid) throws MiddlewareQueryException {
		List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 2, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public Name getPreferredIdByGID(Integer gid) throws MiddlewareQueryException {
		List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 8, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public List<Name> getPreferredIdsByListId(Integer listId) throws MiddlewareQueryException {
		return this.getNameDao().getPreferredIdsByListId(listId);
	}

	@Override
	public Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode) throws MiddlewareQueryException {
		return this.getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
	}

	@Override
	public Integer updateGermplasmPrefName(Integer gid, String newPrefName) throws MiddlewareQueryException {
		this.updateGermplasmPrefNameAbbrev(gid, newPrefName, "Name");
		return gid;
	}

	@Override
	public Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev) throws MiddlewareQueryException {
		this.updateGermplasmPrefNameAbbrev(gid, newPrefAbbrev, "Abbreviation");
		return gid;
	}

	private void updateGermplasmPrefNameAbbrev(Integer gid, String newPrefValue, String nameOrAbbrev) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			// begin update transaction
			trans = session.beginTransaction();
			NameDAO dao = this.getNameDao();

			// check for a name record with germplasm = gid, and nval = newPrefName
			Name newPref = this.getNameByGIDAndNval(gid, newPrefValue, GetGermplasmByNameModes.NORMAL);
			// if a name record with the specified nval exists,
			if (newPref != null) {
				// get germplasm's existing preferred name/abbreviation, set as
				// alternative name, change nstat to 0
				Name oldPref = null;
				int newNstat = 0;
				// nstat to be assigned to newPref: 1 for Name, 2 for Abbreviation
				if ("Name".equals(nameOrAbbrev)) {
					oldPref = this.getPreferredNameByGID(gid);
					newNstat = 1;
				} else if ("Abbreviation".equals(nameOrAbbrev)) {
					oldPref = this.getPreferredAbbrevByGID(gid);
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
				this.logAndThrowException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue="
						+ newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "): The specified Germplasm Name does not exist.",
						new Throwable(), GermplasmDataManagerImpl.LOG);
			}

			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue="
					+ newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "):  " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public Integer addGermplasmName(Name name) throws MiddlewareQueryException {
		List<Name> names = new ArrayList<Name>();
		names.add(name);
		List<Integer> ids = this.addOrUpdateGermplasmName(names, Operation.ADD);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmName(List<Name> names) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmName(names, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmName(Name name) throws MiddlewareQueryException {
		List<Name> names = new ArrayList<Name>();
		names.add(name);
		List<Integer> ids = this.addOrUpdateGermplasmName(names, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmName(List<Name> names) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmName(names, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmName(List<Name> names, Operation operation) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int namesSaved = 0;
		List<Integer> idNamesSaved = new ArrayList<Integer>();
		try {
			// begin save transaction
			trans = session.beginTransaction();
			NameDAO dao = this.getNameDao();

			for (Name name : names) {
				Name recordAdded = dao.saveOrUpdate(name);
				idNamesSaved.add(recordAdded.getNid());
				namesSaved++;
				if (namesSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error while saving Germplasm Name: GermplasmDataManager.addOrUpdateGermplasmName(names=" + names
					+ ", operation=" + operation + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return idNamesSaved;
	}

	@Override
	public List<Attribute> getAttributesByGID(Integer gid) throws MiddlewareQueryException {
		return this.getAttributeDao().getByGID(gid);
	}

	@Override
	public List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getAttributeDao(), "getAttributeTypesByGIDList", new Object[] {gidList}, new Class[] {List.class});
	}

	@Override
	public Map<Integer, String> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList)
			throws MiddlewareQueryException {
		Map<Integer, String> returnMap = new HashMap<Integer, String>();
		// initialize map with GIDs
		for (Integer gid : gidList) {
			returnMap.put(gid, "-");
		}

		// retrieve attribute values
		List<Attribute> attributeList =
				super.getAllByMethod(this.getAttributeDao(), "getAttributeValuesByTypeAndGIDList", new Object[] {attributeType, gidList},
						new Class[] {Integer.class, List.class});
		for (Attribute attribute : attributeList) {
			returnMap.put(attribute.getGermplasmId(), attribute.getAval());
		}

		return returnMap;
	}

	@Override
	public Method getMethodByID(Integer id) throws MiddlewareQueryException {
		return this.getMethodDao().getById(id, false);
	}

	@Override
	public List<Method> getMethodsByIDs(List<Integer> ids) throws MiddlewareQueryException {
		List<Method> results = new ArrayList<Method>();

		if (!ids.isEmpty()) {
			results.addAll(this.getMethodDao().getMethodsByIds(ids));
		}

		return results;
	}

	@Override
	public List<Method> getAllMethods() throws MiddlewareQueryException {
		return this.getAllByMethod(this.getMethodDao(), "getAllMethod", new Object[] {}, new Class[] {});
	}

	@Override
	public List<Method> getAllMethodsNotGenerative() throws MiddlewareQueryException {
		return this.getAllByMethod(this.getMethodDao(), "getAllMethodsNotGenerative", new Object[] {}, new Class[] {});
	}

	@Override
	public long countAllMethods() throws MiddlewareQueryException {
		return this.countAll(this.getMethodDao());
	}

	@Override
	public List<Method> getMethodsByUniqueID(String programUUID) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByUniqueID", new Object[] {programUUID}, new Class[] {String.class});
	}

	@Override
	public long countMethodsByUniqueID(String programUUID) throws MiddlewareQueryException {
		return super.countAllByMethod(this.getMethodDao(), "countByUniqueID", new Object[] {programUUID}, new Class[] {String.class});
	}

	@Override
	public List<Method> getMethodsByType(String type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByType", new Object[] {type}, new Class[] {String.class});
	}

	@Override
	public List<Method> getMethodsByType(String type, String programUUID) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByType", new Object[] {type, programUUID}, new Class[] {String.class,
				String.class});
	}

	@Override
	public List<Method> getMethodsByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByType", "getByType");
		return super.getFromCentralAndLocalByMethod(this.getMethodDao(), methods, start, numOfRows, new Object[] {type},
				new Class[] {String.class});
	}

	@Override
	public long countMethodsByType(String type) throws MiddlewareQueryException {
		return super.countAllByMethod(this.getMethodDao(), "countByType", new Object[] {type}, new Class[] {String.class});
	}

	@Override
	public long countMethodsByType(String type, String programUUID) throws MiddlewareQueryException {
		return super.countAllByMethod(this.getMethodDao(), "countByType", new Object[] {type, programUUID}, new Class[] {String.class,
				String.class});
	}

	@Override
	public List<Method> getMethodsByGroup(String group) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByGroup", new Object[] {group}, new Class[] {String.class});
	}

	@Override
	public List<Method> getMethodsByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
		List<String> methods = Arrays.asList("countByGroup", "getByGroup");
		return super.getFromCentralAndLocalByMethod(this.getMethodDao(), methods, start, numOfRows, new Object[] {group},
				new Class[] {String.class});
	}

	@Override
	public List<Method> getMethodsByGroupAndType(String group, String type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByGroupAndType", new Object[] {group, type}, new Class[] {String.class,
				String.class});
	}

	@Override
	public List<Method> getMethodsByGroupAndTypeAndName(String group, String type, String name) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByGroupAndTypeAndName", new Object[] {group, type, name}, new Class[] {
				String.class, String.class, String.class});
	}

	@Override
	public long countMethodsByGroup(String group) throws MiddlewareQueryException {
		return super.countAllByMethod(this.getMethodDao(), "countByGroup", new Object[] {group}, new Class[] {String.class});
	}

	@Override
	public Integer addMethod(Method method) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer methodId = null;
		try {
			trans = session.beginTransaction();
			MethodDAO dao = this.getMethodDao();

			Method recordSaved = dao.saveOrUpdate(method);
			methodId = recordSaved.getMid();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e,
					GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return methodId;
	}

	@Override
	public Method editMethod(Method method) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Method recordSaved = null;

		try {

			if (method.getMid() == null) {
				throw new MiddlewareQueryException("method has no Id or is not a local method");
			}

			trans = session.beginTransaction();
			MethodDAO dao = this.getMethodDao();

			recordSaved = dao.merge(method);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e,
					GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return recordSaved;
	}

	@Override
	public List<Integer> addMethod(List<Method> methods) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		List<Integer> idMethodsSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			MethodDAO dao = this.getMethodDao();

			for (Method method : methods) {
				Method recordSaved = dao.saveOrUpdate(method);
				idMethodsSaved.add(recordSaved.getMid());
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving a list of Methods: GermplasmDataManager.addMethod(methods=" + methods
					+ "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return idMethodsSaved;
	}

	@Override
	public void deleteMethod(Method method) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getMethodDao().makeTransient(method);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while deleting Method: GermplasmDataMananger.deleteMethod(method=" + method
					+ "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException {
		return this.getUserDefinedFieldDao().getById(id, false);
	}

	@Override
	public UserDefinedField getUserDefinedFieldByLocalFieldNo(Integer lfldno) throws MiddlewareQueryException {
		return this.getUserDefinedFieldDao().getByLocalFieldNo(lfldno);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Country getCountryById(Integer id) throws MiddlewareQueryException {
		return this.getCountryDao().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Location getLocationByID(Integer id) throws MiddlewareQueryException {
		return this.getLocationDao().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByIDs(List<Integer> ids) throws MiddlewareQueryException {
		List<Location> results = new ArrayList<Location>();

		if (ids != null && !ids.isEmpty()) {
			results.addAll(this.getLocationDao().getLocationByIds(ids));
		}

		Collections.sort(results, new Comparator<Object>() {

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
	public Bibref getBibliographicReferenceByID(Integer id) throws MiddlewareQueryException {
		return this.getBibrefDao().getById(id, false);
	}

	@Override
	public Integer addBibliographicReference(Bibref bibref) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer idBibrefSaved = null;
		try {
			trans = session.beginTransaction();
			BibrefDAO dao = this.getBibrefDao();

			Bibref recordSaved = dao.saveOrUpdate(bibref);
			idBibrefSaved = recordSaved.getRefid();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving Bibliographic Reference: GermplasmDataManager.addBibliographicReference(bibref="
							+ bibref + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return idBibrefSaved;
	}

	@Override
	public Integer addGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute);
		List<Integer> ids = this.addGermplasmAttribute(attributes);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException {
		return this.addOrUpdateAttributes(attributes, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute);
		List<Integer> ids = this.updateGermplasmAttribute(attributes);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException {
		return this.addOrUpdateAttributes(attributes, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateAttributes(List<Attribute> attributes, Operation operation) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		List<Integer> idAttributesSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			AttributeDAO dao = this.getAttributeDao();

			for (Attribute attribute : attributes) {
				Attribute recordSaved = dao.saveOrUpdate(attribute);
				idAttributesSaved.add(recordSaved.getAid());
			}
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Attribute: GermplasmDataManager.addOrUpdateAttributes(attributes="
					+ attributes + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return idAttributesSaved;
	}

	@Override
	public Attribute getAttributeById(Integer id) throws MiddlewareQueryException {
		return this.getAttributeDao().getById(id, false);
	}

	@Override
	public Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws MiddlewareQueryException {

		// check if the germplasm record identified by gid exists
		Germplasm child = this.getGermplasmByGID(gid);
		if (child == null) {
			this.logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
					+ ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with gid: " + gid, new Throwable(),
					GermplasmDataManagerImpl.LOG);
		}

		// check if the germplasm record identified by progenitorId exists
		Germplasm parent = this.getGermplasmByGID(progenitorId);
		if (parent == null) {
			this.logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
					+ ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with progenitorId: " + progenitorId,
					new Throwable(), GermplasmDataManagerImpl.LOG);
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
			this.addOrUpdateGermplasms(germplasms, Operation.UPDATE);
		} else if (progenitorNumber > 2) {
			ProgenitorDAO dao = this.getProgenitorDao();

			// check if there is an existing Progenitor record
			ProgenitorPK id = new ProgenitorPK(gid, progenitorNumber);
			Progenitor p = dao.getById(id, false);

			if (p != null) {
				// update the existing record
				p.setPid(progenitorId);

				List<Progenitor> progenitors = new ArrayList<Progenitor>();
				progenitors.add(p);
				int updated = this.addOrUpdateProgenitors(progenitors);
				if (updated == 1) {
					return progenitorId;
				}
			} else {
				// create new Progenitor record
				Progenitor newRecord = new Progenitor(id);
				newRecord.setPid(progenitorId);

				List<Progenitor> progenitors = new ArrayList<Progenitor>();
				progenitors.add(newRecord);
				int added = this.addOrUpdateProgenitors(progenitors);
				if (added == 1) {
					return progenitorId;
				}
			}
		} else {
			this.logAndThrowException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId
					+ ", progenitorNumber=" + progenitorNumber + "): Invalid progenitor number: " + progenitorNumber, new Throwable(),
					GermplasmDataManagerImpl.LOG);
		}

		return progenitorId;
	}

	private List<Integer> addOrUpdateGermplasms(List<Germplasm> germplasms, Operation operation) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int germplasmsSaved = 0;
		List<Integer> idGermplasmsSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			GermplasmDAO dao = this.getGermplasmDao();

			for (Germplasm germplasm : germplasms) {
				Germplasm recordSaved = dao.saveOrUpdate(germplasm);
				idGermplasmsSaved.add(recordSaved.getGid());
				recordSaved.setLgid(recordSaved.getGid());
				germplasmsSaved++;
				if (germplasmsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Germplasm: GermplasmDataManager.addOrUpdateGermplasms(germplasms="
					+ germplasms + ", operation=" + operation + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return idGermplasmsSaved;
	}

	private int addOrUpdateProgenitors(List<Progenitor> progenitors) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int progenitorsSaved = 0;
		try {
			trans = session.beginTransaction();
			ProgenitorDAO dao = this.getProgenitorDao();

			for (Progenitor progenitor : progenitors) {
				dao.saveOrUpdate(progenitor);
				progenitorsSaved++;
			}
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Progenitor: GermplasmDataManager.addOrUpdateProgenitors(progenitors="
					+ progenitors + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return progenitorsSaved;
	}

	@Override
	public Integer updateGermplasm(Germplasm germplasm) throws MiddlewareQueryException {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		germplasms.add(germplasm);
		List<Integer> ids = this.updateGermplasm(germplasms);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasm(List<Germplasm> germplasms) throws MiddlewareQueryException {
		if (germplasms != null) {
			List<Integer> gids = new ArrayList<Integer>();
			for (Germplasm germplasm : germplasms) {
				if (germplasm.getGid().equals(germplasm.getGrplce())) {// deleted
					gids.add(germplasm.getGid());
				}
			}
			if (gids != null && !gids.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForGermplasms(gids);
			}
		}
		return this.addOrUpdateGermplasms(germplasms, Operation.UPDATE);
	}

	@Override
	public Integer addGermplasm(Germplasm germplasm, Name preferredName) throws MiddlewareQueryException {
		Map<Germplasm, Name> germplasmNameMap = new HashMap<Germplasm, Name>();
		germplasmNameMap.put(germplasm, preferredName);
		List<Integer> ids = this.addGermplasm(germplasmNameMap);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int germplasmsSaved = 0;
		List<Integer> isGermplasmsSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			GermplasmDAO dao = this.getGermplasmDao();
			NameDAO nameDao = this.getNameDao();

			for (Germplasm germplasm : germplasmNameMap.keySet()) {
				Name name = germplasmNameMap.get(germplasm);

				name.setNstat(Integer.valueOf(1));

				Germplasm germplasmSaved = dao.save(germplasm);
				isGermplasmsSaved.add(germplasmSaved.getGid());
				name.setGermplasmId(germplasmSaved.getGid());
				nameDao.save(name);
				germplasmsSaved++;

				if (germplasmsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Germplasm: GermplasmDataManager.addGermplasm(germplasmNameMap="
					+ germplasmNameMap + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return isGermplasmsSaved;
	}

	@Override
	public Integer addUserDefinedField(UserDefinedField field) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			UserDefinedFieldDAO dao = this.getUserDefinedFieldDao();
			dao.save(field);

			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedField(): " + e.getMessage(), e,
					GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return field.getFldno();
	}

	@Override
	public List<Integer> addUserDefinedFields(List<UserDefinedField> fields) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		List<Integer> isUdfldSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			UserDefinedFieldDAO dao = this.getUserDefinedFieldDao();

			int udfldSaved = 0;
			for (UserDefinedField field : fields) {

				UserDefinedField udflds = dao.save(field);
				isUdfldSaved.add(udflds.getFldno());
				udfldSaved++;

				if (udfldSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedFields(fields="
					+ fields + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return isUdfldSaved;
	}

	@Override
	public Integer addAttribute(Attribute attr) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer isAttrSaved = 0;
		try {
			trans = session.beginTransaction();
			AttributeDAO dao = this.getAttributeDao();
			dao.save(attr);
			isAttrSaved++;

			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Attribute: GermplasmDataManager.addAttribute(addAttribute=" + attr
					+ "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return isAttrSaved;
	}

	@Override
	public List<Integer> addAttributes(List<Attribute> attrs) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		List<Integer> isAttrSaved = new ArrayList<Integer>();
		try {
			trans = session.beginTransaction();
			AttributeDAO dao = this.getAttributeDao();

			int attrSaved = 0;
			for (Attribute attr : attrs) {
				Attribute newAttr = dao.save(attr);
				isAttrSaved.add(newAttr.getAid());
				attrSaved++;

				if (attrSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving UserDefinedField: GermplasmDataManager.addAttributes(attrs="
					+ isAttrSaved + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

		return isAttrSaved;
	}

	@Override
	public List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(List<String> germplasmNames, GetGermplasmByNameModes mode)
			throws MiddlewareQueryException {
		List<String> namesToUse = GermplasmDataManagerUtil.getNamesToUseByMode(germplasmNames, mode);
		return super.getAllByMethod(this.getNameDao(), "getGermplasmNameDetailsByNames", new Object[] {namesToUse, mode}, new Class[] {
				List.class, GetGermplasmByNameModes.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Country> getAllCountry() throws MiddlewareQueryException {
		return super.getAllByMethod(this.getCountryDao(), "getAllCountry", new Object[] {}, new Class[] {});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByCountryAndType", new Object[] {country, type}, new Class[] {Country.class,
				Integer.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByNameCountryAndType(String name, Country country, Integer type) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getByNameCountryAndType", new Object[] {name, country, type}, new Class[] {
				String.class, Country.class, Integer.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getLocationDao(), "getLocationDetails", new Object[] {locationId, start, numOfRows}, new Class[] {
				Integer.class, Integer.class, Integer.class});

	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType)
			throws MiddlewareQueryException {
		return super.getAllByMethod(this.getUserDefinedFieldDao(), "getByFieldTableNameAndType", new Object[] {tableName, fieldType},
				new Class[] {String.class, String.class});
	}

	@Override
	public List<Method> getMethodsByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
		return super.getAllByMethod(this.getMethodDao(), "getByGroupIncludesGgroup", new Object[] {group}, new Class[] {String.class});
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
		return this.getFromInstanceByMethod(this.getLocationDAO(), Database.LOCAL, "getAllBreedingLocations", new Object[] {},
				new Class[] {});
	}

	@Override
	public String getNextSequenceNumberForCrossName(String prefix) throws MiddlewareQueryException {
		String nextSequenceStr = "1";
		nextSequenceStr = this.getGermplasmDao().getNextSequenceNumberForCrossName(prefix);
		return nextSequenceStr;
	}

	@Override
	public Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, String> toreturn = new HashMap<Integer, String>();

		if (!gids.isEmpty()) {
			Map<Integer, String> results = this.getNameDao().getPrefferedIdsByGIDs(gids);
			for (Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
		}
		return toreturn;
	}

	@Override
	public List<Germplasm> getGermplasmByLocationId(String name, int locationID) throws MiddlewareQueryException {
		List<Germplasm> germplasmList = new ArrayList<Germplasm>();
		germplasmList.addAll(this.getGermplasmDao().getByLocationId(name, locationID));
		return germplasmList;
	}

	@Override
	public Germplasm getGermplasmWithMethodType(Integer gid) throws MiddlewareQueryException {
		return this.getGermplasmDao().getByGIDWithMethodType(gid);
	}

	@Override
	public List<Germplasm> getGermplasmByGidRange(int startGIDParam, int endGIDParam) throws MiddlewareQueryException {
		List<Germplasm> germplasmList = new ArrayList<Germplasm>();

		int startGID = startGIDParam;
		int endGID = endGIDParam;
		// assumes the lesser value be the start of the range
		if (endGID < startGID) {
			int temp = endGID;
			endGID = startGID;
			startGID = temp;
		}

		germplasmList.addAll(this.getGermplasmDao().getByGIDRange(startGID, endGID));
		return germplasmList;
	}

	@Override
	public List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException {
		List<Germplasm> germplasmList = new ArrayList<Germplasm>();
		germplasmList.addAll(this.getGermplasmDao().getByGIDList(gids));
		return germplasmList;
	}

	@Override
	public Map<Integer, String> getPreferredNamesByGids(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, String> toreturn = new HashMap<Integer, String>();

		if (!gids.isEmpty()) {
			Map<Integer, String> results = this.getNameDao().getPrefferedNamesByGIDs(gids);
			for (Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
		}

		return toreturn;
	}

	@Override
	public Map<Integer, String> getLocationNamesByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.getLocationDao().getLocationNamesMapByGIDs(gids);
	}

	@Override
	public List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents, boolean withInventoryOnly)
			throws MiddlewareQueryException {
		return this.getGermplasmDao().searchForGermplasms(q, o, includeParents, withInventoryOnly);
	}

	@Override
	public Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.getGermplasmDao().getGermplasmDatesByGids(gids);
	}

	@Override
	public Map<Integer, Object> getMethodsByGids(List<Integer> gids) throws MiddlewareQueryException {

		Map<Integer, Object> results = new HashMap<Integer, Object>();
		Map<Integer, Integer> methodIds = new HashMap<Integer, Integer>();

		methodIds = this.getGermplasmDao().getMethodIdsByGids(gids);
		for (Map.Entry<Integer, Integer> entry : methodIds.entrySet()) {
			Method method = this.getMethodDao().getById(entry.getValue(), false);
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

		return this.getTermBuilder().getTermsByIds(ids);

	}

	@Override
	public Method getMethodByCode(String code, String programUUID) throws MiddlewareQueryException {
		Method method = new Method();
		method = this.getMethodDao().getByCode(code, programUUID);
		return method;
	}

	@Override
	public Method getMethodByCode(String code) throws MiddlewareQueryException {
		Method method = new Method();
		method = this.getMethodDao().getByCode(code);
		return method;
	}

	@Override
	public Method getMethodByName(String name) throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		methods = this.getMethodDao().getByName(name);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public Method getMethodByName(String name, String programUUID) throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		methods = this.getMethodDao().getByName(name, programUUID);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
		return this.getGermplasmDao().getProgenitorsByGIDWithPrefName(gid);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, String programUUID) throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().getProgramFavorites(type, programUUID);
	}

	@Override
	public int countProgramFavorites(FavoriteType type) throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().countProgramFavorites(type);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, int max, String programUUID) throws MiddlewareQueryException {
		return this.getProgramFavoriteDao().getProgramFavorites(type, max, programUUID);
	}

	@Override
	public void saveProgramFavorites(List<ProgramFavorite> list) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int favoriteSaved = 0;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = this.getProgramFavoriteDao();

			for (ProgramFavorite favorite : list) {

				dao.save(favorite);
				favoriteSaved++;

				if (favoriteSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorites(list="
					+ list + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public void saveProgramFavorite(ProgramFavorite favorite) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = this.getProgramFavoriteDao();
			dao.save(favorite);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorite(favorite="
					+ favorite + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

	}

	@Override
	public void deleteProgramFavorites(List<ProgramFavorite> list) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		int favoriteDeleted = 0;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = this.getProgramFavoriteDao();

			for (ProgramFavorite favorite : list) {

				dao.makeTransient(favorite);

				if (favoriteDeleted % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving ProgramFavorite: GermplasmDataManager.deleteProgramFavorites(list="
					+ list + "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

	}

	@Override
	public void deleteProgramFavorite(ProgramFavorite favorite) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			ProgramFavoriteDAO dao = this.getProgramFavoriteDao();
			dao.makeTransient(favorite);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while deleting ProgramFavorite: GermplasmDataManager.deleteProgramFavorite(favorite=" + favorite
							+ "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}

	}

	@Override
	public int getMaximumSequence(boolean isBulk, String prefix, String suffix, int count) throws MiddlewareQueryException {
		return this.getNameBuilder().getMaximumSequence(isBulk, prefix, suffix, count);
	}

	@Override
	public boolean checkIfMatches(String name) throws MiddlewareQueryException {
		return this.getNameDao().checkIfMatches(name);
	}

	@Override
	public List<Method> getProgramMethods(String programUUID) throws MiddlewareQueryException {
		return this.getMethodDao().getProgramMethods(programUUID);
	}

	@Override
	public void deleteProgramMethodsByUniqueId(String programUUID) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		MethodDAO methodDao = this.getMethodDao();
		int deleted = 0;
		try {
			trans = session.beginTransaction();
			List<Method> list = this.getProgramMethods(programUUID);
			for (Method method : list) {
				methodDao.makeTransient(method);
				if (deleted % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					methodDao.flush();
					methodDao.clear();
				}
			}
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while deleting methods: GermplasmDataManager.deleteProgramMethodsByUniqueId(uniqueId=" + programUUID
							+ "): " + e.getMessage(), e, GermplasmDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public Map<Integer, GermplasmPedigreeTreeNode> getDirectParentsForStudy(int studyId) {

		GermplasmDAO dao = this.getGermplasmDao();
		Map<Integer, Map<GermplasmNameType, Name>> namesMap = dao.getGermplasmParentNamesForStudy(studyId);
		List<Germplasm> germs = dao.getGermplasmParentsForStudy(studyId);

		Map<Integer, GermplasmPedigreeTreeNode> germNodes = new HashMap<>();

		for (Germplasm germ : germs) {
			GermplasmPedigreeTreeNode root = new GermplasmPedigreeTreeNode();
			root.setGermplasm(germ);

			List<GermplasmPedigreeTreeNode> parents = new ArrayList<>();
			Map<GermplasmNameType, Name> names = namesMap.get(germ.getGpid1());

			// TODO: compare again new GermplasmNameTypes in merged database

			GermplasmPedigreeTreeNode femaleNode = new GermplasmPedigreeTreeNode();
			Germplasm female = new Germplasm(germ.getGpid1());
			female.setPreferredName(this.getPreferredName(names));
			female.setPreferredAbbreviation(this.getNameByType(names, GermplasmNameType.LINE_NAME).getNval());
			female.setSelectionHistory(this.getNameByType(names, GermplasmNameType.OLD_MUTANT_NAME_1).getNval());
			female.setCrossName(this.getNameByType(names, GermplasmNameType.CROSS_NAME).getNval());
			female.setAccessionName(this.getNameByType(names, GermplasmNameType.GERMPLASM_BANK_ACCESSION_NUMBER).getNval());
			femaleNode.setGermplasm(female);

			names = namesMap.get(germ.getGpid2());
			GermplasmPedigreeTreeNode maleNode = new GermplasmPedigreeTreeNode();
			Germplasm male = new Germplasm(germ.getGpid2());
			male.setPreferredName(this.getPreferredName(names));
			male.setPreferredAbbreviation(this.getNameByType(names, GermplasmNameType.LINE_NAME).getNval());
			male.setSelectionHistory(this.getNameByType(names, GermplasmNameType.OLD_MUTANT_NAME_1).getNval());
			male.setCrossName(this.getNameByType(names, GermplasmNameType.CROSS_NAME).getNval());
			male.setAccessionName(this.getNameByType(names, GermplasmNameType.GERMPLASM_BANK_ACCESSION_NUMBER).getNval());
			maleNode.setGermplasm(male);

			parents.add(femaleNode);
			parents.add(maleNode);
			root.setLinkedNodes(parents);

			germNodes.put(germ.getGid(), root);
		}

		return germNodes;
	}

	/**
	 * Local method for getting a particular germplasm's Name.
	 *
	 * @param namesMap The Map containing Names for a germplasm. This is usually provided by getGermplasmParentNamesForStudy() in
	 *        GermplasmDAO.
	 * @param ntype the name type, i.e. Pedigree, Selection History, Cross Name,etc.
	 * @return an instance of Name representing the searched name, or an empty Name instance if it doesn't exist
	 */
	private Name getNameByType(Map<GermplasmNameType, Name> names, GermplasmNameType ntype) {
		Name n = null;
		if (null != names) {
			n = names.get(ntype);
		}

		return null == n ? new Name() : n;
	}

	private Name getPreferredName(Map<GermplasmNameType, Name> names) {
		for (Name n : names.values()) {
			if (1 == n.getNstat()) {
				return n;
			}
		}

		return new Name();
	}

	@Override
	public Germplasm getGermplasmByLocalGid(Integer lgid) throws MiddlewareQueryException {
		return this.getGermplasmDao().getByLGid(lgid);
	}
}
