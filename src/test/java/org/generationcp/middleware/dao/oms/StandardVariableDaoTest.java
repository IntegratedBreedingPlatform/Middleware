
package org.generationcp.middleware.dao.oms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class StandardVariableDaoTest extends IntegrationTestBase {

	private static final String PROGRAM_UUID = "1234567";

	@Autowired
	private OntologyDataManager manager;

	private StandardVariableSaver standardVariableSaver;
	private StandardVariableDao standardVariableDao;
	private CVTermDao cvTermDao;

	@Before
	public void init() {
		this.standardVariableDao = new StandardVariableDao(this.sessionProvder.getSession());
		this.standardVariableSaver = new StandardVariableSaver(this.sessionProvder);
		this.cvTermDao = new CVTermDao(this.sessionProvder.getSession());
	}

	@Test
	public void testGetStandardVariableSummaryReferenceData() {

		final StandardVariable testVariable = createStandardVariable("testVariable");

		// Load summary from the view
		StandardVariableSummary summary = standardVariableDao.getStandardVariableSummary(testVariable.getId());
		Assert.assertNotNull(summary);

		// Load details using existing method
		StandardVariable details = this.manager.getStandardVariable(testVariable.getId(), PROGRAM_UUID);
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
		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}

	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}

	@Test
	public void testGetStandardVariableSummaryUserCreated() {
		// First create a local Standardvariable
		final StandardVariable testVariable = createStandardVariable("testVariable");

		// Load details using existing method
		StandardVariable details = this.manager.getStandardVariable(testVariable.getId(), PROGRAM_UUID);
		Assert.assertNotNull(details);

		StandardVariableSummary summary = this.standardVariableDao.getStandardVariableSummary(testVariable.getId());
		Assert.assertNotNull(summary);

		// Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);
	}

	@Test
	public void testGetStarndardVariableSummaries() {

		final StandardVariable testVariable1 = createStandardVariable("testVariable1");
		final StandardVariable testVariable2 = createStandardVariable("testVariable2");

		List<StandardVariableSummary> starndardVariableSummaries =
			standardVariableDao.getStarndardVariableSummaries(Arrays.asList(
				testVariable1.getId(),
				testVariable2.getId()));
		Assert.assertEquals(2, starndardVariableSummaries.size());
	}

	private StandardVariable createStandardVariable(final String name) {

		final CVTerm property = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		final CVTerm method = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setProperty(new Term(property.getCvTermId(), property.getName(), property.getDefinition()));
		standardVariable.setScale(new Term(scale.getCvTermId(), scale.getName(), scale.getDefinition()));
		standardVariable.setMethod(new Term(method.getCvTermId(), method.getName(), method.getDefinition()));
		standardVariable.setDataType(new Term(DataType.CHARACTER_VARIABLE.getId(), "Character variable", "variable with char values"));
		standardVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));

		this.standardVariableSaver.save(standardVariable);

		return standardVariable;
	}
}
