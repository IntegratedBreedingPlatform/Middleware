
package org.generationcp.middleware.manager.ontology;

import java.math.BigInteger;
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

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.oms.OntologyVariableInfo;
import org.generationcp.middleware.domain.ontology.OntologyVariableSummary;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.oms.VariableType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableInfoDaoElements;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.VariableProgramOverrides;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements {@link OntologyVariableDataManagerImpl}
 */

public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

	private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
	private static final String TERM_IS_NOT_VARIABLE = "Term is not Variable";
	private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";
	private static final String CAN_NOT_DELETE_USED_VARIABLE = "Used variable can not be deleted";

	private final OntologyMethodDataManager methodDataManager;
	private final OntologyPropertyDataManager propertyDataManager;
	private final OntologyScaleDataManager scaleDataManager;

	public OntologyVariableDataManagerImpl(OntologyMethodDataManager methodDataManager, OntologyPropertyDataManager propertyDataManager,
			OntologyScaleDataManager scaleDataManager, HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.methodDataManager = methodDataManager;
		this.propertyDataManager = propertyDataManager;
		this.scaleDataManager = scaleDataManager;
	}

	@Override
	public List<OntologyVariableSummary> getWithFilter(String programUuid, Boolean favorites, Integer methodId, Integer propertyId,
			Integer scaleId) throws MiddlewareException {

		String filterClause = "";

		if (!Objects.equals(methodId, null)) {
			filterClause += " and vmr.mid = :methodId ";
		}

		if (!Objects.equals(propertyId, null)) {
			filterClause += " and vpr.pid = :propertyId ";
		}

		if (!Objects.equals(scaleId, null)) {
			filterClause += " and vsr.sid = :scaleId ";
		}

		if (!Objects.equals(favorites, null)) {
			if (favorites) {
				filterClause += "  and pf.id is not null ";
			} else {
				filterClause += "  and pf.id is null ";
			}
		}

		Map<Integer, OntologyVariableSummary> map = new HashMap<>();

		try {
			SQLQuery query =
					this.getActiveSession()
							.createSQLQuery(
									"select v.cvterm_id vid, v.name vn, v.definition vd, vmr.mid, vmr.mn, vmr.md, vpr.pid, vpr.pn, vpr.pd, vsr.sid, vsr.sn, vsr.sd, dsr.did, dsr.dn, dsr.dd, vpo.alias, vpo.min_value, vpo.max_value, pf.id fid from cvterm v "
											+ "left join (select mr.subject_id vid, m.cvterm_id mid, m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and mr.type_id = 1210) vmr on vmr.vid = v.cvterm_id "
											+ "left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = 1200) vpr on vpr.vid = v.cvterm_id "
											+ "left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = 1220) vsr on vsr.vid = v.cvterm_id "
											+ "left join (select dr.subject_id sid, d.cvterm_id did, d.name dn, d.definition dd from cvterm_relationship dr inner join cvterm d on d.cvterm_id = dr.object_id and dr.type_id = 1105) dsr on dsr.sid = vsr.sid "
											+ "left join variable_program_overrides vpo on vpo.cvterm_id = v.cvterm_id "
											+ "left join program_favorites pf on pf.entity_id = v.cvterm_id and pf.program_uuid = :programUuid and pf.entity_type = 'VARIABLES'"
											+ "    WHERE (v.cv_id = 1040) " + filterClause + " ORDER BY v.cvterm_id").addScalar("vid")
							.addScalar("vn").addScalar("vd").addScalar("pid").addScalar("pn").addScalar("pd").addScalar("mid")
							.addScalar("mn").addScalar("md").addScalar("sid").addScalar("sn").addScalar("sd").addScalar("did")
							.addScalar("dn").addScalar("dd").addScalar("alias").addScalar("min_value").addScalar("max_value")
							.addScalar("fid");

			query.setParameter("programUuid", programUuid);

			if (!Objects.equals(methodId, null)) {
				query.setParameter("methodId", methodId);
			}

			if (!Objects.equals(propertyId, null)) {
				query.setParameter("propertyId", propertyId);
			}

			if (!Objects.equals(scaleId, null)) {
				query.setParameter("scaleId", scaleId);
			}

			List queryResults = query.list();

			for (Object row : queryResults) {
				Object[] items = (Object[]) row;
				OntologyVariableSummary variable =
						new OntologyVariableSummary(this.typeSafeObjectToInteger(items[0]), (String) items[1], (String) items[2]);
				variable.setPropertySummary(TermSummary.createNonEmpty(this.typeSafeObjectToInteger(items[3]), (String) items[4],
						(String) items[5]));
				variable.setMethodSummary(TermSummary.createNonEmpty(this.typeSafeObjectToInteger(items[6]), (String) items[7],
						(String) items[8]));
				variable.setScaleSummary(new Scale(new Term(this.typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11])));
				variable.getScaleSummary().setDataType(DataType.getById(this.typeSafeObjectToInteger(items[12])));
				variable.setAlias((String) items[15]);
				variable.setMinValue((String) items[16]);
				variable.setMaxValue((String) items[17]);
				variable.setIsFavorite(items[18] != null);
				map.put(variable.getId(), variable);
			}

			// Variable Types, Created, modified from CVTermProperty
			List properties = this.getCvTermPropertyDao().getByCvTermIds(new ArrayList<>(map.keySet()));
			for (Object p : properties) {
				CVTermProperty property = (CVTermProperty) p;

				OntologyVariableSummary variableSummary = map.get(property.getCvTermId());

				if (variableSummary == null) {
					continue;
				}

				if (Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())) {
					variableSummary.addVariableType(VariableType.getByName(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
					variableSummary.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					variableSummary.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				}
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getVariables", e);
		}

		List<OntologyVariableSummary> variables = new ArrayList<>(map.values());

		Collections.sort(variables, new Comparator<OntologyVariableSummary>() {

			@Override
			public int compare(OntologyVariableSummary l, OntologyVariableSummary r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return variables;
	}

	@Override
	public Variable getVariable(String programUuid, Integer id) throws MiddlewareException {

		try {

			// Fetch term from db
			CVTerm term = this.getCvTermDao().getById(id);

			this.checkTermIsVariable(term);

			try {

				Variable variable = new Variable(Term.fromCVTerm(term));

				// load scale, method and property data
				List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(term.getCvTermId());
				for (CVTermRelationship r : relationships) {
					if (Objects.equals(r.getTypeId(), TermId.HAS_METHOD.getId())) {
						variable.setMethod(this.methodDataManager.getMethod(r.getObjectId()));
					} else if (Objects.equals(r.getTypeId(), TermId.HAS_PROPERTY.getId())) {
						variable.setProperty(this.propertyDataManager.getProperty(r.getObjectId()));
					} else if (Objects.equals(r.getTypeId(), TermId.HAS_SCALE.getId())) {
						variable.setScale(this.scaleDataManager.getScaleById(r.getObjectId()));
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
				VariableProgramOverrides overrides = this.getVariableProgramOverridesDao().getByVariableAndProgram(id, programUuid);

				if (overrides != null) {
					variable.setAlias(overrides.getAlias());
					variable.setMinValue(overrides.getMinValue());
					variable.setMaxValue(overrides.getMaxValue());
				}

				// Get favorite from ProgramFavoriteDAO
				ProgramFavorite programFavorite =
						this.getProgramFavoriteDao().getProgramFavorite(programUuid, ProgramFavorite.FavoriteType.VARIABLE,
								term.getCvTermId());
				variable.setIsFavorite(programFavorite != null);

				variable.setObservations(this.getVariableObservations(id));

				return variable;

			} catch (HibernateException e) {
				throw new MiddlewareQueryException("Error in getVariable", e);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getting standard variable summaries from standard_variable_summary view", e);
		}
	}

	@Override
	public void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getByNameAndCvId(variableInfo.getName(), CvId.VARIABLES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_EXIST_WITH_SAME_NAME);
		}

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
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
				property.setCvTermPropertyId(this.getCvTermPropertyDao().getNextId(CVTermProperty.ID_NAME));
				property.setCvTermId(variableInfo.getId());
				property.setTypeId(TermId.VARIABLE_TYPE.getId());
				property.setValue(type.getName());
				property.setRank(rank++);
				this.getCvTermPropertyDao().save(property);
			}

			// Saving min max values
			if (variableInfo.getMinValue() != null || variableInfo.getMaxValue() != null) {
				this.getVariableProgramOverridesDao().save(variableInfo.getId(), variableInfo.getProgramUuid(), null,
						variableInfo.getMinValue(), variableInfo.getMaxValue());
			}

			// Saving favorite
			if (variableInfo.isFavorite()) {
				ProgramFavorite programFavorite = new ProgramFavorite();
				programFavorite.setProgramFavoriteId(this.getProgramFavoriteDao().getNextId(ProgramFavorite.ID_NAME));
				programFavorite.setEntityId(variableInfo.getId());
				programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
				programFavorite.setUniqueID(variableInfo.getProgramUuid());
				this.getProgramFavoriteDao().save(programFavorite);
			}

			// Setting last update time.
			this.getCvTermPropertyDao().save(variableInfo.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at addVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {

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
		VariableProgramOverrides variableProgramOverrides = elements.getVariableProgramOverrides();

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

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
				int nextId = this.getCvTermPropertyDao().getNextId(CVTermProperty.ID_NAME);
				property.setCvTermPropertyId(nextId);
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
			if (variableInfo.getAlias() != null || variableInfo.getMinValue() != null || variableInfo.getMaxValue() != null) {
				this.getVariableProgramOverridesDao().save(variableInfo.getId(), variableInfo.getProgramUuid(), variableInfo.getAlias(),
						variableInfo.getMinValue(), variableInfo.getMaxValue());
			} else if (variableProgramOverrides != null) {
				this.getVariableProgramOverridesDao().makeTransient(variableProgramOverrides);
			}

			// Updating favorite to true if alias is defined
			ProgramFavorite programFavorite =
					this.getProgramFavoriteDao().getProgramFavorite(variableInfo.getProgramUuid(), ProgramFavorite.FavoriteType.VARIABLE,
							term.getCvTermId());
			boolean isFavorite = variableInfo.isFavorite() || variableInfo.getAlias() != null;

			if (isFavorite && programFavorite == null) {
				programFavorite = new ProgramFavorite();
				programFavorite.setProgramFavoriteId(this.getProgramFavoriteDao().getNextId(ProgramFavorite.ID_NAME));
				programFavorite.setEntityId(variableInfo.getId());
				programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
				programFavorite.setUniqueID(variableInfo.getProgramUuid());
				this.getProgramFavoriteDao().save(programFavorite);
			} else if (!isFavorite && programFavorite != null) {
				this.getProgramFavoriteDao().makeTransient(programFavorite);
			}

			this.getCvTermPropertyDao().save(variableInfo.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()),
					0);

			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
	}

	@Override
	public void deleteVariable(Integer id) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(id);

		this.checkTermIsVariable(term);

		// check usage
		Integer usage = this.getVariableObservations(id);

		if (usage > 0) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.CAN_NOT_DELETE_USED_VARIABLE);
		}

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

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
			List<VariableProgramOverrides> variableProgramOverridesList = this.getVariableProgramOverridesDao().getByVariableId(id);

			for (VariableProgramOverrides overrides : variableProgramOverridesList) {
				this.getVariableProgramOverridesDao().makeTransient(overrides);
			}

			// delete main entity
			this.getCvTermDao().makeTransient(term);

			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
		}
	}

	// TODO: Follow DmsProjectDao query COUNT_PROJECTS_WITH_VARIABLE.
	@Override
	public Integer getVariableObservations(int variableId) throws MiddlewareException {

		// Note: Taken from {@link DmsProjectDao.countByVariable()}
		final String COUNT_PROJECTS_WITH_VARIABLE =
				"SELECT count(pp.project_id) " + " FROM projectprop pp " + " WHERE NOT EXISTS( " + " SELECT 1 FROM projectprop stat "
						+ " WHERE stat.project_id = pp.project_id " + " AND stat.type_id = " + TermId.STUDY_STATUS.getId()
						+ " AND value = " + TermId.DELETED_STUDY.getId() + ") " + " AND pp.type_id = " + TermId.STANDARD_VARIABLE.getId()
						+ " AND pp.value = :variableId";

		SQLQuery query = this.getActiveSession().createSQLQuery(COUNT_PROJECTS_WITH_VARIABLE);
		query.setParameter("variableId", variableId);
		return ((BigInteger) query.uniqueResult()).intValue();
	}

	// TODO: Follow DmsProjectDao countExperimentByVariable. This requires STORED_IN and that needs to deprecated.
	@Override
	public Integer getVariableStudies(int variableId) throws MiddlewareException {
		return 0;
	}

	private void checkTermIsVariable(CVTerm term) throws MiddlewareException {

		if (term == null) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.VARIABLE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.VARIABLES.getId()) {
			throw new MiddlewareException(OntologyVariableDataManagerImpl.TERM_IS_NOT_VARIABLE);
		}
	}

	private void fillDaoElementsAndCheckForUsage(VariableInfoDaoElements elements) throws MiddlewareException {

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

		VariableProgramOverrides variableProgramOverrides =
				this.getVariableProgramOverridesDao().getByVariableAndProgram(elements.getVariableId(), elements.getProgramUuid());

		// Set to elements to send response back to caller.
		elements.setVariableTerm(variableTerm);
		elements.setMethodRelation(methodRelation);
		elements.setPropertyRelation(propertyRelation);
		elements.setScaleRelation(scaleRelation);
		elements.setTermProperties(termProperties);
		elements.setVariableProgramOverrides(variableProgramOverrides);
	}
}
