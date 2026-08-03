package domain.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DictionaryTest {

  @RepeatedTest(100)
  @DisplayName("고정된 seed가 주어졌을때 동일한 단어가 선택된다.")
  void chooseCorrectWordTest() {
    final long seed = 1L;
    // 동일한 seed로 각각 생성
    final Dictionary dictionary1 = new Dictionary(new OnlyFirstSelector(),"apple", "white", "green");

    Word word1 = dictionary1.correct();
    Word word2 = dictionary1.correct();

    assertThat(word1).isEqualTo(word2); // 항상 성공
    assertThat(word1.value()).isIn("apple", "white", "green");
  }

  static class OnlyFirstSelector implements CorrectSelector{
    @Override
    public Word select(List<Word> words) {
      return words.get(0);
    }
  }
}
