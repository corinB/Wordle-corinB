package domain.model;

import java.util.Objects;

public record Word(String value) {

  private static final char UPPER_START = 'A';
  private static final char UPPER_END = 'Z';
  private static final char LOWER_START = 'a';
  private static final char LOWER_END = 'z';
  public static int WORD_LENGTH = 5;

  public Word{
    if (!isValidWord(value)) {
      throw new IllegalArgumentException("5글자 영어 알파벳만 입력할 수 있습니다.");
    }
    value = value.toLowerCase();
  }

  private static boolean isValidWord(String word) {
    return word != null
      && word.length() == WORD_LENGTH
      && word.chars()
      .allMatch(character ->
        (character >= UPPER_START && character <= UPPER_END)
          || (character >= LOWER_START && character <= LOWER_END)
      );
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Word word = (Word) o;
    return Objects.equals(value, word.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
