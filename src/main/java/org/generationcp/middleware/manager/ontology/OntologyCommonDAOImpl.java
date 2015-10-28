package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Strings;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.util.ObjectTypeMapper;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

@SuppressWarnings("unchecked")
class OntologyCommonDAOImpl implements OntologyCommonDAO {

	public final String SHOULD_NOT_OBSOLETE = "is_obsolete = 0";

	private HibernateSessionProvider sessionProvider;

	public OntologyCommonDAOImpl() {
		super();
	}

	public void setSessionProvider(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public List<Integer> getAllPropertyIdsWithClassAndVariableType(String[] classes, String[] variableTypes) throws MiddlewareException {
		String classFilter = !(Objects.equals(classes, null) || classes.length == 0) ? " and dt.name in (:classes) " : "";
		String variableTypeFilter =
				!(Objects.equals(variableTypes, null) || variableTypes.length == 0) ? " and c.value in (:variableTypes) " : "";

		SQLQuery query = sessionProvider.getSession().createSQLQuery(
				"SELECT DISTINCT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id " +
				" inner join cvterm dt on dt.cvterm_id = cvtr.object_id where cvtr.type_id = " + TermId.IS_A.getId() +
				" and p.cv_id = 1010 and p.is_obsolete = 0 " + classFilter + " and exists " +
				" (SELECT 1 from cvtermprop c INNER JOIN cvterm_relationship pvtr on c.cvterm_id = pvtr.subject_id " +
				" where c.type_id = " + TermId.VARIABLE_TYPE.getId() + " and pvtr.object_id = p.cvterm_id" + variableTypeFilter
				+ ")");

		if (!classFilter.isEmpty()) {
			query.setParameterList("classes", classes);
		}

		if (!variableTypeFilter.isEmpty()) {
			query.setParameterList("variableTypes", variableTypes);
		}

		return query.list();
	}

	public Map<Integer, Property> getPropertiesWithCropOntologyAndTraits(Boolean fetchAll, List propertyIds, boolean filterObsolete) throws MiddlewareException {

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
}
