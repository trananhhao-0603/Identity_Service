package vn.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.demo.dto.request.ApiResponse;
import vn.demo.dto.request.PermissionRequest;
import vn.demo.dto.response.PermissionResponse;
import vn.demo.service.PermissionService;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
	PermissionService permissionService;

	@PostMapping
	ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
		return ApiResponse.<PermissionResponse>builder().result(permissionService.create(request)).build();
	}

	@GetMapping
	ApiResponse<List<PermissionResponse>> getAll() {
		return ApiResponse.<List<PermissionResponse>>builder().result(permissionService.getAll()).build();
	}

	@DeleteMapping("/{permission}")
	ApiResponse<Void> delete(@PathVariable String permission) {
		permissionService.delete(permission);
		return ApiResponse.<Void>builder().build();
	}
}
