
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.GermplasmType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.beust.jcommander.internal.Lists;

public class GermplasmNamingReferenceDataResolverImplTest {

	@Mock
	private UserDefinedFieldDAO userDefinedFieldDAO;

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private CVTermDao cvTermDAO;

	private static final UserDefinedField L1_NAME_TYPE =
			new UserDefinedField(41, "NAME", "CODE", "CODE1", "CODE1 Name", "-", "CODE1 Desc", 0, 0, 20160101, 0);

	private static final UserDefinedField L2_NAME_TYPE =
			new UserDefinedField(42, "NAME", "CODE", "CODE2", "CODE2 Name", "-", "CODE2 Desc", 0, 0, 20160101, 0);

	private static final UserDefinedField L3_NAME_TYPE =
			new UserDefinedField(43, "NAME", "CODE", "CODE3", "CODE3 Name", "-", "CODE3 Desc", 0, 0, 20160101, 0);

	@InjectMocks
	private final GermplasmNamingReferenceDataResolverImpl service = new GermplasmNamingReferenceDataResolverImpl();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.userDefinedFieldDAO.getByFieldTableNameAndType("NAMES", "CODE"))
				.thenReturn(Lists.newArrayList(L3_NAME_TYPE, L2_NAME_TYPE, L1_NAME_TYPE));
	}

	@Test
	public void testResolveNameType() {
		Assert.assertEquals(L1_NAME_TYPE, this.service.resolveNameType(1));
		Assert.assertEquals(L2_NAME_TYPE, this.service.resolveNameType(2));
		Assert.assertEquals(L3_NAME_TYPE, this.service.resolveNameType(3));
	}

	@Test(expected = IllegalStateException.class)
	public void testResolveNameTypeNotEnoughLevelsSetup() {
		// We have three levels setup. We request level 4 code. Expect exception.
		this.service.resolveNameType(4);
	}

	@Test(expected = IllegalStateException.class)
	public void testResolveNameTypeWhenNotSetup() {
		Mockito.when(this.userDefinedFieldDAO.getByFieldTableNameAndType("NAMES", "CODE")).thenReturn(new ArrayList<UserDefinedField>());
		this.service.resolveNameType(1);
	}

	@Test
	public void testGetGermplasmTypes() {
		Assert.assertEquals("Did not get expected number of germplasm types.", GermplasmType.values().length,
				this.service.getGermplasmTypes().size());
	}

	@Test
	public void testGetCategoryValuesVariableCorrectlySetup() {
		CVTerm variableTerm = new CVTerm();
		variableTerm.setCvTermId(3001);
		Mockito.when(this.cvTermDAO.getByNameAndCvId(Mockito.anyString(), Mockito.anyInt())).thenReturn(variableTerm);

		Variable variable = new Variable();
		Scale scale = new Scale();
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		TermSummary category1 = new TermSummary(1, "AA", "Administrator Hyderabad");
		TermSummary category2 = new TermSummary(2, "AE", "Administrator Kenya");
		scale.addCategory(category1);
		scale.addCategory(category2);
		variable.setScale(scale);

		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.anyString(), Matchers.eq(variableTerm.getCvTermId()),
				Matchers.eq(true), Matchers.eq(false))).thenReturn(variable);

		List<String> serviceResult = this.service.getCategoryValues("Project_Prefix", "uuidgobbledygook");
		Assert.assertTrue(serviceResult.contains(category1.getName()));
		Assert.assertTrue(serviceResult.contains(category2.getName()));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCategoryValuesVariableSetupWithWrongScale() {
		CVTerm variableTerm = new CVTerm();
		variableTerm.setCvTermId(3001);
		Mockito.when(this.cvTermDAO.getByNameAndCvId(Mockito.anyString(), Mockito.anyInt())).thenReturn(variableTerm);

		Variable variable = new Variable();
		Scale scale = new Scale();
		scale.setDataType(DataType.NUMERIC_VARIABLE);
		variable.setScale(scale);

		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.anyString(), Matchers.eq(variableTerm.getCvTermId()),
				Matchers.eq(true), Matchers.eq(false))).thenReturn(variable);

		this.service.getCategoryValues("Project_Prefix", "uuidgobbledygook");
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCategoryValuesVariableNotSetup() {
		Mockito.when(this.cvTermDAO.getByNameAndCvId(Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
		this.service.getCategoryValues("Project_Prefix", "uuidgobbledygook");
	}

}
