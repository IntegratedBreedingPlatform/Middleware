
package org.generationcp.middleware.manager.ontology;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.manager.ontology.daoElements.VariableInfoDaoElements;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.util.Clock;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.SystemClock;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Implements {@link OntologyVariableDataManagerImpl}
 */
@Transactional
public class OntologyVariableDataManagerImpl implements OntologyVariableDataManager {

	private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
	private static final String TERM_IS_NOT_VARIABLE = "The term {0} is not Variable.";
	private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";
	private static final String CAN_NOT_DELETE_USED_VARIABLE = "Used variable can not be deleted";
	private static final String VARIABLE_TYPE_ANALYSIS_SHOULD_BE_USED_SINGLE =
			"Analysis variable type should not be assigned together with any other variable type";

	@Autowired
	private OntologyDaoFactory ontologyDaoFactory;

	@Autowired
	protected Clock systemClock;

	@Autowired
	private OntologyMethodDataManager methodManager;

	@Autowired
	private OntologyPropertyDataManager propertyManager;

	@Autowired
	private OntologyScaleDataManager scaleManager;

	@Autowired
	private OntologyCommonDAO ontologyCommonDAO;

	private static final Logger LOG = LoggerFactory.getLogger(OntologyVariableDataManagerImpl.class);

	public OntologyVariableDataManagerImpl() {
		// no-arg constructor is required by CGLIB proxying used by Spring 3x and older.
	}

    //TODO:This is temporary hack for managerFactory, builder and service. It should refactor to remove this constructor
    public OntologyVariableDataManagerImpl(HibernateSessionProvider sessionProvider) {
        this.ontologyDaoFactory = new OntologyDaoFactory();
        this.ontologyDaoFactory.setSessionProvider(sessionProvider);
        OntologyCommonDAOImpl ontologyCommonDAOImpl = new OntologyCommonDAOImpl();
        ontologyCommonDAOImpl.setSessionProvider(sessionProvider);
        this.ontologyCommonDAO = ontologyCommonDAOImpl;
        this.systemClock = new SystemClock();

        this.methodManager = new OntologyMethodDataManagerImpl(sessionProvider);
        this.propertyManager = new OntologyPropertyDataManagerImpl(sessionProvider);
        this.scaleManager = new OntologyScaleDataManagerImpl(sessionProvider);
    }

