package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.util.List;
import java.util.Map;

public interface UserService {

	Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds);

	List<UserDto> getUsersByPersonIds(final List<Integer> personIds);

	WorkbenchUser getUserByFullname(String fullname);
}
