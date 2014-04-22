/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GdmsType;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartDataRow;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.MappingABHRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSNPRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSSRDArTRow;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDataRow;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.SNPDataRow;
import org.generationcp.middleware.pojos.gdms.SSRDataRow;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGenotypicDataManagerImplUploadFunctions extends TestOutputFormatter{

    private static ManagerFactory       factory;
    private static GenotypicDataManager manager;

    private static final String DATASET             = "Dataset";
    private static final String DATASET_USERS       = "DatasetUsers";
    private static final String ACC_METADATA_SET    = "AccMetadataSet";
    private static final String MARKER_METADATA_SET = "MarkerMetadataSet";
    private static final String ALLELE_VALUES       = "AlleleValues";
    private static final String CHAR_VALUES         = "CharValues";
    private static final String MAPPING_POP         = "MappingPop";
    private static final String MAPPING_POP_VALUES  = "MappingPopValues";
    private static final String DART_VALUES         = "DartValues";
    private static final String MARKER              = "Marker";

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties",
                "localgroundnut");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties",
                "centralgroundnut");
        factory = new ManagerFactory(local, central);
        manager = factory.getGenotypicDataManager();
    }

    private Dataset createDataset() throws Exception {
        Integer datasetId = null; // Crop tested: Groundnut
        String datasetName = " QTL_ ICGS 44 X ICGS 78  " + (int) (Math.random() * 1000);
        String datasetDesc = "ICGS 44 X ICGS 78";
        String datasetType = "QTL";
        String genus = "Groundnut";
        String species = "";
        Date uploadTemplateDate = new Date(System.currentTimeMillis());
        String remarks = "";
        String dataType = "int";
        String missingData = null;
        String method = null;
        String score = null;
        String institute = null;
        String principalInvestigator = null;
        String email = null;
        String purposeOfStudy = null;

        return new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate,
                remarks, dataType, missingData, method, score, institute, principalInvestigator, email, purposeOfStudy);
    }
    
    private List<Object> createMarkerMarkeRecords() {

        Integer markerId = null; // Will be set/overridden by the function
        String markerType = null; // Will be set/overridden by the function
        String markerName = "SeqTEST " + (int) (Math.random() * 1000);
        String species = "Groundnut";
        String dbAccessionId = null;
        String reference = null;
        String genotype = null;
        String ploidy = null;
        String primerId = null;
        String remarks = null;
        String assayType = null;
        String motif = null;
        String forwardPrimer = null;
        String reversePrimer = null;
        String productSize = null;
        Float annealingTemp = Float.valueOf(0);
        String amplification = null;

        String alias = "testalias";
        Integer noOfRepeats = 0;
        String motifType = "";
        String sequence = "";
        Integer sequenceLength = 0;
        Integer minAllele = 0;
        Integer maxAllele = 0;
        Integer ssrNr = 0;
        Float forwardPrimerTemp = 0f;
        Float reversePrimerTemp = 0f;
        Float elongationTemp = 0f;
        Integer fragmentSizeExpected = 0;
        Integer fragmentSizeObserved = 0;
        Integer expectedProductSize = 0;
        Integer positionOnReferenceSequence = 0;
        String restrictionEnzymeForAssay = null;
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";

        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype,
                ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp,
                amplification);
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
        MarkerDetails markerDetails = new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength,
                minAllele, maxAllele, ssrNr, forwardPrimerTemp, reversePrimerTemp, elongationTemp,
                fragmentSizeExpected, fragmentSizeObserved, expectedProductSize, positionOnReferenceSequence,
                restrictionEnzymeForAssay);
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

        List<Object> markerRecords = new ArrayList<Object>();
        markerRecords.add(marker);
        markerRecords.add(markerAlias);
        markerRecords.add(markerDetails);
        markerRecords.add(markerUserInfo);
        return markerRecords;

    }

    // Create test data for setSSR, setSNP, setDArT, setMappingData
    private java.util.Map<String, Object> createMappingRecords() throws Exception {
        java.util.Map<String, Object> records = new HashMap<String, Object>();

        // DatasetUser Fields
        Integer datasetId = null; // Will be set/overridden by the function
        Integer userId = 123;

        // AccMetadataSet Additional Fields
        Integer gId = 1;
        Integer nameId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;

        // AlleleValues Additional Fields
        Integer anId = null; // Will be set/overridden by the function
        String alleleBinValue = "238:238";
        String alleleRawValue = "0.0:0.0";
        Integer peakHeight = 10;

        // DartValues Additional Fields
        Integer adId = null; // Will be set/overridden by the function
        Integer cloneId = 1;
        Float qValue = 0f;
        Float reproducibility = 0f;
        Float callRate = 0f;
        Float picValue = 0f;
        Float discordance = 0f;

        // charValues Additional Fields
        Integer acId = null;
        String charValue = "CV";

        // MappingPop Additional Fields
        String mappingType = "abh";
        Integer parentAGId = 1035;
        Integer parentBGId = 1036;
        Integer populationSize = 999;
        String populationType = "";
        String mapDataDescription = "Flood resistant";
        String scoringScheme = "";
        Integer mapId = 1;

        // MappingPopValues Additional Fields
        Integer mpId = null; // Will be set/overridden by the function
        String mapCharValue = "-";

        Dataset dataset = createDataset();
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, gId, nameId);
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue,
                peakHeight);
        CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue);
        DartValues dartValues = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate,
                picValue, discordance);
        MappingPop mappingPop = new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize,
                populationType, mapDataDescription, scoringScheme, mapId);
        MappingPopValues mappingPopValues = new MappingPopValues(mpId, mapCharValue, datasetId, gId, markerId);
        Marker marker = (Marker) createMarkerMarkeRecords().get(0);

        records.put(DATASET, dataset);
        records.put(ACC_METADATA_SET, accMetadataSet);
        records.put(MARKER_METADATA_SET, markerMetadataSet);
        records.put(DATASET_USERS, datasetUser);
        records.put(ALLELE_VALUES, alleleValues);
        records.put(CHAR_VALUES, charValues);
        records.put(DART_VALUES, dartValues);
        records.put(MAPPING_POP, mappingPop);
        records.put(MAPPING_POP_VALUES, mappingPopValues);
        records.put(MARKER, marker);

        return records;
    }

    @Test
    public void testSetQTL() throws Exception {
        Integer datasetId = null; // Will be set/overridden by the function
        Integer userId = 123;

        Integer qtlId = null; // Will be set/overridden by the function
        Integer mapId = 1;
        Float minPosition = 0f;
        Float maxPosition = 8f;
        Integer traitId = 1001; // "DE";
        String experiment = "";
        Integer effect = 0;
        Float scoreValue = 2.5f;
        Float rSquare = 10f;
        String linkageGroup = "LG06";
        String interactions = "";
        String leftFlankingMarker = "Ah4-101";
        String rightFlankingMarker = "GM2536";
        Float position = 34.71f;
        Float clen = 0f;
        String seAdditive = null;
        String hvParent = null;
        String hvAllele = null;
        String lvParent = null;
        String lvAllele = null;

        String qtlName = "TestQTL";

        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);

        Dataset dataset = createDataset();
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset

        QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect,
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker, rightFlankingMarker, position,
                clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);

        Qtl qtl = new Qtl(qtlId, qtlName, datasetId);

        List<QtlDataRow> dataRows = new ArrayList<QtlDataRow>();
        dataRows.add(new QtlDataRow(qtl, qtlDetails));

        Boolean addStatus = manager.setQTL(dataset, datasetUser, dataRows);

        assertTrue(addStatus);

        Debug.println("testSetQTL() Added: ");
        Debug.println(3, datasetUser.toString());
        Debug.println(3, dataset.toString());
        Debug.printObjects(INDENT, dataRows);
    }

    @Test
    public void testSetDart() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);

        List<DartDataRow> dataRows = new ArrayList<DartDataRow>();
        dataRows.add(new DartDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues, dartValues));

        Boolean addStatus = manager.setDart(dataset, datasetUser, dataRows);

        assertTrue(addStatus);
        Debug.println("testSetDArT() Added: ");
        Debug.println(3, dataset.toString());
        Debug.println(3, datasetUser.toString());
        Debug.printObjects(INDENT, dataRows);
    }
    
    @Test
    public void testSetDartDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);

        List<DartDataRow> dataRows = new ArrayList<DartDataRow>();
        dataRows.add(new DartDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues, dartValues));

        try {
            manager.setDart(dataset, datasetUser, dataRows);
            manager.setDart(dataset, datasetUser, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }

    @Test
    public void testSetSSR() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 1000)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);

        List<SSRDataRow> dataRows = new ArrayList<SSRDataRow>();
        dataRows.add(new SSRDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues));

        Boolean addStatus = manager.setSSR(dataset, datasetUser, dataRows);

        assertTrue(addStatus);
        Debug.println("testSetSSR() Added: ");
        Debug.println(3, dataset.toString());
        Debug.println(3, datasetUser.toString());
        Debug.printObjects(INDENT, dataRows);

    }

    @Test
    public void testSetSSRtDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);

        List<SSRDataRow> dataRows = new ArrayList<SSRDataRow>();
        dataRows.add(new SSRDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues));

        try {
            manager.setSSR(dataset, datasetUser, dataRows);
            manager.setSSR(dataset, datasetUser, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }


    @Test
    public void testSetSNP() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);

        List<SNPDataRow> dataRows = new ArrayList<SNPDataRow>();
        for (int i=0; i<210*260; i++){
            dataRows.add(new SNPDataRow(marker, accMetadataSet, markerMetadataSet, charValues));
        }

        Boolean addStatus = manager.setSNP(dataset, datasetUser, dataRows);
        assertTrue(addStatus);
        Debug.println("testSetSNP() Added: ");
        Debug.println(INDENT, dataset.toString());
        Debug.println(INDENT, datasetUser.toString());
        Debug.printObjects(INDENT, dataRows);
    }

    @Test
    public void testSetSNPDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);

        List<SNPDataRow> dataRows = new ArrayList<SNPDataRow>();
        dataRows.add(new SNPDataRow(marker, accMetadataSet, markerMetadataSet, charValues));

        try {
            manager.setSNP(dataset, datasetUser, dataRows);
            manager.setSNP(dataset, datasetUser, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }
    

    @Test
    public void testSetSNP2() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<CharValues> charValueList = new ArrayList<CharValues>();
        List<SNPDataRow> dataRows = new ArrayList<SNPDataRow>();
        for (int i=0; i<210*260; i++){
            marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
            markers.add(marker);
            markerMetadataSet.setMarkerId(-1);
            markerMetadataSets.add(markerMetadataSet);
            accMetadataSets.add(accMetadataSet);
            dataRows.add(new SNPDataRow(charValues));
            charValueList.add(charValues);
        }
        
        Boolean addStatus = manager.setSNP(dataset, datasetUser, markers, markerMetadataSets, accMetadataSets, charValueList);
        assertTrue(addStatus);
    }

    @Test
    public void testSetMappingABH() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);

        // Dataset Fields
        String datasetName = "Map_Pop GCP-832 Test";
        String datasetDesc = "Map_Pop GCP-832 Test Description";
        String datasetType = "mapping";
        String species = "Groundnut";
        String dataType = "map";
        String genus = "Groundnut";

        dataset.setDatasetName(datasetName);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset

        dataset.setDatasetDesc(datasetDesc);
        dataset.setDatasetType(datasetType);
        dataset.setDatasetType(datasetType);
        dataset.setSpecies(species);
        dataset.setDataType(dataType);
        dataset.setGenus(genus);

        List<MappingABHRow> dataRows = new ArrayList<MappingABHRow>();
        dataRows.add(new MappingABHRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues));

        Boolean addStatus = manager.setMappingABH(dataset, datasetUser, mappingPop, dataRows);

        assertTrue(addStatus);
        Debug.println("testSetMappingABH() Added: ");
        Debug.println(3, dataset.toString());
        Debug.println(3, datasetUser.toString());
        Debug.println(3, mappingPop.toString());
        Debug.printObjects(INDENT, dataRows);
    }

    @Test
    public void testSetMappingABHDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);

        // Dataset Fields
        String datasetName = "Map_Pop GCP-832 Test";
        String datasetDesc = "Map_Pop GCP-832 Test Description";
        String datasetType = "mapping";
        String species = "Groundnut";
        String dataType = "map";
        String genus = "Groundnut";

        dataset.setDatasetName(datasetName);
        dataset.setDatasetDesc(datasetDesc);
        dataset.setDatasetType(datasetType);
        dataset.setDatasetType(datasetType);
        dataset.setSpecies(species);
        dataset.setDataType(dataType);
        dataset.setGenus(genus);

        List<MappingABHRow> dataRows = new ArrayList<MappingABHRow>();
        dataRows.add(new MappingABHRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues));

        try {
            manager.setMappingABH(dataset, datasetUser, mappingPop, dataRows);
            manager.setMappingABH(dataset, datasetUser, mappingPop, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }

    @Test
    public void testSetMappingAllelicSNP() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);

        List<MappingAllelicSNPRow> dataRows = new ArrayList<MappingAllelicSNPRow>();
        dataRows.add(new MappingAllelicSNPRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, charValues));

        Boolean addStatus = manager.setMappingAllelicSNP(dataset, datasetUser, mappingPop, dataRows);

        assertTrue(addStatus);
        Debug.println("testSetMappingAllelicSNP() Added: ");
        Debug.println(3, dataset.toString());
        Debug.println(3, datasetUser.toString());
        Debug.println(3, mappingPop.toString());
        Debug.printObjects(INDENT, dataRows);
    }

    @Test
    public void testSetMappingAllelicSNPDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);

        List<MappingAllelicSNPRow> dataRows = new ArrayList<MappingAllelicSNPRow>();
        dataRows.add(new MappingAllelicSNPRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, charValues));

        try {
            manager.setMappingAllelicSNP(dataset, datasetUser, mappingPop, dataRows);
            manager.setMappingAllelicSNP(dataset, datasetUser, mappingPop, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }

    @Test
    public void testSetMappingAllelicSSRDArT() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        marker.setMarkerName(marker.getMarkerName() + (int) (Math.random() * 100)); // Remove line to test duplicate marker entries
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);

        List<MappingAllelicSSRDArTRow> dataRows = new ArrayList<MappingAllelicSSRDArTRow>();
        dataRows.add(new MappingAllelicSSRDArTRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues,
                alleleValues, dartValues));

        Boolean addStatus = manager.setMappingAllelicSSRDArT(dataset, datasetUser, mappingPop, dataRows);

        assertTrue(addStatus);
        Debug.println("testSetMappingAllelicSSRDArT() Added: ");
        Debug.println(3, dataset.toString());
        Debug.println(3, datasetUser.toString());
        Debug.println(3, mappingPop.toString());
        Debug.printObjects(INDENT, dataRows);
    }

    @Test
    public void testSetMappingAllelicSSRDArTDuplicateDataset() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        Marker marker = (Marker) mappingRecords.get(MARKER);
        marker.setMarkerName(marker.getMarkerName() + (int) (Math.random() * 100));  // Remove line to test duplicate marker entries
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);

        List<MappingAllelicSSRDArTRow> dataRows = new ArrayList<MappingAllelicSSRDArTRow>();
        dataRows.add(new MappingAllelicSSRDArTRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues,
                alleleValues, dartValues));

        try {
            manager.setMappingAllelicSSRDArT(dataset, datasetUser, mappingPop, dataRows);
            manager.setMappingAllelicSSRDArT(dataset, datasetUser, mappingPop, dataRows);
        } catch (MiddlewareQueryException e) {
            assertTrue(e.getMessage().contains("Dataset already exists"));
        }
    }
    
    // ============================================ UPDATE FUNCTIONS =====================================================

    private Dataset getTestDatasetByType(GdmsType type, GdmsType mappingType) throws MiddlewareQueryException{
    	List<Dataset> datasets = new ArrayList<Dataset>();
    	
    	if (type == GdmsType.TYPE_MAPPING){
			datasets = manager.getDatasetsByMappingTypeFromLocal(mappingType);
    	} else {
    		datasets = manager.getDatasetsByType(type);
		}
    	if (datasets.size() > 0){
			return datasets.get(0);
    	}
    	Debug.println("No dataset of type " + type + " found in the local database for testing");
    	return null;
    }

    private void updateDataset(Dataset dataset, String updateId){
    	Debug.printObject(0, "DATASET BEFORE: " + dataset);
    	String datasetDescription = dataset.getDatasetDesc() + updateId;
    	if (datasetDescription.length() > 255){
    		datasetDescription = datasetDescription.substring(0, 255);
    	}
    	dataset.setDatasetDesc(datasetDescription);
    }
    
    private MappingPop updateMappingPop(MappingPop mappingPop, String updateId){
    	Debug.printObject(0, "MAPPINGPOP BEFORE: " + mappingPop);
    	String mapDataDescription = mappingPop.getMapDataDescription() == null ? "" + updateId : mappingPop.getMapDataDescription() + updateId;
    	if (mapDataDescription.length() > 150){
    		mapDataDescription = mapDataDescription.substring(0, 150);
    	}
		mappingPop.setMapDataDescription(mapDataDescription);
		return mappingPop;
    }
    
    private MappingPopValues updateMappingPopValues(MappingPopValues mappingPopValues, String updateId){
    	if (mappingPopValues == null){
    		return null;
    	}
		String mapCharValue = mappingPopValues.getMapCharValue() + updateId;
		if (mapCharValue.length() > 20){
			mapCharValue = mapCharValue.substring(0, 20);
		}
		mappingPopValues.setMapCharValue(mapCharValue);
		return mappingPopValues;
    }
    
    private CharValues updateCharValues(CharValues charValues, String updateId){
    	if (charValues == null){
    		return null;
    	}
		String charValue = charValues.getCharValue() + updateId;
		if (charValue.length() > 4){
			charValue = charValue.substring(0, 4);
		}
		charValues.setCharValue(charValue);
		return charValues;
    }

    private AlleleValues updateAlleleValues(AlleleValues alleleValues, int updateId){
    	if (alleleValues == null){
    		return null;
    	}
		String alleleBinValue = alleleValues.getAlleleBinValue() + updateId;
		if (alleleBinValue.length() > 20){
			alleleBinValue = alleleBinValue.substring(0, 20);
		}
		alleleValues.setAlleleBinValue(alleleBinValue);
		return alleleValues;
    }

    private DartValues updateDartValues(DartValues dartValues, int updateId){
    	if (dartValues == null){
    		return null;
    	}
		Float qValue = dartValues.getqValue() + updateId;
		dartValues.setqValue(qValue);
		return dartValues;
    }

    @Test
    public void testUpdateDart() throws Exception {
    	
    	GdmsType gdmsType = GdmsType.TYPE_DART;
    	Integer updateId = (int)(Math.random() * 1000); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, null);
    	if (dataset == null){
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	List<DartDataRow> rows = manager.getDartDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	List<DartDataRow> updatedRows = new ArrayList<DartDataRow>();
    	for (DartDataRow row : rows){

    		// Update marker
    		Marker marker = row.getMarker();
			marker.setRemarks(marker.getRemarks() + updateId);

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		// Update dartValues
    		DartValues dartValues = updateDartValues(row.getDartValues(), updateId);
    		
    		updatedRows.add(new DartDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues, dartValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
        DartDataRow newRow = new DartDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues, dartValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateDart(dataset, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	List<DartDataRow> rowsAfter = manager.getDartDataRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    	
    }
    
    @Test
    public void testUpdateSNP() throws Exception {
    	GdmsType gdmsType = GdmsType.TYPE_SNP;
    	Integer updateId = (int)(Math.random() * 1000); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, null);
    	if (dataset == null){
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	List<SNPDataRow> rows = manager.getSNPDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	boolean isMarkerUpdated = false;
    	List<SNPDataRow> updatedRows = new ArrayList<SNPDataRow>();
    	for (SNPDataRow row : rows){

    		// Update marker
    		Marker marker = row.getMarker();
    		if (!isMarkerUpdated){
    			marker.setRemarks(marker.getRemarks() + updateId);
    			isMarkerUpdated = true;
    		}

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		// Update dartValues
    		CharValues charValues = updateCharValues(row.getCharValues(), updateId.toString());
    		
    		updatedRows.add(new SNPDataRow(marker, accMetadataSet, markerMetadataSet, charValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
        SNPDataRow newRow = new SNPDataRow(marker, accMetadataSet, markerMetadataSet, charValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateSNP(dataset, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	List<SNPDataRow> rowsAfter = manager.getSNPDataRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }
    
    @Test
    public void testUpdateSSR() throws Exception {
    	
    	GdmsType gdmsType = GdmsType.TYPE_SSR;
    	Integer updateId = (int)(Math.random() * 1000); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, null);
    	if (dataset == null){
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	List<SSRDataRow> rows = manager.getSSRDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	List<SSRDataRow> updatedRows = new ArrayList<SSRDataRow>();
    	for (SSRDataRow row : rows){

    		// Update marker
    		Marker marker = row.getMarker();
			marker.setRemarks(marker.getRemarks() + updateId);

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		updatedRows.add(new SSRDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        SSRDataRow newRow = new SSRDataRow(marker, accMetadataSet, markerMetadataSet, alleleValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateSSR(dataset, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	List<SSRDataRow> rowsAfter = manager.getSSRDataRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }
    
    @Test
    public void testUpdateMappingABH() throws Exception {
    	GdmsType gdmsType = GdmsType.TYPE_MAPPING;
    	String updateId = " UPDATED " + String.valueOf((int)(Math.random() * 1000)); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, null);
    	if (dataset == null){
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId);
    	
    	//Update mappingPop
    	MappingPop mappingPop = manager.getMappingPopByDatasetId(datasetId);
    	updateMappingPop(mappingPop, updateId);
    	
    	List<MappingABHRow> rows = manager.getMappingABHRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	List<MappingABHRow> updatedRows = new ArrayList<MappingABHRow>();
    	for (MappingABHRow row : rows){

    		// Update mappingPopValues
    		MappingPopValues mappingPopValues = updateMappingPopValues(row.getMappingPopValues(), updateId);
    		
    		// Update marker
    		Marker marker = row.getMarker();
			marker.setRemarks(marker.getRemarks() + updateId);

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		updatedRows.add(new MappingABHRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        MappingABHRow newRow = new MappingABHRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingABH(dataset, mappingPop, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	MappingPop mappingPopAfter = manager.getMappingPopByDatasetId(datasetId);
    	Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
    	assertEquals(mappingPop, mappingPopAfter); // MappingPop updated
    	
    	List<MappingABHRow> rowsAfter = manager.getMappingABHRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }
    
    @Test
    public void testUpdateMappingAllelicSNP() throws Exception {
    	GdmsType gdmsType = GdmsType.TYPE_MAPPING;
    	String updateId = " UPDATED " + String.valueOf((int)(Math.random() * 1000)); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, GdmsType.TYPE_SNP);
    	if (dataset == null){
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId);
    	
    	//Update mappingPop
    	MappingPop mappingPop = manager.getMappingPopByDatasetId(datasetId);
    	updateMappingPop(mappingPop, updateId);
    	
    	List<MappingAllelicSNPRow> rows = manager.getMappingAllelicSNPRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	List<MappingAllelicSNPRow> updatedRows = new ArrayList<MappingAllelicSNPRow>();
    	for (MappingAllelicSNPRow row : rows){

    		// Update mappingPopValues
    		MappingPopValues mappingPopValues = updateMappingPopValues(row.getMappingPopValues(), updateId);
    		
    		// Update marker
    		Marker marker = row.getMarker();
			marker.setRemarks(marker.getRemarks() + updateId);

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		// Update charValues
    		CharValues charValues = updateCharValues(row.getCharValues(), updateId);
    		
    		updatedRows.add(new MappingAllelicSNPRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, charValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
        MappingAllelicSNPRow newRow = new MappingAllelicSNPRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, charValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingAllelicSNP(dataset, mappingPop, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	MappingPop mappingPopAfter = manager.getMappingPopByDatasetId(datasetId);
    	Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
    	assertEquals(mappingPop, mappingPopAfter); // MappingPop updated
    	
    	List<MappingABHRow> rowsAfter = manager.getMappingABHRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }

    @Test
    public void testUpdateMappingAllelicSSRDart() throws Exception {
    	GdmsType gdmsType = GdmsType.TYPE_MAPPING;
    	Integer updateId = (int)(Math.random() * 1000); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, GdmsType.TYPE_SSR);
    	if (dataset == null){
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	//Update mappingPop
    	MappingPop mappingPop = manager.getMappingPopByDatasetId(datasetId);
    	updateMappingPop(mappingPop, updateId.toString());
    	
    	List<MappingAllelicSSRDArTRow> rows = manager.getMappingAllelicSSRDArTRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);
    	
    	// Update existing rows
    	List<MappingAllelicSSRDArTRow> updatedRows = new ArrayList<MappingAllelicSSRDArTRow>();
    	for (MappingAllelicSSRDArTRow row : rows){

    		// Update mappingPopValues
    		MappingPopValues mappingPopValues = updateMappingPopValues(row.getMappingPopValues(), updateId.toString());
    		
    		// Update marker
    		Marker marker = row.getMarker();
			marker.setRemarks(marker.getRemarks() + updateId);

    		// No change in accMetadataSet and markerMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		MarkerMetadataSet markerMetadataSet = row.getMarkerMetadataSet();
    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		// Update dartValues
    		DartValues dartValues = updateDartValues(row.getDartValues(), updateId);
    		
    		updatedRows.add(new MappingAllelicSSRDArTRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, alleleValues, dartValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
    	Marker marker = (Marker) mappingRecords.get(MARKER);
        if (updatedRows.size() > 0){
        	marker = (Marker) updatedRows.get(0).getMarker();
        } 
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
        MappingAllelicSSRDArTRow newRow = new MappingAllelicSSRDArTRow(marker, accMetadataSet, markerMetadataSet, mappingPopValues, alleleValues, dartValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingAllelicSSRDArT(dataset, mappingPop, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	MappingPop mappingPopAfter = manager.getMappingPopByDatasetId(datasetId);
    	Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
    	assertEquals(mappingPop, mappingPopAfter); // MappingPop updated
    	
    	List<MappingABHRow> rowsAfter = manager.getMappingABHRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
