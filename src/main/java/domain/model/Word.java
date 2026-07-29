package domain.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public record Word(String value) {

  private static final String WHITE = "⬜";
  private static final String YELLOW = "🟨";
  private static final String GREEN = "🟩";
  public static int WORD_LENGTH = 5;
  private static final Pattern WORD_PATTERN =
    Pattern.compile(String.format("^[a-zA-Z]{%d}$", WORD_LENGTH));

  public Word {
    if (!isValidWord(value)) {
      throw new IllegalArgumentException("5글자 영어 알파벳만 입력할 수 있습니다.");
    }
    value = value.toLowerCase(); // 소문자 정규화
  }

  private static boolean isValidWord(String word) {
    return word != null && WORD_PATTERN.matcher(word).matches();
  }


  public String compare(Word answer) {
    if (this.equals(answer)) {
      return GREEN.repeat(WORD_LENGTH);
    }

    String[] results = new String[WORD_LENGTH];
    int[] remainingLetters = new int[26];

    Arrays.fill(results, WHITE);

    evaluateGreen(answer, results, remainingLetters);
    evaluateYellow(answer, results, remainingLetters);

    return String.join("", results);
  }

  //초록색 판정 함수
  private void evaluateGreen(Word answer, String[] results, int[] remainingLetters) {
    for (int i = 0; i < WORD_LENGTH; i++) {
      char answerChar = answer.value().charAt(i);
      char targetChar = this.value.charAt(i);

      if (answerChar == targetChar) {
        results[i] = GREEN;
        continue;
      }
      remainingLetters[targetChar - 'a']++;
    }
  }

  //노란색 판정 함수
  private void evaluateYellow(Word answer, String[] results, int[] remainingLetters) {
    for (int i = 0; i < WORD_LENGTH; i++) {
      if (GREEN.equals(results[i])) {
        continue;
      }

      char answerChar = answer.value().charAt(i);
      int alphabetIndex = answerChar - 'a';

      if (remainingLetters[alphabetIndex] > 0) {
        results[i] = YELLOW;
        remainingLetters[alphabetIndex]--;
      }
    }
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
