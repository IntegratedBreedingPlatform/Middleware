package org.generationcp.middleware.service.impl.gdms;

import org.generationcp.middleware.pojos.gdms.CharValueElement;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.service.api.gdms.DatasetRetrieveDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clarysabel on 11/21/17.
 */
public class DatasetRetrieveDtoBuilder {

	public static DatasetRetrieveDto build(final Dataset dataset, final List<CharValueElement> charValueElements) {
		final DatasetRetrieveDto datasetRetrieveDto = buildDatasetRetrieveDto(dataset);
		datasetRetrieveDto.setCharValueDtos(buildCharValuesDtoList(charValueElements));
		return datasetRetrieveDto;
	}

	private static DatasetRetrieveDto buildDatasetRetrieveDto(final Dataset dataset) {
		final DatasetRetrieveDto datasetRetrieveDto = new DatasetRetrieveDto();
		datasetRetrieveDto.setDataType(dataset.getDataType());
		datasetRetrieveDto.setDescription(dataset.getDatasetDesc());
		datasetRetrieveDto.setGenus(dataset.getGenus());
		datasetRetrieveDto.setMethod(dataset.getMethod());
		datasetRetrieveDto.setMissingData(dataset.getMissingData());
		datasetRetrieveDto.setRemarks(dataset.getRemarks());
		datasetRetrieveDto.setName(dataset.getDatasetName());
		datasetRetrieveDto.setScore(dataset.getScore());
		datasetRetrieveDto.setSpecies(dataset.getSpecies());
		datasetRetrieveDto.setType(dataset.getDatasetType());
		datasetRetrieveDto.setUploadDate(dataset.getUploadTemplateDate());
		datasetRetrieveDto.setUserId(dataset.getDatasetUsers().getUserId());
		return datasetRetrieveDto;
	}

	private static List<DatasetRetrieveDto.CharValueDto> buildCharValuesDtoList(final List<CharValueElement> charValueElements) {
		final Map<String, List<CharValueElement>> charValuesBySampleAccessionMap = new HashMap<>();
		for (final CharValueElement charValueElement : charValueElements) {
			final String key = charValueElement.getSampleUID() + charValueElement.getAccessionId();
			if (charValuesBySampleAccessionMap.containsKey(key)) {
				charValuesBySampleAccessionMap.get(key).add(charValueElement);
			} else {
				final List<CharValueElement> charValueElementToMap = new ArrayList<>();
				charValueElementToMap.add(charValueElement);
				charValuesBySampleAccessionMap.put(key, charValueElementToMap);
			}
		}
		final List<DatasetRetrieveDto.CharValueDto> charValueDtoList = new ArrayList<>();
		for (final Map.Entry<String, List<CharValueElement>> entry : charValuesBySampleAccessionMap.entrySet()) {
			final DatasetRetrieveDto.CharValueDto charValueDto = buildCharValue(entry.getValue().get(0));
			final Map<String, String> markerValues = new HashMap<>();
			for (final CharValueElement charValueElement : entry.getValue()) {
				markerValues.put(charValueElement.getMarkerName(), charValueElement.getCharValue());
			}
			charValueDto.setCharValues(markerValues);
			charValueDtoList.add(charValueDto);
		}
		return charValueDtoList;
	}

	private static DatasetRetrieveDto.CharValueDto buildCharValue(final CharValueElement charValueElement) {
		final DatasetRetrieveDto.CharValueDto charValueDto = new DatasetRetrieveDto().new CharValueDto();
		charValueDto.setAccession(charValueElement.getAccessionId());
		charValueDto.setDesignation(charValueElement.getDesignation());
		charValueDto.setGid(charValueElement.getGid());
		charValueDto.setPlantNumber(charValueElement.getPlantNo());
		charValueDto.setSampleName(charValueElement.getSampleName());
		charValueDto.setSampleUID(charValueElement.getSampleUID());
		return charValueDto;
	}

}
