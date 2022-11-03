
package org.generationcp.middleware.ruleengine.naming.service;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;

import java.util.List;

public interface ProcessCodeService {

	List<String> applyProcessCode(String currentInput, String processCode, DeprecatedAdvancingSource source);
}
