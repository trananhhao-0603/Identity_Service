package vn.demo.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = { "/users", "/auth/login",
		    "/auth/introspect", "/auth/logout", "/auth/refresh" };

    @Value("${jwt.signerKey}")
    private String secretKey;

    private CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {

	this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity)
	    throws Exception {
	httpSecurity.authorizeHttpRequests(request -> request
		.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
		.anyRequest().authenticated());

	httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
		.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
			.jwtAuthenticationConverter(
				jwtAuthenticationConverter()))
		.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

	httpSecurity.csrf(AbstractHttpConfigurer::disable);
	httpSecurity.cors(
		cors -> cors.configurationSource(corsConfigurationSource()));

	return httpSecurity.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
	CorsConfiguration config = new CorsConfiguration();
	config.setAllowedOrigins(List.of("http://localhost:3000"));
	config.setAllowedMethods(
		List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	config.setAllowedHeaders(List.of("*"));
	config.setAllowCredentials(true);

	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", config);
	return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
	JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

	JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
		jwtGrantedAuthoritiesConverter);
	return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder(10);
    }

}
