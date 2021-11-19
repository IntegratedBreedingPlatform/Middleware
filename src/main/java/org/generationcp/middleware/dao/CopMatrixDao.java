package org.generationcp.middleware.dao;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.generationcp.middleware.pojos.CopMatrix;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;

public class CopMatrixDao {

	private Session session;

	public CopMatrixDao(final Session session) {
		this.session = session;
	}

	public Table<Integer, Integer, Double> getByGids(Set<Integer> gids) {
		//noinspection unchecked
		final List<CopMatrix> results = this.session.createCriteria(CopMatrix.class)
			.add(Restrictions.conjunction()
				.add(Restrictions.in("gid1", gids))
				.add(Restrictions.in("gid2", gids)
				))
			.list();

		final Table<Integer, Integer, Double> matrix = HashBasedTable.create();
		for (final CopMatrix result : results) {
			matrix.put(
				result.getGermplasm1().getGid(),
				result.getGermplasm2().getGid(),
				result.getCop()
			);
		}
		return matrix;
	}

	public void save(final CopMatrix matrix) {
		this.session.save(matrix);
	}
}
