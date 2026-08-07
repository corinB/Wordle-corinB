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

    if (nickname == null) {
      throw new IllegalArgumentException("닉네임은 필수입니다.");
    }
    if (email == null) {
      throw new IllegalArgumentException("이메일은 필수입니다.");
    }
    if (password == null) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }

    this.nickname = nickname;
    this.email = email;
    this.encodedPassword = password;
  }

  public Email getEmail() {
    return email;
  }

  public Nickname getNickname() {
    return nickname;
  }

  public EncodedPassword getEncodedPassword() {
    return encodedPassword;
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

  public static Player create(
    Nickname nickname,
    Email email,
    EncodedPassword encodedPassword
  ){
    return new Player(nickname,email,encodedPassword);
  }
}
