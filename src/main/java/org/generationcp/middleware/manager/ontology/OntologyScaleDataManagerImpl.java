
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyScaleDataManagerImpl implements OntologyScaleDataManager {

	private static final String SCALE_DOES_NOT_EXIST = "Scale does not exist";
	private static final String TERM_IS_NOT_SCALE = "Term is not scale";
	private static final String SCALE_EXIST_WITH_SAME_NAME = "Scale exist with same name";
	private static final String SCALE_CATEGORIES_SHOULD_NOT_EMPTY = "Scale categories should not be empty for categorical data type";
	private static final String SCALE_DATA_TYPE_SHOULD_NOT_EMPTY = "Scale data type should not be empty";
	private static final String SCALE_MIN_VALUE_NOT_VALID = "Min value is not valid";
	private static final String SCALE_MAX_VALUE_NOT_VALID = "Max value is not valid";
	private static final String SCALE_IS_REFERRED_TO_VARIABLE = "Scale is referred to variable.";

	@Autowired
	private OntologyCommonDAO ontologyCommonDAO;

	@Autowired
	private OntologyDaoFactory ontologyDaoFactory;

	@Autowired
	protected Clock systemClock;

	public OntologyScaleDataManagerImpl() {
        // no-arg constructor is required by CGLIB proxying used by Spring 3x and older.
	}

    //TODO:This is temporary hack for managerFactory, builder and service. It should refactor to remove this constructor
    public OntologyScaleDataManagerImpl(HibernateSessionProvider sessionProvider) {
        this.ontologyDaoFactory = new OntologyDaoFactory();
        this.ontologyDaoFactory.setSessionProvider(sessionProvider);
        OntologyCommonDAOImpl ontologyCommonDAOImpl = new OntologyCommonDAOImpl();
        ontologyCommonDAOImpl.setSessionProvider(sessionProvider);
        this.ontologyCommonDAO = ontologyCommonDAOImpl;
        this.systemClock = new SystemClock();
    }

	@Override
	public Scale getScale(int scaleId, boolean filterObsolete) throws MiddlewareException {
		List<Scale> scales = this.getScales(false, new ArrayList<>(Collections.singletonList(scaleId)), filterObsolete);
		if (scales.isEmpty()) {
			return null;
		}
		return scales.get(0);
	}

	@Override
	public List<Scale> getAllScales() throws MiddlewareException {
		return this.getScales(true, null, true);
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
	private List<Scale> getScales(Boolean fetchAll, List<Integer> scaleIds, boolean filterObsolete) throws MiddlewareException {
		Map<Integer, org.generationcp.middleware.domain.ontology.Scale> map = new HashMap<>();

		List<Integer> termIds = scaleIds;
		if (termIds == null) {
			termIds = new ArrayList<>();
		}

		if (!fetchAll && termIds.isEmpty()) {
			return new ArrayList<>(map.values());
		}

		List<CVTerm> terms = fetchAll ? this.ontologyDaoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, filterObsolete)
				: this.ontologyDaoFactory.getCvTermDao().getAllByCvId(termIds, CvId.SCALES, filterObsolete);
		for (CVTerm s : terms) {
			if (fetchAll) {
				termIds.add(s.getCvTermId());
			}
			map.put(s.getCvTermId(), new Scale(Term.fromCVTerm(s)));
		}

		final Map<Integer, Scale> scalesWithDataTypeAndProperties = this.ontologyCommonDAO.getScalesWithDataTypeAndProperties(termIds, map, filterObsolete);
		final List<Scale> scales = new ArrayList<>(scalesWithDataTypeAndProperties.values());

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

		CVDao cvDao = this.ontologyDaoFactory.getCvDao();
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

		CVTerm term = cvTermDao.getByNameAndCvId(scale.getName(), CvId.SCALES.getId());

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

		// Saving term to database.
		CVTerm savedTerm = cvTermDao.save(scale.getName(), scale.getDefinition(), CvId.SCALES);
		scale.setId(savedTerm.getCvTermId());

		// Setting dataType to Scale and saving relationship
		cvTermRelationshipDao.save(scale.getId(), TermRelationshipId.HAS_TYPE.getId(), scale.getDataType().getId());

		// Saving values if present
		if (!Strings.isNullOrEmpty(scale.getMinValue())) {
			cvTermPropertyDao.save(scale.getId(), TermId.MIN_VALUE.getId(), String.valueOf(scale.getMinValue()), 0);
		}

		// Saving values if present
		if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
			cvTermPropertyDao.save(scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(scale.getMaxValue()), 0);
		}

		// Saving categorical values if dataType is CATEGORICAL_VARIABLE
		if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId())) {
			// Saving new CV
			CV cv = new CV();
			cv.setName(String.valueOf(scale.getId()));
			cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
			cvDao.save(cv);

			// Saving Categorical data if present
			for (TermSummary c : scale.getCategories()) {

				String label = c.getName().trim();
				String value = c.getDefinition().trim();

				CVTerm category = new CVTerm(null, cv.getCvId(), label, value, null, 0, 0);
				cvTermDao.save(category);
				cvTermRelationshipDao.save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
			}
		}

		// Save creation time
		scale.setDateCreated(systemClock.now());

		String strValueOfDate = ISO8601DateParser.toString(scale.getDateCreated());

		// Save creation time
		cvTermPropertyDao.updateOrDeleteProperty(scale.getId(), TermId.CREATION_DATE.getId(), strValueOfDate, 0);
	}

	@Override
	public void updateScale(Scale scale) throws MiddlewareException {

		CVDao cvDao = this.ontologyDaoFactory.getCvDao();
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

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
		CVTerm term = cvTermDao.getById(scale.getId());

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.TERM_IS_NOT_SCALE);
		}

		// Fetch entire Scale variable from DB
		List<CVTermRelationship> relationships = cvTermRelationshipDao.getBySubject(scale.getId());

		Optional<CVTermRelationship> optionalDataRelation = Iterables.tryFind(relationships, new Predicate<CVTermRelationship>() {

			@Override
			public boolean apply(CVTermRelationship p) {
				return p.getTypeId() == TermId.HAS_TYPE.getId();
			}
		});

		CVTermRelationship dataRelation = optionalDataRelation.isPresent() ? optionalDataRelation.get() : null;
		DataType oldDataType = dataRelation != null ? DataType.getById(dataRelation.getObjectId()) : null;

		// Check data type change when object is referred to variable
		if (cvTermRelationshipDao.isTermReferred(scale.getId()) && !Objects.equals(oldDataType, scale.getDataType())) {
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

		List<CVTerm> categoricalValues = cvTermDao.getByIds(valueIds);

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

		// Constant CvId
		scale.setVocabularyId(CvId.SCALES.getId());

		// Updating term to database.
		term.setName(scale.getName());
		term.setDefinition(scale.getDefinition());

		cvTermDao.merge(term);

		// Update data type if changed
		if (!Objects.equals(oldDataType, scale.getDataType())) {
			if (dataRelation != null) {
				dataRelation.setObjectId(scale.getDataType().getId());
				cvTermRelationshipDao.merge(dataRelation);
			} else {
				cvTermRelationshipDao.save(scale.getId(), TermId.HAS_TYPE.getId(), scale.getDataType().getId());
			}
		}

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
				cvDao.save(cv);

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
					cvTermDao.merge(ct);
					category = ct;

					break;
				}

				if (category == null) {
					category = new CVTerm(null, cvId, label, value, null, 0, 0);
					cvTermDao.save(category);
					cvTermRelationshipDao.save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
				}
			}
		}

		for (Integer k : removableCategoryRelations.keySet()) {
			cvTermRelationshipDao.makeTransient(removableCategoryRelations.get(k));
			cvTermDao.makeTransient(removableCategoryTerms.get(k));
		}

		if (!scale.getDataType().equals(DataType.CATEGORICAL_VARIABLE) && cvId != null) {
			cvDao.makeTransient(cvDao.getById(cvId));
		}

		// Save last modified Time
		scale.setDateLastModified(systemClock.now());

		String strValueOfDate = ISO8601DateParser.toString(scale.getDateLastModified());

		// Save creation time
		cvTermPropertyDao.updateOrDeleteProperty(scale.getId(), TermId.LAST_UPDATE_DATE.getId(), strValueOfDate, 0);
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
	public void deleteScale(int scaleId) throws MiddlewareException {

		CVDao cvDao = this.ontologyDaoFactory.getCvDao();
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

		CVTerm term = cvTermDao.getById(scaleId);

		if (term == null) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.SCALES.getId()) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_DOES_NOT_EXIST);
		}

		if (cvTermRelationshipDao.isTermReferred(scaleId)) {
			throw new MiddlewareException(OntologyScaleDataManagerImpl.SCALE_IS_REFERRED_TO_VARIABLE);
		}

		// Deleting existing relationships for property
		List<Integer> categoricalTermIds = new ArrayList<>();
		List<CVTermRelationship> relationships = cvTermRelationshipDao.getBySubject(scaleId);

		for (CVTermRelationship r : relationships) {
			if (r.getTypeId().equals(TermId.HAS_VALUE.getId())) {
				categoricalTermIds.add(r.getObjectId());
			}
			cvTermRelationshipDao.makeTransient(r);
		}

		List<CVTerm> terms = cvTermDao.getByIds(categoricalTermIds);

		for (CVTerm c : terms) {
			cvTermDao.makeTransient(c);
		}

		if (!terms.isEmpty()) {
			cvDao.makeTransient(cvDao.getById(terms.get(0).getCv()));
		}

		// Deleting existing values for property
		List<CVTermProperty> properties = cvTermPropertyDao.getByCvTermId(scaleId);
		for (CVTermProperty p : properties) {
			cvTermPropertyDao.makeTransient(p);
		}

		cvTermDao.makeTransient(term);

	}
}
