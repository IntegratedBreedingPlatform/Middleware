package org.generationcp.middleware.manager.test;

import junit.framework.Assert;

import org.generationcp.middleware.manager.DefaultManagerFactoryProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Test;

public class TestManagerFactoryProvider {

    @Test
    public void testManagerFactoryByCropType() {
        ManagerFactoryProvider provider = new DefaultManagerFactoryProvider();
        ManagerFactory factory = null;
        
        factory = provider.getManagerFactoryForCropType(CropType.CASSAVA);
        Assert.assertNotNull(factory);
        
        factory = provider.getManagerFactoryForCropType(CropType.CHICKPEA);
        Assert.assertNotNull(factory);
        
        Project project = new Project();
        project.setProjectId(1L);
        project.setCropType(CropType.CHICKPEA);
        factory = provider.getManagerFactoryForProject(project);
        Assert.assertNotNull(factory);
        
        provider.close();
    }
}
