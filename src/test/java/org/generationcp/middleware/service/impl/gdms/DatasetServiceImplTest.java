package org.generationcp.middleware.service.impl.gdms;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.gdms.DatasetDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by clarysabel on 11/9/17.
 */
public class DatasetServiceImplTest {

	@Mock
	private HibernateSessionProvider session;

	@Mock
	private SampleService sampleService;

	@Mock
	private DatasetDAO datasetDAO;

	@Mock
	private MarkerDAO markerDAO;

	private DatasetServiceImpl datasetService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		datasetService = new DatasetServiceImpl(session);
		datasetService.setSampleService(sampleService);
		datasetService.setDatasetDAO(datasetDAO);
		datasetService.setMarkerDAO(markerDAO);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NullName() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		datasetDto.setName(null);
		datasetService.saveDataset(datasetDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_NullMarkers() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		datasetService.saveDataset(datasetDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NullSamples() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		datasetDto.setSampleAccesions(null);
		datasetService.saveDataset(datasetDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_NullDataset() throws Exception {
		datasetService.saveDataset(null);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_LongName() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		datasetDto.setName(RandomStringUtils.random(31));
		datasetDto.setSampleAccesions(new LinkedHashSet<DatasetDto.SampleKey>());
		datasetDto.setMarkers(new ArrayList<String>());
		datasetService.saveDataset(datasetDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_DatasetNameExists() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("a");
		datasetDto.setName("Dataset");
		datasetDto.setMarkers(markers);
		final Dataset datasetFromDB = new Dataset();
		datasetFromDB.setDatasetName("Dataset");
		Mockito.when(datasetDAO.getByName(datasetDto.getName())).thenReturn(datasetFromDB);
		datasetDto.setSampleAccesions(new LinkedHashSet<DatasetDto.SampleKey>());
		datasetService.saveDataset(datasetDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_DuplicatedMarkers() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		datasetDto.setSampleAccesions(new LinkedHashSet<DatasetDto.SampleKey>());
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("a");
		datasetDto.setName("Dataset");
		datasetDto.setMarkers(markers);
		Mockito.when(datasetDAO.getByName(datasetDto.getName())).thenReturn(null);
		datasetService.saveDataset(datasetDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_InvalidCharValuesSize() throws Exception {
		final DatasetDto datasetDto = new DatasetDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetDto.setName("Dataset");
		datasetDto.setMarkers(markers);
		Mockito.when(datasetDAO.getByName(datasetDto.getName())).thenReturn(null);

		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetDto.SampleKey sampleKey1 = new DatasetDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetDto.setSampleAccesions(sampleAccesionSet);

		final String[][] charValues = { {"A","B"}, {"C","D"}, {"E", "F"}};
		datasetDto.setCharValues(charValues);

		datasetService.saveDataset(datasetDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NotFoundSample() throws Exception {
		final Set<String> sampleUIDs = new HashSet<>();
		sampleUIDs.add("sampleKey1");
		final Map<String, SampleDTO> sampleDTOMap = new HashMap<>();
		Mockito.when(sampleService.getSamplesBySampleUID(sampleUIDs)).thenReturn(sampleDTOMap);

		final DatasetDto datasetDto = new DatasetDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetDto.setName("Dataset");
		datasetDto.setMarkers(markers);

		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetDto.SampleKey sampleKey1 = new DatasetDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetDto.setSampleAccesions(sampleAccesionSet);

		Mockito.when(datasetDAO.getByName(datasetDto.getName())).thenReturn(null);

		final String[][] charValues = { {"A","B"}};
		datasetDto.setCharValues(charValues);

		datasetService.saveDataset(datasetDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NotFoundMarker() throws Exception {
		final Set<String> sampleUIDs = new HashSet<>();
		sampleUIDs.add("SampleUID1");
		final Map<String, SampleDTO> sampleDTOMap = new HashMap<>();
		sampleDTOMap.put("SampleUID1", new SampleDTO());
		Mockito.when(sampleService.getSamplesBySampleUID(sampleUIDs)).thenReturn(sampleDTOMap);
		final DatasetDto datasetDto = new DatasetDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetDto.setName("Dataset");
		datasetDto.setMarkers(markers);
		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetDto.SampleKey sampleKey1 = new DatasetDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetDto.setSampleAccesions(sampleAccesionSet);
		Mockito.when(datasetDAO.getByName(datasetDto.getName())).thenReturn(null);
		final List<Marker> markersFromDB = new ArrayList<>();
		Mockito.when(markerDAO.getByNames(datasetDto.getMarkers(), 0, 0)).thenReturn(markersFromDB);
		final String[][] charValues = { {"A","B"}};
		datasetDto.setCharValues(charValues);
		datasetService.saveDataset(datasetDto);
	}

}
