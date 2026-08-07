package domain.model;

import domain.model.vo.Email;
import domain.model.vo.EncodedPassword;
import domain.model.vo.Nickname;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Player {

  private final Nickname nickname;
  private final Email email;
  private final EncodedPassword encodedPassword;

  public Player(Nickname nickname, Email email, EncodedPassword password) {
    this.nickname = nickname;
    this.email = email;
    this.encodedPassword = password;
  }

  public String getNick() {
    return nickname.value();
  }

  public String getEmail() {
    return email.value();
  }

  public boolean matchesPassword(
    String rawPassword,
    PasswordMatcher passwordMatcher
  ) {
    return encodedPassword.matches(rawPassword,passwordMatcher);
  }

  public static Player create(
    String nickname,
    String email,
    String encodedPassword
  ){
    return new Player(
      new Nickname(nickname),
      new Email(email),
      new EncodedPassword(encodedPassword)
    );
  }
}
