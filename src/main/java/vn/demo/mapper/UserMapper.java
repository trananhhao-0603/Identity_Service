package vn.demo.mapper;

import org.mapstruct.Mapper;

import vn.demo.dto.request.UserCreationRequest;
import vn.demo.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toUser(UserCreationRequest request);
}
