package domain.model;

import domain.model.vo.Word;

import java.time.LocalDateTime;
import java.util.List;

public class WordleGame {

  private final Word correct;
  private final LocalDateTime start;
  private final LocalDateTime end;

  public WordleGame(List<Word> words, CorrectSelector correctSelector) {
    this.correct = correctSelector.select(words);
    this.start = LocalDateTime.now();
    this.end = start.plusDays(1);
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
