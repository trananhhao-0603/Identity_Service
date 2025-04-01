package vn.demo.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.demo.enums.Role;
import vn.demo.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppInitConfig {

//	PasswordEncoder passwordEncoder;

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				var roles = new HashSet<String>();
				roles.add(Role.ADMIN.name());

//				User user = User.builder().username("admin").password(passwordEncoder.encode("admin")).roles(roles)
//						.build();
//				userRepository.save(user);
				log.warn("admin user has been created with default password: admin, please change it!");
			}
		};
	}
}
