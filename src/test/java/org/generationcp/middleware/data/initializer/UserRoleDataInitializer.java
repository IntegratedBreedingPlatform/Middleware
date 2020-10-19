package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;

public class UserRoleDataInitializer {

	public static UserRole createUserRole(final RoleType roleTypeEnum) {
		final UserRole userRole = new UserRole();
		final Role role = new Role();
		final org.generationcp.middleware.pojos.workbench.RoleType roleType = new org.generationcp.middleware.pojos.workbench.RoleType();
		roleType.setId(roleTypeEnum.getId());
		roleType.setName(roleTypeEnum.name());
		role.setRoleType(roleType);
		userRole.setRole(role);
		final CropType cropType = new CropType();
		cropType.setCropName("maize");
		userRole.setCropType(cropType);
		return userRole;
	}
}
