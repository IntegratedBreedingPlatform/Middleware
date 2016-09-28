
package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.ToolLicenseInfoInitializer;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ToolLicenseInfoDAOTest extends IntegrationTestBase {

	private static ToolLicenseInfoDAO dao;

	@Autowired
	@Qualifier(value = "workbenchSessionProvider")
	private HibernateSessionProvider workbenchSessionProvider;

	@Before
	public void setUp() throws Exception {
		ToolLicenseInfoDAOTest.dao = new ToolLicenseInfoDAO();
		ToolLicenseInfoDAOTest.dao.setSession(this.workbenchSessionProvider.getSession());
	}

	@Test
	public void testGetByToolName() throws Exception {
		final String expectedToolName = ToolName.mbdt.toString();
		final ToolLicenseInfo dummyToolLicenseInfo = new ToolLicenseInfoInitializer().createToolLicenseInfo(expectedToolName);
		final ToolLicenseInfo expectedToolLicenseInfo = ToolLicenseInfoDAOTest.dao.save(dummyToolLicenseInfo);
		final ToolLicenseInfo actualToolLicenseInfo = ToolLicenseInfoDAOTest.dao.getByToolName(expectedToolName);
		Assert.assertEquals(expectedToolLicenseInfo.getLicenseInfoId(), actualToolLicenseInfo.getLicenseInfoId());
		Assert.assertEquals(expectedToolLicenseInfo.getTool(), actualToolLicenseInfo.getTool());
		Assert.assertEquals(expectedToolName, actualToolLicenseInfo.getTool().getToolName());
		Assert.assertEquals(expectedToolLicenseInfo.getLicensePath(), actualToolLicenseInfo.getLicensePath());
		Assert.assertEquals(expectedToolLicenseInfo.getLicenseHash(), actualToolLicenseInfo.getLicenseHash());
		Assert.assertEquals(expectedToolLicenseInfo.getExpirationDate(), actualToolLicenseInfo.getExpirationDate());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		ToolLicenseInfoDAOTest.dao.setSession(null);
		ToolLicenseInfoDAOTest.dao = null;
	}
}
