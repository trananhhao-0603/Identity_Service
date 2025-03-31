package vn.demo.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.dto.response.UserResponse;
import vn.demo.entity.User;
import vn.demo.enums.Role;
import vn.demo.exception.AppException;
import vn.demo.exception.ErrorCode;
import vn.demo.mapper.UserMapper;
import vn.demo.repository.RoleRepository;
import vn.demo.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

	UserRepository userRepository;
	RoleRepository roleRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;

	public UserResponse createUser(UserCreationRequest request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		User user = userMapper.toUser(request);

		// encode password with BCrypt
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		HashSet<String> roles = new HashSet<>();
		roles.add(Role.USER.name());
//		user.setRoles(roles);

		return userMapper.toUserResponse(userRepository.save(user));
	}

	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXISTED));
		return userMapper.toUserResponse(user);
	}

	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXISTED));

		userMapper.updateUser(user, request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		var roles = roleRepository.findAllById(request.getRoles());
		user.setRoles(new HashSet<>(roles));

		return userMapper.toUserResponse(userRepository.save(user));
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	public List<UserResponse> getUsers() {
		return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
	}

	public UserResponse getUser(String id) {
		return userMapper
				.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
	}

}
