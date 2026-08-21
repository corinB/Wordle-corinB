package domain.model;

import domain.exception.GameAlreadyFinishedException;
import domain.exception.GameNotFinishedException;
import domain.vo.GameHistory;
import domain.vo.Nickname;
import domain.vo.Round;
import domain.vo.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class GameBoardTest {

  private static final Long PLAYER_ID = 1L;
  private static final Long WORDLE_GAME_ID = 2L;

  @Test
  @DisplayName("보드는 플레이어 ID와 게임 ID를 참조한다")
  void createdBoardReferencesPlayerAndGameIds() {
    GameBoard gameBoard = createBoard();

    assertAll(
      () -> assertThat(gameBoard.getPlayerId()).isEqualTo(PLAYER_ID),
      () -> assertThat(gameBoard.getWordleGameId()).isEqualTo(WORDLE_GAME_ID)
    );
  }

  @Test
  @DisplayName("답안을 제출하면 라운드를 기록하고 진행 상태를 유지한다")
  void submitRecordsRoundAndKeepsPlayingState() {
    Word correct = new Word("spill");
    Word answer = new Word("hello");
    GameBoard gameBoard = createBoard();

    gameBoard.submit(answer, correct);

    assertAll(
      () -> assertThat(gameBoard.getRounds())
        .extracting(Round::answer)
        .containsExactly(answer),
      () -> assertThat(gameBoard.getRecords())
        .containsExactly(correct.compare(answer)),
      () -> assertThat(gameBoard.getSpentChance()).isEqualTo(1),
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.PLAYING)
    );
  }

  @Test
  @DisplayName("진행 중인 보드는 히스토리를 만들 수 없다")
  void cannotCreateHistoryWhenPlaying() {
    GameBoard gameBoard = createBoard();

    assertThatThrownBy(() -> gameBoard.getHistory(
      new Nickname("corinB"),
      new Word("spill")
    )).isInstanceOf(GameNotFinishedException.class);
  }

  @Test
  @DisplayName("정답을 제출하면 승리 상태로 종료된다")
  void submitCorrectAnswerChangesStatusToWin() {
    Word correct = new Word("spill");
    GameBoard gameBoard = createBoard();

    gameBoard.submit(correct, correct);
    GameHistory history = gameBoard.getHistory(
      new Nickname("corinB"),
      correct
    );

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(gameBoard.isCorrect()).isTrue(),
      () -> assertThat(gameBoard.isFinished()).isTrue(),
      () -> assertThat(gameBoard.canSubmit()).isFalse(),
      () -> assertThat(history.tryCount().value()).isEqualTo(1),
      () -> assertThat(history.status()).isEqualTo(GameBoardStatus.WIN),
      () -> assertThatThrownBy(() ->
        gameBoard.submit(new Word("hello"), correct)
      ).isInstanceOf(GameAlreadyFinishedException.class)
    );
  }

  @Test
  @DisplayName("최대 기회까지 오답이면 패배 상태로 종료된다")
  void submitMaxWrongAnswersChangesStatusToLose() {
    Word correct = new Word("spill");
    GameBoard gameBoard = createBoard();
    Word wrongAnswer = new Word("hello");

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      gameBoard.submit(wrongAnswer, correct);
    }
    GameHistory history = gameBoard.getHistory(
      new Nickname("corinB"),
      correct
    );

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.LOSE),
      () -> assertThat(gameBoard.isFailed()).isTrue(),
      () -> assertThat(gameBoard.isFinished()).isTrue(),
      () -> assertThat(gameBoard.canSubmit()).isFalse(),
      () -> assertThat(history.tryCount().value())
        .isEqualTo(GameBoard.MAX_CHANCE),
      () -> assertThat(history.status()).isEqualTo(GameBoardStatus.LOSE)
    );
  }

  @Test
  @DisplayName("게임 종료 시각이 지나면 만료 상태로 종료된다")
  void finishIfGameEndedChangesStatusToExpired() {
    LocalDateTime endAt = LocalDateTime.of(2026, 8, 11, 0, 0);
    Word correct = new Word("spill");
    GameBoard gameBoard = createBoard();

    gameBoard.finishIfGameEnded(endAt, endAt);
    GameHistory history = gameBoard.getHistory(
      new Nickname("corinB"),
      correct
    );

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(gameBoard.isExpired()).isTrue(),
      () -> assertThat(gameBoard.isFinished()).isTrue(),
      () -> assertThat(gameBoard.canSubmit()).isFalse(),
      () -> assertThat(history.tryCount().value()).isZero(),
      () -> assertThat(history.status()).isEqualTo(GameBoardStatus.EXPIRED)
    );
  }

  @Test
  @DisplayName("플레이어 또는 게임 ID 없이 보드를 만들 수 없다")
  void cannotCreateBoardWithoutIds() {
    assertAll(
      () -> assertThatThrownBy(() -> new GameBoard(null, WORDLE_GAME_ID))
        .isInstanceOf(IllegalArgumentException.class),
      () -> assertThatThrownBy(() -> new GameBoard(PLAYER_ID, null))
        .isInstanceOf(IllegalArgumentException.class)
    );
  }

  private GameBoard createBoard() {
    return new GameBoard(PLAYER_ID, WORDLE_GAME_ID);
  }
}
