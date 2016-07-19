
package org.generationcp.middleware;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;

public class GermplasmTestDataGenerator {

	GermplasmDataManager germplasmDataManager;

	public GermplasmTestDataGenerator(final GermplasmDataManager manager) {
		this.germplasmDataManager = manager;
	}

	public Integer[] createGermplasmRecords(final int numberOfGermplasm, final String prefix) throws MiddlewareQueryException {
		final Integer[] gids = new Integer[numberOfGermplasm];
		for (int i = 0; i < numberOfGermplasm; i++) {
			gids[i] = this.createGermplasm(prefix + i);
		}
		return gids;
	}

	private Integer createGermplasm(final String germplasmName) throws MiddlewareQueryException {

		final Germplasm g = new Germplasm();
		g.setGdate(Integer.valueOf(20141014));
		g.setGnpgs(Integer.valueOf(0));
		g.setGpid1(Integer.valueOf(0));
		g.setGpid2(Integer.valueOf(0));
		g.setLgid(Integer.valueOf(0));
		g.setGrplce(Integer.valueOf(0));
		g.setLocationId(Integer.valueOf(1));
		g.setMethodId(Integer.valueOf(1));
		g.setMgid(Integer.valueOf(1));
		g.setUserId(Integer.valueOf(1));
		g.setReferenceId(Integer.valueOf(1));
		g.setLgid(Integer.valueOf(1));

		final Name n = new Name();
		n.setLocationId(Integer.valueOf(1));
		n.setNdate(Integer.valueOf(20141014));
		n.setNval(germplasmName);
		n.setReferenceId(Integer.valueOf(1));
		n.setTypeId(Integer.valueOf(1));
		n.setUserId(Integer.valueOf(1));
		n.setNstat(1);

		this.germplasmDataManager.addGermplasm(g, n);

		return g.getGid();
	}

}
