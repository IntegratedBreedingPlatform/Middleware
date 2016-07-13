package org.generationcp.middleware.manager.ontology;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.ObjectTypeMapper;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

@SuppressWarnings("unchecked")
@Transactional
public class OntologyCommonDAOImpl implements OntologyCommonDAO {

	public final String SHOULD_NOT_OBSOLETE = "is_obsolete = 0";

	private String filterClause = "";

	private HibernateSessionProvider sessionProvider;

	private OntologyDaoFactory ontologyDaoFactory;

	public OntologyCommonDAOImpl() {
		super();
	}

	public void setSessionProvider(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.ontologyDaoFactory = new OntologyDaoFactory();
		this.ontologyDaoFactory.setSessionProvider(sessionProvider);
	}

	public List<Integer> getAllPropertyIdsWithClassAndVariableType(final String[] classes, final String[] variableTypes) throws MiddlewareException {

		boolean shouldFilterByClass = classes != null && classes.length > 0;

		String classFilter = shouldFilterByClass  ? "and p.cvterm_id in (select pt.subject_id from cvterm_relationship pt "
				+ "inner join cvterm t on t.cvterm_id = pt.object_id  and t.name in (:classes) where pt.type_id = :isA)" : "";

		boolean shouldFilterByVariableType = variableTypes != null && variableTypes.length > 0;

		String variableTypeFilter = shouldFilterByVariableType ? "and p.cvterm_id in (select vpr.object_id from cvterm_relationship vpr "
				+ "inner join cvtermprop vp on vp.cvterm_id = vpr.subject_id and vp.value in (:variableTypes) and vp.type_id = :variableType)" : "" ;

		SQLQuery query = sessionProvider.getSession().createSQLQuery(
				"select DISTINCT p.cvterm_id from cvterm p where p.cv_id = :propertyTerm " + classFilter + variableTypeFilter);

		query.setParameter("propertyTerm", CvId.PROPERTIES.getId());

		if (shouldFilterByClass) {
			query.setParameterList("classes", classes);
			query.setParameter("isA", TermId.IS_A.getId());
		}

		if (shouldFilterByVariableType) {
			query.setParameter("variableType", TermId.VARIABLE_TYPE.getId());
			query.setParameterList("variableTypes", variableTypes);
		}

		return query.list();
	}

	public Map<Integer, Property> getPropertiesWithCropOntologyAndTraits(final Boolean fetchAll, List propertyIds, final boolean filterObsolete) throws MiddlewareException {

		Map<Integer, Property> map = new HashMap<>();

		if (propertyIds == null) {
			propertyIds = new ArrayList<>();
		}

		if (!fetchAll && propertyIds.isEmpty()) {
			return new HashMap<>();
		}

		String filterClause = "";
		if (!propertyIds.isEmpty()) {
			filterClause = " and p.cvterm_id in (:propertyIds)";
		}

		String filterObsoleteClause = "";
		if (filterObsolete) {
			filterObsoleteClause = " and p." + this.SHOULD_NOT_OBSOLETE;
		}

		List result;

		SQLQuery query = this.sessionProvider.getSession().createSQLQuery(
			"select p.cvterm_id pId, p.name pName, p.definition pDescription, p.cv_id pVocabularyId, p.is_obsolete pObsolete"
					+ ", tp.value cropOntologyId" + ", GROUP_CONCAT(cs.name SEPARATOR ',') AS classes" + "  from cvterm p"
					+ " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId()
					+ " LEFT JOIN (select cvtr.subject_id PropertyId, o.cv_id, o.cvterm_id, o.name, o.definition, o.is_obsolete "
					+ " from cvterm o inner join cvterm_relationship cvtr on cvtr.object_id = o.cvterm_id and cvtr.type_id = "
					+ TermId.IS_A.getId() + ")" + " cs on cs.PropertyId = p.cvterm_id" + " where p.cv_id = " + CvId.PROPERTIES
					.getId() + filterObsoleteClause + filterClause + " Group BY p.cvterm_id Order BY p.name ")
			.addScalar("pId", new org.hibernate.type.IntegerType()).addScalar("pName").addScalar("pDescription")
			.addScalar("pVocabularyId", new org.hibernate.type.IntegerType())
			.addScalar("pObsolete", new org.hibernate.type.IntegerType()).addScalar("cropOntologyId").addScalar("classes");

		if (!propertyIds.isEmpty()) {
			query.setParameterList("propertyIds", propertyIds);
		}

		result = query.list();

		for (Object row : result) {
			Object[] items = (Object[]) row;

			// Check is row does have objects to access
			if (items.length == 0) {
				continue;
			}

			// Check if Property term is already added to Map. We are iterating multiple classes for property
			Property property = new Property(new Term((Integer) items[0], (String) items[1], (String) items[2], (Integer) items[3],
					ObjectTypeMapper.mapToBoolean(items[4])));

			if (items[5] != null) {
				property.setCropOntologyId((String) items[5]);
			}

			if (items[6] != null) {
				String classes = (String) items[6];
				for (String c : classes.split(",")) {
					if (Strings.isNullOrEmpty(c)) {
						continue;
					}
					property.addClass(c.trim());
				}
			}

			map.put(property.getId(), property);
		}

		return map;
	}

