package application;
import domain.model.GameBoard;
import domain.model.Word;
import domain.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WordleServiceTest {

  @Test
  @DisplayName("게임 시작시 정답 생성")
  void gameStartTest() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    assertThat(wordleService.getCorrect())
      .isNotNull();
  }

  @Test
  @DisplayName("답안을 제출시 진행 기록이 누적")
  void submitRecordTest() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();
    Word answer1 = new Word("aaaaa");
    Word answer2 = new Word("bbbbb");

    wordleService.submit(answer1);
    wordleService.submit(answer2);

    assertThat(wordleService.getRecords())
      .containsExactly(
        correct.compare(answer1),
        correct.compare(answer2)
      );
  }

  @Test
  @DisplayName("답안을 제출시 사용한 기회가 증가")
  void spentChanceTest() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    assertThat(wordleService.getSpentChance())
      .isZero();

    wordleService.submit(new Word("aaaaa"));

    assertThat(wordleService.getSpentChance())
      .isEqualTo(1);
  }

  @Test
  @DisplayName("게임 시작 직후에는 진행 중")
  void gameIsInProgressAfterStart() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    assertThat(wordleService.isFinished())
      .isFalse();
    assertThat(wordleService.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("오답 제출 후에는 진행 중")
  void wrongAnswerKeepsGameInProgress() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    wordleService.submit(findWrongAnswer(wordleService.getCorrect()));

    assertThat(wordleService.isFinished())
      .isFalse();
    assertThat(wordleService.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("정답 제출 후에는 종료")
  void correctAnswerFinishesGame() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    wordleService.submit(wordleService.getCorrect());

    assertThat(wordleService.isFinished())
      .isTrue();
    assertThat(wordleService.isCorrect())
      .isTrue();
  }

  @Test
  @DisplayName("모든 기회 소진 후에는 종료")
  void maxChanceFinishesGame() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word wrongAnswer = findWrongAnswer(wordleService.getCorrect());

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      wordleService.submit(wrongAnswer);
    }

    assertThat(wordleService.isFinished())
      .isTrue();
    assertThat(wordleService.isCorrect())
      .isFalse();
  }

  @Test
  @DisplayName("정답 맞출 시 답변 제출 불가")
  void cannotSubmitAfterCorrectAnswer() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();

    wordleService.submit(correct);

    assertThatThrownBy(() ->
      wordleService.submit(new Word("aaaaa"))
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("기회를 모두 사용시 답변 제출 불가")
  void cannotSubmitAfterMaxChance() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word wrongAnswer = new Word("aaaaa");

    for (int i = 0; i < GameBoard.MAX_CHANCE; i++) {
      wordleService.submit(wrongAnswer);
    }

    assertThat(wordleService.getSpentChance())
      .isEqualTo(GameBoard.MAX_CHANCE);

    assertThatThrownBy(() ->
      wordleService.submit(wrongAnswer)
    ).isInstanceOf(IllegalStateException.class);
  }

  private Word findWrongAnswer(Word correct) {
    return new MockWordRepository().getAllWords()
      .stream()
      .map(Word::new)
      .filter(word -> !word.equals(correct))
      .findFirst()
      .orElseThrow();
  }
}


// 가짜 Repository
class MockWordRepository implements WordRepository {

  @Override
  public List<String> getAllWords() {
    return List.of(
      "apple",
      "cocoa",
      "mania",
      "radar",
      "green"
    );
  }
}
