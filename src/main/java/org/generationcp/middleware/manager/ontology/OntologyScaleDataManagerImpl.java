
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.DataType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public class OntologyScaleDataManagerImpl extends DataManager implements OntologyScaleDataManager {

	private static final String SCALE_DOES_NOT_EXIST = "Scale does not exist";
	private static final String TERM_IS_NOT_SCALE = "Term is not scale";
	private static final String SCALE_EXIST_WITH_SAME_NAME = "Scale exist with same name";
	private static final String SCALE_CATEGORIES_SHOULD_NOT_EMPTY = "Scale categories should not be empty for categorical data type";
	private static final String SCALE_DATA_TYPE_SHOULD_NOT_EMPTY = "Scale data type should not be empty";
	private static final String SCALE_MIN_VALUE_NOT_VALID = "Min value is not valid";
	private static final String SCALE_MAX_VALUE_NOT_VALID = "Max value is not valid";
	private static final String SCALE_IS_REFERRED_TO_VARIABLE = "Scale is referred to variable.";

	public OntologyScaleDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Scale getScaleById(int scaleId) throws MiddlewareException {

		try {
			List<Scale> scales = this.getScales(false, new ArrayList<>(Collections.singletonList(scaleId)));
			if (scales.isEmpty()) {
				return null;
			}
			return scales.get(0);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at getScaleById" + e.getMessage(), e);
		}
	}

	@Override
	public List<Scale> getAllScales() throws MiddlewareException {
		try {
			return this.getScales(true, null);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at getAllScales" + e.getMessage(), e);
		}
	}

	/**
	 * This will fetch list of Scales by passing scaleIds This method is private and consumed by other methods
	 * 
	 * @param fetchAll will tell weather query should get all scales or not.
	 * @param scaleIds will tell weather scaleIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @return List<Scale>
	 * @throws MiddlewareException
	 */
	private List<Scale> getScales(Boolean fetchAll, List<Integer> scaleIds) throws MiddlewareException {
		Map<Integer, org.generationcp.middleware.domain.ontology.Scale> map = new HashMap<>();

		if (scaleIds == null) {
			scaleIds = new ArrayList<>();
		}

		if (!fetchAll && scaleIds.size() == 0) {
			return new ArrayList<>(map.values());
		}

		try {

			List<CVTerm> terms =
					fetchAll ? this.getCvTermDao().getAllByCvId(CvId.SCALES) : this.getCvTermDao().getAllByCvId(scaleIds, CvId.SCALES);
			for (CVTerm s : terms) {
				if (fetchAll) {
					scaleIds.add(s.getCvTermId());
				}
				map.put(s.getCvTermId(), new Scale(Term.fromCVTerm(s)));
			}

			Query query =
					this.getActiveSession()
							.createSQLQuery(
									"select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where t.is_obsolete =0 and t.cv_id = "
											+ CvId.SCALES.getId()).addEntity(CVTermProperty.class);

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
					scale.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					scale.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				}
			}

			query =
					this.getActiveSession().createSQLQuery(
							"SELECT r.subject_id, r.type_id, t.cv_id, t.cvterm_id, t.name, t.definition "
									+ "FROM cvterm_relationship r inner join cvterm t on r.object_id = t.cvterm_id "
									+ "where r.subject_id in (select cvterm_id from cvterm where cv_id = " + CvId.SCALES.getId() + ")");

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
					scale.addCategory((String) items[4], (String) items[5]);
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
	public void addScale(Scale scale) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getByNameAndCvId(scale.getName(), CvId.SCALES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_EXIST_WITH_SAME_NAME);
		}

		if (scale.getDataType() == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DATA_TYPE_SHOULD_NOT_EMPTY);
		}

		if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId()) && scale.getCategories().isEmpty()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_CATEGORIES_SHOULD_NOT_EMPTY);
		}

		// Check supplied value as numeric if non null
		if (Objects.equals(scale.getDataType(), DataType.NUMERIC_VARIABLE)) {
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				Integer min = StringUtil.parseInt(scale.getMinValue(), null);
				if (min == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MIN_VALUE_NOT_VALID);
				}
			}

			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				Integer max = StringUtil.parseInt(scale.getMaxValue(), null);
				if (max == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MAX_VALUE_NOT_VALID);
				}
			}
		}

		// Constant CvId
		scale.setVocabularyId(CvId.SCALES.getId());

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			// Saving term to database.
			CVTerm savedTerm = this.getCvTermDao().save(scale.getName(), scale.getDefinition(), CvId.SCALES);
			scale.setId(savedTerm.getCvTermId());

			// Setting dataType to Scale and saving relationship
			this.getCvTermRelationshipDao().save(scale.getId(), TermRelationshipId.HAS_TYPE.getId(), scale.getDataType().getId());

			// Saving values if present
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				this.getCvTermPropertyDao().save(scale.getId(), TermId.MIN_VALUE.getId(), String.valueOf(scale.getMinValue()), 0);
			}

			// Saving values if present
			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				this.getCvTermPropertyDao().save(scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(scale.getMaxValue()), 0);
			}

			// Saving categorical values if dataType is CATEGORICAL_VARIABLE
			if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId())) {
				// Saving new CV
				CV cv = new CV();
				cv.setCvId(this.getCvDao().getNextId("cvId"));
				cv.setName(String.valueOf(scale.getId()));
				cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
				this.getCvDao().save(cv);

				// Saving Categorical data if present
				for (String c : scale.getCategories().keySet()) {
					CVTerm category =
							new CVTerm(this.getCvTermDao().getNextId("cvTermId"), cv.getCvId(), c, scale.getCategories().get(c), null, 0, 0);
					this.getCvTermDao().save(category);
					this.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
				}
			}

			// Save creation time
			this.getCvTermPropertyDao().save(scale.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at addScale :" + e.getMessage(), e);
		}
	}

	@Override
	public void updateScale(Scale scale) throws MiddlewareException {

		if (Objects.equals(scale.getDataType(), null)) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DATA_TYPE_SHOULD_NOT_EMPTY);
		}

		if (Objects.equals(scale.getDataType(), DataType.CATEGORICAL_VARIABLE) && scale.getCategories().isEmpty()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_CATEGORIES_SHOULD_NOT_EMPTY);
		}

		// Check supplied value as numeric if non null
		if (Objects.equals(scale.getDataType(), DataType.NUMERIC_VARIABLE)) {
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				Integer min = StringUtil.parseInt(scale.getMinValue(), null);
				if (min == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MIN_VALUE_NOT_VALID);
				}
			}

			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				Integer max = StringUtil.parseInt(scale.getMaxValue(), null);
				if (max == null) {
					throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_MAX_VALUE_NOT_VALID);
				}
			}
		}

		// Fetch full scale from db
		CVTerm term = this.getCvTermDao().getById(scale.getId());

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.TERM_IS_NOT_SCALE);
		}

		// Fetch entire Scale variable from DB
		List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(scale.getId());

		Optional<CVTermRelationship> optionalDataRelation = Iterables.tryFind(relationships, new Predicate<CVTermRelationship>() {

			@Override
			public boolean apply(CVTermRelationship p) {
				return p.getTypeId() == TermId.HAS_TYPE.getId();
			}
		});

		CVTermRelationship dataRelation = optionalDataRelation.isPresent() ? optionalDataRelation.get() : null;
		DataType oldDataType = dataRelation != null ? DataType.getById(dataRelation.getObjectId()) : null;

		// Check data type change when object is referred to variable
		if (this.getCvTermRelationshipDao().isTermReferred(scale.getId()) && !Objects.equals(oldDataType, scale.getDataType())) {
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

		List<CVTerm> categoricalValues = this.getCvTermDao().getByIds(valueIds);

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

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			// Constant CvId
			scale.setVocabularyId(CvId.SCALES.getId());

			// Updating term to database.
			term.setName(scale.getName());
			term.setDefinition(scale.getDefinition());

			this.getCvTermDao().merge(term);

			// Update data type if changed
			if (!Objects.equals(oldDataType, scale.getDataType())) {
				if (dataRelation != null) {
					dataRelation.setObjectId(scale.getDataType().getId());
					this.getCvTermRelationshipDao().merge(dataRelation);
				} else {
					this.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_TYPE.getId(), scale.getDataType().getId());
				}
			}

			// Updating values if present
			if (!Strings.isNullOrEmpty(scale.getMinValue())) {
				this.getCvTermPropertyDao().save(scale.getId(), TermId.MIN_VALUE.getId(), String.valueOf(scale.getMinValue()), 0);
			} else {
				CVTermProperty property = this.getCvTermPropertyDao().getOneByCvTermAndType(scale.getId(), TermId.MIN_VALUE.getId());
				if (property != null) {
					this.getCvTermPropertyDao().makeTransient(property);
				}
			}

			// Updating values if present
			if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
				this.getCvTermPropertyDao().save(scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(scale.getMaxValue()), 0);
			} else {
				CVTermProperty property = this.getCvTermPropertyDao().getOneByCvTermAndType(scale.getId(), TermId.MAX_VALUE.getId());
				if (property != null) {
					this.getCvTermPropertyDao().makeTransient(property);
				}
			}

			// Getting cvId. Usually this will be available if previous data type is categorical
			Integer cvId = categoricalValues.isEmpty() ? null : categoricalValues.get(0).getCv();

			if (scale.getDataType().equals(DataType.CATEGORICAL_VARIABLE)) {

				// Creating new cv if old data type was not categorical
				if (Objects.equals(cvId, null)) {
					cvId = this.getCvDao().getNextId("cvId");
					CV cv = new CV();
					cv.setCvId(cvId);
					cv.setName(String.valueOf(scale.getId()));
					cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
					this.getCvDao().save(cv);
				}

				// Saving new categorical data if present
				for (String c : scale.getCategories().keySet()) {
					String name = c.trim();
					String desc = scale.getCategories().get(c).trim();

					CVTerm category = null;

					for (CVTerm ct : categoricalValues) {
						if (!name.equals(ct.getName())) {
							continue;
						}
						// remove from delete source
						removableCategoryTerms.remove(ct.getCvTermId());
						removableCategoryRelations.remove(ct.getCvTermId());

						// update description of existing category and continue
						ct.setDefinition(desc);
						this.getCvTermDao().merge(ct);
						category = ct;

						break;
					}

					if (category == null) {
						Integer nextId = this.getCvTermDao().getNextId("cvTermId");
						category = new CVTerm(nextId, cvId, c, scale.getCategories().get(c), null, 0, 0);
						this.getCvTermDao().save(category);
						this.getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
					}
				}
			}

			for (Integer k : removableCategoryRelations.keySet()) {
				this.getCvTermRelationshipDao().makeTransient(removableCategoryRelations.get(k));
				this.getCvTermDao().makeTransient(removableCategoryTerms.get(k));
			}

			if (!scale.getDataType().equals(DataType.CATEGORICAL_VARIABLE) && cvId != null) {
				this.getCvDao().makeTransient(this.getCvDao().getById(cvId));
			}

			// Save last modified Time
			this.getCvTermPropertyDao().save(scale.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at updateScale :" + e.getMessage(), e);
		}

	}

	@Override
	public void deleteScale(int scaleId) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(scaleId);

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (this.getCvTermRelationshipDao().isTermReferred(scaleId)) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_IS_REFERRED_TO_VARIABLE);
		}

		Session session = this.getActiveSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			// Deleting existing relationships for property
			List<Integer> categoricalTermIds = new ArrayList<>();
			List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(scaleId);

			for (CVTermRelationship r : relationships) {
				if (r.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					categoricalTermIds.add(r.getObjectId());
				}
				this.getCvTermRelationshipDao().makeTransient(r);
			}

			List<CVTerm> terms = this.getCvTermDao().getByIds(categoricalTermIds);

			for (CVTerm c : terms) {
				this.getCvTermDao().makeTransient(c);
			}

			if (!terms.isEmpty()) {
				this.getCvDao().makeTransient(this.getCvDao().getById(terms.get(0).getCv()));
			}

			// Deleting existing values for property
			List<CVTermProperty> properties = this.getCvTermPropertyDao().getByCvTermId(scaleId);
			for (CVTermProperty p : properties) {
				this.getCvTermPropertyDao().makeTransient(p);
			}

			this.getCvTermDao().makeTransient(term);
			transaction.commit();

		} catch (Exception e) {
			this.rollbackTransaction(transaction);
			throw new MiddlewareQueryException("Error at deleteScale" + e.getMessage(), e);
		}
	}
}
