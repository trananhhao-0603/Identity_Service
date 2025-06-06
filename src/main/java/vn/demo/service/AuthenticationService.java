package vn.demo.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import lombok.extern.slf4j.Slf4j;
import vn.demo.dto.request.AuthenticationRequest;
import vn.demo.dto.request.IntrospectRequest;
import vn.demo.dto.request.LogOutRequest;
import vn.demo.dto.request.RefreshRequest;
import vn.demo.dto.response.AuthenticationResponse;
import vn.demo.dto.response.IntrospectResponse;
import vn.demo.entity.InvalidatedToken;
import vn.demo.entity.User;
import vn.demo.exception.AppException;
import vn.demo.exception.ErrorCode;
import vn.demo.repository.InvalidatedTokenRepository;
import vn.demo.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request)
	    throws JOSEException, ParseException {
	var token = request.getToken();
	boolean isValid = true;

	try {
	    verifyToken(token, false);
	} catch (AppException e) {
	    isValid = false;
	}

	return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
	var user = userRepository.findByUsername(request.getUsername())
		.orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXISTED));

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
	boolean authenticated = passwordEncoder.matches(request.getPassword(),
		user.getPassword());

	if (!authenticated) {
	    throw new AppException(ErrorCode.NOT_AUTHENTICATED);
	}
	var token = generateToken(user);

	return AuthenticationResponse.builder().token(token).authenticated(true)
		.build();
    }

    public void logOut(LogOutRequest request)
	    throws JOSEException, ParseException {

	try {
	    var signToken = verifyToken(request.getToken(), true);
	    String jit = signToken.getJWTClaimsSet().getJWTID();
	    Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

	    InvalidatedToken invalidatedToken = InvalidatedToken.builder()
		    .id(jit).expiryTime(expiryTime).build();
	    invalidatedTokenRepository.save(invalidatedToken);
	} catch (AppException e) {
	    log.info("Token already expired");
	}

    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
	    throws JOSEException, ParseException {
	var signJWT = verifyToken(request.getToken(), true);

	var jit = signJWT.getJWTClaimsSet().getJWTID();
	var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
	InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit)
		.expiryTime(expiryTime).build();
	invalidatedTokenRepository.save(invalidatedToken);

	var username = signJWT.getJWTClaimsSet().getSubject();

	log.info(username);
	var user = userRepository.findByUsername(username).orElseThrow(
		() -> new AppException(ErrorCode.NOT_AUTHENTICATED));

	var token = generateToken(user);

	return AuthenticationResponse.builder().token(token).authenticated(true)
		.build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh)
	    throws JOSEException, ParseException {
	JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

	SignedJWT signedJWT = SignedJWT.parse(token);

	Date expiredTime = (isRefresh)
		? new Date(
			signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
				.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
				.toEpochMilli())
		: signedJWT.getJWTClaimsSet().getExpirationTime();

	var verified = signedJWT.verify(verifier);
	if (!(verified && expiredTime.after(new Date()))) {
	    throw new AppException(ErrorCode.NOT_AUTHENTICATED);
	}

	if (invalidatedTokenRepository
		.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
	    throw new AppException(ErrorCode.NOT_AUTHENTICATED);
	}
	return signedJWT;
    }

    private String generateToken(User user) {
	JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

	JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
		.subject(user.getUsername()).issuer("demo.vn")
		.issueTime(new Date())
		.expirationTime(new Date(
			Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)
				.toEpochMilli()))
		.jwtID(UUID.randomUUID().toString())
		.claim("scope", buildScope(user)).build();

	Payload payload = new Payload(jwtClaimsSet.toJSONObject());

	JWSObject jwsObject = new JWSObject(header, payload);

	try {
	    jwsObject.sign(new MACSigner(SIGNER_KEY));
	    return jwsObject.serialize();
	} catch (JOSEException e) {
	    throw new RuntimeException(e);
	}
    }

    private String buildScope(User user) {
	StringJoiner stringJoiner = new StringJoiner(" ");
	if (!CollectionUtils.isEmpty(user.getRoles())) {
	    user.getRoles().forEach(role -> {
		stringJoiner.add("ROLE_" + role.getName());
		if (!CollectionUtils.isEmpty(role.getPermissions())) {
		    role.getPermissions().forEach(permission -> stringJoiner
			    .add(permission.getName()));
		}
	    });
	}
	return stringJoiner.toString();
    }
}
