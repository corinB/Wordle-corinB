package domain.vo;

import domain.policy.PasswordMatcher;

import static domain.exception.DomainErrorType.ENCODED_PASSWORD_REQUIRED;
import static domain.exception.DomainErrorType.PASSWORD_MATCHER_REQUIRED;

public record EncodedPassword(String value) {

  public EncodedPassword {
    if (value == null || value.isBlank()) {
      throw ENCODED_PASSWORD_REQUIRED.createException();
    }
  }

  public boolean matches(
    String rawPassword,
    PasswordMatcher passwordMatcher
  ) {
    if (passwordMatcher == null) {
      throw PASSWORD_MATCHER_REQUIRED.createException();
    }

    if (rawPassword == null || rawPassword.isBlank()) {
      return false;
    }

    return passwordMatcher.matches(
      rawPassword,
      value
    );
  }
}
