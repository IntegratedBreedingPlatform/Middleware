package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.GenotypeData;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenotypeDao extends GenericDAO<Genotype, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GenotypeDao.class);

	private static final String GENOTYPE_SEARCH_QUERY = "SELECT g.gid AS `gid`, " +
		"n.nval AS `designation`, " +
		"IFNULL (plot_no.value, " +
		"(SELECT ep.value FROM nd_experimentprop ep WHERE ep.nd_experiment_id = nde.parent_id AND ep.type_id = 8200)) AS `plotNumber`, \n" +
		"s.sample_no AS `sampleNo`, " +
		"s.sample_name AS `sampleName` ";

	private static final String GENOTYPE_SEARCH_FROM_QUERY = "FROM sample s " +
		"LEFT JOIN nd_experiment nde ON nde.nd_experiment_id = s.nd_experiment_id " +
		"LEFT JOIN  project p ON p.project_id = nde.project_id " +
		"INNER JOIN  genotype geno ON s.sample_id = geno.sample_id " +
		"LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = geno.variabe_id " +
		"LEFT JOIN stock st ON st.stock_id = nde.stock_id " +
		"LEFT JOIN germplsm g ON g.gid = st.dbxref_id " +
		"LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
		"LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = nde.nd_experiment_id AND plot_no.type_id = " +
		TermId.PLOT_NO.getId() + " " +
		"WHERE p.study_id = :studyId  GROUP BY s.sample_id ";

	public GenotypeDao(final Session session) {
		super(session);
	}

	public List<GenotypeDTO> searchGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO, final Pageable pageable) {
		final StringBuilder sql = new StringBuilder(GENOTYPE_SEARCH_QUERY);

		if (!CollectionUtils.isEmpty(searchRequestDTO.getFilter().getVariableMap())) {
			for (final String varName : searchRequestDTO.getFilter().getVariableMap().values()) {
				final StringBuilder genotypeVariablesClause =
					new StringBuilder(", MAX(IF(cvterm_variable.name = '%1$s', geno.value, NULL)) AS `%1$s`,")
						.append(" MAX(IF(cvterm_variable.name = '%1$s', geno.id, NULL)) AS `%1$s_genotypeId`,")
						.append(" MAX(IF(cvterm_variable.name = '%1$s', geno.variabe_id, NULL)) AS `%1$s_variableId`,")
						.append(" MAX(IF(cvterm_variable.name = '%1$s', cvterm_variable.name, NULL)) AS `%1$s_variableName` ");
				sql.append(String.format(genotypeVariablesClause.toString(), varName));
			}
		}
		sql.append(GENOTYPE_SEARCH_FROM_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(sql), searchRequestDTO.getFilter());
		addPageRequestOrderBy(sql, pageable, SampleGenotypeSearchRequestDTO.GenotypeFilter.SORTABLE_FIELDS);

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), searchRequestDTO.getFilter());

		query.addScalar("gid", new IntegerType());
		query.addScalar("designation");
		query.addScalar("plotNumber", new IntegerType());
		query.addScalar("sampleNo", new IntegerType());
		query.addScalar("sampleName");
		if (!CollectionUtils.isEmpty(searchRequestDTO.getFilter().getVariableMap())) {
			for (final String varName : searchRequestDTO.getFilter().getVariableMap().values()) {
				query.addScalar(varName); // Value
				query.addScalar(varName + "_genotypeId", new IntegerType()); // genotypeId
				query.addScalar(varName + "_variableId", new IntegerType()); // Variable Id
				query.addScalar(varName + "_variableName", new StringType()); // Variable Name
			}
		}
		query.setParameter("studyId", searchRequestDTO.getStudyId());
		addPaginationToSQLQuery(query, pageable);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		LOG.error(query.getQueryString());
		return this.getGenotypeResults(searchRequestDTO, query.list());
	}

	private List<GenotypeDTO> getGenotypeResults(final SampleGenotypeSearchRequestDTO searchRequestDTO, final List<Map<String, Object>> results) {
		final List<GenotypeDTO> genotypeDTOList = new ArrayList<>();
		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final GenotypeDTO genotypeDTO = new GenotypeDTO();
				genotypeDTO.setGid((Integer) row.get("gid"));
				genotypeDTO.setDesignation((String) row.get("designation"));
				genotypeDTO.setPlotNumber((Integer) row.get("plotNumber"));
				genotypeDTO.setSampleNo((Integer) row.get("sampleNo"));
				genotypeDTO.setSampleName((String) row.get("sampleName"));
				genotypeDTO.setGenotypeDataList(new ArrayList<>());
				if (!CollectionUtils.isEmpty(searchRequestDTO.getFilter().getVariableMap())) {
					for (final String varName : searchRequestDTO.getFilter().getVariableMap().values()) {
						final GenotypeData data = new GenotypeData();
						data.setValue((String) row.get(varName));
						data.setGenotypeId((Integer) row.get(varName + "_genotypeId"));
						data.setVariableId((Integer) row.get(varName + "_variableId"));
						data.setVariableName((String) row.get(varName + "_variableName"));
						genotypeDTO.getGenotypeDataList().add(data);
					}
				}
				genotypeDTOList.add(genotypeDTO);
			}
		}
		return genotypeDTOList;

	}

	private static void addSearchQueryFilters(
		final SqlQueryParamBuilder paramBuilder,
		final SampleGenotypeSearchRequestDTO.GenotypeFilter filter) {

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
            /*final List<Integer> variableIdsList = filter.getVariableIdsList();
            if (!CollectionUtils.isEmpty(variableIdsList)) {
                paramBuilder.append(" and var.cvterm_id IN (:variableIdsList)");
                paramBuilder.setParameterList("variableIdsList", variableIdsList);
            }*/
		}
	}

	public long countFilteredGenotypes(final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO) {
		final StringBuilder subQuery = new StringBuilder(GENOTYPE_SEARCH_QUERY);
		subQuery.append(GENOTYPE_SEARCH_FROM_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(subQuery), sampleGenotypeSearchRequestDTO.getFilter());

		final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
		mainSql.append(subQuery.toString());
		mainSql.append(") a \n");

		final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), sampleGenotypeSearchRequestDTO.getFilter());

		query.setParameter("studyId", sampleGenotypeSearchRequestDTO.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countGenotypes(final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO) {
		final StringBuilder subQuery = new StringBuilder(GENOTYPE_SEARCH_QUERY);
		subQuery.append(GENOTYPE_SEARCH_FROM_QUERY);
		final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
		mainSql.append(subQuery.toString());
		mainSql.append(") a \n");

		final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());

		query.setParameter("studyId", sampleGenotypeSearchRequestDTO.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}
}
