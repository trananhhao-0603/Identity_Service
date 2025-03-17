package vn.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

}
