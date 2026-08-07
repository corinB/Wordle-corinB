package domain.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import domain.model.Player;
import domain.model.vo.Email;
import domain.model.vo.Nickname;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PlayerRepositoryTest {

  private PlayerRepository playerRepository;

  @BeforeEach
  void setUp() {
    playerRepository = new FakePlayerRepository();
  }

  @Test
  @DisplayName("플레이어를 저장하고 이메일로 조회한다")
  void saveAndFindByEmail() {
    // given
    Player player = Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    );

    // when
    playerRepository.save(player);

    Optional<Player> foundPlayer =
      playerRepository.findByEmail("corin@example.com");

    // then
    assertThat(foundPlayer)
      .isPresent()
      .containsSame(player);
  }

  @Test
  @DisplayName("이메일과 닉네임의 중복 여부를 확인한다")
  void checkDuplicatePlayer() {
    // given
    Player player = Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    );

    playerRepository.save(player);

    // then
    assertAll(
      () -> assertThat(
        playerRepository.existsByEmail("corin@example.com")
      ).isTrue(),

      () -> assertThat(
        playerRepository.existsByEmail("other@example.com")
      ).isFalse(),

      () -> assertThat(
        playerRepository.existsByNick("corinB")
      ).isTrue(),

      () -> assertThat(
        playerRepository.existsByNick("otherNick")
      ).isFalse()
    );
  }

  private static class FakePlayerRepository
    implements PlayerRepository {

    private final Map<String, Player> playersByEmail =
      new HashMap<>();

    @Override
    public Player save(Player player) {
      playersByEmail.put(player.getEmail().value(), player);
      return player;
    }

    @Override
    public Optional<Player> findByEmail(String email) {
      return Optional.ofNullable(playersByEmail.get(email));
    }

    @Override
    public boolean existsByEmail(String email) {
      return playersByEmail.containsKey(email);
    }

    @Override
    public boolean existsByNick(String nick) {
      return playersByEmail.values()
        .stream()
        .anyMatch(player -> player.getNickname().value().equals(nick));
    }

    @Override
    public Optional<Player> findByEmail(Email email) {
      return findByEmail(email.value());
    }

    @Override
    public boolean existsByEmail(Email email) {
      return existsByEmail(email.value());
    }

    @Override
    public boolean existsByNick(Nickname nick) {
      return existsByNick(nick.value());
    }
  }
}
