package org.generationcp.middleware.manager.ontology;

import com.google.common.base.Strings;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyScaleDataManagerImpl extends DataManager implements OntologyScaleDataManager {

    private static final String SCALE_EXIST_WITH_SAME_NAME = "Scale exist with same name";
    private static final String SCALE_CATEGORIES_SHOULD_NOT_EMPTY = "Scale categories should not be empty for categorical data type";
    private static final String SCALE_DATA_TYPE_SHOULD_NOT_EMPTY = "Scale data type should n ot be empty";

    private static final Logger LOG = LoggerFactory.getLogger(OntologyScaleDataManagerImpl.class);

    public OntologyScaleDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public Scale getScaleById(int scaleId) throws MiddlewareQueryException {

        try {
            List<Scale> scales = getScales(false, new ArrayList<>(Arrays.asList(scaleId)));
            if(scales.size() == 0) return null;
            return scales.get(0);
        } catch (Exception e) {
            logAndThrowException("Error at getScaleById" + e.getMessage(), e, LOG);
        }
        return null;
    }

    @Override
    public List<Scale> getAllScales() throws MiddlewareQueryException {
        try {
            return getScales(true, null);
        } catch (Exception e) {
            logAndThrowException("Error at getAllScales" + e.getMessage(), e, LOG);
        }
        return new ArrayList<>();
    }

    @Override
    public void addScale(Scale scale) throws MiddlewareQueryException, MiddlewareException {

        CVTerm term = getCvTermDao().getByNameAndCvId(scale.getName(), CvId.SCALES.getId());

        if (term != null) {
            logAndThrowException(SCALE_EXIST_WITH_SAME_NAME);
        }


        if(scale.getDataType() == null) {
            logAndThrowException(SCALE_DATA_TYPE_SHOULD_NOT_EMPTY);
        }

        if(scale.getDataType().getId() == Scale.DataType.CATEGORICAL_VARIABLE.getId() && scale.getCategories().isEmpty()) {
            logAndThrowException(SCALE_CATEGORIES_SHOULD_NOT_EMPTY);
        }

        //Constant CvId
        scale.getTerm().setVocabularyId(CvId.SCALES.getId());

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            //Saving term to database.
            CVTerm savedTerm = getCvTermDao().save(scale.getName(), scale.getDefinition(), CvId.SCALES);
            scale.setId(savedTerm.getCvTermId());

            //Setting dataType to Scale and saving relationship
            getCvTermRelationshipDao().save(scale.getId(), TermRelationship.HAS_TYPE.getId(), scale.getDataType().getId());

            //Saving values if present
            if (!Strings.isNullOrEmpty(scale.getMinValue())) {
                getCvTermPropertyDao().save(scale.getId(), TermId.MIN_VALUE.getId(), String.valueOf(scale.getMinValue()), 0);
            }

            //Saving values if present
            if (!Strings.isNullOrEmpty(scale.getMaxValue())) {
                getCvTermPropertyDao().save(scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(scale.getMaxValue()), 0);
            }

            //Saving categorical values if dataType is CATEGORICAL_VARIABLE
            if(scale.getDataType().getId() == Scale.DataType.CATEGORICAL_VARIABLE.getId()){
                //Saving new CV
                CV cv = new CV();
                cv.setCvId(getCvDao().getNextId("cvId"));
                cv.setName(String.valueOf(scale.getId()));
                cv.setDefinition(String.valueOf(scale.getName() + " - " + scale.getDefinition()));
                getCvDao().save(cv);

                //Saving Categorical data if present
                for(String c : scale.getCategories().keySet()){
                    CVTerm category = new CVTerm(getCvTermDao().getNextId("cvTermId"), cv.getCvId(), c, scale.getCategories().get(c), null, 0, 0);
                    getCvTermDao().save(category);
                    getCvTermRelationshipDao().save(scale.getId(), TermId.HAS_VALUE.getId(), category.getCvTermId());
                }
            }

            transaction.commit();

        } catch (Exception e) {
            rollbackTransaction(transaction);
            logAndThrowException("Error at addScale :" + e.getMessage(), e);
        }
    }


    /**
     * This will fetch list of properties by passing scaleIds
     * This method is private and consumed by other methods
     * @param fetchAll will tell weather query should get all properties or not.
     * @param scaleIds will tell weather scaleIds should be pass to filter result. Combination of these two will give flexible usage.
     * @return List<Scale>
     * @throws MiddlewareQueryException
     */
    private List<Scale> getScales(Boolean fetchAll, List<Integer> scaleIds) throws MiddlewareQueryException {
        Map<Integer, Scale> map = new HashMap<>();

        if(scaleIds == null) scaleIds = new ArrayList<>();

        if(!fetchAll && scaleIds.size() == 0){
            return new ArrayList<>(map.values());
        }

        try {

            List<CVTerm> terms = fetchAll ? getCvTermDao().getAllByCvId(CvId.SCALES) : getCvTermDao().getAllByCvId(scaleIds, CvId.SCALES);
            for(CVTerm s : terms){
                if(fetchAll) {
                    scaleIds.add(s.getCvTermId());
                }
                map.put(s.getCvTermId(), new Scale(Term.fromCVTerm(s)));
            }

            Query query = getActiveSession()
                    .createSQLQuery("select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where t.is_obsolete =0 and t.cv_id = " + CvId.SCALES.getId())
                    .addEntity(CVTermProperty.class);

            List properties = query.list();

            for(Object p : properties){
                CVTermProperty property = (CVTermProperty) p;
                Scale scale = map.get(property.getCvTermId());

                if(scale == null){
                    continue;
                }

                if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                    scale.setMinValue(property.getValue());
                }

                if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                    scale.setMaxValue(property.getValue());
                }
            }

            query = getActiveSession()
                    .createSQLQuery("SELECT r.subject_id, r.type_id, t.cv_id, t.cvterm_id, t.name, t.definition " +
                            "FROM cvterm_relationship r inner join cvterm t on r.object_id = t.cvterm_id " +
                            "where r.subject_id in (select cvterm_id from cvterm where cv_id = " + CvId.SCALES.getId() + ")" );

            List result = query.list();

            for (Object row : result) {
                Object[] items = (Object[]) row;

                Integer scaleId = (Integer) items[0];

                Scale scale = map.get(scaleId);

                if(scale == null){
                    continue;
                }

                if(Objects.equals(items[1], TermId.HAS_TYPE.getId())){
                    scale.setDataType(Scale.DataType.getById((Integer) items[3]));
                }else if(Objects.equals(items[1], TermId.HAS_VALUE.getId())){
                    scale.addCategory((String) items[4], (String) items[5]);
                }
            }

        } catch (Exception e) {
            logAndThrowException("Error at getScales", e, LOG);
        }

        return new ArrayList<>(map.values());
    }

}
