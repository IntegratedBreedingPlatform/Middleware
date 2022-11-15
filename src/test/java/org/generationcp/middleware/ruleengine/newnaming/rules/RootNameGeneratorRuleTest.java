
package org.generationcp.middleware.ruleengine.newnaming.rules;

import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class RootNameGeneratorRuleTest extends BaseNamingRuleTest {

	private RootNameGeneratorRule rootNameGeneratorRule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.row = Mockito.mock(AdvancingSource.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.row.getBreedingMethod()).thenReturn(this.breedingMethod);
		this.testGermplasmName = "advance-germplasm-name";
		this.rootNameGeneratorRule = new RootNameGeneratorRule();

	}

	private BasicNameDTO generateNewName(final Integer typeId, final  Integer nStat) {
		final BasicNameDTO name = new BasicNameDTO();
		name.setTypeId(typeId);
		name.setNstat(nStat);
		name.setNval(this.testGermplasmName);
		return name;
	}

	@Test
	public void testGetGermplasmRootNameWithTheSameSnameTypeWithMethod() {
		final List<BasicNameDTO> names = new ArrayList<>();
		names.add(this.generateNewName(this.breedingMethodSnameType, 1));
		Mockito.when(this.row.getNames()).thenReturn(names);
		List<String> input = new ArrayList<String>();

		try {
			input = (List<String>) this.rootNameGeneratorRule.runRule(this.createExecutionContext(input));
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct root name if the methd snametype is equal to the names' type id",
				this.testGermplasmName, input.get(0));
	}

}
