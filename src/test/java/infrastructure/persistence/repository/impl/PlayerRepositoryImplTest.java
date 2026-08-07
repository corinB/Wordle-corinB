package infrastructure.persistence.repository.impl;

import application.WordleApplication;
import domain.model.Player;
import domain.model.vo.Email;
import domain.repository.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(PlayerRepositoryImpl.class)
@ContextConfiguration(classes = WordleApplication.class)
class PlayerRepositoryImplTest {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("Player를 저장하고 Email VO로 조회한다")
  void saveAndFindByEmail() {
    Player player = Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    );

    playerRepository.save(player);
    entityManager.flush();
    entityManager.clear();

    Optional<Player> foundPlayer =
      playerRepository.findByEmail(new Email("corin@example.com"));

    assertThat(foundPlayer)
      .isPresent();

    Player savedPlayer = foundPlayer.get();

    assertAll(
      () -> assertThat(savedPlayer.getNickname().value())
        .isEqualTo("corinB"),
      () -> assertThat(savedPlayer.getEmail().value())
        .isEqualTo("corin@example.com"),
      () -> assertThat(savedPlayer.getEncodedPassword().value())
        .isEqualTo("encodedPassword")
    );
  }

  @Test
  @DisplayName("같은 이메일의 Player를 저장하면 예외가 발생한다")
  void cannotSaveDuplicateEmail() {
    playerRepository.save(Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    ));
    entityManager.flush();

    assertThatThrownBy(() -> {
      playerRepository.save(Player.create(
        "otherB",
        "corin@example.com",
        "otherPassword"
      ));
      entityManager.flush();
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("같은 닉네임의 Player를 저장하면 예외가 발생한다")
  void cannotSaveDuplicateNickname() {
    playerRepository.save(Player.create(
      "corinB",
      "corin@example.com",
      "encodedPassword"
    ));
    entityManager.flush();

    assertThatThrownBy(() -> {
      playerRepository.save(Player.create(
        "corinB",
        "other@example.com",
        "otherPassword"
      ));
      entityManager.flush();
    }).isInstanceOf(DataIntegrityViolationException.class);
  }
}
