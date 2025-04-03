package vn.demo.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import vn.demo.entity.Role;
import vn.demo.entity.User;
import vn.demo.repository.RoleRepository;
import vn.demo.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
	    RoleRepository roleRepository) {
	log.info("Initializing application.....");
	return args -> {
	    if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
		roleRepository.save(Role.builder().name("USER")
			.description("User role").build());

		Role adminRole = roleRepository.save(Role.builder()
			.name("ADMIN").description("Admin role").build());

		var roles = new HashSet<Role>();
		roles.add(adminRole);

		User user = User.builder().username(ADMIN_USER_NAME)
			.password(passwordEncoder.encode(ADMIN_PASSWORD))
			.roles(roles).build();

		userRepository.save(user);
		log.warn(
			"admin user has been created with default password: admin, please change it");
	    }
	    log.info("Application initialization completed .....");
	};
    }
}
