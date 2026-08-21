package domain.model;

import domain.policy.PasswordMatcher;
import domain.vo.Email;
import domain.vo.EncodedPassword;
import domain.vo.Nickname;

import static domain.exception.DomainErrorType.EMAIL_REQUIRED;
import static domain.exception.DomainErrorType.NICKNAME_REQUIRED;
import static domain.exception.DomainErrorType.PASSWORD_REQUIRED;

public final class Player {

  private final Long id;
  private final Nickname nickname;
  private final Email email;
  private final EncodedPassword encodedPassword;

  public Player(Nickname nickname, Email email, EncodedPassword password) {
    this(null, nickname, email, password);
  }

  private Player(
    Long id,
    Nickname nickname,
    Email email,
    EncodedPassword password
  ) {
    if (nickname == null) {
      throw NICKNAME_REQUIRED.createException();
    }
    if (email == null) {
      throw EMAIL_REQUIRED.createException();
    }
    if (password == null) {
      throw PASSWORD_REQUIRED.createException();
    }

    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.encodedPassword = password;
  }

  public Email getEmail() {
    return email;
  }

  public Long getId() {
    return id;
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
    return encodedPassword.matches(rawPassword, passwordMatcher);
  }

  public static Player create(
    String nickname,
    String email,
    String encodedPassword
  ) {
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
  ) {
    return new Player(nickname, email, encodedPassword);
  }

  public static Player restore(
    Long id,
    Nickname nickname,
    Email email,
    EncodedPassword encodedPassword
  ) {
    return new Player(id, nickname, email, encodedPassword);
  }
}
