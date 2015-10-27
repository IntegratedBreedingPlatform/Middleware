package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VariableInfoBuilderTest {

    private VariableInfoBuilder variableInfoBuilder;

    @Before
    public void setUp() throws Exception {
        variableInfoBuilder = new VariableInfoBuilder();
    }


    @Test
    public void testCreate(){

        List<ProjectProperty> projectProperties = new ArrayList<>();

        ProjectProperty standardVariableProp1 = new ProjectProperty();
        standardVariableProp1.setProjectPropertyId(1);
        standardVariableProp1.setRank(2);
        standardVariableProp1.setTypeId(TermId.STANDARD_VARIABLE.getId());
        standardVariableProp1.setValue("8007");
        projectProperties.add(standardVariableProp1);

        ProjectProperty studyTypeProp = new ProjectProperty();
        studyTypeProp.setProjectPropertyId(2);
        studyTypeProp.setRank(2);
        studyTypeProp.setTypeId(VariableType.STUDY_DETAIL.getId());
        studyTypeProp.setValue(VariableType.STUDY_DETAIL.getName());
        projectProperties.add(studyTypeProp);

        ProjectProperty studyInfoProp = new ProjectProperty();
        studyInfoProp.setProjectPropertyId(3);
        studyInfoProp.setRank(2);
        studyInfoProp.setTypeId(TermId.STUDY_INFORMATION.getId());
        studyInfoProp.setValue("STUDY Info Type");
        projectProperties.add(studyInfoProp);

        ProjectProperty varDescriptionProp = new ProjectProperty();
        varDescriptionProp.setProjectPropertyId(4);
        varDescriptionProp.setRank(2);
        varDescriptionProp.setTypeId(TermId.VARIABLE_DESCRIPTION.getId());
        varDescriptionProp.setValue("STUDY Info Type Description");
        projectProperties.add(varDescriptionProp);

        ProjectProperty multiFactorialProp = new ProjectProperty();
        multiFactorialProp.setProjectPropertyId(5);
        multiFactorialProp.setRank(2);
        multiFactorialProp.setTypeId(TermId.MULTIFACTORIAL_INFO.getId());
        multiFactorialProp.setValue("Multi Factorial Info");
        projectProperties.add(multiFactorialProp);

        Set<VariableInfo> variableInfoSet = variableInfoBuilder.create(projectProperties);

	  	// Note: Here we only get 1 variable as all the properties will be used to form single variable instance.
        Assert.assertEquals(1, variableInfoSet.size());

        if(variableInfoSet.size() > 0){
            VariableInfo variableInfo = variableInfoSet.iterator().next();
			Assert.assertEquals(variableInfo.getLocalName(), studyInfoProp.getValue());
			Assert.assertEquals(variableInfo.getLocalDescription(), varDescriptionProp.getValue());
			Assert.assertEquals(variableInfo.getTreatmentLabel(), multiFactorialProp.getValue());
			Assert.assertEquals(variableInfo.getVariableType().getName(), studyTypeProp.getValue());
        }

    }
}
