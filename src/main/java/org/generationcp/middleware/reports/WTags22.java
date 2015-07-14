
package org.generationcp.middleware.reports;

import java.util.List;

import org.generationcp.middleware.util.StringUtil;

public class WTags22 extends WTags04 {

	protected WTags22() {
	}

	@Override
	public Reporter createReporter() {
		Reporter r = new WTags22();
		r.setFileNameExpression("TAGS22_{trialName}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WTAG22";
	}

	@Override
	protected String buildRecord(List<String> row, List<String> headers, int rowSpan, int rowSize) {
		String study, occ, subProg, type, season, entry = null, plot = null, pedigreeA = null, pedigreeB = null, selHistA = null, selHistB =
				null;

		study = this.studyMeta.get("STUDY_NAME");
		occ = this.studyMeta.get("TRIAL_INSTANCE");
		subProg = this.studyMeta.get("BreedingProgram");
		type = this.studyMeta.get("STUDY_TYPE"); // a type for nal,int, etc
		season = this.studyMeta.get("CROP_SEASON");

		for (int i = 0; i < headers.size(); i++) {
			switch (headers.get(i)) {
				case "ENTRY_NO":
					entry = row.get(i);
					break;
				case "PLOT_NO":
					plot = row.get(i);
					break;
				case "CROSS":
					pedigreeA = row.get(i);
					pedigreeB = pedigreeA;
					pedigreeA =
							pedigreeA.length() > 40 ? pedigreeA.substring(0, pedigreeA.substring(0, 40).lastIndexOf("/") + 1) : pedigreeA;
					pedigreeB = pedigreeB.length() > 40 ? pedigreeB.substring(pedigreeB.lastIndexOf("/", 40) + 1, pedigreeB.length()) : "";
					break;
				case "DESIGNATION":
					selHistA = row.get(i);
					selHistB = selHistA;
					selHistA = selHistA.length() > 36 ? selHistA.substring(0, selHistA.substring(0, 36).lastIndexOf("-") + 1) : selHistA;
					selHistB = selHistB.length() > 36 ? selHistB.substring(selHistB.lastIndexOf("-", 36) + 1, selHistB.length()) : "";
					break;
			}
		}

		// now format
		StringBuilder sb = new StringBuilder();

		sb.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.format(study, 30, true)).append(" OCC: ")
				.append(StringUtil.format(occ, 4, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
				.append(StringUtil.format(subProg, 3, true)).append(" ").append(StringUtil.format(type, 5, true)).append(" ")
				.append(StringUtil.format(season, 6, true)).append(StringUtil.format("ENTRY", 6, true))
				.append(StringUtil.format(entry, 6, false)).append(" ").append(StringUtil.format("PLOT", 5, true))
				.append(StringUtil.format(plot, 6, false)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan - 15))
				.append(StringUtil.format("CIMMYT", 6, false)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
				.append(StringUtil.format(pedigreeA, rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
				.append(StringUtil.format(pedigreeB, rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
				.append(StringUtil.format("", 4, true)).append(StringUtil.format(selHistA, 36, true)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.format("", 4, true))
				.append(StringUtil.format(selHistB, 36, true)).append("\r\n\r\n");
		return sb.toString();
	}

}
