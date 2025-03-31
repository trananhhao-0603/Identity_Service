package vn.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.demo.dto.request.PermissionRequest;
import vn.demo.dto.response.PermissionResponse;
import vn.demo.entity.Permission;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PermissionMapper {
	@SuppressWarnings("all")
	Permission toPermission(PermissionRequest request);

	PermissionResponse toPermissionResponse(Permission permission);

}
