
package org.generationcp.middleware.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrialReporter extends AbstractNurseryReporter {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> buildJRParams(final Map<String, Object> args) {
		final Map<String, Object> params = super.buildJRParams(args);

		final List<MeasurementRow> entries;

		entries = ((List<MeasurementRow>) args.get(DATA_SOURCE_KEY));

		// entries in trials are ordered by plot number instead of entry number
		final int firstPlot = Integer.valueOf(entries.get(0).getMeasurementData(TermId.PLOT_NO.getId()).getValue());
		final int lastPlot = Integer.valueOf(entries.get(entries.size() - 1).getMeasurementData(TermId.PLOT_NO.getId()).getValue());

		final Pair<Integer, Integer> firstLastEntryNumber = getFirstLastEntry(entries);

		final int offset = firstLastEntryNumber.getLeft() - 1;

		params.put("tid", args.get("studyId"));
		params.put("Iplot", firstPlot);
		params.put("Fplot", lastPlot);
		params.put("Ientry", firstLastEntryNumber.getLeft());
		params.put("Fentry", firstLastEntryNumber.getRight());
		params.put("offset", offset);

		return params;
	}

	private Pair<Integer, Integer> getFirstLastEntry(final Collection<MeasurementRow> rows) {
		Integer highest = Integer.MIN_VALUE;
		Integer lowest = Integer.MAX_VALUE;

		for (final MeasurementRow row : rows) {
			final Integer entryNo = Integer.parseInt(row.getMeasurementData(TermId.ENTRY_NO.getId()).getValue());
			if (entryNo.compareTo(highest) == 1) {
				highest = entryNo;
			} else if (entryNo.compareTo(lowest) == -1) {
				lowest = entryNo;
			}
		}

		return new ImmutablePair<>(lowest, highest);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JRDataSource buildJRDataSource(final Collection<?> args) {

		final List<GermplasmEntry> entries = new ArrayList<>();
		// this null record is added because in Jasper, the record pointer in the data source is incremented by every element that receives
		// it.
		// since the datasource used in entry, is previously passed from occ to entry subreport.
		entries.add(null);

		for (final MeasurementRow row : (Collection<MeasurementRow>) args) {
			final GermplasmEntry entry = new GermplasmEntry();
			for (final MeasurementData dataItem : row.getDataList()) {
				switch (dataItem.getLabel()) {
					case "ENTRY_NO":
						entry.setEntryNum(Integer.valueOf(dataItem.getValue()));
						break;
					case "CROSS":
						entry.setLinea1(dataItem.getValue());
						entry.setLinea2(dataItem.getValue());
						break;
					case "DESIGNATION":
						entry.setLinea3(dataItem.getValue());
						entry.setLinea4(dataItem.getValue());
						break;
					case "PLOT_NO":
						entry.setPlot(Integer.valueOf(dataItem.getValue()));
						break;
					case "REP_NO":
						entry.setReplicationNo(Integer.valueOf(dataItem.getValue()));
						break;
					case "BLOCK_NO":
						entry.setSubblock(Integer.valueOf(dataItem.getValue()));
						break;
				}
			}

			// TODO: pending mappings
			entry.setsEnt(0);
			entry.setsTabbr("");
			entry.setSlocycle("");

			entries.add(entry);
		}

		final JRDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(new Occurrence(entries)));
		return dataSource;

	}

}
