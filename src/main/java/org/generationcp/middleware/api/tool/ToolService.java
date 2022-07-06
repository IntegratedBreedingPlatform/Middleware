package org.generationcp.middleware.api.tool;

import org.generationcp.middleware.pojos.workbench.Tool;

public interface ToolService {


	/**
	 * Gets the tool with the given name.
	 *
	 * @param toolName - the tool name to match
	 * @return the tool with the given name
	 */
	Tool getToolWithName(String toolName);

}
