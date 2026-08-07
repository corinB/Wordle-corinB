package domain.model.vo;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

  private static final Pattern PATTERN =
    Pattern.compile(
      "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

  public Email {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
        "이메일은 필수입니다."
      );
    }

    value = value
      .trim()
      .toLowerCase(Locale.ROOT);

    if (!PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException(
        "올바른 이메일 형식이 아닙니다."
      );
    }
  }
}
