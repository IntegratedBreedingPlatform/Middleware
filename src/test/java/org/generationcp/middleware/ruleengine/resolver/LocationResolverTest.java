
package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

public class LocationResolverTest {

	final Map<String, String> locationIdNameMap = new HashMap<>();

	@Before
	public void init() {
		locationIdNameMap.put("1001", "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)");
	}

	@Test
	public void testResolveForNurseryWithLocationVariableAndValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("MEX");

		workbook.setConditions(Lists.newArrayList(locationMV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in Nursery settings.", "MEX",
				location);
	}

	@Test
	public void testResolveForNurseryWithLocationVariableButNoValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());

		workbook.setConditions(Lists.newArrayList(locationMV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals("Location should be defaulted to an empty string when LOCATION_ABBR variable is present but has no value.", "",
				location);
	}

	@Test
	public void testLocationForNurseryWithoutLocationVariable() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals("Location should be defaulted to an empty string when LOCATION_ABBR variable is not present.", "", location);
	}

	@Test
	public void testResolveForNurseryWithLocationAbbreviationAndLocationName() {
		final String locationAbbr = "MEX";
		final String nurseryLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		instance1LocationAbbrMV.setValue(locationAbbr);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		instance1LocationNameMV.setValue(nurseryLocation);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1LocationAbbrMV, instance1LocationNameMV, instance1MV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in nursery settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForNurseryWithoutLocationVariable() {
		final String trialInstance = "1";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1MV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals(
				"Location should be defaulted to TRIAL_INSTANCE when LOCATION_ABBR and LOCATION_NAME variables are not present in nursery settings.",
				trialInstance, location);
	}

	@Test
	public void testResolveForNurseryWithLocationName() {
		final String nurseryLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		instance1LocationNameMV.setValue(nurseryLocation);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1LocationNameMV, instance1MV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location = new LocationResolver(conditions, null, locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_NAME variable value in nursery settings.",
				nurseryLocation,
				location);
	}

	@Test
	public void testResolveForStudyWithLocationVariableAndValue() {
		final String locationAbbr = "MEX";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		final MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue(locationAbbr);
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in environment level settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForStudyWithLocationVariableButNoValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		final MeasurementData instance1LocationAbbrMD = new MeasurementData();
		// No value
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		final List<MeasurementVariable> conditions = workbook.getConditions();
		final String location =
			new LocationResolver(conditions, new ArrayList<>(), locationIdNameMap).resolve();
		Assert.assertEquals(
				"Location should be defaulted to an empty string when LOCATION_ABBR variable is present in environment level settings, but has no value.",
				"",
				location);
	}

	@Test
	public void testResolveForStudyWithLocationAbbreviationAndLocationName() {
		final String locationAbbr = "MEX";
		final String studyLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		final MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue(locationAbbr);
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		final MeasurementData instance1LocationNameMD = new MeasurementData();
		instance1LocationNameMD.setValue(studyLocation);
		instance1LocationNameMD.setMeasurementVariable(instance1LocationNameMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in environment level settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForStudyWithoutLocationVariable() {
		final String trialInstance = "1";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue(trialInstance);
		instance1MD.setMeasurementVariable(instance1MV);
		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));
		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals(
				"Location should be defaulted to TRIAL_INSTANCE when LOCATION_ABBR and LOCATION_NAME variables are not present in environment level settings.",
				trialInstance, location);
	}

	@Test
	public void testResolveForStudyWithLocationName() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.LOCATION_ID.getId());
		final MeasurementData instance1LocationNameMD = new MeasurementData();
		instance1LocationNameMD.setValue("1001");
		instance1LocationNameMD.setMeasurementVariable(instance1LocationNameMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationNameMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_NAME variable value in environment level settings.",
				"INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)",
				location);
	}

	@Test
	public void testResolveForStudyWithLocationNameFromSettings() {
		final String studyLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		instance1LocationNameMV.setValue(studyLocation);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD));
		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		workbook.setConditions(Lists.newArrayList(instance1LocationNameMV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_NAME variable value in Study settings.",
				studyLocation,
				location);
	}

	@Test
	public void testResolveForStudyWithLocationNameFromEnvironmentAndLocationAbbrFromSettings() {
		final String studyLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";
		final String locationAbbr = "MEX";

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue(locationAbbr);

		final MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		final MeasurementData instance1LocationNameMD = new MeasurementData();
		instance1LocationNameMD.setValue(studyLocation);
		instance1LocationNameMD.setMeasurementVariable(instance1LocationNameMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationNameMD));
		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		workbook.setConditions(Lists.newArrayList(locationMV));

		final List<MeasurementVariable> conditions = workbook.getConditions();

		final String location =
			new LocationResolver(conditions, fromMeasurementRow(instance1Measurements).getVariables().values(), locationIdNameMap).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in Study settings.",
				locationAbbr,
				location);
	}

}
