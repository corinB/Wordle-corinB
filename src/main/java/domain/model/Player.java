package domain.model;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Player {

  private static final int MIN_NICK_LENGTH = 2;
  private static final int MAX_NICK_LENGTH = 10;


  private static final Pattern NICK_PATTERN =
    Pattern.compile("^[A-Za-z0-9!?_*/-]+$");

  private static final Pattern EMAIL_PATTERN =
    Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

  private final String nick;
  private final String email;
  private final String encodedPassword;

  private Player(
    String nick,
    String email,
    String encodedPassword
  ) {
    this.nick = validateNick(nick);
    this.email = validateEmail(email);
    this.encodedPassword =
      validateEncodedPassword(encodedPassword);
  }

  public static Player create(
    String nick,
    String email,
    String encodedPassword
  ) {
    return new Player(nick, email, encodedPassword);
  }

  public String getNick() {
    return nick;
  }

  public String getEmail() {
    return email;
  }

  public boolean matchesPassword(
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
      encodedPassword
    );
  }

  private static String validateNick(String nick) {
    if (nick == null || nick.isBlank()) {
      throw new IllegalArgumentException(
        "닉네임은 필수입니다."
      );
    }

    String value = nick.trim();

    if (value.length() < MIN_NICK_LENGTH
      || value.length() > MAX_NICK_LENGTH) {
      throw new IllegalArgumentException(
        "닉네임은 2자 이상 12자 이하여야 합니다."
      );
    }

    if (!NICK_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException(
        "닉네임은 영문, 숫자, !, ?, _, -, *, /만 사용할 수 있습니다."
      );
    }

    return value;
  }

  private static String validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException(
        "이메일은 필수입니다."
      );
    }

    String value =
      email.trim().toLowerCase(Locale.ROOT);

    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException(
        "올바른 이메일 형식이 아닙니다."
      );
    }

    return value;
  }

  private static String validateEncodedPassword(
    String encodedPassword
  ) {
    if (encodedPassword == null
      || encodedPassword.isBlank()) {
      throw new IllegalArgumentException(
        "인코딩된 비밀번호는 필수입니다."
      );
    }

    return encodedPassword;
  }
}
