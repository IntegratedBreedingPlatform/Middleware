package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableTypeList;

public class VariableTypeListTestDataInitializer {

	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String ENTRY_NO = "ENTRY_NO";
	private static final String PLOT_NO = "PLOT_NO";
	private static final String ASI = "ASI";
	private static final String LOCATION_ID = "LOCATION_ID";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String SITE_SOIL_PH = "SITE_SOIL_PH";
	public static final String N_FERT_NO = "NFert_NO";
	public static final String N_FERT_KG = "NFert_KG";

	public static VariableTypeList createMeansVariableTypesTestData() {
		final VariableTypeList meansVariableTypeList = new VariableTypeList();
		int rank = 0;
		meansVariableTypeList
				.add(new DMSVariableType(VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.TRIAL_INSTANCE, PhenotypicType.TRIAL_ENVIRONMENT),
						++rank));
		meansVariableTypeList.add(new DMSVariableType("ASI_MEAN", "ASI_MEAN",
				StandardVariableTestDataInitializer.createStandardVariableTestData("ASI_MEAN", PhenotypicType.VARIATE),
				++rank));
		return meansVariableTypeList;
	}

	public static  VariableTypeList createTreatmentFactorsVariableTypeList() {
		final VariableTypeList factors = new VariableTypeList();
		factors.add(DMSVariableTypeTestDataInitializer.createDmsVariableType(VariableTypeListTestDataInitializer.N_FERT_NO, VariableTypeListTestDataInitializer.N_FERT_KG));
		factors.add(DMSVariableTypeTestDataInitializer.createDmsVariableType(VariableTypeListTestDataInitializer.N_FERT_KG, VariableTypeListTestDataInitializer.N_FERT_KG));
		return  factors;
	}

	public static VariableTypeList createPlotVariableTypesTestData() {
		final VariableTypeList plotVariableTypeList = new VariableTypeList();
		int rank = 0;
		plotVariableTypeList
				.add(new DMSVariableType(VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.TRIAL_INSTANCE, PhenotypicType.TRIAL_ENVIRONMENT),
						++rank));
		plotVariableTypeList.add(new DMSVariableType(VariableTypeListTestDataInitializer.ENTRY_NO,
				VariableTypeListTestDataInitializer.ENTRY_NO,
				StandardVariableTestDataInitializer.createStandardVariableTestData(
						VariableTypeListTestDataInitializer.ENTRY_NO, PhenotypicType.GERMPLASM),
				++rank));
		plotVariableTypeList.add(new DMSVariableType(VariableTypeListTestDataInitializer.PLOT_NO,
				VariableTypeListTestDataInitializer.PLOT_NO,
				StandardVariableTestDataInitializer.createStandardVariableTestData(
						VariableTypeListTestDataInitializer.PLOT_NO, PhenotypicType.TRIAL_DESIGN),
				++rank));
		plotVariableTypeList.add(
				new DMSVariableType(VariableTypeListTestDataInitializer.ASI, VariableTypeListTestDataInitializer.ASI,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.ASI, PhenotypicType.VARIATE),
						++rank));
		return plotVariableTypeList;
	}

	public static VariableTypeList createStudyVariableTypesTestData() {
		final VariableTypeList variableTypeList = new VariableTypeList();
		int rank = 0;
		variableTypeList
				.add(new DMSVariableType(VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						VariableTypeListTestDataInitializer.TRIAL_INSTANCE,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.TRIAL_INSTANCE, PhenotypicType.TRIAL_ENVIRONMENT),
						++rank));
		variableTypeList
				.add(new DMSVariableType(VariableTypeListTestDataInitializer.LOCATION_ID,
						VariableTypeListTestDataInitializer.LOCATION_ID,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.LOCATION_ID, PhenotypicType.TRIAL_ENVIRONMENT),
						++rank));
		variableTypeList
				.add(new DMSVariableType(VariableTypeListTestDataInitializer.LOCATION_NAME,
						VariableTypeListTestDataInitializer.LOCATION_NAME,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								VariableTypeListTestDataInitializer.LOCATION_NAME, PhenotypicType.TRIAL_ENVIRONMENT),
						++rank));
		variableTypeList.add(new DMSVariableType(VariableTypeListTestDataInitializer.SITE_SOIL_PH,
				VariableTypeListTestDataInitializer.SITE_SOIL_PH,
				StandardVariableTestDataInitializer.createStandardVariableTestData(
						VariableTypeListTestDataInitializer.SITE_SOIL_PH, PhenotypicType.VARIATE),
				++rank));
		return variableTypeList;
	}
}
