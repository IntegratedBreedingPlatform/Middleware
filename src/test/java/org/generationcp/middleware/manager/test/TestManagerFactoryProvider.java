
package org.generationcp.middleware.manager.test;

import junit.framework.Assert;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.DefaultManagerFactoryProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestManagerFactoryProvider{

    private static WorkbenchDataManager manager;
    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil("localhost", "3306", "workbench", "root", "admin");
        manager = new WorkbenchDataManagerImpl(hibernateUtil);
    }

    @Test
    public void testManagerFactoryByCropType() {
        ManagerFactoryProvider provider = new DefaultManagerFactoryProvider();
        ManagerFactory factory = null;

        try {
            factory = provider.getManagerFactoryForCropType(manager.getCropTypeByName(CropType.CASSAVA));
            Assert.assertNotNull(factory);

            factory = provider.getManagerFactoryForCropType(manager.getCropTypeByName(CropType.CHICKPEA));
            Assert.assertNotNull(factory);

            Project project = new Project();
            project.setProjectId(1L);
            project.setCropType(manager.getCropTypeByName(CropType.CHICKPEA));
            factory = provider.getManagerFactoryForProject(project);
            Assert.assertNotNull(factory);

        } catch (QueryException e) {
            System.out.println("Error in testManagerFactoryByCropType(): " + e.getMessage());
            e.printStackTrace();
        }
        provider.close();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }
}
