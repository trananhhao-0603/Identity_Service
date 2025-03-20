package vn.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.dto.response.UserResponse;
import vn.demo.entity.User;
import vn.demo.exception.AppException;
import vn.demo.exception.ErrorCode;
import vn.demo.mapper.UserMapper;
import vn.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserMapper userMapper;

	public User createUser(UserCreationRequest request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		User user = userMapper.toUser(request);

		// encode password with BCrypt
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		return userRepository.save(user);
	}

	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		userMapper.updateUser(user, request);
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
