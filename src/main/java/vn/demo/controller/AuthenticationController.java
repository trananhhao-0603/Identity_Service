package vn.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.demo.dto.request.ApiResponse;
import vn.demo.dto.request.AuthenticationRequest;
import vn.demo.dto.response.AuthenticationResponse;
import vn.demo.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
	AuthenticationService authenticationService;

	@PostMapping("/login")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		boolean result = authenticationService.authenticate(request);
		return ApiResponse.<AuthenticationResponse>builder()
				.result(AuthenticationResponse.builder().authenticated(result).build()).build();
	}
}
