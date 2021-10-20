
package org.generationcp.middleware.manager.ontology;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Transactional
public class OntologyScaleDataManagerImpl extends DataManager implements OntologyScaleDataManager {

	@Autowired
	private TermDataManager termDataManager;

	private static final String SCALE_DOES_NOT_EXIST = "Scale does not exist";
	private static final String TERM_IS_NOT_SCALE = "Term is not scale";
	private static final String SCALE_EXIST_WITH_SAME_NAME = "Scale exist with same name";
	private static final String SCALE_CATEGORIES_SHOULD_NOT_EMPTY = "Scale categories should not be empty for categorical data type";
	private static final String SCALE_DATA_TYPE_SHOULD_NOT_EMPTY = "Scale data type should not be empty";
	private static final String SCALE_MIN_VALUE_NOT_VALID = "Min value is not valid";
	private static final String SCALE_MAX_VALUE_NOT_VALID = "Max value is not valid";
	private static final String SCALE_IS_REFERRED_TO_VARIABLE = "Scale is referred to variable.";

	private DaoFactory daoFactory;

	public OntologyScaleDataManagerImpl() {
		super();
	}

	public OntologyScaleDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Scale getScaleById(int scaleId, boolean filterObsolete) {

		try {
			List<Scale> scales = this.getScales(false, new ArrayList<>(Collections.singletonList(scaleId)), filterObsolete);
			if (scales.isEmpty()) {
				return null;
			}
			return scales.get(0);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at getScaleById" + e.getMessage(), e);
		}
	}

	@Override
	public List<Scale> getAllScales() {
		try {
			return this.getScales(true, null, true);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at getAllScales" + e.getMessage(), e);
		}
	}

	/**
	 * This will fetch list of Scales by passing scaleIds This method is private and consumed by other methods. This will filter obsolete
	 * scales if filterObsolete is true.
	 *
	 * @param fetchAll will tell whether query should get all scales or not.
	 * @param scaleIds will tell whether scaleIds should be passed to filter result. Combination of these two will give flexible usage.
	 * @param filterObsolete will tell whether obsolete scales will be filtered
	 * @return List<Scale>
	 */
	private List<Scale> getScales(Boolean fetchAll, List<Integer> scaleIds, boolean filterObsolete) {
		Map<Integer, org.generationcp.middleware.domain.ontology.Scale> map = new HashMap<>();

		List<Integer> termIds = scaleIds;
		if (termIds == null) {
			termIds = new ArrayList<>();
		}

		if (!fetchAll && termIds.isEmpty()) {
			return new ArrayList<>(map.values());
		}

		try {

			List<CVTerm> terms = fetchAll ? daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, filterObsolete)
					: daoFactory.getCvTermDao().getAllByCvId(termIds, CvId.SCALES, filterObsolete);
			for (CVTerm s : terms) {
				if (fetchAll) {
					termIds.add(s.getCvTermId());
				}
				map.put(s.getCvTermId(), new Scale(Term.fromCVTerm(s)));
			}

			String filterObsoleteClause = "";
			if (filterObsolete) {
				filterObsoleteClause = "t.is_obsolete = 0 and";
			}

			Query query = this.getActiveSession()
					.createSQLQuery("select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where "
							+ filterObsoleteClause + " t.cv_id = " + CvId.SCALES.getId())
					.addEntity(CVTermProperty.class);

			List properties = query.list();

			for (Object p : properties) {
				CVTermProperty property = (CVTermProperty) p;
				Scale scale = map.get(property.getCvTermId());

				if (scale == null) {
					continue;
				}

				if (Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())) {
					scale.setMinValue(property.getValue());
				} else if (Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())) {
					scale.setMaxValue(property.getValue());
				} else if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
					scale.setDateCreated(ISO8601DateParser.tryParseToDateTime(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					scale.setDateLastModified(ISO8601DateParser.tryParseToDateTime(property.getValue()));
				}
			}

