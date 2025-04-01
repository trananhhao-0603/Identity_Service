package vn.demo.dto.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.demo.validator.DobConstraint;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
	String password;
	String firstName;
	String lastName;
	@DobConstraint(min = 18, message = "INVALID_DOB")
	LocalDate dob;
	List<String> roles;
}
