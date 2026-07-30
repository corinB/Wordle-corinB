package domain.model;

import java.util.ArrayList;
import java.util.List;

import static domain.exception.DomainErrorType.GAME_ALREADY_FINISHED;

public class GameBoard {

  public static final int MAX_CHANCE = 6;

  private final Word correct;
  private final List<String> records;
  private boolean correctFlag;

  public GameBoard(Word correct) {
    this.correct = correct;
    this.records = new ArrayList<>();
    this.correctFlag = false;
  }

  public void submit(Word answer) {
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    records.add(correct.compare(answer));

    if (correct.equals(answer)) {
      correctFlag = true;
    }
  }

  public boolean canSubmit() {
    return !correctFlag && records.size() < MAX_CHANCE;
  }

  public boolean isCorrect() {
    return correctFlag;
  }

  public Word getCorrect() {
    return correct;
  }

  public int getSpentChance() {
    return records.size();
  }

  public List<String> getRecords() {
    return List.copyOf(records);
  }
}
