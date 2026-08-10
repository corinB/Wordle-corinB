package domain.model;

import domain.policy.CorrectSelector;
import domain.vo.Word;

import java.time.LocalDateTime;
import java.util.List;

public class WordleGame {

  private final Word correct;
  private final LocalDateTime start;
  private final LocalDateTime end;

  public WordleGame(List<Word> words, CorrectSelector correctSelector) {
    this.correct = correctSelector.select(words);
    this.start = LocalDateTime.now().toLocalDate().atStartOfDay();
    this.end = start.plusDays(1);
  }

  private WordleGame(Word correct, LocalDateTime start, LocalDateTime end) {
    this.correct = correct;
    this.start = start;
    this.end = end;
  }

  public static WordleGame restore(
    Word correct,
    LocalDateTime start,
    LocalDateTime end
  ) {
    return new WordleGame(correct, start, end);
  }

  public Word getCorrect() {
    return correct;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public LocalDateTime getEnd() {
    return end;
  }
}
