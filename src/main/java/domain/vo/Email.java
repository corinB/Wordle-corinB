package domain.vo;

import java.util.Locale;
import java.util.regex.Pattern;

import static domain.exception.DomainErrorType.EMAIL_REQUIRED;
import static domain.exception.DomainErrorType.INVALID_EMAIL_FORMAT;

public record Email(String value) {

  private static final Pattern PATTERN =
    Pattern.compile(
      "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

  public Email {
    if (value == null || value.isBlank()) {
      throw EMAIL_REQUIRED.createException();
    }

    value = value
      .trim()
      .toLowerCase(Locale.ROOT);

    if (!PATTERN.matcher(value).matches()) {
      throw INVALID_EMAIL_FORMAT.createException();
    }
  }
}
