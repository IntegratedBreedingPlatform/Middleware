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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
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
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GermplasmDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 * 
 * @author Kevin Manansala, Lord Hendrix Barboza
 * 
 */
@Transactional
public class GermplasmDataManagerImpl extends DataManager implements GermplasmDataManager {

	public GermplasmDataManagerImpl() {
	}

	public GermplasmDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmDataManagerImpl(final HibernateSessionProvider sessionProvider, final String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public List<Germplasm> getAllGermplasm(final int start, final int numOfRows) {
		return this.getGermplasmDao().getAll(start, numOfRows);
	}

	@Override
	public List<Germplasm> getGermplasmByName(final String name, final int start, final int numOfRows, final Operation op)
			throws MiddlewareQueryException {
		return this.getGermplasmDao().getByNamePermutations(name, op, start, numOfRows);
	}

	@Override
	public long countGermplasmByName(final String name, final Operation operation) throws MiddlewareQueryException {
		return this.getGermplasmDao().countByNamePermutations(name, operation);
	}

	@Override
	public List<Germplasm> getGermplasmByLocationName(final String name, final int start, final int numOfRows, final Operation op)
			throws MiddlewareQueryException {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		final GermplasmDAO dao = this.getGermplasmDao();
		if (op == Operation.EQUAL) {
			germplasms = dao.getByLocationNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			germplasms = dao.getByLocationNameUsingLike(name, start, numOfRows);
		}
		return germplasms;
	}

	@Override
	public long countGermplasmByLocationName(final String name, final Operation op) {
		long count = 0;
		final GermplasmDAO dao = this.getGermplasmDao();
		if (op == Operation.EQUAL) {
			count = dao.countByLocationNameUsingEqual(name);
		} else if (op == Operation.LIKE) {
			count = dao.countByLocationNameUsingLike(name);
		}
		return count;
	}

	@Override
	public List<Germplasm> getGermplasmByMethodName(final String name, final int start, final int numOfRows, final Operation op) {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		final GermplasmDAO dao = this.getGermplasmDao();
		if (op == Operation.EQUAL) {
			germplasms = dao.getByMethodNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			germplasms = dao.getByMethodNameUsingLike(name, start, numOfRows);
		}
		return germplasms;
	}

	@Override
	public long countGermplasmByMethodName(final String name, final Operation op) {
		long count = 0;
		final GermplasmDAO dao = this.getGermplasmDao();
		if (op == Operation.EQUAL) {
			count = dao.countByMethodNameUsingEqual(name);
		} else if (op == Operation.LIKE) {
			count = dao.countByMethodNameUsingLike(name);
		}
		return count;
	}

	@Override
	public Germplasm getGermplasmByGID(final Integer gid) {
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
	public Germplasm getGermplasmWithPrefName(final Integer gid) {
		final Germplasm germplasm = this.getGermplasmByGID(gid);
		if (germplasm != null) {
			final Name preferredName = this.getPreferredNameByGID(germplasm.getGid());
			germplasm.setPreferredName(preferredName);
		}
		return germplasm;
	}

	@Override
	public Germplasm getGermplasmWithPrefAbbrev(final Integer gid) {
		return this.getGermplasmDao().getByGIDWithPrefAbbrev(gid);
	}

	@Override
	public Name getGermplasmNameByID(final Integer id) {
		return this.getNameDao().getById(id, false);
	}

	@Override
	public List<Name> getNamesByGID(final Integer gid, final Integer status, final GermplasmNameType type) {
		return this.getNameDao().getByGIDWithFilters(gid, status, type);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.generationcp.middleware.manager.api.GermplasmDataManager#getByGIDWithListTypeFilters(java.lang.Integer, java.lang.Integer,
	 *      java.util.List)
	 */
	@Override
	public List<Name> getByGIDWithListTypeFilters(final Integer gid, final Integer status, final List<Integer> type) {
		return this.getNameDao().getByGIDWithListTypeFilters(gid, status, type);
	}

	@Override
	public Name getPreferredNameByGID(final Integer gid) {
		final List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public String getPreferredNameValueByGID(final Integer gid) {
		final List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0).getNval();
		}
		return null;
	}

	@Override
	public Name getPreferredAbbrevByGID(final Integer gid) {
		final List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 2, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public Name getPreferredIdByGID(final Integer gid) {
		final List<Name> names = this.getNameDao().getByGIDWithFilters(gid, 8, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public List<Name> getPreferredIdsByListId(final Integer listId) {
		return this.getNameDao().getPreferredIdsByListId(listId);
	}

	@Override
	public Name getNameByGIDAndNval(final Integer gid, final String nval, final GetGermplasmByNameModes mode) {
		return this.getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
	}

	@Override
	public Integer updateGermplasmPrefName(final Integer gid, final String newPrefName) {
		this.updateGermplasmPrefNameAbbrev(gid, newPrefName, "Name");
		return gid;
	}

	@Override
	public Integer updateGermplasmPrefAbbrev(final Integer gid, final String newPrefAbbrev) {
		this.updateGermplasmPrefNameAbbrev(gid, newPrefAbbrev, "Abbreviation");
		return gid;
	}

	private void updateGermplasmPrefNameAbbrev(final Integer gid, final String newPrefValue, final String nameOrAbbrev) {

		try {
			// begin update transaction

			final NameDAO dao = this.getNameDao();

			// check for a name record with germplasm = gid, and nval = newPrefName
			final Name newPref = this.getNameByGIDAndNval(gid, newPrefValue, GetGermplasmByNameModes.NORMAL);
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
				throw new MiddlewareQueryException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid
						+ ", newPrefValue=" + newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev
						+ "): The specified Germplasm Name does not exist.", new Throwable());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid
					+ ", newPrefValue=" + newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public Integer addGermplasmName(final Name name) {
		final List<Name> names = new ArrayList<Name>();
		names.add(name);
		final List<Integer> ids = this.addOrUpdateGermplasmName(names, Operation.ADD);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmName(final List<Name> names) {
		return this.addOrUpdateGermplasmName(names, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmName(final Name name) {
		final List<Name> names = new ArrayList<Name>();
		names.add(name);
		final List<Integer> ids = this.addOrUpdateGermplasmName(names, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmName(final List<Name> names) {
		return this.addOrUpdateGermplasmName(names, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmName(final List<Name> names, final Operation operation) {
		final List<Integer> idNamesSaved = new ArrayList<Integer>();
		try {

			final NameDAO dao = this.getNameDao();

			for (final Name name : names) {
				final Name recordAdded = dao.saveOrUpdate(name);
				idNamesSaved.add(recordAdded.getNid());
			}
		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error while saving Germplasm Name: GermplasmDataManager.addOrUpdateGermplasmName(names="
					+ names + ", operation=" + operation + "): " + e.getMessage(), e);
		}
		return idNamesSaved;
	}

	@Override
	public List<Attribute> getAttributesByGID(final Integer gid) {
		return this.getAttributeDao().getByGID(gid);
	}

	@Override
	public List<UserDefinedField> getAttributeTypesByGIDList(final List<Integer> gidList) {
		return this.getAttributeDao().getAttributeTypesByGIDList(gidList);
	}

	@Override
	public Map<Integer, String> getAttributeValuesByTypeAndGIDList(final Integer attributeType, final List<Integer> gidList) {
		final Map<Integer, String> returnMap = new HashMap<Integer, String>();
		// initialize map with GIDs
		for (final Integer gid : gidList) {
			returnMap.put(gid, "-");
		}

		// retrieve attribute values
		final List<Attribute> attributeList = this.getAttributeDao().getAttributeValuesByTypeAndGIDList(attributeType, gidList);
		for (final Attribute attribute : attributeList) {
			returnMap.put(attribute.getGermplasmId(), attribute.getAval());
		}

		return returnMap;
	}

	@Override
	public Method getMethodByID(final Integer id) {
		return this.getMethodDao().getById(id, false);
	}

	@Override
	public List<Method> getMethodsByIDs(final List<Integer> ids) {
		final List<Method> results = new ArrayList<Method>();

		if (!ids.isEmpty()) {
			results.addAll(this.getMethodDao().getMethodsByIds(ids));
		}

		return results;
	}

	@Override
	public List<Method> getNonGenerativeMethodsByID(final List<Integer> ids) {
		return this.getMethodDao().getMethodsNotGenerativeById(ids);
	}

	@Override
	public List<Method> getAllMethods() {
		return this.getMethodDao().getAllMethod();
	}

	@Override
	public boolean isMethodNamingConfigurationValid(final Integer breedingMethodId) {
		final Method breedingMethod = this.getMethodByID(breedingMethodId);
		if (breedingMethod == null) {
			return false;
		}
		final boolean isConfigurationNotEmpty =
				!(breedingMethod.getSuffix() == null && breedingMethod.getSeparator() == null && breedingMethod.getSnametype() == null
						&& breedingMethod.getPrefix() == null && breedingMethod.getCount() == null);
		return isConfigurationNotEmpty;
	}

	@Override
	public List<Method> getAllMethodsNotGenerative() {
		return this.getMethodDao().getAllMethodsNotGenerative();
	}

	@Override
	public long countAllMethods() {
		return this.countAll(this.getMethodDao());
	}

	@Override
	public List<Method> getMethodsByUniqueID(final String programUUID) {
		return this.getMethodDao().getByUniqueID(programUUID);
	}

	@Override
	public long countMethodsByUniqueID(final String programUUID) {
		return this.getMethodDao().countByUniqueID(programUUID);
	}

	@Override
	public List<Method> getMethodsByType(final String type) {
		return this.getMethodDao().getByType(type);
	}

	@Override
	public List<Method> getMethodsByType(final String type, final String programUUID) {
		return this.getMethodDao().getByType(type, programUUID);
	}

	@Override
	public List<Method> getMethodsByType(final String type, final int start, final int numOfRows) {
		return this.getMethodDao().getByType(type, start, numOfRows);
	}

	@Override
	public long countMethodsByType(final String type) {
		return this.getMethodDao().countByType(type);
	}

	@Override
	public long countMethodsByType(final String type, final String programUUID) {
		return this.getMethodDao().countByType(type, programUUID);
	}

	@Override
	public List<Method> getMethodsByGroup(final String group) {
		return this.getMethodDao().getByGroup(group);
	}

	@Override
	public List<Method> getMethodsByGroup(final String group, final int start, final int numOfRows) {
		return this.getMethodDao().getByGroup(group, start, numOfRows);
	}

	@Override
	public List<Method> getMethodsByGroupAndType(final String group, final String type) {
		return this.getMethodDao().getByGroupAndType(group, type);
	}

	@Override
	public List<Method> getMethodsByGroupAndTypeAndName(final String group, final String type, final String name) {
		return this.getMethodDao().getByGroupAndTypeAndName(group, type, name);
	}

	@Override
	public long countMethodsByGroup(final String group) {
		return this.getMethodDao().countByGroup(group);
	}

	@Override
	public Integer addMethod(final Method method) {

		Integer methodId = null;
		try {

			final MethodDAO dao = this.getMethodDao();

			final Method recordSaved = dao.saveOrUpdate(method);
			methodId = recordSaved.getMid();

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method
					+ "): " + e.getMessage(), e);
		}
		return methodId;
	}

	@Override
	public Method editMethod(final Method method) {

		Method recordSaved = null;

		try {

			if (method.getMid() == null) {
				throw new MiddlewareQueryException("method has no Id or is not a local method");
			}

			final MethodDAO dao = this.getMethodDao();

			recordSaved = dao.merge(method);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method
					+ "): " + e.getMessage(), e);
		}

		return recordSaved;
	}

	@Override
	public List<Integer> addMethod(final List<Method> methods) {

		final List<Integer> idMethodsSaved = new ArrayList<Integer>();
		try {

			final MethodDAO dao = this.getMethodDao();

			for (final Method method : methods) {
				final Method recordSaved = dao.saveOrUpdate(method);
				idMethodsSaved.add(recordSaved.getMid());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving a list of Methods: GermplasmDataManager.addMethod(methods="
					+ methods + "): " + e.getMessage(), e);
		}
		return idMethodsSaved;
	}

	@Override
	public void deleteMethod(final Method method) {

		try {

			this.getMethodDao().makeTransient(method);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while deleting Method: GermplasmDataMananger.deleteMethod(method="
					+ method + "): " + e.getMessage(), e);
		}
	}

	@Override
	public UserDefinedField getUserDefinedFieldByID(final Integer id) {
		return this.getUserDefinedFieldDao().getById(id, false);
	}

	@Override
	public UserDefinedField getUserDefinedFieldByLocalFieldNo(final Integer lfldno) {
		return this.getUserDefinedFieldDao().getByLocalFieldNo(lfldno);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Country getCountryById(final Integer id) {
		return this.getCountryDao().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Location getLocationByID(final Integer id) {
		return this.getLocationDao().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByIDs(final List<Integer> ids) {
		return this.getLocationDao().getLocationByIds(ids);
	}

	@Override
	public Bibref getBibliographicReferenceByID(final Integer id) {
		return this.getBibrefDao().getById(id, false);
	}

	@Override
	public Integer addBibliographicReference(final Bibref bibref) {

		Integer idBibrefSaved = null;
		try {

			final BibrefDAO dao = this.getBibrefDao();

			final Bibref recordSaved = dao.saveOrUpdate(bibref);
			idBibrefSaved = recordSaved.getRefid();

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Bibliographic Reference: GermplasmDataManager.addBibliographicReference(bibref="
							+ bibref + "): " + e.getMessage(), e);
		}
		return idBibrefSaved;
	}

	@Override
	public Integer addGermplasmAttribute(final Attribute attribute) {
		final List<Attribute> attributes = new ArrayList<>();
		attributes.add(attribute);
		final List<Integer> ids = this.addGermplasmAttribute(attributes);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmAttribute(final List<Attribute> attributes) {
		return this.addOrUpdateAttributes(attributes, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmAttribute(final Attribute attribute) {
		final List<Attribute> attributes = new ArrayList<>();
		attributes.add(attribute);
		final List<Integer> ids = this.updateGermplasmAttribute(attributes);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmAttribute(final List<Attribute> attributes) {
		return this.addOrUpdateAttributes(attributes, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateAttributes(final List<Attribute> attributes, final Operation operation) {

		final List<Integer> idAttributesSaved = new ArrayList<>();
		try {

			final AttributeDAO dao = this.getAttributeDao();

			for (final Attribute attribute : attributes) {
				final Attribute recordSaved = dao.saveOrUpdate(attribute);
				idAttributesSaved.add(recordSaved.getAid());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Attribute: GermplasmDataManager.addOrUpdateAttributes(attributes=" + attributes + "): "
							+ e.getMessage(), e);
		}

		return idAttributesSaved;
	}

	@Override
	public Attribute getAttributeById(final Integer id) {
		return this.getAttributeDao().getById(id, false);
	}

	@Override
	public Integer updateProgenitor(final Integer gid, final Integer progenitorId, final Integer progenitorNumber) {

		// check if the germplasm record identified by gid exists
		final Germplasm child = this.getGermplasmByGID(gid);
		if (child == null) {
			throw new MiddlewareQueryException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId="
					+ progenitorId + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with gid: " + gid,
					new Throwable());
		}

		// check if the germplasm record identified by progenitorId exists
		final Germplasm parent = this.getGermplasmByGID(progenitorId);
		if (parent == null) {
			throw new MiddlewareQueryException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId="
					+ progenitorId + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with progenitorId: "
					+ progenitorId, new Throwable());
		}

		// check progenitor number
		if (progenitorNumber == 1 || progenitorNumber == 2) {
			if (progenitorNumber == 1) {
				child.setGpid1(progenitorId);
			} else {
				child.setGpid2(progenitorId);
			}

			final List<Germplasm> germplasms = new ArrayList<Germplasm>();
			germplasms.add(child);
			this.addOrUpdateGermplasms(germplasms, Operation.UPDATE);
		} else if (progenitorNumber > 2) {
			final ProgenitorDAO dao = this.getProgenitorDao();

			// check if there is an existing Progenitor record
			final ProgenitorPK id = new ProgenitorPK(gid, progenitorNumber);
			final Progenitor p = dao.getById(id, false);

			if (p != null) {
				// update the existing record
				p.setPid(progenitorId);

				final List<Progenitor> progenitors = new ArrayList<>();
				progenitors.add(p);
				final int updated = this.addOrUpdateProgenitors(progenitors);
				if (updated == 1) {
					return progenitorId;
				}
			} else {
				// create new Progenitor record
				final Progenitor newRecord = new Progenitor(id);
				newRecord.setPid(progenitorId);

				final List<Progenitor> progenitors = new ArrayList<>();
				progenitors.add(newRecord);
				final int added = this.addOrUpdateProgenitors(progenitors);
				if (added == 1) {
					return progenitorId;
				}
			}
		} else {
			throw new MiddlewareQueryException("Error in GermplasmDataManager.updateProgenitor(gid=" + gid + ", progenitorId="
					+ progenitorId + ", progenitorNumber=" + progenitorNumber + "): Invalid progenitor number: " + progenitorNumber,
					new Throwable());
		}

		return progenitorId;
	}

	private List<Integer> addOrUpdateGermplasms(final List<Germplasm> germplasms, final Operation operation) {
		final List<Integer> idGermplasmsSaved = new ArrayList<Integer>();
		try {
			final GermplasmDAO dao = this.getGermplasmDao();

			for (final Germplasm germplasm : germplasms) {
				final Germplasm recordSaved = dao.saveOrUpdate(germplasm);
				idGermplasmsSaved.add(recordSaved.getGid());
				recordSaved.setLgid(recordSaved.getGid());
			}
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Germplasm: GermplasmDataManager.addOrUpdateGermplasms(germplasms=" + germplasms
							+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idGermplasmsSaved;
	}

	private int addOrUpdateProgenitors(final List<Progenitor> progenitors) {

		int progenitorsSaved = 0;
		try {

			final ProgenitorDAO dao = this.getProgenitorDao();

			for (final Progenitor progenitor : progenitors) {
				dao.saveOrUpdate(progenitor);
				progenitorsSaved++;
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Progenitor: GermplasmDataManager.addOrUpdateProgenitors(progenitors=" + progenitors
							+ "): " + e.getMessage(), e);
		}
		return progenitorsSaved;
	}

	@Override
	public Integer updateGermplasm(final Germplasm germplasm) {
		final List<Germplasm> germplasms = new ArrayList<Germplasm>();
		germplasms.add(germplasm);
		final List<Integer> ids = this.updateGermplasm(germplasms);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasm(final List<Germplasm> germplasms) {
		if (germplasms != null) {
			final List<Integer> gids = new ArrayList<Integer>();
			for (final Germplasm germplasm : germplasms) {
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
	public Integer addGermplasm(final Germplasm germplasm, final Name preferredName) {
		final List<Pair<Germplasm, Name>> pairList = new ArrayList<>();
		pairList.add(new ImmutablePair<Germplasm, Name>(germplasm, preferredName));
		final List<Integer> ids = this.addGermplasm(pairList);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasm(final Map<Germplasm, Name> germplasmNameMap) {
		final List<Pair<Germplasm, Name>> pairList = new ArrayList<>();
		for (final Map.Entry<Germplasm, Name> entry : germplasmNameMap.entrySet()) {
			final Pair<Germplasm, Name> pair = new ImmutablePair<>(entry.getKey(), entry.getValue());
			pairList.add(pair);
		}

		return this.addGermplasm(pairList);
	}

	@Override
	public List<Integer> addGermplasm(final List<Pair<Germplasm, Name>> germplasms) {
		final List<Integer> isGermplasmsSaved = new ArrayList<>();
		try {

			final GermplasmDAO dao = this.getGermplasmDao();
			final NameDAO nameDao = this.getNameDao();

			for (final Pair<Germplasm, Name> pair : germplasms) {
				final Germplasm germplasm = pair.getLeft();
				final Name name = pair.getRight();

				name.setNstat(Integer.valueOf(1));

				final Germplasm germplasmSaved = dao.save(germplasm);
				isGermplasmsSaved.add(germplasmSaved.getGid());
				name.setGermplasmId(germplasmSaved.getGid());
				nameDao.save(name);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Germplasm: GermplasmDataManager.addGermplasm(): "
					+ e.getMessage(), e);
		}
		return isGermplasmsSaved;
	}

	@Override
	public Integer addUserDefinedField(final UserDefinedField field) {

		try {

			final UserDefinedFieldDAO dao = this.getUserDefinedFieldDao();
			dao.save(field);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedField(): " + e.getMessage(), e);
		}

		return field.getFldno();
	}

	@Override
	public List<Integer> addUserDefinedFields(final List<UserDefinedField> fields) {

		final List<Integer> isUdfldSaved = new ArrayList<>();
		try {

			final UserDefinedFieldDAO dao = this.getUserDefinedFieldDao();

			for (final UserDefinedField field : fields) {

				final UserDefinedField udflds = dao.save(field);
				isUdfldSaved.add(udflds.getFldno());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedFields(fields=" + fields + "): "
							+ e.getMessage(), e);
		}

		return isUdfldSaved;
	}

	@Override
	public Integer addAttribute(final Attribute attr) {

		Integer isAttrSaved = 0;
		try {

			final AttributeDAO dao = this.getAttributeDao();
			dao.save(attr);
			isAttrSaved++;

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Attribute: GermplasmDataManager.addAttribute(addAttribute="
					+ attr + "): " + e.getMessage(), e);
		}

		return isAttrSaved;
	}

	@Override
	public List<Integer> addAttributes(final List<Attribute> attrs) {

		final List<Integer> isAttrSaved = new ArrayList<Integer>();
		try {

			final AttributeDAO dao = this.getAttributeDao();

			for (final Attribute attr : attrs) {
				final Attribute newAttr = dao.save(attr);
				isAttrSaved.add(newAttr.getAid());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving UserDefinedField: GermplasmDataManager.addAttributes(attrs="
					+ isAttrSaved + "): " + e.getMessage(), e);
		}

		return isAttrSaved;
	}

	@Override
	public List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(final List<String> germplasmNames,
			final GetGermplasmByNameModes mode) {
		final List<String> namesToUse = GermplasmDataManagerUtil.getNamesToUseByMode(germplasmNames, mode);
		return this.getNameDao().getGermplasmNameDetailsByNames(namesToUse, mode);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Country> getAllCountry() {
		return this.getCountryDao().getAllCountry();
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(final String tableName, final String fieldType) {
		return this.getUserDefinedFieldDao().getByFieldTableNameAndType(tableName, fieldType);
	}

	@Override
	public List<Method> getMethodsByGroupIncludesGgroup(final String group) {
		return this.getMethodDao().getByGroupIncludesGgroup(group);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getAllBreedingLocations() {
		return this.getLocationDAO().getAllBreedingLocations();
	}

	@Override
	public String getNextSequenceNumberForCrossName(final String prefix) {
		String nextSequenceStr = "1";
		nextSequenceStr = this.getGermplasmDao().getNextSequenceNumberForCrossName(prefix);
		return nextSequenceStr;
	}

	@Override
	public Map<Integer, String> getPrefferedIdsByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<Integer, String>();

		if (!gids.isEmpty()) {
			final Map<Integer, String> results = this.getNameDao().getPrefferedIdsByGIDs(gids);
			for (final Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
		}
		return toreturn;
	}

	@Override
	public List<Germplasm> getGermplasmByLocationId(final String name, final int locationID) {
		final List<Germplasm> germplasmList = new ArrayList<>();
		germplasmList.addAll(this.getGermplasmDao().getByLocationId(name, locationID));
		return germplasmList;
	}

	@Override
	public Germplasm getGermplasmWithMethodType(final Integer gid) {
		return this.getGermplasmDao().getByGIDWithMethodType(gid);
	}

	@Override
	public List<Germplasm> getGermplasmByGidRange(final int startGIDParam, final int endGIDParam) {
		final List<Germplasm> germplasmList = new ArrayList<>();

		int startGID = startGIDParam;
		int endGID = endGIDParam;
		// assumes the lesser value be the start of the range
		if (endGID < startGID) {
			final int temp = endGID;
			endGID = startGID;
			startGID = temp;
		}

		germplasmList.addAll(this.getGermplasmDao().getByGIDRange(startGID, endGID));
		return germplasmList;
	}

	@Override
	public List<Germplasm> getGermplasms(final List<Integer> gids) {
		final List<Germplasm> germplasmList = new ArrayList<>();
		germplasmList.addAll(this.getGermplasmDao().getByGIDList(gids));
		return germplasmList;
	}

	@Override
	public Map<Integer, String> getPreferredNamesByGids(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();

		if (!gids.isEmpty()) {
			final Map<Integer, String> results = this.getNameDao().getPrefferedNamesByGIDs(gids);
			for (final Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
		}

		return toreturn;
	}

	@Override
	public Map<Integer, String> getLocationNamesByGids(final List<Integer> gids) {
		return this.getLocationDao().getLocationNamesMapByGIDs(gids);
	}

	@Override
	public List<Germplasm> searchForGermplasm(final String q, final Operation o, final boolean includeParents,
			final boolean withInventoryOnly, final boolean includeMGMembers) {
		return this.getGermplasmDao().searchForGermplasms(q, o, includeParents, withInventoryOnly, includeMGMembers);
	}

	
	@Override
	public List<Germplasm> searchForGermplasm(final GermplasmSearchParameter germplasmSearchParameter) {
		return this.getGermplasmDao().searchForGermplasms(germplasmSearchParameter);
	}

	/**
	 * Return the count of germplasm search results based on the following parameters:
	 * 
	 * @param q - keyword
	 * @param o - operation
	 * @param includeParents - include the parents of the search germplasm
	 * @param withInventoryOnly - include germplasm with inventory details only
	 * @param includeMGMembers - include germplasm of the same group of the search germplasm
	 * @return
	 */
	@Override
	public Integer countSearchForGermplasm(final String q, final Operation o, final boolean includeParents,
			final boolean withInventoryOnly, final boolean includeMGMembers) {
		return this.getGermplasmDao().countSearchForGermplasms(q, o, includeParents, withInventoryOnly, includeMGMembers);
	}

	@Override
	public Map<Integer, Integer> getGermplasmDatesByGids(final List<Integer> gids) {
		return this.getGermplasmDao().getGermplasmDatesByGids(gids);
	}

	@Override
	public Map<Integer, Object> getMethodsByGids(final List<Integer> gids) {

		final Map<Integer, Object> results = new HashMap<>();
		Map<Integer, Integer> methodIds = new HashMap<>();

		methodIds = this.getGermplasmDao().getMethodIdsByGids(gids);
		for (final Map.Entry<Integer, Integer> entry : methodIds.entrySet()) {
			final Method method = this.getMethodDao().getById(entry.getValue(), false);
			results.put(entry.getKey(), method);
		}

		return results;
	}

	@Override
	public List<Term> getMethodClasses() {
		final List<Integer> ids = new ArrayList<Integer>();
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
	public Method getMethodByCode(final String code, final String programUUID) {
		Method method = new Method();
		method = this.getMethodDao().getByCode(code, programUUID);
		return method;
	}

	@Override
	public Method getMethodByCode(final String code) {
		Method method = new Method();
		method = this.getMethodDao().getByCode(code);
		return method;
	}

	@Override
	public Method getMethodByName(final String name) {
		List<Method> methods = new ArrayList<Method>();
		methods = this.getMethodDao().getByName(name);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public Method getMethodByName(final String name, final String programUUID) {
		List<Method> methods = new ArrayList<Method>();
		methods = this.getMethodDao().getByName(name, programUUID);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) {
		return this.getGermplasmDao().getProgenitorsByGIDWithPrefName(gid);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final String programUUID) {
		return this.getProgramFavoriteDao().getProgramFavorites(type, programUUID);
	}

	@Override
	public int countProgramFavorites(final FavoriteType type) {
		return this.getProgramFavoriteDao().countProgramFavorites(type);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final int max, final String programUUID) {
		return this.getProgramFavoriteDao().getProgramFavorites(type, max, programUUID);
	}

	@Override
	public void saveProgramFavorites(final List<ProgramFavorite> list) {

		try {
			final ProgramFavoriteDAO dao = this.getProgramFavoriteDao();

			for (final ProgramFavorite favorite : list) {
				dao.save(favorite);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorites(list=" + list + "): "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void saveProgramFavorite(final ProgramFavorite favorite) {

		try {

			final ProgramFavoriteDAO dao = this.getProgramFavoriteDao();
			dao.save(favorite);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorite(favorite=" + favorite + "): "
							+ e.getMessage(), e);
		}

	}

	@Override
	public void deleteProgramFavorites(final List<ProgramFavorite> list) {
		try {
			final ProgramFavoriteDAO dao = this.getProgramFavoriteDao();
			for (final ProgramFavorite favorite : list) {
				dao.makeTransient(favorite);
			}
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while saving ProgramFavorite: GermplasmDataManager.deleteProgramFavorites(list=" + list + "): "
							+ e.getMessage(), e);
		}

	}

	@Override
	public void deleteProgramFavorite(final ProgramFavorite favorite) {

		try {

			final ProgramFavoriteDAO dao = this.getProgramFavoriteDao();
			dao.makeTransient(favorite);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while deleting ProgramFavorite: GermplasmDataManager.deleteProgramFavorite(favorite=" + favorite
							+ "): " + e.getMessage(), e);
		}

	}

	@Override
	public int getMaximumSequence(final boolean isBulk, final String prefix, final String suffix, final int count) {
		return this.getNameBuilder().getMaximumSequence(isBulk, prefix, suffix, count);
	}

	@Override
	public boolean checkIfMatches(final String name) {
		return this.getNameDao().checkIfMatches(name);
	}

	@Override
	public List<Method> getProgramMethods(final String programUUID) {
		return this.getMethodDao().getProgramMethods(programUUID);
	}

	@Override
	public void deleteProgramMethodsByUniqueId(final String programUUID) {

		final MethodDAO methodDao = this.getMethodDao();
		try {

			final List<Method> list = this.getProgramMethods(programUUID);
			for (final Method method : list) {
				methodDao.makeTransient(method);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while deleting methods: GermplasmDataManager.deleteProgramMethodsByUniqueId(uniqueId=" + programUUID
							+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public Map<Integer, GermplasmPedigreeTreeNode> getDirectParentsForStudy(final int studyId) {

		final GermplasmDAO dao = this.getGermplasmDao();
		final Map<Integer, Map<GermplasmNameType, Name>> namesMap = dao.getGermplasmParentNamesForStudy(studyId);
		final List<Germplasm> germs = dao.getGermplasmParentsForStudy(studyId);

		final Map<Integer, GermplasmPedigreeTreeNode> germNodes = new HashMap<>();

		for (final Germplasm germ : germs) {
			final GermplasmPedigreeTreeNode root = new GermplasmPedigreeTreeNode();
			root.setGermplasm(germ);

			final List<GermplasmPedigreeTreeNode> parents = new ArrayList<>();
			Map<GermplasmNameType, Name> names = namesMap.get(germ.getGpid1());

			// TODO: compare again new GermplasmNameTypes in merged database

			final GermplasmPedigreeTreeNode femaleNode = new GermplasmPedigreeTreeNode();
			final Germplasm female = new Germplasm(germ.getGpid1());
			female.setPreferredName(this.getPreferredName(names));
			female.setPreferredAbbreviation(this.getNameByType(names, GermplasmNameType.LINE_NAME).getNval());
			female.setSelectionHistory(this.getNameByType(names, GermplasmNameType.OLD_MUTANT_NAME_1).getNval());
			female.setCrossName(this.getNameByType(names, GermplasmNameType.CROSS_NAME).getNval());
			female.setAccessionName(this.getNameByType(names, GermplasmNameType.GERMPLASM_BANK_ACCESSION_NUMBER).getNval());
			femaleNode.setGermplasm(female);

			names = namesMap.get(germ.getGpid2());
			final GermplasmPedigreeTreeNode maleNode = new GermplasmPedigreeTreeNode();
			final Germplasm male = new Germplasm(germ.getGpid2());
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
	 * @param names The Map containing Names for a germplasm. This is usually provided by getGermplasmParentNamesForStudy() in GermplasmDAO.
	 * @param ntype the name type, i.e. Pedigree, Selection History, Cross Name,etc.
	 * @return an instance of Name representing the searched name, or an empty Name instance if it doesn't exist
	 */
	private Name getNameByType(final Map<GermplasmNameType, Name> names, final GermplasmNameType ntype) {
		Name n = null;
		if (null != names) {
			n = names.get(ntype);
		}

		return null == n ? new Name() : n;
	}

	private Name getPreferredName(final Map<GermplasmNameType, Name> names) {
		for (final Name n : names.values()) {
			if (1 == n.getNstat()) {
				return n;
			}
		}

		return new Name();
	}

	@Override
	public Germplasm getGermplasmByLocalGid(final Integer lgid) {
		return this.getGermplasmDao().getByLGid(lgid);
	}

	@Override
	public Map<String, Integer> getCountByNamePermutations(final List<String> names) {
		return this.getNameDao().getCountByNamePermutations(names);
	}

	@Override
	public UserDefinedField getPlotCodeField() {
		final List<UserDefinedField> udfldAttributes = this.getUserDefinedFieldByFieldTableNameAndType("ATRIBUTS", "PASSPORT");
		// Defaulting to a UDFLD with fldno = 0 - this prevents NPEs and DB constraint violations.
		UserDefinedField plotCodeUdfld = new UserDefinedField(0);
		for (final UserDefinedField userDefinedField : udfldAttributes) {
			if (userDefinedField.getFcode().equals("PLOTCODE")) {
				plotCodeUdfld = userDefinedField;
				break;
			}
		}
		return plotCodeUdfld;
	}

	@Override
	public String getPlotCodeValue(final Integer gid) {
		String plotCode = "Unknown";
		final List<Attribute> attributes = this.getAttributesByGID(gid);
		final UserDefinedField plotCodeAttribute = this.getPlotCodeField();
		for (final Attribute attr : attributes) {
			if (attr.getTypeId().equals(plotCodeAttribute.getFldno())) {
				plotCode = attr.getAval();
				break;
			}
		}
		return plotCode;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.generationcp.middleware.manager.api.GermplasmDataManager#getUserDefinedFieldsByCodesInMap(java.lang.String,
	 *      java.lang.String, java.util.List)
	 */
	@Override
	public UserDefinedField getUserDefinedFieldByTableTypeAndCode(final String table, final String type, final String code) {
		return this.getUserDefinedFieldDao().getByTableTypeAndCode(table, type, code);
	}
	
	@Override
	public List<Method> getDerivativeAndMaintenanceMethods(final List<Integer> ids) {
		return this.getMethodDao().getDerivativeAndMaintenanceMethods(ids);
	}

	@Override
	public long countMatchGermplasmInList(final Set<Integer> gids) {
		return this.getGermplasmDao().countMatchGermplasmInList(gids);
	}
}
