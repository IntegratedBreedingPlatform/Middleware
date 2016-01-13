
package org.generationcp.middleware.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Please note that this test requires a wheat database with a gid dump to actually run.
 *
 * @author Akhil
 *
 */
public class MaizePedigreeServiceImplTest extends IntegrationTestBase {

	// TODO pedigree service parameters need to be set in testContext.xml
	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private PedigreeService configurablePedigreeService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private CrossExpansionProperties crossExpansionProperties;

	private final PedigreeDataReader pedigreeDataReader = new PedigreeDataReader();

	@Before
	public void setup() {
		this.crossExpansionProperties = new CrossExpansionProperties();
		this.crossExpansionProperties.setDefaultLevel(5);
		this.crossExpansionProperties.setWheatLevel(0);
	}

	@Test
	public void wheatPedigreeMaleBackCross() throws Exception {
		List<Germplasm> allGermplasm = germplasmDataManager.getAllGermplasm(0, 10000);
		for (Germplasm germplasm : allGermplasm) {
			String crossExpansion = pedigreeService.getCrossExpansion(germplasm.getGid(), crossExpansionProperties);
			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(germplasm.getGid(), crossExpansionProperties);
			System.out.println(crossExpansion);
			System.out.println(crossExpansion2);

			Assert.assertEquals(crossExpansion, crossExpansion2);
		}

	}



}
