package vn.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import vn.demo.dto.request.ApiResponse;
import vn.demo.dto.request.UserCreationRequest;
import vn.demo.dto.request.UserUpdateRequest;
import vn.demo.dto.response.UserResponse;
import vn.demo.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {

		return ApiResponse.<UserResponse>builder().result(userService.createUser(request)).build();
	}

	@GetMapping
	ApiResponse<List<UserResponse>> getUsers() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		log.warn("Username: {}", authentication.getName());
		authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

		return ApiResponse.<List<UserResponse>>builder().result(userService.getUsers()).build();
	}

	@GetMapping("/{userId}")
	UserResponse getUser(@PathVariable String userId) {
		return userService.getUser(userId);
	}

	@PutMapping("/{userId}")
	UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return userService.updateUser(userId, request);
	}

	@DeleteMapping("/{userId}")
	String deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return "Deleted user successful";
	}

	@GetMapping("/myinfo")
	ApiResponse<UserResponse> getMyInfo() {
		return ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build();
	}

}
