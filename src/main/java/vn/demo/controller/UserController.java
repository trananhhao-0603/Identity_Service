package vn.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.demo.dto.request.ApiResponse;
import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.entity.User;
import vn.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
		ApiResponse<User> apiResponse = new ApiResponse<User>();

		apiResponse.setResult(userService.createUser(request));

		return apiResponse;
	}

	@GetMapping
	List<User> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/{userId}")
	User getUser(@PathVariable String userId) {
		return userService.getUser(userId);
	}

	@PutMapping("/{userId}")
	User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return userService.updateUser(userId, request);
	}

	@DeleteMapping("/{userId}")
	String deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return "Deleted user successful";
	}

}
