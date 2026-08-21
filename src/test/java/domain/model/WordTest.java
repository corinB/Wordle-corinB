package domain.model;
import domain.exception.InvalidWordException;
import domain.vo.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WordTest {

  @Test
  @DisplayName("비교")
  void equalsTest(){
    Word word1 = new Word("apple");
    Word word2 = new Word("apple");
    Word word3 = new Word("aPPle");
    Word word4 = new Word("applg");

    assertThat(word1).isEqualTo(word2);
    assertThat(word1).isEqualTo(word3);
    assertThat(word1).isNotEqualTo(word4);
  }

  @Test
  @DisplayName("영속 ID가 달라도 같은 단어는 동등하다")
  void equalsByValueRegardlessOfPersistenceId() {
    assertThat(new Word(1L, "apple"))
      .isEqualTo(new Word(2L, "APPLE"));
  }

  @Test
  @DisplayName("영문으로만 생성 가능하며, 반드시 5글자이고 소문자로 저장된다")
  void constructorParameterValidation() {
    assertThatThrownBy(() -> new Word("appl"))
      .isInstanceOf(InvalidWordException.class);

    assertThatThrownBy(() -> new Word("appleee"))
      .isInstanceOf(InvalidWordException.class);

    assertThatThrownBy(() -> new Word("appl!"))
      .isInstanceOf(InvalidWordException.class);

    Word word1 = new Word("apple");
    Word word2 = new Word("APPLE");

    assertThat(word1.value()).isEqualTo("apple");
    assertThat(word2.value()).isEqualTo("apple");
    assertThat(word1).isEqualTo(word2);
  }

  @Test
  @DisplayName("상세 비교")
  void compareToTest() {
    Word correct = new Word("spill");
    Word answer1 = new Word("hello");
    Word answer2 = new Word("label");
    Word answer3 = new Word("spell");
    Word answer4 = new Word("spill");

    assertThat(correct.compare(answer1)).isEqualTo("⬜⬜🟨🟩⬜");
    assertThat(correct.compare(answer2)).isEqualTo("🟨⬜⬜⬜🟩");
    assertThat(correct.compare(answer3)).isEqualTo("🟩🟩⬜🟩🟩");
    assertThat(correct.compare(answer4)).isEqualTo("🟩🟩🟩🟩🟩");
  }
}
