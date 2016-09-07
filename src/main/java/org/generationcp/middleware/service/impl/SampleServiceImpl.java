
package org.generationcp.middleware.service.impl;

import java.util.Date;

import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.Sample;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SampleServiceImpl implements SampleService {

	private SampleDao sampleDao;
	private ExperimentDao experimentDao;

	public SampleServiceImpl() {

	}

	public SampleServiceImpl(HibernateSessionProvider sessionProvider) {
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(sessionProvider.getSession());
	}

	public SampleServiceImpl(SampleDao sampleDao, ExperimentDao experimentDao) {
		this.sampleDao = sampleDao;
		this.experimentDao = experimentDao;
	}

	@Override
	public String createSample(Sample sample) {

		org.generationcp.middleware.pojos.Sample sampleBean = new org.generationcp.middleware.pojos.Sample();
		String sampleId = "SampleID-" + new Date().getTime();
		sampleBean.setSampleId(sampleId);

		final ExperimentModel plot = this.experimentDao.getById(sample.getPlotId());
		if (plot != null) {
			sampleBean.setPlot(plot);
		} else {
			throw new IllegalArgumentException(
					"Could not locate plot based on the plot id [" + sample.getPlotId()
							+ "] supplied for the sample. Please provide a valid id of the plot the sample was taken from.");
		}
		sampleBean.setPlantId(sample.getPlantId());
		sampleBean.setTakenBy(sample.getTakenBy());
		sampleBean.setSampleDate(sample.getSampleDate());
		sampleBean.setNotes(sample.getNotes());

		this.sampleDao.save(sampleBean);

		return sampleId;
	}

	@Override
	public Sample getSample(String sampleId) {
		final org.generationcp.middleware.pojos.Sample sampleBean = this.sampleDao.getBySampleId(sampleId);

		Sample sample = new Sample();
		sample.setSampleId(sampleBean.getSampleId());
		sample.setPlantId(sampleBean.getPlantId());
		sample.setTakenBy(sampleBean.getTakenBy());
		sample.setSampleDate(sampleBean.getSampleDate());
		sample.setNotes(sampleBean.getNotes());

		// Plot Metadata
		final ExperimentModel plot = sampleBean.getPlot();
		sample.setPlotId(plot.getNdExperimentId());

		for (ProjectRelationship projRel : plot.getProject().getRelatedTos()) {
			if (projRel.getTypeId().equals(1150)) {
				sample.setStudyId(projRel.getObjectProject().getProjectId());
				sample.setStudyName(projRel.getObjectProject().getName());
			}
		}

		for (ExperimentProperty expProp : plot.getProperties()) {
			if (expProp.getTypeId().equals(TermId.PLOT_NO.getId())) {
				sample.setPlotNumber(Integer.valueOf(expProp.getValue()));
			}
		}

		// nd_geolocation.description is the trial instance number - we use that as location id for now.
		sample.setLocationId(Integer.valueOf(plot.getGeoLocation().getDescription()));

		for (GeolocationProperty geoPorp : plot.getGeoLocation().getProperties()) {
			if (geoPorp.getTypeId().equals(20360)) {
				sample.setPlantingDate(geoPorp.getValue());
			} else if (geoPorp.getTypeId().equals(20363)) {
				sample.setHarvestDate(geoPorp.getValue());
			} else if (geoPorp.getTypeId().equals(8189)) {
				sample.setLocationName(geoPorp.getValue());
			} else if (geoPorp.getTypeId().equals(8370)) {
				sample.setSeason(geoPorp.getValue());
			}
		}

		for (ExperimentStock expStock : plot.getExperimentStocks()) {
			StockModel stock = expStock.getStock();
			sample.setGermplasmId(stock.getDbxrefId());
			sample.setEntryNumber(Integer.valueOf(stock.getUniqueName()));
		}

		// // TODO in middleware query
		// sample.setYear(mwSample.getYear());
		//
		// sample.setFieldId(mwSample.getFieldId());
		// sample.setFieldName(mwSample.getFieldName());
		//
		// sample.setSeedSource(mwSample.getSeedSource());
		// sample.setPedigree(mwSample.getPedigree());

		return sample;
	}

}
