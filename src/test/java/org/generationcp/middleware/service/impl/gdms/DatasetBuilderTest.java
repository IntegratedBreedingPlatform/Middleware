package org.generationcp.middleware.service.impl.gdms;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.service.api.gdms.DatasetUploadDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by clarysabel on 11/13/17.
 */
public class DatasetBuilderTest {

	private String name;

	private String description;

	private String type;

	private String genus;

	private String remarks;

	private String dataType;

	private String missingData;

	private String method;

	private String score;

	private Integer userId;

	private String species;

	private String[][] charValues;

	private List<String> markers;

	private LinkedHashSet<DatasetUploadDto.SampleKey> sampleAccessions;

	private Map<String, SampleDTO> sampleDTOMap;

	private Map<String, Marker> markerMap;

	public DatasetBuilderTest() {
		name = RandomStringUtils.random(20);
		description = RandomStringUtils.random(20);
		type = RandomStringUtils.random(3);
		genus = RandomStringUtils.random(3);
		remarks = RandomStringUtils.random(3);
		dataType = RandomStringUtils.random(3);
		missingData = RandomStringUtils.random(30);
		method = RandomStringUtils.random(30);
		score = RandomStringUtils.random(30);
		userId = 1;
		species = RandomStringUtils.random(3);
		charValues = new String[][] {{"A/C", "A/C"}, {"A/C", "A/C"}};
		markers = new ArrayList<>();
		markers.add("marker1");
		markers.add("marker2");
		sampleAccessions = new LinkedHashSet<>();
		final DatasetUploadDto.SampleKey sampleKey1 = new DatasetUploadDto().new SampleKey();
		sampleKey1.setAccession(1);
		sampleKey1.setSampleUID("UID1");
		final DatasetUploadDto.SampleKey sampleKey2 = new DatasetUploadDto().new SampleKey();
		sampleKey2.setAccession(1);
		sampleKey2.setSampleUID("UID2");
		sampleAccessions.add(sampleKey1);
		sampleAccessions.add(sampleKey2);
		SampleDTO sample1 = new SampleDTO();
		sample1.setSampleBusinessKey("UID1");
		sample1.setSampleId(1);
		SampleDTO sample2 = new SampleDTO();
		sample2.setSampleBusinessKey("UID2");
		sample2.setSampleId(2);
		sampleDTOMap = new HashMap<>();
		sampleDTOMap.put("UID1", sample1);
		sampleDTOMap.put("UID2", sample2);
		final Marker marker1 = new Marker();
		marker1.setMarkerId(1);
		marker1.setMarkerName("marker1");
		final Marker marker2 = new Marker();
		marker2.setMarkerId(2);
		marker2.setMarkerName("marker2");
		markerMap = new HashMap<>();
		markerMap.put("marker1", marker1);
		markerMap.put("marker2", marker2);
	}

	private DatasetUploadDto getDatasetUploadDto() {
		final DatasetUploadDto datasetUploadDto = new DatasetUploadDto();
		datasetUploadDto.setName(name);
		datasetUploadDto.setDescription(description);
		datasetUploadDto.setType(type);
		datasetUploadDto.setGenus(genus);
		datasetUploadDto.setRemarks(remarks);
		datasetUploadDto.setDataType(dataType);
		datasetUploadDto.setMissingData(missingData);
		datasetUploadDto.setMethod(method);
		datasetUploadDto.setScore(score);
		datasetUploadDto.setUserId(userId);
		datasetUploadDto.setSpecies(species);
		datasetUploadDto.setCharValues(charValues);
		datasetUploadDto.setMarkers(markers);
		datasetUploadDto.setSampleAccessions(sampleAccessions);
		return datasetUploadDto;
	}

	@Test
	public void build_Ok() throws Exception {
		final DatasetUploadDto datasetUploadDto = this.getDatasetUploadDto();
		final Dataset dataset = DatasetBuilder.build(datasetUploadDto, sampleDTOMap, markerMap);
		assertThat(dataset.getDatasetName(), is(equalTo(datasetUploadDto.getName())));
		assertThat(dataset.getDatasetDesc(), is(equalTo(datasetUploadDto.getDescription())));
		assertThat(dataset.getDataType(), is(equalTo(datasetUploadDto.getDataType())));
		assertThat(dataset.getDatasetType(), is(equalTo(datasetUploadDto.getType())));
		assertThat(dataset.getGenus(), is(equalTo(datasetUploadDto.getGenus())));
		assertThat(dataset.getRemarks(), is(equalTo(datasetUploadDto.getRemarks())));
		assertThat(dataset.getMissingData(), is(equalTo(datasetUploadDto.getMissingData())));
		assertThat(dataset.getMethod(), is(equalTo(datasetUploadDto.getMethod())));
		assertThat(dataset.getScore(), is(equalTo(datasetUploadDto.getScore())));
		assertThat(dataset.getSpecies(), is(equalTo(datasetUploadDto.getSpecies())));
		assertThat(dataset.getDatasetUsers().getUserId(), is(equalTo(datasetUploadDto.getUserId())));

		final List<AccMetadataSet> accMetadataSets = dataset.getAccMetadataSets();
		assertThat(accMetadataSets, hasSize(sampleAccessions.size()));
		final List<DatasetUploadDto.SampleKey> sampleKeys = new ArrayList<>();
		for (final AccMetadataSet accMetadataSet : accMetadataSets) {
			final DatasetUploadDto.SampleKey sampleKey = new DatasetUploadDto().new SampleKey();
			sampleKey.setSampleUID(accMetadataSet.getSample().getSampleBusinessKey());
			sampleKey.setAccession(accMetadataSet.getAccSampleId());
			sampleKeys.add(sampleKey);
		}
		assertThat(sampleKeys, contains(sampleAccessions.toArray()));

		final List<MarkerMetadataSet> markerMetadataSets = dataset.getMarkerMetadataSets();

		final Function<MarkerMetadataSet, Integer> markerMetadatasetToIds = new Function<MarkerMetadataSet, Integer>() {

			public Integer apply(MarkerMetadataSet markerMetadataSet) {
				return markerMetadataSet.getMarkerId();
			}
		};

		final List<Integer> markerIdsFromMarkerMetadataset = Lists.transform(markerMetadataSets, markerMetadatasetToIds);

		final Function<Marker, Integer> markersToIds = new Function<Marker, Integer>() {

			public Integer apply(Marker marker) {
				return marker.getMarkerId();
			}
		};

		final List<Integer> markerIdsFromMarkerMap = Lists.transform(new ArrayList<Marker>(markerMap.values()), markersToIds);

		assertThat(markerIdsFromMarkerMetadataset, contains(markerIdsFromMarkerMap.toArray()));

	}

}
