package vn.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.dto.response.UserResponse;
import vn.demo.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toUser(UserCreationRequest request);

	UserResponse toUserResponse(User user);

	void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
