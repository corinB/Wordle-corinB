package application;
import domain.model.Word;
import domain.model.WordDictionary;
import domain.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WordleServiceTest {

  @Test
  @DisplayName("정답 검증 테스트")
  void submitCorrectTest() {
    WordleService wordleService = new WordleService(new WordDictionary(new MockWordRepository()));

    wordleService.gameStart();

    Word correct = wordleService.getCorrect();

    assertThat(wordleService.submit(correct)).isTrue();
  }


}
//가짜 래포지토리
class MockWordRepository implements WordRepository {
  @Override
  public List<String> getAllWords() {
    return List.of("apple","cocoa","mania", "radar", "green");
  }

  @Override
  public int getTotalWordsCNT() {
    return 6;
  }
}

