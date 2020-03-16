package org.generationcp.middleware.dao.dms;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.util.BasicFormatterImpl;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PhenotypeDaoTest {

	private PhenotypeDao dao;

	final private BasicFormatterImpl formattedSQL = new BasicFormatterImpl();

	@Mock
	private Session session;

	@Mock
	private SQLQuery query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.dao = new PhenotypeDao();
		this.dao.setSession(this.session);

		Mockito.when(this.query.addScalar(Matchers.anyString())).thenReturn(this.query);
		Mockito.when(this.query.addScalar(Matchers.anyString(), Matchers.any(Type.class))).thenReturn(this.query);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(this.query);
	}

	@Test
	public void testCountPhenotypes() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final PhenotypeSearchRequestDTO request = new PhenotypeSearchRequestDTO();
		request.setPage(0);
		request.setPageSize(10);
		final String studyDbId = "1";
		final List<String> studyIds = Arrays.asList(studyDbId);
		request.setStudyDbIds(studyIds);
		final List<String> termIds = Arrays.asList("111", "222");
		request.setObservationVariableDbIds(termIds);
		final long count = this.dao.countPhenotypes(request);

		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Mockito.verify(this.query).setParameterList("cvTermIds", termIds);
		Mockito.verify(this.query).setParameterList("studyDbIds", studyIds);
		Assert.assertEquals(100L, count);
	}

	@Test
	public void testGetObservationForTraitOnGermplasms() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> germplasmIds = Arrays.asList(51, 52, 53, 54, 55);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getObservationForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);

		final String expectedSql = this.getObservationsForTraitMainQuery() + " AND s.dbxref_id IN (:germplasmIds) "
				+ "ORDER BY p.observable_id, s.dbxref_id, plot.parent_id, p.value ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("germplasmIds", germplasmIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}

	@Test
	public void testGetObservationForTraits() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		final int start = 100;
		final int numOfRows = 500;
		this.dao.getObservationForTraits(traitIds, environmentIds, start, numOfRows);

		final String expectedSql = this.getObservationsForTraitMainQuery()
				+ "ORDER BY p.observable_id, s.dbxref_id, plot.parent_id, p.value ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
		Mockito.verify(this.query).setFirstResult(start);
		Mockito.verify(this.query).setMaxResults(numOfRows);
	}

	@Test
	public void testCountObservationForTraits() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		final long count = this.dao.countObservationForTraits(traitIds, environmentIds);

		final String expectedSql = "SELECT COUNT(*) "
			+ "		FROM nd_experiment e "
			+ "		INNER JOIN stock s ON e.stock_id = s.stock_id "
			+ "		INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id  "
			+ "		INNER JOIN project pr ON pr.project_id = e.project_id "
			+ "		INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4 "
			+ "		INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id "
			+ "		WHERE plot.parent_id IN (:environmentIds) AND p.observable_id IN (:traitIds)";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
		Assert.assertEquals(100L, count);
	}

	@Test
	public void testGetNumericTraitInfoList() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getNumericTraitInfoList(environmentIds, traitIds);

		final String expectedSql = "SELECT p.observable_id, "
			+ " 	COUNT(DISTINCT plot.parent_id) AS location_count, "
			+ "		COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
			+ "		COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
			+ "		IF (MIN(p.value * 1) IS NULL, 0, MIN(p.value * 1))  AS min_value, "
			+ "		IF (MAX(p.value * 1) IS NULL, 0, MAX(p.value * 1)) AS max_value "
			+ "		FROM phenotype p "
			+ "		INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
			+ "		INNER JOIN project pr ON pr.project_id = e.project_id "
			+ "		INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4 "
			+ "		INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id "
			+ "		INNER JOIN stock s ON e.stock_id = s.stock_id "
			+ "		WHERE plot.parent_id IN (:environmentIds) "
			+ "		AND p.observable_id IN (:numericVariableIds) GROUP by p.observable_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("numericVariableIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}

	@Test
	public void testGetTraitInfoCountsForTraits() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getTraitInfoCounts(environmentIds, traitIds);

		final String expectedSql = "SELECT p.observable_id, "
			+ "		COUNT(DISTINCT plot.parent_id) AS location_count, "
			+ "		COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
			+ "		COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
			+ "		FROM phenotype p "
			+ "		INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
			+ "		INNER JOIN stock s ON e.stock_id = s.stock_id "
			+ "		INNER JOIN project pr ON pr.project_id = e.project_id "
			+ "		INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4 "
			+ "		INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id "
			+ "		WHERE plot.parent_id IN (:environmentIds) AND p.observable_id IN (:variableIds) GROUP by p.observable_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("variableIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}

	@Test
	public void testGetTraitInfoCounts() {
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getTraitInfoCounts(environmentIds);

		final String expectedSql = "SELECT p.observable_id, "
			+ "		COUNT(DISTINCT plot.parent_id) AS location_count, "
			+ "		COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
			+ "		COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
			+ "		FROM phenotype p "
			+ "		INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
			+ "		INNER JOIN stock s ON e.stock_id = s.stock_id  INNER JOIN project pr ON pr.project_id = e.project_id "
			+ "		INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4 "
			+ "		INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id "
			+ "		WHERE plot.parent_id IN (:environmentIds) GROUP by p.observable_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}

	@Test
	public void testUpdatePhenotypesByProjectIdAndLocationId() {
		final int projectId = 1;
		final int cvTermId = 5157;
		final String value = "1.5678";
		this.dao.updatePhenotypesByExperimentIdAndObervableId(projectId, cvTermId, value);

		Mockito.verify(this.session).flush();
		final String updateSql = "UPDATE phenotype pheno "
				+ "SET pheno.value = '" + value + "'"
				+ " WHERE pheno.nd_experiment_id = " + projectId
				+ " AND pheno.observable_id = " + cvTermId;
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(updateSql, sqlCaptor.getValue());
		Mockito.verify(this.query).executeUpdate();
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithDesignationGermplasmFactor() {
		this.dao.containsAtLeast2CommonEntriesWithValues(1, 2, TermId.DESIG.getId());

		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery("stock.name");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithGIDGermplasmFactor() {
		this.dao.containsAtLeast2CommonEntriesWithValues(1, 2, TermId.GID.getId());

		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery("stock.dbxref_id");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithEntryNoGermplasmFactor() {
		this.dao.containsAtLeast2CommonEntriesWithValues(1, 2, TermId.ENTRY_NO.getId());

		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery("stock.uniquename");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithOtherGermplasmFactor() {
		this.dao.containsAtLeast2CommonEntriesWithValues(1, 2, TermId.CROSS.getId());

		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery("stock.stock_id");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(this.formatString(expectedSql), this.formatString(sqlCaptor.getValue()));
	}

	private String formatString(final String format) {
		return this.formattedSQL.format(format).replace(" ", "");
	}

	private String getContainsAtLeast2CommonEntriesQuery(final String germplasmGroupBy) {
		return " SELECT phenotype.observable_id,count(phenotype.observable_id)  FROM nd_experiment nd_exp  INNER JOIN stock ON nd_exp.stock_id = stock.stock_id  LEFT JOIN phenotype  ON nd_exp.nd_experiment_id = phenotype.nd_experiment_id  INNER JOIN project pr ON pr.project_id = nd_exp.project_id   INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4  INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id  where nd_exp.project_id = :projectId and plot.parent_id = :environmentId and ((phenotype.value <> '' and phenotype.value is not null) or  (phenotype.cvalue_id <> '' and phenotype.cvalue_id is not null))  group by plot.parent_id, " + germplasmGroupBy + " , phenotype.observable_id  having count(phenotype.observable_id) >= 2 LIMIT 1";
	}
	private String getObservationsForTraitMainQuery() {
		return "SELECT p.observable_id, "
			+ "		s.dbxref_id, plot.parent_id, "
			+ "		p.value FROM nd_experiment e "
			+ "		INNER JOIN stock s ON e.stock_id = s.stock_id "
			+ "		INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id "
			+ "		INNER JOIN project pr ON pr.project_id = e.project_id "
			+ "		INNER JOIN project plot_ds on plot_ds.study_id = pr.study_id and plot_ds.dataset_type_id = 4 "
			+ "		INNER JOIN nd_experiment plot ON plot_ds.project_id = plot.project_id "
			+ "		WHERE plot.parent_id IN (:environmentIds) AND p.observable_id IN (:traitIds)";
	}


}
