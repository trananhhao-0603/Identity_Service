package vn.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.dto.response.UserResponse;
import vn.demo.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
	@SuppressWarnings("all")
	User toUser(UserCreationRequest request);

	UserResponse toUserResponse(User user);

	@Mapping(target = "roles", ignore = true)
	void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
