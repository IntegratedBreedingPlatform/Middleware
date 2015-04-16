package org.generationcp.middleware.manager.ontology;

import com.google.common.base.Strings;
import org.generationcp.middleware.domain.oms.*;
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
import org.generationcp.middleware.pojos.oms.CVTermProgramProperty;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigInteger;
import java.util.*;

public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

    private static final String VARIABLE_DOES_NOT_EXIST = "Variable does not exist";
    private static final String TERM_IS_NOT_VARIABLE = "Term is not Variable";
    private static final String VARIABLE_EXIST_WITH_SAME_NAME = "Variable exist with same name";
    private static final String USED_VARIABLE_CAN_NOT_CHANGE = "Variable is already in use. You can not change properties other than favorite, description and variable types";
    private static final String INSUFFICIENT_DATA = "One or more required fields are missing.";


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
    public List<OntologyVariableSummary> getWithFilter(String programUuid, Boolean favorites, Integer methodId, Integer propertyId, Integer scaleId) throws MiddlewareException {

        String filterClause = "";

        if(!Objects.equals(methodId, null)) {
            filterClause += " and vmr.mid = :methodId ";
        }

        if(!Objects.equals(propertyId, null)) {
            filterClause += " and vpr.pid = :propertyId ";
        }

        if(!Objects.equals(scaleId, null)) {
            filterClause += " and vsr.sid = :scaleId ";
        }

        if(!Objects.equals(favorites, null)){
            if(favorites){
                filterClause += "  and pf.id is not null ";
            } else {
                filterClause += "  and pf.id is null ";
            }
        }

        Map<Integer, OntologyVariableSummary> map = new HashMap<>();

        try {
            SQLQuery query = getActiveSession().createSQLQuery("select v.cvterm_id vid, v.name vn, v.definition vd, vmr.mid, vmr.mn, vmr.md, vpr.pid, vpr.pn, vpr.pd, vsr.sid, vsr.sn, vsr.sd, pf.id fid from cvterm v " +
                    "left join (select mr.subject_id vid, m.cvterm_id mid, m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and mr.type_id = 1210) vmr on vmr.vid = v.cvterm_id " +
                    "left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = 1200) vpr on vpr.vid = v.cvterm_id " +
                    "left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = 1220) vsr on vsr.vid = v.cvterm_id " +
                    "left join program_favorites pf on pf.entity_id = v.cvterm_id and pf.program_uuid = :programUuid and pf.entity_type = 'VARIABLES'" +
                    "    WHERE (v.cv_id = 1040) " + filterClause + " ORDER BY v.cvterm_id")
                    .addScalar("vid").addScalar("vn").addScalar("vd")
                    .addScalar("pid").addScalar("pn").addScalar("pd")
                    .addScalar("mid").addScalar("mn").addScalar("md")
                    .addScalar("sid").addScalar("sn").addScalar("sd")
                    .addScalar("fid");

            query.setParameter("programUuid", programUuid);

            if(!Objects.equals(methodId, null)) {
                query.setParameter("methodId", methodId);
            }

            if(!Objects.equals(propertyId, null)) {
                query.setParameter("propertyId", propertyId);
            }

            if(!Objects.equals(scaleId, null)) {
                query.setParameter("scaleId", scaleId);
            }

            List queryResults = query.list();

            for(Object row : queryResults) {
                Object[] items = (Object[]) row;
                OntologyVariableSummary variable = new OntologyVariableSummary(typeSafeObjectToInteger(items[0]), (String)items[1], (String) items[2]);
                variable.setPropertySummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5]));
                variable.setMethodSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8]));
                variable.setScaleSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11]));
                variable.setIsFavorite(items[12] != null);
                map.put(variable.getId(), variable);
            }

            //Variable Types, Created, modified from CVTermProperty
            List properties = getCvTermPropertyDao().getByCvId(CvId.VARIABLES.getId());
            for(Object p : properties){
                CVTermProperty property = (CVTermProperty) p;

                OntologyVariableSummary variableSummary = map.get(property.getCvTermId());

                if(variableSummary == null){
                    continue;
                }

                if(Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())){
                    variableSummary.addVariableType(VariableType.getByName(property.getValue()));
                } else if(Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())){
                    variableSummary.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
                } else if(Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())){
                    variableSummary.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
                }
            }

            //Variable alias and expected range
            List<CVTermProgramProperty> programProperties = getCvTermProgramPropertyDao().filterByColumnValue("programUuid", programUuid);

            for(CVTermProgramProperty property : programProperties) {

                OntologyVariableSummary variableSummary = map.get(property.getCvTermId());

                if (variableSummary == null) {
                    continue;
                }

                if(Objects.equals(property.getTypeId(), TermId.ALIAS.getId())){
                    variableSummary.setAlias(property.getValue());
                }else if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                    variableSummary.setMinValue(property.getValue());
                } else if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                    variableSummary.setMaxValue(property.getValue());
                }
            }

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getVariables", e);
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public OntologyVariable getVariable(String programUuid, Integer id) throws MiddlewareException {

        try {

            //Fetch full scale from db
            CVTerm term = getCvTermDao().getById(id);

            checkTermIsVariable(term);

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

                //Variable Types, Created, modified from CVTermProperty
                List properties = getCvTermPropertyDao().getByCvTermId(term.getCvTermId());

                for(Object p : properties){
                    CVTermProperty property = (CVTermProperty) p;

                    if(Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())){
                        variable.addVariableType(VariableType.getByName(property.getValue()));
                    } else if(Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())){
                        variable.setDateCreated(ISO8601DateParser.getDateFromTimeString(property.getValue()));
                    } else if(Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())){
                        variable.setDateLastModified(ISO8601DateParser.getDateFromTimeString(property.getValue()));
                    }
                }

                //Variable alias and expected range
                List<CVTermProgramProperty> programProperties = getCvTermProgramPropertyDao().getByCvTermAndProgram(id, programUuid);

                for(CVTermProgramProperty property : programProperties) {

                    if(Objects.equals(property.getTypeId(), TermId.ALIAS.getId())){
                        variable.setAlias(property.getValue());
                    }else if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                        variable.setMinValue(property.getValue());
                    } else if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                        variable.setMaxValue(property.getValue());
                    }
                }

                //Get favorite from ProgramFavoriteDAO
                ProgramFavorite programFavorite = getProgramFavoriteDao().getProgramFavorite(programUuid, ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());
                variable.setIsFavorite(programFavorite != null);

                variable.setObservations(getVariableUsage(id));

                return variable;

            } catch(HibernateException e) {
                throw new MiddlewareQueryException("Error in getVariable", e);
            }

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getting standard variable summaries from standard_variable_summary view", e);
        }
    }

    //TODO: Temporary implementation. Need to discuss this with actual use cases.
    //TODO: Move this implementation to OntologyBasicDataManager.isTermReferred
    private Integer getVariableUsage(Integer variableId) {
        SQLQuery query = getActiveSession().createSQLQuery("select (select count(*) from projectprop where type_id = :variableId) + (select count(*) from phenotype where observable_id = :variableId) c");
        query.setParameter("variableId", variableId);
        query.addScalar("c");
        return ((BigInteger) query.uniqueResult()).intValue();
    }

    @Override
    public void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {

        CVTerm term = getCvTermDao().getByNameAndCvId(variableInfo.getName(), CvId.VARIABLES.getId());

        if (term != null) {
            throw new MiddlewareException(VARIABLE_EXIST_WITH_SAME_NAME);
        }

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            //Saving term to database.
            CVTerm savedTerm = getCvTermDao().save(variableInfo.getName(), variableInfo.getDescription(), CvId.VARIABLES);
            variableInfo.setId(savedTerm.getCvTermId());

            //Setting method to variable
            if(variableInfo.getMethodId() != null){
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_METHOD.getId(), variableInfo.getMethodId());
            }

            //Setting property to variable
            if(variableInfo.getPropertyId() != null){
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_PROPERTY.getId(), variableInfo.getPropertyId());
            }

            //Setting scale to variable
            if(variableInfo.getMethodId() != null){
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_SCALE.getId(), variableInfo.getScaleId());
            }

            int rank = 0;
            for(VariableType type : variableInfo.getVariableTypes()){
                CVTermProperty property = new CVTermProperty();
                property.setCvTermPropertyId(getCvTermPropertyDao().getNextId(CVTermProperty.ID_NAME));
                property.setCvTermId(variableInfo.getId());
                property.setTypeId(TermId.VARIABLE_TYPE.getId());
                property.setValue(type.getName());
                property.setRank(rank++);
                getCvTermPropertyDao().save(property);
            }

            //Saving values if present
            if (!Strings.isNullOrEmpty(variableInfo.getMinValue())) {
                getCvTermPropertyDao().save(variableInfo.getId(), TermId.MIN_VALUE.getId(), String.valueOf(variableInfo.getMinValue()), 0);
            }

            //Saving values if present
            if (!Strings.isNullOrEmpty(variableInfo.getMaxValue())) {
                getCvTermPropertyDao().save(variableInfo.getId(), TermId.MAX_VALUE.getId(), String.valueOf(variableInfo.getMaxValue()), 0);
            }

            getCvTermPropertyDao().save(variableInfo.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.getCurrentTime().toString(), 0);

            transaction.commit();

        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error at addVariable :" + e.getMessage(), e);
        }
    }

    @Override
    public void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareException {

        VariableInfoDaoElements elements = new VariableInfoDaoElements();
        elements.setVariableInfo(variableInfo);

        fillDaoElementsAndCheckForUsage(elements);

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            CVTerm term = elements.getVariableTerm();
            CVTermRelationship methodRelation = elements.getMethodRelation();
            CVTermRelationship propertyRelation = elements.getPropertyRelation();
            CVTermRelationship scaleRelation = elements.getScaleRelation();
            CVTermProgramProperty aliasProperty = elements.getAliasProperty();
            CVTermProgramProperty minValueProperty = elements.getMinValueProperty();
            CVTermProgramProperty maxValueProperty = elements.getMaxValueProperty();


            transaction = session.beginTransaction();

            //Updating term to database.
            term.setName(variableInfo.getName());
            term.setDefinition(variableInfo.getDescription());

            getCvTermDao().merge(term);

            //Setting method to variable
            if(methodRelation == null) {
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_METHOD.getId(), variableInfo.getMethodId());
            } else {
                methodRelation.setObjectId(variableInfo.getMethodId());
                getCvTermRelationshipDao().merge(methodRelation);
            }

            //Setting property to variable
            if(propertyRelation == null) {
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_PROPERTY.getId(), variableInfo.getPropertyId());
            } else {
                propertyRelation.setObjectId(variableInfo.getPropertyId());
                getCvTermRelationshipDao().merge(propertyRelation);
            }

            //Setting scale to variable
            if(scaleRelation == null) {
                getCvTermRelationshipDao().save(variableInfo.getId(), TermRelationship.HAS_SCALE.getId(), variableInfo.getScaleId());
            } else {
                scaleRelation.setObjectId(variableInfo.getScaleId());
                getCvTermRelationshipDao().merge(scaleRelation);
            }

            //Updating values if present
            if (!Strings.isNullOrEmpty(variableInfo.getMinValue())) {
                getCvTermPropertyDao().save(variableInfo.getId(), TermId.MIN_VALUE.getId(), String.valueOf(variableInfo.getMinValue()), 0);
            } else{
                CVTermProperty property = getCvTermPropertyDao().getOneByCvTermAndType(variableInfo.getId(), TermId.MIN_VALUE.getId());
                if(property != null) {
                    getCvTermPropertyDao().makeTransient(property);
                }
            }

            //Updating values if present
            if (!Strings.isNullOrEmpty(variableInfo.getMaxValue())) {
                getCvTermPropertyDao().save(variableInfo.getId(), TermId.MAX_VALUE.getId(), String.valueOf(variableInfo.getMaxValue()), 0);
            } else{
                CVTermProperty property = getCvTermPropertyDao().getOneByCvTermAndType(variableInfo.getId(), TermId.MAX_VALUE.getId());
                if(property != null) {
                    getCvTermPropertyDao().makeTransient(property);
                }
            }

            //Updating variable types
            Map<VariableType, CVTermProperty> existingProperties = new HashMap<>();
            Set<VariableType> existingVariableTypes = new HashSet<>();

            //Variable Types from CVTermProperty
            List<CVTermProperty> properties = getCvTermPropertyDao().getByCvTermId(term.getCvTermId());

            for(CVTermProperty property : properties){
                if(Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())){
                    VariableType type = VariableType.getByName(property.getValue());
                    existingVariableTypes.add(type);
                    existingProperties.put(type, property);
                }
            }

            int rank = 0;
            for (VariableType type : variableInfo.getVariableTypes()) {

                //skip existing
                if (existingVariableTypes.contains(type)) {
                    continue;
                }

                CVTermProperty property = new CVTermProperty();
                int nextId = getCvTermPropertyDao().getNextId(CVTermProperty.ID_NAME);
                property.setCvTermPropertyId(nextId);
                property.setCvTermId(variableInfo.getId());
                property.setTypeId(TermId.VARIABLE_TYPE.getId());
                property.setValue(type.toString());
                property.setRank(rank++);
                getCvTermPropertyDao().save(property);
            }

            // Remove variable type properties which are not part of incoming set.
            Set<VariableType> toRemove = new HashSet<>(existingVariableTypes);
            toRemove.removeAll(variableInfo.getVariableTypes());

            for (VariableType type : toRemove) {
                getCvTermPropertyDao().makeTransient(existingProperties.get(type));
            }

            //Updating alias
            if(variableInfo.getAlias() != null) {
                getCvTermProgramPropertyDao().save(variableInfo.getId(), TermId.ALIAS.getId(), variableInfo.getProgramUuid(), variableInfo.getAlias());
            } else if(aliasProperty != null) {
                getCvTermProgramPropertyDao().makeTransient(aliasProperty);
            }

            //Updating min max values
            if(variableInfo.getMinValue() != null) {
                getCvTermProgramPropertyDao().save(variableInfo.getId(), TermId.MIN_VALUE.getId(), variableInfo.getProgramUuid(), variableInfo.getMinValue());
            } else if(minValueProperty != null) {
                getCvTermProgramPropertyDao().makeTransient(minValueProperty);
            }

            if(variableInfo.getMaxValue() != null) {
                getCvTermProgramPropertyDao().save(variableInfo.getId(), TermId.MAX_VALUE.getId(), variableInfo.getProgramUuid(), variableInfo.getMaxValue());
            } else if(maxValueProperty != null) {
                getCvTermProgramPropertyDao().makeTransient(maxValueProperty);
            }

            //Updating favorite to true if alias is defined
            ProgramFavorite programFavorite = getProgramFavoriteDao().getProgramFavorite(variableInfo.getProgramUuid(), ProgramFavorite.FavoriteType.VARIABLE, term.getCvTermId());
            boolean isFavorite = variableInfo.isFavorite() || variableInfo.getAlias() != null;

            if(isFavorite && programFavorite != null) {
                programFavorite = new ProgramFavorite();
                programFavorite.setProgramFavoriteId(getProgramFavoriteDao().getNextId(ProgramFavorite.ID_NAME));
                programFavorite.setEntityId(variableInfo.getId());
                programFavorite.setEntityType(ProgramFavorite.FavoriteType.VARIABLE.getName());
                programFavorite.setUniqueID(variableInfo.getProgramUuid());
                getProgramFavoriteDao().save(programFavorite);
            } else if(!isFavorite && programFavorite != null){
                getProgramFavoriteDao().makeTransient(programFavorite);
            }

            transaction.commit();

        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error at updateVariable :" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteVariable(Integer id) throws MiddlewareException {

    }

    private void checkAndThrowForNullObjects(Object ... objects) throws MiddlewareException {
        for(Object o : objects) {
            if(o != null) continue;
            throw new MiddlewareException(INSUFFICIENT_DATA);
        }
    }

    private void checkTermIsVariable(CVTerm term) throws MiddlewareException {

        if (term == null) {
            throw new MiddlewareException(VARIABLE_DOES_NOT_EXIST);
        }

        if(term.getCv() != CvId.VARIABLES.getId()){
            throw new MiddlewareException(TERM_IS_NOT_VARIABLE);
        }
    }

    private void fillDaoElementsAndCheckForUsage(VariableInfoDaoElements elements) throws MiddlewareException {

        OntologyVariableInfo variableInfo = elements.getVariableInfo();

        //Fetch term from db
        CVTerm variableTerm = getCvTermDao().getById(elements.getVariableInfo().getId());

        checkTermIsVariable(variableTerm);

        //Name is required
        checkAndThrowForNullObjects(variableInfo.getName(), variableInfo.getId(), variableInfo.getMethodId(), variableInfo.getProgramUuid(), variableInfo.getScaleId());

        CVTermRelationship methodRelation = null;
        CVTermRelationship propertyRelation = null;
        CVTermRelationship scaleRelation = null;

        //load scale, method and property data
        List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(variableTerm.getCvTermId());
        for(CVTermRelationship  r : relationships) {
            if(Objects.equals(r.getTypeId(), TermId.HAS_METHOD.getId())){
                methodRelation = r;
            } else if(Objects.equals(r.getTypeId(), TermId.HAS_PROPERTY.getId())){
                propertyRelation = r;
            } else if(Objects.equals(r.getTypeId(), TermId.HAS_SCALE.getId())) {
                scaleRelation = r;
            }
        }

        CVTermProgramProperty aliasProperty = null;
        CVTermProgramProperty minValueProperty = null;
        CVTermProgramProperty maxValueProperty = null;

        //Variable alias and expected range
        List<CVTermProgramProperty> programProperties = getCvTermProgramPropertyDao().getByCvTermAndProgram(variableInfo.getId(), variableInfo.getProgramUuid());

        for(CVTermProgramProperty property : programProperties) {

            if(Objects.equals(property.getTypeId(), TermId.ALIAS.getId())){
                aliasProperty = property;
            }else if(Objects.equals(property.getTypeId(), TermId.MIN_VALUE.getId())){
                minValueProperty = property;
            } else if(Objects.equals(property.getTypeId(), TermId.MAX_VALUE.getId())){
                maxValueProperty = property;
            }
        }

        Integer observations = getVariableUsage(elements.getVariableInfo().getId());

        if(observations > 0){

            //Name can not change
            if(!Objects.equals(elements.getVariableTerm().getName(), elements.getVariableInfo().getName())){
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            //method can not change
            if(methodRelation != null && !Objects.equals(methodRelation.getObjectId(), elements.getVariableInfo().getMethodId())) {
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            //property can not change
            if(propertyRelation != null && !Objects.equals(propertyRelation.getObjectId(), elements.getVariableInfo().getPropertyId())) {
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            //scale can not change
            if(scaleRelation != null && !Objects.equals(scaleRelation.getObjectId(), elements.getVariableInfo().getScaleId())) {
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            //Alias can not change but can be added.
            if(aliasProperty != null && !Objects.equals(aliasProperty.getValue(), elements.getVariableInfo().getAlias())){
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            //Expected range can not change
            if(minValueProperty != null && !Objects.equals(minValueProperty.getValue(), elements.getVariableInfo().getMinValue())){
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }

            if(maxValueProperty != null && !Objects.equals(maxValueProperty.getValue(), elements.getVariableInfo().getMaxValue())){
                throw new MiddlewareException(USED_VARIABLE_CAN_NOT_CHANGE);
            }
        }

        //Set to elements to send response back to caller.
        elements.setVariableTerm(variableTerm);
        elements.setMethodRelation(methodRelation);
        elements.setPropertyRelation(propertyRelation);
        elements.setScaleRelation(scaleRelation);
        elements.setAliasProperty(aliasProperty);
        elements.setMinValueProperty(minValueProperty);
        elements.setMaxValueProperty(maxValueProperty);
    }
}
