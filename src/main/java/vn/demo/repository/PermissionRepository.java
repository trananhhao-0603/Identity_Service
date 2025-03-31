package vn.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.demo.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
