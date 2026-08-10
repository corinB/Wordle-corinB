package domain.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import domain.model.Player;
import domain.vo.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PlayerRepositoryTest {

  private PlayerRepository playerRepository;

  @BeforeEach
  void setUp() {
    playerRepository = new FakePlayerRepository();
  }

  @Test
  @DisplayName("플레이어를 저장하고 Email VO로 조회한다")
  void saveAndFindByEmail() {
    Player player = Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    );

    playerRepository.save(player);

    Optional<Player> foundPlayer =
      playerRepository.findByEmail(new Email("corin@example.com"));

    assertThat(foundPlayer)
      .isPresent()
      .containsSame(player);
  }

  @Test
  @DisplayName("저장되지 않은 이메일로 조회하면 빈 값을 반환한다")
  void findByUnknownEmailReturnsEmpty() {
    Optional<Player> foundPlayer =
      playerRepository.findByEmail(new Email("unknown@example.com"));

    assertThat(foundPlayer)
      .isEmpty();
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
    public Optional<Player> findByEmail(Email email) {
      return Optional.ofNullable(playersByEmail.get(email.value()));
    }
  }
}
