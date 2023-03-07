package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.GenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

public class GenotypeDao extends GenericDAO<Genotype, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(GenotypeDao.class);

    private static final String GENOTYPE_SEARCH_QUERY = "SELECT geno.id AS `genotypeId`, \n" +
            "g.gid AS `gid`, \n" +
            "n.nval AS `designation`, \n" +
            "IFNULL (plot_no.value, \n" +
            "\t(SELECT ep.value FROM nd_experimentprop ep WHERE ep.nd_experiment_id = nde.parent_id AND ep.type_id = 8200)) AS `plotNumber`, \n" +
            "s.sample_no AS `sampleNo`, \n" +
            "s.sample_name AS `sampleName`, \n" +
            "var.name AS `variableName`, \n" +
            "geno.value AS `value` \n" +
            "FROM genotype geno \n" +
            "INNER JOIN sample s ON s.sample_id = geno.sample_id \n" +
            "INNER JOIN nd_experiment nde ON nde.nd_experiment_id = s.nd_experiment_id \n" +
            "INNER JOIN project p ON p.project_id = nde.project_id \n" +
            "INNER JOIN stock st ON st.stock_id = nde.stock_id \n" +
            "INNER JOIN germplsm g ON g.gid = st.dbxref_id \n" +
            "INNER JOIN cvterm var ON var.cvterm_id = geno.variabe_id \n" +
            "LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 \n" +
            "LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = nde.nd_experiment_id AND plot_no.type_id = " +
                TermId.PLOT_NO.getId() + " \n" +
            "WHERE p.study_id = :studyId ";

    public GenotypeDao(final Session session) {
        super(session);
    }

    public List<GenotypeDTO> searchGenotypes(final GenotypeSearchRequestDTO searchRequestDTO, final Pageable pageable) {
        final StringBuilder sql = new StringBuilder(GENOTYPE_SEARCH_QUERY);
        addSearchQueryFilters(new SqlQueryParamBuilder(sql), searchRequestDTO.getFilter());
        addPageRequestOrderBy(sql, pageable, GenotypeSearchRequestDTO.Filter.SORTABLE_FIELDS);

        final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        addSearchQueryFilters(new SqlQueryParamBuilder(query), searchRequestDTO.getFilter());

        query.addScalar("genotypeId");
        query.addScalar("gid");
        query.addScalar("designation");
        query.addScalar("plotNumber");
        query.addScalar("sampleNo");
        query.addScalar("sampleName");
        query.addScalar("variableName");
        query.addScalar("value");
        query.setParameter("studyId", searchRequestDTO.getStudyId());

        addPaginationToSQLQuery(query, pageable);

        query.setResultTransformer(Transformers.aliasToBean(GenotypeDTO.class));
        return query.list();
    }


    private static void addSearchQueryFilters(
            final SqlQueryParamBuilder paramBuilder,
            final GenotypeSearchRequestDTO.Filter filter) {

        if (filter != null) {
            final List<Integer> gidList = filter.getGidList();
            if (!CollectionUtils.isEmpty(gidList)) {
                paramBuilder.append(" and g.gid IN (:gidList)");
                paramBuilder.setParameterList("gidList", gidList);
            }
            final String designation = filter.getDesignation();
            if (!StringUtils.isEmpty(designation)) {
                paramBuilder.append(" and n.nval like :designation"); //
                paramBuilder.setParameter("designation", '%' + designation + '%');
            }
            final List<Integer> plotNumberList = filter.getPlotNumberList();
            if (!CollectionUtils.isEmpty(plotNumberList)) {
                paramBuilder.append(" and plot_no.value IN (:plotNumberList)");
                paramBuilder.setParameterList("plotNumberList", plotNumberList);
            }
            final List<Integer> sampleNumberList = filter.getSampleNumberList();
            if (!CollectionUtils.isEmpty(sampleNumberList)) {
                paramBuilder.append(" and s.sample_no IN (:sampleNumberList)");
                paramBuilder.setParameterList("sampleNumberList", sampleNumberList);
            }
            final String sampleName = filter.getSampleName();
            if (!StringUtils.isEmpty(sampleName)) {
                paramBuilder.append(" and s.sample_name like :sampleName"); //
                paramBuilder.setParameter("sampleName", '%' + sampleName + '%');
            }
            final List<Integer> variableIdsList = filter.getVariableIdsList();
            if (!CollectionUtils.isEmpty(variableIdsList)) {
                paramBuilder.append(" and var.cvterm_id IN (:variableIdsList)");
                paramBuilder.setParameterList("variableIdsList", variableIdsList);
            }
            final String value = filter.getValue();
            if (!StringUtils.isEmpty(value)) {
                paramBuilder.append(" and geno.value like :value"); //
                paramBuilder.setParameter("value", '%' + value + '%');
            }

        }
    }

    public long countFilteredGenotypes(final GenotypeSearchRequestDTO genotypeSearchRequestDTO) {
        final StringBuilder subQuery = new StringBuilder(GENOTYPE_SEARCH_QUERY);
        addSearchQueryFilters(new SqlQueryParamBuilder(subQuery), genotypeSearchRequestDTO.getFilter());

        final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
        mainSql.append(subQuery.toString());
        mainSql.append(") a \n");

        final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());
        addSearchQueryFilters(new SqlQueryParamBuilder(query), genotypeSearchRequestDTO.getFilter());

        query.setParameter("studyId", genotypeSearchRequestDTO.getStudyId());
        return ((BigInteger) query.uniqueResult()).longValue();
    }

    public long countGenotypes(final GenotypeSearchRequestDTO genotypeSearchRequestDTO) {
        final StringBuilder subQuery = new StringBuilder(GENOTYPE_SEARCH_QUERY);

        final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
        mainSql.append(subQuery.toString());
        mainSql.append(") a \n");

        final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());

        query.setParameter("studyId", genotypeSearchRequestDTO.getStudyId());
        return ((BigInteger) query.uniqueResult()).longValue();
    }
}
