package vn.demo.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.demo.dto.request.RoleRequest;
import vn.demo.dto.response.RoleResponse;
import vn.demo.mapper.RoleMapper;
import vn.demo.repository.PermissionRepository;
import vn.demo.repository.RoleRepository;

@Service
@RequiredArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
	RoleRepository roleRepository;
	PermissionRepository permissionRepository;

	RoleMapper roleMapper;

	public RoleResponse create(RoleRequest request) {
		var role = roleMapper.toRole(request);

		var permissions = permissionRepository.findAllById(request.getPermissions());
		role.setPermissions(new HashSet<>(permissions));

		role = roleRepository.save(role);
		return roleMapper.toRoleResponse(role);
	}

	public List<RoleResponse> getAll() {
		return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
	}

	public void delete(String role) {
		roleRepository.deleteById(role);
	}
}
