package org.generationcp.middleware.dao.oms;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StandardVariableDaoTest extends DataManagerIntegrationTest {
	
	private static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
	private static OntologyDataManager manager;
	
	@BeforeClass
	public static void setUp()  {
		manager = managerFactory.getNewOntologyDataManager();
	}
	
	@Test
	public void testGetStandardVariableSummaryCentral() throws MiddlewareQueryException {
		
		StandardVariableDao dao = new StandardVariableDao(managerFactory.getSessionProviderForCentral().getSession());
		
		//Load summary from the view
		StandardVariableSummary summary = dao.getStandardVariableSummary(PLANT_HEIGHT_ID);	
		Assert.assertNotNull(summary);

		//Load details using existing method
		StandardVariable details = manager.getStandardVariable(PLANT_HEIGHT_ID);
		Assert.assertNotNull(details);
		
		//Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		assertVariableDataMatches(details, summary);	
	}
	
	private void assertVariableDataMatches(StandardVariable details, StandardVariableSummary summary) {
		Assert.assertEquals(new Integer(details.getId()), summary.getId());
		Assert.assertEquals(details.getName(), summary.getName());
		Assert.assertEquals(details.getDescription(), summary.getDescription());
		
		assertTermDataMatches(details.getProperty(), summary.getProperty());
		assertTermDataMatches(details.getMethod(), summary.getMethod());
		assertTermDataMatches(details.getScale(), summary.getScale());
		assertTermDataMatches(details.getIsA(), summary.getIsA());
		assertTermDataMatches(details.getDataType(), summary.getDataType());
		assertTermDataMatches(details.getStoredIn(), summary.getStoredIn());
		
		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}
	
	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}
	
	@Test
	public void testGetStandardVariableSummaryLocal() throws MiddlewareQueryException {			
		//First create a local Standardvariable
		StandardVariable myOwnPlantHeight = new StandardVariable();
		myOwnPlantHeight.setName("MyOwnPlantHeight " + new Random().nextInt(1000));
		myOwnPlantHeight.setDescription(myOwnPlantHeight.getName() + " - Description.");
		myOwnPlantHeight.setProperty(new Term(15020, "Plant height", "Plant height"));
		myOwnPlantHeight.setMethod(new Term(16010, "Soil to tip at maturity", "Soil to tip at maturity"));
		
		Term myOwnScale = new Term();
		myOwnScale.setName("MyOwnScale " + new Random().nextInt(1000));
		myOwnScale.setDefinition(myOwnScale.getName() + " - Description.");
		myOwnPlantHeight.setScale(myOwnScale);
		
		myOwnPlantHeight.setIsA(new Term(1340, "Agronomic", "Agronomic"));
		myOwnPlantHeight.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));
		myOwnPlantHeight.setStoredIn(new Term(1043, "Observation variate", "Phenotypic data stored in phenotype.value"));
		
		manager.addStandardVariable(myOwnPlantHeight);
		
		//Load details using existing method
		StandardVariable details = manager.getStandardVariable(myOwnPlantHeight.getId());
		Assert.assertNotNull(details);
		
		//Load summary from the view
		StandardVariableDao dao = new StandardVariableDao(managerFactory.getSessionProviderForLocal().getSession());
		StandardVariableSummary summary = dao.getStandardVariableSummary(myOwnPlantHeight.getId());	
		Assert.assertNotNull(summary);
		
		//Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		Assert.assertEquals(new Integer(details.getId()), summary.getId());
		Assert.assertEquals(details.getName(), summary.getName());
		Assert.assertEquals(details.getDescription(), summary.getDescription());

		// For local standard variables we only assert the IDs of main components of the ontology star because
		// some ID references in the LOCAL 'standard_variable_summary' view data may be to central DB which view does not hard link to.
		Assert.assertEquals(new Integer(details.getProperty().getId()), summary.getProperty().getId());
		Assert.assertEquals(new Integer(details.getMethod().getId()), summary.getMethod().getId());
		Assert.assertEquals(new Integer(details.getScale().getId()), summary.getScale().getId());
		//isA is not populated in local databases anymore against the standard variable, it is stored against the Property of SV.
		//the view always returns null for isA :(
		Assert.assertNull(summary.getIsA());
		Assert.assertEquals(new Integer(details.getDataType().getId()), summary.getDataType().getId());
		Assert.assertEquals(new Integer(details.getStoredIn().getId()), summary.getStoredIn().getId());
		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
		
		//Test done. Cleanup the test data created.
		manager.deleteStandardVariable(myOwnPlantHeight.getId());
	}
	
	@Test
	public void testGetStarndardVariableSummaries() throws MiddlewareQueryException {
		
		StandardVariableDao dao = new StandardVariableDao(managerFactory.getSessionProviderForCentral().getSession());		
		List<StandardVariableSummary> starndardVariableSummaries = dao.getStarndardVariableSummaries(Arrays.asList(GRAIN_YIELD_ID, PLANT_HEIGHT_ID));		
		Assert.assertEquals(2, starndardVariableSummaries.size());
	}
}
