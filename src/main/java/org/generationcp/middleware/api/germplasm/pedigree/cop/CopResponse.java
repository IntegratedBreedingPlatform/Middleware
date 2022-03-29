package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class CopResponse {

	private Table<Integer, Integer, Double> matrix;
	private List<String[]> array;
	private Boolean hasFile;

	private double progress;

	public CopResponse(final double progress) {
		this.progress = progress;
	}

	public CopResponse(final Table<Integer, Integer, Double> matrix) {
		this.matrix = matrix;
		this.array = convertTableTo2DArray(matrix);
	}

	public CopResponse(final Boolean hasFile) {
		this.hasFile = hasFile;
	}

	static List<String[]> convertTableTo2DArray(final Table<Integer, Integer, Double> table) {
		final List<Integer> gids = new ArrayList<>(table.columnKeySet());
		final List<String[]> array = new ArrayList<>();

		final List<String> header = new ArrayList<>();
		header.add(EMPTY);
		header.addAll(gids.stream().map(Object::toString).collect(toList()));
		array.add(header.toArray(new String[] {}));

		/*
		 * show only one half of symmetric matrix:
		 * x x x x x x
		 *   x x x x x
		 *     x x x x
		 *       x x x
		 *         x x
		 *           x
		 */
		final Set<Pair<Integer, Integer>> added = new HashSet<>();

		for (final Integer gid1 : gids) {
			final List<String> row = new ArrayList<>();
			row.add(gid1.toString());
			for (final Integer gid2 : gids) {
				if (added.contains(Pair.of(gid1, gid2))
					|| added.contains(Pair.of(gid2, gid1))) {
					row.add(EMPTY);
					continue;
				}
				Double cop = table.get(gid1, gid2);
				if (cop == null) {
					cop = table.get(gid2, gid1);
				}
				final String value = cop != null ? String.valueOf(cop) : EMPTY;
				row.add(value);
				added.add(Pair.of(gid1, gid2));
			}
			array.add(row.toArray(new String[] {}));
		}

		return array;
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

	public Boolean getHasFile() {
		return hasFile;
	}
}
