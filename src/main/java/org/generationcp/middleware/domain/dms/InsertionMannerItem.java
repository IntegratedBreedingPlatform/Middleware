package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermSummary;

import java.util.ArrayList;
import java.util.List;


public class InsertionMannerItem {

	public static final TermSummary INSERT_EACH_IN_TURN = new TermSummary(8414,"1","Insert each check in turn");
	public static final TermSummary INSERT_ALL_CHECKS = new TermSummary(8415,"2","Insert all checks at each position");

	public static List<TermSummary> getInsertionManners() {
		final java.util.List<TermSummary> list = new ArrayList<>();
		list.add(INSERT_EACH_IN_TURN);
		list.add(INSERT_ALL_CHECKS);
		return list;
	}

}