	@Override
	public List<Variable> getWithFilter(VariableFilter variableFilter) throws MiddlewareQueryException {

		final Map<Integer, Variable> variableMap = new HashMap<>();

		final Map<String, List<Integer>> listParameters = new HashMap<>();

		// Execute only if fetchAll is false
		if (!variableFilter.isFetchAll()) {

			this.ontologyCommonDAO.makeFilterClauseByIsFavouritesMethodIdsAndPropertyIds(variableFilter, listParameters);

			// check if property class list is not empty then get properties by classes and add filter clause of it
			if (!variableFilter.getPropertyClasses().isEmpty()) {

				List<Integer> propertyIds = this.ontologyCommonDAO.getPropertyIdsAndAddToFilterClause(variableFilter, listParameters);

				// Filtering with class that is invalid. So no further iteration required.
				if (propertyIds.isEmpty()) {
					return new ArrayList<>();
				}
			}

			this.ontologyCommonDAO.makeFilterClauseByScaleIds(variableFilter, listParameters);

			// check if data type list is not empty then get scales by data types and add filter clause of it
			if (!variableFilter.getDataTypes().isEmpty()) {

					final List<Integer> dataTypeIds = Util.convertAll(variableFilter.getDataTypes(), new Function<DataType, Integer>() {

						@Override
						public Integer apply(final DataType x) {
							return x.getId();
						}
					});

				List<Integer> scaleIds = this.ontologyCommonDAO.getScaleIdsAndAddToFilterClause(variableFilter, dataTypeIds, listParameters);

				// Filtering with data type gives no scale. So no further iteration required.
				if (scaleIds.isEmpty()) {
					return new ArrayList<>();
				}
			}

			this.ontologyCommonDAO.makeFilterClauseByVariableIdsAndExcludedVariableIds(variableFilter, listParameters);

			// check if variable type list is not empty then get variables by variable types and add filter clause of it
			if (!variableFilter.getVariableTypes().isEmpty()) {

				final List<String> variableTypeNames =
						Util.convertAll(variableFilter.getVariableTypes(), new Function<VariableType, String>() {

							@Override
							public String apply(final VariableType x) {
								return x.getName();
							}
						});

				List<Integer> variableIds = this.ontologyCommonDAO.getVariableIdsAndAddToFilterClause(variableFilter, variableTypeNames, listParameters);

				// Filtering with variable types that is not used or invalid. So no further iteration required.
				if (variableIds.isEmpty()) {
					return new ArrayList<>();
				}
			}
		}

		final Map<Integer, Method> methodMap = new HashMap<>();
		final Map<Integer, Property> propertyMap = new HashMap<>();
		final Map<Integer, Scale> scaleMap = new HashMap<>();

		this.ontologyCommonDAO.fillVariableMapUsingFilterClause(variableFilter, listParameters, variableMap, methodMap, propertyMap, scaleMap);

		// No variable found based on criteria
		if (variableMap.isEmpty()) {
			return new ArrayList<>();
		}

		this.ontologyCommonDAO.getVariableRelationships(methodMap, propertyMap, scaleMap);

		this.ontologyCommonDAO.getVariableProperties(variableMap, methodMap, propertyMap, scaleMap);

		List<Variable> variables = new ArrayList<>(variableMap.values());

		// sort variable list by variable name
		Collections.sort(variables, new Comparator<Variable>() {

			@Override
			public int compare(final Variable l, final Variable r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return variables;
	}

	@Override
	public Variable getVariable(final String programUuid, final Integer id, final boolean filterObsolete, final boolean calculateVariableUsage) throws MiddlewareQueryException {

		final Variable cachedVariable = VariableCache.getFromCache(id);
		if (cachedVariable != null) {
			return cachedVariable;
		}

		CVTermDao termDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao relationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao propertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();
		VariableOverridesDao programOverridesDao = this.ontologyDaoFactory.getVariableProgramOverridesDao();
		ProgramFavoriteDAO programFavoriteDao = this.ontologyDaoFactory.getProgramFavoriteDao();

		final Monitor monitor = MonitorFactory.start("Get Variable");
		try {

			// Fetch term from db
			final CVTerm term = termDao.getById(id);

			this.checkTermIsVariable(term);

			final Variable variable = new Variable(Term.fromCVTerm(term));

			// load scale, method and property data
			final List<CVTermRelationship> relationships = relationshipDao.getBySubject(term.getCvTermId());
			for (final CVTermRelationship r : relationships) {
				if (r.getTypeId() == TermId.HAS_METHOD.getId()) {
					variable.setMethod(this.methodManager.getMethod(r.getObjectId(), filterObsolete));
				} else if (r.getTypeId() == TermId.HAS_PROPERTY.getId()) {
					variable.setProperty(this.propertyManager.getProperty(r.getObjectId(), filterObsolete));
				} else if (r.getTypeId() == TermId.HAS_SCALE.getId()) {
					variable.setScale(this.scaleManager.getScale(r.getObjectId(), filterObsolete));
				}
			}

			// Variable Types, Created, modified from CVTermProperty
			final List<CVTermProperty> properties = propertyDao.getByCvTermId(term.getCvTermId());

			for (final CVTermProperty property : properties) {
				if (property.getTypeId() == TermId.VARIABLE_TYPE.getId()) {
					variable.addVariableType(VariableType.getByName(property.getValue()));
				} else if (property.getTypeId() == TermId.CREATION_DATE.getId()) {
					variable.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (property.getTypeId() == TermId.LAST_UPDATE_DATE.getId()) {
					variable.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				} else if (property.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
					variable.getProperty().setCropOntologyId(property.getValue());
				}
			}

			// Variable alias and expected range
			final VariableOverrides overrides = programOverridesDao.getByVariableAndProgram(id, programUuid);

			if (overrides != null) {
				variable.setAlias(overrides.getAlias());
				variable.setMinValue(overrides.getExpectedMin());
				variable.setMaxValue(overrides.getExpectedMax());
			}

			// Get favorite from ProgramFavoriteDAO
			final ProgramFavorite programFavorite =
					programFavoriteDao.getProgramFavorite(programUuid, ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());
			variable.setIsFavorite(programFavorite != null);

			if (calculateVariableUsage) {
				// setting variable studies
				variable.setStudies((int) this.ontologyDaoFactory.getDmsProjectDao().countByVariable(id));

				// setting variable observations, first observations will be null so set it to 0
				variable.setObservations(0);
				for (final VariableType v : variable.getVariableTypes()) {
					final long observation = this.ontologyDaoFactory.getExperimentDao().countByObservedVariable(id, v.getId());
					variable.setObservations((int) (variable.getObservations() + observation));
				}
			} else {
				final int unknownUsage = -1;
				variable.setStudies(unknownUsage);
				variable.setObservations(unknownUsage);

			}

			VariableCache.addToCache(id, variable);

			return variable;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getVariable", e);
		} finally {
			OntologyVariableDataManagerImpl.LOG.debug("" + monitor.stop() + ". This instance was for variable id: " + id);
		}
	}

	@Override
	public void processTreatmentFactorHasPairValue(final List<Variable> summaryList, final List<Integer> hiddenFields) throws MiddlewareQueryException {
		for (final Variable variable : summaryList) {
			variable.setHasPair(
					this.ontologyDaoFactory.getCvTermDao().hasPossibleTreatmentPairs(variable.getId(), variable.getProperty().getId(), hiddenFields));
		}
	}

	@Override
	public void addVariable(final OntologyVariableInfo variableInfo) {

		CVTermDao termDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao relationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao propertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();
		VariableOverridesDao programOverridesDao = this.ontologyDaoFactory.getVariableProgramOverridesDao();
		ProgramFavoriteDAO programFavoriteDao = this.ontologyDaoFactory.getProgramFavoriteDao();

		final CVTerm term = termDao.getByNameAndCvId(variableInfo.getName(), CvId.VARIABLES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_EXIST_WITH_SAME_NAME);
		}

		// Throw if variable type is analysis used with other variable types.
		if (variableInfo.getVariableTypes().contains(VariableType.ANALYSIS) && variableInfo.getVariableTypes().size() > 1) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_TYPE_ANALYSIS_SHOULD_BE_USED_SINGLE);
		}

		// Saving term to database.
		final CVTerm savedTerm = termDao.save(variableInfo.getName(), variableInfo.getDescription(), CvId.VARIABLES);
		variableInfo.setId(savedTerm.getCvTermId());

		// Setting method to variable
		if (variableInfo.getMethodId() != null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(), variableInfo.getMethodId());
		}

		// Setting property to variable
		if (variableInfo.getPropertyId() != null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(), variableInfo.getPropertyId());
		}

