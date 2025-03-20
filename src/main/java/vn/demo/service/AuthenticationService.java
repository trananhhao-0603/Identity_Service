package vn.demo.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import vn.demo.dto.request.AuthenticationRequest;
import vn.demo.dto.request.IntrospectRequest;
import vn.demo.dto.response.AuthenticationResponse;
import vn.demo.dto.response.IntrospectResponse;
import vn.demo.exception.AppException;
import vn.demo.exception.ErrorCode;
import vn.demo.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
	UserRepository userRepository;

	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();

		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		return IntrospectResponse.builder().valid(verified && expiredTime.after(new Date())).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXISTED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated) {
			throw new AppException(ErrorCode.NOT_AUTHENTICATED);
		}
		var token = generateToken(request.getUsername());

		return AuthenticationResponse.builder().token(token).authenticated(authenticated).build();
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
