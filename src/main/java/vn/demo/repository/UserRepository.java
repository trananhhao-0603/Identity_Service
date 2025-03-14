package vn.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	boolean existsByUsername(String username);

}
