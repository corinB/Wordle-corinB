package domain.vo;

import java.util.regex.Pattern;

import static domain.exception.DomainErrorType.INVALID_NICKNAME_FORMAT;
import static domain.exception.DomainErrorType.INVALID_NICKNAME_LENGTH;
import static domain.exception.DomainErrorType.NICKNAME_REQUIRED;

public record Nickname(String value) {

  private static final int MIN_LENGTH = 2;
  private static final int MAX_LENGTH = 10;

  private static final Pattern PATTERN =
    Pattern.compile("^[A-Za-z0-9!?_*/-]+$");

  public Nickname {
    if (value == null || value.isBlank()) {
      throw NICKNAME_REQUIRED.createException();
    }

    value = value.trim();

    if (value.length() < MIN_LENGTH
      || value.length() > MAX_LENGTH) {
      throw INVALID_NICKNAME_LENGTH.createException();
    }

    if (!PATTERN.matcher(value).matches()) {
      throw INVALID_NICKNAME_FORMAT.createException();
    }
  }
}
