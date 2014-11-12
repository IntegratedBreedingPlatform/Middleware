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
			stdVar = createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(),
					"", "");
			variable = new VariableType(TRIAL_INSTANCE_STORAGE, TRIAL_INSTANCE_STORAGE, stdVar, 1);
			list.add(variable);
		}
		
		stdVar = createStandardVariable(TermId.ENTRY_NO.getId(), ENTRY_NO, TermId.ENTRY_NUMBER_STORAGE.getId(),
				"", "");
		variable = new VariableType(ENTRY_NO, ENTRY_NO, stdVar, 2);
		list.add(variable);
		
		stdVar = createStandardVariable(TermId.DESIG.getId(), DESIGNATION, TermId.ENTRY_DESIGNATION_STORAGE.getId(),
				"", "");
		variable = new VariableType(DESIGNATION, DESIGNATION, stdVar, 3);
		list.add(variable);
		
		stdVar = createStandardVariable(TermId.GID.getId(), GID, TermId.ENTRY_GID_STORAGE.getId(),
				"", "");
		variable = new VariableType(GID, GID, stdVar, 4);
		list.add(variable);
		
		stdVar = createStandardVariable(TermId.PLOT_NO.getId(), PLOT_NO, TermId.TRIAL_DESIGN_INFO_STORAGE.getId(),
				"", "");
		variable = new VariableType(PLOT_NO, PLOT_NO, stdVar, 5);
		list.add(variable);
		
		stdVar = createStandardVariable(ASPERGILLUS_FLAVUS_PPB_ID, ASPERGILLUS_FLAVUS_PPB, TermId.OBSERVATION_VARIATE.getId(),
				"", "");
		variable = new VariableType(ASPERGILLUS_FLAVUS_PPB, ASPERGILLUS_FLAVUS_PPB, stdVar, 6);
		list.add(variable);
		
		return list;
	}
	
	private static StandardVariable createStandardVariable(int id, String name, int storedInId, 
			String storedInName, String storedInDef) {
		//set only id, name and stored in for now
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
		
		stdVar = createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), TRIAL_INSTANCE, TermId.TRIAL_INSTANCE_STORAGE.getId(),
				"", "");
		variable = new VariableType(TRIAL_INSTANCE, TRIAL_INSTANCE, stdVar, 1);
		list.add(variable);
		
		if (hasTrialEnvironmentAndConstants) {
			stdVar = createStandardVariable(-1, ALTITUDE, TermId.ALTITUDE_STORAGE.getId(),
					"", "");
			variable = new VariableType(ALTITUDE, ALTITUDE, stdVar, 2);
			list.add(variable);
			
			stdVar = createStandardVariable(-2, LATITUDE, TermId.LATITUDE_STORAGE.getId(),
					"", "");
			variable = new VariableType(LATITUDE, LATITUDE, stdVar, 3);
			list.add(variable);
			
			stdVar = createStandardVariable(TermId.LOCATION_ID.getId(), LOCATION_NAME_ID, TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(),
					"", "");
			variable = new VariableType(LOCATION_NAME_ID, LOCATION_NAME_ID, stdVar, 4);
			list.add(variable);
			
			stdVar = createStandardVariable(TermId.TRIAL_LOCATION.getId(), LOCATION_NAME, TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(),
					"", "");
			variable = new VariableType(LOCATION_NAME, LOCATION_NAME, stdVar, 5);
			list.add(variable);
			
			stdVar = createStandardVariable(20308, ASI, TermId.OBSERVATION_VARIATE.getId(),
					"", "");
			variable = new VariableType(ASI, ASI, stdVar, 6);
			list.add(variable);
		}
		
		return list;
	}
}
