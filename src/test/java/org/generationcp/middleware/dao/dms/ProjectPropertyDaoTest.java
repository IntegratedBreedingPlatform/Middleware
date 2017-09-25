/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectPropertyDaoTest extends IntegrationTestBase {

	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String CROSS = "CROSS";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";
	public static final String SITE_SOIL_PH = "SITE_SOIL_PH";
	public static final String SITE_SOIL_PH_TERMID = "9999";
	private static ProjectPropertyDao dao;
	private static CVTermDao cvTermDao;

	@Before
	public void setUp() throws Exception {

		dao = new ProjectPropertyDao();
		dao.setSession(this.sessionProvder.getSession());
		cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.sessionProvder.getSession());

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertyNames() throws Exception {

		final List<String> propertyNames = new ArrayList<String>();
		propertyNames.add(TRIAL_INSTANCE);

		final Map<String, Map<Integer, VariableType>> results = this.dao.getStandardVariableIdsWithTypeByPropertyNames(propertyNames);

		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsValue(VariableType.ENVIRONMENT_DETAIL));
		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
	}

	@Test
	public void testConvertToVariablestandardVariableIdsWithTypeMap() {

		final List<Object[]> objectToConvert = this.createObjectToConvert();

		final Map<String, Map<Integer, VariableType>> results = dao.convertToVariablestandardVariableIdsWithTypeMap(objectToConvert);

		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsValue(VariableType.ENVIRONMENT_DETAIL));
		Assert.assertTrue(results.get(TRIAL_INSTANCE).containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
		Assert.assertTrue(results.get(ENTRY_NO).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(ENTRY_NO).containsKey(TermId.ENTRY_NO.getId()));
		Assert.assertTrue(results.get(DESIGNATION).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(DESIGNATION).containsKey(TermId.DESIG.getId()));
		Assert.assertTrue(results.get(GID).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(GID).containsKey(TermId.GID.getId()));
		Assert.assertTrue(results.get(CROSS).containsValue(VariableType.GERMPLASM_DESCRIPTOR));
		Assert.assertTrue(results.get(CROSS).containsKey(TermId.CROSS.getId()));
		Assert.assertTrue(results.get(PLOT_NO).containsValue(VariableType.EXPERIMENTAL_DESIGN));
		Assert.assertTrue(results.get(PLOT_NO).containsKey(TermId.PLOT_NO.getId()));
		Assert.assertTrue(results.get(REP_NO).containsValue(VariableType.EXPERIMENTAL_DESIGN));
		Assert.assertTrue(results.get(REP_NO).containsKey(TermId.REP_NO.getId()));
		Assert.assertTrue(results.get(SITE_SOIL_PH).containsValue(VariableType.TRAIT));
		Assert.assertTrue(results.get(SITE_SOIL_PH).containsKey(Integer.valueOf(SITE_SOIL_PH_TERMID)));

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertyNamesVariableIsObsolete() throws Exception {

		final List<String> propertyNames = Arrays.asList(TRIAL_INSTANCE);

		final CVTerm trialInstanceTerm = cvTermDao.getByName(TRIAL_INSTANCE);
		trialInstanceTerm.setIsObsolete(true);
		cvTermDao.saveOrUpdate(trialInstanceTerm);
		this.sessionProvder.getSession().flush();

		final Map<String, Map<Integer, VariableType>> results = this.dao.getStandardVariableIdsWithTypeByPropertyNames(propertyNames);

		// The TRIAL_INSTANCE variable is obsolete so the result should be empty
		Assert.assertTrue(results.isEmpty());

	}

	private List<Object[]> createObjectToConvert() {

		final List<Object[]> objectToConvert = new ArrayList<>();

		objectToConvert.add(new Object[] {TRIAL_INSTANCE, String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()),
				VariableType.ENVIRONMENT_DETAIL.getId()});
		objectToConvert.add(new Object[] {ENTRY_NO, String.valueOf(TermId.ENTRY_NO.getId()), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {DESIGNATION, String.valueOf(TermId.DESIG.getId()), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {GID, String.valueOf(TermId.GID.getId()), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {CROSS, String.valueOf(TermId.CROSS.getId()), VariableType.GERMPLASM_DESCRIPTOR.getId()});
		objectToConvert.add(new Object[] {PLOT_NO, String.valueOf(TermId.PLOT_NO.getId()), VariableType.EXPERIMENTAL_DESIGN.getId()});
		objectToConvert.add(new Object[] {REP_NO, String.valueOf(TermId.REP_NO.getId()), VariableType.EXPERIMENTAL_DESIGN.getId()});
		objectToConvert.add(new Object[] {SITE_SOIL_PH, SITE_SOIL_PH_TERMID, VariableType.TRAIT.getId()});

		return objectToConvert;
	}

}
