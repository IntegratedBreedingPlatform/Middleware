/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager.test;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmAddUpdateFunctions{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
    }

    /**
     * @Test public void testUpdateGermplasmPrefName() throws Exception { try {
     *       manager.updateGermplasmPrefName(1, "GCP-TEST 1"); } catch
     *       (Exception e) { e.printStackTrace(); } }
     * @Test public void testUpdateGermplasmPrefAbbrev() throws Exception { try
     *       { manager.updateGermplasmPrefAbbrev(1, "GCP-TEST 2"); } catch
     *       (Exception e) { e.printStackTrace(); } }
     * @Test public void testAddAndUpdateGermplasmName() throws Exception {
     *       System.out.println("***** testAddGermplasmName *****"); Name name =
     *       new Name( null, 1, 1, 2, 112, "GCP Test Germplasm Name", 9016,
     *       20120406, 12); System.out.println("addGermplasmName(): " +
     *       manager.addGermplasmName(name));
     * 
     *       System.out.println("***** testUpdateGermplasmName *****"); name =
     *       manager.getGermplasmNameByID(-2);
     *       name.setNval("GCP Test Germplasm Name UPDATE");
     *       System.out.println("updateGermplasmName(): " +
     *       manager.updateGermplasmName(name)); }
     * @Test public void testAddLocation() throws Exception { UserDefinedField
     *       type = manager.getUserDefinedFieldByID(405);
     *       System.out.println(type); Country country =
     *       manager.getCountryById(229); System.out.println(country); Location
     *       location = new Location(null, type, 0, "GCP Test Location 1",
     *       "GCP-1", 0, 0, 0, country, 0); manager.addLocation(location); }
     * @Test public void testAddBibliographicReference() throws Exception {
     *       Bibref bibref = new Bibref( null, "GCP Test Authors",
     *       "GCP Test Editors", "GCP Analytic Title", "GCP Monographic Title",
     *       "Series", "Volume", "Issue", "GCP Page Collation", "GCP Publisher",
     *       "GCP Publishing City", "GCP Publisiong Country"); UserDefinedField
     *       type = manager.getUserDefinedFieldByID(436); bibref.setType(type);
     *       manager.addBibliographicReference(bibref); }
     * @Test public void testAddGermplasmAttribute() throws Exception {
     *       Attribute a = new Attribute(); a.setGermplasmId(new
     *       Integer(50533)); a.setAdate(new Integer(20120406));
     *       a.setAval("sample attribute"); a.setLocationId(new Integer(1538));
     *       a.setReferenceId(null); a.setTypeId(new Integer(103));
     *       a.setUserId(new Integer(1)); int returned =
     *       manager.addGermplasmAttribute(a); Assert.assertTrue(returned == 1);
     *       }
     * @Test public void testAddGermplasmAttributeGivenList() throws Exception {
     *       List<Attribute> attributes = new ArrayList<Attribute>();
     * 
     *       Attribute a = new Attribute(); a.setGermplasmId(new
     *       Integer(50533)); a.setAdate(new Integer(20120406));
     *       a.setAval("sample attribute"); a.setLocationId(new Integer(1538));
     *       a.setReferenceId(null); a.setTypeId(new Integer(103));
     *       a.setUserId(new Integer(1)); attributes.add(a);
     * 
     *       Attribute a1 = new Attribute(); a1.setGermplasmId(new
     *       Integer(156)); a1.setAdate(new Integer(20120406));
     *       a1.setAval("sample attribute 2"); a1.setLocationId(new
     *       Integer(1538)); a1.setReferenceId(null); a1.setTypeId(new
     *       Integer(103)); a1.setUserId(new Integer(1)); attributes.add(a1);
     * 
     *       Attribute a2 = new Attribute(); a2.setGermplasmId(new
     *       Integer(50533)); a2.setAdate(new Integer(20120406));
     *       a2.setAval("sample attribute 3"); a2.setLocationId(new
     *       Integer(1538)); a2.setReferenceId(null); a2.setTypeId(new
     *       Integer(103)); a2.setUserId(new Integer(1)); attributes.add(a2);
     * 
     *       int returned = manager.addGermplasmAttribute(attributes);
     *       Assert.assertTrue(returned == 3); }
     * @Test public void testUpdateGermplasmAttribute() throws Exception {
     *       Attribute a = manager.getAttributeById(new Integer(-1));
     *       a.setAval("updated sample attribute"); int returned =
     *       manager.updateGermplasmAttribute(a); Assert.assertTrue(returned ==
     *       1); }
     * @Test public void testUpdateGermplasmAttributeGivenList() throws
     *       Exception { List<Attribute> attributes = new
     *       ArrayList<Attribute>();
     * 
     *       Attribute a = manager.getAttributeById(new Integer(-2));
     *       a.setAval("sample attribute - updated"); attributes.add(a);
     * 
     *       Attribute a1 = manager.getAttributeById(new Integer(-3));
     *       a1.setAval("sample attribute 2 - updated"); attributes.add(a1);
     * 
     *       Attribute a2 = manager.getAttributeById(new Integer(-4));
     *       a2.setAval("sample attribute 3 - updated"); attributes.add(a2);
     * 
     *       int returned = manager.updateGermplasmAttribute(attributes);
     *       Assert.assertTrue(returned == 3); }
     * @Test public void testUpdateProgenitorWillCreateNewProgenitor() throws
     *       Exception { boolean success = manager.updateProgenitor(new
     *       Integer(306437), new Integer(1), new Integer(5));
     *       Assert.assertTrue(success); }
     * @Test public void testUpdateProgenitorWillUpdateProgenitorRecord() throws
     *       Exception { boolean success = manager.updateProgenitor(new
     *       Integer(306437), new Integer(2), new Integer(5));
     *       Assert.assertTrue(success); }
     * @Test public void testUpdateProgenitorWillUpdateGermplasm() throws
     *       Exception { //run this query first before testing //INSERT INTO
     *       germplsm
     *       (`gid`,`methn`,`gnpgs`,`gpid1`,`gpid2`,`germuid`,`lgid`,`glocn
     *       `,`gdate`,`gref`,`grplce`,`mgid`,`cid`,`sid`,`gchange`)
     *       //VALUES(-1,
     *       62,-1,420273,431701,1,-10123,9016,0,1,0,1,null,null,null)
     * 
     *       boolean success = manager.updateProgenitor(new Integer(-1), new
     *       Integer(2), new Integer(1)); Assert.assertTrue(success); }
     * @Test public void testUpdateGermplasm() throws Exception { //run these
     *       queries first before testing
     * 
     *       //INSERT INTO
     *       germplsm(`gid`,`methn`,`gnpgs`,`gpid1`,`gpid2`,`germuid
     *       `,`lgid`,`glocn
     *       `,`gdate`,`gref`,`grplce`,`mgid`,`cid`,`sid`,`gchange`)
     *       //VALUES(-1,
     *       62,-1,420273,431701,1,-10123,9016,0,1,0,1,null,null,null)
     * 
     *       //INSERT INTO
     *       germplsm(`gid`,`methn`,`gnpgs`,`gpid1`,`gpid2`,`germuid
     *       `,`lgid`,`glocn
     *       `,`gdate`,`gref`,`grplce`,`mgid`,`cid`,`sid`,`gchange`)
     *       ///VALUES(-2
     *       ,62,-1,420273,431701,1,-10124,9016,0,1,0,1,null,null,null)
     *       Germplasm g = manager.getGermplasmByGID(new Integer(-1));
     *       g.setGnpgs(new Integer(5));
     * 
     *       Germplasm g2 = manager.getGermplasmByGID(new Integer(-2));
     *       g2.setGnpgs(new Integer(5));
     * 
     *       List<Germplasm> germplasms = new ArrayList<Germplasm>();
     *       germplasms.add(g); germplasms.add(g2);
     * 
     *       int updated = manager.updateGermplasm(germplasms);
     *       Assert.assertTrue(updated == 2); }
     **/

    @Test
    public void testAddGermplasm() throws Exception {
        Germplasm g = new Germplasm();
        g.setGdate(new Integer(20120412));
        g.setGnpgs(new Integer(0));
        g.setGpid1(new Integer(0));
        g.setGpid2(new Integer(0));
        g.setGrplce(new Integer(0));
        g.setLocationId(new Integer(9000));
        g.setMethodId(new Integer(1));
        g.setMgid(new Integer(1));
        // g.setUserId(new Integer(527));
        g.setUserId(new Integer(1));
        g.setReferenceId(new Integer(1));

        Name n = new Name();
        n.setLocationId(new Integer(9000));
        n.setNdate(new Integer(20120412));
        n.setNval("Kevin 64");
        n.setReferenceId(new Integer(1));
        n.setTypeId(new Integer(1));
        n.setUserId(new Integer(1));

        int added = manager.addGermplasm(g, n);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
