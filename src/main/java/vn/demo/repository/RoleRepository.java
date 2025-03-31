package vn.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.demo.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
