
package org.generationcp.middleware.manager.ontology;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.DataManager;
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
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.impl.derived_variables.FormulaServiceImpl;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implements {@link OntologyVariableDataManagerImpl}
 */
@Transactional
public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

	private static final String PROPERTY_IDS = "propertyIds";
	private static final String VARIABLE_IDS = "variableIds";
	private static final String SCALE_IDS = "scaleIds";
	private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
	private static final String TERM_IS_NOT_VARIABLE = "The term {0} is not Variable.";
	private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";
	private static final String CAN_NOT_DELETE_USED_VARIABLE = "Used variable can not be deleted";
	private static final String VARIABLE_TYPE_ANALYSIS_SHOULD_BE_USED_SINGLE =
			"Analysis and/or Analysis Summary variable type(s) should not be assigned together with any other variable type";
	private static final String OBSERVATION_UNIT_VARIABLES_CANNOT_BE_TRAITS =
			"Variables cannot be classified as both Observation Unit and Trait. Please check the variable types assigned and try again.";
		
	@Autowired
	private OntologyMethodDataManager methodManager;

	@Autowired
	private OntologyPropertyDataManager propertyManager;

	@Autowired
	private OntologyScaleDataManager scaleManager;

	@Autowired
	private FormulaService formulaService;

	private DaoFactory daoFactory;

	private static final Logger LOG = LoggerFactory.getLogger(OntologyVariableDataManagerImpl.class);

	public OntologyVariableDataManagerImpl() {
		super();
	}

	public OntologyVariableDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.propertyManager = new OntologyPropertyDataManagerImpl(sessionProvider);
		this.methodManager = new OntologyMethodDataManagerImpl(sessionProvider);
		this.scaleManager = new OntologyScaleDataManagerImpl(sessionProvider);
		this.formulaService = new FormulaServiceImpl(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public OntologyVariableDataManagerImpl(final OntologyMethodDataManager methodDataManager,
			final OntologyPropertyDataManager propertyDataManager, final OntologyScaleDataManager scaleDataManager, final FormulaService formulaService,
			final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.methodManager = methodDataManager;
		this.propertyManager = propertyDataManager;
		this.scaleManager = scaleDataManager;
		this.formulaService = formulaService;
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Variable> getWithFilter(final VariableFilter variableFilter) {

		final Map<Integer, Variable> map = new HashMap<>();

		try {

			final Map<String, List<Integer>> listParameters = new HashMap<>();

			String filterClause = "";

			// Execute only if fetchAll is false
			if (!variableFilter.isFetchAll()) {

				// check for to fetch favorites variables and add filter clause for it
				if (variableFilter.isFavoritesOnly()) {
					filterClause += "  and pf.id is not null";
				}

				// check if methodIds not empty and add filter clause of it
				if (!variableFilter.getMethodIds().isEmpty()) {
					filterClause += " and vmr.mid in (:methodIds)";
					listParameters.put("methodIds", variableFilter.getMethodIds());
				}

				// check if propertyIds not empty and add filter clause of it
				if (!variableFilter.getPropertyIds().isEmpty()) {
					filterClause += " and vpr.pid in (:propertyIds)";
					listParameters.put(PROPERTY_IDS, variableFilter.getPropertyIds());
				}

				// check if property class list is not empty then get properties by classes and add filter clause of it
				if (!variableFilter.getPropertyClasses().isEmpty()) {

					final SQLQuery pSQLQuery = this.getActiveSession()
							.createSQLQuery("select subject_id from cvterm_relationship where type_id = " + TermId.IS_A.getId()
									+ " and object_id in (select cvterm_id from cvterm where name in(:classNames) and cv_id = "
									+ CvId.TRAIT_CLASS.getId() + ");");

					pSQLQuery.setParameterList("classNames", variableFilter.getPropertyClasses());
					final List queryResults = pSQLQuery.list();

					if (!listParameters.containsKey(PROPERTY_IDS)) {
						filterClause += " and vpr.pid in (:propertyIds)";
						listParameters.put(PROPERTY_IDS, variableFilter.getPropertyIds());
					}

					final List<Integer> propertyIds = listParameters.get(PROPERTY_IDS);
					for (final Object row : queryResults) {
						propertyIds.add(this.typeSafeObjectToInteger(row));
					}

					// Filtering with class that is invalid. So no further iteration required.
					if (propertyIds.isEmpty()) {
						return new ArrayList<>();
					}
				}

				// check if scaleIds not empty and add filter clause of it
				if (!variableFilter.getScaleIds().isEmpty()) {
					filterClause += " and vsr.sid in (:scaleIds)";
					listParameters.put(SCALE_IDS, variableFilter.getScaleIds());
				}

				// check of data type list is not empty then get scales by data types and add filter clause of it
				if (!variableFilter.getDataTypes().isEmpty()) {

					final List<Integer> dataTypeIds = Util.convertAll(variableFilter.getDataTypes(), new Function<DataType, Integer>() {

						@Override
						public Integer apply(final DataType x) {
							return x.getId();
						}
					});

					final SQLQuery sSQLQuery =
							this.getActiveSession().createSQLQuery("select subject_id from cvterm_relationship where type_id = "
									+ TermId.HAS_TYPE.getId() + " and object_id in(:dataTypeIds)");

					sSQLQuery.setParameterList("dataTypeIds", dataTypeIds);
					final List queryResults = sSQLQuery.list();

					if (!listParameters.containsKey(SCALE_IDS)) {
						filterClause += " and vsr.sid in (:scaleIds)";
						listParameters.put(SCALE_IDS, variableFilter.getScaleIds());
					}

					final List<Integer> scaleIds = listParameters.get(SCALE_IDS);
					for (final Object row : queryResults) {
						scaleIds.add(this.typeSafeObjectToInteger(row));
					}

					// Filtering with data type gives no scale. So no further iteration required.
					if (scaleIds.isEmpty()) {
						return new ArrayList<>();
					}
				}

				// check if variableIds not empty and add filter clause of it
				if (!variableFilter.getVariableIds().isEmpty()) {
					filterClause += " and v.cvterm_id in (:variableIds)";
					listParameters.put(VARIABLE_IDS, variableFilter.getVariableIds());
				}

				// check if excludedVariableIds not empty and add filter clause of it
				if (!variableFilter.getExcludedVariableIds().isEmpty()) {
					filterClause += " and v.cvterm_id not in(:excludedVariableIds)";
					listParameters.put("excludedVariableIds", variableFilter.getExcludedVariableIds());
				}

				// check of variable type list is not empty then get variables by variable types and add filter clause of it
				if (!variableFilter.getVariableTypes().isEmpty()) {

					final List<String> variableTypeNames =
							Util.convertAll(variableFilter.getVariableTypes(), new Function<VariableType, String>() {

								@Override
								public String apply(final VariableType x) {
									return x.getName();
								}
							});

					final SQLQuery vSQLQuery = this.getActiveSession()
							.createSQLQuery("select cvterm_id from cvtermprop where type_id = 1800 and value in (:variableTypeNames)");
					vSQLQuery.setParameterList("variableTypeNames", variableTypeNames);
					final List queryResults = vSQLQuery.list();

					if (!listParameters.containsKey(VARIABLE_IDS)) {
						filterClause += " and v.cvterm_id in (:variableIds)";
						listParameters.put(VARIABLE_IDS, variableFilter.getVariableIds());
					}

					final List<Integer> variableIds = listParameters.get(VARIABLE_IDS);
					for (final Object row : queryResults) {
						variableIds.add(this.typeSafeObjectToInteger(row));
					}

					// Filtering with variable types that is not used or invalid. So no further iteration required.
					if (variableIds.isEmpty()) {
						return new ArrayList<>();
					}
				}
			}

			// this query will get variables using filter
			final SQLQuery query = this.getActiveSession()
					.createSQLQuery(
							"select v.cvterm_id vid, v.name vn, v.definition vd, vmr.mid, vmr.mn, vmr.md, vpr.pid, vpr.pn, vpr.pd, vsr.sid, vsr.sn, vsr.sd, vo.alias g_alias, vo.expected_min g_min_value, vo.expected_max g_max_value, vpo.alias p_alias, vpo.expected_min p_min_value, vpo.expected_max p_max_value, pf.id fid from cvterm v "
									+ "left join (select mr.subject_id vid, m.cvterm_id mid, m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and mr.type_id = 1210) vmr on vmr.vid = v.cvterm_id "
									+ "left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = 1200) vpr on vpr.vid = v.cvterm_id "
									+ "left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = 1220) vsr on vsr.vid = v.cvterm_id "
									+ "left join variable_overrides vo on vo.cvterm_id = v.cvterm_id and vo.program_uuid is null "
									+ "left join variable_overrides vpo on vpo.cvterm_id = v.cvterm_id and vpo.program_uuid = :programUuid "
									+ "left join program_favorites pf on pf.entity_id = v.cvterm_id and pf.program_uuid = :programUuid and pf.entity_type = 'VARIABLES' "
									+ "WHERE (v.cv_id = 1040) " + filterClause)
					.addScalar("vid").addScalar("vn").addScalar("vd").addScalar("pid").addScalar("pn").addScalar("pd").addScalar("mid")
					.addScalar("mn").addScalar("md").addScalar("sid").addScalar("sn").addScalar("sd").addScalar("g_alias")
					.addScalar("g_min_value").addScalar("g_max_value").addScalar("p_alias").addScalar("p_min_value")
					.addScalar("p_max_value").addScalar("fid");

			query.setParameter("programUuid", variableFilter.getProgramUuid());

			// get data from parameter map and apply parameter to query
			for (final String lp : listParameters.keySet()) {
				query.setParameterList(lp, listParameters.get(lp));
			}

			final Map<Integer, Method> mMap = new HashMap<>();
			final Map<Integer, Property> pMap = new HashMap<>();
			final Map<Integer, Scale> sMap = new HashMap<>();

			final List queryResults = query.list();

			// get result output of query and store related data to variable (expected min/max, alias, method, scale, property, variable
			// term data)
			for (final Object row : queryResults) {
				final Object[] items = (Object[]) row;
				final Variable variable =
						new Variable(new Term(this.typeSafeObjectToInteger(items[0]), (String) items[1], (String) items[2]));

				final Integer propertyId = this.typeSafeObjectToInteger(items[3]);
				if (!pMap.containsKey(propertyId)) {
					pMap.put(propertyId,
							new Property(new Term(this.typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5])));
				}
				variable.setProperty(pMap.get(propertyId));

				final Integer methodId = this.typeSafeObjectToInteger(items[6]);
				if (!mMap.containsKey(methodId)) {
					mMap.put(methodId, new Method(new Term(this.typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8])));
				}
				variable.setMethod(mMap.get(methodId));

				final Integer scaleId = this.typeSafeObjectToInteger(items[9]);
				if (!sMap.containsKey(scaleId)) {
					sMap.put(scaleId, new Scale(new Term(this.typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11])));
				}

				variable.setScale(sMap.get(scaleId));

				// Alias, Expected Min Value, Expected Max Value
				final String gAlias = (String) items[12];
				final String gExpMin = (String) items[13];
				final String gExpMax = (String) items[14];
				final String pAlias = (String) items[15];
				final String pExpMin = (String) items[16];
				final String pExpMax = (String) items[17];

				if (pAlias == null && pExpMin == null && pExpMax == null) {
					variable.setAlias(gAlias);
					variable.setMinValue(gExpMin);
					variable.setMaxValue(gExpMax);
				} else {
					variable.setAlias(pAlias);
					variable.setMinValue(pExpMin);
					variable.setMaxValue(pExpMax);
				}

				variable.setIsFavorite(items[18] != null);
				map.put(variable.getId(), variable);
			}

			// No variable found based on criteria
			if (map.isEmpty()) {
				return new ArrayList<>();
			}

			// Fetch Property Class, Data Type and Categories from cvterm_relationship
			final SQLQuery rQuery = this.getActiveSession()
					.createSQLQuery(
							"select tr.subject_id sid, tr.type_id tid, tr.object_id rid, t.name rn, t.definition rd from cvterm_relationship tr inner join cvterm t on t.cvterm_id = tr.object_id "
									+ "where tr.subject_id in (:propertyIds) or tr.subject_id in (:scaleIds)")
					.addScalar("sid").addScalar("tid").addScalar("rid").addScalar("rn").addScalar("rd");

			rQuery.setParameterList(PROPERTY_IDS, pMap.keySet());
			rQuery.setParameterList(SCALE_IDS, sMap.keySet());

			final List rQueryResults = rQuery.list();

			for (final Object row : rQueryResults) {
				final Object[] items = (Object[]) row;

				final Integer subjectId = this.typeSafeObjectToInteger(items[0]);
				final Integer typeId = this.typeSafeObjectToInteger(items[1]);
				final Integer objectId = this.typeSafeObjectToInteger(items[2]);

				final String name = (String) items[3];
				final String description = (String) items[4];

				if (Objects.equals(typeId, TermId.IS_A.getId())) {
					pMap.get(subjectId).addClass(name);
				} else if (Objects.equals(typeId, TermId.HAS_TYPE.getId())) {
					sMap.get(subjectId).setDataType(DataType.getById(objectId));
				} else if (Objects.equals(typeId, TermId.HAS_VALUE.getId())) {
					sMap.get(subjectId).addCategory(new TermSummary(objectId, name, description));
				}
			}

			// Fetch Property CropOntologyId, Scale min-max, Variable Type, Creation and Last Modified date of all terms
			final SQLQuery pQuery = this.getActiveSession()
					.createSQLQuery("select t.cvterm_id tid, t.cv_id cvid, tp.type_id typeid, tp.value value from cvtermprop tp "
							+ "inner join cvterm t on t.cvterm_id = tp.cvterm_id "
							+ "WHERE tp.cvterm_id in(:methodIds) or tp.cvterm_id in(:propertyIds) or tp.cvterm_id in(:scaleIds) or tp.cvterm_id in(:variableIds)")
					.addScalar("tid").addScalar("cvid").addScalar("typeid").addScalar("value");

			// set parameter to query
			pQuery.setParameterList("methodIds", mMap.keySet());
			pQuery.setParameterList(PROPERTY_IDS, pMap.keySet());
			pQuery.setParameterList(SCALE_IDS, sMap.keySet());
			pQuery.setParameterList(VARIABLE_IDS, map.keySet());

			final List pQueryResults = pQuery.list();

			// fetch data from results and add data to related terms
			for (final Object row : pQueryResults) {
				final Object[] items = (Object[]) row;

				final Integer cvTermId = this.typeSafeObjectToInteger(items[0]);
				final Integer cvId = this.typeSafeObjectToInteger(items[1]);
				final Integer typeId = this.typeSafeObjectToInteger(items[2]);
				final String value = (String) items[3];

				if (Objects.equals(typeId, TermId.CROP_ONTOLOGY_ID.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
					pMap.get(cvTermId).setCropOntologyId(value);
				} else if (Objects.equals(typeId, TermId.VARIABLE_TYPE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
					map.get(cvTermId).addVariableType(VariableType.getByName(value));
				} else if (Objects.equals(typeId, TermId.MIN_VALUE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
					sMap.get(cvTermId).setMinValue(value);
				} else if (Objects.equals(typeId, TermId.MAX_VALUE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
					sMap.get(cvTermId).setMaxValue(value);
				} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.METHODS.getId())) {
					mMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.METHODS.getId())) {
					mMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
					pMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
					pMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
					sMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
					sMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
					map.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
				} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
					map.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
				}

			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getAllVariables", e);
		}

		final List<FormulaDto> formulaDtoList = this.formulaService.getByTargetIds(map.keySet());
		for (final FormulaDto formulaDto : formulaDtoList) {
			map.get(formulaDto.getTarget().getId()).setFormula(formulaDto);
		}

		final List<Variable> variables = new ArrayList<>(map.values());

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
	public Variable getVariable(final String programUuid, final Integer id, final boolean filterObsolete,
			final boolean calculateVariableUsage) {

		final Variable cachedVariable = VariableCache.getFromCache(id, programUuid);
		if (cachedVariable != null) {
			return cachedVariable;
		}

		final Monitor monitor = MonitorFactory.start("Get Variable");
		try {

			// Fetch term from db
			final CVTerm term = daoFactory.getCvTermDao().getById(id);

			this.checkTermIsVariable(term);

			final Variable variable = new Variable(Term.fromCVTerm(term));

			// load scale, method and property data
			final List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(term.getCvTermId());
			for (final CVTermRelationship r : relationships) {
				if (r.getTypeId() == TermId.HAS_METHOD.getId()) {
					variable.setMethod(this.methodManager.getMethod(r.getObjectId(), filterObsolete));
				} else if (r.getTypeId() == TermId.HAS_PROPERTY.getId()) {
					variable.setProperty(this.propertyManager.getProperty(r.getObjectId(), filterObsolete));
				} else if (r.getTypeId() == TermId.HAS_SCALE.getId()) {
					variable.setScale(this.scaleManager.getScaleById(r.getObjectId(), filterObsolete));
				}
			}

			// Variable Types, Created, modified from CVTermProperty
			final List<CVTermProperty> properties = daoFactory.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());

			for (final CVTermProperty property : properties) {
				if (property.getTypeId() == TermId.VARIABLE_TYPE.getId()) {
					final VariableType variableType = VariableType.getByName(property.getValue());
					variable.addVariableType(variableType);
					if (variableType.equals(VariableType.TRAIT)) {
						variable.setAllowsFormula(true);
					}
				} else if (property.getTypeId() == TermId.CREATION_DATE.getId()) {
					variable.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (property.getTypeId() == TermId.LAST_UPDATE_DATE.getId()) {
					variable.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				} else if (property.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
					variable.getProperty().setCropOntologyId(property.getValue());
				}
			}

			// Formula
			final Optional<FormulaDto> formula = this.formulaService.getByTargetId(id);
			if (formula.isPresent()) {
				variable.setFormula(formula.get());
			}

			// Variable alias and expected range
			final VariableOverrides overrides = this.getVariableProgramOverridesDao().getByVariableAndProgram(id, programUuid);

			if (overrides != null) {
				variable.setAlias(overrides.getAlias());
				variable.setMinValue(overrides.getExpectedMin());
				variable.setMaxValue(overrides.getExpectedMax());
			}

			// Get favorite from ProgramFavoriteDAO
			final ProgramFavorite programFavorite =
					this.getProgramFavoriteDao().getProgramFavorite(programUuid, ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());
			variable.setIsFavorite(programFavorite != null);

			if (calculateVariableUsage) {

				// setting variable studies
				variable.setStudies(0);

				variable.setDataSets((int) this.getDmsProjectDao().countByVariable(id));

				//setting variable observations, first observations will be null so set it to 0
				Integer observations = 0;
				for (VariableType v : variable.getVariableTypes()) {
					long observationsPerType = this.getExperimentDao().countByObservedVariable(id, v.getId());
					observations = (int) (observations + observationsPerType);
				}
				variable.setObservations(observations);
				//it can be replaced by observations > 0
				variable.setHasUsage(this.isVariableUsedInStudy(id));

			} else {
				final int unknownUsage = -1;
				variable.setStudies(unknownUsage);
				variable.setObservations(unknownUsage);

			}

			VariableCache.addToCache(id, variable, programUuid);

			return variable;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getVariable", e);
		} finally {
			OntologyVariableDataManagerImpl.LOG.debug("" + monitor.stop() + ". This instance was for variable id: " + id);
		}
	}

	@Override
	public void processTreatmentFactorHasPairValue(final List<Variable> summaryList, final List<Integer> hiddenFields) {
		for (final Variable variable : summaryList) {
			variable.setHasPair(
					daoFactory.getCvTermDao().hasPossibleTreatmentPairs(variable.getId(), variable.getProperty().getId(), hiddenFields));
		}
	}

	@Override
	public void addVariable(final OntologyVariableInfo variableInfo) {

		final CVTerm term = daoFactory.getCvTermDao().getByNameAndCvId(variableInfo.getName(), CvId.VARIABLES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_EXIST_WITH_SAME_NAME);
		}

		this.validateVariableTypes(variableInfo);

		// Saving term to database.
		final CVTerm savedTerm = daoFactory.getCvTermDao().save(variableInfo.getName(), variableInfo.getDescription(), CvId.VARIABLES);
		variableInfo.setId(savedTerm.getCvTermId());

		// Setting method to variable
		if (variableInfo.getMethodId() != null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(), variableInfo.getMethodId());
		}

		// Setting property to variable
		if (variableInfo.getPropertyId() != null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(),
					variableInfo.getPropertyId());
		}

		// Setting scale to variable
		if (variableInfo.getScaleId() != null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
		}

		int rank = 0;
		for (final VariableType type : variableInfo.getVariableTypes()) {
			final CVTermProperty property = new CVTermProperty();
			property.setCvTermId(variableInfo.getId());
			property.setTypeId(TermId.VARIABLE_TYPE.getId());
			property.setValue(type.getName());
			property.setRank(rank++);
			daoFactory.getCvTermPropertyDao().save(property);
		}

		// Saving min max values
		if (variableInfo.getExpectedMin() != null || variableInfo.getExpectedMax() != null) {
			this.getVariableProgramOverridesDao().save(variableInfo.getId(), variableInfo.getProgramUuid(), null,
					variableInfo.getExpectedMin(), variableInfo.getExpectedMax());
		}

		// Saving favorite
		if (variableInfo.isFavorite() != null && variableInfo.isFavorite()) {
			final ProgramFavorite programFavorite = new ProgramFavorite();
			programFavorite.setEntityId(variableInfo.getId());
			programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
			programFavorite.setUniqueID(variableInfo.getProgramUuid());
			this.getProgramFavoriteDao().save(programFavorite);
		}

		// Setting last update time.
		daoFactory.getCvTermPropertyDao().save(variableInfo.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);
	}

	@Override
	public void updateVariable(final OntologyVariableInfo variableInfo) {

		VariableCache.removeFromCache(variableInfo.getId());

		final List<FormulaDto> formulas = this.formulaService.getByInputId(Integer.valueOf(variableInfo.getId()));
		for (final FormulaDto formula : formulas) {
			VariableCache.removeFromCache(formula.getTarget().getId());
		}

		final VariableInfoDaoElements elements = new VariableInfoDaoElements();
		elements.setVariableId(variableInfo.getId());
		elements.setProgramUuid(variableInfo.getProgramUuid());

		this.fillDaoElementsAndCheckForUsage(elements);

		final CVTerm term = elements.getVariableTerm();

		this.checkTermIsVariable(term);

		this.validateVariableTypes(variableInfo);

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
			daoFactory.getCvTermDao().merge(term);
		}

		// Setting method to variable
		if (methodRelation == null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(), variableInfo.getMethodId());
		} else if (!Objects.equals(methodRelation.getObjectId(), variableInfo.getMethodId())) {
			methodRelation.setObjectId(variableInfo.getMethodId());
			daoFactory.getCvTermRelationshipDao().merge(methodRelation);
		}

		// Setting property to variable
		if (propertyRelation == null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(),
					variableInfo.getPropertyId());
		} else if (!Objects.equals(propertyRelation.getObjectId(), variableInfo.getPropertyId())) {
			propertyRelation.setObjectId(variableInfo.getPropertyId());
			daoFactory.getCvTermRelationshipDao().merge(propertyRelation);
		}

		// Setting scale to variable
		if (scaleRelation == null) {
			daoFactory.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
		} else if (!Objects.equals(scaleRelation.getObjectId(), variableInfo.getScaleId())) {
			scaleRelation.setObjectId(variableInfo.getScaleId());
			daoFactory.getCvTermRelationshipDao().merge(scaleRelation);
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
			daoFactory.getCvTermPropertyDao().save(property);
		}

		// Remove variable type properties which are not part of incoming set.
		final Set<VariableType> toRemove = new HashSet<>(existingVariableTypes);
		toRemove.removeAll(variableInfo.getVariableTypes());

		for (final VariableType type : toRemove) {
			daoFactory.getCvTermPropertyDao().makeTransient(existingProperties.get(type));
		}

		// Saving alias, min, max values
		if (!Strings.isNullOrEmpty(variableInfo.getAlias()) || variableInfo.getExpectedMin() != null
				|| variableInfo.getExpectedMax() != null) {
			this.getVariableProgramOverridesDao().save(variableInfo.getId(), variableInfo.getProgramUuid(), variableInfo.getAlias(),
					variableInfo.getExpectedMin(), variableInfo.getExpectedMax());
		} else if (variableOverrides != null) {
			this.getVariableProgramOverridesDao().makeTransient(variableOverrides);
		}

		// Updating favorite to true if alias is defined
		ProgramFavorite programFavorite = this.getProgramFavoriteDao().getProgramFavorite(variableInfo.getProgramUuid(),
				ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());

		final String previousAlias = variableOverrides == null ? null : variableOverrides.getAlias();
		final String newAlias = "".equals(variableInfo.getAlias()) ? null : variableInfo.getAlias();
		boolean isFavorite = variableInfo.isFavorite();

		if (newAlias != null && previousAlias == null) {
			isFavorite = true;
		}

		if (isFavorite && programFavorite == null) {
			programFavorite = new ProgramFavorite();
			programFavorite.setEntityId(variableInfo.getId());
			programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
			programFavorite.setUniqueID(variableInfo.getProgramUuid());
			this.getProgramFavoriteDao().save(programFavorite);
		} else if (!isFavorite && programFavorite != null) {
			this.getProgramFavoriteDao().makeTransient(programFavorite);
		}

		daoFactory.getCvTermPropertyDao().save(variableInfo.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

	}

	private void validateVariableTypes(final OntologyVariableInfo variableInfo) {
		// Variable types "Analysis" or "Analysis Summary" cannot be used with other variable types
		if (!Collections.disjoint(variableInfo.getVariableTypes(), VariableType.getReservedVariableTypes())
				&& variableInfo.getVariableTypes().size() > 1) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_TYPE_ANALYSIS_SHOULD_BE_USED_SINGLE);
		
		} else if (variableInfo.getVariableTypes().contains(VariableType.OBSERVATION_UNIT)
				&& variableInfo.getVariableTypes().contains(VariableType.TRAIT)) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.OBSERVATION_UNIT_VARIABLES_CANNOT_BE_TRAITS);
		}
	}

	@Override
	public void deleteVariable(final Integer variableId) {

		VariableCache.removeFromCache(variableId);

		final CVTerm term = daoFactory.getCvTermDao().getById(variableId);

		this.checkTermIsVariable(term);

		// check usage
		final Boolean isUsed = this.isVariableUsedInStudy(variableId);

		if (isUsed) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.CAN_NOT_DELETE_USED_VARIABLE);
		}

		try {

			// Delete relationships
			final List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(variableId);
			for (final CVTermRelationship relationship : relationships) {
				daoFactory.getCvTermRelationshipDao().makeTransient(relationship);
			}

			// delete properties
			final List<CVTermProperty> properties = daoFactory.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());
			for (final CVTermProperty property : properties) {
				daoFactory.getCvTermPropertyDao().makeTransient(property);
			}

			// delete Variable alias and expected range
			final List<VariableOverrides> variableOverridesList = this.getVariableProgramOverridesDao().getByVariableId(variableId);

			for (final VariableOverrides overrides : variableOverridesList) {
				this.getVariableProgramOverridesDao().makeTransient(overrides);
			}

			// delete variable synonym
			this.deleteVariableSynonym(variableId);

			// delete main entity
			daoFactory.getCvTermDao().makeTransient(term);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
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

	@Override
	public boolean isVariableUsedInStudy(final int variableId) {
		final String variableUsageCount = "SELECT *  FROM projectprop pp " + " WHERE "
				+ " pp.variable_id = :variableId "
				+ " AND pp.project_id not in (SELECT p.project_id FROM project p WHERE p.deleted = 1) limit 1";

		final SQLQuery query = this.getActiveSession().createSQLQuery(variableUsageCount);
		query.setParameter("variableId", variableId);
		return !query.list().isEmpty();
	}

	private void updateVariableSynonym(final CVTerm term, final String newVariableName) {
		final String oldVariableName = term.getName().trim();
		final String newName = newVariableName.trim();

		if (!Objects.equals(oldVariableName, newName)) {

			final List<CVTermSynonym> byCvTermSynonymList = this.getCvTermSynonymDao().getByCvTermId(term.getCvTermId());
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
				this.getCvTermSynonymDao().save(cvTermSynonym);
			}
		}
	}

	private void deleteVariableSynonym(final int variableId) {
		// delete Variable synonym
		final List<CVTermSynonym> cvTermSynonymList = this.getCvTermSynonymDao().getByCvTermId(variableId);

		for (final CVTermSynonym synonym : cvTermSynonymList) {
			this.getCvTermSynonymDao().makeTransient(synonym);
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

	private void fillDaoElementsAndCheckForUsage(final VariableInfoDaoElements elements) {

		// check required elements
		Util.checkAndThrowForNullObjects(elements.getVariableId());

		// Fetch term from db
		final CVTerm variableTerm = daoFactory.getCvTermDao().getById(elements.getVariableId());

		this.checkTermIsVariable(variableTerm);

		CVTermRelationship methodRelation = null;
		CVTermRelationship propertyRelation = null;
		CVTermRelationship scaleRelation = null;

		// load scale, method and property data
		final List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(variableTerm.getCvTermId());
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
		final List<CVTermProperty> termProperties = daoFactory.getCvTermPropertyDao().getByCvTermId(elements.getVariableId());

		final VariableOverrides variableOverrides =
				this.getVariableProgramOverridesDao().getByVariableAndProgram(elements.getVariableId(), elements.getProgramUuid());

		// Set to elements to send response back to caller.
		elements.setVariableTerm(variableTerm);
		elements.setMethodRelation(methodRelation);
		elements.setPropertyRelation(propertyRelation);
		elements.setScaleRelation(scaleRelation);
		elements.setTermProperties(termProperties);
		elements.setVariableOverrides(variableOverrides);
	}

	@Override
	public boolean areVariablesUsedInStudy(final List<Integer> variablesIds) {
		final String variableUsageCount = "SELECT *  FROM projectprop pp "
				+ " WHERE pp.variable_id IN (:variablesIds) "
				+ " AND pp.project_id not in ( SELECT p.project_id FROM project p WHERE p.deleted = 1) limit 1";

		final SQLQuery query = this.getActiveSession().createSQLQuery(variableUsageCount);
		query.setParameterList("variablesIds", variablesIds);
		return !query.list().isEmpty();
	}

	@Override
	public List<VariableOverrides> getVariableOverridesByVariableIds(final List<Integer> variableIds) {
		try {
			return this.getVariableProgramOverridesDao().getVariableOverridesByVariableIds(variableIds);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error at getVariableOverridesByVariableIds:" + e.getMessage(), e);
		}
	}

	@Override
	public List<VariableType> getVariableTypes(final Integer variableId) {
		final List<VariableType> variableTypes = new ArrayList<>();
		final List<CVTermProperty> properties = daoFactory.getCvTermPropertyDao().getByCvTermAndType(variableId, TermId.VARIABLE_TYPE.getId());
		for (final CVTermProperty property : properties) {
			variableTypes.add(VariableType.getByName(property.getValue()));
		}
		return variableTypes;
	}

	@Override
	public Optional<DataType> getDataType(final Integer variableId) {
		List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(Arrays.asList(variableId), TermId.HAS_SCALE.getId());
		if (!relationships.isEmpty()) {
			final Integer scaleId = relationships.get(0).getObjectId();
			return Optional.of(this.scaleManager.getScaleById(scaleId, false).getDataType());
		}
		return Optional.absent();
	}

	@Override
	public void deleteVariablesFromCache(final List<Integer> variablesIds) {
		for (final Iterator<Integer> iterator = variablesIds.iterator(); iterator.hasNext();) {
			final Integer variableId = iterator.next();
			VariableCache.removeFromCache(variableId);
		}
	}
}
