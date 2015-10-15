
package org.generationcp.middleware.manager.ontology;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
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
public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

	private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
	private static final String TERM_IS_NOT_VARIABLE = "The term {0} is not Variable.";
	private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";
	private static final String CAN_NOT_DELETE_USED_VARIABLE = "Used variable can not be deleted";

	@Autowired
	private OntologyMethodDataManager methodManager;

	@Autowired
	private OntologyPropertyDataManager propertyManager;

	@Autowired
	private OntologyScaleDataManager scaleManager;
	
	private static final ConcurrentMap<Integer, Variable> variableCache = new ConcurrentHashMap<>();
	
	private static final Logger LOG = LoggerFactory.getLogger(OntologyVariableDataManagerImpl.class);

	public OntologyVariableDataManagerImpl() {
		super();
	}

	public OntologyVariableDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public OntologyVariableDataManagerImpl(OntologyMethodDataManager methodDataManager, OntologyPropertyDataManager propertyDataManager,
			OntologyScaleDataManager scaleDataManager, HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.methodManager = methodDataManager;
		this.propertyManager = propertyDataManager;
		this.scaleManager = scaleDataManager;
	}

	@Override
	public List<Variable> getWithFilter(VariableFilter variableFilter) {

		Map<Integer, Variable> map = new HashMap<>();

		try {

			Map<String, List<Integer>> listParameters = new HashMap<>();

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
					listParameters.put("propertyIds", variableFilter.getPropertyIds());
				}

				// check if property class list is not empty then get properties by classes and add filter clause of it
				if (!variableFilter.getPropertyClasses().isEmpty()) {

					SQLQuery pSQLQuery =
							this.getActiveSession().createSQLQuery(
									"select subject_id from cvterm_relationship where type_id = " + TermId.IS_A.getId()
									+ " and object_id in (select cvterm_id from cvterm where name in(:classNames) and cv_id = "
									+ CvId.TRAIT_CLASS.getId() + ");");

					pSQLQuery.setParameterList("classNames", variableFilter.getPropertyClasses());
					List queryResults = pSQLQuery.list();

					if (!listParameters.containsKey("propertyIds")) {
						filterClause += " and vpr.pid in (:propertyIds)";
						listParameters.put("propertyIds", variableFilter.getPropertyIds());
					}

					List<Integer> propertyIds = listParameters.get("propertyIds");
					for (Object row : queryResults) {
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
					listParameters.put("scaleIds", variableFilter.getScaleIds());
				}

				// check of data type list is not empty then get scales by data types and add filter clause of it
				if (!variableFilter.getDataTypes().isEmpty()) {

					List<Integer> dataTypeIds = Util.convertAll(variableFilter.getDataTypes(), new Function<DataType, Integer>() {

						@Override
						public Integer apply(DataType x) {
							return x.getId();
						}
					});

					SQLQuery sSQLQuery =
							this.getActiveSession().createSQLQuery(
									"select subject_id from cvterm_relationship where type_id = " + TermId.HAS_TYPE.getId()
									+ " and object_id in(:dataTypeIds)");

					sSQLQuery.setParameterList("dataTypeIds", dataTypeIds);
					List queryResults = sSQLQuery.list();

					if (!listParameters.containsKey("scaleIds")) {
						filterClause += " and vsr.sid in (:scaleIds)";
						listParameters.put("scaleIds", variableFilter.getScaleIds());
					}

					List<Integer> scaleIds = listParameters.get("scaleIds");
					for (Object row : queryResults) {
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
					listParameters.put("variableIds", variableFilter.getVariableIds());
				}

				// check if excludedVariableIds not empty and add filter clause of it
				if (!variableFilter.getExcludedVariableIds().isEmpty()) {
					filterClause += " and v.cvterm_id not in(:excludedVariableIds)";
					listParameters.put("excludedVariableIds", variableFilter.getExcludedVariableIds());
				}

				// check of variable type list is not empty then get variables by variable types and add filter clause of it
				if (!variableFilter.getVariableTypes().isEmpty()) {

					List<String> variableTypeNames =
							Util.convertAll(variableFilter.getVariableTypes(), new Function<VariableType, String>() {

								@Override
								public String apply(VariableType x) {
									return x.getName();
								}
							});

					SQLQuery vSQLQuery =
							this.getActiveSession().createSQLQuery(
									"select cvterm_id from cvtermprop where type_id = 1800 and value in (:variableTypeNames)");
					vSQLQuery.setParameterList("variableTypeNames", variableTypeNames);
					List queryResults = vSQLQuery.list();

					if (!listParameters.containsKey("variableIds")) {
						filterClause += " and v.cvterm_id in (:variableIds)";
						listParameters.put("variableIds", variableFilter.getVariableIds());
					}

					List<Integer> variableIds = listParameters.get("variableIds");
					for (Object row : queryResults) {
						variableIds.add(this.typeSafeObjectToInteger(row));
					}

					// Filtering with variable types that is not used or invalid. So no further iteration required.
					if (variableIds.isEmpty()) {
						return new ArrayList<>();
					}
				}
			}

			// this query will get variables using filter
			SQLQuery query =
					this.getActiveSession()
					.createSQLQuery(
							"select v.cvterm_id vid, v.name vn, v.definition vd, vmr.mid, vmr.mn, vmr.md, vpr.pid, vpr.pn, vpr.pd, vsr.sid, vsr.sn, vsr.sd, vo.alias g_alias, vo.expected_min g_min_value, vo.expected_max g_max_value, vpo.alias p_alias, vpo.expected_min p_min_value, vpo.expected_max p_max_value, pf.id fid from cvterm v "
									+ "left join (select mr.subject_id vid, m.cvterm_id mid, m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and mr.type_id = 1210) vmr on vmr.vid = v.cvterm_id "
									+ "left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = 1200) vpr on vpr.vid = v.cvterm_id "
									+ "left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = 1220) vsr on vsr.vid = v.cvterm_id "
									+ "left join variable_overrides vo on vo.cvterm_id = v.cvterm_id and vo.program_uuid is null "
									+ "left join variable_overrides vpo on vpo.cvterm_id = v.cvterm_id and vpo.program_uuid = :programUuid "
									+ "left join program_favorites pf on pf.entity_id = v.cvterm_id and pf.program_uuid = :programUuid and pf.entity_type = 'VARIABLES' "
									+ "WHERE (v.cv_id = 1040) " + filterClause).addScalar("vid").addScalar("vn").addScalar("vd")
									.addScalar("pid").addScalar("pn").addScalar("pd").addScalar("mid").addScalar("mn").addScalar("md")
									.addScalar("sid").addScalar("sn").addScalar("sd").addScalar("g_alias").addScalar("g_min_value")
									.addScalar("g_max_value").addScalar("p_alias").addScalar("p_min_value").addScalar("p_max_value")
									.addScalar("fid");

			query.setParameter("programUuid", variableFilter.getProgramUuid());

			// get data from parameter map and apply parameter to query
			for (String lp : listParameters.keySet()) {
				query.setParameterList(lp, listParameters.get(lp));
			}

			Map<Integer, Method> mMap = new HashMap<>();
			Map<Integer, Property> pMap = new HashMap<>();
			Map<Integer, Scale> sMap = new HashMap<>();

			List queryResults = query.list();

			// get result output of query and store related data to variable (expected min/max, alias, method, scale, property, variable
			// term data)
			for (Object row : queryResults) {
				Object[] items = (Object[]) row;
				Variable variable = new Variable(new Term(this.typeSafeObjectToInteger(items[0]), (String) items[1], (String) items[2]));

				Integer propertyId = this.typeSafeObjectToInteger(items[3]);
				if (!pMap.containsKey(propertyId)) {
					pMap.put(propertyId, new Property(
							new Term(this.typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5])));
				}
				variable.setProperty(pMap.get(propertyId));

				Integer methodId = this.typeSafeObjectToInteger(items[6]);
				if (!mMap.containsKey(methodId)) {
					mMap.put(methodId, new Method(new Term(this.typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8])));
				}
				variable.setMethod(mMap.get(methodId));

				Integer scaleId = this.typeSafeObjectToInteger(items[9]);
				if (!sMap.containsKey(scaleId)) {
					sMap.put(scaleId, new Scale(new Term(this.typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11])));
				}

				variable.setScale(sMap.get(scaleId));

				// Alias, Expected Min Value, Expected Max Value
				String gAlias = (String) items[12];
				String gExpMin = (String) items[13];
				String gExpMax = (String) items[14];
				String pAlias = (String) items[15];
				String pExpMin = (String) items[16];
				String pExpMax = (String) items[17];

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
			SQLQuery rQuery =
					this.getActiveSession()
					.createSQLQuery(
							"select tr.subject_id sid, tr.type_id tid, tr.object_id rid, t.name rn, t.definition rd from cvterm_relationship tr inner join cvterm t on t.cvterm_id = tr.object_id "
									+ "where tr.subject_id in (:propertyIds) or tr.subject_id in (:scaleIds)").addScalar("sid")
									.addScalar("tid").addScalar("rid").addScalar("rn").addScalar("rd");

			rQuery.setParameterList("propertyIds", pMap.keySet());
			rQuery.setParameterList("scaleIds", sMap.keySet());

			List rQueryResults = rQuery.list();

			for (Object row : rQueryResults) {
				Object[] items = (Object[]) row;

				Integer subjectId = this.typeSafeObjectToInteger(items[0]);
				Integer typeId = this.typeSafeObjectToInteger(items[1]);
				Integer objectId = this.typeSafeObjectToInteger(items[2]);

				String name = (String) items[3];
				String description = (String) items[4];

				if (Objects.equals(typeId, TermId.IS_A.getId())) {
					pMap.get(subjectId).addClass(name);
				} else if (Objects.equals(typeId, TermId.HAS_TYPE.getId())) {
					sMap.get(subjectId).setDataType(DataType.getById(objectId));
				} else if (Objects.equals(typeId, TermId.HAS_VALUE.getId())) {
					sMap.get(subjectId).addCategory(new TermSummary(objectId, name, description));
				}
			}

			// Fetch Property CropOntologyId, Scale min-max, Variable Type, Creation and Last Modified date of all terms
			SQLQuery pQuery =
					this.getActiveSession()
					.createSQLQuery(
							"select t.cvterm_id tid, t.cv_id cvid, tp.type_id typeid, tp.value value from cvtermprop tp "
									+ "inner join cvterm t on t.cvterm_id = tp.cvterm_id "
									+ "WHERE tp.cvterm_id in(:methodIds) or tp.cvterm_id in(:propertyIds) or tp.cvterm_id in(:scaleIds) or tp.cvterm_id in(:variableIds)")
									.addScalar("tid").addScalar("cvid").addScalar("typeid").addScalar("value");

			// set parameter to query
			pQuery.setParameterList("methodIds", mMap.keySet());
			pQuery.setParameterList("propertyIds", pMap.keySet());
			pQuery.setParameterList("scaleIds", sMap.keySet());
			pQuery.setParameterList("variableIds", map.keySet());

			List pQueryResults = pQuery.list();

			// fetch data from results and add data to related terms
			for (Object row : pQueryResults) {
				Object[] items = (Object[]) row;

				Integer cvTermId = this.typeSafeObjectToInteger(items[0]);
				Integer cvId = this.typeSafeObjectToInteger(items[1]);
				Integer typeId = this.typeSafeObjectToInteger(items[2]);
				String value = (String) items[3];

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

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getAllVariables", e);
		}

		List<Variable> variables = new ArrayList<>(map.values());

		// sort variable list by variable name
		Collections.sort(variables, new Comparator<Variable>() {

			@Override
			public int compare(Variable l, Variable r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return variables;
	}

	@Override
	public Variable getVariable(String programUuid, Integer id, boolean filterObsolete, boolean calculateVariableUsage) {

		Variable cachedVariable = variableCache.get(id);
		if (cachedVariable != null) {
			LOG.debug("Variable for id [{}] found in cahce, returning the cached value.", id);
			return cachedVariable;
		}

		Monitor monitor = MonitorFactory.start("Get Variable");				
		try {

			// Fetch term from db
			CVTerm term = this.getCvTermDao().getById(id);

			this.checkTermIsVariable(term);

			Variable variable = new Variable(Term.fromCVTerm(term));

			// load scale, method and property data
			List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(term.getCvTermId());
			for (CVTermRelationship r : relationships) {
				if (Objects.equals(r.getTypeId(), TermId.HAS_METHOD.getId())) {
					variable.setMethod(this.methodManager.getMethod(r.getObjectId(), filterObsolete));
				} else if (Objects.equals(r.getTypeId(), TermId.HAS_PROPERTY.getId())) {
					variable.setProperty(this.propertyManager.getProperty(r.getObjectId(), filterObsolete));
				} else if (Objects.equals(r.getTypeId(), TermId.HAS_SCALE.getId())) {
					variable.setScale(this.scaleManager.getScaleById(r.getObjectId(), filterObsolete));
				}
			}

			// Variable Types, Created, modified from CVTermProperty
			List properties = this.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());

			for (Object p : properties) {
				CVTermProperty property = (CVTermProperty) p;

				if (Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())) {
					variable.addVariableType(VariableType.getByName(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
					variable.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					variable.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				}
			}

			// Variable alias and expected range
			VariableOverrides overrides = this.getVariableProgramOverridesDao().getByVariableAndProgram(id, programUuid);

			if (overrides != null) {
				variable.setAlias(overrides.getAlias());
				variable.setMinValue(overrides.getExpectedMin());
				variable.setMaxValue(overrides.getExpectedMax());
			}

			// Get favorite from ProgramFavoriteDAO
			ProgramFavorite programFavorite =
					this.getProgramFavoriteDao().getProgramFavorite(programUuid, ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());
			variable.setIsFavorite(programFavorite != null);

			if(calculateVariableUsage) {
				// setting variable studies
				variable.setStudies((int) this.getDmsProjectDao().countByVariable(id));

				// setting variable observations, first observations will be null so set it to 0
				variable.setObservations(0);
				for (VariableType v : variable.getVariableTypes()) {
					long observation = this.getExperimentDao().countByObservedVariable(id, v.getId());
					variable.setObservations((int) (variable.getObservations() + observation));
				}
			} else {
				final int unknownUsage = -1;
				variable.setStudies(unknownUsage);
				variable.setObservations(unknownUsage);

			}
			
			variableCache.put(id, variable);
			
			return variable;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getVariable", e);
		} finally {
			LOG.debug("" + monitor.stop() + ". This instance was for variable id: " + id);
		}
	}

	@Override
	public void processTreatmentFactorHasPairValue(List<Variable> summaryList, List<Integer> hiddenFields) {
		for (Variable variable : summaryList) {
			variable.setHasPair(this.getCvTermDao().hasPossibleTreatmentPairs(variable.getId(), variable.getProperty().getId(),
					hiddenFields));
		}
	}

	@Override
	public void addVariable(OntologyVariableInfo variableInfo) {

		CVTerm term = this.getCvTermDao().getByNameAndCvId(variableInfo.getName(), CvId.VARIABLES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_EXIST_WITH_SAME_NAME);
		}

		try {
			// Saving term to database.
			CVTerm savedTerm = this.getCvTermDao().save(variableInfo.getName(), variableInfo.getDescription(), CvId.VARIABLES);
			variableInfo.setId(savedTerm.getCvTermId());

			// Setting method to variable
			if (variableInfo.getMethodId() != null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(),
						variableInfo.getMethodId());
			}

			// Setting property to variable
			if (variableInfo.getPropertyId() != null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(),
						variableInfo.getPropertyId());
			}

			// Setting scale to variable
			if (variableInfo.getScaleId() != null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
			}

			int rank = 0;
			for (VariableType type : variableInfo.getVariableTypes()) {
				CVTermProperty property = new CVTermProperty();
				property.setCvTermId(variableInfo.getId());
				property.setTypeId(TermId.VARIABLE_TYPE.getId());
				property.setValue(type.getName());
				property.setRank(rank++);
				this.getCvTermPropertyDao().save(property);
			}

			// Saving min max values
			if (variableInfo.getExpectedMin() != null || variableInfo.getExpectedMax() != null) {
				this.getVariableProgramOverridesDao().save(variableInfo.getId(), variableInfo.getProgramUuid(), null,
						variableInfo.getExpectedMin(), variableInfo.getExpectedMax());
			}

			// Saving favorite
			if (variableInfo.isFavorite() != null && variableInfo.isFavorite()) {
				ProgramFavorite programFavorite = new ProgramFavorite();
				programFavorite.setEntityId(variableInfo.getId());
				programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
				programFavorite.setUniqueID(variableInfo.getProgramUuid());
				this.getProgramFavoriteDao().save(programFavorite);
			}

			// Setting last update time.
			this.getCvTermPropertyDao().save(variableInfo.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);


		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at addVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public void updateVariable(OntologyVariableInfo variableInfo) {

		variableCache.remove(variableInfo.getId());
		
		VariableInfoDaoElements elements = new VariableInfoDaoElements();
		elements.setVariableId(variableInfo.getId());
		elements.setProgramUuid(variableInfo.getProgramUuid());

		this.fillDaoElementsAndCheckForUsage(elements);

		CVTerm term = elements.getVariableTerm();

		this.checkTermIsVariable(term);

		CVTermRelationship methodRelation = elements.getMethodRelation();
		CVTermRelationship propertyRelation = elements.getPropertyRelation();
		CVTermRelationship scaleRelation = elements.getScaleRelation();
		List<CVTermProperty> termProperties = elements.getTermProperties();
		VariableOverrides variableOverrides = elements.getVariableOverrides();


		try {

			// Updating term to database.
			if (!(variableInfo.getName().equals(term.getName()) && Objects.equals(variableInfo.getDescription(), term.getDefinition()))) {
				term.setName(variableInfo.getName());
				term.setDefinition(variableInfo.getDescription());
				this.getCvTermDao().merge(term);
			}

			// Setting method to variable
			if (methodRelation == null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_METHOD.getId(),
						variableInfo.getMethodId());
			} else if (!Objects.equals(methodRelation.getObjectId(), variableInfo.getMethodId())) {
				methodRelation.setObjectId(variableInfo.getMethodId());
				this.getCvTermRelationshipDao().merge(methodRelation);
			}

			// Setting property to variable
			if (propertyRelation == null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_PROPERTY.getId(),
						variableInfo.getPropertyId());
			} else if (!Objects.equals(propertyRelation.getObjectId(), variableInfo.getPropertyId())) {
				propertyRelation.setObjectId(variableInfo.getPropertyId());
				this.getCvTermRelationshipDao().merge(propertyRelation);
			}

			// Setting scale to variable
			if (scaleRelation == null) {
				this.getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationshipId.HAS_SCALE.getId(), variableInfo.getScaleId());
			} else if (!Objects.equals(scaleRelation.getObjectId(), variableInfo.getScaleId())) {
				scaleRelation.setObjectId(variableInfo.getScaleId());
				this.getCvTermRelationshipDao().merge(scaleRelation);
			}

			// Updating variable types
			Map<VariableType, CVTermProperty> existingProperties = new HashMap<>();
			Set<VariableType> existingVariableTypes = new HashSet<>();

			// Variable Types from CVTermProperty
			for (CVTermProperty property : termProperties) {
				if (Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())) {
					VariableType type = VariableType.getByName(property.getValue());
					existingVariableTypes.add(type);
					existingProperties.put(type, property);
				}
			}

			int rank = 0;
			for (VariableType type : variableInfo.getVariableTypes()) {

				// skip existing
				if (existingVariableTypes.contains(type)) {
					continue;
				}

				CVTermProperty property = new CVTermProperty();
				property.setCvTermId(variableInfo.getId());
				property.setTypeId(TermId.VARIABLE_TYPE.getId());
				property.setValue(type.getName());
				property.setRank(rank++);
				this.getCvTermPropertyDao().save(property);
			}

			// Remove variable type properties which are not part of incoming set.
			Set<VariableType> toRemove = new HashSet<>(existingVariableTypes);
			toRemove.removeAll(variableInfo.getVariableTypes());

			for (VariableType type : toRemove) {
				this.getCvTermPropertyDao().makeTransient(existingProperties.get(type));
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
			ProgramFavorite programFavorite =
					this.getProgramFavoriteDao().getProgramFavorite(variableInfo.getProgramUuid(), ProgramFavorite.FavoriteType.VARIABLE,
							term.getCvTermId());
			boolean isFavorite = variableInfo.isFavorite() || !Strings.isNullOrEmpty(variableInfo.getAlias());

			if (isFavorite && programFavorite == null) {
				programFavorite = new ProgramFavorite();
				programFavorite.setEntityId(variableInfo.getId());
				programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
				programFavorite.setUniqueID(variableInfo.getProgramUuid());
				this.getProgramFavoriteDao().save(programFavorite);
			} else if (!isFavorite && programFavorite != null) {
				this.getProgramFavoriteDao().makeTransient(programFavorite);
			}

			this.getCvTermPropertyDao().save(variableInfo.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()),
					0);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public void deleteVariable(Integer id) {
		
		variableCache.remove(id);
		
		CVTerm term = this.getCvTermDao().getById(id);

		this.checkTermIsVariable(term);

		// check usage
		Integer usage = this.getVariableObservations(id);

		if (usage > 0) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.CAN_NOT_DELETE_USED_VARIABLE);
		}

		try {

			// Delete relationships
			List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(id);
			for (CVTermRelationship relationship : relationships) {
				this.getCvTermRelationshipDao().makeTransient(relationship);
			}

			// delete properties
			List<CVTermProperty> properties = this.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());
			for (CVTermProperty property : properties) {
				this.getCvTermPropertyDao().makeTransient(property);
			}

			// delete Variable alias and expected range
			List<VariableOverrides> variableOverridesList = this.getVariableProgramOverridesDao().getByVariableId(id);

			for (VariableOverrides overrides : variableOverridesList) {
				this.getVariableProgramOverridesDao().makeTransient(overrides);
			}

			// delete main entity
			this.getCvTermDao().makeTransient(term);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public Integer getVariableObservations(int variableId) {

		final String numOfProjectsWithVariable =
				"SELECT count(pp.project_id) " + " FROM projectprop pp " + " WHERE NOT EXISTS( " + " SELECT 1 FROM projectprop stat "
						+ " WHERE stat.project_id = pp.project_id " + " AND stat.type_id = " + TermId.STUDY_STATUS.getId()
						+ " AND value = " + TermId.DELETED_STUDY.getId() + ") " + " AND pp.type_id = " + TermId.STANDARD_VARIABLE.getId()
						+ " AND pp.value = :variableId";

		SQLQuery query = this.getActiveSession().createSQLQuery(numOfProjectsWithVariable);
		query.setParameter("variableId", variableId);
		return ((BigInteger) query.uniqueResult()).intValue();
	}

	// TODO: Follow DmsProjectDao countExperimentByVariable. This requires STORED_IN and that needs to deprecated.
	@Override
	public Integer getVariableStudies(int variableId) {
		return 0;
	}

	private void checkTermIsVariable(CVTerm term) {

		if (term == null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.VARIABLES.getId()) {
			throw new MiddlewareException(MessageFormat.format(OntologyVariableDataManagerImpl.TERM_IS_NOT_VARIABLE, term.getName()));
		}
	}

	private void fillDaoElementsAndCheckForUsage(VariableInfoDaoElements elements) {

		// check required elements
		Util.checkAndThrowForNullObjects(elements.getVariableId());

		// Fetch term from db
		CVTerm variableTerm = this.getCvTermDao().getById(elements.getVariableId());

		this.checkTermIsVariable(variableTerm);

		CVTermRelationship methodRelation = null;
		CVTermRelationship propertyRelation = null;
		CVTermRelationship scaleRelation = null;

		// load scale, method and property data
		List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(variableTerm.getCvTermId());
		for (CVTermRelationship relationship : relationships) {
			if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_METHOD.getId())) {
				methodRelation = relationship;
			} else if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_PROPERTY.getId())) {
				propertyRelation = relationship;
			} else if (Objects.equals(relationship.getTypeId(), TermRelationshipId.HAS_SCALE.getId())) {
				scaleRelation = relationship;
			}
		}

		// Variable Types from CVTermProperty
		List<CVTermProperty> termProperties = this.getCvTermPropertyDao().getByCvTermId(elements.getVariableId());

		VariableOverrides variableOverrides =
				this.getVariableProgramOverridesDao().getByVariableAndProgram(elements.getVariableId(), elements.getProgramUuid());

		// Set to elements to send response back to caller.
		elements.setVariableTerm(variableTerm);
		elements.setMethodRelation(methodRelation);
		elements.setPropertyRelation(propertyRelation);
		elements.setScaleRelation(scaleRelation);
		elements.setTermProperties(termProperties);
		elements.setVariableOverrides(variableOverrides);
	}
}
