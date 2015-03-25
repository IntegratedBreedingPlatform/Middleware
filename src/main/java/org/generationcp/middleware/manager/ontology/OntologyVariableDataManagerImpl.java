package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

import java.util.*;

public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

    private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
    private static final String TERM_IS_NOT_VARIABLE = "Term is not Variable";
    private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";


    private final OntologyMethodDataManager methodDataManager;
    private final OntologyPropertyDataManager propertyDataManager;
    private final OntologyScaleDataManager scaleDataManager;

    public OntologyVariableDataManagerImpl(OntologyMethodDataManager methodDataManager,
                                           OntologyPropertyDataManager propertyDataManager,
                                           OntologyScaleDataManager scaleDataManager,
                                           HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
        this.methodDataManager = methodDataManager;
        this.propertyDataManager = propertyDataManager;
        this.scaleDataManager = scaleDataManager;
    }


    @Override
    public List<OntologyVariableSummary> getAllVariables() throws MiddlewareQueryException {

        Map<Integer, OntologyVariableSummary> map = new HashMap<>();

        try {
            SQLQuery query = getActiveSession().createSQLQuery("SELECT cvt.cvterm_id AS id," +
                    "        cvt.name AS name," +
                    "        cvt.definition AS description," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1200), cvtr.object_id, NULL) SEPARATOR ',') AS property_id," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1200), prop.name, NULL) SEPARATOR ',') AS property_name," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1200), prop.definition, NULL) SEPARATOR ',') AS property_description," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1210), cvtr.object_id, NULL) SEPARATOR ',') AS method_id," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1210), method.name, NULL) SEPARATOR ',') AS method_name," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1210), method.definition, NULL) SEPARATOR ',') AS method_description," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1220), cvtr.object_id, NULL) SEPARATOR ',') AS scale_id," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1220), scale.name, NULL) SEPARATOR ',') AS scale_name," +
                    "        GROUP_CONCAT(IF((cvtr.type_id = 1220), scale.definition, NULL) SEPARATOR ',') AS scale_description" +
                    "    FROM ((((cvterm cvt" +
                    "        LEFT JOIN cvterm_relationship cvtr ON ((cvtr.subject_id = cvt.cvterm_id)))" +
                    "        LEFT JOIN cvterm prop ON ((prop.cvterm_id = cvtr.object_id)))" +
                    "        LEFT JOIN cvterm method ON ((method.cvterm_id = cvtr.object_id)))" +
                    "        LEFT JOIN cvterm scale ON ((scale.cvterm_id = cvtr.object_id)))" +
                    "    WHERE (cvt.cv_id = 1040)" +
                    "    GROUP BY cvt.cvterm_id , cvt.name" +
                    "    ORDER BY id")
                    .addScalar("id").addScalar("name").addScalar("description")
                    .addScalar("property_id").addScalar("property_name").addScalar("property_description")
                    .addScalar("method_id").addScalar("method_name").addScalar("method_description")
                    .addScalar("scale_id").addScalar("scale_id").addScalar("scale_id");

            List queryResults = query.addScalar("id").list();
            for(Object row : queryResults) {
                Object[] items = (Object[]) row;
                OntologyVariableSummary variable = new OntologyVariableSummary(typeSafeObjectToInteger(items[0]), (String)items[1], (String) items[2]);
                variable.setPropertySummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5]));
                variable.setMethodSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8]));
                variable.setScaleSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11]));
                map.put(variable.getId(), variable);
            }

            //Variable Types, Created, modified, min, max from CVTermProperty
            List properties = getCvTermPropertyDao().getByCvId(CvId.VARIABLES.getId());
            for(Object p : properties){
                CVTermProperty property = (CVTermProperty) p;

                OntologyVariableSummary variableSummary = map.get(property.getCvTermId());

                if(variableSummary == null){
                    continue;
                }

                if(Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())){
                    variableSummary.addVariableType(VariableType.getByName(property.getValue()));
                } else if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                    variableSummary.setMinValue(property.getValue());
                } else if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                    variableSummary.setMaxValue(property.getValue());
                } else if(Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())){
                    variableSummary.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
                } else if(Objects.equals(property.getTypeId(), TermId.LAST_UPDATION_DATE.getId())){
                    variableSummary.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
                }
            }

            //Get favorite from ProgramFavoriteDAO
            List<ProgramFavorite> favorites = getProgramFavoriteDao().getProgramFavorites(ProgramFavorite.FavoriteType.VARIABLE);

            for(ProgramFavorite f : favorites) {
                OntologyVariableSummary variableSummary = map.get(f.getEntityId());

                if(variableSummary == null){
                    continue;
                }

                variableSummary.setIsFavorite(true);
            }

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getVariables", e);
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public OntologyVariable getVariable(Integer id) throws MiddlewareQueryException, MiddlewareException {

        try {

            //Fetch full scale from db
            CVTerm term = getCvTermDao().getById(id);

            if (term == null) {
                throw new MiddlewareException(VARIABLE_DOES_NOT_EXIST);
            }

            if(term.getCv() != CvId.VARIABLES.getId()){
                throw new MiddlewareException(TERM_IS_NOT_VARIABLE);
            }

            try {

                OntologyVariable variable = new OntologyVariable(Term.fromCVTerm(term));

                //load scale, method and property data
                List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(term.getCvTermId());
                for(CVTermRelationship  r : relationships) {
                    if(Objects.equals(r.getTypeId(), TermId.HAS_METHOD.getId())){
                        variable.setMethod(methodDataManager.getMethod(r.getObjectId()));
                    } else if(Objects.equals(r.getTypeId(), TermId.HAS_PROPERTY.getId())){
                        variable.setProperty(propertyDataManager.getProperty(r.getObjectId()));
                    } else if(Objects.equals(r.getTypeId(), TermId.HAS_SCALE.getId())) {
                        variable.setScale(scaleDataManager.getScaleById(r.getObjectId()));
                    }
                }

                //Variable Types, Created, modified, min, max from CVTermProperty
                List properties = getCvTermPropertyDao().getByCvTermId(term.getCvTermId());

                for(Object p : properties){
                    CVTermProperty property = (CVTermProperty) p;

                    if(Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())){
                        variable.addVariableType(VariableType.getByName(property.getValue()));
                    } else if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                        variable.setMinValue(property.getValue());
                    } else if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                        variable.setMaxValue(property.getValue());
                    } else if(Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())){
                        variable.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
                    } else if(Objects.equals(property.getTypeId(), TermId.LAST_UPDATION_DATE.getId())){
                        variable.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
                    }
                }

                //Get favorite from ProgramFavoriteDAO
                variable.setIsFavorite(getProgramFavoriteDao().isEntityFavorite(ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId()));

                //TODO: Need to figure out observations which seems to be costly operation.
                return variable;

            } catch(HibernateException e) {
                throw new MiddlewareQueryException("Error in getVariable", e);
            }

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getting standard variable summaries from standard_variable_summary view", e);
        }
    }

    @Override
    public void addVariable(VariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException {

    }

    @Override
    public void updateVariable(VariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException {

    }

    @Override
    public void deleteVariable(Integer id) throws MiddlewareQueryException, MiddlewareException {

    }
}
