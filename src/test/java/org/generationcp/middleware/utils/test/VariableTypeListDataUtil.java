
package org.generationcp.middleware.utils.test;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;

public class VariableTypeListDataUtil {

	private static final int ASPERGILLUS_FLAVUS_PPB_ID = 20369;
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String ASI = "ASI";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String LOCATION_NAME_ID = "LOCATION_NAME_ID";
	private static final String LATITUDE = "latitude";
	private static final String ALTITUDE = "altitude";
	private static final String ASPERGILLUS_FLAVUS_PPB = "Aspergillus_flavusPPB";
	private static final String PLOT_NO = "PLOT_NO";
	private static final String GID = "GID";
	private static final String DESIGNATION = "DESIGNATION";
	private static final String ENTRY_NO = "ENTRY_NO";
	private static final String TRIAL_INSTANCE_STORAGE = "TRIAL_INSTANCE_STORAGE";

	public static VariableTypeList createPlotVariableTypeList(final boolean isAddTrialInstanceStorage) {
		final VariableTypeList list = new VariableTypeList();
		DMSVariableType variable;
		StandardVariable stdVar;
		if (isAddTrialInstanceStorage) {
			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(),
							VariableTypeListDataUtil.TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.TRIAL_INSTANCE_STORAGE, VariableTypeListDataUtil.TRIAL_INSTANCE_STORAGE,
							stdVar, 1);
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);
		}

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.ENTRY_NO.getId(), VariableTypeListDataUtil.ENTRY_NO,
						TermId.ENTRY_NUMBER_STORAGE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.ENTRY_NO, VariableTypeListDataUtil.ENTRY_NO, stdVar, 2);
		variable.setRole(PhenotypicType.GERMPLASM);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.DESIG.getId(), VariableTypeListDataUtil.DESIGNATION,
						TermId.ENTRY_DESIGNATION_STORAGE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.DESIGNATION, VariableTypeListDataUtil.DESIGNATION, stdVar, 3);
		variable.setRole(PhenotypicType.GERMPLASM);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.GID.getId(), VariableTypeListDataUtil.GID,
						TermId.ENTRY_GID_STORAGE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.GID, VariableTypeListDataUtil.GID, stdVar, 4);
		variable.setRole(PhenotypicType.GERMPLASM);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.PLOT_NO.getId(), VariableTypeListDataUtil.PLOT_NO,
						TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.PLOT_NO, VariableTypeListDataUtil.PLOT_NO, stdVar, 5);
		variable.setRole(PhenotypicType.TRIAL_DESIGN);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB_ID,
						VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB, TermId.OBSERVATION_VARIATE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB, VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB,
				stdVar,
						6);
		variable.setRole(PhenotypicType.VARIATE);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		return list;
	}

	private static StandardVariable createStandardVariable(final int id, final String name, final int storedInId, final String storedInName, final String storedInDef) {
		// set only id, name and stored in for now
		final StandardVariable stdVar = new StandardVariable();
		stdVar.setId(id);
		stdVar.setName(name);
		return stdVar;
	}

	public static VariableTypeList createVariableTypeList(final boolean hasEnvironmentAndConstants) {
		final VariableTypeList list = new VariableTypeList();
		DMSVariableType variable;
		StandardVariable stdVar;

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(),
						VariableTypeListDataUtil.TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
		variable = new DMSVariableType(VariableTypeListDataUtil.TRIAL_INSTANCE, VariableTypeListDataUtil.TRIAL_INSTANCE, stdVar, 1);
		variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
		stdVar.setPhenotypicType(variable.getRole());
		list.add(variable);

		if (hasEnvironmentAndConstants) {
			stdVar =
					VariableTypeListDataUtil.createStandardVariable(-1, VariableTypeListDataUtil.ALTITUDE, TermId.ALTITUDE_STORAGE.getId(),
							"", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.ALTITUDE, VariableTypeListDataUtil.ALTITUDE, stdVar, 2);
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(-2, VariableTypeListDataUtil.LATITUDE, TermId.LATITUDE_STORAGE.getId(),
							"", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.LATITUDE, VariableTypeListDataUtil.LATITUDE, stdVar, 3);
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.LOCATION_ID.getId(), VariableTypeListDataUtil.LOCATION_NAME_ID,
							TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.LOCATION_NAME_ID, VariableTypeListDataUtil.LOCATION_NAME_ID, stdVar, 4);
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_LOCATION.getId(), VariableTypeListDataUtil.LOCATION_NAME,
							TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.LOCATION_NAME, VariableTypeListDataUtil.LOCATION_NAME, stdVar, 5);
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(20308, VariableTypeListDataUtil.ASI,
							TermId.OBSERVATION_VARIATE.getId(), "", "");
			variable = new DMSVariableType(VariableTypeListDataUtil.ASI, VariableTypeListDataUtil.ASI, stdVar, 6);
			variable.setRole(PhenotypicType.VARIATE);
			stdVar.setPhenotypicType(variable.getRole());
			list.add(variable);
		}

		return list;
	}
}
