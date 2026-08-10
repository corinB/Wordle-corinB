package domain.model;

import domain.exception.GameAlreadyFinishedException;
import domain.exception.GameNotFinishedException;
import domain.vo.GameHistory;
import domain.vo.Round;
import domain.vo.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class GameBoardTest {

  @Test
  @DisplayName("보드는 플레이어와 게임을 참조한다")
  void createdBoardReferencesPlayerAndGame() {
    Word correct = new Word("spill");
    Player player = createPlayer();
    WordleGame game = createGame(correct);

    GameBoard gameBoard = new GameBoard(player, game);

    assertAll(
      () -> assertThat(gameBoard.getPlayer())
        .isSameAs(player),
      () -> assertThat(gameBoard.getGame())
        .isSameAs(game),
      () -> assertThat(gameBoard.getCorrect())
        .isEqualTo(correct)
    );
  }

  @Test
  @DisplayName("답안을 제출하면 라운드를 기록하고 진행 상태를 유지한다")
  void submitRecordsRoundAndKeepsPlayingState() {
    Word correct = new Word("spill");
    Word answer = new Word("hello");
    GameBoard gameBoard = createBoard(correct);

    gameBoard.submit(answer);

    assertAll(
      () -> assertThat(gameBoard.getRounds())
        .extracting(Round::answer)
        .containsExactly(answer),
      () -> assertThat(gameBoard.getRecords())
        .containsExactly(correct.compare(answer)),
      () -> assertThat(gameBoard.getSpentChance())
        .isEqualTo(1),
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.PLAYING)
    );
  }

  @Test
  @DisplayName("진행 중인 보드는 히스토리를 만들 수 없다")
  void cannotCreateHistoryWhenPlaying() {
    GameBoard gameBoard = createBoard(new Word("spill"));

    assertThatThrownBy(gameBoard::getHistory)
      .isInstanceOf(GameNotFinishedException.class);
  }

  @Test
  @DisplayName("정답을 제출하면 승리 상태로 종료된다")
  void submitCorrectAnswerChangesStatusToWin() {
    Word correct = new Word("spill");
    GameBoard gameBoard = createBoard(correct);

    gameBoard.submit(correct);
    GameHistory history = gameBoard.getHistory();

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(gameBoard.isCorrect())
        .isTrue(),
      () -> assertThat(gameBoard.isFinished())
        .isTrue(),
      () -> assertThat(gameBoard.canSubmit())
        .isFalse(),
      () -> assertThat(history.tryCount().value())
        .isEqualTo(1),
      () -> assertThat(history.status())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThatThrownBy(() -> gameBoard.submit(new Word("hello")))
        .isInstanceOf(GameAlreadyFinishedException.class)
    );
  }

  @Test
  @DisplayName("최대 기회까지 오답이면 패배 상태로 종료된다")
  void submitMaxWrongAnswersChangesStatusToLose() {
    GameBoard gameBoard = createBoard(new Word("spill"));
    Word wrongAnswer = new Word("hello");

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      gameBoard.submit(wrongAnswer);
    }
    GameHistory history = gameBoard.getHistory();

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.LOSE),
      () -> assertThat(gameBoard.isFailed())
        .isTrue(),
      () -> assertThat(gameBoard.isFinished())
        .isTrue(),
      () -> assertThat(gameBoard.canSubmit())
        .isFalse(),
      () -> assertThat(history.tryCount().value())
        .isEqualTo(GameBoard.MAX_CHANCE),
      () -> assertThat(history.status())
        .isEqualTo(GameBoardStatus.LOSE),
      () -> assertThatThrownBy(() -> gameBoard.submit(new Word("label")))
        .isInstanceOf(GameAlreadyFinishedException.class)
    );
  }

  @Test
  @DisplayName("게임 종료 시각이 지나면 만료 상태로 종료된다")
  void finishIfGameEndedChangesStatusToExpired() {
    Word correct = new Word("spill");
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 10, 0, 0);
    LocalDateTime endAt = startAt.plusDays(1);
    GameBoard gameBoard = new GameBoard(
      createPlayer(),
      createGame(correct, startAt, endAt)
    );

    gameBoard.finishIfGameEnded(endAt);
    GameHistory history = gameBoard.getHistory();

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(gameBoard.isExpired())
        .isTrue(),
      () -> assertThat(gameBoard.isFinished())
        .isTrue(),
      () -> assertThat(gameBoard.canSubmit())
        .isFalse(),
      () -> assertThat(history.tryCount().value())
        .isZero(),
      () -> assertThat(history.status())
        .isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThatThrownBy(() -> gameBoard.submit(new Word("hello")))
        .isInstanceOf(GameAlreadyFinishedException.class)
    );
  }

  private GameBoard createBoard(Word correct) {
    return new GameBoard(createPlayer(), createGame(correct));
  }

  private Player createPlayer() {
    return Player.create(
      "corinB",
      "corin@example.com",
      "encoded:password123"
    );
  }

  private WordleGame createGame(Word correct) {
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 10, 0, 0);
    return createGame(correct, startAt, startAt.plusDays(1));
  }

  private WordleGame createGame(
    Word correct,
    LocalDateTime startAt,
    LocalDateTime endAt
  ) {
    return WordleGame.restore(correct, startAt, endAt);
  }
}
