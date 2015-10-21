package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StandardVariableSaverTest extends IntegrationTestBase{
	private StandardVariableSaver stdVarSaver;
	private CVTermDao cvtermDao;
	
	private final Integer CVID = 2050;
	private final String TESTNAME = "Test Name";
	private final String TESTDEFINITION = "Test Definition";
	
	@Before
	public void setUp(){
		this.stdVarSaver = new StandardVariableSaver(super.sessionProvder);
		this.cvtermDao = this.stdVarSaver.getCvTermDao();
	}
	
	@Test
	public void testSaveCheckType(){
		StandardVariable stdVar = this.createStdVariable();
		Enumeration testEnum = new Enumeration(null, this.TESTNAME, this.TESTDEFINITION, 0);
		this.stdVarSaver.saveEnumeration(stdVar, testEnum, CVID);
		
		CVTerm cvTerm = this.cvtermDao.getByName("Test Name");
		Assert.assertNotNull("The newly created CvTerm should exist in the DB", cvTerm);
		Assert.assertEquals("The newly created CvTerm's name should be " + this.TESTNAME, this.TESTNAME, cvTerm.getName());
		Assert.assertEquals("The newly created CvTerm's description should be " + this.TESTDEFINITION, this.TESTDEFINITION, cvTerm.getDefinition());
	}
	
	public StandardVariable createStdVariable(){
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(61220, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");
		
		return stdVariable;
	}
}
