package org.generationcp.middleware.service.impl.gdms;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.gdms.CharValueElement;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.gdms.DatasetRetrieveDto;
import org.generationcp.middleware.service.api.gdms.DatasetService;
import org.generationcp.middleware.service.api.gdms.DatasetUploadDto;
import org.generationcp.middleware.service.impl.study.SampleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by clarysabel on 11/9/17.
 */
@Service
@Transactional
public class DatasetServiceImpl implements DatasetService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatasetServiceImpl.class);

	private DatasetDAO datasetDAO;
	private MarkerDAO markerDAO;
	private SampleService sampleService;
	private CharValuesDAO charValuesDAO;

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.datasetDAO = new DatasetDAO(sessionProvider.getSession());
		this.markerDAO = new MarkerDAO(sessionProvider.getSession());
		this.charValuesDAO = new CharValuesDAO(sessionProvider.getSession());
		this.sampleService = new SampleServiceImpl(sessionProvider);
	}

	@Override
	public Integer saveDataset(final DatasetUploadDto datasetUploadDto) {
		Preconditions.checkNotNull(datasetUploadDto);
		Preconditions.checkNotNull(datasetUploadDto.getMarkers());
		Preconditions.checkNotNull(datasetUploadDto.getSampleAccessions());

		Preconditions.checkArgument(StringUtils.isNotEmpty(datasetUploadDto.getName()), new Exception("Empty dataset name"));

		if (datasetUploadDto.getName().length() > 30) {
			throw new MiddlewareException("Dataset Name value exceeds max char size");
		}
		if (this.datasetDAO.getByName(datasetUploadDto.getName()) != null) {
			throw new MiddlewareException("Dataset Name already exists");
		}
		if (this.isDuplicatedMarkerNames(datasetUploadDto)) {
			throw new MiddlewareException("Duplicated markers not allowed");
		}

		this.validateInput(datasetUploadDto);

		final Set<String> sampleUIDSet = this.getSampleUIDList(datasetUploadDto);
		final Map<String, SampleDTO> sampleDTOMap = this.sampleService.getSamplesBySampleUID(sampleUIDSet);

		this.validateSamples(sampleUIDSet, sampleDTOMap);

		final List<Marker> markers = this.markerDAO.getByNames(datasetUploadDto.getMarkers(), 0, 0);
		final Map<String, Marker> markerMap = this.getMarkersMap(markers);
		this.validateMarkers(datasetUploadDto, markerMap);

		final Dataset dataset = DatasetBuilder.build(datasetUploadDto, sampleDTOMap, markerMap);

		try {
			return this.datasetDAO.save(dataset).getDatasetId();
		} catch (MiddlewareQueryException e) {
			LOGGER.error(e.getMessage(), e);
			throw new MiddlewareException("An error has occurred while saving the dataset");
		}
	}

	@Override
	public DatasetRetrieveDto getDataset(final String datasetName) {
		Preconditions.checkNotNull(datasetName);
		try {
			final Dataset dataset = this.datasetDAO.getByName(datasetName);
			if (dataset != null) {
				List<CharValueElement> charValueElements = this.charValuesDAO.getCharValueElementsByDatasetId(dataset.getDatasetId());
				return DatasetRetrieveDtoBuilder.build(dataset, charValueElements);
			} else {
				return null;
			}
		} catch (MiddlewareQueryException e) {
			LOGGER.error(e.getMessage(), e);
			throw new MiddlewareException("An error has occurred while querying the dataset");
		}
	}

	private Boolean isDuplicatedMarkerNames(final DatasetUploadDto datasetUploadDto) {
		final Set<String> uniqueMarkers = new HashSet<>(datasetUploadDto.getMarkers());
		if (uniqueMarkers.size() != datasetUploadDto.getMarkers().size()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	private Set<String> getSampleUIDList(final DatasetUploadDto datasetUploadDto) {
		final Set<String> uniqueSamples = new HashSet<>();
		for (final DatasetUploadDto.SampleKey key : datasetUploadDto.getSampleAccessions()) {
			uniqueSamples.add(key.getSampleUID());
		}
		return uniqueSamples;
	}

	private Map<String, Marker> getMarkersMap(final List<Marker> markers) {
		final Map<String, Marker> mappedMarkers = Maps.uniqueIndex(markers, new Function<Marker, String>() {

			public String apply(Marker from) {
				return from.getMarkerName();
			}
		});
		return mappedMarkers;
	}

	private void validateMarkers(final DatasetUploadDto datasetUploadDto, final Map<String, Marker> markerMap) {

		if (markerMap.size() != datasetUploadDto.getMarkers().size()) {
			List<String> markersNotFound = new ArrayList<>();

			for (final String marker : datasetUploadDto.getMarkers()) {
				if (!markerMap.containsKey(marker)) {
					markersNotFound.add(marker);
				}
			}

			if (!markersNotFound.isEmpty()) {
				throw new MiddlewareException(
					"Some of the data uploaded is not present in the system. Please verify your file again. Markers not found: "
						+ StringUtils.join(markersNotFound, ","));
			}
		}
	}

	private void validateSamples(final Set<String> sampleUIDSet, final Map<String, SampleDTO> sampleDTOMap) {
		if (sampleDTOMap.size() != sampleUIDSet.size()) {
			List<String> samplesNotFound = new ArrayList<>();

			for (final String sample : sampleUIDSet) {
				if (!sampleDTOMap.containsKey(sample)) {
					samplesNotFound.add(sample);
				}
			}

			if (!samplesNotFound.isEmpty()) {
				throw new MiddlewareException(
					"Some of the data uploaded is not present in the system. Please verify your file again. Samples not found: "
						+ StringUtils.join(samplesNotFound, ","));
			}
		}
	}

	private void validateInput(final DatasetUploadDto datasetUploadDto) {
		final Integer numberOfRows = datasetUploadDto.getCharValues().length;
		final Integer numberOfColums = datasetUploadDto.getCharValues()[0].length;

		if (!(numberOfRows > 0 && numberOfColums > 0 && numberOfColums == datasetUploadDto.getMarkers().size()
			&& numberOfRows == datasetUploadDto
			.getSampleAccessions().size())) {
			throw new MiddlewareException("Invalid matrix size");
		}
	}

	public DatasetDAO getDatasetDAO() {
		return this.datasetDAO;
	}

	public void setDatasetDAO(final DatasetDAO datasetDAO) {
		this.datasetDAO = datasetDAO;
	}

	public MarkerDAO getMarkerDAO() {
		return this.markerDAO;
	}

	public void setMarkerDAO(final MarkerDAO markerDAO) {
		this.markerDAO = markerDAO;
	}

	public SampleService getSampleService() {
		return this.sampleService;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public CharValuesDAO getCharValuesDAO() {
		return this.charValuesDAO;
	}

	public void setCharValuesDAO(final CharValuesDAO charValuesDAO) {
		this.charValuesDAO = charValuesDAO;
	}
}
