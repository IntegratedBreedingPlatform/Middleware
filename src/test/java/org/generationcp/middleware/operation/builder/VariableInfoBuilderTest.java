package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class VariableInfoBuilderTest {

    private VariableInfoBuilder variableInfoBuilder;

    @Before
    public void setUp() throws Exception {
        variableInfoBuilder = new VariableInfoBuilder();
    }

	/**
	 * Test to verify create method of VariableInfoBuilder.
	 */

    @Test
    public void testCreate(){

        List<ProjectProperty> projectProperties = new ArrayList<>();

		final String variableTypeProp = VariableType.TRAIT.getName();
		final String studyInformationProp = "STUDY Info Type";
		final String multiFactorialInformationProp = "Multi Factorial Info";


		projectProperties.add(this.createProjectProperty(2, 2, TermId.STUDY_INFORMATION.getId(), studyInformationProp, studyInformationProp));
        projectProperties.add(this.createProjectProperty(4, 2, VariableType.TRAIT.getId(), variableTypeProp, variableTypeProp));
        projectProperties.add(this.createProjectProperty(5, 2, TermId.MULTIFACTORIAL_INFO.getId(), multiFactorialInformationProp, multiFactorialInformationProp));

        Set<VariableInfo> variableInfoSet = variableInfoBuilder.create(projectProperties);

		String message = "The %s for VariableInfo was not mapped correctly.";

		Assert.assertEquals(3, variableInfoSet.size());

		for (VariableInfo variableInfo:variableInfoSet) {
			if (variableInfo.getLocalName().equalsIgnoreCase(studyInformationProp)) {
				Assert.assertEquals(String.format(message, "Local Name"), studyInformationProp, variableInfo.getLocalName());
				Assert.assertEquals(2, variableInfo.getRank());
			}
			else if (variableInfo.getLocalName().equalsIgnoreCase(variableTypeProp)) {
				Assert.assertEquals(String.format(message, "Variable Type Name"), variableTypeProp, variableInfo.getVariableType().getName());
				Assert.assertEquals(2, variableInfo.getRank());
			}
			else {
				Assert.assertEquals(String.format(message, "Treatment Label"), multiFactorialInformationProp, variableInfo.getLocalName());
				Assert.assertEquals(2, variableInfo.getRank());
			}
		}
    }

	/**
	 * method to create instance of ProjectProperty based on given raw data.
	 * @param projectPropId project property id
	 * @param rank rank
	 * @param typeId type of property
	 * @param value value of property
	 * @param alias
	 * @return newly created instance of project property.
	 */
	private ProjectProperty createProjectProperty(final Integer projectPropId, final Integer rank, final Integer typeId, final String value,
		String alias){
		ProjectProperty projectProperty = new ProjectProperty();

		projectProperty.setProjectPropertyId(projectPropId);
		projectProperty.setRank(rank);
		projectProperty.setTypeId(typeId);
		projectProperty.setValue(value);
		projectProperty.setVariableId(new Random().nextInt(10000));
		projectProperty.setAlias(alias);


		return projectProperty;
	}
}
