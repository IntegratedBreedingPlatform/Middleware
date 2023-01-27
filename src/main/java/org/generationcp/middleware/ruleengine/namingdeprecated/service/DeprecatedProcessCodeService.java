
package org.generationcp.middleware.ruleengine.namingdeprecated.service;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;

import java.util.List;

@Deprecated
public interface DeprecatedProcessCodeService {

	List<String> applyProcessCode(String currentInput, String processCode, DeprecatedAdvancingSource source);
}
