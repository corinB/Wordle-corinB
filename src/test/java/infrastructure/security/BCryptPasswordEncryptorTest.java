package infrastructure.security;

import domain.vo.EncodedPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BCryptPasswordEncryptorTest {

  @Test
  @DisplayName("비밀번호를 BCrypt로 암호화하고 검증한다")
  void encryptAndMatchesPassword() {
    BCryptPasswordEncryptor passwordEncryptor =
      new BCryptPasswordEncryptor();

    EncodedPassword encodedPassword =
      passwordEncryptor.encrypt("password123");

    assertAll(
      () -> assertThat(encodedPassword.value())
        .isNotEqualTo("password123"),
      () -> assertThat(
        passwordEncryptor.matches("password123", encodedPassword.value())
      ).isTrue(),
      () -> assertThat(
        passwordEncryptor.matches("wrongPassword", encodedPassword.value())
      ).isFalse()
    );
  }

  @Test
  @DisplayName("비어 있는 비밀번호는 암호화할 수 없다")
  void cannotEncryptBlankPassword() {
    BCryptPasswordEncryptor passwordEncryptor =
      new BCryptPasswordEncryptor();

    assertThatThrownBy(() -> passwordEncryptor.encrypt(" "))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
