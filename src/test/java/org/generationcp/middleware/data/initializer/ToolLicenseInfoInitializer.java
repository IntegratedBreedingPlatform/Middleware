
package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
import org.generationcp.middleware.pojos.workbench.ToolName;

public class ToolLicenseInfoInitializer {

	public static final String WB_INSTALLATION_DIR = "./BMS4/";
	public static final String BREEDING_VIEW_TOOL_NAME = ToolName.breeding_view.toString();
	public static final String BV_LICENSE_FILENAME = ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME + ".lic";

	public ToolLicenseInfo createToolLicenseInfo(final String toolName) {
		// note that the data is just for testing; data set is not the same as the real data
		final ToolLicenseInfo toolLicenseInfo = new ToolLicenseInfo();
		final Tool tool = new Tool(toolName, toolName, toolName + ".exe");
		tool.setToolId(1L);
		toolLicenseInfo.setTool(tool);
		toolLicenseInfo.setLicensePath(this.getInitialLicensePath(toolName));
		toolLicenseInfo.setLicenseHash("1234456degkdjkshdkdsdf");
		toolLicenseInfo.setExpirationDate(new Date());
		return toolLicenseInfo;
	}

	public String getInitialLicensePath(final String toolName) {
		return "INSTALLATION_PATH/tools/" + toolName + "/Bin/" + toolName + ".lic";
	}

	public String getLicensePathFromWBInstallationDir(final String toolName) {
		return ToolLicenseInfoInitializer.WB_INSTALLATION_DIR + "/tools/" + toolName + "/Bin/" + toolName + ".lic";
	}
}
