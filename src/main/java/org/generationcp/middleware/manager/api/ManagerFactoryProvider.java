package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 * A {@link ManagerFactoryProvider} provides a method for getting an instance of
 * ManagerFactory.
 * 
 * @author Glenn Marintes
 */
public interface ManagerFactoryProvider {
    
    /**
     * Get a {@link ManagerFactory} setup to connect to central and local
     * databases needed by the specified {@link Project}. When done using the
     * ManagerFactory, call
     * {@link ManagerFactoryProvider#closeManagerFactory(ManagerFactory)} to
     * close the database connections used by the {@link ManagerFactory}.
     * 
     * @param project
     * @return The ManagerFactory for the given project.
     */
    public ManagerFactory getManagerFactoryForProject(Project project);
    
    /**
     * Get the {@link ManagerFactory} setup connected to the central of the
     * specified {@link CropType} but has no connection to a local database.
     * When done using the ManagerFactory, call
     * {@link ManagerFactoryProvider#closeManagerFactory(ManagerFactory)} to
     * close the database connections used by the {@link ManagerFactory}.
     * 
     * @param cropType
     * @return The ManagerFactory for the given crop type.
     */
    public ManagerFactory getManagerFactoryForCropType(CropType cropType);
    
    /**
     * Close this ManagerFactoryProvider.<br>
     * Calling this method will close all ManagerFactory created.
     */
    public void close();
}
