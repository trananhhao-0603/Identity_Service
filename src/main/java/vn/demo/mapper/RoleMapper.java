package vn.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.demo.dto.request.RoleRequest;
import vn.demo.dto.response.RoleResponse;
import vn.demo.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	@Mapping(target = "permissions", ignore = true)
	Role toRole(RoleRequest request);

	RoleResponse toRoleResponse(Role role);

}
