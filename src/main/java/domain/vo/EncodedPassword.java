package domain.vo;

import domain.policy.PasswordMatcher;
import java.util.Objects;

public record EncodedPassword(String value) {

  public EncodedPassword {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
        "인코딩된 비밀번호는 필수입니다."
      );
    }
  }

  public boolean matches(
    String rawPassword,
    PasswordMatcher passwordMatcher
  ) {
    Objects.requireNonNull(
      passwordMatcher,
      "비밀번호 비교기가 필요합니다."
    );

    if (rawPassword == null || rawPassword.isBlank()) {
      return false;
    }

    return passwordMatcher.matches(
      rawPassword,
      value
    );
  }
}
