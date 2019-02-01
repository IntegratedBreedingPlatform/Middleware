package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;

public class SampleTestDataInitializer {

	public static Sample createSample(final SampleList sampleList, final User user) {
		final Sample sample = new Sample();
		sample.setTakenBy(user);
		sample.setCreatedBy(user);
		sample.setSampleName("GID");
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey("SABCD");
		sample.setSampleList(sampleList);
		return sample;
	}
}
