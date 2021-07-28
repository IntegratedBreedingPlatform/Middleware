package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

public class SampleServiceImplTest extends IntegrationTestBase {

    private DmsProjectDao dmsProjectDao;
    private ExperimentDao experimentDao;
    private IntegrationTestDataInitializer testDataInitializer;
    private WorkbenchDataManager workbenchDataManager;

    @Resource
    private SampleService sampleService;

    @Before
    public void setUp() {

        this.dmsProjectDao = new DmsProjectDao();
        this.dmsProjectDao.setSession(this.sessionProvder.getSession());
        this.experimentDao = new ExperimentDao();
        this.experimentDao.setSession(this.sessionProvder.getSession());
        this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
    }

}
