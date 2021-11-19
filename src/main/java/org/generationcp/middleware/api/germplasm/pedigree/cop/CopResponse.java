package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CopResponse {

	private Table<Integer, Integer, Double> matrix;
	private List<String[]> array;

	private double progress;

	public CopResponse(final double progress) {
		this.progress = progress;
	}

	public CopResponse(final Table<Integer, Integer, Double> matrix) {
		this.matrix = matrix;
		this.array = convertTableTo2DArray(matrix);
	}

	static List<String[]> convertTableTo2DArray(final Table<Integer, Integer, Double> results) {
		final List<String[]> rowValues = new ArrayList<>();

		final List<String> header = new ArrayList<>();
		header.add("");
		header.addAll(results.columnKeySet().stream().map(Object::toString).collect(toList()));
		rowValues.add(header.toArray(new String[] {}));

		int offset = 0;
		for (final Map.Entry<Integer, Map<Integer, Double>> rowEntrySet : results.rowMap().entrySet()) {
			final List<String> row = new ArrayList<>();
			row.add(rowEntrySet.getKey().toString());

			/*
			 * x x x x x x
			 *   x x x x x
			 *     x x x x
			 *       x x x
			 *         x x
			 *           x
			 */
			IntStream.range(0, offset).forEach(i -> row.add(""));
			offset++;

			row.addAll(rowEntrySet.getValue().values().stream().map(Object::toString).collect(toList()));
			rowValues.add(row.toArray(new String[] {}));
		}
		return rowValues;
	}

	public Table<Integer, Integer, Double> getMatrix() {
		return matrix;
	}

	public void setMatrix(final Table<Integer, Integer, Double> matrix) {
		this.matrix = matrix;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(final double progress) {
		this.progress = progress;
	}

	public List<String[]> getArray() {
		return array;
	}

	public void setArray(final List<String[]> array) {
		this.array = array;
	}
}
