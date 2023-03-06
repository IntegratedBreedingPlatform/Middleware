package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DataSetDaoTest {
	
	@Mock
	private Session mockSession;
	
	@Mock
	private SQLQuery mockQuery;
	
	private DataSetDao dao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.dao = new DataSetDao(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockQuery);
	}

	@Test
	public void testDeleteExperimentsByLocation_WithExperimentProperties() {
		Mockito.doReturn(1).when(this.mockQuery).executeUpdate();
		final int datasetId = 1234;
		final int locationId = 555;
		this.dao.deleteExperimentsByLocation(datasetId, locationId);
		
		Mockito.verify(this.mockSession).flush();
		final String deleteExperimentSql = "delete e, pheno, eprop " + "from nd_experiment e, "
				+ "phenotype pheno, nd_experimentprop eprop "
				+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
				+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
				+ "  and e.nd_experiment_id = eprop.nd_experiment_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(deleteExperimentSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).executeUpdate();
	}
	
	@Test
	public void testDeleteExperimentsByLocation_NoExperimentProperties() {
		final int datasetId = 1234;
		final int locationId = 555;
		this.dao.deleteExperimentsByLocation(datasetId, locationId);
		
		Mockito.verify(this.mockSession).flush();
		final String deleteExperimentSqlQuery1 = "delete e, pheno, eprop " + "from nd_experiment e, "
				+ "phenotype pheno, nd_experimentprop eprop "
				+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
				+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
				+ "  and e.nd_experiment_id = eprop.nd_experiment_id";
		final String deleteExperimentSqlQuery2 = "delete e, pheno " + "from nd_experiment e, "
				+ "phenotype pheno  "
				+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
				+ "  and e.nd_experiment_id = pheno.nd_experiment_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession, Mockito.times(2)).createSQLQuery(sqlCaptor.capture());
		final List<String> queries = sqlCaptor.getAllValues();
		Assert.assertEquals(deleteExperimentSqlQuery1, queries.get(0));
		Assert.assertEquals(deleteExperimentSqlQuery2, queries.get(1));
		Mockito.verify(this.mockQuery, Mockito.times(2)).executeUpdate();
	}
	
	@Test
	public void testDeleteExperimentsByLocationAndType() {
		final int datasetId = 1234;
		final int locationId = 555;
		final int typeId = TermId.PLOT_EXPERIMENT.getId();
		this.dao.deleteExperimentsByLocationAndType(datasetId, locationId, typeId);
		
		Mockito.verify(this.mockSession).flush();
		final String deleteExperimentSql = "delete e, pheno, eprop " + "from nd_experiment e, "
				+ "phenotype pheno, nd_experimentprop eprop "
				+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
				+ "  and e.type_id = " + typeId
				+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
				+ "  and e.nd_experiment_id = eprop.nd_experiment_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(deleteExperimentSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).executeUpdate();
	}

}
