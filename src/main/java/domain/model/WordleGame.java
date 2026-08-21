package domain.model;

import domain.policy.CorrectSelector;
import domain.vo.Word;

import java.time.LocalDateTime;
import java.util.List;

import static domain.exception.DomainErrorType.CORRECT_WORD_ID_REQUIRED;

public class WordleGame {

  private final Long id;
  private final Long correctWordId;
  private final LocalDateTime start;
  private final LocalDateTime end;

  public WordleGame(List<Word> words, CorrectSelector correctSelector) {
    this(
      null,
      requireCorrectWordId(correctSelector.select(words)),
      LocalDateTime.now().toLocalDate().atStartOfDay()
    );
  }

  private WordleGame(
    Long id,
    Long correctWordId,
    LocalDateTime start
  ) {
    this(id, correctWordId, start, start.plusDays(1));
  }

  private WordleGame(
    Long id,
    Long correctWordId,
    LocalDateTime start,
    LocalDateTime end
  ) {
    this.id = id;
    this.correctWordId = requireCorrectWordId(correctWordId);
    this.start = start;
    this.end = end;
  }

  public static WordleGame restore(
    Long id,
    Long correctWordId,
    LocalDateTime start,
    LocalDateTime end
  ) {
    return new WordleGame(id, correctWordId, start, end);
  }

  public Long getId() {
    return id;
  }

  public Long getCorrectWordId() {
    return correctWordId;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  private static Long requireCorrectWordId(Word correct) {
    if (correct == null) {
      throw CORRECT_WORD_ID_REQUIRED.createException();
    }
    return requireCorrectWordId(correct.getId());
  }

  private static Long requireCorrectWordId(Long correctWordId) {
    if (correctWordId == null) {
      throw CORRECT_WORD_ID_REQUIRED.createException();
    }
    return correctWordId;
  }
}
