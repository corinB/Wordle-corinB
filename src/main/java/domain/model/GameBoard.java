package domain.model;

import java.util.ArrayList;
import java.util.List;

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
      throw new IllegalStateException("끝난 게임입니다.");
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
