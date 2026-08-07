package domain.model.vo;

import java.util.regex.Pattern;

public record Nickname(String value) {

  private static final int MIN_LENGTH = 2;
  private static final int MAX_LENGTH = 10;

  private static final Pattern PATTERN =
    Pattern.compile("^[A-Za-z0-9!?_*/-]+$");

  public Nickname {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
        "닉네임은 필수입니다."
      );
    }

    value = value.trim();

    if (value.length() < MIN_LENGTH
      || value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
        "닉네임은 2자 이상 10자 이하여야 합니다."
      );
    }

    if (!PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException(
        "닉네임은 영문, 숫자, !, ?, _, -, *, /만 사용할 수 있습니다."
      );
    }
  }
}