			query =
					this.getActiveSession().createSQLQuery(
							"SELECT r.subject_id, r.type_id, t.cv_id, t.cvterm_id, t.name, t.definition "
									+ "FROM cvterm_relationship r inner join cvterm t on r.object_id = t.cvterm_id "
									+ "where r.subject_id in (:scaleIds)");

			query.setParameterList("scaleIds", termIds);

			List result = query.list();

			for (Object row : result) {
				Object[] items = (Object[]) row;

				Integer scaleId = (Integer) items[0];

				Scale scale = map.get(scaleId);

				if (scale == null) {
					continue;
				}

				if (Objects.equals(items[1], TermId.HAS_TYPE.getId())) {
					scale.setDataType(DataType.getById((Integer) items[3]));
				} else if (Objects.equals(items[1], TermId.HAS_VALUE.getId())) {
					scale.addCategory(new TermSummary((Integer) items[3], (String) items[4], (String) items[5]));
				}
			}

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at getScales", e);
		}

		List<Scale> scales = new ArrayList<>(map.values());

		Collections.sort(scales, new Comparator<Scale>() {

			@Override
			public int compare(Scale l, Scale r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return scales;
	}

	@Override
	public void addScale(Scale scale) {

		CVTerm term = daoFactory.getCvTermDao().getByNameAndCvId(scale.getName(), CvId.SCALES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_EXIST_WITH_SAME_NAME);
		}

		if (scale.getDataType() == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DATA_TYPE_SHOULD_NOT_EMPTY);
		}

		if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId()) && scale.getCategories().isEmpty()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_CATEGORIES_SHOULD_NOT_EMPTY);
		}

		// Constant CvId
		scale.setVocabularyId(CvId.SCALES.getId());

		try {

			// Saving term to database.
			CVTerm savedTerm = daoFactory.getCvTermDao().save(scale.getName(), scale.getDefinition(), CvId.SCALES);
			scale.setId(savedTerm.getCvTermId());

			// Setting dataType to Scale and saving relationship
			daoFactory.getCvTermRelationshipDao().save(scale.getId(), TermRelationshipId.HAS_TYPE.getId(), scale.getDataType().getId());

			// Saving values if present
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				daoFactory.getCvTermPropertyDao().save(scale.getId(), TermId.MIN_VALUE.getId(), String.valueOf(scale.getMinValue()), 0);
			}

			// Saving values if present
			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				daoFactory.getCvTermPropertyDao().save(scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(scale.getMaxValue()), 0);
			}

			// Saving categorical values if dataType is CATEGORICAL_VARIABLE
			if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId())) {
				// Saving new CV
				CV cv = new CV();
				cv.setName(String.valueOf(scale.getId()));
				cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
				this.daoFactory.getCvDao().save(cv);

				// Saving Categorical data if present
				for (TermSummary c : scale.getCategories()) {

					String label = c.getName().trim();
					String value = c.getDefinition().trim();

					CVTerm category = new CVTerm(null, cv.getCvId(), label, value, null, 0, 0);
					daoFactory.getCvTermDao().save(category);
					daoFactory.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
				}
			}

			// Save creation time
			daoFactory.getCvTermPropertyDao().save(scale.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);


		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at addScale :" + e.getMessage(), e);
		}
	}

	@Override
	public void updateScale(Scale scale) {

		if (Objects.equals(scale.getDataType(), null)) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DATA_TYPE_SHOULD_NOT_EMPTY);
		}

		if (Objects.equals(scale.getDataType(), DataType.CATEGORICAL_VARIABLE) && scale.getCategories().isEmpty()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_CATEGORIES_SHOULD_NOT_EMPTY);
		}

		// Check supplied value as numeric if non null
		if (Objects.equals(scale.getDataType(), DataType.NUMERIC_VARIABLE)) {
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				Float min = StringUtil.parseFloat(scale.getMinValue(), null);
				if (min == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MIN_VALUE_NOT_VALID);
				}
			}

			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				Float max = StringUtil.parseFloat(scale.getMaxValue(), null);
				if (max == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MAX_VALUE_NOT_VALID);
				}
			}
		}

		// Fetch full scale from db
		CVTerm term = daoFactory.getCvTermDao().getById(scale.getId());

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.TERM_IS_NOT_SCALE);
		}

		// Fetch entire Scale variable from DB
		List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(scale.getId());

		Optional<CVTermRelationship> optionalDataRelation = Iterables.tryFind(relationships, new Predicate<CVTermRelationship>() {

			@Override
			public boolean apply(CVTermRelationship p) {
				return p.getTypeId() == TermId.HAS_TYPE.getId();
			}
		});

		CVTermRelationship dataRelation = optionalDataRelation.isPresent() ? optionalDataRelation.get() : null;
		DataType oldDataType = dataRelation != null ? DataType.getById(dataRelation.getObjectId()) : null;

		// Check data type change when object is referred to variable
		if (daoFactory.getCvTermRelationshipDao().isTermReferred(scale.getId()) && !Objects.equals(oldDataType, scale.getDataType())) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_IS_REFERRED_TO_VARIABLE);
		}

		List<Integer> valueIds = new ArrayList<>();
		List<CVTermRelationship> valueRelationships = new ArrayList<>();

		// Existing categorical values
		for (CVTermRelationship r : relationships) {
			if (r.getTypeId() == TermId.HAS_VALUE.getId()) {
				valueIds.add(r.getObjectId());
				valueRelationships.add(r);
			}
		}

		List<CVTerm> categoricalValues = daoFactory.getCvTermDao().getByIds(valueIds);

		Map<Integer, CVTerm> removableCategoryTerms = Util.mapAll(categoricalValues, new Function<CVTerm, Integer>() {

			@Override
			public Integer apply(CVTerm x) {
				return x.getCvTermId();
			}
		});

		Map<Integer, CVTermRelationship> removableCategoryRelations =
				Util.mapAll(valueRelationships, new Function<CVTermRelationship, Integer>() {

					@Override
					public Integer apply(CVTermRelationship r) {
						return r.getObjectId();
					}
				});

		try {

			// Constant CvId
			scale.setVocabularyId(CvId.SCALES.getId());

			// Updating term to database.
			term.setName(scale.getName());
			term.setDefinition(scale.getDefinition());

			daoFactory.getCvTermDao().merge(term);

			// Update data type if changed
			if (!Objects.equals(oldDataType, scale.getDataType())) {
				if (dataRelation != null) {
					dataRelation.setObjectId(scale.getDataType().getId());
					daoFactory.getCvTermRelationshipDao().merge(dataRelation);
				} else {
					daoFactory.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_TYPE.getId(), scale.getDataType().getId());
				}
			}

			CvTermPropertyDao cvTermPropertyDao = daoFactory.getCvTermPropertyDao();
			int maxTermId = TermId.MAX_VALUE.getId();
			int minTermId = TermId.MIN_VALUE.getId();
			String minScale = scale.getMinValue();
			String maxScale = scale.getMaxValue();

			// Updating values if present
			this.updatingValues(cvTermPropertyDao, scale, minScale, minTermId);
			this.updatingValues(cvTermPropertyDao, scale, maxScale, maxTermId);

			// Getting cvId. Usually this will be available if previous data type is categorical
			Integer cvId = categoricalValues.isEmpty() ? null : categoricalValues.get(0).getCv();

			if (scale.getDataType().equals(DataType.CATEGORICAL_VARIABLE)) {

				// Creating new cv if old data type was not categorical
				if (cvId == null) {
					CV cv = new CV();
					cv.setName(String.valueOf(scale.getId()));
					cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
					this.daoFactory.getCvDao().save(cv);

					//Setting cvId from auto incremented value.
					cvId = cv.getCvId();
				}

				// Saving new categorical data if present
				for (TermSummary c : scale.getCategories()) {

					String label = c.getName().trim();
					String value = c.getDefinition().trim();

					CVTerm category = null;

					for (CVTerm ct : categoricalValues) {

						if (!label.equals(ct.getName())) {
							continue;
						}
						// remove from delete source
						removableCategoryTerms.remove(ct.getCvTermId());
						removableCategoryRelations.remove(ct.getCvTermId());

						// update description of existing category and continue
						ct.setDefinition(value);
						daoFactory.getCvTermDao().merge(ct);
						category = ct;

						break;
					}

					if (category == null) {
						category = new CVTerm(null, cvId, label, value, null, 0, 0);
						daoFactory.getCvTermDao().save(category);
						daoFactory.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
					}
				}
			}

			for (Integer k : removableCategoryRelations.keySet()) {
				daoFactory.getCvTermRelationshipDao().makeTransient(removableCategoryRelations.get(k));
				daoFactory.getCvTermDao().makeTransient(removableCategoryTerms.get(k));
			}

			if (!scale.getDataType().equals(DataType.CATEGORICAL_VARIABLE) && cvId != null) {
				this.daoFactory.getCvDao().makeTransient(this.daoFactory.getCvDao().getById(cvId));
			}

			// Save last modified Time
			daoFactory.getCvTermPropertyDao().save(scale.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);
			this.deleteScalesRelatedVariablesFromCache(Integer.valueOf(scale.getId()));

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at updateScale :" + e.getMessage(), e);
		}

	}

	private void deleteScalesRelatedVariablesFromCache(final Integer scaleId) {
		// Note : Get list of relationships related to scale Id
		final List<TermRelationship> relationships =
				this.termDataManager.getRelationshipsWithObjectAndType(scaleId, TermRelationshipId.HAS_SCALE);

		for (final Iterator<TermRelationship> iterator = relationships.iterator(); iterator.hasNext();) {
			final TermRelationship termRelationship = iterator.next();
			final int variableId = termRelationship.getSubjectTerm().getId();
			VariableCache.removeFromCache(variableId);
		}
	}

	void updatingValues(CvTermPropertyDao cvTermPropertyDao, Scale scale, String scaleSize, int termId) {
		if (!Strings.isNullOrEmpty(scaleSize)) {
			cvTermPropertyDao.save(scale.getId(), termId, String.valueOf(scaleSize), 0);
		} else {
			CVTermProperty property = cvTermPropertyDao.getOneByCvTermAndType(scale.getId(), termId);
			if (property != null) {
				cvTermPropertyDao.makeTransient(property);
			}
		}
	}

	@Override
	public void deleteScale(int scaleId) {

		CVTerm term = daoFactory.getCvTermDao().getById(scaleId);

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (daoFactory.getCvTermRelationshipDao().isTermReferred(scaleId)) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_IS_REFERRED_TO_VARIABLE);
		}

		try {

			// Deleting existing relationships for property
			List<Integer> categoricalTermIds = new ArrayList<>();
			List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(scaleId);

			for (CVTermRelationship r : relationships) {
				if (r.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					categoricalTermIds.add(r.getObjectId());
				}
				daoFactory.getCvTermRelationshipDao().makeTransient(r);
			}

			List<CVTerm> terms = daoFactory.getCvTermDao().getByIds(categoricalTermIds);

			for (CVTerm c : terms) {
				daoFactory.getCvTermDao().makeTransient(c);
			}

			if (!terms.isEmpty()) {
				this.daoFactory.getCvDao().makeTransient(this.daoFactory.getCvDao().getById(terms.get(0).getCv()));
			}

			// Deleting existing values for property
			List<CVTermProperty> properties = daoFactory.getCvTermPropertyDao().getByCvTermId(scaleId);
			for (CVTermProperty p : properties) {
				daoFactory.getCvTermPropertyDao().makeTransient(p);
			}

			daoFactory.getCvTermDao().makeTransient(term);


		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at deleteScale" + e.getMessage(), e);
		}
	}
}
