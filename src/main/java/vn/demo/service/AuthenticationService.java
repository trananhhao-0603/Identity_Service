package vn.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import vn.demo.dto.request.AuthenticationRequest;
import vn.demo.dto.response.AuthenticationResponse;
import vn.demo.exception.AppException;
import vn.demo.exception.ErrorCode;
import vn.demo.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
	UserRepository userRepository;

	@NonFinal
	protected static final String SIGNER_KEY = "hSWgPEWVi5X6MJWJWJF9HmR1sscFhTXcnL/HCPBOHzoH4m1VzLdH0JOT5I1UHjdI";

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXISTED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated) {
			throw new AppException(ErrorCode.NOT_AUTHENTICATED);
		}
		var token = generateToken(request.getUsername());

		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	}

	String generateToken(String username) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(username).issuer("demo.vn").issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
				.claim("customClaim", "custom").build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}
}
