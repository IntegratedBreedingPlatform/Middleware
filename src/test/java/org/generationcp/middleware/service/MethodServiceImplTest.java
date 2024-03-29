package org.generationcp.middleware.service;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MethodServiceImplTest extends IntegrationTestBase {

	@Autowired
	private RoleService roleService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private MethodDAO methodDAO;

	private Project commonTestProject;

	private MethodServiceImpl methodService;

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setUp() {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.methodService = new MethodServiceImpl(this.sessionProvder);
		this.methodDAO = new MethodDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testGetAllBreedingMethods() {
		final List<Method> methodList = this.methodService.getAllBreedingMethods();
		Assert.assertNotNull(methodList);
		Assert.assertFalse(methodList.isEmpty());

		Method method = new Method();
		method.setMname("NEW METHOD NAME");
		method.setMdesc("NEW METHOD DESC");
		method.setMcode("0");
		method.setMgrp("0");
		method.setMtype("GEN");
		method.setReference(0);
		method.setGeneq(0);
		method.setMprgn(0);
		method.setMfprg(0);
		method.setMattr(0);
		method.setUser(0);
		method.setLmid(0);
		method.setMdate(0);
		method = this.methodDAO.save(method);

		final List<Method> newMethodList = this.methodService.getAllBreedingMethods();
		Assert.assertNotNull(newMethodList);
		Assert.assertFalse(newMethodList.isEmpty());
		Assert.assertEquals(newMethodList.size()-1, methodList.size());
		Assert.assertTrue(newMethodList.contains(method));
	}
}
