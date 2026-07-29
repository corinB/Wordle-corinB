package domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;



public class GameBoardTest {

  @Test
  @DisplayName("답안 기록 누적 및 순서 확인")
  void gameRecordTest() {
    Word correct = new Word("spill");

    List<Word> answers = List.of(
      new Word("hello"),
      new Word("label"),
      new Word("spell"),
      correct
    );

    GameBoard gameBoard = new GameBoard(correct);

    answers.forEach(gameBoard::submit);

    List<String> expected = answers.stream()
      .map(correct::compare)
      .toList();

    assertThat(gameBoard.getRecords())
      .containsExactlyElementsOf(expected);
  }

  @Test
  @DisplayName("답안을 제출하면 사용한 기회 증가")
  void spentChanceTest() {
    GameBoard gameBoard = new GameBoard(new Word("spill"));

    gameBoard.submit(new Word("hello"));

    assertThat(gameBoard.getSpentChance())
      .isEqualTo(1);
  }

  @Test
  @DisplayName("정답 확인")
  void correctAnswerTest() {
    Word correct = new Word("spill");
    GameBoard gameBoard = new GameBoard(correct);

    gameBoard.submit(correct);

    assertThat(gameBoard.getSpentChance())
      .isEqualTo(1);
  }

  @Test
  @DisplayName("정답 이후 답안 제출 불가")
  void cannotSubmitAfterCorrectAnswer() {
    Word correct = new Word("spill");
    GameBoard gameBoard = new GameBoard(correct);

    gameBoard.submit(correct);

    assertThatThrownBy(() ->
      gameBoard.submit(new Word("hello"))
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("최대 기회 소진 이후 답안 제출 불가")
  void cannotSubmitAfterMaxChance() {
    GameBoard gameBoard = new GameBoard(new Word("spill"));
    Word wrongAnswer = new Word("hello");

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      gameBoard.submit(wrongAnswer);
    }

    assertThat(gameBoard.getSpentChance())
      .isEqualTo(GameBoard.MAX_CHANCE);

    assertThatThrownBy(() ->
      gameBoard.submit(wrongAnswer)
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("생성 시 전달한 정답 반환")
  void getCorrectTest() {
    Word correct = new Word("spill");
    GameBoard gameBoard = new GameBoard(correct);

    assertThat(gameBoard.getCorrect())
      .isEqualTo(correct);
  }
}
