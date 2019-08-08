package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;

public class SampleTestDataInitializer {

	public static Sample createSample(final SampleList sampleList, final Integer userId) {
		final Sample sample = new Sample();
		sample.setTakenBy(userId);
		sample.setCreatedBy(userId);
		sample.setSampleName("GID");
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey("SABCD");
		sample.setSampleList(sampleList);
		return sample;
	}
}
