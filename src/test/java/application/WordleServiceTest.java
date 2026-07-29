package application;
import domain.model.Word;
import domain.model.WordDictionary;
import domain.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class WordleServiceTest {

  @Test
  @DisplayName("정답 검증 테스트")
  void submitCorrectTest() {
    WordleService wordleService = new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();

    assertThat(wordleService.submit(correct)).isTrue();
  }

  @Test
  @DisplayName("답안을 제출시 진행 기록 누적")
  void getRecordTest() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();
    Word answer1 = new Word("aaaaa");
    Word answer2 = new Word("bbbbb");

    List<String> expected = new ArrayList<>();

    expected.add(correct.compare(answer1));
    wordleService.submit(answer1);

    assertThat(wordleService.getRecords())
      .isEqualTo(expected);

    expected.add(correct.compare(answer2));
    wordleService.submit(answer2);

    assertThat(wordleService.getRecords())
      .isEqualTo(expected);
  }

  @Test
  @DisplayName("정답시 답 제출 불가")
  void cannotSubmitAfterCorrectAnswer() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();

    wordleService.submit(correct);

    assertThatThrownBy(() ->
      wordleService.submit(new Word("ccccc"))
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("기회를 모두 소진tl 답안을 제출 불가")
  void cannotSubmitAfterLastChance() {
    WordleService wordleService =
      new WordleService(new MockWordRepository());

    wordleService.gameStart();

    Word wrongAnswer = new Word("aaaaa");

    while (wordleService.canSubmit()) {
      wordleService.submit(wrongAnswer);
    }

    assertThatThrownBy(() ->
      wordleService.submit(new Word("ccccc"))
    ).isInstanceOf(IllegalArgumentException.class);
  }

}
//가짜 래포지토리
class MockWordRepository implements WordRepository {
  @Override
  public List<String> getAllWords() {
    return List.of("apple","cocoa","mania", "radar", "green");
  }
}

