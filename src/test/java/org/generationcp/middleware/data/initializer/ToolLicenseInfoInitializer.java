package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;


public class ToolLicenseInfoInitializer {

	public ToolLicenseInfo createToolLicenseInfo(final String toolName) {
		// note that the data is just for testing; data set is not the same as the real data
		final ToolLicenseInfo toolLicenseInfo = new ToolLicenseInfo();
		final Tool tool = new Tool(toolName, toolName, toolName + ".exe");
		tool.setToolId(1L);
		toolLicenseInfo.setTool(tool);
		toolLicenseInfo.setLicensePath(toolName + ".lic");
		toolLicenseInfo.setLicenseHash("1234456degkdjkshdkdsdf");
		toolLicenseInfo.setExpirationDate(new Date());
		return toolLicenseInfo;
	}
}
