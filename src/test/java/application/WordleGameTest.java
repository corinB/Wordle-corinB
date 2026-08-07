package application;
import domain.exception.GameAlreadyFinishedException;
import domain.model.GameBoard;
import domain.model.vo.Word;
import domain.model.WordleGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WordleGameTest {

  private final List<Word> words = List.of(
    "apple",
    "cocoa",
    "mania",
    "radar",
    "green"
  ).stream().map(Word::new).toList();

  private final Word wrongAnswer = new Word("hello");


  @Test
  @DisplayName("게임 시작시 정답 생성")
  void gameStartTest() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    assertThat(wordleGame.getCorrect())
      .isNotNull();
  }

  @Test
  @DisplayName("답안을 제출시 진행 기록이 누적")
  void submitRecordTest() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    Word correct = wordleGame.getCorrect();
    Word answer1 = new Word("aaaaa");
    Word answer2 = new Word("bbbbb");

    wordleGame.submit(answer1);
    wordleGame.submit(answer2);

    assertThat(wordleGame.getRecords())
      .containsExactly(
        correct.compare(answer1),
        correct.compare(answer2)
      );
  }

  @Test
  @DisplayName("답안을 제출시 사용한 기회가 증가")
  void spentChanceTest() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    assertThat(wordleGame.getSpentChance())
      .isZero();

    wordleGame.submit(new Word("aaaaa"));

    assertThat(wordleGame.getSpentChance())
      .isEqualTo(1);
  }

  @Test
  @DisplayName("게임 시작 직후에는 진행 중")
  void gameIsInProgressAfterStart() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    assertThat(wordleGame.isFinished())
      .isFalse();
    assertThat(wordleGame.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("오답 제출 후에는 진행 중")
  void wrongAnswerKeepsGameInProgress() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    wordleGame.submit(wrongAnswer);

    assertThat(wordleGame.isFinished())
      .isFalse();
    assertThat(wordleGame.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("정답 제출 후에는 종료")
  void correctAnswerFinishesGame() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    wordleGame.submit(wordleGame.getCorrect());

    assertThat(wordleGame.isFinished())
      .isTrue();
    assertThat(wordleGame.isCorrect())
      .isTrue();
  }

  @Test
  @DisplayName("모든 기회 소진 후에는 종료")
  void maxChanceFinishesGame() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      wordleGame.submit(wrongAnswer);
    }

    assertThat(wordleGame.isFinished())
      .isTrue();
    assertThat(wordleGame.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("정답 맞출 시 답변 제출 불가")
  void cannotSubmitAfterCorrectAnswer() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    Word correct = wordleGame.getCorrect();

    wordleGame.submit(correct);

    assertThatThrownBy(() ->
      wordleGame.submit(new Word("aaaaa"))
    ).isInstanceOf(GameAlreadyFinishedException.class);
  }

  @Test
  @DisplayName("기회를 모두 사용시 답변 제출 불가")
  void cannotSubmitAfterMaxChance() {
    WordleGame wordleGame =
      new WordleGame(words);

    wordleGame.gameStart();

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      wordleGame.submit(wrongAnswer);
    }

    assertThat(wordleGame.getSpentChance())
      .isEqualTo(GameBoard.MAX_CHANCE);

    assertThatThrownBy(() ->
      wordleGame.submit(wrongAnswer)
    ).isInstanceOf(GameAlreadyFinishedException.class);
  }
}
