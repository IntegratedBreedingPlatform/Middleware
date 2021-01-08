/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager;

import com.google.common.collect.ImmutableSet;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmDTO;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
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
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the GermplasmDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 * @author Kevin Manansala, Lord Hendrix Barboza
 */
@Transactional
public class GermplasmDataManagerImpl extends DataManager implements GermplasmDataManager {

	private DaoFactory daoFactory;

	private static final String GID_SEPARATOR_FOR_STORED_PROCEDURE_CALL = ",";

	public GermplasmDataManagerImpl() {
	}

	public GermplasmDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Germplasm> getAllGermplasm(final int start, final int numOfRows) {
		return this.daoFactory.getGermplasmDao().getAll(start, numOfRows);
	}

	@Override
	public List<Germplasm> getGermplasmByName(final String name, final int start, final int numOfRows, final Operation op) {
		return this.daoFactory.getGermplasmDao().getByNamePermutations(name, op, start, numOfRows);
	}

	@Override
	public long countGermplasmByName(final String name, final Operation operation) {
		return this.daoFactory.getGermplasmDao().countByNamePermutations(name, operation);
	}

	@Override
	public List<Germplasm> getGermplasmByLocationName(final String name, final int start, final int numOfRows, final Operation op) {
		List<Germplasm> germplasms = new ArrayList<>();
		final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
		if (op == Operation.EQUAL) {
			germplasms = dao.getByLocationNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			germplasms = dao.getByLocationNameUsingLike(name, start, numOfRows);
		}
		return germplasms;
	}

	@Override
	@Deprecated
	public long countGermplasmByLocationName(final String name, final Operation op) {
		long count = 0;
		final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
		if (op == Operation.EQUAL) {
			count = dao.countByLocationNameUsingEqual(name);
		} else if (op == Operation.LIKE) {
			count = dao.countByLocationNameUsingLike(name);
		}
		return count;
	}

	@Override
	@Deprecated
	public List<Germplasm> getGermplasmByMethodName(final String name, final int start, final int numOfRows, final Operation op) {
		List<Germplasm> germplasms = new ArrayList<>();
		final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
		if (op == Operation.EQUAL) {
			germplasms = dao.getByMethodNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			germplasms = dao.getByMethodNameUsingLike(name, start, numOfRows);
		}
		return germplasms;
	}

