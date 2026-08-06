package domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private static final String VALID_NICK = "corinB";
  private static final String VALID_EMAIL = "corin@example.com";
  private static final String ENCODED_PASSWORD = "encoded:password123";

  @Test
  @DisplayName("조건에 맞는 값으로 Player를 생성한다")
  void createPlayer() {
    Player player = Player.create(
      VALID_NICK,
      VALID_EMAIL,
      ENCODED_PASSWORD
    );

    assertAll(
      () -> assertThat(player.getNick())
        .isEqualTo(VALID_NICK),
      () -> assertThat(player.getEmail())
        .isEqualTo(VALID_EMAIL)
    );
  }

  @Test
  @DisplayName("유효하지 않은 닉네임으로 Player를 생성할 수 없다")
  void cannotCreatePlayerWithInvalidNick() {
    assertAll(
      () -> assertThatThrownBy(() ->
        Player.create(
          "a",
          VALID_EMAIL,
          ENCODED_PASSWORD
        )
      ).isInstanceOf(IllegalArgumentException.class),

      () -> assertThatThrownBy(() ->
        Player.create(
          "abcdefghijklm",
          VALID_EMAIL,
          ENCODED_PASSWORD
        )
      ).isInstanceOf(IllegalArgumentException.class),

      () -> assertThatThrownBy(() ->
        Player.create(
          "백종현",
          VALID_EMAIL,
          ENCODED_PASSWORD
        )
      ).isInstanceOf(IllegalArgumentException.class)
    );
  }

  @Test
  @DisplayName("필수값이 없으면 Player를 생성할 수 없다")
  void cannotCreatePlayerWithoutRequiredValues() {
    assertAll(
      () -> assertThatThrownBy(() ->
        Player.create(
          VALID_NICK,
          null,
          ENCODED_PASSWORD
        )
      ).isInstanceOf(IllegalArgumentException.class),

      () -> assertThatThrownBy(() ->
        Player.create(
          VALID_NICK,
          VALID_EMAIL,
          null
        )
      ).isInstanceOf(IllegalArgumentException.class)
    );
  }

  //Player가 비밀번호 인코딩 방법에 의존하지 않도록 분리
  @Test
  @DisplayName("입력한 비밀번호가 저장된 비밀번호와 일치하는지 확인한다")
  void matchesPassword() {
    Player player = Player.create(
      VALID_NICK,
      VALID_EMAIL,
      ENCODED_PASSWORD
    );

    PasswordMatcher matcher =
      (rawPassword, encodedPassword) ->
        encodedPassword.equals("encoded:" + rawPassword);

    assertAll(
      () -> assertThat(
        player.matchesPassword("password123", matcher)
      ).isTrue(),

      () -> assertThat(
        player.matchesPassword("aaasssccc!!!", matcher)
      ).isFalse()
    );
  }
}
