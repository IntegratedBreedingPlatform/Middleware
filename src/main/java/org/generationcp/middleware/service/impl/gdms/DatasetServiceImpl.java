package org.generationcp.middleware.service.impl.gdms;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.gdms.DatasetDto;
import org.generationcp.middleware.service.api.gdms.DatasetService;
import org.generationcp.middleware.service.impl.study.SampleServiceImpl;
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

	private DatasetDAO datasetDAO;

	private MarkerDAO markerDAO;

	private SampleService sampleService;

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		datasetDAO = new DatasetDAO();
		markerDAO = new MarkerDAO();
		this.datasetDAO.setSession(sessionProvider.getSession());
		this.markerDAO.setSession(sessionProvider.getSession());
		sampleService = new SampleServiceImpl(sessionProvider);
	}

	@Override
	public Integer saveDataset(final DatasetDto datasetDto) throws Exception {
		Preconditions.checkNotNull(datasetDto);
		Preconditions.checkNotNull(datasetDto.getMarkers());
		Preconditions.checkNotNull(datasetDto.getSampleAccesions());

		Preconditions.checkArgument(StringUtils.isNotEmpty(datasetDto.getName()), new Exception("Empty dataset name"));

		if (datasetDto.getName().length() > 30) {
			throw new Exception("Dataset Name value exceeds max char size");
		}
		if (datasetDAO.getByName(datasetDto.getName()) != null) {
			throw new Exception("Dataset Name already exists");
		}
		if (isDuplicatedMarkerNames(datasetDto)) {
			throw new Exception("Duplicated markers not allowed");
		}

		this.validateInput(datasetDto);

		final Set<String> sampleUIDSet = this.getSampleUIDList(datasetDto);
		final Map<String, SampleDTO> sampleDTOMap = sampleService.getSamplesBySampleUID(sampleUIDSet);

		if (sampleDTOMap.size() != sampleUIDSet.size()) {
			throw new Exception("Some of the data uploaded is not present in the system. Please verify your file again.");
		}

		final List<Marker> markers = this.markerDAO.getByNames(datasetDto.getMarkers(), 0, 0);
		final Map<String, Marker> markerMap = this.getMarkersMap(markers);
		this.validateMarkers(datasetDto, markerMap);

		final Dataset dataset = DatasetBuilder.build(datasetDto, sampleDTOMap, markerMap);

		try {
			return datasetDAO.save(dataset).getDatasetId();
		} catch (Exception e) {
			throw new Exception("An error has occurred while saving the dataset");
		}
	}

	private Boolean isDuplicatedMarkerNames(final DatasetDto datasetDto) {
		final Set<String> uniqueMarkers = new HashSet<>(datasetDto.getMarkers());
		if (uniqueMarkers.size() != datasetDto.getMarkers().size()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	private Set<String> getSampleUIDList(final DatasetDto datasetDto) {
		final Set<String> uniqueSamples = new HashSet<>();
		for (final DatasetDto.SampleKey key : datasetDto.getSampleAccesions()) {
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

	private void validateMarkers(final DatasetDto datasetDto, final Map<String, Marker> markerMap) throws Exception {

		if (markerMap.size() != datasetDto.getMarkers().size()) {
			List<String> markersNotFound = new ArrayList<>();

			for (final String marker : datasetDto.getMarkers()) {
				if (!markerMap.containsKey(marker)) {
					markersNotFound.add(marker);
				}
			}

			if (!markersNotFound.isEmpty()) {
				throw new Exception("Markers not found: " + StringUtils.join(markersNotFound, ","));
			}
		}
	}

	private void validateInput(final DatasetDto datasetDto) throws Exception{
		final Integer numberOfRows = datasetDto.getCharValues().length;
		final Integer numberOfColums = datasetDto.getCharValues()[0].length;

		if (!(numberOfRows > 0 && numberOfColums > 0 && numberOfColums == datasetDto.getMarkers().size() && numberOfRows == datasetDto
				.getSampleAccesions().size())){
			throw new Exception("Invalid matrix size");
		}
	}

	public DatasetDAO getDatasetDAO() {
		return datasetDAO;
	}

	public void setDatasetDAO(final DatasetDAO datasetDAO) {
		this.datasetDAO = datasetDAO;
	}

	public MarkerDAO getMarkerDAO() {
		return markerDAO;
	}

	public void setMarkerDAO(final MarkerDAO markerDAO) {
		this.markerDAO = markerDAO;
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}
}
