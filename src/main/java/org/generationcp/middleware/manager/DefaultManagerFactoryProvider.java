package org.generationcp.middleware.manager;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 * The {@link DefaultManagerFactoryProvider} is an implementation of
 * {@link ManagerFactoryProvider} that expects central databases are named using
 * the format: <br>
 * <code>ibdb_&lt;crop type&gt;_central</code><br>
 * and local databases are named using the format: <br>
 * <code>&lt;crop type&gt;_&lt;project id&gt;_local</code><br>
 * 
 * @author Glenn Marintes
 */
public class DefaultManagerFactoryProvider implements ManagerFactoryProvider {
    private Map<Long, ManagerFactory> managerFactories = new HashMap<Long, ManagerFactory>();
    private Map<CropType, ManagerFactory> centralManagerFactories = new HashMap<CropType, ManagerFactory>();
    
    private String localHost = "localhost";

    private Integer localPort = 13306;

    private String localUsername = "local";

    private String localPassword = "local";

    private String centralHost = "localhost";

    private Integer centralPort = 13306;

    private String centralUsername = "central";

    private String centralPassword = "central";

    public void setManagerFactories(Map<Long, ManagerFactory> managerFactories) {
        this.managerFactories = managerFactories;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public void setLocalUsername(String localUsername) {
        this.localUsername = localUsername;
    }

    public void setLocalPassword(String localPassword) {
        this.localPassword = localPassword;
    }

    public void setCentralHost(String centralHost) {
        this.centralHost = centralHost;
    }

    public void setCentralPort(Integer centralPort) {
        this.centralPort = centralPort;
    }

    public void setCentralUsername(String centralUsername) {
        this.centralUsername = centralUsername;
    }

    public void setCentralPassword(String centralPassword) {
        this.centralPassword = centralPassword;
    }

    @Override
    public synchronized ManagerFactory getManagerFactoryForProject(Project project) {
        ManagerFactory factory = managerFactories.get(project.getProjectId());
        if (factory == null) {
            String localDbName = String.format("%s_%d_local", project.getCropType().getCropName().toLowerCase(), project.getProjectId().longValue());
            String centralDbName = String.format("ibdb_%s_central", project.getCropType().getCropName().toLowerCase());

            DatabaseConnectionParameters paramsForLocal = new DatabaseConnectionParameters(localHost, String.valueOf(localPort), localDbName, localUsername, localPassword);
            DatabaseConnectionParameters paramsForCentral = new DatabaseConnectionParameters(centralHost, String.valueOf(centralPort), centralDbName, centralUsername, centralPassword);

            factory = new ManagerFactory(paramsForLocal, paramsForCentral);
            managerFactories.put(project.getProjectId(), factory);
        }

        return factory;
    }
    
    @Override
    public ManagerFactory getManagerFactoryForCropType(CropType cropType) {
        ManagerFactory factory = centralManagerFactories.get(cropType);
        if (factory == null) {
            String centralDbName = String.format("ibdb_%s_central", cropType.getCropName().toLowerCase());

            DatabaseConnectionParameters paramsForCentral = new DatabaseConnectionParameters(centralHost, String.valueOf(centralPort), centralDbName, centralUsername, centralPassword);

            factory = new ManagerFactory(null, paramsForCentral);
            centralManagerFactories.put(cropType, factory);
        }

        return factory;
    }

    @Override
    public synchronized void close() {
        for (Long projectId : managerFactories.keySet()) {
            ManagerFactory factory = managerFactories.get(projectId);
            if (factory == null) {
                continue;
            }

            factory.close();
        }
        
        for (CropType cropType : centralManagerFactories.keySet()) {
            ManagerFactory factory = centralManagerFactories.get(cropType);
            if (factory == null) {
                continue;
            }

            factory.close();
        }

        managerFactories.clear();
    }
}
