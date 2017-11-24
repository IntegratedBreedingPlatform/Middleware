package org.generationcp.middleware.service.impl.gdms;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.gdms.DatasetRetrieveDto;
import org.generationcp.middleware.service.api.gdms.DatasetUploadDto;
import org.hamcrest.CoreMatchers;
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

	@Mock
	private CharValuesDAO charValuesDAO;

	private DatasetServiceImpl datasetService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		datasetService = new DatasetServiceImpl(session);
		datasetService.setSampleService(sampleService);
		datasetService.setDatasetDAO(datasetDAO);
		datasetService.setMarkerDAO(markerDAO);
		datasetService.setCharValuesDAO(charValuesDAO);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NullName() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetUploadDto.setName(null);
		datasetService.saveDataset(datasetUploadDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_NullMarkers() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetService.saveDataset(datasetUploadDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NullSamples() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetUploadDto.setSampleAccessions(null);
		datasetService.saveDataset(datasetUploadDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_NullDataset() throws Exception {
		datasetService.saveDataset(null);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_LongName() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetUploadDto.setName(RandomStringUtils.random(31));
		datasetUploadDto.setSampleAccessions(new LinkedHashSet<DatasetUploadDto.SampleKey>());
		datasetUploadDto.setMarkers(new ArrayList<String>());
		datasetService.saveDataset(datasetUploadDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_DatasetNameExists() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("a");
		datasetUploadDto.setName("Dataset");
		datasetUploadDto.setMarkers(markers);
		final Dataset datasetFromDB = new Dataset();
		datasetFromDB.setDatasetName("Dataset");
		Mockito.when(datasetDAO.getByName(datasetUploadDto.getName())).thenReturn(datasetFromDB);
		datasetUploadDto.setSampleAccessions(new LinkedHashSet<DatasetUploadDto.SampleKey>());
		datasetService.saveDataset(datasetUploadDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_DuplicatedMarkers() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetUploadDto.setSampleAccessions(new LinkedHashSet<DatasetUploadDto.SampleKey>());
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("a");
		datasetUploadDto.setName("Dataset");
		datasetUploadDto.setMarkers(markers);
		Mockito.when(datasetDAO.getByName(datasetUploadDto.getName())).thenReturn(null);
		datasetService.saveDataset(datasetUploadDto);
	}


	@Test (expected = Exception.class)
	public void testSaveDataset_InvalidCharValuesSize() throws Exception {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetUploadDto.setName("Dataset");
		datasetUploadDto.setMarkers(markers);
		Mockito.when(datasetDAO.getByName(datasetUploadDto.getName())).thenReturn(null);

		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetUploadDto.SampleKey sampleKey1 = new DatasetUploadDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetUploadDto.setSampleAccessions(sampleAccesionSet);

		final String[][] charValues = { {"A","B"}, {"C","D"}, {"E", "F"}};
		datasetUploadDto.setCharValues(charValues);

		datasetService.saveDataset(datasetUploadDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NotFoundSample() throws Exception {
		final Set<String> sampleUIDs = new HashSet<>();
		sampleUIDs.add("sampleKey1");
		final Map<String, SampleDTO> sampleDTOMap = new HashMap<>();
		Mockito.when(sampleService.getSamplesBySampleUID(sampleUIDs)).thenReturn(sampleDTOMap);

		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetUploadDto.setName("Dataset");
		datasetUploadDto.setMarkers(markers);

		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetUploadDto.SampleKey sampleKey1 = new DatasetUploadDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetUploadDto.setSampleAccessions(sampleAccesionSet);

		Mockito.when(datasetDAO.getByName(datasetUploadDto.getName())).thenReturn(null);

		final String[][] charValues = { {"A","B"}};
		datasetUploadDto.setCharValues(charValues);

		datasetService.saveDataset(datasetUploadDto);
	}

	@Test (expected = Exception.class)
	public void testSaveDataset_NotFoundMarker() throws Exception {
		final Set<String> sampleUIDs = new HashSet<>();
		sampleUIDs.add("SampleUID1");
		final Map<String, SampleDTO> sampleDTOMap = new HashMap<>();
		sampleDTOMap.put("SampleUID1", new SampleDTO());
		Mockito.when(sampleService.getSamplesBySampleUID(sampleUIDs)).thenReturn(sampleDTOMap);
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		final List<String> markers = new ArrayList<>();
		markers.add("a");
		markers.add("b");
		datasetUploadDto.setName("Dataset");
		datasetUploadDto.setMarkers(markers);
		final LinkedHashSet sampleAccesionSet = new LinkedHashSet<>();
		final DatasetUploadDto.SampleKey sampleKey1 = new DatasetUploadDto().new SampleKey();
		sampleKey1.setSampleUID("SampleUID1");
		sampleAccesionSet.add(sampleKey1);
		datasetUploadDto.setSampleAccessions(sampleAccesionSet);
		Mockito.when(datasetDAO.getByName(datasetUploadDto.getName())).thenReturn(null);
		final List<Marker> markersFromDB = new ArrayList<>();
		Mockito.when(markerDAO.getByNames(datasetUploadDto.getMarkers(), 0, 0)).thenReturn(markersFromDB);
		final String[][] charValues = { {"A","B"}};
		datasetUploadDto.setCharValues(charValues);
		datasetService.saveDataset(datasetUploadDto);
	}

	@Test (expected = NullPointerException.class)
	public void testGetDataset_NullDatasetName() throws Exception{
		datasetService.getDataset(null);
	}

	@Test
	public void testGetDataset_DatasetNotExist() throws Exception{
		final String datasetName = "name";
		Mockito.when(datasetDAO.getByName(datasetName)).thenReturn(null);
		final DatasetRetrieveDto datasetRetrieveDto = datasetService.getDataset(datasetName);
		assertThat(datasetRetrieveDto, is(nullValue()));
	}

	@Test (expected = MiddlewareException.class)
	public void testGetDataset_ExceptionWhenQueryingData() throws Exception{
		final String datasetName = "name";
		Mockito.when(datasetDAO.getByName(datasetName)).thenReturn(new Dataset());
		Mockito.when(charValuesDAO.getCharValueElementsByDatasetId(Mockito.anyInt())).thenThrow(MiddlewareQueryException.class);
		datasetService.getDataset(datasetName);
	}



}
