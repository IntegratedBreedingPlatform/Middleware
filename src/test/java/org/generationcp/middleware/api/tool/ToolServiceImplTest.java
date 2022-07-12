package org.generationcp.middleware.api.tool;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ToolServiceImplTest extends IntegrationTestBase {

	@Autowired
	private ToolService toolService;

	@Test
	public void testGetToolWithName() {
		final String toolName = "fieldbook_web";
		final Tool tool = this.toolService.getToolWithName(toolName);
		Assert.assertNotNull(tool);
	}

}
