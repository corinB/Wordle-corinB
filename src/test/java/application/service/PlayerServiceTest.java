package application.service;

import domain.model.Player;
import domain.policy.PasswordEncryptor;
import domain.policy.PasswordMatcher;
import domain.repository.PlayerRepository;
import domain.vo.Email;
import domain.vo.EncodedPassword;
import domain.vo.Nickname;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PlayerServiceTest {

  private PlayerRepository playerRepository;
  private PlayerService playerService;

  @BeforeEach
  void setUp() {
    playerRepository = new FakePlayerRepository();
    PasswordEncryptor passwordEncryptor =
      rawPassword -> new EncodedPassword("encoded:" + rawPassword);
    PasswordMatcher passwordMatcher =
      (rawPassword, encodedPassword) ->
        encodedPassword.equals("encoded:" + rawPassword);

    playerService = new PlayerService(
      playerRepository,
      passwordEncryptor,
      passwordMatcher
    );
  }

  @Test
  @DisplayName("플레이어를 가입시킨다")
  void registerPlayer() {
    Player player = playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    assertAll(
      () -> assertThat(player.getNickname())
        .isEqualTo(new Nickname("corinB")),
      () -> assertThat(player.getEmail())
        .isEqualTo(new Email("corin@example.com")),
      () -> assertThat(player.getEncodedPassword().value())
        .isEqualTo("encoded:password123")
    );
  }

  @Test
  @DisplayName("이미 사용 중인 이메일로 가입할 수 없다")
  void cannotRegisterWithDuplicateEmail() {
    playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    assertThatThrownBy(() ->
      playerService.register(
        "otherB",
        "corin@example.com",
        "password123"
      )
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("이미 사용 중인 닉네임으로 가입할 수 없다")
  void cannotRegisterWithDuplicateNickname() {
    playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    assertThatThrownBy(() ->
      playerService.register(
        "corinB",
        "other@example.com",
        "password123"
      )
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("이메일과 비밀번호로 로그인한다")
  void loginPlayer() {
    playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    Player player =
      playerService.login("corin@example.com", "password123");

    assertThat(player.getNickname())
      .isEqualTo(new Nickname("corinB"));
  }

  @Test
  @DisplayName("가입되지 않은 이메일로 로그인할 수 없다")
  void cannotLoginWithUnknownEmail() {
    assertThatThrownBy(() ->
      playerService.login("unknown@example.com", "password123")
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("일치하지 않는 비밀번호로 로그인할 수 없다")
  void cannotLoginWithWrongPassword() {
    playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    assertThatThrownBy(() ->
      playerService.login("corin@example.com", "wrongPassword")
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("이메일로 플레이어를 조회한다")
  void findPlayerByEmail() {
    playerService.register(
      "corinB",
      "corin@example.com",
      "password123"
    );

    Player player =
      playerService.findByEmail("corin@example.com");

    assertThat(player.getNickname())
      .isEqualTo(new Nickname("corinB"));
  }

  @Test
  @DisplayName("가입되지 않은 이메일로 플레이어를 조회할 수 없다")
  void cannotFindPlayerByUnknownEmail() {
    assertThatThrownBy(() ->
      playerService.findByEmail("unknown@example.com")
    ).isInstanceOf(IllegalArgumentException.class);
  }

  private static class FakePlayerRepository
    implements PlayerRepository {

    private final Map<String, Player> playersByEmail =
      new HashMap<>();
    private final Map<String, Player> playersByNickname =
      new HashMap<>();

    @Override
    public Player save(Player player) {
      playersByEmail.put(player.getEmail().value(), player);
      playersByNickname.put(player.getNickname().value(), player);
      return player;
    }

    @Override
    public Optional<Player> findByEmail(Email email) {
      return Optional.ofNullable(playersByEmail.get(email.value()));
    }

    @Override
    public Optional<Player> findByNickname(Nickname nickname) {
      return Optional.ofNullable(playersByNickname.get(nickname.value()));
    }
  }
}
