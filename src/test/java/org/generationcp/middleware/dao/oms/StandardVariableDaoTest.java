
package org.generationcp.middleware.dao.oms;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class StandardVariableDaoTest extends IntegrationTestBase {

	private static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
	private static final String PROGRAM_UUID = "1234567";

	@Autowired
	private OntologyDataManager manager;

	@Test
	public void testGetStandardVariableSummaryReferenceData() throws MiddlewareException {

		StandardVariableDao dao = new StandardVariableDao(this.sessionProvder.getSession());

		// Load summary from the view
		StandardVariableSummary summary = dao.getStandardVariableSummary(StandardVariableDaoTest.PLANT_HEIGHT_ID);
		Assert.assertNotNull(summary);

		// Load details using existing method
		StandardVariable details = this.manager.getStandardVariable(StandardVariableDaoTest.PLANT_HEIGHT_ID, PROGRAM_UUID);
		Assert.assertNotNull(details);

		// Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);
	}

	private void assertVariableDataMatches(StandardVariable details, StandardVariableSummary summary) {
		Assert.assertEquals(new Integer(details.getId()), summary.getId());
		Assert.assertEquals(details.getName(), summary.getName());
		Assert.assertEquals(details.getDescription(), summary.getDescription());

		this.assertTermDataMatches(details.getProperty(), summary.getProperty());
		this.assertTermDataMatches(details.getMethod(), summary.getMethod());
		this.assertTermDataMatches(details.getScale(), summary.getScale());
		this.assertTermDataMatches(details.getIsA(), summary.getIsA());
		this.assertTermDataMatches(details.getDataType(), summary.getDataType());

		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}

	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}

	@Test
	public void testGetStandardVariableSummaryUserCreated() throws MiddlewareException {
		// First create a local Standardvariable
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
		
		this.manager.addStandardVariable(myOwnPlantHeight,PROGRAM_UUID);

		// Load details using existing method
		StandardVariable details = this.manager.getStandardVariable(myOwnPlantHeight.getId(), PROGRAM_UUID);
		Assert.assertNotNull(details);

		// Load summary from the view
		StandardVariableDao dao = new StandardVariableDao(this.sessionProvder.getSession());
		StandardVariableSummary summary = dao.getStandardVariableSummary(myOwnPlantHeight.getId());
		Assert.assertNotNull(summary);

		// Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);
	}

	@Test
	public void testGetStarndardVariableSummaries() throws MiddlewareQueryException {

		StandardVariableDao dao = new StandardVariableDao(this.sessionProvder.getSession());
		List<StandardVariableSummary> starndardVariableSummaries =
				dao.getStarndardVariableSummaries(Arrays.asList(StandardVariableDaoTest.GRAIN_YIELD_ID,
						StandardVariableDaoTest.PLANT_HEIGHT_ID));
		Assert.assertEquals(2, starndardVariableSummaries.size());
	}
}
