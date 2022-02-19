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
import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
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
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
	public List<Germplasm> getGermplasmByName(final String name, final int start, final int numOfRows, final Operation op) {
		return this.daoFactory.getGermplasmDao().getByNamePermutations(name, op, start, numOfRows);
	}

	@Override
	public long countGermplasmByName(final String name, final Operation operation) {
		return this.daoFactory.getGermplasmDao().countByNamePermutations(name, operation);
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
	public Name getGermplasmNameByID(final Integer id) {
		return this.daoFactory.getNameDao().getById(id, false);
	}

	@Override
	public List<Name> getNamesByGID(final Integer gid, final Integer status, final GermplasmNameType type) {
		return this.daoFactory.getNameDao().getByGIDWithFilters(gid, status, type);
	}

	private Name getPreferredNameByGID(final Integer gid) {
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
	public Name getNameByGIDAndNval(final Integer gid, final String nval, final GetGermplasmByNameModes mode) {
		return this.daoFactory.getNameDao().getByGIDAndNval(gid, GermplasmDataManagerUtil.getNameToUseByMode(nval, mode));
	}

	@Override
	public List<Integer> addGermplasmName(final List<Name> names) {
		return this.addOrUpdateGermplasmName(names, Operation.ADD);
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
	public Method getMethodByID(final Integer id) {
		return this.daoFactory.getMethodDAO().getById(id, false);
	}

	@Override
	public List<Method> getAllMethods() {
		return this.daoFactory.getMethodDAO().getAllMethod();
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
	public List<Method> getMethodsByType(final String type) {
		return this.daoFactory.getMethodDAO().getByType(type);
	}

	@Override
	public List<Method> getMethodsByGroupAndTypeAndName(final String group, final String type, final String name) {
		return this.daoFactory.getMethodDAO().getByGroupAndTypeAndName(group, type, name);
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

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Location getLocationByID(final Integer id) {
		return this.daoFactory.getLocationDAO().getById(id, false);
	}

	@Override
	public List<Integer> addGermplasm(final List<Triple<Germplasm, Name, List<Progenitor>>> germplasmTriples, final CropType cropType) {
		final List<Integer> listOfGermplasm = new ArrayList<>();
		try {

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

				name.setGermplasm(germplasm);
				germplasm.getNames().clear();
				germplasm.getNames().add(name);
				this.daoFactory.getGermplasmDao().save(germplasm);

				listOfGermplasm.add(germplasm.getGid());

				for (final Progenitor progenitor : progenitors) {
					progenitor.setGermplasm(germplasm);
					this.daoFactory.getProgenitorDao().save(progenitor);
				}
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm: GermplasmDataManager.addGermplasm(): " + e.getMessage(), e);
		}
		return listOfGermplasm;
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
	public String getNextSequenceNumberAsString(final String prefix) {
		final String nextSequenceStr;
		nextSequenceStr = this.daoFactory.getGermplasmDao().getNextSequenceNumber(prefix);
		return nextSequenceStr;
	}

	@Override
	public List<Germplasm> getGermplasms(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getByGIDList(gids);
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
	public Method getMethodByCode(final String code) {
		return this.daoFactory.getMethodDAO().getByCode(code);
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
	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(gid);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final String programUUID) {
		return this.daoFactory.getProgramFavoriteDao().getProgramFavorites(type, programUUID);
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
	public boolean checkIfMatches(final String name) {
		return this.daoFactory.getNameDao().checkIfMatches(name);
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

	/**
	 * (non-Javadoc)
	 */
	@Override
	public UserDefinedField getUserDefinedFieldByTableTypeAndCode(final String table, final String type, final String code) {
		return this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(table, type, code);
	}

	@Override
	public List<String> getMethodCodeByMethodIds(final Set<Integer> methodIds) {
		return this.daoFactory.getMethodDAO().getMethodCodeByMethodIds(methodIds);
	}

	@Override
	public List<String> getNamesByGidsAndPrefixes(final List<Integer> gids, final List<String> prefixes) {
		return this.daoFactory.getNameDao().getNamesByGidsAndPrefixes(gids, prefixes);
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
	public String getAttributeValue(final Integer gid, final Integer variableId) {
		List<Attribute> attributes = new ArrayList<>();
		if (gid != null) {
			attributes =
				this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(variableId, Collections.singletonList(gid));
		}
		if (attributes.isEmpty()) {
			return "";
		} else {
			return attributes.get(0).getAval();
		}
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
	public List<Germplasm> getExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		return this.daoFactory.getGermplasmDao().getExistingCrosses(femaleParent, maleParentIds, gid);
	}

	@Override
	public boolean hasExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		return this.daoFactory.getGermplasmDao().hasExistingCrosses(femaleParent, maleParentIds, gid);
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}

