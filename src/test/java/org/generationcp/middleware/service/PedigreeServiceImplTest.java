package org.generationcp.middleware.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PedigreeServiceImplTest extends DataManagerIntegrationTest {
	
	private PedigreeService pedigreeCimmytWheatService;
	private PedigreeService pedigreeNonWheatService;
	private CrossExpansionProperties crossExpansionProperties;
	
	@Before
	public void setUp() {
		pedigreeCimmytWheatService = managerFactory.getPedigreeService(PedigreeFactory.PROFILE_CIMMYT, CropType.CropEnum.WHEAT.toString());
		pedigreeNonWheatService = managerFactory.getPedigreeService(PedigreeFactory.PROFILE_DEFAULT, CropType.CropEnum.RICE.toString());
		crossExpansionProperties = new CrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);
		crossExpansionProperties.setWheatLevel(0);
	}
	
	/**
	 * Temporary test harness to use for invoking the actual service and generate a pedigree strings with CIMMYT algorithm.
	 */
    @Test
    public void getCrossExpansionCimmytWheat() throws Exception {
    	
    	String crossString1 = pedigreeCimmytWheatService.getCrossExpansion(342454, crossExpansionProperties);
    	String crossString7 = pedigreeCimmytWheatService.getCrossExpansion(342454, crossExpansionProperties);
    	String crossString17 = pedigreeCimmytWheatService.getCrossExpansion(342454, crossExpansionProperties);
    	
    	Debug.println(INDENT, "Cross string ntype = 1 [" +crossString1 + "]") ;
    	Debug.println(INDENT, "Cross string ntype = 7 [" +crossString7 + "]") ;
    	Debug.println(INDENT, "Cross string ntype = 17 [" +crossString17 + "]") ;
    }

    @Test
    public void testCrossExpansionSwitchingLogicNonCimmyt() throws MiddlewareQueryException{
    	Map<Integer, String> gidCrossMapping = new HashMap<Integer, String>();
    	gidCrossMapping.put(new Integer(1935), "27217-A-4-8/BAART");
    	gidCrossMapping.put(new Integer(14898), "THATCHER*6/RL 5406");
    	gidCrossMapping.put(new Integer(5781802), "ND/VG9144//KAL/BB/3/YACO/VEERY #5");
    	gidCrossMapping.put(new Integer(14790), "TC*4/AGENT");
    	
    	Iterator<Integer> gidKeys = gidCrossMapping.keySet().iterator();
    	while(gidKeys.hasNext()){
    		Integer gid = gidKeys.next();
    		String crossString = pedigreeNonWheatService.getCrossExpansion(gid, crossExpansionProperties);
    		String expected = gidCrossMapping.get(gid);
    		Assert.assertEquals("Should have the same cross string using the current logic of non cimmyt wheat cross string generation", expected,  crossString);
    	}
    }
    
    @Test
    public void testCrossExpansionSwitchingLogicCimmyt() throws MiddlewareQueryException{
    	   
    	Map<Integer, String> gidCrossMapping = new HashMap<Integer, String>();
    	gidCrossMapping.put(new Integer(1935), "27217-A-4-8/BRT");
    	gidCrossMapping.put(new Integer(14898), "RL6043");
    	gidCrossMapping.put(new Integer(5781802), "CM85836-4Y-0M-0Y-8M-0Y-0IND");
    	gidCrossMapping.put(new Integer(14790), "TC*4/AG");
    	
    	Iterator<Integer> gidKeys = gidCrossMapping.keySet().iterator();
    	while(gidKeys.hasNext()){
    		Integer gid = gidKeys.next();
    		String crossString = pedigreeCimmytWheatService.getCrossExpansion(gid, crossExpansionProperties);
    		String expected = gidCrossMapping.get(gid);
    		Assert.assertEquals("Should have the same cross string using the current logic of cimmyt wheat cross string generation", expected,  crossString);
    	}
    }

}