	@Override
	@Deprecated
	public long countGermplasmByMethodName(final String name, final Operation op) {
		long count = 0;
		final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
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
		Germplasm germplasm;
		do {
			germplasm = this.daoFactory.getGermplasmDao().getById(updatedGid, false);
			if (germplasm != null) {
				updatedGid = germplasm.getGrplce();
			}
		} while (germplasm != null && !Integer.valueOf(0).equals(updatedGid) && !germplasm.getGid().equals(updatedGid));
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
	public List<Germplasm> getSortedGermplasmWithPrefName(final List<Integer> gids) {
		final List<Germplasm> result = new ArrayList<>();
		for (final Iterator<Integer> iterator = gids.iterator(); iterator.hasNext(); ) {
			final Integer gid = iterator.next();
			result.add(this.getGermplasmWithPrefName(gid));
		}
		final Comparator<Germplasm> comparator = new Comparator<Germplasm>() {

			@Override
			public int compare(final Germplasm left, final Germplasm right) {
				return left.getPreferredName().getNval().compareTo(right.getPreferredName().getNval());
			}
		};

		Collections.sort(result, comparator);
		return result;
	}

	@Override
	public Germplasm getGermplasmWithPrefAbbrev(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getByGIDWithPrefAbbrev(gid);
	}

	@Override
	public Name getGermplasmNameByID(final Integer id) {
		return this.daoFactory.getNameDao().getById(id, false);
	}

	@Override
	public List<Name> getNamesByGID(final Integer gid, final Integer status, final GermplasmNameType type) {
		return this.daoFactory.getNameDao().getByGIDWithFilters(gid, status, type);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.generationcp.middleware.manager.api.GermplasmDataManager#getByGIDWithListTypeFilters(java.lang.Integer, java.lang.Integer, *
	 * java.util.List)
	 */
	@Override
	public List<Name> getByGIDWithListTypeFilters(final Integer gid, final Integer status, final List<Integer> type) {
		return this.daoFactory.getNameDao().getByGIDWithListTypeFilters(gid, status, type);
	}

	@Override
	public Name getPreferredNameByGID(final Integer gid) {
		final List<Name> names = this.daoFactory.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public String getPreferredNameValueByGID(final Integer gid) {
		final List<Name> names = this.daoFactory.getNameDao().getByGIDWithFilters(gid, 1, null);
		if (!names.isEmpty()) {
			return names.get(0).getNval();
		}
		return null;
	}

	@Override
	public Name getPreferredAbbrevByGID(final Integer gid) {
		final List<Name> names = this.daoFactory.getNameDao().getByGIDWithFilters(gid, 2, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public Name getPreferredIdByGID(final Integer gid) {
		final List<Name> names = this.daoFactory.getNameDao().getByGIDWithFilters(gid, 8, null);
		if (!names.isEmpty()) {
			return names.get(0);
		}
		return null;
	}

	@Override
	public List<Name> getPreferredIdsByListId(final Integer listId) {
		return this.daoFactory.getNameDao().getPreferredIdsByListId(listId);
	}

	@Override
	public Name getNameByGIDAndNval(final Integer gid, final String nval, final GetGermplasmByNameModes mode) {
		return this.daoFactory.getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
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

			final NameDAO dao = this.daoFactory.getNameDao();

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
				throw new MiddlewareQueryException(
					"Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue=" + newPrefValue
						+ ", nameOrAbbrev=" + nameOrAbbrev + "): The specified Germplasm Name does not exist.",
					new Throwable());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in GermplasmpDataManager.updateGermplasmPrefNameAbbrev(gid=" + gid + ", newPrefValue="
				+ newPrefValue + ", nameOrAbbrev=" + nameOrAbbrev + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public Integer addGermplasmName(final Name name) {
		final List<Name> names = new ArrayList<>();
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
		final List<Name> names = new ArrayList<>();
		names.add(name);
		final List<Integer> ids = this.addOrUpdateGermplasmName(names, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmName(final List<Name> names) {
		return this.addOrUpdateGermplasmName(names, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmName(final List<Name> names, final Operation operation) {
		final List<Integer> idNamesSaved = new ArrayList<>();
		try {

			final NameDAO dao = this.daoFactory.getNameDao();

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
		return this.daoFactory.getAttributeDAO().getByGID(gid);
	}

	@Override
	public List<UserDefinedField> getAttributeTypesByGIDList(final List<Integer> gidList) {
		return this.daoFactory.getUserDefinedFieldDAO().getAttributeTypesByGIDList(gidList);
	}

	@Override
	public Map<Integer, String> getAttributeValuesByTypeAndGIDList(final Integer attributeType, final List<Integer> gidList) {
		final Map<Integer, String> returnMap = new HashMap<>();
		// initialize map with GIDs
		for (final Integer gid : gidList) {
			returnMap.put(gid, "-");
		}

		// retrieve attribute values
		final List<Attribute> attributeList = this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(attributeType, gidList);
		for (final Attribute attribute : attributeList) {
			returnMap.put(attribute.getGermplasmId(), attribute.getAval());
		}

		return returnMap;
	}

	@Override
	public Map<Integer, Map<Integer, String>> getAttributeValuesGIDList(final List<Integer> gidList) {
		final Map<Integer, Map<Integer, String>> attributeMap = new HashMap<>();

		// retrieve attribute values
		final List<Attribute> attributeList = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(gidList);
		for (final Attribute attribute : attributeList) {
			Map<Integer, String> attrByType = attributeMap.get(attribute.getGermplasmId());
			if (attrByType == null) {
				attrByType = new HashMap<>();
			}
			attrByType.put(attribute.getTypeId(), attribute.getAval());
			attributeMap.put(attribute.getGermplasmId(), attrByType);
		}

		return attributeMap;
	}

	@Override
	public List<UserDefinedField> getNameTypesByGIDList(final List<Integer> gidList) {
		return this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gidList);
	}

	@Override
	public Map<Integer, String> getNamesByTypeAndGIDList(final Integer nameType, final List<Integer> gidList) {
		final Map<Integer, String> returnMap = new HashMap<>();
		// initialize map with GIDs
		for (final Integer gid : gidList) {
			returnMap.put(gid, "-");
		}

		final List<Name> nameList = this.daoFactory.getNameDao().getNamesByTypeAndGIDList(nameType, gidList);
		for (final Name name : nameList) {
			returnMap.put(name.getGermplasmId(), name.getNval());
		}

		return returnMap;
	}

	@Override
	public Method getMethodByID(final Integer id) {
		return this.daoFactory.getMethodDAO().getById(id, false);
	}

	@Override
	public List<Method> getMethodsByIDs(final List<Integer> ids) {
		final List<Method> results = new ArrayList<>();

		if (!ids.isEmpty()) {
			results.addAll(this.daoFactory.getMethodDAO().getMethodsByIds(ids));
		}

		return results;
	}

	@Override
	public List<Method> getNonGenerativeMethodsByID(final List<Integer> ids) {
		return this.daoFactory.getMethodDAO().getMethodsNotGenerativeById(ids);
	}

	@Override
	public List<Method> getAllMethods() {
		return this.daoFactory.getMethodDAO().getAllMethod();
	}

	@Override
	public List<Method> getFavoriteMethodsByMethodType(final String methodType, final String programUUID) {
		return this.daoFactory.getMethodDAO().getFavoriteMethodsByMethodType(methodType, programUUID);
	}

	@Override
	public List<Method> getAllMethodsOrderByMname() {
		return this.daoFactory.getMethodDAO().getAllMethodOrderByMname();
	}

	@Override
	public boolean isMethodNamingConfigurationValid(final Method breedingMethod) {
		if (breedingMethod == null) {
			return false;
		}
		return !(breedingMethod.getSuffix() == null && breedingMethod.getSeparator() == null && breedingMethod.getSnametype() == null
			&& breedingMethod.getPrefix() == null && breedingMethod.getCount() == null);
	}

	@Override
	public List<Method> getAllMethodsNotGenerative() {
		return this.daoFactory.getMethodDAO().getAllMethodsNotGenerative();
	}

	@Override
	public long countAllMethods() {
		return this.countAll(this.daoFactory.getMethodDAO());
	}

	@Override
	public List<Method> getMethodsByUniqueID(final String programUUID) {
		return this.daoFactory.getMethodDAO().getByUniqueID(programUUID);
	}

	@Override
	public long countMethodsByUniqueID(final String programUUID) {
		return this.daoFactory.getMethodDAO().countByUniqueID(programUUID);
	}

	@Override
	public List<Method> getMethodsByType(final String type) {
		return this.daoFactory.getMethodDAO().getByType(type);
	}

	@Override
	public List<Method> getMethodsByType(final String type, final String programUUID) {
		return this.daoFactory.getMethodDAO().getByType(type, programUUID);
	}

	@Override
	public List<Method> getMethodsByType(final String type, final int start, final int numOfRows) {
		return this.daoFactory.getMethodDAO().getByType(type, start, numOfRows);
	}

	@Override
	public long countMethodsByType(final String type) {
		return this.daoFactory.getMethodDAO().countByType(type);
	}

	@Override
	public long countMethodsByType(final String type, final String programUUID) {
		return this.daoFactory.getMethodDAO().countByType(type, programUUID);
	}

	@Override
	public List<Method> getMethodsByGroup(final String group) {
		return this.daoFactory.getMethodDAO().getByGroup(group);
	}

	@Override
	public List<Method> getMethodsByGroup(final String group, final int start, final int numOfRows) {
		return this.daoFactory.getMethodDAO().getByGroup(group, start, numOfRows);
	}

	@Override
	public List<Method> getMethodsByGroupAndType(final String group, final String type) {
		return this.daoFactory.getMethodDAO().getByGroupAndType(group, type);
	}

	@Override
	public List<Method> getMethodsByGroupAndTypeAndName(final String group, final String type, final String name) {
		return this.daoFactory.getMethodDAO().getByGroupAndTypeAndName(group, type, name);
	}

	@Override
	public long countMethodsByGroup(final String group) {
		return this.daoFactory.getMethodDAO().countByGroup(group);
	}

	@Override
	public Integer addMethod(final Method method) {

		final Integer methodId;
		try {

			final MethodDAO dao = this.daoFactory.getMethodDAO();

			final Method recordSaved = dao.saveOrUpdate(method);
			methodId = recordSaved.getMid();

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e);
		}
		return methodId;
	}

	@Override
	public Method editMethod(final Method method) {

		final Method recordSaved;

		try {

			if (method.getMid() == null) {
				throw new MiddlewareQueryException("method has no Id or is not a local method");
			}

			final MethodDAO dao = this.daoFactory.getMethodDAO();

			recordSaved = dao.merge(method);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Method: GermplasmDataManager.addMethod(method=" + method + "): " + e.getMessage(), e);
		}

		return recordSaved;
	}

	@Override
	public List<Integer> addMethod(final List<Method> methods) {

		final List<Integer> idMethodsSaved = new ArrayList<>();
		try {

			final MethodDAO dao = this.daoFactory.getMethodDAO();

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

			this.daoFactory.getMethodDAO().makeTransient(method);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting Method: GermplasmDataMananger.deleteMethod(method=" + method + "): " + e.getMessage(),
				e);
		}
	}

	@Override
	public UserDefinedField getUserDefinedFieldByID(final Integer id) {
		return this.daoFactory.getUserDefinedFieldDAO().getById(id, false);
	}

	@Override
	public UserDefinedField getUserDefinedFieldByLocalFieldNo(final Integer lfldno) {
		return this.daoFactory.getUserDefinedFieldDAO().getByLocalFieldNo(lfldno);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Country getCountryById(final Integer id) {
		return this.daoFactory.getCountryDao().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Location getLocationByID(final Integer id) {
		return this.daoFactory.getLocationDAO().getById(id, false);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getLocationsByIDs(final List<Integer> ids) {
		return this.daoFactory.getLocationDAO().getLocationByIds(ids);
	}

	@Override
	public Bibref getBibliographicReferenceByID(final Integer id) {
		return this.daoFactory.getBibrefDao().getById(id, false);
	}

	@Override
	public Integer addBibliographicReference(final Bibref bibref) {

		final Integer idBibrefSaved;
		try {

			final Bibref recordSaved = this.daoFactory.getBibrefDao().saveOrUpdate(bibref);
			idBibrefSaved = recordSaved.getRefid();

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Bibliographic Reference: GermplasmDataManager.addBibliographicReference(bibref="
					+ bibref + "): " + e.getMessage(),
				e);
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
		return this.addOrUpdateAttributes(attributes);
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
		return this.addOrUpdateAttributes(attributes);
	}

	private List<Integer> addOrUpdateAttributes(final List<Attribute> attributes) {

		final List<Integer> idAttributesSaved = new ArrayList<>();
		try {

			final AttributeDAO dao = this.daoFactory.getAttributeDAO();

			for (final Attribute attribute : attributes) {
				final Attribute recordSaved = dao.saveOrUpdate(attribute);
				idAttributesSaved.add(recordSaved.getAid());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Attribute: GermplasmDataManager.addOrUpdateAttributes(attributes=" + attributes + "): "
					+ e.getMessage(),
				e);
		}

		return idAttributesSaved;
	}

	@Override
	public Attribute getAttributeById(final Integer id) {
		return this.daoFactory.getAttributeDAO().getById(id, false);
	}

	@Override
	public List<Integer> addOrUpdateGermplasm(final List<Germplasm> germplasms, final Operation operation) {
		final List<Integer> idGermplasmsSaved = new ArrayList<>();
		try {
			final GermplasmDAO dao = this.daoFactory.getGermplasmDao();

			for (final Germplasm germplasm : germplasms) {
				final Germplasm recordSaved = dao.saveOrUpdate(germplasm);
				idGermplasmsSaved.add(recordSaved.getGid());
				recordSaved.setLgid(recordSaved.getGid());
			}
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm: GermplasmDataManager.addOrUpdateGermplasms(germplasms=" + germplasms
					+ ", operation=" + operation + "): " + e.getMessage(),
				e);
		}

		return idGermplasmsSaved;
	}

	@Override
	public long countGermplasmByStudy(final Integer studyDbId) {
		return this.daoFactory.getGermplasmDao().countGermplasmByStudy(studyDbId);
	}

	@Override
	public List<GermplasmDTO> getGermplasmByStudy(final Integer studyDbId, final Integer pageNumber, final Integer pageSize) {
		return this.daoFactory.getGermplasmDao().getGermplasmByStudy(studyDbId, pageNumber, pageSize);
	}

	@Override
	public Integer addGermplasm(final Germplasm germplasm, final Name preferredName, final CropType cropType) {
		final List<Triple<Germplasm, Name, List<Progenitor>>> tripleList = new ArrayList<>();
		final List<Progenitor> progenitors = new ArrayList<>();
		final Triple<Germplasm, Name, List<Progenitor>> triple = new ImmutableTriple<>(germplasm, preferredName, progenitors);
		tripleList.add(triple);
		final List<Integer> ids = this.addGermplasm(tripleList, cropType);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasm(final Map<Germplasm, Name> germplasmNameMap, final CropType cropType) {
		final List<Triple<Germplasm, Name, List<Progenitor>>> tripleList = new ArrayList<>();
		final List<Progenitor> progenitors = new ArrayList<>();
		for (final Map.Entry<Germplasm, Name> entry : germplasmNameMap.entrySet()) {
			final Triple<Germplasm, Name, List<Progenitor>> triple = new ImmutableTriple<>(entry.getKey(), entry.getValue(), progenitors);
			tripleList.add(triple);
		}
		return this.addGermplasm(tripleList, cropType);
	}

	@Override
	public List<Integer> addGermplasm(final List<Triple<Germplasm, Name, List<Progenitor>>> germplasmTriples, final CropType cropType) {
		final List<Integer> listOfGermplasm = new ArrayList<>();
		try {
			final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
			final NameDAO nameDao = this.daoFactory.getNameDao();
			final ProgenitorDAO progenitorDao = this.daoFactory.getProgenitorDao();

			for (final Triple<Germplasm, Name, List<Progenitor>> triple : germplasmTriples) {
				final Germplasm germplasm = triple.getLeft();
				final Name name = triple.getMiddle();
				final List<Progenitor> progenitors = triple.getRight();

				// If germplasm has multiple male parents, automatically set the value of gnpgs to (<count of progenitors> + 2). We need to add 2
				// to take into account the gpid1 and gpid2 parents.
				// Setting gnpgs to >2 will indicate that there are other parents in progenitors table other than gpid1 and gpid2.
				if (!progenitors.isEmpty()) {
					germplasm.setGnpgs(progenitors.size() + 2);
				}

				if (name.getNstat() == null) {
					name.setNstat(1);
				}

				GermplasmGuidGenerator.generateGermplasmGuids(cropType, Arrays.asList(germplasm));

				final Germplasm germplasmSaved = dao.save(germplasm);
				listOfGermplasm.add(germplasmSaved.getGid());
				name.setGermplasmId(germplasmSaved.getGid());
				nameDao.save(name);

				for (final Progenitor progenitor : progenitors) {
					progenitor.setGermplasm(germplasmSaved);
					progenitorDao.save(progenitor);
				}
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm: GermplasmDataManager.addGermplasm(): " + e.getMessage(), e);
		}
		return listOfGermplasm;
	}

	@Override
	public Integer addUserDefinedField(final UserDefinedField field) {

		try {

			this.daoFactory.getUserDefinedFieldDAO().save(field);

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

			for (final UserDefinedField field : fields) {

				final UserDefinedField udflds = this.daoFactory.getUserDefinedFieldDAO().save(field);
				isUdfldSaved.add(udflds.getFldno());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving UserDefinedField: GermplasmDataManager.addUserDefinedFields(fields=" + fields + "): "
					+ e.getMessage(),
				e);
		}

		return isUdfldSaved;
	}

	@Override
	public Integer addAttribute(final Attribute attr) {

		Integer isAttrSaved = 0;
		try {

			final AttributeDAO dao = this.daoFactory.getAttributeDAO();
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

		final List<Integer> isAttrSaved = new ArrayList<>();
		try {

			final AttributeDAO dao = this.daoFactory.getAttributeDAO();

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
		return this.daoFactory.getNameDao().getGermplasmNameDetailsByNames(namesToUse, mode);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Country> getAllCountry() {
		return this.daoFactory.getCountryDao().getAllCountry();
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(final String tableName, final String fieldType) {
		return this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(tableName, ImmutableSet.of(fieldType));
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndFTypeAndFName(final String tableName, final String fieldType,
		final String fieldName) {
		return this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndFTypeAndFName(tableName, fieldType, fieldName);
	}

	@Override
	public List<Method> getMethodsByGroupIncludesGgroup(final String group) {
		return this.daoFactory.getMethodDAO().getByGroupIncludesGgroup(group);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<Location> getAllBreedingLocations() {
		return this.daoFactory.getLocationDAO().getAllBreedingLocations();
	}

	@Override
	public String getNextSequenceNumberAsString(final String prefix) {
		final String nextSequenceStr;
		nextSequenceStr = this.daoFactory.getGermplasmDao().getNextSequenceNumber(prefix);
		return nextSequenceStr;
	}

	@Override
	public Map<Integer, String> getPreferredIdsByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();

		if (!gids.isEmpty()) {
			final Map<Integer, String> results = this.daoFactory.getNameDao().getPreferredIdsByGIDs(gids);
			for (final Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
		}
		return toreturn;
	}

	@Override
	public List<Germplasm> getGermplasmByLocationId(final String name, final int locationID) {
		return this.daoFactory.getGermplasmDao().getByLocationId(name, locationID);
	}

	@Override
	public List<Germplasm> getGermplasmByGidRange(final int startGIDParam, final int endGIDParam) {
		int startGID = startGIDParam;
		int endGID = endGIDParam;
		// assumes the lesser value be the start of the range
		if (endGID < startGID) {
			final int temp = endGID;
			endGID = startGID;
			startGID = temp;
		}

		return this.daoFactory.getGermplasmDao().getByGIDRange(startGID, endGID);
	}

	@Override
	public List<Germplasm> getGermplasms(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getByGIDList(gids);
	}

	@Override
	public List<Germplasm> getGermplasmWithoutGroup(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getGermplasmWithoutGroup(gids);
	}

	@Override
	public Map<Integer, String> getPreferredNamesByGids(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();

		if (!gids.isEmpty()) {
			final Map<Integer, String> results = this.daoFactory.getNameDao().getPreferredNamesByGIDs(gids);
			for (final Integer gid : results.keySet()) {
				toreturn.put(gid, results.get(gid));
			}
			if (gids.contains(0)) {
				toreturn.put(0, Name.UNKNOWN);
			}
		}

		return toreturn;
	}

	@Override
	public Map<Integer, Integer> getPreferredNameIdsByGIDs(final List<Integer> gids) {
		return this.daoFactory.getNameDao().getPreferredNameIdsByGIDs(gids);
	}

	@Override
	public Map<Integer, String> getLocationNamesByGids(final List<Integer> gids) {
		return this.daoFactory.getLocationDAO().getLocationNamesMapByGIDs(gids);
	}

	@Override
	public List<Germplasm> searchForGermplasm(final GermplasmSearchParameter germplasmSearchParameter) {
		final List<Germplasm> germplasms = this.getGermplasmSearchDao().searchForGermplasms(germplasmSearchParameter);
		this.getListInventoryBuilder().setAvailableBalanceScaleForGermplasm(germplasms);
		return germplasms;
	}

	@Override
	public Set<Integer> retrieveGidsOfSearchGermplasmResult(final GermplasmSearchParameter germplasmSearchParameter) {
		return this.getGermplasmSearchDao().retrieveGIDSearchResults(germplasmSearchParameter);
	}

	/**
	 * Return the count of germplasm search results based on the following parameters:
	 *
	 * @param q                 - keyword
	 * @param o                 - operation
	 * @param includeParents    - include the parents of the search germplasm
	 * @param withInventoryOnly - include germplasm with inventory details only
	 * @param includeMGMembers  - include germplasm of the same group of the search germplasm
	 * @return
	 */
	@Override
	public Integer countSearchForGermplasm(final GermplasmSearchParameter germplasmSearchParameter) {
		return this.getGermplasmSearchDao().countSearchForGermplasms(germplasmSearchParameter);
	}

	@Override
	public Map<Integer, Integer> getGermplasmDatesByGids(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getGermplasmDatesByGids(gids);
	}

	@Override
	public Map<Integer, Object> getMethodsByGids(final List<Integer> gids) {

		final Map<Integer, Object> results = new HashMap<>();
		final Map<Integer, Integer> methodIds;

		methodIds = this.daoFactory.getGermplasmDao().getMethodIdsByGids(gids);
		for (final Map.Entry<Integer, Integer> entry : methodIds.entrySet()) {
			final Method method = this.daoFactory.getMethodDAO().getById(entry.getValue(), false);
			results.put(entry.getKey(), method);
		}

		return results;
	}

	/**
	 * See {@link org.generationcp.middleware.pojos.MethodClass}
	 */
	@Deprecated
	@Override
	public List<Term> getMethodClasses() {
		final List<Integer> ids = new ArrayList<>();
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
		return this.daoFactory.getMethodDAO().getByCode(code, programUUID);
	}

	@Override
	public Method getMethodByCode(final String code) {
		return this.daoFactory.getMethodDAO().getByCode(Collections.singletonList(code)).get(0);
	}

	@Override
	public Method getMethodByName(final String name) {
		final List<Method> methods;
		methods = this.daoFactory.getMethodDAO().getByName(name);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public Method getMethodByName(final String name, final String programUUID) {
		final List<Method> methods;
		methods = this.daoFactory.getMethodDAO().getByName(name, programUUID);
		if (methods != null && !methods.isEmpty()) {
			return methods.get(0);
		} else {
			return new Method();
		}
	}

	@Override
	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(gid);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final String programUUID) {
		return this.daoFactory.getProgramFavoriteDao().getProgramFavorites(type, programUUID);
	}

	@Override
	public int countProgramFavorites(final FavoriteType type) {
		return this.daoFactory.getProgramFavoriteDao().countProgramFavorites(type);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final int max, final String programUUID) {
		return this.daoFactory.getProgramFavoriteDao().getProgramFavorites(type, max, programUUID);
	}

	@Override
	public void saveProgramFavorites(final List<ProgramFavorite> list) {

		try {
			final ProgramFavoriteDAO dao = this.daoFactory.getProgramFavoriteDao();

			for (final ProgramFavorite favorite : list) {
				dao.save(favorite);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorites(list=" + list + "): "
					+ e.getMessage(),
				e);
		}
	}

	@Override
	public void saveProgramFavorite(final ProgramFavorite favorite) {

		try {

			final ProgramFavoriteDAO dao = this.daoFactory.getProgramFavoriteDao();
			dao.save(favorite);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving ProgramFavorite: GermplasmDataManager.saveProgramFavorite(favorite=" + favorite + "): "
					+ e.getMessage(),
				e);
		}

	}

	@Override
	public void deleteProgramFavorites(final List<ProgramFavorite> list) {
		try {
			final ProgramFavoriteDAO dao = this.daoFactory.getProgramFavoriteDao();
			for (final ProgramFavorite favorite : list) {
				dao.makeTransient(favorite);
			}
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while saving ProgramFavorite: GermplasmDataManager.deleteProgramFavorites(list=" + list + "): "
					+ e.getMessage(),
				e);
		}

	}

	@Override
	public void deleteProgramFavorite(final ProgramFavorite favorite) {

		try {

			final ProgramFavoriteDAO dao = this.daoFactory.getProgramFavoriteDao();
			dao.makeTransient(favorite);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting ProgramFavorite: GermplasmDataManager.deleteProgramFavorite(favorite=" + favorite
					+ "): " + e.getMessage(),
				e);
		}

	}

	@Override
	public boolean checkIfMatches(final String name) {
		return this.daoFactory.getNameDao().checkIfMatches(name);
	}

	@Override
	public List<Method> getProgramMethods(final String programUUID) {
		return this.daoFactory.getMethodDAO().getProgramMethods(programUUID);
	}

	@Override
	public void deleteProgramMethodsByUniqueId(final String programUUID) {

		final MethodDAO methodDao = this.daoFactory.getMethodDAO();
		try {

			final List<Method> list = this.getProgramMethods(programUUID);
			for (final Method method : list) {
				methodDao.makeTransient(method);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting methods: GermplasmDataManager.deleteProgramMethodsByUniqueId(uniqueId=" + programUUID
					+ "): " + e.getMessage(),
				e);
		}
	}

	@Override
	public Map<Integer, GermplasmPedigreeTreeNode> getDirectParentsForStudy(final int studyId) {

		final GermplasmDAO dao = this.daoFactory.getGermplasmDao();
		final Map<Integer, Map<GermplasmNameType, Name>> namesMap = dao.getGermplasmParentNamesForStudy(studyId);
		final List<Germplasm> germs = dao.getGermplasmParentsForStudy(studyId);

		final Map<Integer, GermplasmPedigreeTreeNode> germNodes = new HashMap<>();

		for (final Germplasm germ : germs) {
			final GermplasmPedigreeTreeNode root = new GermplasmPedigreeTreeNode();
			root.setGermplasm(germ);

			Map<GermplasmNameType, Name> names = namesMap.get(germ.getGpid1());

			// TODO: compare again new GermplasmNameTypes in merged database

			final GermplasmPedigreeTreeNode femaleNode = this.createGermplasmPedigreeTreeNode(germ.getGpid1(), names);

			names = namesMap.get(germ.getGpid2());
			final GermplasmPedigreeTreeNode maleNode = this.createGermplasmPedigreeTreeNode(germ.getGpid2(), names);

			root.setFemaleParent(femaleNode);
			root.setMaleParent(maleNode);

			// providing legacy support for use of linked nodes to represent parent nodes
			if (femaleNode != null) {
				root.getLinkedNodes().add(femaleNode);
			}

			if (maleNode != null) {
				root.getLinkedNodes().add(maleNode);
			}

			germNodes.put(germ.getGid(), root);
		}

		return germNodes;
	}

	protected GermplasmPedigreeTreeNode createGermplasmPedigreeTreeNode(final Integer gid, final Map<GermplasmNameType, Name> names) {
		// this is encountered in cases where parental information is not available (gpid1 or gpid2 does not point to an actual germplasm)
		if (gid == null || gid == 0) {
			return null;
		}

		final GermplasmPedigreeTreeNode treeNode = new GermplasmPedigreeTreeNode();
		final Germplasm female = new Germplasm(gid);
		female.setPreferredName(this.getPreferredName(names));
		female.setPreferredAbbreviation(this.getNameByType(names, GermplasmNameType.LINE_NAME).getNval());
		female.setSelectionHistory(this.getNameByType(names, GermplasmNameType.OLD_MUTANT_NAME_1).getNval());
		female.setCrossName(this.getNameByType(names, GermplasmNameType.CROSS_NAME).getNval());
		female.setAccessionName(this.getNameByType(names, GermplasmNameType.GERMPLASM_BANK_ACCESSION_NUMBER).getNval());
		treeNode.setGermplasm(female);

		return treeNode;
	}

	@Override
	public PedigreeDTO getPedigree(final Integer germplasmDbId, final String notation, final Boolean includeSiblings) {
		return this.daoFactory.getGermplasmDao().getPedigree(germplasmDbId, notation, includeSiblings);
	}

	@Override
	public ProgenyDTO getProgeny(final Integer germplasmDbId) {
		return this.daoFactory.getGermplasmDao().getProgeny(germplasmDbId);
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
		return this.daoFactory.getGermplasmDao().getByLGid(lgid);
	}

	@Override
	public UserDefinedField getPlotCodeField() {
		final List<UserDefinedField> udfldAttributes = this.getUserDefinedFieldByFieldTableNameAndType("ATRIBUTS", "PASSPORT");
		// Defaulting to a UDFLD with fldno = 0 - this prevents NPEs and DB constraint violations.
		UserDefinedField plotCodeUdfld = new UserDefinedField(0);
		for (final UserDefinedField userDefinedField : udfldAttributes) {
			if ("PLOTCODE".equals(userDefinedField.getFcode())) {
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
	 */
	@Override
	public UserDefinedField getUserDefinedFieldByTableTypeAndCode(final String table, final String type, final String code) {
		return this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(table, type, code);
	}

	@Override
	public List<Method> getDerivativeAndMaintenanceMethods(final List<Integer> ids) {
		return this.daoFactory.getMethodDAO().getDerivativeAndMaintenanceMethods(ids);
	}

	@Override
	public List<String> getMethodCodeByMethodIds(final Set<Integer> methodIds) {
		return this.daoFactory.getMethodDAO().getMethodCodeByMethodIds(methodIds);
	}

	@Override
	public Map<Integer, String> getGroupSourcePreferredNamesByGids(final List<Integer> gids) {
		return this.daoFactory.getNameDao().getSourcePreferredNamesByGids(gids);
	}

	@Override
	public List<String> getNamesByGidsAndPrefixes(final List<Integer> gids, final List<String> prefixes) {
		return this.daoFactory.getNameDao().getNamesByGidsAndPrefixes(gids, prefixes);
	}

	@Override
	public Map<Integer, String> getImmediateSourcePreferredNamesByGids(final List<Integer> gids) {
		return this.daoFactory.getNameDao().getImmediatePreferredNamesByGids(gids);
	}

	@Override
	public long countMatchGermplasmInList(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().countMatchGermplasmInList(gids);
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGidsAndNTypeIdsInMap(final List<Integer> gids, final List<Integer> ntypeIds) {
		return this.daoFactory.getNameDao().getNamesByGidsAndNTypeIdsInMap(gids, ntypeIds);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.generationcp.middleware.manager.api.GermplasmDataManager#getGermplasmWithAllNamesAndAncestry(java.util.Set, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Germplasm> getGermplasmWithAllNamesAndAncestry(final Set<Integer> gids, final int numberOfLevelsToTraverse) {
		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.manager.GermplasmDataManagerImpl"
			+ ".getGermplasmWithAllNamesAndAncestry(Set<Integer> - SetSize(" + gids.size() + ") , int)");

		try {
			final StringBuilder commaSeparatedListOfGids = this.getGidsAsCommaSeparatedList(gids);

			final SQLQuery storedProcedure =
				this.getActiveSession().createSQLQuery("CALL getGermplasmWithNamesAndAncestry(:gids, :numberOfLevelsToTraverse) ");
			storedProcedure.setParameter("gids", commaSeparatedListOfGids.toString());
			storedProcedure.setParameter("numberOfLevelsToTraverse", numberOfLevelsToTraverse);

			storedProcedure.addEntity("g", Germplasm.class);
			storedProcedure.addJoin("n", "g.names");
			// Be very careful changing anything here.
			// The entity has been added again because the distinct root entity works on the
			// Last added entity
			storedProcedure.addEntity("g", Germplasm.class);
			storedProcedure.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			return storedProcedure.list();
		} finally {
			monitor.stop();
		}

	}

	private StringBuilder getGidsAsCommaSeparatedList(final Set<Integer> gids) {
		final StringBuilder commaSeparatedListOfGids = new StringBuilder();

		for (final Integer input : gids) {
			if (input != null) {
				if (commaSeparatedListOfGids.length() == 0) {
					commaSeparatedListOfGids.append(input.toString());
				} else {
					commaSeparatedListOfGids.append(GermplasmDataManagerImpl.GID_SEPARATOR_FOR_STORED_PROCEDURE_CALL);

					commaSeparatedListOfGids.append(input.toString());
				}
			}
		}
		return commaSeparatedListOfGids;
	}

	@Override
	public Map<Integer, String[]> getParentsInfoByGIDList(final List<Integer> gidList) {
		return this.daoFactory.getGermplasmDao().getParentsInfoByGIDList(gidList);
	}

	@Override
	public List<UserDefinedField> getAllAttributesTypes() {
		return this.daoFactory.getAttributeDAO().getAttributeTypes();
	}

	/**
	 * @return List of all no bulking methods
	 */
	@Override
	public List<Method> getAllNoBulkingMethods() {
		return this.daoFactory.getMethodDAO().getAllNoBulkingMethods();
	}

	@Override
	public List<Method> getNoBulkingMethodsByIdList(final List<Integer> ids) {
		return this.daoFactory.getMethodDAO().getNoBulkingMethodsByIdList(ids);
	}

	@Override
	public List<Method> getAllMethodsNotBulkingNotGenerative() {
		return this.daoFactory.getMethodDAO().getAllMethodsNotBulkingNotGenerative();
	}

	@Override
	public List<Method> getNoBulkingMethodsByType(final String type, final String programUUID) {
		return this.daoFactory.getMethodDAO().getNoBulkingMethodsByType(type, programUUID);
	}

	@Override
	public String getAttributeValue(final Integer gid, final String attributeName) {
		final Attribute attribute = this.daoFactory.getAttributeDAO().getAttribute(gid, attributeName);
		if (attribute == null) {
			return "";
		} else {
			return attribute.getAval();
		}
	}

	@Override
	public void save(final Germplasm germplasm) {
		this.daoFactory.getGermplasmDao().save(germplasm);
	}

	@Override
	public NamingConfiguration getNamingConfigurationByName(final String name) {
		return this.daoFactory.getNamingConfigurationDAO().getByName(name);
	}

	@Override
	public GermplasmDTO getGermplasmDTOByGID(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getGermplasmDTO(gid);
	}

	@Override
	public List<GermplasmDTO> searchGermplasmDTO(
		final GermplasmSearchRequestDto germplasmSearchRequestDTO, final Integer page, final Integer pageSize) {
		return this.daoFactory.getGermplasmDao().getGermplasmDTOList(germplasmSearchRequestDTO, page, pageSize);
	}

	@Override
	public long countGermplasmDTOs(final GermplasmSearchRequestDto germplasmSearchRequestDTO) {
		return this.daoFactory.getGermplasmDao().countGermplasmDTOs(germplasmSearchRequestDTO);
	}

	@Override
	public Germplasm getUnknownGermplasmWithPreferredName() {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(0);
		final Name preferredName = new Name();
		preferredName.setNval(Name.UNKNOWN);
		germplasm.setPreferredName(preferredName);
		return germplasm;
	}

	@Override
	public List<AttributeDTO> getAttributesByGid(
		final String gid, final List<String> attributeDbIds, final Integer pageSize, final Integer pageNumber) {
		return this.daoFactory.getAttributeDAO().getAttributesByGidAndAttributeIds(gid, attributeDbIds, pageSize, pageNumber);
	}

	@Override
	public long countAttributesByGid(final String gid, final List<String> attributeDbIds) {
		return this.daoFactory.getAttributeDAO().countAttributesByGid(gid, attributeDbIds);
	}

	@Override
	public List<Attribute> getAttributeByIds(final List<Integer> ids) {
		return this.daoFactory.getAttributeDAO().getByIDs(ids);
	}

	@Override
	public List<Germplasm> getExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		return this.daoFactory.getGermplasmDao().getExistingCrosses(femaleParent, maleParentIds, gid);
	}

	@Override
	public boolean hasExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		return this.daoFactory.getGermplasmDao().hasExistingCrosses(femaleParent, maleParentIds, gid);
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByTableTypeAndCodes(final String table, final Set<String> types,
		final Set<String> codes) {
		return this.daoFactory.getUserDefinedFieldDAO().getByCodes(table, types, codes);
	}

	@Override
	public List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(final String tableName, final Set<String> fieldTypes) {
		return this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(tableName, fieldTypes);
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}

