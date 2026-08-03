package domain.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DictionaryTest {


  @RepeatedTest(100)
  @DisplayName("지금 시간을 기준으로 랜덤 단어 선택")
  void chooseCorrectWordTest() {

    long seed1 = 1L;
    long seed2 = 2L;

    Dictionary dictionary = new Dictionary("apple", "white", "green");

    Word word1 = dictionary.correct(seed1);
    Word word2 = dictionary.correct(seed1);
    Word word3 = dictionary.correct(seed2);

    assertAll(
      () -> assertThat(word1).isEqualTo(word2),
      () -> assertThat(word1).isNotEqualTo(word3)
    );
  }

}
