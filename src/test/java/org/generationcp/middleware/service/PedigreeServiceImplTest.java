package org.generationcp.middleware.service;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;


public class PedigreeServiceImplTest extends DataManagerIntegrationTest {
	
	private PedigreeService pedigreeService;
	
	@Before
	public void setUp() {
		pedigreeService = managerFactory.getPedigreeService();
	}
	
	/**
	 * Temporary test harness to use for invoking the actual service and generate a pedigree strings with CIMMYT algorithm.
	 */
    @Test
    public void getCrossExpansionCimmytWheat() throws Exception {
    	
    	String crossString1 = pedigreeService.getCrossExpansionCimmytWheat(342454, 1, 1);
    	String crossString7 = pedigreeService.getCrossExpansionCimmytWheat(342454, 1, 7);
    	String crossString17 = pedigreeService.getCrossExpansionCimmytWheat(342454, 1, 17);
    	
    	Debug.println(INDENT, "Cross string ntype = 1 [" +crossString1 + "]") ;
    	Debug.println(INDENT, "Cross string ntype = 7 [" +crossString7 + "]") ;
    	Debug.println(INDENT, "Cross string ntype = 17 [" +crossString17 + "]") ;
    }


}