		// Setting scale to variable
		if (variableInfo.getScaleId() != null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
		}

		int rank = 0;
		for (final VariableType type : variableInfo.getVariableTypes()) {
			final CVTermProperty property = new CVTermProperty();
			property.setCvTermId(variableInfo.getId());
			property.setTypeId(TermId.VARIABLE_TYPE.getId());
			property.setValue(type.getName());
			property.setRank(rank++);
			propertyDao.save(property);
		}

		// Saving min max values
		if (variableInfo.getExpectedMin() != null || variableInfo.getExpectedMax() != null) {
			programOverridesDao.save(variableInfo.getId(), variableInfo.getProgramUuid(), null, variableInfo.getExpectedMin(),
					variableInfo.getExpectedMax());
		}

		// Saving favorite
		if (variableInfo.isFavorite() != null && variableInfo.isFavorite()) {
			final ProgramFavorite programFavorite = new ProgramFavorite();
			programFavorite.setEntityId(variableInfo.getId());
			programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
			programFavorite.setUniqueID(variableInfo.getProgramUuid());
			programFavoriteDao.save(programFavorite);
		}

		String strValueOfDate = ISO8601DateParser.toString(systemClock.now());
		// Setting last update time.
		propertyDao.save(variableInfo.getId(), TermId.CREATION_DATE.getId(), strValueOfDate, 0);
	}

	@Override
	public void updateVariable(final OntologyVariableInfo variableInfo) {

		VariableCache.removeFromCache(variableInfo.getId());

		final VariableInfoDaoElements elements = new VariableInfoDaoElements();
		CVTermDao termDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao relationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao propertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();
		VariableOverridesDao programOverridesDao = this.ontologyDaoFactory.getVariableProgramOverridesDao();
		ProgramFavoriteDAO programFavoriteDao = this.ontologyDaoFactory.getProgramFavoriteDao();

		elements.setVariableId(variableInfo.getId());
		elements.setProgramUuid(variableInfo.getProgramUuid());

		this.fillDaoElementsAndCheckForUsage(elements);

		final CVTerm term = elements.getVariableTerm();

		this.checkTermIsVariable(term);

		// Throw if variable type is analysis used with other variable types.
		if (variableInfo.getVariableTypes().contains(VariableType.ANALYSIS) && variableInfo.getVariableTypes().size() > 1) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_TYPE_ANALYSIS_SHOULD_BE_USED_SINGLE);
		}

		final CVTermRelationship methodRelation = elements.getMethodRelation();
		final CVTermRelationship propertyRelation = elements.getPropertyRelation();
		final CVTermRelationship scaleRelation = elements.getScaleRelation();
		final List<CVTermProperty> termProperties = elements.getTermProperties();
		final VariableOverrides variableOverrides = elements.getVariableOverrides();

		// Updating synonym
		this.updateVariableSynonym(term, variableInfo.getName());

		// Updating term to database.
		if (!(variableInfo.getName().equals(term.getName()) && Objects.equals(variableInfo.getDescription(), term.getDefinition()))) {
			term.setName(variableInfo.getName());
			term.setDefinition(variableInfo.getDescription());
			termDao.merge(term);
		}

		// Setting method to variable
		if (methodRelation == null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(), variableInfo.getMethodId());
		} else if (!Objects.equals(methodRelation.getObjectId(), variableInfo.getMethodId())) {
			methodRelation.setObjectId(variableInfo.getMethodId());
			relationshipDao.merge(methodRelation);
		}

		// Setting property to variable
		if (propertyRelation == null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(), variableInfo.getPropertyId());
		} else if (!Objects.equals(propertyRelation.getObjectId(), variableInfo.getPropertyId())) {
			propertyRelation.setObjectId(variableInfo.getPropertyId());
			relationshipDao.merge(propertyRelation);
		}

		// Setting scale to variable
		if (scaleRelation == null) {
			relationshipDao.save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
		} else if (!Objects.equals(scaleRelation.getObjectId(), variableInfo.getScaleId())) {
			scaleRelation.setObjectId(variableInfo.getScaleId());
			relationshipDao.merge(scaleRelation);
		}

		// Updating variable types
		final Map<VariableType, CVTermProperty> existingProperties = new HashMap<>();
		final Set<VariableType> existingVariableTypes = new HashSet<>();

		// Variable Types from CVTermProperty
		for (final CVTermProperty property : termProperties) {
			if (Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())) {
				final VariableType type = VariableType.getByName(property.getValue());
				existingVariableTypes.add(type);
				existingProperties.put(type, property);
			}
		}

		int rank = 0;
		for (final VariableType type : variableInfo.getVariableTypes()) {

			// skip existing
			if (existingVariableTypes.contains(type)) {
				continue;
			}

			final CVTermProperty property = new CVTermProperty();
			property.setCvTermId(variableInfo.getId());
			property.setTypeId(TermId.VARIABLE_TYPE.getId());
			property.setValue(type.getName());
			property.setRank(rank++);
			propertyDao.save(property);
		}

		// Remove variable type properties which are not part of incoming set.
		final Set<VariableType> toRemove = new HashSet<>(existingVariableTypes);
		toRemove.removeAll(variableInfo.getVariableTypes());

		for (final VariableType type : toRemove) {
			propertyDao.makeTransient(existingProperties.get(type));
		}

		// Saving alias, min, max values
		if (!Strings.isNullOrEmpty(variableInfo.getAlias()) || variableInfo.getExpectedMin() != null
				|| variableInfo.getExpectedMax() != null) {
			programOverridesDao.save(variableInfo.getId(), variableInfo.getProgramUuid(), variableInfo.getAlias(),
					variableInfo.getExpectedMin(), variableInfo.getExpectedMax());
		} else if (variableOverrides != null) {
			programOverridesDao.makeTransient(variableOverrides);
		}

		final String previousAlias = variableOverrides == null ? null : variableOverrides.getAlias();
		final String newAlias = variableInfo.getAlias().equals("") ? null : variableInfo.getAlias();
		boolean isFavorite = variableInfo.isFavorite();

		//Setting isFavorite if variable alias is set.
		if(Strings.isNullOrEmpty(previousAlias) && !Strings.isNullOrEmpty(newAlias)) {
			isFavorite = true;
		}

        // Updating favorite to true if alias is defined
        ProgramFavorite programFavorite =
                programFavoriteDao.getProgramFavorite(variableInfo.getProgramUuid(), ProgramFavorite.FavoriteType.VARIABLE,
                        term.getCvTermId());

		if (newAlias != null && previousAlias == null) {
			isFavorite = true;
		}

		if (isFavorite && programFavorite == null) {
			programFavorite = new ProgramFavorite();
			programFavorite.setEntityId(variableInfo.getId());
			programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
			programFavorite.setUniqueID(variableInfo.getProgramUuid());
			programFavoriteDao.save(programFavorite);
		} else if (!isFavorite && programFavorite != null) {
			programFavoriteDao.makeTransient(programFavorite);
		}

		String strValueOfDate = ISO8601DateParser.toString(systemClock.now());

		// Save creation time
		propertyDao.save(variableInfo.getId(), TermId.LAST_UPDATE_DATE.getId(), strValueOfDate, 0);

	}

	@Override
	public void deleteVariable(final Integer variableId) {

		VariableCache.removeFromCache(variableId);

		CVTermDao termDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao relationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao propertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();
		VariableOverridesDao programOverridesDao = this.ontologyDaoFactory.getVariableProgramOverridesDao();

		final CVTerm term = termDao.getById(variableId);

		this.checkTermIsVariable(term);

		// check usage
		final Integer usage = this.getVariableObservations(variableId);

		if (usage > 0) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.CAN_NOT_DELETE_USED_VARIABLE);
		}

		try {

			// Delete relationships
			final List<CVTermRelationship> relationships = this.ontologyDaoFactory.getCvTermRelationshipDao().getBySubject(variableId);
			for (final CVTermRelationship relationship : relationships) {
				relationshipDao.makeTransient(relationship);
			}

			// delete properties
			final List<CVTermProperty> properties = propertyDao.getByCvTermId(term.getCvTermId());
			for (final CVTermProperty property : properties) {
				propertyDao.makeTransient(property);
			}

			// delete Variable alias and expected range
			final List<VariableOverrides> variableOverridesList = programOverridesDao.getByVariableId(variableId);

			for (final VariableOverrides overrides : variableOverridesList) {
				programOverridesDao.makeTransient(overrides);
			}

			// delete variable synonym
			this.deleteVariableSynonym(variableId);

			// delete main entity
			termDao.makeTransient(term);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public Integer getVariableObservations(final int variableId) {
		return this.ontologyCommonDAO.getVariableObservations(variableId);
	}

	@Override
	public Integer getVariableStudies(final int variableId) {
		return 0;
	}

	@Override
	public String retrieveVariableCategoricalValue(final String programUuid, final Integer variableId, final Integer categoricalValueId) {
		if (variableId == null || categoricalValueId == null) {
			return null;
		}

		final Variable variable = this.getVariable(programUuid, variableId, true, false);
		for (final TermSummary summary : variable.getScale().getCategories()) {
			if (summary.getId().equals(categoricalValueId)) {
				return summary.getDefinition();
			}
		}

		return null;

    }

	@Override
	public String retrieveVariableCategoricalNameValue(final String programUuid, final Integer variableId, final Integer categoricalValueId,
			final boolean removeBraces) {

		if (variableId == null || categoricalValueId == null) {
			return null;
		}

		final Variable variable = this.getVariable(programUuid, variableId, true, false);
		for (final TermSummary summary : variable.getScale().getCategories()) {
			if (summary.getId().equals(categoricalValueId)) {
				return StringUtil.removeBraces(summary.getName());
			}
		}

		return null;
	}

	private void updateVariableSynonym(final CVTerm term, final String newVariableName) {
		final String oldVariableName = term.getName().trim();
		final String newName = newVariableName.trim();

		if (!Objects.equals(oldVariableName, newName)) {

			final List<CVTermSynonym> byCvTermSynonymList = this.ontologyDaoFactory.getCvTermSynonymDao().getByCvTermId(term.getCvTermId());
			boolean synonymFound = false;

			for (final CVTermSynonym cvTermSynonym : byCvTermSynonymList) {
				if (Objects.equals(oldVariableName, cvTermSynonym.getSynonym())) {
					synonymFound = true;
					break;
				}
			}

			if (!synonymFound) {
				final CVTermSynonym cvTermSynonym =
						CvTermSynonymDao.buildCvTermSynonym(term.getCvTermId(), oldVariableName, NameType.ALTERNATIVE_ENGLISH.getId());
				this.ontologyDaoFactory.getCvTermSynonymDao().save(cvTermSynonym);
			}
		}
	}

	private void deleteVariableSynonym(final int variableId) {
		// delete Variable synonym
		final List<CVTermSynonym> cvTermSynonymList = this.ontologyDaoFactory.getCvTermSynonymDao().getByCvTermId(variableId);

		for (final CVTermSynonym synonym : cvTermSynonymList) {
			this.ontologyDaoFactory.getCvTermSynonymDao().makeTransient(synonym);
		}
	}

	private void checkTermIsVariable(final CVTerm term) {

		if (term == null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.VARIABLES.getId()) {
			throw new MiddlewareException(MessageFormat.format(OntologyVariableDataManagerImpl.TERM_IS_NOT_VARIABLE, term.getName()));
		}
	}

	private void fillDaoElementsAndCheckForUsage(final VariableInfoDaoElements elements) throws MiddlewareQueryException {

		CVTermDao termDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao relationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao propertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();
		VariableOverridesDao programOverridesDao = this.ontologyDaoFactory.getVariableProgramOverridesDao();

		// check required elements
		Util.checkAndThrowForNullObjects(elements.getVariableId());

		// Fetch term from db
		final CVTerm variableTerm = termDao.getById(elements.getVariableId());

		this.checkTermIsVariable(variableTerm);

		CVTermRelationship methodRelation = null;
		CVTermRelationship propertyRelation = null;
		CVTermRelationship scaleRelation = null;

		// load scale, method and property data
		final List<CVTermRelationship> relationships = relationshipDao.getBySubject(variableTerm.getCvTermId());
		for (final CVTermRelationship relationship : relationships) {
			if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_METHOD.getId())) {
				methodRelation = relationship;
			} else if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_PROPERTY.getId())) {
				propertyRelation = relationship;
			} else if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_SCALE.getId())) {
				scaleRelation = relationship;
			}
		}

		// Variable Types from CVTermProperty
		final List<CVTermProperty> termProperties = propertyDao.getByCvTermId(elements.getVariableId());

		final VariableOverrides variableOverrides =
				programOverridesDao.getByVariableAndProgram(elements.getVariableId(), elements.getProgramUuid());

		// Set to elements to send response back to caller.
		elements.setVariableTerm(variableTerm);
		elements.setMethodRelation(methodRelation);
		elements.setPropertyRelation(propertyRelation);
		elements.setScaleRelation(scaleRelation);
		elements.setTermProperties(termProperties);
		elements.setVariableOverrides(variableOverrides);
	}
}
