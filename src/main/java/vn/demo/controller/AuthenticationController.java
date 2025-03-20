package vn.demo.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.demo.dto.request.ApiResponse;
import vn.demo.dto.request.AuthenticationRequest;
import vn.demo.dto.request.IntrospectRequest;
import vn.demo.dto.response.AuthenticationResponse;
import vn.demo.dto.response.IntrospectResponse;
import vn.demo.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
	AuthenticationService authenticationService;

	@PostMapping("/token")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		var result = authenticationService.authenticate(request);
		return ApiResponse.<AuthenticationResponse>builder().result(result).build();
	}

	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
			throws JOSEException, ParseException {
		var result = authenticationService.introspect(request);
		return ApiResponse.<IntrospectResponse>builder().result(result).build();
	}
}
