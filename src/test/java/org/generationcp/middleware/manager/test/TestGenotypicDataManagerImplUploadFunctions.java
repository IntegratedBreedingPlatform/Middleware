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
import java.util.Arrays;
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
import org.generationcp.middleware.pojos.gdms.SNPDataRow;
import org.generationcp.middleware.pojos.gdms.SSRDataRow;
import org.generationcp.middleware.utils.test.Debug;
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
    
    private static final int NUMBER_OF_ROWS = 10; //210 * 260; 

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
    
    private List<Object> createMarkerRecords() {

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
        MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
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
        Integer sampleId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;
        Integer markerSampleId = 1;
        Integer accSampleId = 1;

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

        AccMetadataSet accMetadataSet = new AccMetadataSet(null, datasetId, gId, nameId, sampleId);
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(null, datasetId, markerId, markerSampleId);
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue,
                peakHeight);
        CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue, markerSampleId, accSampleId);
        DartValues dartValues = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate,
                picValue, discordance);
        MappingPop mappingPop = new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize,
                populationType, mapDataDescription, scoringScheme, mapId);
        MappingPopValues mappingPopValues = new MappingPopValues(mpId, mapCharValue, datasetId, gId, markerId, markerSampleId, accSampleId);
        Marker marker = (Marker) createMarkerRecords().get(0);

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
    public void testSetDart() throws Exception {
    	
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<AlleleValues> alleleValueList = new ArrayList<AlleleValues>();
        List<DartValues> dartValueList = new ArrayList<DartValues>();
        
        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){
            
        	mappingRecords = createMappingRecords();
        	
        	Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_DART.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);
            
            AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
            alleleValues.setMarkerId(marker.getMarkerId());
            alleleValues.setGid(accMetadataSet.getGermplasmId());
            alleleValueList.add(alleleValues);
            
            DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
            dartValues.setMarkerId(marker.getMarkerId());
            dartValueList.add(dartValues);

            id--;
        }

        Boolean addStatus = manager.setDart(dataset, datasetUser, markers, markerMetadataSets, 
                accMetadataSets, dartValueList, alleleValueList);
        assertTrue(addStatus);
        
        Debug.println(INDENT, "testSetDArT() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, null);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, alleleValueList);
            Debug.printObjects(INDENT * 2, dartValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
        	select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
        	select * from gdms_marker_metadataset where dataset_id = <datasetId>;
        	select * from gdms_acc_metadataset where dataset_id = <datasetId>;
        	select * from gdms_allele_values where dataset_id = <datasetId>;
        	select * from gdms_dart_values where dataset_id = <datasetId>;
         */
    }

    @Test
    public void testSetSSR() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 1000)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<AlleleValues> alleleValueList = new ArrayList<AlleleValues>();
        
        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){

            mappingRecords = createMappingRecords();
        	
            Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_SSR.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            accMetadataSets.add(accMetadataSet);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);
            
            AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
            alleleValues.setMarkerId(marker.getMarkerId());
            alleleValues.setGid(accMetadataSet.getGermplasmId());
            alleleValueList.add(alleleValues);
            
            id--;
        }

        Boolean addStatus = manager.setSSR(dataset, datasetUser, markers, markerMetadataSets, accMetadataSets, alleleValueList);
        assertTrue(addStatus);
        
        Debug.println(INDENT, "testSetSSR() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, null);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, alleleValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
    		select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
    		select * from gdms_marker_metadataset where dataset_id = <datasetId>;
    		select * from gdms_acc_metadataset where dataset_id = <datasetId>;
    		select * from gdms_allele_values where dataset_id = <datasetId>;
         */
    }

    @Test
    public void testSetSNP() throws Exception {
    	
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<CharValues> charValueList = new ArrayList<CharValues>();

        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){
        	
            mappingRecords = createMappingRecords();
        	
        	Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            accMetadataSets.add(accMetadataSet);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);

            CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
            charValues.setMarkerId(marker.getMarkerId());
            charValues.setGid(accMetadataSet.getGermplasmId());
            charValueList.add(charValues);

            id--;
        }
        
        Boolean addStatus = manager.setSNP(dataset, datasetUser, markers, markerMetadataSets, accMetadataSets, charValueList);
        assertTrue(addStatus);
        
        Debug.println(INDENT, "testSetSNP() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, null);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, charValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
	    	select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
	    	select * from gdms_marker_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_acc_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_char_values where dataset_id = <datasetId>;
         */
    }
    

    @Test
    public void testSetMappingABH() throws Exception {
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);

        // Dataset Fields
        String datasetName = "Map_Pop GCP-832 Test";
        String datasetDesc = "Map_Pop GCP-832 Test Description";
        String datasetType = "mapping";
        String species = "Groundnut";
        String dataType = "map";
        String genus = "Groundnut";

        dataset.setDatasetName(datasetName);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }

        dataset.setDatasetDesc(datasetDesc);
        dataset.setDatasetType(datasetType);
        dataset.setDatasetType(datasetType);
        dataset.setSpecies(species);
        dataset.setDataType(dataType);
        dataset.setGenus(genus);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<MappingPopValues> mappingPopValueList = new ArrayList<MappingPopValues>();
        
        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){

            mappingRecords = createMappingRecords();
        	
            Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_MAPPING.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            accMetadataSets.add(accMetadataSet);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);
            
            MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
            mappingPopValues.setMarkerId(marker.getMarkerId());
            mappingPopValues.setGid(accMetadataSet.getGermplasmId());
            mappingPopValueList.add(mappingPopValues);

            id--;
        }
        
        Boolean addStatus = manager.setMappingABH(dataset, datasetUser, mappingPop, markers, 
                markerMetadataSets, accMetadataSets, mappingPopValueList);
        assertTrue(addStatus);
        
        Debug.println(INDENT, "testSetMappingABH() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, mappingPop);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, mappingPopValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
	    	select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
	    	select * from gdms_marker_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_acc_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_mapping_pop_values where dataset_id = <datasetId>;
	     */
    }

    @Test
    public void testSetMappingAllelicSNP() throws Exception {
    	
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<MappingPopValues> mappingPopValueList = new ArrayList<MappingPopValues>();
        List<CharValues> charValueList = new ArrayList<CharValues>();
        
        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){
            
        	mappingRecords = createMappingRecords();
        	
        	Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_MAPPING.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            accMetadataSets.add(accMetadataSet);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);
            
            MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
            mappingPopValues.setMarkerId(marker.getMarkerId());
            mappingPopValues.setGid(accMetadataSet.getGermplasmId());
            mappingPopValueList.add(mappingPopValues);
            
            CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
            charValues.setMarkerId(marker.getMarkerId());
            charValues.setGid(accMetadataSet.getGermplasmId());
            charValueList.add(charValues);
            
            id--;
        }

        Boolean addStatus = manager.setMappingAllelicSNP(dataset, datasetUser, mappingPop, 
                markers, markerMetadataSets, accMetadataSets, mappingPopValueList, charValueList);

        assertTrue(addStatus);
        
        Debug.println(INDENT, "testSetMappingAllelicSNP() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, mappingPop);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, mappingPopValueList);
            Debug.printObjects(INDENT * 2, charValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
	    	select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
	    	select * from gdms_marker_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_acc_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_mapping_pop_values where dataset_id = <datasetId>;
	    	select * from gdms_char_values where dataset_id = <datasetId>;
	     */
    }

    @Test
    public void testSetMappingAllelicSSRDArT() throws Exception {
    	
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
        
        Dataset dataset = (Dataset) mappingRecords.get(DATASET);
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset
        if (dataset.getDatasetName().length() > 30){
            dataset.setDatasetName(dataset.getDatasetName().substring(0, 30));
        }
        DatasetUsers datasetUser = (DatasetUsers) mappingRecords.get(DATASET_USERS);
        MappingPop mappingPop = (MappingPop) mappingRecords.get(MAPPING_POP);

        List<Marker> markers = new ArrayList<Marker>();
        List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();
        List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
        List<MappingPopValues> mappingPopValueList = new ArrayList<MappingPopValues>();
        List<AlleleValues> alleleValueList = new ArrayList<AlleleValues>();
        List<DartValues> dartValueList = new ArrayList<DartValues>();
        
        int id = -1;
        for (int i=0; i<NUMBER_OF_ROWS; i++){
            
        	mappingRecords = createMappingRecords();
        	
        	Marker marker = (Marker) mappingRecords.get(MARKER);
        	marker.setMarkerId(id);
            marker.setMarkerType(GdmsType.TYPE_MAPPING.getValue());
            marker.setMarkerName(marker.getMarkerName() + id);
            markers.add(marker);
            
            AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
            accMetadataSet.setGermplasmId(id);
            accMetadataSets.add(accMetadataSet);
            
            MarkerMetadataSet markerMetadataSet = (MarkerMetadataSet) mappingRecords.get(MARKER_METADATA_SET);
            markerMetadataSet.setMarkerId(marker.getMarkerId());
            markerMetadataSets.add(markerMetadataSet);
            
            MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
            mappingPopValues.setMarkerId(marker.getMarkerId());
            mappingPopValues.setGid(accMetadataSet.getGermplasmId());
            mappingPopValueList.add(mappingPopValues);
            
            AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
            alleleValues.setMarkerId(marker.getMarkerId());
            alleleValues.setGid(accMetadataSet.getGermplasmId());
            alleleValueList.add(alleleValues);
            
            DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
            dartValues.setMarkerId(marker.getMarkerId());
            dartValueList.add(dartValues);

            id--;
        }

        Boolean addStatus = manager.setMappingAllelicSSRDArT(dataset, datasetUser, mappingPop, markers, 
                markerMetadataSets, accMetadataSets, mappingPopValueList, alleleValueList, dartValueList);

        assertTrue(addStatus);
                
        Debug.println(INDENT, "testSetMappingAllelicSSRDArT() Added: ");
        printUploadedData(dataset, datasetUser, markers, markerMetadataSets, mappingPop);
        if (accMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, accMetadataSets);
            Debug.printObjects(INDENT * 2, mappingPopValueList);
            Debug.printObjects(INDENT * 2, alleleValueList);
            Debug.printObjects(INDENT * 2, dartValueList);
        } else {
            Debug.println(INDENT * 2, "#Data Rows Added: " + accMetadataSets.size());
        }
        /* TO VERIFY RESULTS IN DB:
			select * from gdms_dataset where dataset_id = <datasetId>;
	    	select * from gdms_dataset_users order by dataset_id limit 1;
    		select * from gdms_marker order by marker_id limit <NUMBER_OF_ROWS>;
	    	select * from gdms_marker_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_acc_metadataset where dataset_id = <datasetId>;
	    	select * from gdms_mapping_pop_values where dataset_id = <datasetId>;
	    	select * from gdms_allele_values where dataset_id = <datasetId>;
	    	select * from gdms_dart_values where dataset_id = <datasetId>;
	     */

    }
    
    private void printUploadedData(Dataset dataset, DatasetUsers datasetUser, 
            List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets, MappingPop mappingPop){
    	
        Debug.println(INDENT * 2, dataset.toString());
        Debug.println(INDENT * 2, datasetUser.toString());
        if (mappingPop != null){
            Debug.println(INDENT * 2, mappingPop.toString());
        }
        if (markers != null && markers.size() < 20) {
            Debug.printObjects(INDENT * 2, markers);
        } else {
            Debug.println(INDENT * 2, "#Markers Added: " + markers.size());
        }
        if (markerMetadataSets != null && markerMetadataSets.size() < 20) {
            Debug.printObjects(INDENT * 2, markerMetadataSets);
        } else {
            Debug.println(INDENT * 2, "#MarkerMetadataSets Added: " + markerMetadataSets.size());
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
    		for (Dataset dataset : datasets){
    			if (dataset.getDatasetId() < 0){
    				return dataset;
    			}
    		}
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
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	List<DartDataRow> rows = manager.getDartDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);

		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

    	// Update existing rows
    	List<DartDataRow> updatedRows = new ArrayList<DartDataRow>();

    	for (DartDataRow row : rows){

    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		// Update dartValues
    		DartValues dartValues = updateDartValues(row.getDartValues(), updateId);
    		
    		updatedRows.add(new DartDataRow(accMetadataSet, alleleValues, dartValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new alleleValues, new dartValues, new accMetadataSet
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
        DartDataRow newRow = new DartDataRow(accMetadataSet, alleleValues, dartValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateDart(dataset, markers, markerMetadataSets, updatedRows);
    	
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
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
       	
    	updateDataset(dataset, updateId.toString());
    	
    	List<SNPDataRow> rows = manager.getSNPDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);

		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

    	// Update existing rows
    	List<SNPDataRow> updatedRows = new ArrayList<SNPDataRow>();
    	
    	for (SNPDataRow row : rows){

    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		    		
    		// Update dartValues
    		CharValues charValues = updateCharValues(row.getCharValues(), updateId.toString());
    		
    		updatedRows.add(new SNPDataRow(accMetadataSet, charValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new charValues, new accMetadataSet
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
        SNPDataRow newRow = new SNPDataRow(accMetadataSet, charValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateSNP(dataset, markers, markerMetadataSets, updatedRows);
    	
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
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
       	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
    	List<SSRDataRow> rows = manager.getSSRDataRows(datasetId);
    	Debug.println("ROWS BEFORE: ");
    	Debug.printObjects(INDENT, rows);

		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

    	// Update existing rows
    	List<SSRDataRow> updatedRows = new ArrayList<SSRDataRow>();

    	for (SSRDataRow row : rows){

    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		updatedRows.add(new SSRDataRow(accMetadataSet, alleleValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new alleleValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        SSRDataRow newRow = new SSRDataRow(accMetadataSet, alleleValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateSSR(dataset, markers, markerMetadataSets, updatedRows);
    	
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
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId);
    	
		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

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
    		
    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		
    		updatedRows.add(new MappingABHRow(accMetadataSet, mappingPopValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        MappingABHRow newRow = new MappingABHRow(accMetadataSet, mappingPopValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingABH(dataset, mappingPop, markers, markerMetadataSets, updatedRows);
    	
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
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId);
    	
		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

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
    		
    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		    		
    		// Update charValues
    		CharValues charValues = updateCharValues(row.getCharValues(), updateId);
    		
    		updatedRows.add(new MappingAllelicSNPRow(accMetadataSet, mappingPopValues, charValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        CharValues charValues = (CharValues) mappingRecords.get(CHAR_VALUES);
        MappingAllelicSNPRow newRow = new MappingAllelicSNPRow(accMetadataSet, mappingPopValues, charValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingAllelicSNP(dataset, mappingPop, markers, markerMetadataSets, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	MappingPop mappingPopAfter = manager.getMappingPopByDatasetId(datasetId);
    	Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
    	assertEquals(mappingPop, mappingPopAfter); // MappingPop updated
    	
    	List<MappingAllelicSNPRow> rowsAfter = manager.getMappingAllelicSNPRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }

    @Test
    public void testUpdateMappingAllelicSSRDart() throws Exception {
    	GdmsType gdmsType = GdmsType.TYPE_MAPPING;
    	Integer updateId = (int)(Math.random() * 1000); // to append to description/remarks field for the update operation

    	Dataset dataset = getTestDatasetByType(gdmsType, GdmsType.TYPE_SSR);
    	if (dataset == null){
    		Debug.println(INDENT, "Please upload dataset of type " + gdmsType.getValue() + " first before testing update.");
    		return;
    	}
    	
    	Integer datasetId = dataset.getDatasetId();
    	updateDataset(dataset, updateId.toString());
    	
		// Update markers
    	List<Marker> markers = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5), 0, Integer.MAX_VALUE);

		// No change in markerMetadataSet
    	List<MarkerMetadataSet> markerMetadataSets = new ArrayList<MarkerMetadataSet>();

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
    		
    		// No change in accMetadataSet
    		AccMetadataSet accMetadataSet = row.getAccMetadataSet();
    		    		
    		// Update alleleValues
    		AlleleValues alleleValues = updateAlleleValues(row.getAlleleValues(), updateId);

    		// Update dartValues
    		DartValues dartValues = updateDartValues(row.getDartValues(), updateId);
    		
    		updatedRows.add(new MappingAllelicSSRDArTRow(accMetadataSet, mappingPopValues, alleleValues, dartValues));
    	}    	
    	
    	// Add (or update) a row - existing dataset, new mappingPopValues, new accMetadataSet, new markerMetadataset
        java.util.Map<String, Object> mappingRecords = createMappingRecords();
    	AccMetadataSet accMetadataSet = (AccMetadataSet) mappingRecords.get(ACC_METADATA_SET);
        MappingPopValues mappingPopValues = (MappingPopValues) mappingRecords.get(MAPPING_POP_VALUES);
        AlleleValues alleleValues = (AlleleValues) mappingRecords.get(ALLELE_VALUES);
        DartValues dartValues = (DartValues) mappingRecords.get(DART_VALUES);
        MappingAllelicSSRDArTRow newRow = new MappingAllelicSSRDArTRow(accMetadataSet, mappingPopValues, alleleValues, dartValues); 
		updatedRows.add(newRow);
    	Debug.println("ADD OR UPDATE ROW: " + newRow);
    	
    	//UPDATE
    	manager.updateMappingAllelicSSRDArT(dataset, mappingPop, markers, markerMetadataSets, updatedRows);
    	
    	Dataset datasetAfter = manager.getDatasetById(datasetId);
    	Debug.printObject(0, "DATASET AFTER: " + datasetAfter);
    	assertEquals(dataset, datasetAfter); // Dataset updated

    	MappingPop mappingPopAfter = manager.getMappingPopByDatasetId(datasetId);
    	Debug.printObject(0, "MAPPINGPOP AFTER: " + mappingPopAfter);
    	assertEquals(mappingPop, mappingPopAfter); // MappingPop updated
    	
    	List<MappingAllelicSSRDArTRow> rowsAfter = manager.getMappingAllelicSSRDArTRows(datasetId);
    	Debug.println("ROWS AFTER: ");
    	Debug.printObjects(INDENT, rowsAfter);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
