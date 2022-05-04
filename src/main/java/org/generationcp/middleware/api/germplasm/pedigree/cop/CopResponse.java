package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class CopResponse {

	private Table<Integer, Integer, Double> matrix;

	/**
	 * <pre>
	 *       1 2 3 4 5 6
	 * gid 1 x x x x x x
	 * gid 2   x x x x x
	 * gid 3     x x x x
	 * gid 4       x x x
	 * gid 5         x x
	 * gid 6           x
	 * </pre>
	 */
	private List<String[]> upperTriangularMatrix;

	/**
	 * <pre>
	 * gid1 gid2 cop
	 * 1    2    0.5
	 * 1    3    0.7
	 * </pre>
	 */
	private List<String[]> array2d;

	private Map<String, Map<Integer, String>> germplasmCommonNamesMap;

	private Boolean hasFile;

	private double progress;

	public CopResponse(final double progress) {
		this.progress = progress;
	}

	public CopResponse(final Table<Integer, Integer, Double> matrix) {
		this.matrix = matrix;
		this.upperTriangularMatrix = convertTableToUpperTriangularMatrix(matrix);
		this.array2d = convertTableTo2dArray(matrix);
	}

	public CopResponse(final Table<Integer, Integer, Double> matrix, Map<String, Map<Integer, String>> germplasmCommonNamesMap) {
		this(matrix);
		this.germplasmCommonNamesMap = germplasmCommonNamesMap;
	}

	public CopResponse(final Boolean hasFile) {
		this.hasFile = hasFile;
	}

	static List<String[]> convertTableToUpperTriangularMatrix(final Table<Integer, Integer, Double> table) {
		final List<Integer> gids = new ArrayList<>(table.columnKeySet());
		final List<String[]> array = new ArrayList<>();

		final List<String> header = new ArrayList<>();
		header.add(EMPTY);
		header.addAll(gids.stream().map(Object::toString).collect(toList()));
		array.add(header.toArray(new String[] {}));

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

	private static List<String[]> convertTableTo2dArray(final Table<Integer, Integer, Double> matrix) {
		final List<String[]> array = new ArrayList<>();

		final List<String> header = new ArrayList<>(Lists.newArrayList("gid1", "gid2", "cop"));
		array.add(header.toArray(new String[] {}));
		for (Map.Entry<Integer, Map<Integer, Double>> rowMap : matrix.rowMap().entrySet()) {
			for (Map.Entry<Integer, Double> colMap : rowMap.getValue().entrySet()) {
				array.add(new String[]{
					rowMap.getKey().toString(),
					colMap.getKey().toString(),
					colMap.getValue().toString()
				});
			}
		}

		return array;
	}

	public Table<Integer, Integer, Double> getMatrix() {
		return matrix;
	}

	public double getProgress() {
		return progress;
	}

	public List<String[]> getUpperTriangularMatrix() {
		return upperTriangularMatrix;
	}

	public Boolean getHasFile() {
		return hasFile;
	}

	public List<String[]> getArray2d() {
		return array2d;
	}

	public Map<String, Map<Integer, String>> getGermplasmCommonNamesMap() {
		return germplasmCommonNamesMap;
	}
}
