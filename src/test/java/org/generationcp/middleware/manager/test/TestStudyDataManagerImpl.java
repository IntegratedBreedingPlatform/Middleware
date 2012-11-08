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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.DatasetCondition;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.StudyInfo;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStudyDataManagerImpl{

    private static ManagerFactory factory;
    private static StudyDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getStudyDataManager();
    }

    @Test
    public void testGetGIDSByPhenotypicData() throws Exception {
        Integer traitId = Integer.valueOf(1003);
        Integer scaleId = Integer.valueOf(9);
        Integer methodId = Integer.valueOf(30);
        NumericRange range = new NumericRange(new Double(2000), new Double(3000));
        TraitCombinationFilter combination = new TraitCombinationFilter(traitId, scaleId, methodId, range);
        List<TraitCombinationFilter> filters = new ArrayList<TraitCombinationFilter>();
        filters.add(combination);

        // TraitCombinationFilter combination1 = new TraitCombinationFilter(new
        // Integer(1007), new Integer(266), new Integer(260), new Double(5));
        // filters.add(combination1);
        // TraitCombinationFilter combination2 = new TraitCombinationFilter(new
        // Integer(1007), new Integer(266), new Integer(260), "5");
        // filters.add(combination2);

        List<Integer> results = manager.getGIDSByPhenotypicData(filters, 0, 10, Database.CENTRAL);
        System.out.println("testGetGIDSByPhenotypicData(traitId=" + scaleId + ", scaleId=" + scaleId + ", methodId=" + methodId
                + ", range(" + range.getStart() + ", " + range.getEnd() + ")) RESULTS:");
        for (Integer gid : results)
            System.out.println("  " + gid);
    }

    @Test
    public void testGetStudyByNameUsingLike() throws Exception {
        String name = "IRTN%";
        List<Study> studyList = manager.getStudyByName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        System.out.println("testGetStudyByNameUsingLike(" + name + ") RESULTS: ");
        for (Study study : studyList) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testGetStudyByNameUsingEqual() throws Exception {
        String name = "PEATSOIL";
        List<Study> studyList = manager.getStudyByName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testGetStudyByNameUsingEqual(" + name + ") RESULTS: ");
        for (Study study : studyList) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testCountStudyByName() throws Exception {
        String name = "IRTN%";
        long start = System.currentTimeMillis();
        long count = manager.countStudyByName("IRTN%", Operation.LIKE, Database.CENTRAL);
        long end = System.currentTimeMillis();
        System.out.println("testCountStudyByName(" + name + "): " + count);
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetStudyBySDateUsingEqual() throws Exception {
        Integer sdate = 20050119;
        List<Study> studyList = manager.getStudyBySDate(sdate, 0, 2, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testGetStudyBySDateUsingEqual(" + sdate + ") RESULTS: ");
        for (Study study : studyList) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testCountStudyBySDate() throws Exception {
        Integer sdate = 20050119;
        long start = System.currentTimeMillis();
        long count = manager.countStudyBySDate(sdate, Operation.LIKE, Database.CENTRAL);
        long end = System.currentTimeMillis();
        System.out.println("testCountStudyBySDate(" + sdate + "): " + count);
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetStudyByEDateUsingEqual() throws Exception {
        Integer edate = 2004;
        List<Study> studyList = manager.getStudyByEDate(edate, 0, 2, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testGetStudyByEDateUsingEqual(" + edate + ") RESULTS: ");
        for (Study study : studyList) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testCountStudyByEDate() throws Exception {
        Integer edate = 2004;
        long start = System.currentTimeMillis();
        long count = manager.countStudyByEDate(edate, Operation.LIKE, Database.CENTRAL);
        long end = System.currentTimeMillis();
        System.out.println("testCountStudyByEDate(" + edate + "): " + count);
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetStudyByID() throws Exception {
        Integer id = Integer.valueOf(714);
        Study study = manager.getStudyByID(id);
        System.out.println("testGetStudyByID(" + id + "): " + study);
    }

    @Test
    public void testGetAllTopLevelStudies() throws Exception {
        List<Study> topLevelStudies = manager.getAllTopLevelStudies(0, 10, Database.LOCAL);
        System.out.println("testGetAllTopLevelStudies() Number of top-level studies: " + topLevelStudies.size());
        for (Study study : topLevelStudies) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testCountAllTopLevelStudies() throws Exception {
        long count = manager.countAllTopLevelStudies(Database.CENTRAL);
        System.out.println("testCountAllTopLevelStudies(): " + count);
    }

    @Test
    public void testCountAllStudyByParentFolderID() throws Exception {
        Integer parentFolderId = Integer.valueOf(640);
        long count = manager.countAllStudyByParentFolderID(parentFolderId, Database.CENTRAL);
        System.out.println("testCountAllStudyByParentFolderID(" + parentFolderId + ") Number of Studies belong to this parent folder: "
                + count);
    }

    @Test
    public void testGetStudiesByParentFolderID() throws Exception {
        Integer parentFolderId = Integer.valueOf(640);
        List<Study> studies = manager.getStudiesByParentFolderID(parentFolderId, 0, 100);
        System.out.println("testGetStudiesByParentFolderID(" + parentFolderId + ") STUDIES BY PARENT FOLDER: " + studies.size());
        for (Study study : studies) {
            System.out.println("  " + study);
        }
    }

    @Test
    public void testGetFactorsByStudyID() throws Exception {
        Integer studyId = Integer.valueOf(430);
        List<Factor> factors = manager.getFactorsByStudyID(studyId);
        System.out.println("testGetFactorsByStudyID(" + studyId + ") RESULTS: " + factors.size());
        for (Factor factor : factors) {
            System.out.println("  " + factor);
        }
    }

    @Test
    public void testGetVariatesByStudyID() throws Exception {
        Integer studyId = Integer.valueOf(430);
        List<Variate> variates = manager.getVariatesByStudyID(studyId);
        System.out.println("testGetVariatesByStudyID(" + studyId + ") RESULTS: " + variates.size());
        for (Variate variate : variates) {
            System.out.println("  " + variate);
        }
    }

    @Test
    public void testGetEffectsByStudyID() throws Exception {
        Integer studyId = Integer.valueOf(430);
        List<StudyEffect> studyEffects = manager.getEffectsByStudyID(studyId);
        System.out.println("testGetEffectsByStudyID(" + studyId + ") RESULTS: " + studyEffects.size());
        for (StudyEffect studyEffect : studyEffects) {
            System.out.println("  " + studyEffect);
        }
    }

    @Test
    public void testGetRepresentationByEffectID() throws Exception {
        Integer effectId = Integer.valueOf(430);
        List<Representation> representations = manager.getRepresentationByEffectID(effectId);
        System.out.println("testGetRepresentationByEffectID(" + effectId + ") RESULTS: " + representations.size());
        for (Representation representation : representations) {
            System.out.println("  " + representation);
        }
    }

    @Test
    public void testGetRepresentationByStudyID() throws Exception {
        Integer studyId = Integer.valueOf(1);
        List<Representation> representations = manager.getRepresentationByStudyID(studyId);
        System.out.println("testGetRepresentationByStudyID(" + studyId + ") RESULTS: " + representations.size());
        for (Representation representation : representations) {
            System.out.println("  " + representation);
        }
    }

    @Test
    public void testGetFactorsByRepresentationId() throws Exception {
        Integer representationId = Integer.valueOf(1176);
        List<Factor> factors = manager.getFactorsByRepresentationId(representationId);
        System.out.println("testGetFactorsByRepresentationId(" + representationId + ") RESULTS: " + factors.size());
        for (Factor factor : factors) {
            System.out.println("  " + factor);
        }
    }

    @Test
    public void testCountOunitIDsByRepresentationId() throws Exception {
        Integer representationId = Integer.valueOf(1176);
        long ounitIdCount = manager.countOunitIDsByRepresentationId(representationId);
        System.out.println("testCountOunitIDsByRepresentationId(" + representationId + ") COUNT OF OUNIT IDS BY REPRESENTATION: "
                + ounitIdCount);
    }

    @Test
    public void testGetOunitIDsByRepresentationId() throws Exception {
        Integer representationId = Integer.valueOf(1176);
        List<Integer> ounitIDs = manager.getOunitIDsByRepresentationId(representationId, 0, 100);
        System.out.println("testGetOunitIDsByRepresentationId(" + representationId + ") RESULTS: " + ounitIDs.size() + "\n  " + ounitIDs);
    }

    @Test
    public void testGetVariatesByRepresentationId() throws Exception {
        Integer representationId = Integer.valueOf(1176);
        List<Variate> variates = manager.getVariatesByRepresentationId(representationId);
        System.out.println("testGetVariatesByRepresentationId(" + representationId + ") VARIATES BY REPRESENTATION: " + variates.size());
        for (Variate variate : variates) {
            System.out.println("  " + variate);
        }
    }

    @Test
    public void testGetNumericDataValuesByOunitIdList() throws Exception {
        List<Integer> ounitIdList = new ArrayList<Integer>();
        ounitIdList.add(447201);
        ounitIdList.add(447202);
        ounitIdList.add(447203);
        ounitIdList.add(447204);
        ounitIdList.add(447205);
        List<NumericDataElement> dataElements = manager.getNumericDataValuesByOunitIdList(ounitIdList);
        System.out.println("testGetNumericDataValuesByOunitIdList(" + ounitIdList + ") NUMERIC DATA VALUES BY OUNITIDLIST: "
                + dataElements.size());
        for (NumericDataElement data : dataElements) {
            System.out.println("  " + data);
        }
    }

    @Test
    public void testGetCharacterDataValuesByOunitIdList() throws Exception {
        List<Integer> ounitIdList = new ArrayList<Integer>();
        ounitIdList.add(447201);
        ounitIdList.add(447202);
        ounitIdList.add(447203);
        ounitIdList.add(447204);
        ounitIdList.add(447205);
        List<CharacterDataElement> dataElements = manager.getCharacterDataValuesByOunitIdList(ounitIdList);
        System.out.println("testGetCharacterDataValuesByOunitIdList(" + ounitIdList + ") CHARACTER DATA VALUES BY OUNITIDLIST: "
                + dataElements.size());
        for (CharacterDataElement data : dataElements) {
            System.out.println("  " + data);
        }
    }

    @Test
    public void testGetNumericLevelValuesByOunitIdList() throws Exception {
        List<Integer> ounitIdList = new ArrayList<Integer>();
        ounitIdList.add(447201);
        ounitIdList.add(447202);
        ounitIdList.add(447203);
        ounitIdList.add(447204);
        ounitIdList.add(447205);
        List<NumericLevelElement> levelElements = manager.getNumericLevelValuesByOunitIdList(ounitIdList);
        System.out.println("testGetNumericLevelValuesByOunitIdList(" + ounitIdList + ") NUMERIC LEVEL VALUES BY OUNITIDLIST: "
                + levelElements.size());
        for (NumericLevelElement level : levelElements) {
            System.out.println("  " + level);
        }
    }

    @Test
    public void testGetCharacterLevelValuesByOunitIdList() throws Exception {
        List<Integer> ounitIdList = new ArrayList<Integer>();
        ounitIdList.add(447201);
        ounitIdList.add(447202);
        ounitIdList.add(447203);
        ounitIdList.add(447204);
        ounitIdList.add(447205);
        List<CharacterLevelElement> levelElements = manager.getCharacterLevelValuesByOunitIdList(ounitIdList);
        System.out.println("CHARACTER LEVEL VALUES BY OUNITIDLIST: " + levelElements.size());
        System.out.println("testGetCharacterLevelValuesByOunitIdList(" + ounitIdList + ") CHARACTER LEVEL VALUES BY OUNITIDLIST: "
                + levelElements.size());
        for (CharacterLevelElement level : levelElements) {
            System.out.println("  " + level);
        }
    }

    @Test
    public void testGetConditionsByRepresentationId() throws Exception {
        Integer representationId = Integer.valueOf(2);
        List<DatasetCondition> results = manager.getConditionsByRepresentationId(representationId);
        System.out.println("testGetConditionsByRepresentationId(" + representationId + ") RESULTS: ");
        for (DatasetCondition result : results) {
            System.out.println("  " + result);
        }
    }

    @Test
    public void testGetMainLabelOfFactorByFactorId() throws Exception {
        Integer factorId = Integer.valueOf(1031);
        System.out.println("testGetMainLabelOfFactorByFactorId(" + factorId + ") RESULTS: "
                + manager.getMainLabelOfFactorByFactorId(factorId));
    }

    @Test
    public void testCountStudyInformationByGID() throws Exception {
        Long gid = Long.valueOf(50533);
        System.out.println("testCountStudyInformationByGID(" + gid + "): " + manager.countStudyInformationByGID(gid));
    }

    @Test
    public void testGetStudyInformationByGID() throws Exception {
        Long gid = Long.valueOf(50533);
        List<StudyInfo> results = manager.getStudyInformationByGID(gid);
        System.out.println("testGetStudyInformationByGID(" + gid + ") RESULTS:");
        for (StudyInfo info : results) {
            System.out.println("  " + info);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
