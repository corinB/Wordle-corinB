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
}
