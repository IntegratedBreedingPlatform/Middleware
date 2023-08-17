package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.PhenotypeExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import java.util.List;

public class PhenotypeExternalReferenceDAO extends GenericExternalReferenceDAO<PhenotypeExternalReference> {

    public PhenotypeExternalReferenceDAO(final Session session) {
        super(session);
    }

    @Override
    String getIdField() {
        return "phenotype_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_phenotype";
    }

    public void deleteByProjectIdAndVariableIds(final Integer projectId, final List<Integer> variableIds) {
        try {
            // Delete phenotypes external reference
            final String sql = "delete external_reference " + " from nd_experiment e "
                    + " INNER JOIN phenotype pheno ON e.nd_experiment_id = pheno.nd_experiment_id "
                    + " INNER JOIN external_reference_phenotype external_reference ON external_reference.phenotype_id = pheno.phenotype_id "
                    + " where e.project_id = :projectId "
                    + " and pheno.observable_id IN (:variableIds) ";
            final SQLQuery statement = this.getSession().createSQLQuery(sql);
            statement.setParameter("projectId", projectId);
            statement.setParameterList("variableIds", variableIds);
            statement.executeUpdate();

        } catch (final HibernateException e) {
            throw new MiddlewareQueryException("Error in deleteByProjectIdAndVariableIds=" + projectId + ", " + variableIds + e.getMessage(), e);
        }
    }

}
