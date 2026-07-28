package domain.model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class wordTest {

  @Test
  @DisplayName("비교")
  void equalsTest(){
    Word word1 = new word("apple");
    Word word2 = new word("apple");
    Word word3 = new word("aPPle");
    Word word4 = new word("applg");

    assertThat(word1).isEqualTo(word2);
    assertThat(word1).isEqualTo(word3);
    assertThat(word1).isNotEqualTo(word4);
  }

  @Test
  @DisplayName("영문으로만 생성 가능하며, 반드시 5글자이고 소문자로 저장된다")
  void constructorParameterValidation() {
    assertThatThrownBy(() -> new Word("appl"))
      .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> new Word("appleee"))
      .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> new Word("appl!"))
      .isInstanceOf(IllegalArgumentException.class);

    Word word1 = new Word("apple");
    Word word2 = new Word("APPLE");

    assertThat(word1.value()).isEqualTo("apple");
    assertThat(word2.value()).isEqualTo("apple");
    assertThat(word1).isEqualTo(word2);
  }
}
