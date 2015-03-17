package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyScaleDataManagerImpl extends DataManager implements OntologyScaleDataManager {

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
