package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

    public OntologyVariableDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }


    @Override
    public List<OntologyVariableSummary> getAllVariables() throws MiddlewareQueryException {

        Map<Integer, OntologyVariableSummary> map = new HashMap<>();

        try {
            SQLQuery query = getActiveSession().createSQLQuery("SELECT * FROM standard_variable_summary");
            List queryResults = query.list();
            for(Object row : queryResults) {
                Object[] items = (Object[]) row;
                OntologyVariableSummary variable = new OntologyVariableSummary(typeSafeObjectToInteger(items[0]), (String)items[1], (String) items[2]);
                variable.setPropertySummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5]));
                variable.setMethodSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8]));
                variable.setScaleSummary(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11]));
                map.put(variable.getId(), variable);
            }

            //Created, modified, min, max from CVTermProperty
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

            //TODO: Need to figure out observations which seems to be costly operation.

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getting standard variable summaries from standard_variable_summary view", e);
        }

        return (List<OntologyVariableSummary>) map.values();
    }
}
