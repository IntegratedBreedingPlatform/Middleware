package org.generationcp.middleware.dao;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import org.generationcp.middleware.pojos.CopMatrix;
import org.hibernate.Session;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

public class CopMatrixDao {

	private Session session;

	public CopMatrixDao(final Session session) {
		this.session = session;
	}

	public Table<Integer, Integer, Double> getByGids(Set<Integer> gids) {
		//noinspection unchecked
		final List<CopMatrix> results = this.session.createSQLQuery("select * from cop_matrix as c "
			+ " where c.gid1 in (:gids) and c.gid2 in (:gids) "
			+ " order by field(gid1, :gids), field(gid2, :gids) ")
			.addEntity(CopMatrix.class)
			.setParameterList("gids", gids)
			.list();

		final Set<Integer> rows = results.stream().map(CopMatrix::getGid1).collect(toCollection(LinkedHashSet::new));
		final Set<Integer> cols = results.stream().map(CopMatrix::getGid2).collect(toCollection(LinkedHashSet::new));
		final Table<Integer, Integer, Double> matrix = ArrayTable.create(rows, cols);
		for (final CopMatrix result : results) {
			matrix.put(
				result.getGermplasm1().getGid(),
				result.getGermplasm2().getGid(),
				result.getCop()
			);
		}
		return matrix;
	}

	public void saveOrUpdate(final CopMatrix matrix) {
		this.session.saveOrUpdate(matrix);
	}
}
