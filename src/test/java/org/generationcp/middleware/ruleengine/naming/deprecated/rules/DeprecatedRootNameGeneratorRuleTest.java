
package org.generationcp.middleware.ruleengine.naming.deprecated.rules;

import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedRootNameGeneratorRuleTest extends BaseNamingRuleTest {

	private DeprecatedRootNameGeneratorRule deprecatedRootNameGeneratorRule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.row = new DeprecatedAdvancingSource();
		this.row.setBreedingMethod(this.breedingMethod);
		this.testGermplasmName = "advance-germplasm-name";
		this.deprecatedRootNameGeneratorRule = new DeprecatedRootNameGeneratorRule();

	}

	private Name generateNewName(Integer typeId, Integer nStat) {
		Name name = new Name();
		name.setTypeId(typeId);
		name.setNstat(nStat);
		name.setNval(this.testGermplasmName);
		return name;
	}

	@Test
	public void testGetGermplasmRootNameWithTheSameSnameTypeWithMethod() {
		List<Name> names = new ArrayList<Name>();
		names.add(this.generateNewName(this.breedingMethodSnameType, 1));
		this.row.setNames(names);
		List<String> input = new ArrayList<String>();

		try {
			input = (List<String>) this.deprecatedRootNameGeneratorRule.runRule(this.createExecutionContext(input));
		} catch (RuleException re) {
			Assert.fail("Should return the correct root name if the methd snametype is equal to the names' type id");
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct root name if the methd snametype is equal to the names' type id",
				this.testGermplasmName, input.get(0));
	}

	// @Test
	// public void testGetGermplasmRootNameWithTheDifferentSnameTypeWithMethodButWithNstatEqualTo1(){
	// List<Name> names = new ArrayList<Name>();
	// names.add(generateNewName(2, 1));
	// row.setNames(names);
	// List<String> input = new ArrayList<String>();
	// try{
	// rootName = namingConventionService.getGermplasmRootName(breedingMethodSnameType, row);
	// }catch(RuleException re){
	// Assert.fail("Should return the correct root name if the methd snametype is equal to the names' type id");
	// }
	// Assert.assertEquals("Should return the correct root name if the names' nstat is equal to 1", testGermplasmName, rootName);
	// }

	// @Test
	// public void testGetGermplasmRootNameWithTheDifferentSnameTypeWithMethodWithnstatNotEqualTo1(){
	// List<Name> names = new ArrayList<Name>();
	// names.add(generateNewName( 2, 0));
	// row.setNames(names);
	// row.setGermplasm(new ImportedGermplasm());
	// boolean throwsException = false;
	// try{
	// namingConventionService.getGermplasmRootName(breedingMethodSnameType, row);
	// }catch(MiddlewareQueryException e){
	// throwsException = true;
	// }catch(NoSuchMessageException e){
	// throwsException = true;
	// }
	// Assert.assertTrue("Should throw an exception if there is no germplasm root name retrieved", throwsException);
	// }
}
