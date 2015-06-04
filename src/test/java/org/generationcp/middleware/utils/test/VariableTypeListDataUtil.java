
package org.generationcp.middleware.utils.test;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
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

	public static VariableTypeList createPlotVariableTypeList(boolean isAddTrialInstanceStorage) {
		VariableTypeList list = new VariableTypeList();
		VariableType variable;
		StandardVariable stdVar;
		if (isAddTrialInstanceStorage) {
			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(),
							VariableTypeListDataUtil.TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
			variable =
					new VariableType(VariableTypeListDataUtil.TRIAL_INSTANCE_STORAGE, VariableTypeListDataUtil.TRIAL_INSTANCE_STORAGE,
							stdVar, 1);
			list.add(variable);
		}

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.ENTRY_NO.getId(), VariableTypeListDataUtil.ENTRY_NO,
						TermId.ENTRY_NUMBER_STORAGE.getId(), "", "");
		variable = new VariableType(VariableTypeListDataUtil.ENTRY_NO, VariableTypeListDataUtil.ENTRY_NO, stdVar, 2);
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.DESIG.getId(), VariableTypeListDataUtil.DESIGNATION,
						TermId.ENTRY_DESIGNATION_STORAGE.getId(), "", "");
		variable = new VariableType(VariableTypeListDataUtil.DESIGNATION, VariableTypeListDataUtil.DESIGNATION, stdVar, 3);
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.GID.getId(), VariableTypeListDataUtil.GID,
						TermId.ENTRY_GID_STORAGE.getId(), "", "");
		variable = new VariableType(VariableTypeListDataUtil.GID, VariableTypeListDataUtil.GID, stdVar, 4);
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.PLOT_NO.getId(), VariableTypeListDataUtil.PLOT_NO,
						TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", "");
		variable = new VariableType(VariableTypeListDataUtil.PLOT_NO, VariableTypeListDataUtil.PLOT_NO, stdVar, 5);
		list.add(variable);

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB_ID,
						VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB, TermId.OBSERVATION_VARIATE.getId(), "", "");
		variable =
				new VariableType(VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB, VariableTypeListDataUtil.ASPERGILLUS_FLAVUS_PPB, stdVar,
						6);
		list.add(variable);

		return list;
	}

	private static StandardVariable createStandardVariable(int id, String name, int storedInId, String storedInName, String storedInDef) {
		// set only id, name and stored in for now
		StandardVariable stdVar = new StandardVariable();
		stdVar.setId(id);
		stdVar.setName(name);
		stdVar.setStoredIn(new Term(storedInId, storedInName, storedInDef));
		return stdVar;
	}

	public static VariableTypeList createTrialVariableTypeList(boolean hasTrialEnvironmentAndConstants) {
		VariableTypeList list = new VariableTypeList();
		VariableType variable;
		StandardVariable stdVar;

		stdVar =
				VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(),
						VariableTypeListDataUtil.TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
		variable = new VariableType(VariableTypeListDataUtil.TRIAL_INSTANCE, VariableTypeListDataUtil.TRIAL_INSTANCE, stdVar, 1);
		list.add(variable);

		if (hasTrialEnvironmentAndConstants) {
			stdVar =
					VariableTypeListDataUtil.createStandardVariable(-1, VariableTypeListDataUtil.ALTITUDE, TermId.ALTITUDE_STORAGE.getId(),
							"", "");
			variable = new VariableType(VariableTypeListDataUtil.ALTITUDE, VariableTypeListDataUtil.ALTITUDE, stdVar, 2);
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(-2, VariableTypeListDataUtil.LATITUDE, TermId.LATITUDE_STORAGE.getId(),
							"", "");
			variable = new VariableType(VariableTypeListDataUtil.LATITUDE, VariableTypeListDataUtil.LATITUDE, stdVar, 3);
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.LOCATION_ID.getId(), VariableTypeListDataUtil.LOCATION_NAME_ID,
							TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
			variable = new VariableType(VariableTypeListDataUtil.LOCATION_NAME_ID, VariableTypeListDataUtil.LOCATION_NAME_ID, stdVar, 4);
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(TermId.TRIAL_LOCATION.getId(), VariableTypeListDataUtil.LOCATION_NAME,
							TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
			variable = new VariableType(VariableTypeListDataUtil.LOCATION_NAME, VariableTypeListDataUtil.LOCATION_NAME, stdVar, 5);
			list.add(variable);

			stdVar =
					VariableTypeListDataUtil.createStandardVariable(20308, VariableTypeListDataUtil.ASI,
							TermId.OBSERVATION_VARIATE.getId(), "", "");
			variable = new VariableType(VariableTypeListDataUtil.ASI, VariableTypeListDataUtil.ASI, stdVar, 6);
			list.add(variable);
		}

		return list;
	}
}
