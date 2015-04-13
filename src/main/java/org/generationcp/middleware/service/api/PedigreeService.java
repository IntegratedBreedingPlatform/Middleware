package org.generationcp.middleware.service.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.util.CrossExpansionRule;

public interface PedigreeService {
	/**
     * Gets the cross expansion.
     *
     * @param gid the gid
     * @param level the level
     * @return The cross expansion based on the given gid and level
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getCrossExpansion(Integer gid, CrossExpansionRule crossExpansionRule) throws MiddlewareQueryException;
    String getCrossExpansion(Integer gid, int level) throws MiddlewareQueryException;
	String getCrossExpansionCimmytWheat(int gid, int level, int type) throws MiddlewareQueryException;
}
