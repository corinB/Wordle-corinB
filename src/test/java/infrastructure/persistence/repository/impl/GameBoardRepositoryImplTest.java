package infrastructure.persistence.repository.impl;

import application.WordleApplication;
import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.vo.Word;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.entity.GameBoardRoundEntity;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.entity.WordEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import infrastructure.persistence.repository.PlayerJPARepository;
import infrastructure.persistence.repository.WordleGameJPARepository;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

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
  @DisplayName("GameBoard를 ID로 저장하고 라운드와 함께 복원한다")
  void saveAndFindByPlayerAndGameIds() {
    Long playerId = persistPlayer("corinB", "corin@example.com");
    PersistedGame game = persistGame(
      "apple",
      LocalDateTime.of(2026, 8, 10, 0, 0)
    );
    GameBoard gameBoard = new GameBoard(playerId, game.id());
    Word firstAnswer = new Word("hello");

    gameBoard.submit(firstAnswer, game.correct());
    gameBoardRepository.save(gameBoard);
    gameBoard.submit(game.correct(), game.correct());
    gameBoardRepository.save(gameBoard);
    entityManager.flush();
    entityManager.clear();

    GameBoard savedGameBoard = gameBoardRepository
      .findByPlayerIdAndWordleGameId(playerId, game.id())
      .orElseThrow();

    assertAll(
      () -> assertThat(savedGameBoard.getPlayerId()).isEqualTo(playerId),
      () -> assertThat(savedGameBoard.getWordleGameId()).isEqualTo(game.id()),
      () -> assertThat(savedGameBoard.getStatus()).isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(savedGameBoard.getRounds())
        .extracting(round -> round.answer().value())
        .containsExactly(firstAnswer.value(), game.correct().value()),
      () -> assertThat(savedGameBoard.getRecords())
        .containsExactly(
          game.correct().compare(firstAnswer),
          game.correct().compare(game.correct())
        ),
      () -> assertThat(gameBoardJPARepository.count()).isEqualTo(1)
    );
  }

  @Test
  @DisplayName("여러 게임 ID 중 진행 중인 보드와 라운드를 한 번에 조회한다")
  void findAllPlayingBoardsByWordleGameIds() {
    Long endedPlayerId = persistPlayer("endedB", "ended@example.com");
    Long activePlayerId = persistPlayer("activeB", "active@example.com");
    PersistedGame endedGame = persistGame(
      "apple",
      LocalDateTime.of(2026, 8, 8, 0, 0)
    );
    PersistedGame activeGame = persistGame(
      "cocoa",
      LocalDateTime.of(2026, 8, 12, 0, 0)
    );

    GameBoard endedBoard = new GameBoard(endedPlayerId, endedGame.id());
    endedBoard.submit(new Word("hello"), endedGame.correct());
    gameBoardRepository.save(endedBoard);
    gameBoardRepository.save(new GameBoard(activePlayerId, activeGame.id()));
    entityManager.flush();
    entityManager.clear();

    List<GameBoard> endedBoards = gameBoardRepository
      .findAllPlayingByWordleGameIds(List.of(endedGame.id()));

    assertAll(
      () -> assertThat(endedBoards).hasSize(1),
      () -> assertThat(endedBoards.getFirst().getPlayerId())
        .isEqualTo(endedPlayerId),
      () -> assertThat(endedBoards.getFirst().getWordleGameId())
        .isEqualTo(endedGame.id()),
      () -> assertThat(endedBoards.getFirst().getSpentChance()).isEqualTo(1)
    );
  }

  @Test
  @DisplayName("여러 GameBoard 상태를 ID 기반으로 한 번에 저장한다")
  void saveAllUpdatesGameBoardStatuses() {
    Long firstPlayerId = persistPlayer("firstB", "first@example.com");
    Long secondPlayerId = persistPlayer("secondB", "second@example.com");
    PersistedGame game = persistGame(
      "apple",
      LocalDateTime.of(2026, 8, 8, 0, 0)
    );
    LocalDateTime endAt = LocalDateTime.of(2026, 8, 9, 0, 0);

    GameBoard firstBoard = new GameBoard(firstPlayerId, game.id());
    GameBoard secondBoard = new GameBoard(secondPlayerId, game.id());
    gameBoardRepository.save(firstBoard);
    gameBoardRepository.save(secondBoard);

    firstBoard.finishIfGameEnded(endAt, endAt);
    secondBoard.finishIfGameEnded(endAt, endAt);
    gameBoardRepository.saveAll(List.of(firstBoard, secondBoard));
    entityManager.flush();
    entityManager.clear();

    assertAll(
      () -> assertThat(gameBoardRepository
        .findByPlayerIdAndWordleGameId(firstPlayerId, game.id())
        .orElseThrow().getStatus()).isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(gameBoardRepository
        .findByPlayerIdAndWordleGameId(secondPlayerId, game.id())
        .orElseThrow().getStatus()).isEqualTo(GameBoardStatus.EXPIRED)
    );
  }

  @Test
  @DisplayName("보드 저장소는 다른 JPA 저장소를 주입하지 않는다")
  void doesNotDependOnOtherJpaRepositories() {
    assertThat(GameBoardRepositoryImpl.class.getDeclaredFields())
      .extracting(Field::getType)
      .doesNotContain(
        PlayerJPARepository.class,
        WordleGameJPARepository.class
      );
  }

  @Test
  @DisplayName("GameBoardEntity가 라운드 목록의 생명주기를 소유한다")
  void gameBoardEntityOwnsRoundAggregate() throws NoSuchFieldException {
    Field rounds = GameBoardEntity.class.getDeclaredField("rounds");
    OneToMany oneToMany = rounds.getAnnotation(OneToMany.class);
    JoinColumn joinColumn = rounds.getAnnotation(JoinColumn.class);
    OrderBy orderBy = rounds.getAnnotation(OrderBy.class);
    Field gameBoardId = GameBoardRoundEntity.class
      .getDeclaredField("gameBoardId");
    Column gameBoardIdColumn = gameBoardId.getAnnotation(Column.class);

    assertAll(
      () -> assertThat(oneToMany).isNotNull(),
      () -> assertThat(oneToMany.cascade()).contains(CascadeType.ALL),
      () -> assertThat(oneToMany.orphanRemoval()).isTrue(),
      () -> assertThat(joinColumn.name()).isEqualTo("game_board_id"),
      () -> assertThat(orderBy.value()).isEqualTo("roundIndex ASC"),
      () -> assertThat(gameBoardIdColumn.insertable()).isFalse(),
      () -> assertThat(gameBoardIdColumn.updatable()).isFalse()
    );
  }

  private Long persistPlayer(String nickname, String email) {
    return entityManager.persist(PlayerEntity.create(
      domain.model.Player.create(nickname, email, "encodedPassword")
    )).getId();
  }

  private PersistedGame persistGame(String correctValue, LocalDateTime startAt) {
    WordEntity wordEntity = entityManager.persist(
      WordEntity.create(new Word(correctValue))
    );
    Word correct = wordEntity.toDomain();
    WordleGame game = WordleGame.restore(
      null,
      correct.getId(),
      startAt,
      startAt.plusDays(1)
    );
    WordleGameEntity gameEntity = entityManager.persist(
      WordleGameEntity.create(game)
    );

    return new PersistedGame(gameEntity.getId(), correct);
  }

  private record PersistedGame(Long id, Word correct) {
  }
}