	public Map<Integer, Scale> getScalesWithDataTypeAndProperties(final List<Integer> termIds, final Map<Integer, Scale> scaleMap, final Boolean filterObsolete) throws MiddlewareException {
		String filterObsoleteClause = "";
		if (filterObsolete) {
			filterObsoleteClause = "t.is_obsolete = 0 and";
		}

		SQLQuery query = this.sessionProvider.getSession().createSQLQuery(
				"select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where " + filterObsoleteClause
						+ " t.cv_id = " + CvId.SCALES.getId()).addEntity(CVTermProperty.class);

		List properties = query.list();

		for (Object p : properties) {
			CVTermProperty property = (CVTermProperty) p;
			Scale scale = scaleMap.get(property.getCvTermId());

			if (scale == null) {
				continue;
			}

			if (Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())) {
				scale.setMinValue(property.getValue());
			} else if (Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())) {
				scale.setMaxValue(property.getValue());
			} else if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
				scale.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
			} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
				scale.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
			}
		}

		query = this.sessionProvider.getSession().createSQLQuery(
				"SELECT r.subject_id, r.type_id, t.cv_id, t.cvterm_id, t.name, t.definition " + "FROM cvterm_relationship r inner join cvterm t on r.object_id = t.cvterm_id "
						+ "where r.subject_id in (:scaleIds)");

		query.setParameterList("scaleIds", termIds);

		List result = query.list();

		for (Object row : result) {
			Object[] items = (Object[]) row;

			Integer scaleId = (Integer) items[0];

			Scale scale = scaleMap.get(scaleId);

			if (scale == null) {
				continue;
			}

			if (Objects.equals(items[1], TermId.HAS_TYPE.getId())) {
				scale.setDataType(DataType.getById((Integer) items[3]));
			} else if (Objects.equals(items[1], TermId.HAS_VALUE.getId())) {
				scale.addCategory(new TermSummary((Integer) items[3], (String) items[4], (String) items[5]));
			}

			scaleMap.put(scale.getId(), scale);
		}

		return scaleMap;
	}

	public void makeFilterClauseByIsFavouritesMethodIdsAndPropertyIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException {
		// check to fetch favourite variables and add filter clause for it
		filterClause = "";
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
	}

	public void makeFilterClauseByVariableIdsAndExcludedVariableIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException {
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
	}

	public void makeFilterClauseByScaleIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException {
		if (!variableFilter.getScaleIds().isEmpty()) {
			filterClause += " and vsr.sid in (:scaleIds)";
			listParameters.put("scaleIds", variableFilter.getScaleIds());
		}
	}

	public List<Integer> getPropertyIdsAndAddToFilterClause(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException {

		SQLQuery pSQLQuery = this.ontologyDaoFactory.getActiveSession().createSQLQuery(
				"select subject_id from cvterm_relationship where type_id = " + TermId.IS_A.getId() + " and object_id in"
						+ " (select cvterm_id from cvterm where name in(:classNames) and cv_id = "
						+ CvId.TRAIT_CLASS.getId() + ");");

		pSQLQuery.setParameterList("classNames", variableFilter.getPropertyClasses());
		List queryResults = pSQLQuery.list();

		if (!listParameters.containsKey("propertyIds")) {
			filterClause += " and vpr.pid in (:propertyIds)";
			listParameters.put("propertyIds", variableFilter.getPropertyIds());
		}

		List<Integer> propertyIds = listParameters.get("propertyIds");
		for (Object row : queryResults) {
			propertyIds.add(HibernateUtil.typeSafeObjectToInteger(row));
		}

		return propertyIds;
	}

	public List<Integer> getScaleIdsAndAddToFilterClause(VariableFilter variableFilter, List<Integer> dataTypeIds,
			Map<String, List<Integer>> listParameters) throws MiddlewareException {

		SQLQuery sSQLQuery =
				this.ontologyDaoFactory.getActiveSession().createSQLQuery(
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
			scaleIds.add(HibernateUtil.typeSafeObjectToInteger(row));
		}

		return scaleIds;
	}

	public List<Integer> getVariableIdsAndAddToFilterClause(VariableFilter variableFilter, List<String> variableTypeNames,
			Map<String, List<Integer>> listParameters) throws MiddlewareException {

		List<Integer> variableIds;

		SQLQuery vSQLQuery = this.ontologyDaoFactory.getActiveSession()
				.createSQLQuery("select cvterm_id from cvtermprop where type_id = 1800 and value in (:variableTypeNames)");
		vSQLQuery.setParameterList("variableTypeNames", variableTypeNames);
		List queryResults = vSQLQuery.list();

		if (!listParameters.containsKey("variableIds")) {
			filterClause += " and v.cvterm_id in (:variableIds)";
			listParameters.put("variableIds", variableFilter.getVariableIds());
		}

		variableIds = listParameters.get("variableIds");
		for (Object row : queryResults) {
			variableIds.add(HibernateUtil.typeSafeObjectToInteger(row));
		}

		return variableIds;
	}

	public Map<Integer, Variable> fillVariableMapUsingFilterClause(VariableFilter variableFilter, Map<String, List<Integer>> listParameters,
			Map<Integer, Variable> variableMap, Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap,
			Map<Integer, Scale> scaleMap) throws MiddlewareException {

		// this query will get variables using filter
		SQLQuery query = this.ontologyDaoFactory.getActiveSession().createSQLQuery(
				"select v.cvterm_id vid, v.name vn, v.definition vd, vmr.mid, vmr.mn, vmr.md, vpr.pid, vpr.pn, vpr.pd, vsr.sid, vsr.sn, vsr.sd, "
						+ "vo.alias g_alias, vo.expected_min g_min_value, vo.expected_max g_max_value, vpo.alias p_alias, vpo.expected_min p_min_value, "
						+ "vpo.expected_max p_max_value, pf.id fid from cvterm v left join (select mr.subject_id vid, m.cvterm_id mid, "
						+ "m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and "
						+ "mr.type_id = 1210) vmr on vmr.vid = v.cvterm_id left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, "
						+ "p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = 1200) vpr "
						+ "on vpr.vid = v.cvterm_id left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from "
						+ "cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = 1220) vsr on vsr.vid = v.cvterm_id "
						+ "left join variable_overrides vo on vo.cvterm_id = v.cvterm_id and vo.program_uuid is null "
						+ "left join variable_overrides vpo on vpo.cvterm_id = v.cvterm_id and vpo.program_uuid = :programUuid "
						+ "left join program_favorites pf on pf.entity_id = v.cvterm_id and pf.program_uuid = :programUuid and pf.entity_type = 'VARIABLES' "
						+ "WHERE (v.cv_id = 1040) " + filterClause).addScalar("vid").addScalar("vn").addScalar("vd").addScalar("pid")
				.addScalar("pn").addScalar("pd").addScalar("mid").addScalar("mn").addScalar("md").addScalar("sid").addScalar("sn")
				.addScalar("sd").addScalar("g_alias").addScalar("g_min_value").addScalar("g_max_value").addScalar("p_alias")
				.addScalar("p_min_value").addScalar("p_max_value").addScalar("fid");

		query.setParameter("programUuid", variableFilter.getProgramUuid());

		// get data from parameter map and apply parameter to query
		for (String lp : listParameters.keySet()) {
			query.setParameterList(lp, listParameters.get(lp));
		}

		List queryResults = query.list();

		// get result output of query and store related data to variable (expected min/max, alias, method, scale, property, variable term data)
		for (Object row : queryResults) {
			Object[] items = (Object[]) row;
			Variable variable =
					new Variable(new Term(HibernateUtil.typeSafeObjectToInteger(items[0]), (String) items[1], (String) items[2]));

			Integer propertyId = HibernateUtil.typeSafeObjectToInteger(items[3]);
			if (!propertyMap.containsKey(propertyId)) {
				propertyMap.put(propertyId,
						new Property(new Term(HibernateUtil.typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5])));
			}
			variable.setProperty(propertyMap.get(propertyId));

			Integer methodId = HibernateUtil.typeSafeObjectToInteger(items[6]);
			if (!methodMap.containsKey(methodId)) {
				methodMap.put(methodId,
						new Method(new Term(HibernateUtil.typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8])));
			}
			variable.setMethod(methodMap.get(methodId));

			Integer scaleId = HibernateUtil.typeSafeObjectToInteger(items[9]);
			if (!scaleMap.containsKey(scaleId)) {
				scaleMap.put(scaleId,
						new Scale(new Term(HibernateUtil.typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11])));
			}

			variable.setScale(scaleMap.get(scaleId));

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
			variableMap.put(variable.getId(), variable);
		}
		return variableMap;
	}

	public void getVariableRelationships(Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap, Map<Integer, Scale> scaleMap) throws MiddlewareException {

		// Fetch Property Class, Data Type and Categories from cvterm_relationship
		SQLQuery rQuery =
				this.ontologyDaoFactory.getActiveSession()
						.createSQLQuery(
								"select tr.subject_id sid, tr.type_id tid, tr.object_id rid, t.name rn, t.definition rd from cvterm_relationship tr "
										+ "inner join cvterm t on t.cvterm_id = tr.object_id "
										+ "where tr.subject_id in (:propertyIds) or tr.subject_id in (:scaleIds)").addScalar("sid")
						.addScalar("tid").addScalar("rid").addScalar("rn").addScalar("rd");

		rQuery.setParameterList("propertyIds", propertyMap.keySet());
		rQuery.setParameterList("scaleIds", scaleMap.keySet());

		List rQueryResults = rQuery.list();

		for (Object row : rQueryResults) {
			Object[] items = (Object[]) row;

			Integer subjectId = HibernateUtil.typeSafeObjectToInteger(items[0]);
			Integer typeId = HibernateUtil.typeSafeObjectToInteger(items[1]);
			Integer objectId = HibernateUtil.typeSafeObjectToInteger(items[2]);

			String name = (String) items[3];
			String description = (String) items[4];

			if (Objects.equals(typeId, TermId.IS_A.getId())) {
				propertyMap.get(subjectId).addClass(name);
			} else if (Objects.equals(typeId, TermId.HAS_TYPE.getId())) {
				scaleMap.get(subjectId).setDataType(DataType.getById(objectId));
			} else if (Objects.equals(typeId, TermId.HAS_VALUE.getId())) {
				scaleMap.get(subjectId).addCategory(new TermSummary(objectId, name, description));
			}
		}
	}

	public void getVariableProperties(Map<Integer, Variable> map, Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap, Map<Integer, Scale> scaleMap)
			throws MiddlewareException {

		// Fetch Property CropOntologyId, Scale min-max, Variable Type, Creation and Last Modified date of all terms
		SQLQuery pQuery =
				this.ontologyDaoFactory.getActiveSession()
						.createSQLQuery(
								"select t.cvterm_id tid, t.cv_id cvid, tp.type_id typeid, tp.value value from cvtermprop tp "
										+ "inner join cvterm t on t.cvterm_id = tp.cvterm_id "
										+ "WHERE tp.cvterm_id in(:methodIds) or tp.cvterm_id in(:propertyIds) or tp.cvterm_id in(:scaleIds) or tp.cvterm_id in(:variableIds)")
						.addScalar("tid").addScalar("cvid").addScalar("typeid").addScalar("value");

		// set parameter to query
		pQuery.setParameterList("methodIds", methodMap.keySet());
		pQuery.setParameterList("propertyIds", propertyMap.keySet());
		pQuery.setParameterList("scaleIds", scaleMap.keySet());
		pQuery.setParameterList("variableIds", map.keySet());

		List pQueryResults = pQuery.list();

		// fetch data from results and add data to related terms
		for (Object row : pQueryResults) {
			Object[] items = (Object[]) row;

			Integer cvTermId = HibernateUtil.typeSafeObjectToInteger(items[0]);
			Integer cvId = HibernateUtil.typeSafeObjectToInteger(items[1]);
			Integer typeId = HibernateUtil.typeSafeObjectToInteger(items[2]);
			String value = (String) items[3];

			if (Objects.equals(typeId, TermId.CROP_ONTOLOGY_ID.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
				propertyMap.get(cvTermId).setCropOntologyId(value);
			} else if (Objects.equals(typeId, TermId.VARIABLE_TYPE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
				map.get(cvTermId).addVariableType(VariableType.getByName(value));
			} else if (Objects.equals(typeId, TermId.MIN_VALUE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
				scaleMap.get(cvTermId).setMinValue(value);
			} else if (Objects.equals(typeId, TermId.MAX_VALUE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
				scaleMap.get(cvTermId).setMaxValue(value);
			} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.METHODS.getId())) {
				methodMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.METHODS.getId())) {
				methodMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
				propertyMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.PROPERTIES.getId())) {
				propertyMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
				scaleMap.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.SCALES.getId())) {
				scaleMap.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.CREATION_DATE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
				map.get(cvTermId).setDateCreated(ISO8601DateParser.tryParse(value));
			} else if (Objects.equals(typeId, TermId.LAST_UPDATE_DATE.getId()) && Objects.equals(cvId, CvId.VARIABLES.getId())) {
				map.get(cvTermId).setDateLastModified(ISO8601DateParser.tryParse(value));
			}
		}
	}

    @Override
    public Integer getVariableObservations(int variableId) {

        final String numOfProjectsWithVariable =
                "SELECT count(pp.project_id) " + " FROM projectprop pp " + " WHERE NOT EXISTS( " + " SELECT 1 FROM projectprop stat "
                        + " WHERE stat.project_id = pp.project_id " + " AND stat.type_id = " + TermId.STUDY_STATUS.getId()
                        + " AND value = " + TermId.DELETED_STUDY.getId() + ") " + " AND pp.type_id = " + TermId.STANDARD_VARIABLE.getId()
                        + " AND pp.value = :variableId";

        SQLQuery query = this.ontologyDaoFactory.getActiveSession().createSQLQuery(numOfProjectsWithVariable);
        query.setParameter("variableId", variableId);
        return ((BigInteger) query.uniqueResult()).intValue();
    }

    // TODO: Follow DmsProjectDao countExperimentByVariable. This requires STORED_IN and that needs to deprecated.
    @Override
    public Integer getVariableStudies(int variableId) {
        return 0;
    }

}
