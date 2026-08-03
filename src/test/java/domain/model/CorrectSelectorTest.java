package domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CorrectSelectorTest {

  @RepeatedTest(100)
  @DisplayName("고정된 seed가 주어졌을때 동일한 단어가 선택된다.")
  void chooseCorrectWordTest() {
    final long seed = 1L;

    List<Word> words = Stream.of("apple", "white", "green").map(Word::new).toList();
    // 동일한 seed로 각각 생성
   var selecteor = new RandomCorrectSelector(seed);

    Word word1 = selecteor.select(words);
    Word word2 = selecteor.select(words);

    assertThat(word1).isEqualTo(word2); // 항상 성공
  }
}
