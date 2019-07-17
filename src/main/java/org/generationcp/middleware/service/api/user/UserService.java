package org.generationcp.middleware.service.api.user;

import java.util.List;
import java.util.Map;

public interface UserService {

	Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds);

}
