/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StandardVariableTest {
	
	private static final String VAR_NAME = "VARIABLE1";
	private static final String VAR_DEF = "VARIABLE DESCRIPTION";
	private static final int PROPERTY_ID = -1;
	private static final String PROPERTY_NAME = "PROPERTY";
	private static final String PROPERTY_DEF = "PROPERTY DEF";
	private static final int SCALE_ID = -2;
	private static final String SCALE_NAME = "SCALE";
	private static final String SCALE_DEF = "SCALE DEF";
	private static final int METHOD_ID = -3;
	private static final String METHOD_NAME = "METHOD";
	private static final String METHOD_DEF = "METHOD DEF";
	private static final int DATA_TYPE_ID = -4;
	private static final String DATA_TYPE_NAME = "DATA TYPE";
	private static final String DATA_TYPE_DEF = "DATA TYPE DEF";
	private static final int STORED_IN_ID = -5;
	private static final String STORED_IN_NAME = "STORED_IN";
	private static final String STORED_IN_DEF = "STORED IN DEF";
	private static final int TRAIT_CLASS_ID = -6;
	private static final String TRAIT_CLASS_NAME = "TRAIT CLASS";
	private static final String TRAIT_CLASS_DEF = "TRAIT CLASS DEF";
	
	private static final int NUMERIC_VAR_DATA_TYPE_ID = TermId.NUMERIC_VARIABLE.getId();
	private static final String NUMERIC_VAR_DATA_TYPE_NAME = "Numeric variable";
	private static final String NUMERIC_VAR_DATA_TYPE_DEF = "Variable with numeric values either continuous or integer";
	
	private static final int CATEGORICAL_VAR_DATA_TYPE_ID = TermId.CATEGORICAL_VARIABLE.getId();
	private static final String CATEGORICAL_VAR_DATA_TYPE_NAME = "Categorical  variable";
	private static final String CATEGORICAL_VAR_DATA_TYPE_DEF = "Variable with discrete class values (numeric or character all treated as character)";
	
	private static final int CATEGORICAL_VAR_STORED_IN_ID = TermId.CATEGORICAL_VARIATE.getId();
	private static final String CATEGORICAL_VAR_STORED_IN_NAME = "Categorical variate";
	private static final String CATEGORICAL_VAR_STORED_IN_DEF = "Categorical variate with values stored in phenotype.cvalue_id";
	
	private static final int CATEGORICAL_VAR_NUMERIC_ENUM_ID = -7;
	private static final String CATEGORICAL_VAR_NUMERIC_ENUM_NAME = "0";
	private static final String CATEGORICAL_VAR_NUMERIC_ENUM_DEF = "No disease";
	
	private static final int CATEGORICAL_VAR_NON_NUMERIC_ENUM_ID = -8;
	private static final String CATEGORICAL_VAR_NON_NUMERIC_ENUM_NAME = "Dry";
	private static final String CATEGORICAL_VAR_NON_NUMERIC_ENUM_DEF = "Dry";
	
    private static StandardVariable standardVariable;
    
    @BeforeClass
    public static void setup() {
    	getTestStandardVariable();
    }
    
		
    private static StandardVariable getTestStandardVariable(){
		if (standardVariable == null){
			standardVariable = createStandardVariableTestData();
		}
		return standardVariable;
	}
	
	
	private static StandardVariable createStandardVariableTestData(){
		StandardVariable var = new StandardVariable();
		var.setId(0);
		var.setName(VAR_NAME);
		var.setDescription(VAR_DEF);
		var.setCropOntologyId(null);
	    var.setProperty(new Term(PROPERTY_ID,PROPERTY_NAME,PROPERTY_DEF));
		var.setScale(new Term(SCALE_ID,SCALE_NAME,SCALE_DEF));
		var.setMethod(new Term(METHOD_ID,METHOD_NAME,METHOD_DEF));
	    var.setDataType(new Term(DATA_TYPE_ID,DATA_TYPE_NAME,DATA_TYPE_DEF));
	    var.setStoredIn(new Term(STORED_IN_ID,STORED_IN_NAME,STORED_IN_DEF));
	    var.setIsA(new Term(TRAIT_CLASS_ID,TRAIT_CLASS_NAME,TRAIT_CLASS_DEF));
	    var.setPhenotypicType(PhenotypicType.VARIATE);
	    var.setConstraints(null);
	    var.setEnumerations(null);
	    return var;
	}
	
	@Test
	public void testIsNumeric() {
		standardVariable.setDataType(new Term(
				NUMERIC_VAR_DATA_TYPE_ID,NUMERIC_VAR_DATA_TYPE_NAME,NUMERIC_VAR_DATA_TYPE_DEF));
		Assert.assertTrue("A standard variable with DATA TYPE EQUAL TO NUMERIC VARIABLE is numeric.",
				standardVariable.isNumeric());
	}
	
	@Test
	public void testIsNumericIfDataTypeIsNotNumericAndIsNumericalCategoricalVariate() {
		standardVariable.setDataType(new Term(
				CATEGORICAL_VAR_DATA_TYPE_ID,CATEGORICAL_VAR_DATA_TYPE_NAME,CATEGORICAL_VAR_DATA_TYPE_DEF));
		standardVariable.setStoredIn(new Term(
				CATEGORICAL_VAR_STORED_IN_ID,CATEGORICAL_VAR_STORED_IN_NAME,CATEGORICAL_VAR_STORED_IN_DEF));
		
		List<Enumeration> validValues = new ArrayList<Enumeration>();
		validValues.add(new Enumeration(CATEGORICAL_VAR_NUMERIC_ENUM_ID, 
				CATEGORICAL_VAR_NUMERIC_ENUM_NAME, CATEGORICAL_VAR_NUMERIC_ENUM_DEF, 1));
		standardVariable.setEnumerations(validValues);
		
		Assert.assertTrue(standardVariable.isNumericCategoricalVariate());
		Assert.assertTrue("A standard variable with DATA TYPE EQUAL NOT EQUAL TO NUMERIC VARIABLE and " +
				"is A NUMERIC CATEGORICAL VARIATE is numeric.",
				standardVariable.isNumeric());
	}
	
	@Test
	public void testIsNumericIfDataTypeIsNotNumericAndIsNotNumericalCategoricalVariate() {
		standardVariable.setDataType(new Term(
				CATEGORICAL_VAR_DATA_TYPE_ID,CATEGORICAL_VAR_DATA_TYPE_NAME,CATEGORICAL_VAR_DATA_TYPE_DEF));
		standardVariable.setStoredIn(null);
		Assert.assertFalse(standardVariable.isNumericCategoricalVariate());
		Assert.assertFalse("A standard variable with DATA TYPE EQUAL NOT EQUAL TO NUMERIC VARIABLE and " +
				"is NOT A NUMERIC CATEGORICAL VARIATE is numeric.",
				standardVariable.isNumeric());
	}
	
	@Test
	public void testIsNumericCategoricalVariate() {
		standardVariable.setStoredIn(new Term(
				CATEGORICAL_VAR_STORED_IN_ID,CATEGORICAL_VAR_STORED_IN_NAME,CATEGORICAL_VAR_STORED_IN_DEF));
		List<Enumeration> validValues = new ArrayList<Enumeration>();
		validValues.add(new Enumeration(CATEGORICAL_VAR_NUMERIC_ENUM_ID, 
				CATEGORICAL_VAR_NUMERIC_ENUM_NAME, CATEGORICAL_VAR_NUMERIC_ENUM_DEF, 1));
		standardVariable.setEnumerations(validValues);
		Assert.assertTrue("A standard variable with ROLE EQUAL to CATEGORICAL VARIATE " +
				"with NUMERIC VALID VALUES only is a numeric categorical variate.",
				standardVariable.isNumericCategoricalVariate());
	}
	
	@Test
	public void testIsNumericCategoricalVariateIfRoleIsNull() {
		standardVariable.setStoredIn(null);
		Assert.assertFalse("A standard variable with NO ROLE is not a numeric categorical variate.",
				standardVariable.isNumericCategoricalVariate());
	}
	
	@Test
	public void testIsNumericCategoricalVariateWithNoValidValues() {
		standardVariable.setStoredIn(new Term(
				CATEGORICAL_VAR_STORED_IN_ID,CATEGORICAL_VAR_STORED_IN_NAME,CATEGORICAL_VAR_STORED_IN_DEF));
		standardVariable.setEnumerations(null);
		Assert.assertFalse("A standard variable with ROLE EQUAL to CATEGORICAL VARIATE " +
				"with NO VALID VALUES may be a numeric or non-numeric categorical variate.",
				standardVariable.isNumericCategoricalVariate());
	}
	
	@Test
	public void testIsNumericCategoricalVariateWithNonNumericValidValues() {
		standardVariable.setDataType(new Term(
				CATEGORICAL_VAR_DATA_TYPE_ID,CATEGORICAL_VAR_DATA_TYPE_NAME,CATEGORICAL_VAR_DATA_TYPE_DEF));
		standardVariable.setStoredIn(new Term(
				CATEGORICAL_VAR_STORED_IN_ID,CATEGORICAL_VAR_STORED_IN_NAME,CATEGORICAL_VAR_STORED_IN_DEF));
		
		List<Enumeration> validValues = new ArrayList<Enumeration>();
		validValues.add(new Enumeration(CATEGORICAL_VAR_NUMERIC_ENUM_ID, 
				CATEGORICAL_VAR_NUMERIC_ENUM_NAME, CATEGORICAL_VAR_NUMERIC_ENUM_DEF, 1));
		validValues.add(new Enumeration(CATEGORICAL_VAR_NON_NUMERIC_ENUM_ID, 
				CATEGORICAL_VAR_NON_NUMERIC_ENUM_NAME, CATEGORICAL_VAR_NON_NUMERIC_ENUM_DEF, 1));
		standardVariable.setEnumerations(validValues);
		Assert.assertFalse("A standard variable with categorical variate as role " +
				"with non-numeric valid values is non-numeric.",standardVariable.isNumericCategoricalVariate());
	}
	
	

}
