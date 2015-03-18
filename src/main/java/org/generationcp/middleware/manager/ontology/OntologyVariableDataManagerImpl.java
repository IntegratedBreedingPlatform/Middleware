package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.OntologyVariableSummary;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

import java.util.ArrayList;
import java.util.List;

public class OntologyVariableDataManagerImpl extends DataManager implements OntologyVariableDataManager {

    public OntologyVariableDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }


    @Override
    public List<OntologyVariableSummary> getAllVariables() throws MiddlewareQueryException {
        List<OntologyVariableSummary> result = new ArrayList<>();
        try {
            SQLQuery query = getActiveSession().createSQLQuery("SELECT * FROM standard_variable_summary");
            List queryResults = query.list();
            for(Object row : queryResults) {
                Object[] items = (Object[]) row;
                OntologyVariableSummary variable = new OntologyVariableSummary(typeSafeObjectToInteger(items[0]), (String)items[1], (String) items[2]);
                variable.setProperty(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5]));
                variable.setMethod(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[6]), (String) items[7], (String) items[8]));
                variable.setScale(TermSummary.createNonEmpty(typeSafeObjectToInteger(items[9]), (String) items[10], (String) items[11]));
                result.add(variable);
            }
        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getting standard variable summaries from standard_variable_summary view", e);
        }

        return result;
    }
}
