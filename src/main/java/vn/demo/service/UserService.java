package vn.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.entity.User;
import vn.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	public User createUser(UserCreationRequest request) {
		User user = new User();

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new RuntimeException("Username exited");
		}

		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setDob(request.getDob());

		return userRepository.save(user);
	}

	public User updateUser(String userId, UserUpdateRequest request) {
		User user = getUser(userId);

		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setDob(request.getDob());

		return userRepository.save(user);
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User getUser(String id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

}
