package org.generationcp.middleware.service.impl.gdms;

import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.service.api.gdms.DatasetUploadDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by clarysabel on 11/9/17.
 */
public class DatasetBuilder {

	public static Dataset build(final DatasetUploadDto datasetUploadDto, final Map<String, SampleDTO> sampleDTOMap,
		final Map<String, Marker> markerMap) {
		final Dataset dataset = buildDataset(datasetUploadDto);
		dataset.setDatasetUsers(buildDatasetUser(dataset, datasetUploadDto));
		dataset.setAccMetadataSets(buildAccessionMetadataset(dataset, datasetUploadDto, sampleDTOMap));
		dataset.setMarkerMetadataSets(buildMarkerMetadataSet(dataset, markerMap));
		dataset.setCharValues(buildCharValues(dataset, datasetUploadDto, sampleDTOMap, markerMap));
		return dataset;
	}

	private static Dataset buildDataset(final DatasetUploadDto datasetUploadDto) {
		final Dataset ds = new Dataset();
		ds.setDatasetName(datasetUploadDto.getName());
		ds.setDatasetDesc(datasetUploadDto.getDescription());
		ds.setDatasetType(datasetUploadDto.getType());
		ds.setGenus(datasetUploadDto.getGenus());
		ds.setSpecies(datasetUploadDto.getSpecies());
		ds.setUploadTemplateDate(new Date());
		ds.setRemarks(datasetUploadDto.getRemarks());
		ds.setDataType(datasetUploadDto.getDataType());
		ds.setMissingData(datasetUploadDto.getMissingData());
		ds.setMethod(datasetUploadDto.getMethod());
		ds.setScore(datasetUploadDto.getScore());

		return ds;
	}

	private static List<CharValues> buildCharValues(final Dataset dataset, final DatasetUploadDto datasetUploadDto, final Map<String, SampleDTO> sampleDTOMap,
			final Map<String, Marker> markerMap) {

		final List<CharValues> charValuesList = new ArrayList<>();

		int row = 0;
		for (final DatasetUploadDto.SampleKey sampleKey : datasetUploadDto.getSampleAccessions()) {
			int column = 0;
			for (final String marker : datasetUploadDto.getMarkers()) {
				final CharValues charValues = new CharValues();
				final Sample sample = new Sample();
				sample.setSampleId(sampleDTOMap.get(sampleKey.getSampleUID()).getSampleId());

				charValues.setDataset(dataset);
				charValues.setSample(sample);
				charValues.setCharValue(getCharData(datasetUploadDto.getCharValues()[row][column]));
				charValues.setMarker(markerMap.get(marker));
				charValues.setAccSampleId(sampleKey.getAccession());
				charValuesList.add(charValues);
				column++;
			}
			row++;
		}
		return charValuesList;
	}


	private static List<MarkerMetadataSet> buildMarkerMetadataSet(final Dataset dataset, final Map<String, Marker> markerMap) {
		final List<MarkerMetadataSet> markerMetadataSetList = new ArrayList<>();
		for (final Marker marker : markerMap.values()) {
			final MarkerMetadataSet mdb = new MarkerMetadataSet();
			mdb.setDataset(dataset);
			mdb.setMarkerId(marker.getMarkerId());
			mdb.setMarkerSampleId(1);
			markerMetadataSetList.add(mdb);
		}
		return markerMetadataSetList;
	}

	private static DatasetUsers buildDatasetUser(final Dataset dataset, final DatasetUploadDto datasetUploadDto) {
		final DatasetUsers datasetUsers = new DatasetUsers();
		datasetUsers.setDataset(dataset);
		datasetUsers.setUserId(datasetUploadDto.getUserId());
		return datasetUsers;
	}

	private static List<AccMetadataSet> buildAccessionMetadataset(final Dataset dataset, final DatasetUploadDto datasetUploadDto,
			final Map<String, SampleDTO> sampleDTOMap) {

		final List<AccMetadataSet> accMetadataSetList = new ArrayList<>();

		for (final DatasetUploadDto.SampleKey sampleAccesionKey : datasetUploadDto.getSampleAccessions()) {
			final AccMetadataSet ams = new AccMetadataSet();
			final Sample sample = new Sample();
			sample.setSampleId(sampleDTOMap.get(sampleAccesionKey.getSampleUID()).getSampleId());
			sample.setSampleBusinessKey(sampleDTOMap.get(sampleAccesionKey.getSampleUID()).getSampleBusinessKey());
			ams.setDataset(dataset);
			ams.setSample(sample);
			ams.setAccSampleId(sampleAccesionKey.getAccession());
			accMetadataSetList.add(ams);
		}

		return accMetadataSetList;
	}

	private static String getCharData(final String charValue) {
		String charData = "";
		if (charValue.length() > 2) {
			if (charValue.contains(":")) {
				final String str1 = charValue.substring(0, charValue.length() - 2);
				final String str2 = charValue.substring(2, charValue.length());
				charData = str1 + "/" + str2;
			} else if (charValue.contains("/")) {
				charData = charValue;
			} else if ((charValue.equalsIgnoreCase("DUPE")) || (charValue.equalsIgnoreCase("BAD"))) {
				charData = "?";
			} else {
				throw new MiddlewareException("Heterozygote data representation should be either : or /" + charValue);
			}

		} else if (charValue.length() == 2) {
			final String str1 = charValue.substring(0, charValue.length() - 1);
			final String str2 = charValue.substring(1);
			charData = str1 + "/" + str2;
		} else if (charValue.length() == 1) {
			if (charValue.equalsIgnoreCase("A")) {
				charData = "A/A";
			} else if (charValue.equalsIgnoreCase("C")) {
				charData = "C/C";
			} else if (charValue.equalsIgnoreCase("G")) {
				charData = "G/G";
			} else if (charValue.equalsIgnoreCase("T")) {
				charData = "T/T";
			} else {
				charData = charValue;
			}
		}
		return charData;
	}

}
