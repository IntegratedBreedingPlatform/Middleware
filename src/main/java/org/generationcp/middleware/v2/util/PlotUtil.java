package org.generationcp.middleware.v2.util;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.v2.domain.TermId;

public class PlotUtil {

	private static List<TermId> plotTypes = null;
	
	public static List<TermId> getAllPlotTypes() {
		if (plotTypes == null) {
			 plotTypes = new ArrayList<TermId>();
			 plotTypes.add(TermId.PLOT_EXPERIMENT);
			 plotTypes.add(TermId.SAMPLE_EXPERIMENT);
			 plotTypes.add(TermId.AVERAGE_EXPERIMENT);
			 plotTypes.add(TermId.SUMMARY_EXPERIMENT);
		}
		return plotTypes;
	}

	public static String getSqlTypeIds() {
		String sql = new String();
		sql += "(";
		sql += getAllPlotTypes().get(0).getId();
		for (int i = 1; i < getAllPlotTypes().size(); i++) {
			sql += "," + getAllPlotTypes().get(i).getId();
		}
		sql += ") ";
		return sql;
	}
}
