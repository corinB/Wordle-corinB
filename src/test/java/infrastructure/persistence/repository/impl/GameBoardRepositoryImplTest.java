package infrastructure.persistence.repository.impl;

import application.WordleApplication;
import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.vo.Word;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.entity.WordEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
@Import(GameBoardRepositoryImpl.class)
@ContextConfiguration(classes = WordleApplication.class)
class GameBoardRepositoryImplTest {

  @Autowired
  private GameBoardRepository gameBoardRepository;

  @Autowired
  private GameBoardJPARepository gameBoardJPARepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("GameBoard를 저장하고 플레이어와 게임으로 조회한다")
  void saveAndFindByPlayerAndGame() {
    Player player = persistPlayer("corinB", "corin@example.com");
    Word correct = new Word("apple");
    WordleGame game = persistGame(
      correct,
      LocalDateTime.of(2026, 8, 10, 0, 0)
    );
    GameBoard gameBoard = new GameBoard(player, game);
    Word firstAnswer = new Word("hello");

    gameBoard.submit(firstAnswer);
    gameBoardRepository.save(gameBoard);
    gameBoard.submit(correct);
    gameBoardRepository.save(gameBoard);
    entityManager.flush();
    entityManager.clear();

    Optional<GameBoard> foundGameBoard =
      gameBoardRepository.findByPlayerAndGame(player, game);

    assertThat(foundGameBoard)
      .isPresent();

    GameBoard savedGameBoard = foundGameBoard.get();

    assertAll(
      () -> assertThat(savedGameBoard.getPlayer().getEmail())
        .isEqualTo(player.getEmail()),
      () -> assertThat(savedGameBoard.getGame().getStart())
        .isEqualTo(game.getStart()),
      () -> assertThat(savedGameBoard.getStatus())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(savedGameBoard.getRounds())
        .extracting(round -> round.answer().value())
        .containsExactly(firstAnswer.value(), correct.value()),
      () -> assertThat(savedGameBoard.getRecords())
        .containsExactly(
          correct.compare(firstAnswer),
          correct.compare(correct)
        )
    );
  }

  @Test
  @DisplayName("같은 플레이어와 게임의 GameBoard는 중복 생성하지 않고 갱신한다")
  void updateExistingGameBoard() {
    Player player = persistPlayer("corinB", "corin@example.com");
    Word correct = new Word("apple");
    WordleGame game = persistGame(
      correct,
      LocalDateTime.of(2026, 8, 10, 0, 0)
    );
    GameBoard gameBoard = new GameBoard(player, game);

    gameBoard.submit(new Word("hello"));
    gameBoardRepository.save(gameBoard);
    gameBoard.submit(correct);
    gameBoardRepository.save(gameBoard);
    entityManager.flush();
    entityManager.clear();

    GameBoard savedGameBoard = gameBoardRepository
      .findByPlayerAndGame(player, game)
      .orElseThrow();

    assertAll(
      () -> assertThat(gameBoardJPARepository.count())
        .isEqualTo(1),
      () -> assertThat(savedGameBoard.getStatus())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(savedGameBoard.getSpentChance())
        .isEqualTo(2)
    );
  }

  @Test
  @DisplayName("종료 시간이 지난 진행 중인 GameBoard를 조회한다")
  void findAllPlayingBoardsEndedBefore() {
    Player endedPlayer = persistPlayer("endedB", "ended@example.com");
    Player activePlayer = persistPlayer("activeB", "active@example.com");
    Word endedCorrect = new Word("apple");
    Word activeCorrect = new Word("cocoa");
    LocalDateTime currentTime = LocalDateTime.of(2026, 8, 10, 12, 0);
    WordleGame endedGame = persistGame(
      endedCorrect,
      currentTime.minusDays(2)
    );
    WordleGame activeGame = persistGame(
      activeCorrect,
      currentTime.plusDays(1)
    );

    gameBoardRepository.save(new GameBoard(endedPlayer, endedGame));
    gameBoardRepository.save(new GameBoard(activePlayer, activeGame));
    entityManager.flush();
    entityManager.clear();

    List<GameBoard> endedBoards =
      gameBoardRepository.findAllPlayingBoardsEndedBefore(currentTime);

    assertAll(
      () -> assertThat(endedBoards)
        .hasSize(1),
      () -> assertThat(endedBoards.getFirst().getPlayer().getEmail())
        .isEqualTo(endedPlayer.getEmail()),
      () -> assertThat(endedBoards.getFirst().getGame().getStart())
        .isEqualTo(endedGame.getStart())
    );
  }

  @Test
  @DisplayName("만료 상태의 GameBoard를 저장하고 복원한다")
  void saveExpiredGameBoard() {
    Player player = persistPlayer("corinB", "corin@example.com");
    Word correct = new Word("apple");
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 8, 0, 0);
    WordleGame game = persistGame(correct, startAt);
    GameBoard gameBoard = new GameBoard(player, game);

    gameBoard.finishIfGameEnded(game.getEnd());
    gameBoardRepository.save(gameBoard);
    entityManager.flush();
    entityManager.clear();

    GameBoard savedGameBoard = gameBoardRepository
      .findByPlayerAndGame(player, game)
      .orElseThrow();

    assertAll(
      () -> assertThat(savedGameBoard.getStatus())
        .isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(savedGameBoard.isExpired())
        .isTrue()
    );
  }

  private Player persistPlayer(String nickname, String email) {
    Player player = Player.create(
      nickname,
      email,
      "encodedPassword"
    );
    entityManager.persist(PlayerEntity.create(player));
    return player;
  }

  private WordleGame persistGame(Word correct, LocalDateTime startAt) {
    return persistGame(correct, startAt, startAt.plusDays(1));
  }

  private WordleGame persistGame(
    Word correct,
    LocalDateTime startAt,
    LocalDateTime endAt
  ) {
    WordEntity wordEntity = entityManager.persist(WordEntity.create(correct));
    WordleGame game = WordleGame.restore(correct, startAt, endAt);
    entityManager.persist(WordleGameEntity.create(game, wordEntity));
    return game;
  }
}
