package vn.demo.exception;

public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(9999, "ERROR"), USER_EXISTED(1002, "User existed"),
	USERNAME_INVALID(1003, "Username must be at least 3 characters"),
	PASSWORD_INVALID(1004, "Password must be at least 8 characters"),
	USER_NOTEXISTED(1005, "Password must be at least 8 characters");

	ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
